import { NextRequest, NextResponse } from 'next/server';

function getKeycloakAdminUrl(): string {
  return (
    process.env.INTERNAL_KEYCLOAK_URL ||
    process.env.NEXT_PUBLIC_KEYCLOAK_URL ||
    'http://localhost:9080'
  );
}

async function getAdminToken(keycloakBase: string): Promise<string> {
  const adminTokenParams = new URLSearchParams({
    client_id: 'admin-cli',
    grant_type: 'password',
    username: process.env.KEYCLOAK_ADMIN_USERNAME || 'admin',
    password: process.env.KEYCLOAK_ADMIN_PASSWORD || 'admin',
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

async function verifyAdminCaller(
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

/**
 * GET /api/admin/users
 * Lấy danh sách toàn bộ người dùng từ Keycloak Realm 'jhipster' kèm vai trò & nhóm
 */
export async function GET(request: NextRequest) {
  try {
    const keycloakBase = getKeycloakAdminUrl();

    // Xác thực quyền Admin của người gọi
    const authCheck = await verifyAdminCaller(request, keycloakBase);
    if (!authCheck.success) {
      return NextResponse.json(
        { success: false, error: authCheck.error },
        { status: authCheck.status || 401 }
      );
    }

    const adminToken = await getAdminToken(keycloakBase);

    // Lấy tối đa 100 người dùng gần nhất
    const usersRes = await fetch(
      `${keycloakBase}/admin/realms/jhipster/users?max=100`,
      {
        headers: { Authorization: `Bearer ${adminToken}` },
        cache: 'no-store',
      }
    );

    if (!usersRes.ok) {
      return NextResponse.json(
        { success: false, error: 'Không thể lấy danh sách người dùng từ Keycloak' },
        { status: usersRes.status }
      );
    }

    const users = await usersRes.json();

    // Lấy thông tin nhóm/vai trò cho từng người dùng
    const usersWithRoles = await Promise.all(
      users.map(async (u: { id: string; username: string; email?: string; firstName?: string; lastName?: string; enabled?: boolean; createdTimestamp?: number }) => {
        try {
          const rolesRes = await fetch(
            `${keycloakBase}/admin/realms/jhipster/users/${u.id}/role-mappings/realm/composite`,
            {
              headers: { Authorization: `Bearer ${adminToken}` },
              cache: 'no-store',
            }
          );
          const rolesData = rolesRes.ok ? await rolesRes.json() : [];
          const roles = rolesData.map((r: { name: string }) => r.name);
          return {
            id: u.id,
            username: u.username,
            email: u.email || '—',
            firstName: u.firstName || '',
            lastName: u.lastName || '',
            fullName: [u.firstName, u.lastName].filter(Boolean).join(' ') || u.username,
            enabled: u.enabled ?? true,
            createdTimestamp: u.createdTimestamp,
            isAdmin: roles.includes('ROLE_ADMIN'),
            roles: roles.filter((r: string) => ['ROLE_ADMIN', 'ROLE_USER'].includes(r)),
          };
        } catch {
          return {
            id: u.id,
            username: u.username,
            email: u.email || '—',
            firstName: u.firstName || '',
            lastName: u.lastName || '',
            fullName: [u.firstName, u.lastName].filter(Boolean).join(' ') || u.username,
            enabled: u.enabled ?? true,
            createdTimestamp: u.createdTimestamp,
            isAdmin: false,
            roles: ['ROLE_USER'],
          };
        }
      })
    );

    return NextResponse.json({
      success: true,
      users: usersWithRoles,
    });
  } catch (error) {
    const msg = error instanceof Error ? error.message : 'Unknown error';
    return NextResponse.json(
      { success: false, error: `Lỗi máy chủ nội bộ: ${msg}` },
      { status: 500 }
    );
  }
}

/**
 * POST /api/admin/users
 * Cập nhật vai trò Admin (ROLE_ADMIN) cho người dùng
 * Body: { userId: string, makeAdmin: boolean }
 */
export async function POST(request: NextRequest) {
  try {
    const keycloakBase = getKeycloakAdminUrl();

    // Xác thực quyền Admin của người gọi
    const authCheck = await verifyAdminCaller(request, keycloakBase);
    if (!authCheck.success) {
      return NextResponse.json(
        { success: false, error: authCheck.error },
        { status: authCheck.status || 401 }
      );
    }

    const body = await request.json();
    const { userId, makeAdmin } = body;

    if (!userId || typeof makeAdmin !== 'boolean') {
      return NextResponse.json(
        { success: false, error: 'Thiếu userId hoặc makeAdmin' },
        { status: 400 }
      );
    }

    const adminToken = await getAdminToken(keycloakBase);

    // Lấy thông tin role ROLE_ADMIN
    const adminRoleRes = await fetch(
      `${keycloakBase}/admin/realms/jhipster/roles/ROLE_ADMIN`,
      { headers: { Authorization: `Bearer ${adminToken}` } }
    );
    if (!adminRoleRes.ok) {
      return NextResponse.json(
        { success: false, error: 'Không tìm thấy vai trò ROLE_ADMIN trong hệ thống' },
        { status: 500 }
      );
    }
    const adminRole = await adminRoleRes.json();

    // Lấy group Admins
    const groupsRes = await fetch(
      `${keycloakBase}/admin/realms/jhipster/groups`,
      { headers: { Authorization: `Bearer ${adminToken}` } }
    );
    const groups = await groupsRes.json();
    const adminsGroup = groups.find((g: { name: string }) => g.name === 'Admins');

    if (makeAdmin) {
      // Gán role ROLE_ADMIN
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

      // Thêm vào nhóm Admins nếu có
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
      // Gỡ role ROLE_ADMIN
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

      // Xóa khỏi nhóm Admins nếu có
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

    return NextResponse.json({
      success: true,
      message: makeAdmin ? 'Đã cấp quyền Admin thành công!' : 'Đã gỡ quyền Admin thành công!',
    });
  } catch (error) {
    const msg = error instanceof Error ? error.message : 'Unknown error';
    return NextResponse.json(
      { success: false, error: `Lỗi máy chủ nội bộ: ${msg}` },
      { status: 500 }
    );
  }
}
