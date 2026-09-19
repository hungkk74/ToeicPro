'use client';

import {
  LayoutDashboard,
  FileText,
  Users,
  GraduationCap,
  Server,
  ExternalLink,
  ShieldAlert,
  Sparkles,
} from 'lucide-react';

export type AdminTab = 'overview' | 'exams' | 'users' | 'courses' | 'services';

interface AdminSidebarProps {
  activeTab: AdminTab;
  onSelectTab: (tab: AdminTab) => void;
}

export default function AdminSidebar({ activeTab, onSelectTab }: AdminSidebarProps) {
  const navItems: { id: AdminTab; label: string; icon: React.ElementType; badge?: string }[] = [
    { id: 'overview', label: 'Tổng Quan Hệ Thống', icon: LayoutDashboard },
    { id: 'exams', label: 'Quản Lý Đề Thi', icon: FileText, badge: '5 Đề' },
    { id: 'users', label: 'Quản Lý Học Viên', icon: Users },
    { id: 'courses', label: 'Quản Lý Khóa Học', icon: GraduationCap, badge: '3 Khóa' },
    { id: 'services', label: 'Hạ Tầng Microservices', icon: Server, badge: '12 Live' },
  ];

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col shrink-0 min-h-[calc(100vh-4rem)] border-r border-slate-800">
      {/* Sidebar Section Title */}
      <div className="p-4 border-b border-slate-800/80">
        <div className="flex items-center gap-2 px-2 py-1 text-[11px] font-semibold tracking-wider text-slate-400 uppercase">
          <Sparkles className="w-3.5 h-3.5 text-blue-400" />
          <span>Bảng Điều Khiển Quản Trị</span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="p-3 space-y-1 flex-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              type="button"
              onClick={() => onSelectTab(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
                isActive
                  ? 'bg-blue-600 text-white shadow-sm'
                  : 'text-slate-300 hover:bg-slate-800 hover:text-white'
              }`}
            >
              <div className="flex items-center gap-3">
                <Icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span
                  className={`text-[10px] px-1.5 py-0.5 rounded font-medium ${
                    isActive
                      ? 'bg-blue-700/80 text-white'
                      : 'bg-slate-800 text-slate-400 border border-slate-700'
                  }`}
                >
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}

        <div className="pt-4 mt-4 border-t border-slate-800/80">
          <span className="px-3 text-[10px] font-semibold text-slate-500 uppercase tracking-wider block mb-2">
            Công Cụ Quản Trị Nâng Cao
          </span>
          <a
            href="http://localhost:9080/admin/master/console/#/jhipster"
            target="_blank"
            rel="noreferrer"
            className="w-full flex items-center justify-between px-3 py-2 text-xs font-medium text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl transition-colors cursor-pointer"
          >
            <div className="flex items-center gap-2.5">
              <ShieldAlert className="w-4 h-4 text-amber-400" />
              <span>Keycloak Admin Console</span>
            </div>
            <ExternalLink className="w-3.5 h-3.5 text-slate-500" />
          </a>
          <a
            href="http://localhost:8080"
            target="_blank"
            rel="noreferrer"
            className="w-full flex items-center justify-between px-3 py-2 text-xs font-medium text-slate-400 hover:bg-slate-800 hover:text-white rounded-xl transition-colors cursor-pointer mt-1"
          >
            <div className="flex items-center gap-2.5">
              <Server className="w-4 h-4 text-emerald-400" />
              <span>Gateway Technical UI (:8080)</span>
            </div>
            <ExternalLink className="w-3.5 h-3.5 text-slate-500" />
          </a>
        </div>
      </nav>

      {/* Footer info */}
      <div className="p-4 border-t border-slate-800/80 bg-slate-950/40 text-[11px] text-slate-500">
        <div className="flex items-center justify-between">
          <span>Phiên bản Backend</span>
          <span className="font-mono text-slate-400">Spring Boot 3.4</span>
        </div>
        <div className="flex items-center justify-between mt-1">
          <span>Kiến trúc</span>
          <span className="font-mono text-blue-400">Microservices</span>
        </div>
      </div>
    </aside>
  );
}
