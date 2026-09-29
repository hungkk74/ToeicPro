import { NextRequest, NextResponse } from 'next/server';
import {
  getKeycloakAdminUrl,
  getAdminToken,
  verifyAdminCaller,
  fetchUsersWithRoles,
  updateUserAdminRole,
} from '@/lib/keycloak-admin';

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
    const usersWithRoles = await fetchUsersWithRoles(keycloakBase, adminToken);

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
    await updateUserAdminRole(keycloakBase, adminToken, userId, makeAdmin);

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
