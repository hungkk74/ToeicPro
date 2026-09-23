import { NextRequest, NextResponse } from 'next/server';

function getKeycloakAdminUrl(): string {
  return (
    process.env.INTERNAL_KEYCLOAK_URL ||
    process.env.NEXT_PUBLIC_KEYCLOAK_URL ||
    'http://localhost:9080'
  );
}


export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { username, email, password, firstName, lastName } = body;

    // 1. Kiểm tra đầu vào hợp lệ
    if (!username || typeof username !== 'string' || username.trim().length < 3) {
      return NextResponse.json(
        { success: false, error: 'Tên đăng nhập phải có ít nhất 3 ký tự.' },
        { status: 400 }
      );
    }

    if (!email || typeof email !== 'string' || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      return NextResponse.json(
        { success: false, error: 'Địa chỉ email không hợp lệ.' },
        { status: 400 }
      );
    }

    if (!password || typeof password !== 'string' || password.length < 4) {
      return NextResponse.json(
        { success: false, error: 'Mật khẩu phải có ít nhất 4 ký tự.' },
        { status: 400 }
      );
    }

    const keycloakBase = getKeycloakAdminUrl();

    console.log(`[API /api/auth/register] Bắt đầu đăng ký cho user: ${username}, email: ${email}`);

    // 2. Lấy Admin Access Token từ Realm 'master' qua client 'admin-cli'
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
        signal: AbortSignal.timeout(8000),
      }
    );

    if (!tokenRes.ok) {
      const errText = await tokenRes.text().catch(() => '');
      console.error(`[API /api/auth/register] Lỗi lấy admin token: status ${tokenRes.status}, ${errText}`);
      return NextResponse.json(
        {
          success: false,
          error: `Không thể kết nối dịch vụ quản trị Keycloak (${tokenRes.status}): ${errText}`,
        },
        { status: 502 }
      );
    }

    const tokenData = await tokenRes.json();
    const adminToken = tokenData.access_token;

    // 3. Gọi Keycloak Admin API tạo User trong Realm 'jhipster'
    const cleanUsername = username.trim().toLowerCase();
    const cleanEmail = email.trim().toLowerCase();

    const createPayload = {
      username: cleanUsername,
      email: cleanEmail,
      firstName: (firstName || '').trim(),
      lastName: (lastName || '').trim(),
      enabled: true,
      emailVerified: true,
      credentials: [
        {
          type: 'password',
          value: password,
          temporary: false,
        },
      ],
      realmRoles: ['ROLE_USER'],
    };

    const createRes = await fetch(`${keycloakBase}/admin/realms/jhipster/users`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${adminToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(createPayload),
      signal: AbortSignal.timeout(8000),
    });

    if (createRes.status === 201) {
      return NextResponse.json(
        {
          success: true,
          message: 'Tài khoản đã được tạo thành công!',
          username: cleanUsername,
        },
        { status: 201 }
      );
    }

    if (createRes.status === 409) {
      return NextResponse.json(
        {
          success: false,
          error: 'Tên đăng nhập hoặc địa chỉ email đã tồn tại trong hệ thống.',
        },
        { status: 409 }
      );
    }

    const errorJson = await createRes.json().catch(() => null);
    const errorMessage =
      errorJson?.errorMessage ||
      errorJson?.error ||
      `Đăng ký thất bại với mã lỗi HTTP ${createRes.status}.`;

    return NextResponse.json(
      {
        success: false,
        error: errorMessage,
      },
      { status: createRes.status }
    );
  } catch (error) {
    const msg = error instanceof Error ? error.message : 'Lỗi hệ thống';
    return NextResponse.json(
      {
        success: false,
        error: `Lỗi máy chủ nội bộ: ${msg}`,
      },
      { status: 500 }
    );
  }
}
