'use client';

import { useState, useEffect } from 'react';
import { LayoutGrid, X } from 'lucide-react';
import ExamBreadcrumb from '@/components/exam/ExamBreadcrumb';
import ExamDirections from '@/components/exam/ExamDirections';
import QuestionCard from '@/components/exam/QuestionCard';
import QuestionMatrix, { MatrixPartItem } from '@/components/exam/QuestionMatrix';
import { FlattenedQuestion, ReviewMapItem } from '@/types/examTaking';

interface ExamWorkspaceProps {
  examId: string;
  examTitle?: string;
  currentQData?: FlattenedQuestion;
  currentQuestion: number;
  totalQuestions: number;
  activeAudioUrl?: string;
  playbackSpeed: string;
  onChangeSpeed: (s: string) => void;
  currentOptions?: Array<{ key: string; text: string }>;
  isFlagged: boolean;
  onToggleFlag: () => void;
  selectedAnswer?: string;
  onSelectAnswer: (ans: string) => void;
  onPrevQuestion: () => void;
  onNextQuestion: () => void;
  canGoPrev: boolean;
  canGoNext: boolean;
  isReviewMode: boolean;
  correctOption?: string;
  explanation?: string;
  transcript?: string;
  answeredCount: number;
  flaggedCount: number;
  selectedAnswers: Record<number, string>;
  flaggedQuestions: Record<number, boolean>;
  onSelectQuestion: (num: number) => void;
  matrixParts?: MatrixPartItem[];
  reviewAnswers?: Record<number, ReviewMapItem>;
  correctCount: number;
  wrongCount: number;
  skippedCount: number;
}

