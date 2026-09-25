/**
 * API Client Utility - Hỗ trợ phân biệt môi trường Client (Browser) vs Server (SSR Docker)
 * Hỗ trợ tự động làm mới access token (Silent Refresh) khi gặp HTTP 401.
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

export function getStoredRefreshToken(): string | null {
  if (typeof window === 'undefined') return null;
  try {
    return localStorage.getItem('toeic_refresh_token');
  } catch {
    return null;
  }
}

export function setStoredRefreshToken(refreshToken: string | null): void {
  if (typeof window === 'undefined') return;
  try {
    if (refreshToken) {
      localStorage.setItem('toeic_refresh_token', refreshToken);
    } else {
      localStorage.removeItem('toeic_refresh_token');
    }
  } catch {
    // Storage safeguard
  }
}

let refreshTokenPromise: Promise<string | null> | null = null;

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = getStoredRefreshToken();
  if (!refreshToken) {
    return null;
  }

  if (refreshTokenPromise) {
    return refreshTokenPromise;
  }

  refreshTokenPromise = (async () => {
    try {
      const res = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refresh_token: refreshToken }),
      });

      if (!res.ok) {
        setStoredToken(null);
        setStoredRefreshToken(null);
        if (typeof window !== 'undefined') {
          window.dispatchEvent(new CustomEvent('auth-state-changed', { detail: { user: null } }));
        }
        return null;
      }

      const data = await res.json();
      if (data.success && data.access_token) {
        setStoredToken(data.access_token);
        if (data.refresh_token) {
          setStoredRefreshToken(data.refresh_token);
        }
        return data.access_token as string;
      }
      return null;
    } catch {
      return null;
    } finally {
      refreshTokenPromise = null;
    }
  })();

  return refreshTokenPromise;
}

export async function fetchApi<T = unknown>(
  path: string,
  options: RequestInit & { skipAuth?: boolean; _isRetry?: boolean } = {},
  token?: string
): Promise<T> {
  const baseUrl = getBaseApiUrl();
  const url = `${baseUrl}${path.startsWith('/') ? path : `/${path}`}`;

  const { skipAuth, _isRetry, ...fetchOptions } = options;

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(fetchOptions.headers as Record<string, string>),
  };

  const effectiveToken = skipAuth ? null : (token || getStoredToken());
  if (effectiveToken) {
    headers['Authorization'] = `Bearer ${effectiveToken}`;
  }

  let response = await fetch(url, {
    cache: 'no-store',
    credentials: 'include',
    ...fetchOptions,
    headers,
  });

  // Tự động refresh token khi gặp 401 (chỉ retry 1 lần)
  if (response.status === 401 && !skipAuth && !_isRetry && typeof window !== 'undefined') {
    const newToken = await refreshAccessToken();
    if (newToken) {
      headers['Authorization'] = `Bearer ${newToken}`;
      response = await fetch(url, {
        cache: 'no-store',
        credentials: 'include',
        ...fetchOptions,
        headers,
      });
    } else {
      setStoredToken(null);
      setStoredRefreshToken(null);
    }
  }

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
