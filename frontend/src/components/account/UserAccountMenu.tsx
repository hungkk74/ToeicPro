'use client';

import { useState, useRef, useEffect } from 'react';
import Link from 'next/link';
import { ChevronDown, LogOut, LogIn, User, Shield, ExternalLink, UserPlus, LayoutDashboard, CircleUserRound } from 'lucide-react';
import { getCurrentUser, performLogout } from '@/services/authService';
import { UserAccountDTO } from '@/types/backend';
import AccountManagementModal from './AccountManagementModal';
import LoginModal from './LoginModal';
import RegisterModal from './RegisterModal';

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
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
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
  };

  // Mở modal đăng nhập trực tiếp (Không redirect sang Keycloak)
  const handleSwitchAccount = () => {
    setIsDropdownOpen(false);
    setIsModalOpen(false);
    setIsRegisterModalOpen(false);
    setIsLoginModalOpen(true);
  };

  // Mở modal đăng ký trực tiếp
  const handleOpenRegister = () => {
    setIsDropdownOpen(false);
    setIsModalOpen(false);
    setIsLoginModalOpen(false);
    setIsRegisterModalOpen(true);
  };

  const handleOpenAccountManagement = () => {
    setIsDropdownOpen(false);
    setIsModalOpen(true);
  };

  const handleLoginSuccess = (newUser: UserAccountDTO) => {
    setUser(newUser);
    setIsLoginModalOpen(false);
  };

  const handleRegisterSuccess = (newUser: UserAccountDTO) => {
    setUser(newUser);
    setIsRegisterModalOpen(false);
  };

  const handleSwitchToLogin = () => {
    setIsRegisterModalOpen(false);
    setIsLoginModalOpen(true);
  };

  const handleSwitchToRegister = () => {
    setIsLoginModalOpen(false);
    setIsRegisterModalOpen(true);
  };

  const displayName = user
    ? [user.firstName, user.lastName].filter(Boolean).join(' ') || user.login
    : 'Chưa đăng nhập';



  const isAdmin = hasPrivilegedRole(user?.authorities);

  return (
    <>
      <div className="relative flex items-center gap-2" ref={dropdownRef}>
        {/* Nút Đăng ký nhanh khi chưa đăng nhập (desktop view) */}
        {!user && !compact && (
          <button
            type="button"
            onClick={handleOpenRegister}
            className="hidden sm:inline-flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium text-xs rounded-lg transition-colors shadow-xs cursor-pointer"
            title="Đăng ký tài khoản học viên mới"
          >
            <UserPlus className="w-3.5 h-3.5" />
            <span>Đăng ký</span>
          </button>
        )}

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
                {user?.email || (user ? 'Target: 950+' : 'Bấm để đăng nhập')}
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
              onClick={user ? handleOpenAccountManagement : handleSwitchAccount}
              title={user ? 'Bấm để xem chi tiết hồ sơ' : 'Bấm để đăng nhập'}
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
                    {user?.email || (user ? 'Học viên TOEIC Pro' : 'Nhấn để đăng nhập')}
                  </span>
                </div>
              </div>
            </div>

            {/* Menu Items */}
            <div className="p-1.5 space-y-0.5">
              {user && (
                <button
                  type="button"
                  onClick={handleOpenAccountManagement}
                  className="flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-slate-800 hover:bg-blue-50 hover:text-blue-700 rounded-lg w-full text-left transition-colors cursor-pointer"
                >
                  <User className="w-4 h-4 text-blue-600" />
                  <span className="font-semibold">Quản lý tài khoản</span>
                </button>
              )}

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
                  href="http://localhost:9080/realms/jhipster/account"
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

              {user && <div className="h-px bg-slate-100 my-1"></div>}

              <button
                type="button"
                onClick={handleSwitchAccount}
                className="flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-slate-700 hover:bg-slate-100 hover:text-slate-900 rounded-lg w-full text-left transition-colors cursor-pointer"
              >
                <LogIn className="w-4 h-4 text-slate-500" />
                <span>{user ? 'Đăng nhập tài khoản khác' : 'Đăng nhập'}</span>
              </button>

              {/* Nút Đăng ký tài khoản mới */}
              <button
                type="button"
                onClick={handleOpenRegister}
                className="flex items-center gap-2.5 px-3 py-2 text-xs font-semibold text-blue-700 hover:bg-blue-50 rounded-lg w-full text-left transition-colors cursor-pointer"
              >
                <UserPlus className="w-4 h-4 text-blue-600" />
                <span>Đăng ký tài khoản mới</span>
              </button>

              {user && (
                <button
                  type="button"
                  onClick={handleLogout}
                  className="flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-rose-600 hover:bg-rose-50 hover:text-rose-700 rounded-lg w-full text-left transition-colors cursor-pointer"
                >
                  <LogOut className="w-4 h-4 text-rose-600" />
                  <span>Đăng xuất tài khoản</span>
                </button>
              )}
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
        onSwitchAccount={handleSwitchAccount}
      />

      {/* Modal Đăng Nhập Trực Tiếp Tại Trang (Zero Keycloak Redirect) */}
      <LoginModal
        isOpen={isLoginModalOpen}
        onClose={() => setIsLoginModalOpen(false)}
        onLoginSuccess={handleLoginSuccess}
        onSwitchToRegister={handleSwitchToRegister}
      />

      {/* Modal Đăng Ký Trực Tiếp Tại Trang (Zero Keycloak Redirect) */}
      <RegisterModal
        isOpen={isRegisterModalOpen}
        onClose={() => setIsRegisterModalOpen(false)}
        onRegisterSuccess={handleRegisterSuccess}
        onSwitchToLogin={handleSwitchToLogin}
      />
    </>
  );
}
