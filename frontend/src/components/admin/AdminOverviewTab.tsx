'use client';

import {
  FileText,
  Users,
  Award,
  Activity,
  TrendingUp,
  Clock,
  ArrowUpRight,
  CheckCircle2,
} from 'lucide-react';
import { AdminTab } from './AdminSidebar';

interface AdminOverviewTabProps {
  onNavigateTab: (tab: AdminTab) => void;
  userCount: number;
}

export default function AdminOverviewTab({ onNavigateTab, userCount }: AdminOverviewTabProps) {
  const stats = [
    {
      title: 'Tổng Số Đề Thi',
      value: '5',
      change: '+2 đề mới tuần này',
      isPositive: true,
      icon: FileText,
      color: 'blue',
      tab: 'exams' as AdminTab,
    },
    {
      title: 'Học Viên Hệ Thống',
      value: `${userCount || 4}`,
      change: 'Đồng bộ từ Keycloak',
      isPositive: true,
      icon: Users,
      color: 'indigo',
      tab: 'users' as AdminTab,
    },
    {
      title: 'Lượt Thi Thử Hoàn Thành',
      value: '1,280',
      change: '+14% so với tháng trước',
      isPositive: true,
      icon: Award,
      color: 'amber',
      tab: 'exams' as AdminTab,
    },
    {
      title: 'Trạng Thái Hạ Tầng',
      value: '12 / 12',
      change: '100% Uptime hoạt động',
      isPositive: true,
      icon: Activity,
      color: 'emerald',
      tab: 'services' as AdminTab,
    },
  ];

  const scoreDistribution = [
    { range: '850 – 990 (Xuất sắc)', count: 245, percentage: 19, color: 'bg-emerald-500' },
    { range: '650 – 845 (Khá / Giỏi)', count: 520, percentage: 41, color: 'bg-blue-600' },
    { range: '450 – 645 (Trung bình)', count: 375, percentage: 29, color: 'bg-amber-500' },
    { range: '10 – 445 (Mất gốc)', count: 140, percentage: 11, color: 'bg-rose-500' },
  ];

  const recentAttempts = [
    {
      student: 'Nguyễn Kiều Hưng (hungnk)',
      exam: 'ETS TOEIC 2026 - Test 01',
      score: '920 / 990',
      listening: '475',
      reading: '445',
      time: '15 phút trước',
      status: 'Đã chấm điểm',
    },
    {
      student: 'Trần Văn Minh (user)',
      exam: 'ETS TOEIC 2024 - Test 03',
      score: '785 / 990',
      listening: '410',
      reading: '375',
      time: '1 giờ trước',
      status: 'Đã chấm điểm',
    },
    {
      student: 'Lê Hoàng Yến (test_beta2)',
      exam: 'Hacker TOEIC Actual Test 01',
      score: '640 / 990',
      listening: '335',
      reading: '305',
      time: '3 giờ trước',
      status: 'Đã chấm điểm',
    },
    {
      student: 'Học viên vãng lai',
      exam: 'ETS Mini Test 30 Phút',
      score: '850 / 990',
      listening: '430',
      reading: '420',
      time: '5 giờ trước',
      status: 'Đã chấm điểm',
    },
  ];

  return (
    <div className="space-y-6">
      {/* 4 KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((item) => {
          const Icon = item.icon;
          return (
            <div
              key={item.title}
              onClick={() => onNavigateTab(item.tab)}
              className="p-5 bg-white rounded-2xl border border-slate-200 shadow-2xs hover:border-blue-500 hover:shadow-md transition-all cursor-pointer group"
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500">{item.title}</span>
                <div className="w-9 h-9 rounded-xl bg-slate-100 flex items-center justify-center text-slate-700 group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors">
                  <Icon className="w-4 h-4" />
                </div>
              </div>
              <div className="mt-3 flex items-baseline gap-2">
                <span className="text-2xl font-bold text-slate-900 tracking-tight font-numeric-timer">
                  {item.value}
                </span>
              </div>
              <div className="mt-2 flex items-center justify-between text-xs">
                <span className="text-emerald-700 font-medium flex items-center gap-1">
                  <TrendingUp className="w-3.5 h-3.5" />
                  {item.change}
                </span>
                <span className="text-slate-400 group-hover:text-blue-600 group-hover:translate-x-0.5 transition-all">
                  <ArrowUpRight className="w-4 h-4" />
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Grid 2 Columns: Score Distribution & Microservices Health */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Phân bố điểm thi TOEIC */}
        <div className="lg:col-span-1 p-5 bg-white rounded-2xl border border-slate-200 shadow-2xs space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="font-bold text-slate-900 text-sm">Phân Bố Điểm Thi TOEIC</h3>
            <span className="text-[11px] font-semibold text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
              1,280 Bài Thi
            </span>
          </div>
          <div className="space-y-3 pt-1">
            {scoreDistribution.map((score) => (
              <div key={score.range} className="space-y-1">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-medium text-slate-700">{score.range}</span>
                  <span className="font-bold text-slate-900">{score.percentage}% ({score.count})</span>
                </div>
                <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div
                    className={`h-full ${score.color} rounded-full transition-all duration-500`}
                    style={{ width: `${score.percentage}%` }}
                  ></div>
                </div>
              </div>
            ))}
          </div>
          <div className="pt-2 border-t border-slate-100 text-xs text-slate-500 flex items-center justify-between">
            <span>Điểm trung bình hệ thống:</span>
            <span className="font-bold text-blue-600">685 / 990</span>
          </div>
        </div>

        {/* Lượt thi thử gần nhất */}
        <div className="lg:col-span-2 p-5 bg-white rounded-2xl border border-slate-200 shadow-2xs space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Clock className="w-4 h-4 text-blue-600" />
              <h3 className="font-bold text-slate-900 text-sm">Lượt Thi Thử Gần Nhất</h3>
            </div>
            <button
              type="button"
              onClick={() => onNavigateTab('exams')}
              className="text-xs font-semibold text-blue-600 hover:text-blue-700 cursor-pointer"
            >
              Xem tất cả đề thi &rarr;
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-100 text-slate-400 font-semibold uppercase tracking-wider text-[10px]">
                  <th className="pb-2.5">Học Viên</th>
                  <th className="pb-2.5">Tên Đề Thi</th>
                  <th className="pb-2.5">Tổng Điểm</th>
                  <th className="pb-2.5">Chi Tiết L/R</th>
                  <th className="pb-2.5">Thời Gian</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {recentAttempts.map((item, idx) => (
                  <tr key={idx} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-2.5 font-semibold text-slate-900">{item.student}</td>
                    <td className="py-2.5 text-slate-600 font-medium">{item.exam}</td>
                    <td className="py-2.5">
                      <span className="font-bold text-blue-600 bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
                        {item.score}
                      </span>
                    </td>
                    <td className="py-2.5 text-slate-500">
                      L: <span className="font-medium text-slate-800">{item.listening}</span> | R: <span className="font-medium text-slate-800">{item.reading}</span>
                    </td>
                    <td className="py-2.5 text-slate-400">{item.time}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Quick Action Shortcuts */}
      <div className="p-4 bg-linear-to-r from-blue-50 to-indigo-50 border border-blue-100 rounded-2xl flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center shrink-0 shadow-xs">
            <CheckCircle2 className="w-5 h-5" />
          </div>
          <div>
            <h4 className="font-bold text-slate-900 text-sm">Hệ Thống Đang Vận Hành Ổn Định</h4>
            <p className="text-xs text-slate-600 mt-0.5">
              Tất cả 7 microservices nghiệp vụ và 5 dịch vụ hạ tầng (Keycloak, Consul, Kafka, MySQL, Redis) sẵn sàng.
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2 shrink-0">
          <button
            type="button"
            onClick={() => onNavigateTab('users')}
            className="px-3 py-2 bg-white text-slate-700 hover:bg-slate-100 text-xs font-semibold rounded-lg border border-slate-200 transition-colors cursor-pointer"
          >
            Quản Lý Học Viên
          </button>
          <button
            type="button"
            onClick={() => onNavigateTab('services')}
            className="px-3 py-2 bg-blue-600 text-white hover:bg-blue-700 text-xs font-semibold rounded-lg transition-colors shadow-xs cursor-pointer"
          >
            Xem Hạ Tầng Microservices
          </button>
        </div>
      </div>
    </div>
  );
}
