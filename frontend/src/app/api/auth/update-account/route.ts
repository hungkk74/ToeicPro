import { NextRequest, NextResponse } from 'next/server';

function getKeycloakAdminUrl(): string {
  return (
    process.env.INTERNAL_KEYCLOAK_URL ||
    process.env.NEXT_PUBLIC_KEYCLOAK_URL ||
    'http://localhost:9080'
  );
}

export async function PUT(request: NextRequest) {
  try {
    const authHeader = request.headers.get('Authorization');
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return NextResponse.json({ success: false, error: 'Unauthorized' }, { status: 401 });
    }
    const userToken = authHeader.substring(7);

    const body = await request.json();
    const { firstName, lastName, email } = body;

    const keycloakBase = getKeycloakAdminUrl();

    // 1. Lấy thông tin userinfo bằng token của người dùng
    const userInfoRes = await fetch(`${keycloakBase}/realms/jhipster/protocol/openid-connect/userinfo`, {
      headers: { Authorization: `Bearer ${userToken}` }
    });

    if (!userInfoRes.ok) {
      return NextResponse.json({ success: false, error: 'Token không hợp lệ hoặc đã hết hạn.' }, { status: 401 });
    }

    const userInfo = await userInfoRes.json();
    const userId = userInfo.sub;

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
      return NextResponse.json(
        { success: false, error: `Không thể kết nối dịch vụ quản trị Keycloak: ${errText}` },
        { status: 502 }
      );
    }

    const tokenData = await tokenRes.json();
    const adminToken = tokenData.access_token;

    // 3. Gọi Keycloak Admin API để update user (chỉ lấy các trường user hiện có trên hệ thống, update firstName, lastName, email)
    // Đầu tiên fetch user detail để không ghi đè các trường khác
    const getUserRes = await fetch(`${keycloakBase}/admin/realms/jhipster/users/${userId}`, {
      headers: { Authorization: `Bearer ${adminToken}` }
    });
    if (!getUserRes.ok) {
      return NextResponse.json({ success: false, error: 'Không tìm thấy thông tin tài khoản trên hệ thống quản trị.' }, { status: 404 });
    }
    const userDetail = await getUserRes.json();

    userDetail.firstName = (firstName || '').trim();
    userDetail.lastName = (lastName || '').trim();
    if (email) {
      userDetail.email = email.trim().toLowerCase();
    }

    const updateRes = await fetch(`${keycloakBase}/admin/realms/jhipster/users/${userId}`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${adminToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userDetail),
      signal: AbortSignal.timeout(8000),
    });

    if (updateRes.status === 204) {
      return NextResponse.json({ success: true, message: 'Cập nhật thông tin thành công!' });
    }

    if (updateRes.status === 409) {
      return NextResponse.json({ success: false, error: 'Địa chỉ email đã tồn tại trong hệ thống.' }, { status: 409 });
    }

    const errorJson = await updateRes.json().catch(() => null);
    const errorMessage = errorJson?.errorMessage || errorJson?.error || `Cập nhật thất bại với mã lỗi HTTP ${updateRes.status}.`;
    return NextResponse.json({ success: false, error: errorMessage }, { status: updateRes.status });

  } catch (error) {
    const msg = error instanceof Error ? error.message : 'Lỗi hệ thống';
    return NextResponse.json({ success: false, error: `Lỗi máy chủ nội bộ: ${msg}` }, { status: 500 });
  }
}
