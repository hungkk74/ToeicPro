'use client';

import React from 'react';
import Link from 'next/link';
import { RefreshCw, ArrowLeft, Layers } from 'lucide-react';

interface Props {
  className?: string;
  showBackHome?: boolean;
}

export default function BackendUnavailableNotice({ className = '', showBackHome = false }: Props) {
  return (
    <div className={`w-full flex items-center justify-center py-16 px-4 font-sans ${className}`}>
      <div className="w-full max-w-md bg-white rounded-xl p-8 border border-slate-200/90 shadow-xs text-center space-y-5">
        <div className="w-12 h-12 rounded-xl bg-slate-100 border border-slate-200 text-slate-700 flex items-center justify-center mx-auto shadow-xs">
          <Layers className="w-5 h-5 stroke-[1.75]" />
        </div>

        <div className="space-y-1.5">
          <h2 className="text-base sm:text-lg font-serif font-semibold text-slate-950 tracking-tight">
            Đang đồng bộ dữ liệu đề thi
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 leading-relaxed max-w-xs mx-auto">
            Hệ thống đang chuẩn bị và đồng bộ đề thi chuẩn format ETS. Vui lòng tải lại hoặc quay lại sau giây lát.
          </p>
        </div>

        <div className="flex items-center justify-center gap-3 pt-2">
          <button
            type="button"
            onClick={() => window.location.reload()}
            className="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-medium rounded-lg shadow-xs transition-colors cursor-pointer"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Tải lại trang</span>
          </button>

          {showBackHome && (
            <Link
              href="/de-thi"
              className="inline-flex items-center gap-2 px-4 py-2 bg-white hover:bg-slate-50 text-slate-700 text-xs font-medium rounded-lg transition-colors border border-slate-200"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Kho đề thi</span>
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}
