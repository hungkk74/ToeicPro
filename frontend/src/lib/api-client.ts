/**
 * API Client Utility - Hỗ trợ phân biệt môi trường Client (Browser) vs Server (SSR Docker)
 */

export function getBaseApiUrl(): string {
  const isServer = typeof window === 'undefined';
  if (isServer) {
    // Gọi nội bộ giữa các container trong Docker Network (mặc định: http://gateway:8080)
    return process.env.INTERNAL_API_URL || process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  }
  // Gọi từ trình duyệt của người dùng (mặc định: http://localhost:8080)
  return process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
}

export function getKeycloakUrl(): string {
  return process.env.NEXT_PUBLIC_KEYCLOAK_URL || 'http://localhost:9080';
}

export function getStoredToken(): string | null {
  if (typeof window === 'undefined') return null;
  try {
    return localStorage.getItem('toeic_auth_token');
  } catch {
    return null;
  }
}

export function setStoredToken(token: string | null): void {
  if (typeof window === 'undefined') return;
  try {
    if (token) {
      localStorage.setItem('toeic_auth_token', token);
    } else {
      localStorage.removeItem('toeic_auth_token');
    }
  } catch {
    // Storage safeguard
  }
}

export async function fetchApi<T = unknown>(
  path: string,
  options: RequestInit = {},
  token?: string
): Promise<T> {
  const baseUrl = getBaseApiUrl();
  const url = `${baseUrl}${path.startsWith('/') ? path : `/${path}`}`;

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  const effectiveToken = token || getStoredToken();
  if (effectiveToken) {
    headers['Authorization'] = `Bearer ${effectiveToken}`;
  }

  const response = await fetch(url, {
    cache: 'no-store',
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorText = await response.text().catch(() => '');
    throw new Error(`API Error [${response.status}] ${response.statusText}: ${errorText}`);
  }

  // Nếu response rỗng (vd: 204 No Content)
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json() as Promise<T>;
  }

  return response.text() as unknown as T;
}
