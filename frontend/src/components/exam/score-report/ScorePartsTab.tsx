'use client';

import { BarChart3 } from 'lucide-react';
import { ExamReviewDTO } from '@/types/backend';

interface ScorePartsTabProps {
  reviewData: ExamReviewDTO | null;
  loadingReview: boolean;
}

export function ScorePartsTab({ reviewData, loadingReview }: ScorePartsTabProps) {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-bold text-slate-800 flex items-center gap-1.5">
          <BarChart3 className="w-4 h-4 text-slate-700" />
          <span>Độ chính xác chi tiết theo từng Part</span>
        </h3>
        <span className="text-xs text-slate-500 font-medium">
          {reviewData?.partSummaries?.length || 0} phần thi
        </span>
      </div>

      {loadingReview ? (
        <div className="py-12 text-center text-xs text-slate-500">
          Đang tải dữ liệu phân tích từng Part...
        </div>
      ) : reviewData?.partSummaries && reviewData.partSummaries.length > 0 ? (
        <div className="space-y-2.5">
          {reviewData.partSummaries.map((part) => {
            const correct = part.correctCount ?? part.correctQuestions ?? 0;
            const total = part.totalCount ?? part.totalQuestions ?? 1;
            const percentage = Math.round((correct / total) * 100);

            return (
              <div
                key={part.partNumber}
                className="p-3.5 rounded-xl border border-slate-200 bg-white hover:border-slate-300 transition-colors space-y-2"
              >
                <div className="flex items-center justify-between text-xs sm:text-sm">
                  <div className="flex items-center gap-2">
                    <span className="w-6 h-6 rounded-md bg-slate-900 text-white font-bold text-xs flex items-center justify-center shrink-0">
                      {part.partNumber}
                    </span>
                    <span className="font-semibold text-slate-900">{part.partName}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-slate-600 text-xs font-medium tabular-nums">
                      {correct}/{total} câu đúng
                    </span>
                    <span className="font-bold text-slate-900 tabular-nums">{percentage}%</span>
                  </div>
                </div>

                {/* Accuracy progress bar */}
                <div className="w-full h-1.5 bg-slate-100 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${
                      percentage >= 80
                        ? 'bg-emerald-500'
                        : percentage >= 50
                        ? 'bg-blue-500'
                        : 'bg-amber-500'
                    }`}
                    style={{ width: `${percentage}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="py-12 text-center text-xs text-slate-500">
          Chưa có số liệu phân tích từng phần cho bài thi này.
        </div>
      )}
    </div>
  );
}