export function ExamWorkspace({
  examId,
  examTitle,
  currentQData,
  currentQuestion,
  totalQuestions,
  activeAudioUrl,
  playbackSpeed,
  onChangeSpeed,
  currentOptions,
  isFlagged,
  onToggleFlag,
  selectedAnswer,
  onSelectAnswer,
  onPrevQuestion,
  onNextQuestion,
  canGoPrev,
  canGoNext,
  isReviewMode,
  correctOption,
  explanation,
  transcript,
  answeredCount,
  flaggedCount,
  selectedAnswers,
  flaggedQuestions,
  onSelectQuestion,
  matrixParts,
  reviewAnswers,
  correctCount,
  wrongCount,
  skippedCount,
}: ExamWorkspaceProps) {
  const [isMobileMatrixOpen, setIsMobileMatrixOpen] = useState(false);

  // Keyboard navigation shortcuts for exam taker
  useEffect(() => {
    if (isReviewMode) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      // Ignore shortcut combinations with Ctrl, Meta (Cmd), Alt
      if (e.ctrlKey || e.metaKey || e.altKey) return;

      // Do not trigger if user is in an input or dialog
      const target = e.target as HTMLElement | null;
      if (
        target &&
        (target.tagName === 'INPUT' ||
          target.tagName === 'TEXTAREA' ||
          target.isContentEditable)
      ) {
        return;
      }

      const key = e.key.toUpperCase();
      const availableKeys = currentOptions?.map((o) => o.key) || ['A', 'B', 'C', 'D'];

      let targetKey: string | null = null;
      if (['A', 'B', 'C', 'D'].includes(key)) {
        targetKey = key;
      } else if (key === '1') {
        targetKey = 'A';
      } else if (key === '2') {
        targetKey = 'B';
      } else if (key === '3') {
        targetKey = 'C';
      } else if (key === '4') {
        targetKey = 'D';
      }

      if (targetKey && availableKeys.includes(targetKey)) {
        e.preventDefault();
        onSelectAnswer(targetKey);
      } else if (e.key === 'ArrowLeft' || e.key === 'PageUp') {
        e.preventDefault();
        if (canGoPrev) onPrevQuestion();
      } else if (e.key === 'ArrowRight' || e.key === 'PageDown') {
        e.preventDefault();
        if (canGoNext) onNextQuestion();
      } else if (key === 'F') {
        e.preventDefault();
        onToggleFlag();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [
    isReviewMode,
    currentOptions,
    onSelectAnswer,
    canGoPrev,
    canGoNext,
    onPrevQuestion,
    onNextQuestion,
    onToggleFlag,
  ]);

  return (
    <div className="w-full max-w-[1440px] mx-auto px-4 sm:px-6 lg:px-8 py-4 sm:py-space-lg">
      <ExamBreadcrumb
        examId={examId}
        examTitle={examTitle}
        partName={currentQData?.partName}
        partNumber={currentQData?.partNumber}
      />

      <div className="flex flex-col lg:flex-row items-start gap-space-lg w-full">
        <section className="w-full lg:flex-1 min-w-0 flex flex-col gap-space-lg">
          <ExamDirections
            currentQuestion={currentQuestion}
            totalQuestions={totalQuestions}
            partNumber={currentQData?.partNumber}
            partName={currentQData?.partName}
            hasAudio={Boolean(activeAudioUrl)}
            audioUrl={activeAudioUrl}
            playbackSpeed={playbackSpeed}
            onChangeSpeed={onChangeSpeed}
          />

          <QuestionCard
            currentQuestion={currentQuestion}
            totalQuestions={totalQuestions}
            partNumber={currentQData?.partNumber}
            partName={currentQData?.partName}
            content={currentQData?.content}
            passageText={currentQData?.passageText}
            imageUrl={currentQData?.imageUrl || currentQData?.groupImageUrl}
            options={currentOptions}
            isFlagged={isFlagged}
            onToggleFlag={onToggleFlag}
            selectedAnswer={selectedAnswer}
            onSelectAnswer={onSelectAnswer}
            onPrevQuestion={onPrevQuestion}
            onNextQuestion={onNextQuestion}
            canGoPrev={canGoPrev}
            canGoNext={canGoNext}
            isReviewMode={isReviewMode}
            correctOption={correctOption}
            explanation={explanation}
            transcript={transcript}
          />
        </section>

        <QuestionMatrix
          currentQuestion={currentQuestion}
          totalQuestions={totalQuestions}
          answeredCount={answeredCount}
          flaggedCount={flaggedCount}
          selectedAnswers={selectedAnswers}
          flaggedQuestions={flaggedQuestions}
          onSelectQuestion={onSelectQuestion}
          parts={matrixParts}
          isReviewMode={isReviewMode}
          reviewAnswers={reviewAnswers}
          correctCount={correctCount}
          wrongCount={wrongCount}
          skippedCount={skippedCount}
        />
      </div>

      {/* Mobile Floating Button to open Question Matrix */}
      <div className="lg:hidden fixed bottom-5 right-5 z-40">
        <button
          type="button"
          onClick={() => setIsMobileMatrixOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white px-4 py-2.5 rounded-full shadow-lg flex items-center gap-2 text-xs font-semibold cursor-pointer border border-blue-400/40"
          title="Mở danh sách câu hỏi"
        >
          <LayoutGrid className="w-4 h-4" />
          <span>Danh sách câu ({answeredCount}/{totalQuestions})</span>
        </button>
      </div>

      {/* Mobile Question Matrix Drawer Modal */}
      {isMobileMatrixOpen && (
        <div
          className="fixed inset-0 z-50 lg:hidden flex flex-col justify-end bg-slate-950/40 backdrop-blur-sm animate-in fade-in duration-150"
          onClick={(e) => {
            if (e.target === e.currentTarget) setIsMobileMatrixOpen(false);
          }}
        >
          <div className="bg-white rounded-t-2xl max-h-[82vh] flex flex-col overflow-hidden border-t border-slate-200 shadow-xl animate-in slide-in-from-bottom duration-200">
            <div className="p-4 border-b border-slate-100 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <LayoutGrid className="w-4 h-4 text-blue-600" />
                <span className="font-semibold text-sm text-slate-900">
                  Danh sách câu hỏi ({answeredCount}/{totalQuestions})
                </span>
              </div>
              <button
                type="button"
                onClick={() => setIsMobileMatrixOpen(false)}
                className="p-1 rounded-lg hover:bg-slate-100 text-slate-500 cursor-pointer"
                aria-label="Đóng bảng câu hỏi"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="p-4 overflow-y-auto">
              <QuestionMatrix
                currentQuestion={currentQuestion}
                totalQuestions={totalQuestions}
                answeredCount={answeredCount}
                flaggedCount={flaggedCount}
                selectedAnswers={selectedAnswers}
                flaggedQuestions={flaggedQuestions}
                onSelectQuestion={(qNum) => {
                  onSelectQuestion(qNum);
                  setIsMobileMatrixOpen(false);
                }}
                parts={matrixParts}
                isReviewMode={isReviewMode}
                reviewAnswers={reviewAnswers}
                correctCount={correctCount}
                wrongCount={wrongCount}
                skippedCount={skippedCount}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
