import { fetchApi, getBaseApiUrl, getKeycloakUrl, setStoredToken, getStoredToken, setStoredRefreshToken } from '@/lib/api-client';
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

export function parseUserFromToken(token: string): UserAccountDTO | null {
  try {
    const parts = token.split('.');
    if (parts.length < 2) return null;
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const payload = JSON.parse(jsonPayload);
    const authorities: string[] =
      payload.roles || payload.realm_access?.roles || ['ROLE_USER'];

    return {
      id: payload.sub,
      login: payload.preferred_username || payload.sub,
      firstName: payload.given_name || payload.name || '',
      lastName: payload.family_name || '',
      email: payload.email || '',
      authorities: authorities,
    };
  } catch {
    return null;
  }
}

/**
 * Lấy thông tin tài khoản người dùng hiện tại (/api/account)
 */
export async function getCurrentUser(): Promise<UserAccountDTO | null> {
  const token = getStoredToken();
  if (!token) return null;

  try {
    const user = await fetchApi<UserAccountDTO>('/api/account', {
      credentials: 'include',
    });
    if (user && user.login) return user;
  } catch {
    // Sử dụng thông tin từ JWT đã giải mã nếu endpoint backend bận
  }

  const userFromToken = parseUserFromToken(token);
  if (!userFromToken) return null;

  if (typeof window !== 'undefined') {
    try {
      const cached = localStorage.getItem('user_profile_override');
      if (cached) {
        const parsed = JSON.parse(cached);
        if (parsed && parsed.id === userFromToken.id) {
          return { ...userFromToken, ...parsed };
        }
      }
    } catch {
      // Ignore cache parse error
    }
  }

  return userFromToken;
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
      if (data.refresh_token) {
        setStoredRefreshToken(data.refresh_token);
      }
      let user = await getCurrentUser();
      if (!user) {
        user = parseUserFromToken(data.access_token);
      }
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('auth-state-changed', { detail: { user } }));
      }
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


export interface UpdateAccountPayload {
  firstName?: string;
  lastName?: string;
  email?: string;
}

export interface UpdateAccountResult {
  success: boolean;
  error?: string;
  message?: string;
}

/**
 * Cập nhật thông tin tài khoản người dùng
 */
export async function updateUserAccount(payload: UpdateAccountPayload): Promise<UpdateAccountResult> {
  const token = getStoredToken();
  if (!token) {
    return { success: false, error: 'Bạn chưa đăng nhập.' };
  }

  try {
    const res = await fetch('/api/auth/update-account', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(payload),
    });

    const data = await res.json().catch(() => ({}));
    if (!res.ok || !data.success) {
      return {
        success: false,
        error: data.error || 'Cập nhật tài khoản thất bại.',
      };
    }

    return {
      success: true,
      message: data.message || 'Cập nhật thông tin thành công!',
    };
  } catch {
    return {
      success: false,
      error: 'Không thể kết nối đến máy chủ. Vui lòng thử lại.',
    };
  }
}



/**
 * Đăng xuất trực tiếp ngay trong ứng dụng:
 * Xóa token lưu trữ, gọi API hủy phiên làm việc nền, KHÔNG chuyển hướng sang Keycloak!
 */
export async function performLogout(): Promise<void> {
  setStoredToken(null);
  setStoredRefreshToken(null);
  if (typeof window !== 'undefined') {
    try {
      localStorage.removeItem('user_profile_override');
    } catch {}
    window.dispatchEvent(new CustomEvent('auth-state-changed', { detail: { user: null } }));
  }
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
