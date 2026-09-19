'use client';

import { RotateCcw, Filter } from 'lucide-react';
import { ExamCategory, FilterState } from '@/types/examList';

interface FilterSectionProps {
  filterState: FilterState;
  onFilterChange: (updates: Partial<FilterState>) => void;
  onResetFilters: () => void;
  totalFiltered: number;
  totalAll: number;
}

const CATEGORY_TABS: { key: ExamCategory; label: string }[] = [
  { key: 'all', label: 'Tất cả đề' },
  { key: 'full', label: 'Đề đầy đủ (200 câu)' },
  { key: 'mini', label: 'Rút gọn (50 câu)' },
  { key: 'listening', label: 'Nghe (Part 1–4)' },
  { key: 'reading', label: 'Đọc (Part 5–7)' },
];

export default function FilterSection({
  filterState,
  onFilterChange,
  onResetFilters,
  totalFiltered,
  totalAll: _totalAll,
}: FilterSectionProps) {
  // Kiểm tra xem người dùng có đang áp dụng bộ lọc tùy chỉnh nào không
  const isFilterActive =
    filterState.category !== 'all' ||
    filterState.source !== 'all' ||
    filterState.targetScore !== 'all' ||
    filterState.status !== 'all' ||
    filterState.sortBy !== 'recent' ||
    Boolean(filterState.searchQuery);

  return (
    <div className="bg-white rounded-xl p-4 sm:p-5 border border-slate-200 space-y-4">
      {/* Top Row: Segmented Control Tabs & Filter Stats / Reset */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-3 pb-1 border-b border-slate-100">
        {/* Segmented Control Tabs */}
        <div className="inline-flex p-1 bg-slate-100 rounded-lg flex-wrap gap-1 border border-slate-200">
          {CATEGORY_TABS.map((tab) => {
            const isActive = filterState.category === tab.key;
            return (
              <button
                key={tab.key}
                type="button"
                onClick={() => onFilterChange({ category: tab.key })}
                className={`px-3.5 py-1.5 rounded-md text-xs font-medium transition-colors ${
                  isActive
                    ? 'bg-blue-600 text-white shadow-none'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                {tab.label}
              </button>
            );
          })}
        </div>

        {/* Right Status: Counter & Reset Button */}
        <div className="flex items-center gap-3 text-xs text-slate-500 self-end lg:self-auto">
          <span>
            Hiển thị: <strong className="font-semibold text-slate-900">{totalFiltered}</strong> bộ đề
          </span>
          <span className="text-slate-300">|</span>
          <button
            type="button"
            onClick={onResetFilters}
            disabled={!isFilterActive}
            className={`inline-flex items-center gap-1.5 font-medium transition-colors ${
              isFilterActive
                ? 'text-blue-600 hover:text-blue-700 cursor-pointer'
                : 'text-slate-400 cursor-not-allowed opacity-60'
            }`}
          >
            <RotateCcw className={`w-3.5 h-3.5 ${isFilterActive ? 'text-blue-600' : 'text-slate-400'}`} />
            <span>Đặt lại bộ lọc</span>
          </button>
        </div>
      </div>

      {/* Bottom Row: Dropdown Selectors */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 pt-1">
        {/* Nguồn đề thi */}
        <div className="relative min-w-[180px]">
          <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
            Nguồn đề thi
          </label>
          <div className="relative">
            <select
              value={filterState.source}
              onChange={(e) => onFilterChange({ source: e.target.value })}
              className="w-full truncate bg-white border border-slate-200 text-slate-700 text-sm rounded-lg px-3 py-2 pr-8 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors cursor-pointer appearance-none"
            >
              <option value="all">Tất cả nguồn (ETS, Economy...)</option>
              <option value="ETS Authentic">ETS Authentic 2026</option>
              <option value="Economy">Economy Toeic</option>
              <option value="Hackers">Hackers Practice</option>
            </select>
            <Filter className="w-3.5 h-3.5 text-slate-400 absolute right-2.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          </div>
        </div>

        {/* Mục tiêu điểm */}
        <div className="relative min-w-[180px]">
          <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
            Mục tiêu điểm
          </label>
          <div className="relative">
            <select
              value={filterState.targetScore}
              onChange={(e) => onFilterChange({ targetScore: e.target.value })}
              className="w-full truncate bg-white border border-slate-200 text-slate-700 text-sm rounded-lg px-3 py-2 pr-8 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors cursor-pointer appearance-none"
            >
              <option value="all">Tất cả mục tiêu điểm</option>
              <option value="550+">Mục tiêu 550+ (Cơ bản)</option>
              <option value="750+">Mục tiêu 750+ (Khá giỏi)</option>
              <option value="850+">Mục tiêu 850+ (Chuyên sâu)</option>
              <option value="900+">Mục tiêu 900+ (Mastery)</option>
            </select>
            <Filter className="w-3.5 h-3.5 text-slate-400 absolute right-2.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          </div>
        </div>

        {/* Trạng thái làm bài */}
        <div className="relative min-w-[180px]">
          <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
            Trạng thái làm bài
          </label>
          <div className="relative">
            <select
              value={filterState.status}
              onChange={(e) => onFilterChange({ status: e.target.value })}
              className="w-full truncate bg-white border border-slate-200 text-slate-700 text-sm rounded-lg px-3 py-2 pr-8 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors cursor-pointer appearance-none"
            >
              <option value="all">Tất cả trạng thái</option>
              <option value="untaken">Đề chưa làm</option>
              <option value="completed">Đã hoàn thành / Xem lại</option>
            </select>
            <Filter className="w-3.5 h-3.5 text-slate-400 absolute right-2.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          </div>
        </div>

        {/* Sắp xếp */}
        <div className="relative min-w-[180px]">
          <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
            Sắp xếp theo
          </label>
          <div className="relative">
            <select
              value={filterState.sortBy}
              onChange={(e) => onFilterChange({ sortBy: e.target.value as FilterState['sortBy'] })}
              className="w-full truncate bg-white border border-slate-200 text-slate-700 text-sm rounded-lg px-3 py-2 pr-8 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors cursor-pointer appearance-none"
            >
              <option value="recent">Mới nhất</option>
              <option value="popular">Lượt thi nhiều nhất</option>
              <option value="hardest">Độ khó cao nhất</option>
            </select>
            <Filter className="w-3.5 h-3.5 text-slate-400 absolute right-2.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          </div>
        </div>
      </div>
    </div>
  );
}
