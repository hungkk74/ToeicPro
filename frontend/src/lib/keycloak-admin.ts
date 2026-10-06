import { NextRequest } from 'next/server';

export interface KeycloakUserRaw {
  id: string;
  username: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  enabled?: boolean;
  createdTimestamp?: number;
}

export interface AdminUserItem {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  enabled: boolean;
  createdTimestamp?: number;
  isAdmin: boolean;
  roles: string[];
}

export function getKeycloakAdminUrl(): string {
  return (
    process.env.INTERNAL_KEYCLOAK_URL ||
    process.env.NEXT_PUBLIC_KEYCLOAK_URL ||
    'http://localhost:9080'
  );
}

export async function getAdminToken(keycloakBase: string): Promise<string> {
  const adminUsername = process.env.KEYCLOAK_ADMIN_USERNAME;
  const adminPassword = process.env.KEYCLOAK_ADMIN_PASSWORD;
  if (!adminUsername || !adminPassword) {
    throw new Error('Missing required env vars: KEYCLOAK_ADMIN_USERNAME, KEYCLOAK_ADMIN_PASSWORD');
  }

  const adminTokenParams = new URLSearchParams({
    client_id: 'admin-cli',
    grant_type: 'password',
    username: adminUsername,
    password: adminPassword,
  });

  const tokenRes = await fetch(
    `${keycloakBase}/realms/master/protocol/openid-connect/token`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: adminTokenParams.toString(),
    }
  );

  if (!tokenRes.ok) {
    throw new Error(`Failed to obtain admin token: ${tokenRes.status}`);
  }

  const tokenData = await tokenRes.json();
  return tokenData.access_token;
}

export async function verifyAdminCaller(
  request: NextRequest,
  keycloakBase: string
): Promise<{ success: boolean; error?: string; status?: number }> {
  const authHeader = request.headers.get('Authorization');
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return {
      success: false,
      error: 'Yêu cầu đăng nhập quản trị viên (Missing Authorization Header).',
      status: 401,
    };
  }
  const userToken = authHeader.substring(7);

  // 1. Kiểm tra tính hợp lệ của token qua Keycloak UserInfo
  try {
    const userInfoRes = await fetch(
      `${keycloakBase}/realms/jhipster/protocol/openid-connect/userinfo`,
      {
        headers: { Authorization: `Bearer ${userToken}` },
        signal: AbortSignal.timeout(5000),
      }
    );

    if (!userInfoRes.ok) {
      return {
        success: false,
        error: 'Phiên làm việc không hợp lệ hoặc đã hết hạn.',
        status: 401,
      };
    }
  } catch {
    return {
      success: false,
      error: 'Không thể kết nối đến máy chủ xác thực Keycloak.',
      status: 502,
    };
  }

  // 2. Decode claims từ JWT để xác nhận quyền ROLE_ADMIN
  try {
    const parts = userToken.split('.');
    if (parts.length >= 2) {
      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const payload = JSON.parse(jsonPayload);
      const roles: string[] = [
        ...(payload.roles || []),
        ...(payload.realm_access?.roles || []),
      ];
      const isAdmin = roles.some((r) => r === 'ROLE_ADMIN' || r === 'ROLE_STAFF');
      if (!isAdmin) {
        return {
          success: false,
          error: 'Từ chối truy cập: Bạn không có quyền Quản trị viên (ROLE_ADMIN).',
          status: 403,
        };
      }
      return { success: true };
    }
  } catch {
    return {
      success: false,
      error: 'Không thể giải mã quyền hạn từ mã xác thực.',
      status: 403,
    };
  }

  return { success: false, error: 'Quyền hạn không hợp lệ.', status: 403 };
}

export async function fetchUsersWithRoles(
  keycloakBase: string,
  adminToken: string
): Promise<AdminUserItem[]> {
  const usersRes = await fetch(
    `${keycloakBase}/admin/realms/jhipster/users?max=100`,
    {
      headers: { Authorization: `Bearer ${adminToken}` },
      cache: 'no-store',
    }
  );

  if (!usersRes.ok) {
    throw new Error(`Failed to fetch users from Keycloak: ${usersRes.status}`);
  }

  const users: KeycloakUserRaw[] = await usersRes.json();

  // Single batch call for ROLE_ADMIN to eliminate N+1 HTTP request cascade
  let adminUserIds = new Set<string>();
  try {
    const adminUsersRes = await fetch(
      `${keycloakBase}/admin/realms/jhipster/roles/ROLE_ADMIN/users`,
      {
        headers: { Authorization: `Bearer ${adminToken}` },
        cache: 'no-store',
      }
    );
    if (adminUsersRes.ok) {
      const adminUsers: KeycloakUserRaw[] = await adminUsersRes.json();
      adminUserIds = new Set(adminUsers.map((u) => u.id));
    }
  } catch {
    // fallback to user list if role endpoint fails
  }

  return users.map((u) => {
    const isAdmin = adminUserIds.has(u.id);
    return {
      id: u.id,
      username: u.username,
      email: u.email || '—',
      firstName: u.firstName || '',
      lastName: u.lastName || '',
      fullName: [u.firstName, u.lastName].filter(Boolean).join(' ') || u.username,
      enabled: u.enabled ?? true,
      createdTimestamp: u.createdTimestamp,
      isAdmin,
      roles: isAdmin ? ['ROLE_ADMIN', 'ROLE_USER'] : ['ROLE_USER'],
    };
  });
}

export async function updateUserAdminRole(
  keycloakBase: string,
  adminToken: string,
  userId: string,
  makeAdmin: boolean
): Promise<void> {
  const adminRoleRes = await fetch(
    `${keycloakBase}/admin/realms/jhipster/roles/ROLE_ADMIN`,
    { headers: { Authorization: `Bearer ${adminToken}` } }
  );

  if (!adminRoleRes.ok) {
    throw new Error('Không tìm thấy vai trò ROLE_ADMIN trong hệ thống');
  }
  const adminRole = await adminRoleRes.json();

  const groupsRes = await fetch(
    `${keycloakBase}/admin/realms/jhipster/groups`,
    { headers: { Authorization: `Bearer ${adminToken}` } }
  );
  const groups = await groupsRes.json();
  const adminsGroup = groups.find((g: { name: string }) => g.name === 'Admins');

  if (makeAdmin) {
    await fetch(
      `${keycloakBase}/admin/realms/jhipster/users/${userId}/role-mappings/realm`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${adminToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify([adminRole]),
      }
    );

    if (adminsGroup) {
      await fetch(
        `${keycloakBase}/admin/realms/jhipster/users/${userId}/groups/${adminsGroup.id}`,
        {
          method: 'PUT',
          headers: { Authorization: `Bearer ${adminToken}` },
        }
      );
    }
  } else {
    await fetch(
      `${keycloakBase}/admin/realms/jhipster/users/${userId}/role-mappings/realm`,
      {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${adminToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify([adminRole]),
      }
    );

    if (adminsGroup) {
      await fetch(
        `${keycloakBase}/admin/realms/jhipster/users/${userId}/groups/${adminsGroup.id}`,
        {
          method: 'DELETE',
          headers: { Authorization: `Bearer ${adminToken}` },
        }
      );
    }
  }
}
