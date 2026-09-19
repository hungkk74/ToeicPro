'use client';

import Link from 'next/link';
import Image from 'next/image';
import { ArrowLeft, ShieldCheck, Bell, Activity, CircleUserRound } from 'lucide-react';
import { UserAccountDTO } from '@/types/backend';

interface AdminHeaderProps {
  user: UserAccountDTO | null;
  activeTabTitle: string;
}

export default function AdminHeader({ user, activeTabTitle }: AdminHeaderProps) {
  const displayName = user
    ? [user.firstName, user.lastName].filter(Boolean).join(' ') || user.login
    : 'Quản trị viên';



  return (
    <header className="sticky top-0 z-40 bg-white border-b border-slate-200 shadow-2xs">
      <div className="h-16 px-4 sm:px-6 lg:px-8 flex items-center justify-between gap-4">
        {/* Left: Brand & Breadcrumb */}
        <div className="flex items-center gap-4 sm:gap-6">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 flex items-center justify-center shrink-0">
              <Image
                src="/logo.png"
                alt="ToeicPro Logo"
                width={32}
                height={32}
                className="w-full h-full object-contain"
                priority
              />
            </div>
            <div className="flex items-center gap-2">
              <span className="font-bold text-base text-slate-900 tracking-tight leading-none">
                Toeic<span className="text-blue-600">Pro</span>
              </span>
              <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-bold bg-blue-50 text-blue-700 border border-blue-200 uppercase tracking-wider">
                <ShieldCheck className="w-3 h-3 text-blue-600" />
                Admin Console
              </span>
            </div>
          </div>

          <div className="h-4 w-px bg-slate-200 hidden sm:block"></div>

          <div className="hidden sm:flex items-center gap-2 text-xs">
            <span className="text-slate-400">Hệ thống</span>
            <span className="text-slate-300">/</span>
            <span className="font-semibold text-slate-800">{activeTabTitle}</span>
          </div>
        </div>

        {/* Right Tools */}
        <div className="flex items-center gap-3">
          {/* Back to Client App Button */}
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg border border-slate-200 transition-colors"
            title="Quay lại giao diện học viên"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Trang Học Viên</span>
          </Link>

          {/* Quick System Health Tag */}
          <div className="hidden md:flex items-center gap-1.5 px-2.5 py-1 bg-emerald-50 border border-emerald-200 rounded-lg text-emerald-700 text-xs font-medium">
            <Activity className="w-3.5 h-3.5 text-emerald-600 animate-pulse" />
            <span>12/12 Services Online</span>
          </div>

          <button
            type="button"
            className="p-2 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors relative"
            title="Thông báo hệ thống"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-blue-600 rounded-full"></span>
          </button>

          {/* Admin User Pill */}
          <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
            <div className="w-8 h-8 rounded-full bg-white border border-slate-300 flex items-center justify-center shrink-0">
              <CircleUserRound className="w-5 h-5 text-slate-600" />
            </div>
            <div className="hidden lg:flex flex-col text-left">
              <span className="text-xs font-bold text-slate-900 leading-tight">
                {displayName}
              </span>
              <span className="text-[10px] text-blue-600 font-semibold leading-tight">
                Administrator
              </span>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}
