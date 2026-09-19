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


export async function loginWithCredentials(
  username: string,
  password: string
): Promise<LoginResult> {
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username.trim(),
        password: password,
      }),
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok || !data.success) {
      return { success: false, error: data.error || 'Tên đăng nhập hoặc mật khẩu không chính xác.' };
    }

    if (data.access_token) {
      setStoredToken(data.access_token);
      // Lấy thông tin người dùng từ backend Gateway bằng token mới
      const user = await getCurrentUser();
      return { success: true, user: user || undefined };
    }

    return { success: false, error: 'Không nhận được mã xác thực hợp lệ từ hệ thống.' };
  } catch {
    return { success: false, error: 'Không thể kết nối đến máy chủ xác thực (:3000).' };
  }
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface RegisterResult {
  success: boolean;
  user?: UserAccountDTO;
  error?: string;
}


export async function registerNewUser(payload: RegisterPayload): Promise<RegisterResult> {
  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    const data = await res.json().catch(() => ({}));
    if (!res.ok || !data.success) {
      return {
        success: false,
        error: data.error || 'Đăng ký tài khoản thất bại. Vui lòng thử lại.',
      };
    }


    const loginRes = await loginWithCredentials(payload.username, payload.password);
    if (loginRes.success) {
      return { success: true, user: loginRes.user };
    }

    return {
      success: true,
      error: 'Tạo tài khoản thành công! Vui lòng đăng nhập với thông tin vừa tạo.',
    };
  } catch {
    return {
      success: false,
      error: 'Không thể kết nối đến máy chủ đăng ký. Vui lòng kiểm tra lại kết nối.',
    };
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
