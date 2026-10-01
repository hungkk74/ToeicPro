'use client';

import { useState, useRef, useEffect } from 'react';
import { RotateCcw, ChevronDown, Check, Search } from 'lucide-react';
import { FilterState } from '@/types/examList';
import { CustomSelect } from '@/components/ui/CustomSelect';
import {
  CATEGORY_TABS,
  SORT_OPTIONS,
  SOURCE_OPTIONS,
  SCORE_OPTIONS,
  STATUS_OPTIONS,
} from '@/constants/examFilters';

interface FilterSectionProps {
  filterState: FilterState;
  onFilterChange: (updates: Partial<FilterState>) => void;
  onResetFilters: () => void;
  totalFiltered: number;
  totalAll: number;
}

export default function FilterSection({
  filterState,
  onFilterChange,
  onResetFilters,
  totalFiltered,
  totalAll: _totalAll,
}: FilterSectionProps) {
  const [openDropdown, setOpenDropdown] = useState<string | null>(null);
  const [searchValue, setSearchValue] = useState(filterState.searchQuery || '');
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setSearchValue(filterState.searchQuery || '');
  }, [filterState.searchQuery]);

  useEffect(() => {
    const timer = setTimeout(() => {
      if (searchValue !== filterState.searchQuery) {
        onFilterChange({ searchQuery: searchValue });
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [searchValue, onFilterChange, filterState.searchQuery]);

  const isFilterActive =
    filterState.category !== 'all' ||
    filterState.source !== 'all' ||
    filterState.targetScore !== 'all' ||
    filterState.status !== 'all' ||
    filterState.sortBy !== 'recent' ||
    Boolean(filterState.searchQuery);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpenDropdown(null);
      }
    };
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setOpenDropdown(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  const toggleDropdown = (name: string) => {
    setOpenDropdown((prev) => (prev === name ? null : name));
  };

  const selectedSort =
    SORT_OPTIONS.find((s) => s.value === filterState.sortBy) || SORT_OPTIONS[0];

  return (
    <div
      ref={containerRef}
      className="relative z-20 bg-white rounded-xl p-4 sm:p-5 border border-slate-200/80 shadow-sm space-y-4"
    >
      {/* Search Bar */}
      <div className="relative">
        <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
        <input
          type="text"
          placeholder="Tìm kiếm đề thi (VD: ETS 2026, Test 1)..."
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
          className="w-full bg-slate-50 hover:bg-white border border-slate-200 rounded-lg pl-10 pr-4 py-2.5 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
        />
      </div>

      {/* Top Row: Segmented Control Tabs + Counter + Sort */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-3 pb-1 border-b border-slate-100">
        <div className="inline-flex p-1 bg-slate-100 rounded-lg flex-wrap gap-1 border border-slate-200">
          {CATEGORY_TABS.map((tab) => {
            const isActive = filterState.category === tab.key;
            return (
              <button
                key={tab.key}
                type="button"
                onClick={() => onFilterChange({ category: tab.key })}
                className={`px-3.5 py-1.5 rounded-md text-xs font-medium transition-colors cursor-pointer ${
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

        <div className="flex items-center gap-3 self-end lg:self-auto">
          <span className="text-xs text-slate-500 font-medium">
            Tìm thấy{' '}
            <strong className="text-slate-900 font-semibold">{totalFiltered}</strong> đề thi
          </span>

          <div className="relative">
            <button
              type="button"
              onClick={() => toggleDropdown('sort')}
              className={`bg-white border text-xs font-medium rounded-lg px-2.5 py-1.5 flex items-center gap-1.5 transition-colors cursor-pointer ${
                openDropdown === 'sort'
                  ? 'border-blue-500 ring-2 ring-blue-500/15 text-slate-900'
                  : 'border-slate-200 hover:border-slate-300 text-slate-800'
              }`}
            >
              <span>{selectedSort.label}</span>
              <ChevronDown
                className={`w-3.5 h-3.5 text-slate-400 transition-transform duration-150 ${
                  openDropdown === 'sort' ? 'rotate-180 text-blue-600' : ''
                }`}
              />
            </button>

            {openDropdown === 'sort' && (
              <div className="absolute top-full right-0 mt-1.5 w-48 bg-white border border-slate-200 rounded-xl shadow-lg py-1 z-40">
                {SORT_OPTIONS.map((opt) => {
                  const isSelected = opt.value === filterState.sortBy;
                  return (
                    <button
                      key={opt.value}
                      type="button"
                      onClick={() => {
                        onFilterChange({ sortBy: opt.value });
                        setOpenDropdown(null);
                      }}
                      className={`w-full text-left px-3.5 py-2 text-sm flex items-center justify-between transition-colors cursor-pointer ${
                        isSelected
                          ? 'bg-blue-50 text-blue-700 font-semibold'
                          : 'text-slate-700 hover:bg-slate-50 hover:text-slate-900'
                      }`}
                    >
                      <span>{opt.label}</span>
                      {isSelected && <Check className="w-4 h-4 text-blue-600 shrink-0 ml-2" />}
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          <span className="text-slate-200 hidden sm:inline">|</span>
          <button
            type="button"
            onClick={onResetFilters}
            disabled={!isFilterActive}
            title={isFilterActive ? 'Đặt lại tất cả bộ lọc về mặc định' : 'Bộ lọc đang ở mặc định'}
            className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-medium border transition-all duration-150 ${
              isFilterActive
                ? 'bg-blue-50 border-blue-200 text-blue-600 hover:bg-blue-100 hover:border-blue-300 hover:text-blue-700 shadow-sm cursor-pointer active:scale-95 group'
                : 'bg-slate-50 border-slate-200/80 text-slate-400 cursor-not-allowed opacity-50'
            }`}
          >
            <RotateCcw
              className={`w-3.5 h-3.5 transition-transform duration-300 ${
                isFilterActive ? 'text-blue-600 group-hover:-rotate-90' : 'text-slate-400'
              }`}
            />
            <span className={isFilterActive ? 'font-semibold' : ''}>Đặt lại</span>
          </button>
        </div>
      </div>

      {/* Bottom Row: 3 Custom Dropdown Selectors */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3 pt-1 w-full">
        <div className="w-full">
          <CustomSelect
            label="Nguồn đề thi"
            value={filterState.source}
            options={SOURCE_OPTIONS}
            isOpen={openDropdown === 'source'}
            onToggle={() => toggleDropdown('source')}
            onSelect={(val) => {
              onFilterChange({ source: val });
              setOpenDropdown(null);
            }}
          />
        </div>

        <div className="w-full">
          <CustomSelect
            label="Mục tiêu điểm"
            value={filterState.targetScore}
            options={SCORE_OPTIONS}
            isOpen={openDropdown === 'targetScore'}
            onToggle={() => toggleDropdown('targetScore')}
            onSelect={(val) => {
              onFilterChange({ targetScore: val });
              setOpenDropdown(null);
            }}
          />
        </div>

        <div className="w-full sm:col-span-2 lg:col-span-1">
          <CustomSelect
            label="Trạng thái làm bài"
            value={filterState.status}
            options={STATUS_OPTIONS}
            isOpen={openDropdown === 'status'}
            onToggle={() => toggleDropdown('status')}
            onSelect={(val) => {
              onFilterChange({ status: val });
              setOpenDropdown(null);
            }}
          />
        </div>
      </div>
    </div>
  );
}
