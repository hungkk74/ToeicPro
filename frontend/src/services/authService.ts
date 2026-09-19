import { fetchApi, getBaseApiUrl, getKeycloakUrl, setStoredToken, getStoredToken } from '@/lib/api-client';
import { UserAccountDTO } from '@/types/backend';

/**
 * Kiểm tra người dùng đã đăng nhập chưa (/api/authenticate)
 */
export async function checkIsAuthenticated(): Promise<boolean> {
  const token = getStoredToken();
  if (token) return true;

  try {
    const baseUrl = getBaseApiUrl();
    const res = await fetch(`${baseUrl}/api/authenticate`, {
      credentials: 'include',
    });
    return res.status === 204 || res.ok;
  } catch {
    return false;
  }
}

/**
 * Lấy thông tin tài khoản người dùng hiện tại (/api/account)
 */
export async function getCurrentUser(): Promise<UserAccountDTO | null> {
  try {
    return await fetchApi<UserAccountDTO>('/api/account', {
      credentials: 'include',
    });
  } catch {
    return null;
  }
}

export interface LoginResult {
  success: boolean;
  user?: UserAccountDTO;
  error?: string;
}

/**
 * Đăng nhập trực tiếp (Direct Access Grant) ngay trong ứng dụng:
 * Không chuyển trang sang giao diện web của Keycloak!
 */
export async function loginWithCredentials(
  username: string,
  password: string
): Promise<LoginResult> {
  try {
    const keycloakBase = getKeycloakUrl();
    const body = new URLSearchParams({
      client_id: 'web_app',
      grant_type: 'password',
      username: username.trim(),
      password: password,
    });

    const res = await fetch(`${keycloakBase}/realms/jhipster/protocol/openid-connect/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: body.toString(),
    });

    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      const desc = errData.error_description || 'Tên đăng nhập hoặc mật khẩu không chính xác.';
      return { success: false, error: desc };
    }

    const data = await res.json();
    if (data.access_token) {
      setStoredToken(data.access_token);
      // Lấy thông tin người dùng từ backend Gateway bằng token mới
      const user = await getCurrentUser();
      return { success: true, user: user || undefined };
    }

    return { success: false, error: 'Không nhận được mã xác thực hợp lệ từ hệ thống.' };
  } catch {
    return { success: false, error: 'Không thể kết nối đến máy chủ xác thực Keycloak (:9080).' };
  }
}

/**
 * Đăng xuất trực tiếp ngay trong ứng dụng:
 * Xóa token lưu trữ, gọi API hủy phiên làm việc nền, KHÔNG chuyển hướng sang Keycloak!
 */
export async function performLogout(): Promise<void> {
  setStoredToken(null);
  try {
    const baseUrl = getBaseApiUrl();
    await fetch(`${baseUrl}/api/logout`, {
      method: 'POST',
      credentials: 'include',
    });
  } catch {
    // Không làm gián đoạn người dùng nếu backend đã đóng phiên
  }
}

/**
 * URL fallback OIDC (dự phòng)
 */
export function getLoginUrl(): string {
  const baseUrl = getBaseApiUrl();
  return `${baseUrl}/oauth2/authorization/oidc`;
}

/**
 * URL fallback Logout (dự phòng)
 */
export function getLogoutUrl(): string {
  const baseUrl = getBaseApiUrl();
  return `${baseUrl}/logout`;
}
