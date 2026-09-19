'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { ShieldAlert, ArrowLeft, LogIn, Loader2, Home } from 'lucide-react';
import { getCurrentUser } from '@/services/authService';
import { UserAccountDTO } from '@/types/backend';
import AdminHeader from '@/components/admin/AdminHeader';
import AdminSidebar, { AdminTab } from '@/components/admin/AdminSidebar';
import AdminOverviewTab from '@/components/admin/AdminOverviewTab';
import AdminExamsTab from '@/components/admin/AdminExamsTab';
import AdminUsersTab from '@/components/admin/AdminUsersTab';
import AdminCoursesTab from '@/components/admin/AdminCoursesTab';
import AdminServicesTab from '@/components/admin/AdminServicesTab';
import LoginModal from '@/components/account/LoginModal';

export default function AdminPage() {
  const [user, setUser] = useState<UserAccountDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);

  useEffect(() => {
    getCurrentUser()
      .then((u) => {
        setUser(u);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  const handleLoginSuccess = (newUser: UserAccountDTO) => {
    setUser(newUser);
    setIsLoginModalOpen(false);
  };

  const isAdmin = Boolean(
    user?.authorities?.some((r) => ['ROLE_ADMIN', 'ROLE_STAFF'].includes(r) || r.toUpperCase().includes('ADMIN'))
  );

  const tabTitles: Record<AdminTab, string> = {
    overview: 'Tổng Quan Hệ Thống',
    exams: 'Quản Lý Đề Thi',
    users: 'Quản Lý Học Viên & Tài Khoản',
    courses: 'Quản Lý Khóa Học',
    services: 'Hạ Tầng Microservices',
  };

  // Loading State
  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center gap-3">
        <Loader2 className="w-8 h-8 text-blue-600 animate-spin" />
        <span className="text-xs font-semibold text-slate-600">
          Đang xác thực quyền Quản trị viên...
        </span>
      </div>
    );
  }

  // Access Denied State (Non-admin or Guest)
  if (!user || !isAdmin) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
        <div className="max-w-md w-full bg-white rounded-3xl border border-slate-200 shadow-2xl p-8 text-center space-y-5 animate-in zoom-in-95 duration-200">
          <div className="w-16 h-16 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mx-auto border border-rose-200 shadow-xs">
            <ShieldAlert className="w-8 h-8" />
          </div>

          <div className="space-y-1.5">
            <span className="text-[11px] font-bold text-rose-600 uppercase tracking-wider bg-rose-50 px-2 py-0.5 rounded border border-rose-200">
              403 Forbidden • Yêu Cầu Quyền Admin
            </span>
            <h1 className="text-xl font-bold text-slate-900 tracking-tight">
              Khu Vực Quản Trị Hệ Thống
            </h1>
            <p className="text-xs text-slate-500 leading-relaxed">
              Trang này chỉ dành riêng cho tài khoản có vai trò Quản trị viên (
              <span className="font-semibold text-slate-700">ROLE_ADMIN</span>).
              {user ? (
                <> Tài khoản hiện tại của bạn ({user.login}) không có quyền truy cập.</>
              ) : (
                <> Bạn chưa đăng nhập tài khoản quản trị viên.</>
              )}
            </p>
          </div>

          <div className="pt-2 flex flex-col gap-2.5">
            <button
              type="button"
              onClick={() => setIsLoginModalOpen(true)}
              className="w-full inline-flex items-center justify-center gap-2 py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-semibold rounded-xl transition-colors cursor-pointer shadow-xs"
            >
              <LogIn className="w-4 h-4" />
              <span>Đăng Nhập Tài Khoản Admin</span>
            </button>

            <Link
              href="/"
              className="w-full inline-flex items-center justify-center gap-2 py-2.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-xl transition-colors"
            >
              <Home className="w-4 h-4" />
              <span>Quay Lại Trang Học Viên</span>
            </Link>
          </div>
        </div>

        {/* Modal Đăng Nhập Trực Tiếp */}
        <LoginModal
          isOpen={isLoginModalOpen}
          onClose={() => setIsLoginModalOpen(false)}
          onLoginSuccess={handleLoginSuccess}
        />
      </div>
    );
  }

  // Admin Dashboard Layout
  return (
    <div className="min-h-screen bg-slate-100 flex flex-col">
      {/* Top Header */}
      <AdminHeader user={user} activeTabTitle={tabTitles[activeTab]} />

      {/* Main Workspace: Sidebar + Content Area */}
      <div className="flex-1 flex">
        {/* Left Sidebar */}
        <AdminSidebar activeTab={activeTab} onSelectTab={setActiveTab} />

        {/* Content Body */}
        <main className="flex-1 p-6 lg:p-8 max-w-7xl mx-auto overflow-y-auto w-full">
          {activeTab === 'overview' && (
            <AdminOverviewTab onNavigateTab={setActiveTab} userCount={4} />
          )}
          {activeTab === 'exams' && <AdminExamsTab />}
          {activeTab === 'users' && <AdminUsersTab />}
          {activeTab === 'courses' && <AdminCoursesTab />}
          {activeTab === 'services' && <AdminServicesTab />}
        </main>
      </div>

      {/* Direct Login Modal (if switching accounts inside admin) */}
      <LoginModal
        isOpen={isLoginModalOpen}
        onClose={() => setIsLoginModalOpen(false)}
        onLoginSuccess={handleLoginSuccess}
      />
    </div>
  );
}
