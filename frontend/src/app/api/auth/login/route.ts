import { NextRequest, NextResponse } from 'next/server';

function getKeycloakUrl(): string {
  return (
    process.env.INTERNAL_KEYCLOAK_URL ||
    process.env.NEXT_PUBLIC_KEYCLOAK_URL ||
    'http://localhost:9080'
  );
}


export async function POST(request: NextRequest) {
  try {
    const body = await request.json().catch(() => ({}));
    const { username, password } = body;

    // 1. Kiểm tra đầu vào
    if (!username || typeof username !== 'string' || !username.trim()) {
      return NextResponse.json(
        { success: false, error: 'Vui lòng cung cấp tên đăng nhập.' },
        { status: 400 }
      );
    }

    if (!password || typeof password !== 'string') {
      return NextResponse.json(
        { success: false, error: 'Vui lòng cung cấp mật khẩu.' },
        { status: 400 }
      );
    }

    const keycloakBase = getKeycloakUrl();


    const loginParams = new URLSearchParams({
      client_id: 'web_app',
      grant_type: 'password',
      username: username.trim(),
      password: password,
      scope: 'openid profile email',
    });

    console.log(`[API /api/auth/login] Nhận yêu cầu đăng nhập: ${username}`);
    const tokenRes = await fetch(
      `${keycloakBase}/realms/jhipster/protocol/openid-connect/token`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: loginParams.toString(),
        signal: AbortSignal.timeout(8000),
      }
    );

    if (!tokenRes.ok) {
      const errData = await tokenRes.json().catch(() => ({}));
      const errorDesc =
        errData.error_description ||
        (errData.error === 'invalid_grant'
          ? 'Tên đăng nhập hoặc mật khẩu không chính xác.'
          : 'Đăng nhập thất bại.');

      return NextResponse.json(
        {
          success: false,
          error: errorDesc,
        },
        { status: tokenRes.status === 400 || tokenRes.status === 401 ? 401 : tokenRes.status }
      );
    }

    const tokenData = await tokenRes.json();

    return NextResponse.json(
      {
        success: true,
        message: 'Đăng nhập thành công!',
        access_token: tokenData.access_token,
        refresh_token: tokenData.refresh_token,
        expires_in: tokenData.expires_in,
        token_type: tokenData.token_type || 'Bearer',
        scope: tokenData.scope,
      },
      { status: 200 }
    );
  } catch (error) {
    const msg = error instanceof Error ? error.message : 'Lỗi kết nối';
    return NextResponse.json(
      {
        success: false,
        error: `Không thể kết nối đến máy chủ xác thực Keycloak: ${msg}`,
      },
      { status: 502 }
    );
  }
}
