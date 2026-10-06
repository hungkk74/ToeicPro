'use client';

import Link from 'next/link';
import Image from 'next/image';
import {
  Timer,
  Flag,
  RotateCcw,
  LogOut,
  ArrowLeft,
  Award,
  BarChart3,
} from 'lucide-react';
import UserAccountMenu from '@/components/account/UserAccountMenu';

interface ExamHeaderProps {
  timeRemaining: number;
  formatTime: (sec: number) => string;
  currentQuestion: number;
  isFlagged: boolean;
  onToggleFlag: () => void;
  isSubmitting: boolean;
  onSubmit: () => void;
  onExit: () => void;
  onResetExam?: () => void;
  answeredCount: number;
  totalQuestions: number;
  isPlaying?: boolean;
  onTogglePlay?: () => void;
  playbackSpeed?: string;
  onChangeSpeed?: (speed: string) => void;
  isReviewMode?: boolean;
  onOpenScoreModal?: () => void;
  reviewScore?: number;
}

export default function ExamHeader({
  timeRemaining,
  formatTime,
  currentQuestion: _currentQuestion,
  isFlagged,
  onToggleFlag,
  isSubmitting,
  onSubmit,
  onExit,
  onResetExam,
  answeredCount,
  totalQuestions,
  isPlaying: _isPlaying,
  onTogglePlay: _onTogglePlay,
  playbackSpeed: _playbackSpeed,
  onChangeSpeed: _onChangeSpeed,
  isReviewMode = false,
  onOpenScoreModal,
  reviewScore,
}: ExamHeaderProps) {
  const isTimeCritical = timeRemaining > 0 && timeRemaining < 300; // < 5 mins

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-white/95 backdrop-blur-md border-b border-slate-200/90">
      <div className="h-14 px-4 sm:px-6 lg:px-8 max-w-[1440px] mx-auto flex items-center justify-between gap-3">
        {/* Logo & Subtitle */}
        <div className="flex items-center gap-3 shrink-0">
          <Link className="flex items-center gap-2 group" href="/">
            <div className="w-6 h-6 flex items-center justify-center shrink-0">
              <Image
                src="/logo.png"
                alt="ToeicPro Logo"
                width={24}
                height={24}
                className="w-full h-full object-contain"
                priority
              />
            </div>
            <span className="font-serif font-bold text-slate-950 text-base tracking-tight">
              TOEIC<span className="text-blue-600">Pro</span>
            </span>
          </Link>
          <div className="h-4 w-px bg-slate-200 hidden lg:block" />
          <div className="hidden lg:flex items-center">
            <span className="bg-slate-100 text-slate-700 px-2 py-0.5 rounded text-[11px] font-medium tracking-wide border border-slate-200/80 whitespace-nowrap">
              Phòng khảo thí trực tuyến
            </span>
          </div>
        </div>

        {/* Center Spacer */}
        <div className="flex-1 min-w-0" />

        {/* Right Toolbar */}
        <div className="flex items-center justify-end gap-2 shrink-0 flex-nowrap">
          {isReviewMode ? (
            <>
              {reviewScore !== undefined && (
                <div className="flex items-center gap-1.5 px-2.5 py-1 rounded bg-slate-100 text-slate-900 border border-slate-300 text-xs font-semibold shrink-0">
                  <Award className="w-3.5 h-3.5 text-slate-700" />
                  <span className="font-serif tabular-nums">{reviewScore} điểm</span>
                </div>
              )}

              {onOpenScoreModal && (
                <button
                  type="button"
                  onClick={onOpenScoreModal}
                  className="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-200 text-xs sm:text-sm font-medium transition-colors shrink-0 cursor-pointer"
                >
                  <BarChart3 className="w-3.5 h-3.5 text-slate-600" />
                  <span>Bảng điểm</span>
                </button>
              )}

              {onResetExam && (
                <button
                  className="flex items-center gap-1 px-2.5 sm:px-3 py-1.5 rounded-lg border border-slate-200 bg-white text-slate-700 hover:bg-slate-100 transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0"
                  type="button"
                  onClick={onResetExam}
                  title="Làm lại bài thi từ đầu"
                >
                  <RotateCcw className="w-3.5 h-3.5 text-slate-500" />
                  <span className="hidden md:inline">Thi lại</span>
                </button>
              )}

              <button
                className="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-blue-600 text-white hover:bg-blue-700 text-xs sm:text-sm font-medium transition-colors cursor-pointer shrink-0 shadow-xs"
                type="button"
                onClick={onExit}
                title="Về danh sách đề thi"
              >
                <ArrowLeft className="w-3.5 h-3.5" />
                <span>Danh sách đề</span>
              </button>
            </>
          ) : (
            <>
              <div
                className={`flex items-center gap-1.5 px-2.5 py-1 rounded-md border shrink-0 transition-colors ${
                  isTimeCritical
                    ? 'bg-amber-50 border-amber-300 text-amber-900'
                    : 'bg-slate-100 border-slate-200 text-slate-900'
                }`}
              >
                <Timer className={`w-3.5 h-3.5 ${isTimeCritical ? 'text-amber-700' : 'text-slate-600'}`} />
                <span className="font-mono text-xs sm:text-sm tabular-nums tracking-tight font-semibold">
                  {formatTime(timeRemaining)}
                </span>
              </div>

              {onResetExam && (
                <button
                  className="flex items-center gap-1 px-2.5 sm:px-3 py-1.5 rounded-lg border border-slate-200 bg-white text-slate-700 hover:bg-slate-100 transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0"
                  type="button"
                  onClick={onResetExam}
                  title="Làm lại bài thi từ đầu"
                >
                  <RotateCcw className="w-3.5 h-3.5 text-slate-500" />
                  <span className="hidden md:inline">Làm lại</span>
                </button>
              )}

              <button
                className="flex items-center gap-1 px-2.5 sm:px-3 py-1.5 rounded-lg border border-slate-200 bg-white text-slate-700 hover:bg-slate-100 hover:text-slate-950 transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0"
                type="button"
                onClick={onExit}
                title="Thoát phòng thi"
              >
                <LogOut className="w-3.5 h-3.5" />
                <span className="hidden md:inline">Thoát</span>
              </button>

              <button
                className={`flex items-center gap-1 px-2.5 sm:px-3 py-1.5 rounded-lg border transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0 ${
                  isFlagged
                    ? 'text-amber-900 bg-amber-50 border-amber-300 font-semibold'
                    : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'
                }`}
                type="button"
                onClick={onToggleFlag}
                title={isFlagged ? 'Đã đặt cờ (Click để bỏ)' : 'Đặt cờ câu này'}
              >
                <Flag className={`w-3.5 h-3.5 ${isFlagged ? 'text-amber-600 fill-amber-600' : 'text-slate-400'}`} />
                <span className="hidden md:inline">{isFlagged ? 'Đã đặt cờ' : 'Đặt cờ'}</span>
              </button>

              <button
                className="bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-800 text-xs sm:text-sm px-3.5 sm:px-4 py-1.5 rounded-lg transition-colors font-medium disabled:opacity-50 cursor-pointer shrink-0 shadow-xs"
                disabled={isSubmitting}
                type="button"
                onClick={onSubmit}
              >
                {isSubmitting ? 'Đang nộp...' : 'Nộp bài'}
              </button>
            </>
          )}

          <UserAccountMenu compact />
        </div>
      </div>

      {/* Subtle Progress Track */}
      <div className="w-full h-[2px] bg-slate-100 relative">
        <div
          className="absolute left-0 top-0 bottom-0 bg-blue-600 transition-all duration-300"
          style={{ width: `${totalQuestions > 0 ? (answeredCount / totalQuestions) * 100 : 0}%` }}
        />
      </div>
    </header>
  );
}
