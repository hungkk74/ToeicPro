import { memo } from 'react';
import Link from 'next/link';
import { Clock, FileText, TrendingUp, ArrowRight, CheckCircle2, CircleDot } from 'lucide-react';
import { ExamItem } from '@/types/examList';

interface ExamCardProps {
  card: ExamItem;
}

function ExamCard({ card }: ExamCardProps) {
  const calculateInProgressPercent = () => {
    if (!card.userProgress) return 0;
    const parts = card.userProgress.split('/');
    if (parts.length === 2) {
      const done = Number(parts[0]);
      const total = Number(parts[1]);
      if (total > 0) return Math.min(100, Math.round((done / total) * 100));
    }
    return 0;
  };

  const inProgressPercent = calculateInProgressPercent();
  const maxScore = (card.category === 'reading' || card.category === 'listening') ? 495 : 990;

  const renderStatusBadge = () => {
    if (card.status === 'completed') {
      const scoreToDisplay = card.userScore ?? card.latestAttempt?.totalScore;
      return (
        <span
          title={`Đã hoàn thành${scoreToDisplay !== undefined ? ` - Điểm: ${scoreToDisplay}/${maxScore}` : ''}`}
          className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium bg-emerald-50 text-emerald-900 border border-emerald-200 shrink-0"
        >
          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-700 shrink-0" />
          <span>{scoreToDisplay !== undefined ? `${scoreToDisplay}/${maxScore} điểm` : 'Đã nộp'}</span>
        </span>
      );
    }

    if (card.status === 'in_progress') {
      return (
        <span
          title={`Đang làm dở${card.userProgress ? ` - Tiến độ: ${card.userProgress} câu` : ''}`}
          className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium bg-amber-50 text-amber-900 border border-amber-200 shrink-0"
        >
          <Clock className="w-3.5 h-3.5 text-amber-700 shrink-0" />
          <span>{card.userProgress ? `${card.userProgress} câu` : 'Đang làm'}</span>
        </span>
      );
    }

    return (
      <span
        title="Chưa từng làm đề này"
        className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200 shrink-0"
      >
        <CircleDot className="w-3 h-3 text-slate-400 shrink-0" />
        <span>Chưa làm</span>
      </span>
    );
  };

  return (
    <article className="flex flex-col justify-between h-full bg-white rounded-xl border border-slate-200/80 hover:border-slate-400 hover:-translate-y-0.5 transition-all duration-200 p-5 group shadow-xs">
      <div className="space-y-3.5">
        {/* Badges/Tags & Status Indicator Bar */}
        <div className="flex items-center justify-between gap-2 flex-wrap">
          <div className="flex items-center gap-1.5 flex-wrap">
            <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-700 border border-slate-200/70">
              {card.tag1}
            </span>
            <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-700 border border-slate-200/70">
              {card.tag2}
            </span>
          </div>

          {renderStatusBadge()}
        </div>

        {/* Title & Description with Serif */}
        <div className="space-y-1">
          <Link href={`/exam/${card.id}`}>
            <h3
              title={card.title}
              className="font-serif font-semibold text-slate-950 text-base sm:text-lg group-hover:text-blue-600 transition-colors line-clamp-2 leading-snug cursor-pointer"
            >
              {card.title}
            </h3>
          </Link>
          <p
            title={card.description}
            className="text-slate-600 text-xs sm:text-sm line-clamp-2 leading-relaxed"
          >
            {card.description}
          </p>
        </div>

        {/* Metrics Grid */}
        <div className="grid grid-cols-2 gap-2 p-2.5 rounded-lg bg-slate-50 border border-slate-200/70 text-xs">
          <div className="flex items-center gap-1.5 text-slate-700 min-w-0">
            <Clock className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="tabular-nums font-medium text-slate-800 truncate">
              {card.durationMinutes} phút
            </span>
          </div>

          <div className="flex items-center gap-1.5 text-slate-700 min-w-0">
            <FileText className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="tabular-nums font-medium text-slate-800 truncate">
              {card.totalQuestions} câu hỏi
            </span>
          </div>
        </div>

        {/* In-progress status display */}
        {card.status === 'in_progress' && (
          <div className="space-y-1.5 p-2.5 rounded-lg bg-amber-50/60 border border-amber-200/70 text-xs">
            <div className="flex items-center justify-between font-medium text-amber-900">
              <span className="flex items-center gap-1">
                <Clock className="w-3.5 h-3.5 text-amber-700" />
                <span>Tiến độ bài thi</span>
              </span>
              <span className="font-bold tabular-nums text-amber-800">{card.userProgress} câu</span>
            </div>
            <div className="w-full h-1.5 bg-amber-200/60 rounded-full overflow-hidden">
              <div
                className="h-full bg-amber-600 rounded-full transition-all duration-300"
                style={{ width: `${inProgressPercent}%` }}
              />
            </div>
            <div className="flex items-center justify-between text-[11px] text-amber-800/90">
              <span>{inProgressPercent}% hoàn thành</span>
              <span>Lưu tự động</span>
            </div>
          </div>
        )}

        {/* Completed status display */}
        {card.status === 'completed' && (
          <div className="space-y-2 p-2.5 rounded-lg bg-slate-50 border border-slate-200 text-xs">
            <div className="flex items-center justify-between font-medium text-slate-900">
              <span className="flex items-center gap-1.5">
                <TrendingUp className="w-4 h-4 text-emerald-700 shrink-0" />
                <span className="font-semibold">Điểm số</span>
              </span>
              <span className="font-serif font-bold text-sm tabular-nums text-slate-900">
                {card.userScore ?? card.latestAttempt?.totalScore ?? 0}/{maxScore} điểm
              </span>
            </div>

            {maxScore === 990 && card.latestAttempt?.listeningScore !== undefined && card.latestAttempt?.readingScore !== undefined && (
              <div className="flex items-center justify-between text-[11px] px-2 py-1 rounded bg-slate-100 text-slate-800 font-medium">
                <span>Nghe: <strong className="tabular-nums font-bold text-slate-950">{card.latestAttempt.listeningScore}</strong>/495</span>
                <span className="text-slate-300">|</span>
                <span>Đọc: <strong className="tabular-nums font-bold text-slate-950">{card.latestAttempt.readingScore}</strong>/495</span>
              </div>
            )}

            <div className="w-full h-1 bg-slate-200 rounded-full overflow-hidden">
              <div
                className="h-full bg-slate-800 rounded-full transition-all duration-300"
                style={{
                  width: `${
                    card.latestAttempt?.correctAnswers != null && card.totalQuestions > 0
                      ? Math.min(100, Math.round((card.latestAttempt.correctAnswers / card.totalQuestions) * 100))
                      : Math.min(100, Math.round(((card.userScore ?? card.latestAttempt?.totalScore ?? 0) / maxScore) * 100))
                  }%`,
                }}
              />
            </div>
            <div className="flex items-center justify-between text-[11px] text-slate-600 font-medium">
              <span>
                Đúng: <strong className="tabular-nums font-bold text-slate-900">{card.latestAttempt?.correctAnswers ?? 0}</strong>/{card.totalQuestions} câu
              </span>
              {card.latestAttempt?.completedAt && (
                <span className="tabular-nums text-slate-500 text-[10px]">
                  {new Date(card.latestAttempt.completedAt).toLocaleDateString('vi-VN')}
                </span>
              )}
            </div>
          </div>
        )}

        {card.status === 'untaken' && (
          <div className="flex items-center gap-2 pt-0.5 flex-wrap">
            <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/70">
              Listening: {card.listeningQuestions} câu
            </span>
            <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/70">
              Reading: {card.readingQuestions} câu
            </span>
          </div>
        )}
      </div>

      {/* Action Footer */}
      <div className="pt-3.5 mt-3.5 border-t border-slate-100">
        {card.status === 'untaken' ? (
          <Link
            href={`/exam/${card.id}`}
            className="w-full bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium py-2 px-3.5 rounded-lg text-center text-xs sm:text-sm transition-colors flex items-center justify-center gap-1.5 shadow-xs"
          >
            <span>Bắt đầu làm bài</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        ) : (
          <div className="flex items-center gap-2">
            <Link
              href={`/exam/${card.id}`}
              className={`flex-1 min-w-0 text-white font-medium py-2 px-3 rounded-lg text-center text-xs sm:text-sm transition-colors flex items-center justify-center gap-1.5 shadow-xs ${
                card.status === 'in_progress'
                  ? 'bg-amber-600 hover:bg-amber-700'
                  : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800'
              }`}
            >
              <span className="truncate">
                {card.status === 'completed' ? 'Thi lại' : 'Tiếp tục thi'}
              </span>
              <ArrowRight className="w-3.5 h-3.5 shrink-0" />
            </Link>
            {card.status === 'completed' && card.latestAttempt?.attemptId && (
              <Link
                href={`/exam/${card.id}?review=${card.latestAttempt.attemptId}`}
                className="shrink-0 px-3 py-2 border border-slate-300 hover:border-blue-600 hover:text-blue-600 hover:bg-blue-50/50 text-slate-800 font-medium rounded-lg text-center text-xs sm:text-sm transition-colors"
              >
                Xem lại
              </Link>
            )}
          </div>
        )}
      </div>
    </article>
  );
}

export default memo(ExamCard);
