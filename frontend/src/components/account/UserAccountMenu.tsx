'use client';

import { useState, useRef, useEffect } from 'react';
import Link from 'next/link';
import { ChevronDown, LogOut, LogIn, User, Shield, ExternalLink, UserPlus, LayoutDashboard, CircleUserRound } from 'lucide-react';
import { getCurrentUser, performLogout } from '@/services/authService';
import { getKeycloakUrl } from '@/lib/api-client';
import { UserAccountDTO } from '@/types/backend';
import AccountManagementModal from './AccountManagementModal';

const PRIVILEGED_ROLES = ['ROLE_ADMIN', 'ROLE_STAFF'];

function hasPrivilegedRole(authorities?: string[]): boolean {
  return Boolean(
    authorities?.some((r) => PRIVILEGED_ROLES.includes(r) || r.toUpperCase().includes('ADMIN'))
  );
}

interface UserAccountMenuProps {
  compact?: boolean;
  onRoleResolved?: (isPrivileged: boolean) => void;
}

export default function UserAccountMenu({ compact = false, onRoleResolved }: UserAccountMenuProps) {
  const [user, setUser] = useState<UserAccountDTO | null>(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    getCurrentUser().then((userData) => {
      if (userData) {
        setUser(userData);
        onRoleResolved?.(hasPrivilegedRole(userData.authorities));
      } else {
        onRoleResolved?.(false);
      }
    });

    const handleAuthChange = (e: Event) => {
      const customEvent = e as CustomEvent<{ user: UserAccountDTO | null }>;
      const newUser = customEvent.detail?.user || null;
      setUser(newUser);
      onRoleResolved?.(hasPrivilegedRole(newUser?.authorities));
    };

    window.addEventListener('auth-state-changed', handleAuthChange);
    return () => {
      window.removeEventListener('auth-state-changed', handleAuthChange);
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Đóng dropdown khi click ra ngoài
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Đăng xuất trực tiếp ngay trên trang (Không redirect sang Keycloak)
  const handleLogout = async () => {
    await performLogout();
    setUser(null);
    setIsDropdownOpen(false);
    setIsModalOpen(false);
    onRoleResolved?.(false);
  };

  const handleOpenAccountManagement = () => {
    setIsDropdownOpen(false);
    setIsModalOpen(true);
  };

  if (!user) {
    return (
      <div className="flex items-center gap-2">
        {!compact && (
          <Link
            href="/dang-ky"
            className="hidden sm:inline-flex items-center gap-1.5 px-3 py-1.5 text-slate-700 hover:text-slate-900 hover:bg-slate-100 font-medium text-xs rounded-lg transition-colors cursor-pointer"
            title="Đăng ký tài khoản học viên mới"
          >
            <UserPlus className="w-3.5 h-3.5 text-slate-500" />
            <span>Đăng ký</span>
          </Link>
        )}
        <Link
          href="/dang-nhap"
          className="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium text-xs rounded-lg transition-colors shadow-xs cursor-pointer"
          title="Đăng nhập tài khoản"
        >
          <LogIn className="w-3.5 h-3.5" />
          <span>Đăng nhập</span>
        </Link>
      </div>
    );
  }

  const displayName = [user.firstName, user.lastName].filter(Boolean).join(' ') || user.login;
  const isAdmin = hasPrivilegedRole(user?.authorities);

  return (
    <>
      <div className="relative flex items-center gap-2" ref={dropdownRef}>
        {/* Nút người dùng mở menu thao tác */}
        <button
          type="button"
          onClick={() => setIsDropdownOpen((prev) => !prev)}
          className="flex items-center gap-2 p-1.5 rounded-lg hover:bg-slate-100 transition-colors focus:outline-none text-left cursor-pointer group"
          title="Tài khoản học viên - Bấm để mở menu quản lý"
          aria-expanded={isDropdownOpen}
          aria-haspopup="true"
        >
          <div className="w-8 h-8 rounded-full bg-white border border-slate-300 flex items-center justify-center shrink-0 group-hover:border-blue-500 transition-all">
            <CircleUserRound className="w-5 h-5 text-slate-600" />
          </div>
          {!compact && (
            <div className="hidden lg:flex flex-col text-left">
              <span className="text-xs font-semibold text-slate-800 group-hover:text-blue-600 transition-colors leading-tight">
                {displayName}
              </span>
              <span className="text-[10px] text-slate-500 leading-tight">
                {user.email || 'Target: 950+'}
              </span>
            </div>
          )}
          <ChevronDown
            className={`w-3.5 h-3.5 text-slate-400 group-hover:text-slate-700 transition-transform ${isDropdownOpen ? 'rotate-180 text-blue-600' : ''}`}
          />
        </button>

        {/* Dropdown Menu */}
        {isDropdownOpen && (
          <div className="absolute right-0 top-full mt-2 w-64 bg-white rounded-xl border border-slate-200 shadow-xl py-1.5 z-50 animate-in fade-in zoom-in-95 duration-100">
            {/* User Brief Info Header */}
            <div
              className="px-3.5 py-2.5 border-b border-slate-100 bg-slate-50/50 cursor-pointer hover:bg-slate-100/60 transition-colors"
              onClick={handleOpenAccountManagement}
              title="Bấm để xem chi tiết hồ sơ"
            >
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-full bg-white border border-slate-300 flex items-center justify-center shrink-0">
                  <CircleUserRound className="w-5.5 h-5.5 text-slate-600" />
                </div>
                <div className="flex flex-col overflow-hidden">
                  <span className="text-xs font-bold text-slate-900 truncate">
                    {displayName}
                  </span>
                  <span className="text-[11px] text-slate-500 truncate">
                    {user.email || 'Học viên TOEIC Pro'}
                  </span>
                </div>
              </div>
            </div>

            {/* Menu Items */}
            <div className="p-1.5 space-y-0.5">
              <button
                type="button"
                onClick={handleOpenAccountManagement}
                className="flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-slate-800 hover:bg-blue-50 hover:text-blue-700 rounded-lg w-full text-left transition-colors cursor-pointer"
              >
                <User className="w-4 h-4 text-blue-600" />
                <span className="font-semibold">Quản lý tài khoản</span>
              </button>

              {/* Trang Quản Trị Hệ Thống - Chỉ hiển thị cho Admin */}
              {isAdmin && (
                <Link
                  href="/admin"
                  onClick={() => setIsDropdownOpen(false)}
                  className="flex items-center justify-between px-3 py-2 text-xs font-bold text-blue-700 bg-blue-50/80 hover:bg-blue-100 rounded-lg w-full text-left transition-colors cursor-pointer"
                >
                  <div className="flex items-center gap-2.5">
                    <LayoutDashboard className="w-4 h-4 text-blue-600" />
                    <span>Trang Quản Trị Admin</span>
                  </div>
                  <span className="text-[10px] bg-blue-600 text-white font-bold px-1.5 py-0.5 rounded uppercase">
                    Admin
                  </span>
                </Link>
              )}

              {/* Cổng bảo mật Keycloak - Chỉ hiển thị cho Admin */}
              {isAdmin && (
                <a
                  href={`${getKeycloakUrl()}/realms/jhipster/account`}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center justify-between px-3 py-2 text-xs font-medium text-slate-700 hover:bg-slate-100 hover:text-slate-900 rounded-lg w-full text-left transition-colors cursor-pointer"
                >
                  <div className="flex items-center gap-2.5">
                    <Shield className="w-4 h-4 text-slate-500" />
                    <span>Cổng bảo mật Keycloak</span>
                  </div>
                  <ExternalLink className="w-3 h-3 text-slate-400" />
                </a>
              )}

              <div className="h-px bg-slate-100 my-1"></div>

              <Link
                href="/dang-nhap"
                onClick={() => setIsDropdownOpen(false)}
                className="flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-slate-700 hover:bg-slate-100 hover:text-slate-900 rounded-lg w-full text-left transition-colors cursor-pointer"
              >
                <LogIn className="w-4 h-4 text-slate-500" />
                <span>Đăng nhập tài khoản khác</span>
              </Link>

              <button
                type="button"
                onClick={handleLogout}
                className="flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-rose-600 hover:bg-rose-50 hover:text-rose-700 rounded-lg w-full text-left transition-colors cursor-pointer"
              >
                <LogOut className="w-4 h-4 text-rose-600" />
                <span>Đăng xuất tài khoản</span>
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Modal Quản Lý Tài Khoản Chi Tiết */}
      <AccountManagementModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        user={user}
        onLogout={handleLogout}
      />
    </>
  );
}
