'use client';

import ScoreReportModal from '@/components/exam/ScoreReportModal';
import ExamExitDialog from '@/components/exam/ExamExitDialog';
import ExamResetDialog from '@/components/exam/ExamResetDialog';
import { ExamResultDTO } from '@/types/backend';

interface ExamModalsContainerProps {
  examResult: ExamResultDTO | null;
  showResultModal: boolean;
  onCloseResultModal: () => void;
  onViewDetailedReview?: () => void;
  showExitDialog: boolean;
  onCloseExitDialog: () => void;
  onSaveAndExit: () => void;
  onExitWithoutSaving: () => void;
  showResetDialog: boolean;
  onCloseResetDialog: () => void;
  onConfirmReset: () => void;
  timeRemaining: number;
  formatTime: (sec: number) => string;
  answeredCount: number;
  totalQuestions: number;
}

export function ExamModalsContainer({
  examResult,
  showResultModal,
  onCloseResultModal,
  onViewDetailedReview,
  showExitDialog,
  onCloseExitDialog,
  onSaveAndExit,
  onExitWithoutSaving,
  showResetDialog,
  onCloseResetDialog,
  onConfirmReset,
  timeRemaining,
  formatTime,
  answeredCount,
  totalQuestions,
}: ExamModalsContainerProps) {
  return (
    <>
      {examResult && (
        <ScoreReportModal
          isOpen={showResultModal}
          onClose={onCloseResultModal}
          examResult={examResult}
          timeRemaining={timeRemaining}
          formatTime={formatTime}
          answeredCount={answeredCount}
          totalQuestions={totalQuestions}
          onViewDetailedReview={onViewDetailedReview}
        />
      )}

      <ExamExitDialog
        isOpen={showExitDialog}
        onClose={onCloseExitDialog}
        onSaveAndExit={onSaveAndExit}
        onExitWithoutSaving={onExitWithoutSaving}
        answeredCount={answeredCount}
        totalQuestions={totalQuestions}
        timeRemaining={timeRemaining}
        formatTime={formatTime}
      />

      <ExamResetDialog
        isOpen={showResetDialog}
        onClose={onCloseResetDialog}
        onConfirmReset={onConfirmReset}
        answeredCount={answeredCount}
      />
    </>
  );
}
