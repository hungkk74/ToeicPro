import { memo } from 'react';
import Link from 'next/link';
import { Clock, FileText, TrendingUp, ArrowRight, CheckCircle2, CircleDot } from 'lucide-react';
import { ExamItem } from '@/types/examList';

interface ExamCardProps {
  card: ExamItem;
}

function ExamCard({ card }: ExamCardProps) {
  // Quy chuẩn màu đơn sắc đồng bộ cho Badge/Tag danh mục & format
  const getTag1Class = () => 'bg-slate-100 text-slate-700 border-slate-200';
  const getTag2Class = () => 'bg-slate-100 text-slate-700 border-slate-200';

  // Tính phần trăm tiến độ đang làm
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

  // Visual Indicator trạng thái làm bài ở góc card
  const renderStatusBadge = () => {
    if (card.status === 'completed') {
      const scoreToDisplay = card.userScore ?? card.latestAttempt?.totalScore;
      return (
        <span
          title={`Đã nộp đề thi${scoreToDisplay !== undefined ? ` - Lần gần nhất: ${scoreToDisplay}/${maxScore} điểm` : ''}`}
          className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200/80 shrink-0"
        >
          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
          <span>{scoreToDisplay !== undefined ? `Đã nộp: ${scoreToDisplay}/${maxScore}` : 'Đã nộp'}</span>
        </span>
      );
    }

    if (card.status === 'in_progress') {
      return (
        <span
          title={`Đang làm dở (tiến trình lưu 24h)${card.userProgress ? ` - Tiến độ: ${card.userProgress} câu` : ''}`}
          className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200/80 shrink-0"
        >
          <Clock className="w-3.5 h-3.5 text-amber-600 shrink-0" />
          <span>{card.userProgress ? `Đang làm: ${card.userProgress}` : 'Đang làm'}</span>
        </span>
      );
    }

    // status === 'untaken'
    return (
      <span
        title="Chưa từng làm đề này"
        className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200 shrink-0"
      >
        <CircleDot className="w-3 h-3 text-slate-400 shrink-0" />
        <span>Chưa làm</span>
      </span>
    );
  };

  return (
    <article className="flex flex-col justify-between h-full bg-white rounded-xl border border-slate-100 shadow-sm hover:shadow-lg hover:border-blue-300 hover:-translate-y-1 transition-all duration-200 p-5 group">
      <div className="space-y-4">
        {/* Badges/Tags & Status Indicator Bar */}
        <div className="flex items-center justify-between gap-2 flex-wrap">
          <div className="flex items-center gap-1.5 flex-wrap">
            <span
              className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${getTag1Class()}`}
            >
              {card.tag1}
            </span>
            <span
              className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${getTag2Class()}`}
            >
              {card.tag2}
            </span>
          </div>

          {/* Badge trạng thái ở góc card */}
          {renderStatusBadge()}
        </div>

        {/* Title & Description với line-clamp (tối đa 2 dòng) kèm tooltip */}
        <div className="space-y-1.5">
          <h3
            title={card.title}
            className="font-bold text-slate-900 text-base sm:text-lg group-hover:text-blue-600 transition-colors line-clamp-2 leading-snug cursor-pointer"
          >
            {card.title}
          </h3>
          <p
            title={card.description}
            className="text-slate-500 text-sm line-clamp-2 leading-relaxed"
          >
            {card.description}
          </p>
        </div>

        {/* Metrics Grid */}
        <div className="grid grid-cols-2 gap-2 p-2.5 sm:p-3 rounded-lg bg-slate-50 border border-slate-100 text-xs">
          <div className="flex items-center gap-1.5 text-slate-600 min-w-0">
            <Clock className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="tabular-nums font-semibold text-slate-800 truncate">
              {card.durationMinutes} phút
            </span>
          </div>

          <div className="flex items-center gap-1.5 text-slate-600 min-w-0">
            <FileText className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="tabular-nums font-semibold text-slate-800 truncate">
              {card.totalQuestions} câu hỏi
            </span>
          </div>
        </div>

        {/* Dynamic Progress Indicator based on current state */}
        {card.status === 'in_progress' && (
          <div className="space-y-1.5 p-2.5 rounded-lg bg-amber-50/70 border border-amber-200/80 text-xs">
            <div className="flex items-center justify-between font-medium text-amber-900">
              <span className="flex items-center gap-1">
                <Clock className="w-3.5 h-3.5 text-amber-600" />
                <span>Tiến độ đang làm</span>
              </span>
              <span className="font-bold tabular-nums text-amber-800">{card.userProgress} câu</span>
            </div>
            <div className="w-full h-1.5 bg-amber-200/60 rounded-full overflow-hidden">
              <div
                className="h-full bg-amber-500 rounded-full transition-all duration-300"
                style={{ width: `${inProgressPercent}%` }}
              />
            </div>
            <div className="flex items-center justify-between text-[10px] text-amber-700/80">
              <span>{inProgressPercent}% hoàn thành</span>
              <span>Lưu trạng thái trong 24h</span>
            </div>
            {card.latestAttempt && (
              <div className="pt-1 mt-1 border-t border-amber-200/60 flex items-center justify-between text-[10px] text-amber-900/90 font-medium">
                <span className="flex items-center gap-1 text-slate-600">
                  <TrendingUp className="w-3 h-3 text-emerald-600" />
                  <span>Điểm lần gần nhất:</span>
                </span>
                <span className="font-bold text-emerald-700 tabular-nums">
                  {card.latestAttempt.totalScore}/{maxScore} điểm ({card.latestAttempt.correctAnswers ?? 0}/{card.totalQuestions} câu)
                </span>
              </div>
            )}
          </div>
        )}

        {card.status === 'completed' && (
          <div className="space-y-2 p-2.5 rounded-lg bg-emerald-50/70 border border-emerald-200/80 text-xs">
            <div className="flex items-center justify-between font-medium text-emerald-900">
              <span className="flex items-center gap-1.5">
                <TrendingUp className="w-4 h-4 text-emerald-600 shrink-0" />
                <span className="font-semibold">Kết quả lần gần nhất</span>
              </span>
              <span className="font-bold tabular-nums text-emerald-800">
                {card.userScore ?? card.latestAttempt?.totalScore ?? 0}/{maxScore} điểm
              </span>
            </div>

            {/* Hiển thị chi tiết điểm Nghe & Đọc nếu là Full Test */}
            {maxScore === 990 && card.latestAttempt?.listeningScore !== undefined && card.latestAttempt?.readingScore !== undefined && (
              <div className="flex items-center justify-between text-[11px] px-2 py-0.5 rounded bg-emerald-100/60 text-emerald-900 font-medium">
                <span>Nghe: <strong className="tabular-nums font-bold text-blue-700">{card.latestAttempt.listeningScore}</strong>/495</span>
                <span className="text-emerald-300">|</span>
                <span>Đọc: <strong className="tabular-nums font-bold text-emerald-700">{card.latestAttempt.readingScore}</strong>/495</span>
              </div>
            )}

            <div className="w-full h-1.5 bg-emerald-200/60 rounded-full overflow-hidden">
              <div
                className="h-full bg-emerald-500 rounded-full transition-all duration-300"
                style={{
                  width: `${
                    card.latestAttempt?.correctAnswers != null && card.totalQuestions > 0
                      ? Math.min(100, Math.round((card.latestAttempt.correctAnswers / card.totalQuestions) * 100))
                      : Math.min(100, Math.round(((card.userScore ?? card.latestAttempt?.totalScore ?? 0) / maxScore) * 100))
                  }%`,
                }}
              />
            </div>
            <div className="flex items-center justify-between text-[10px] text-emerald-800 font-medium">
              <span>
                Đúng: <strong className="tabular-nums font-bold text-emerald-900">{card.latestAttempt?.correctAnswers ?? 0}</strong>/{card.totalQuestions} câu
                {card.totalQuestions > 0 && (
                  <span className="text-emerald-600 ml-1">
                    ({Math.round(((card.latestAttempt?.correctAnswers ?? 0) / card.totalQuestions) * 100)}%)
                  </span>
                )}
              </span>
              {card.latestAttempt?.completedAt && (
                <span className="text-emerald-700/80">
                  Thi: {new Date(card.latestAttempt.completedAt).toLocaleDateString('vi-VN')}
                </span>
              )}
            </div>
          </div>
        )}

        {card.status === 'untaken' && (
          /* Question Breakdown Chips */
          <div className="flex items-center gap-2 pt-0.5 flex-wrap">
            <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/80">
              Nghe {card.listeningQuestions} câu
            </span>
            <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/80">
              Đọc {card.readingQuestions} câu
            </span>
          </div>
        )}
      </div>

      {/* Action Footer */}
      <div className="flex items-center gap-2 pt-4 mt-4 border-t border-slate-100">
        <Link
          href={`/exam/${card.id}`}
          className={`flex-1 min-w-0 text-white font-medium py-2.5 px-3 sm:px-4 rounded-lg shadow hover:shadow-md active:translate-y-0 text-center text-sm transition-all flex items-center justify-center gap-1.5 group/btn ${
            card.status === 'in_progress'
              ? 'bg-amber-600 hover:bg-amber-700 active:bg-amber-800'
              : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800'
          }`}
        >
          <span className="truncate">
            {card.status === 'completed'
              ? 'Làm lại đề'
              : card.status === 'in_progress'
              ? 'Tiếp tục thi'
              : 'Vào thi'}
          </span>
          <ArrowRight className="w-4 h-4 shrink-0 group-hover/btn:translate-x-0.5 transition-transform" />
        </Link>
        <Link
          href={
            card.status === 'completed' && card.latestAttempt?.attemptId
              ? `/exam/${card.id}?review=${card.latestAttempt.attemptId}`
              : `/exam/${card.id}`
          }
          className="shrink-0 px-3 py-2.5 border border-slate-200 hover:border-slate-300 hover:bg-slate-50 active:bg-slate-100 text-slate-700 font-medium rounded-lg text-center text-sm shadow-sm hover:shadow active:translate-y-0 transition-all cursor-pointer"
        >
          {card.status === 'completed' ? 'Xem lại' : 'Chi tiết'}
        </Link>
      </div>
    </article>
  );
}

export default memo(ExamCard);
