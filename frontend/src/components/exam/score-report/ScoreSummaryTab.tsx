'use client';

import { Clock, Lock, ArrowRight } from 'lucide-react';
import { ExamResultDTO, ExamReviewDTO } from '@/types/backend';
import { CefrInfo } from './scoreReportUtils';

interface ScoreSummaryTabProps {
  examResult: ExamResultDTO;
  reviewData: ExamReviewDTO | null;
  maxTotalScore: number;
  hasListening: boolean;
  hasReading: boolean;
  cefr: CefrInfo;
  actualAnswered: number;
  resolvedTotal: number;
  accuracyPercentage: number;
  completionPercentage: number;
  correctAnswers: number;
  wrongAnswers: number;
  skippedAnswers: number;
  actualTimeSpentStr: string;
  canViewAnswers: boolean;
  requiredQuestionsToUnlock: number;
  onNavigateToParts: () => void;
}

export function ScoreSummaryTab({
  examResult,
  reviewData,
  maxTotalScore,
  hasListening,
  hasReading,
  cefr,
  actualAnswered,
  resolvedTotal,
  accuracyPercentage,
  completionPercentage,
  correctAnswers,
  wrongAnswers,
  skippedAnswers,
  actualTimeSpentStr,
  canViewAnswers,
  requiredQuestionsToUnlock,
  onNavigateToParts,
}: ScoreSummaryTabProps) {
  return (
    <div className="space-y-5">
      {/* Main Score Banner */}
      <div className="p-5 rounded-xl bg-slate-50 border border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="text-center sm:text-left space-y-1">
          <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-slate-200/80 text-slate-700">
            Tổng Điểm TOEIC
          </span>
          <div className="flex items-baseline justify-center sm:justify-start gap-1.5 pt-1">
            <span className="text-4xl sm:text-5xl font-extrabold text-slate-900 tabular-nums tracking-tight">
              {examResult.totalScore}
            </span>
            <span className="text-sm font-semibold text-slate-400">/ {maxTotalScore}</span>
          </div>
          <p className="text-xs text-slate-600 pt-0.5">
            Trình độ: <strong className="text-slate-900 font-semibold">{cefr.title}</strong>
          </p>
        </div>

        {/* Subscores: Listening & Reading */}
        <div className="w-full sm:w-auto flex items-center justify-center gap-6 border-t sm:border-t-0 sm:border-l border-slate-200 pt-3 sm:pt-0 sm:pl-6">
          {hasListening && (
            <div className="text-center px-2">
              <span className="text-xs text-slate-500 font-medium block">Listening</span>
              <span className="text-2xl sm:text-3xl font-bold text-slate-900 tabular-nums">
                {examResult.listeningScore}
              </span>
              <span className="text-[11px] text-slate-400 block">/ 495</span>
            </div>
          )}

          {hasListening && hasReading && <div className="w-px h-10 bg-slate-200" />}

          {hasReading && (
            <div className="text-center px-2">
              <span className="text-xs text-slate-500 font-medium block">Reading</span>
              <span className="text-2xl sm:text-3xl font-bold text-slate-900 tabular-nums">
                {examResult.readingScore}
              </span>
              <span className="text-[11px] text-slate-400 block">/ 495</span>
            </div>
          )}
        </div>
      </div>

      {/* Progress & Performance Metrics */}
      <div className="space-y-3 p-4 rounded-xl bg-slate-50 border border-slate-200">
        <div className="flex items-center justify-between text-xs sm:text-sm font-medium">
          <span className="text-slate-700">
            Tiến độ hoàn thành:{' '}
            <strong className="text-slate-900 font-bold">
              {actualAnswered}/{resolvedTotal} câu
            </strong>
          </span>
          <div className="flex items-center gap-3">
            <span className="text-slate-900 font-bold tabular-nums">
              Chính xác: {accuracyPercentage}%
            </span>
            <span className="text-slate-600 font-medium tabular-nums">
              {completionPercentage}% hoàn thành
            </span>
          </div>
        </div>

        {/* Multi-segmented Progress Track */}
        <div className="w-full h-2 bg-slate-200 rounded-full overflow-hidden flex">
          <div
            className="bg-emerald-500 h-full transition-all duration-500"
            style={{ width: `${(correctAnswers / resolvedTotal) * 100}%` }}
            title={`Đúng: ${correctAnswers} câu`}
          />
          <div
            className="bg-rose-500 h-full transition-all duration-500"
            style={{ width: `${(wrongAnswers / resolvedTotal) * 100}%` }}
            title={`Sai: ${wrongAnswers} câu`}
          />
          <div
            className="bg-slate-300 h-full transition-all duration-500"
            style={{ width: `${(skippedAnswers / resolvedTotal) * 100}%` }}
            title={`Chưa làm: ${skippedAnswers} câu`}
          />
        </div>

        {/* Metrics Pill Boxes */}
        <div className="grid grid-cols-4 gap-2 pt-1 text-center">
          <div className="py-2.5 px-2 rounded-lg bg-white border border-slate-200">
            <div className="flex items-center justify-center gap-1.5 text-slate-600 text-xs font-medium">
              <span className="w-2 h-2 rounded-full bg-emerald-500 shrink-0" />
              <span>Đúng</span>
            </div>
            <span className="text-base sm:text-lg font-bold text-slate-900 tabular-nums block mt-0.5">
              {correctAnswers}
            </span>
          </div>

          <div className="py-2.5 px-2 rounded-lg bg-white border border-slate-200">
            <div className="flex items-center justify-center gap-1.5 text-slate-600 text-xs font-medium">
              <span className="w-2 h-2 rounded-full bg-rose-500 shrink-0" />
              <span>Sai</span>
            </div>
            <span className="text-base sm:text-lg font-bold text-slate-900 tabular-nums block mt-0.5">
              {wrongAnswers}
            </span>
          </div>

          <div className="py-2.5 px-2 rounded-lg bg-white border border-slate-200">
            <div className="flex items-center justify-center gap-1.5 text-slate-600 text-xs font-medium">
              <span className="w-2 h-2 rounded-full bg-slate-300 shrink-0" />
              <span>Chưa làm</span>
            </div>
            <span className="text-base sm:text-lg font-bold text-slate-900 tabular-nums block mt-0.5">
              {skippedAnswers}
            </span>
          </div>

          <div className="py-2.5 px-2 rounded-lg bg-white border border-slate-200">
            <div className="flex items-center justify-center gap-1.5 text-slate-600 text-xs font-medium">
              <Clock className="w-3.5 h-3.5 text-slate-400 shrink-0" />
              <span>Thời gian</span>
            </div>
            <span className="text-xs sm:text-sm font-bold text-slate-900 tabular-nums block mt-1 truncate px-1">
              {actualTimeSpentStr}
            </span>
          </div>
        </div>
      </div>

      {/* Notice when < 80% */}
      {!canViewAnswers && (
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-700 flex items-start gap-3">
          <div className="w-6 h-6 rounded-md bg-slate-200/80 text-slate-700 flex items-center justify-center shrink-0 mt-0.5">
            <Lock className="w-3.5 h-3.5" />
          </div>
          <div className="space-y-0.5 flex-1 min-w-0">
            <strong className="font-semibold text-slate-900 block text-xs sm:text-sm">
              Chưa mở khóa đáp án và lời giải chi tiết
            </strong>
            <p className="text-slate-600 leading-relaxed text-[11px] sm:text-xs">
              Bạn đã hoàn thành <strong>{actualAnswered}/{resolvedTotal} câu ({completionPercentage}%)</strong>.
              Hệ thống yêu cầu làm từ <strong>80% bài thi trở lên (tối thiểu {requiredQuestionsToUnlock}/{resolvedTotal} câu)</strong> mới hiển thị đáp án đúng và lời giải chi tiết.
            </p>
          </div>
        </div>
      )}

      {/* Quick Part Overview Snippet if available */}
      {reviewData?.partSummaries && reviewData.partSummaries.length > 0 && (
        <div className="space-y-2 pt-1">
          <div className="flex items-center justify-between text-xs font-semibold text-slate-700">
            <span>Kết quả sơ bộ từng Part</span>
            <button
              type="button"
              onClick={onNavigateToParts}
              className="text-slate-700 hover:text-slate-900 text-xs font-semibold inline-flex items-center gap-0.5"
            >
              <span>Xem đầy đủ</span>
              <ArrowRight className="w-3 h-3" />
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            {reviewData.partSummaries.slice(0, 4).map((p) => {
              const correct = p.correctCount ?? p.correctQuestions ?? 0;
              const total = p.totalCount ?? p.totalQuestions ?? 1;
              const pct = Math.round((correct / total) * 100);
              return (
                <div
                  key={p.partNumber}
                  className="p-2.5 rounded-lg border border-slate-200 bg-white flex items-center justify-between text-xs"
                >
                  <span className="font-medium text-slate-800 truncate mr-2">{p.partName}</span>
                  <span className="tabular-nums font-bold text-slate-700 shrink-0">
                    {correct}/{total} ({pct}%)
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
