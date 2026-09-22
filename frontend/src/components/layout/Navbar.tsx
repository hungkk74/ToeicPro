'use client';

import { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { Search, Bell, ShieldCheck } from 'lucide-react';
import UserAccountMenu from '@/components/account/UserAccountMenu';

export default function Navbar() {
  const [showAdminNav, setShowAdminNav] = useState(false);

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-white border-b border-slate-200 shadow-xs">
      <div className="h-16 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between gap-4">
        {/* Brand Logo & Navigation */}
        <div className="flex items-center gap-6 lg:gap-8">
          <Link className="flex items-center gap-2.5 group focus:outline-none" href="/">
            <div className="w-8 h-8 flex items-center justify-center shrink-0">
              <Image
                src="/logo.png"
                alt="ToeicPro Logo"
                width={32}
                height={32}
                className="w-full h-full object-contain drop-shadow-xs"
                priority
              />
            </div>
            <div className="flex flex-col min-w-0">
              <span className="font-bold text-base text-slate-900 tracking-tight leading-none">
                Toeic<span className="text-blue-600">Pro</span>
              </span>
              <span className="text-[11px] text-slate-500 tracking-normal mt-0.5 whitespace-nowrap hidden lg:block">
                Hệ thống Đào tạo &amp; Thi thử TOEIC Trực tuyến
              </span>
            </div>
          </Link>

          <nav className="hidden md:flex items-center gap-1 shrink-0">
            <Link
              href="/"
              className="px-3 py-1.5 rounded-lg text-sm font-medium text-blue-700 bg-blue-50 border border-blue-100 transition-colors"
            >
              Kho Đề Thi
            </Link>
            <Link
              href="/exam/1"
              className="px-3 py-1.5 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-colors"
            >
              Phòng Thi Thử
            </Link>
            {showAdminNav && (
              <Link
                href="/admin"
                className="px-3 py-1.5 rounded-lg text-sm font-medium text-slate-600 hover:text-blue-600 hover:bg-blue-50 transition-colors inline-flex items-center gap-1.5"
              >
                <ShieldCheck className="w-3.5 h-3.5 text-blue-600" />
                <span>Quản Trị</span>
              </Link>
            )}
          </nav>
        </div>

        {/* Right Tools: Search, Status, Notification, User Profile */}
        <div className="flex items-center gap-2 sm:gap-4 shrink-0">
          {/* Search Bar */}
          <div className="relative hidden md:flex items-center group">
            <div className="absolute left-3.5 pointer-events-none text-slate-400 group-focus-within:text-blue-500 transition-colors">
              <Search className="w-4 h-4" />
            </div>
            <input
              type="text"
              placeholder="Tìm đề thi, bài học..."
              className="w-40 lg:w-56 xl:w-64 pl-10 pr-4 py-2 bg-slate-100 hover:bg-slate-200/70 border-none rounded-xl text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:bg-white focus:ring-2 focus:ring-blue-500/30 focus:shadow-sm transition-all"
            />
          </div>

          <button
            type="button"
            aria-label="Thông báo"
            className="p-2 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors relative focus:outline-none"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-blue-600 rounded-full"></span>
          </button>

          <div className="h-5 w-px bg-slate-200 hidden sm:block"></div>

          {/* User Profile */}
          <div className="flex items-center gap-2.5">
            <UserAccountMenu onRoleResolved={setShowAdminNav} />
          </div>
        </div>
      </div>
    </header>
  );
}
