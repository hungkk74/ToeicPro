'use client';

import { useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import { X, User, Mail, Shield, Target, Key, LogOut, LogIn, ExternalLink, CheckCircle2 } from 'lucide-react';
import { UserAccountDTO } from '@/types/backend';

interface AccountManagementModalProps {
  isOpen: boolean;
  onClose: () => void;
  user: UserAccountDTO | null;
  onLogout: () => void;
  onSwitchAccount: () => void;
}

export default function AccountManagementModal({
  isOpen,
  onClose,
  user,
  onLogout,
  onSwitchAccount,
}: AccountManagementModalProps) {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  // Đóng modal khi nhấn phím Escape
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape') {
        onClose();
      }
    }
    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen || !mounted) return null;

  const displayName = user
    ? [user.firstName, user.lastName].filter(Boolean).join(' ') || user.login
    : 'Alex Morgan';

  const userInitials = user
    ? (user.firstName?.[0] || user.login?.[0] || 'U').toUpperCase()
    : 'AM';

  const email = user?.email || 'alex.morgan@toeicpro.internal';
  const username = user?.login || 'alex.morgan';
  const authorities = user?.authorities && user.authorities.length > 0
    ? user.authorities
    : ['ROLE_USER', 'ROLE_PRO_MEMBER'];

  const isAdmin = Boolean(
    authorities.some((r) => r === 'ROLE_ADMIN' || r.toUpperCase().includes('ADMIN'))
  );

  return createPortal(
    <div
      className="fixed inset-0 z-[99999] bg-slate-900/60 flex items-center justify-center p-4 backdrop-blur-sm animate-in fade-in duration-150"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xl max-w-lg w-full overflow-hidden">
        {/* Modal Header */}
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50">
          <div className="flex items-center gap-2">
            <User className="w-5 h-5 text-blue-600" />
            <h2 className="font-bold text-slate-900 text-base">
              Quản Lý Tài Khoản Học Viên
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-200/60 rounded-lg transition-colors focus:outline-none"
            title="Đóng"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Modal Content */}
        <div className="p-6 space-y-5">
          {/* User Profile Summary Card */}
          <div className="flex items-center gap-4 p-4 rounded-xl bg-slate-50 border border-slate-200">
            <div className="w-14 h-14 rounded-full bg-slate-800 text-white flex items-center justify-center font-bold text-lg ring-2 ring-slate-200 shrink-0">
              {userInitials}
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2">
                <h3 className="text-base font-bold text-slate-900 truncate">
                  {displayName}
                </h3>
                <span className="inline-flex items-center gap-1 text-[11px] font-medium bg-emerald-50 text-emerald-700 border border-emerald-200 px-2 py-0.5 rounded">
                  <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                  Đang hoạt động
                </span>
              </div>
              <p className="text-xs text-slate-500 truncate mt-0.5">{email}</p>
              <div className="flex items-center gap-1.5 mt-2 flex-wrap">
                {authorities.map((role) => (
                  <span
                    key={role}
                    className="text-[10px] font-semibold bg-slate-200/80 text-slate-700 px-2 py-0.5 rounded border border-slate-300"
                  >
                    {role}
                  </span>
                ))}
              </div>
            </div>
          </div>

          {/* Account Details Grid */}
          <div className={`grid grid-cols-1 ${isAdmin ? 'sm:grid-cols-2' : 'sm:grid-cols-3'} gap-3 text-xs`}>
            <div className="p-3 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-2 text-slate-500 mb-1">
                <User className="w-3.5 h-3.5 text-slate-400" />
                <span className="font-semibold uppercase tracking-wider text-[10px]">Tên đăng nhập</span>
              </div>
              <span className="font-semibold text-slate-900">{username}</span>
            </div>

            <div className="p-3 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-2 text-slate-500 mb-1">
                <Mail className="w-3.5 h-3.5 text-slate-400" />
                <span className="font-semibold uppercase tracking-wider text-[10px]">Địa chỉ Email</span>
              </div>
              <span className="font-semibold text-slate-900 truncate block">{email}</span>
            </div>

            <div className="p-3 rounded-lg border border-slate-200 bg-white">
              <div className="flex items-center gap-2 text-slate-500 mb-1">
                <Target className="w-3.5 h-3.5 text-slate-400" />
                <span className="font-semibold uppercase tracking-wider text-[10px]">Mục tiêu điểm thi</span>
              </div>
              <span className="font-semibold text-blue-600">TOEIC 850–950+</span>
            </div>

            {/* Chỉ hiển thị Phương thức xác thực đối với quản trị viên (Admin) */}
            {isAdmin && (
              <div className="p-3 rounded-lg border border-slate-200 bg-white">
                <div className="flex items-center gap-2 text-slate-500 mb-1">
                  <Shield className="w-3.5 h-3.5 text-slate-400" />
                  <span className="font-semibold uppercase tracking-wider text-[10px]">Phương thức xác thực</span>
                </div>
                <span className="font-semibold text-slate-900">Keycloak OIDC Single Sign-On</span>
              </div>
            )}
          </div>

          {/* Chỉ hiển thị Đổi mật khẩu & Phiên bảo mật đối với quản trị viên (Admin) */}
          {isAdmin && (
            <div className="p-3.5 rounded-xl bg-blue-50/70 border border-blue-200 flex items-center justify-between gap-3">
              <div className="text-xs">
                <span className="font-bold text-blue-950 block">Đổi mật khẩu &amp; Phiên bảo mật</span>
                <span className="text-blue-700 text-[11px]">
                  Quản lý mật khẩu và 2FA trực tiếp qua cổng thông tin bảo mật Keycloak.
                </span>
              </div>
              <a
                href="http://localhost:9080/realms/jhipster/account"
                target="_blank"
                rel="noreferrer"
                className="inline-flex items-center gap-1 bg-white hover:bg-slate-100 text-blue-700 border border-blue-200 font-medium text-xs px-3 py-1.5 rounded-lg transition-colors shrink-0 shadow-2xs"
              >
                <Key className="w-3.5 h-3.5 text-blue-600" />
                <span>Cổng bảo mật</span>
                <ExternalLink className="w-3 h-3" />
              </a>
            </div>
          )}
        </div>

        {/* Modal Footer Actions */}
        <div className="px-6 py-3.5 border-t border-slate-100 bg-slate-50 flex items-center justify-between gap-2">
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={onSwitchAccount}
              className="inline-flex items-center gap-1.5 bg-white hover:bg-slate-100 active:bg-slate-200 text-slate-700 border border-slate-200 font-medium text-xs px-3 py-2 rounded-lg transition-colors cursor-pointer"
            >
              <LogIn className="w-3.5 h-3.5 text-slate-500" />
              <span>Đăng nhập tài khoản khác</span>
            </button>

            <button
              type="button"
              onClick={onLogout}
              className="inline-flex items-center gap-1.5 bg-rose-50 hover:bg-rose-100 active:bg-rose-200 text-rose-700 border border-rose-200 font-medium text-xs px-3 py-2 rounded-lg transition-colors cursor-pointer"
            >
              <LogOut className="w-3.5 h-3.5 text-rose-600" />
              <span>Đăng xuất</span>
            </button>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="bg-blue-600 hover:bg-blue-700 text-white font-medium text-xs px-4 py-2 rounded-lg transition-colors"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
}
