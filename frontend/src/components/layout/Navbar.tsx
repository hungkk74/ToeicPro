'use client';

import Link from 'next/link';
import { Search, Bell, Sparkles } from 'lucide-react';
import BackendStatus from '@/components/BackendStatus';
import UserAccountMenu from '@/components/account/UserAccountMenu';

export default function Navbar() {
  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-white border-b border-slate-200 shadow-xs">
      <div className="h-16 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between gap-4">
        {/* Brand Logo & Navigation */}
        <div className="flex items-center gap-6 lg:gap-8">
          <Link className="flex items-center gap-2.5 group focus:outline-none" href="/">
            <div className="w-8 h-8 rounded-lg bg-blue-600 text-white flex items-center justify-center font-bold text-sm shadow-xs group-hover:bg-blue-700 transition-colors shrink-0">
              <Sparkles className="w-4 h-4 text-white" />
            </div>
            <div className="flex flex-col">
              <div className="flex items-center gap-1.5">
                <span className="font-bold text-base text-slate-900 tracking-tight leading-none">
                  Toeic<span className="text-blue-600">Pro</span>
                </span>
                <span className="px-1.5 py-0.5 rounded text-[10px] font-medium bg-slate-100 text-slate-600 border border-slate-200">
                  v2026
                </span>
              </div>
              <span className="text-[11px] text-slate-500 tracking-normal mt-0.5 whitespace-nowrap hidden sm:block">
                Hệ thống Đào tạo &amp; Thi thử TOEIC Trực tuyến
              </span>
            </div>
          </Link>

          <nav className="hidden md:flex items-center gap-1">
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
          </nav>
        </div>

        {/* Right Tools: Search, Status, Notification, User Profile */}
        <div className="flex items-center gap-3 sm:gap-4">
          {/* Search Bar with ⌘K Badge */}
          <div className="relative hidden sm:flex items-center">
            <div className="absolute left-3 pointer-events-none text-slate-400 flex items-center">
              <Search className="w-4 h-4" />
            </div>
            <input
              type="text"
              placeholder="Tìm kiếm đề thi..."
              className="w-48 lg:w-60 pl-9 pr-12 py-1.5 bg-white border border-slate-200 rounded-lg text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
            />
            <div className="absolute right-2.5 flex items-center pointer-events-none">
              <kbd className="px-1.5 py-0.5 text-[11px] font-medium text-slate-500 bg-slate-100 border border-slate-200 rounded font-mono">
                ⌘K
              </kbd>
            </div>
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

          {/* User Profile & System Status Indicator */}
          <div className="flex items-center gap-2.5">
            <BackendStatus />
            <UserAccountMenu />
          </div>
        </div>
      </div>
    </header>
  );
}
