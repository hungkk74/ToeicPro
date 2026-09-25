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
    const { refresh_token } = body;

    if (!refresh_token || typeof refresh_token !== 'string') {
      return NextResponse.json(
        { success: false, error: 'Thiếu refresh token hợp lệ.' },
        { status: 400 }
      );
    }

    const keycloakBase = getKeycloakUrl();
    const tokenParams = new URLSearchParams({
      client_id: 'web_app',
      grant_type: 'refresh_token',
      refresh_token: refresh_token.trim(),
    });

    const tokenRes = await fetch(
      `${keycloakBase}/realms/jhipster/protocol/openid-connect/token`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: tokenParams.toString(),
        signal: AbortSignal.timeout(8000),
      }
    );

    if (!tokenRes.ok) {
      const errData = await tokenRes.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          error: errData.error_description || 'Refresh token không hợp lệ hoặc đã hết hạn.',
        },
        { status: 401 }
      );
    }

    const tokenData = await tokenRes.json();

    return NextResponse.json({
      success: true,
      access_token: tokenData.access_token,
      refresh_token: tokenData.refresh_token || refresh_token,
      expires_in: tokenData.expires_in,
      token_type: tokenData.token_type || 'Bearer',
    });
  } catch (error) {
    const msg = error instanceof Error ? error.message : 'Lỗi hệ thống';
    return NextResponse.json(
      { success: false, error: `Lỗi máy chủ khi làm mới token: ${msg}` },
      { status: 500 }
    );
  }
}
