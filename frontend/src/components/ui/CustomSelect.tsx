'use client';

import { ChevronDown, Check } from 'lucide-react';

export interface DropdownOption {
  value: string;
  label: string;
}

interface CustomSelectProps {
  label?: string;
  value: string;
  options: DropdownOption[];
  isOpen: boolean;
  onToggle: () => void;
  onSelect: (val: string) => void;
}

export function CustomSelect({
  label,
  value,
  options,
  isOpen,
  onToggle,
  onSelect,
}: CustomSelectProps) {
  const selected = options.find((o) => o.value === value) || options[0];

  return (
    <div className="relative">
      {label && (
        <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
          {label}
        </label>
      )}
      <button
        type="button"
        onClick={onToggle}
        className={`w-full bg-white border text-left text-sm rounded-lg px-3 py-2 flex items-center justify-between transition-colors cursor-pointer ${
          isOpen
            ? 'border-blue-500 ring-2 ring-blue-500/15 text-slate-900'
            : 'border-slate-200 hover:border-slate-300 text-slate-700'
        }`}
      >
        <span className="truncate pr-2 font-medium">{selected.label}</span>
        <ChevronDown
          className={`w-4 h-4 text-slate-400 shrink-0 transition-transform duration-150 ${
            isOpen ? 'rotate-180 text-blue-600' : ''
          }`}
        />
      </button>

      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-1.5 bg-white border border-slate-200 rounded-xl shadow-lg py-1 z-40">
          {options.map((opt) => {
            const isSelected = opt.value === value;
            return (
              <button
                key={opt.value}
                type="button"
                onClick={() => onSelect(opt.value)}
                className={`w-full text-left px-3 py-2 text-xs flex items-center justify-between transition-colors cursor-pointer ${
                  isSelected
                    ? 'bg-blue-50 text-blue-700 font-semibold'
                    : 'text-slate-700 hover:bg-slate-50'
                }`}
              >
                <span className="truncate">{opt.label}</span>
                {isSelected && <Check className="w-3.5 h-3.5 text-blue-600 shrink-0 ml-1.5" />}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
