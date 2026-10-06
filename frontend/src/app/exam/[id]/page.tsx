'use client';

import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { Loader2 } from 'lucide-react';
import ExamHeader from '@/components/exam/ExamHeader';
import ExamFixedAudioPlayer from '@/components/exam/ExamFixedAudioPlayer';
import { ExamWorkspace } from '@/components/exam/ExamWorkspace';
import { ExamModalsContainer } from '@/components/exam/ExamModalsContainer';
import { useExamRunner } from '@/hooks/useExamRunner';
import { useExamAudio } from '@/hooks/useExamAudio';
import BackendUnavailableNotice from '@/components/common/BackendUnavailableNotice';

export default function ExamRoomPage() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const examId = params?.id ? String(params.id) : '1';
  const reviewAttemptIdParam = searchParams.get('review');

  const runner = useExamRunner(examId, reviewAttemptIdParam);
  const audio = useExamAudio(runner.currentQData, runner.examData);

  if (runner.loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center gap-3 font-sans">
        <Loader2 className="w-8 h-8 text-blue-600 animate-spin" />
        <span className="text-sm font-medium text-slate-600">
          Đang tải đề thi và câu hỏi từ hệ thống...
        </span>
      </div>
    );
  }

  if (!runner.examData) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 font-sans">
        <BackendUnavailableNotice showBackHome={true} />
      </div>
    );
  }

  const handleCloseResultModal = () => {
    runner.setShowResultModal(false);
    if (
      runner.examResult?.canViewAnswers ??
      runner.answeredCount * 100 >= runner.totalQuestions * 80
    ) {
      runner.setIsReviewMode(true);
    } else {
      router.push('/de-thi');
    }
  };

  const canViewAnswers = Boolean(
    runner.examResult?.canViewAnswers ??
      runner.answeredCount * 100 >= runner.totalQuestions * 80
  );

  return (
    <div className="bg-slate-50 text-slate-900 antialiased min-h-screen selection:bg-blue-100 selection:text-blue-950 font-sans">
      <ExamHeader
        timeRemaining={runner.timeRemaining}
        formatTime={runner.formatTime}
        currentQuestion={runner.currentQuestion}
        isFlagged={Boolean(runner.flaggedQuestions[runner.currentQuestion])}
        onToggleFlag={() => runner.toggleFlag(runner.currentQuestion)}
        isSubmitting={runner.isSubmitting}
        onSubmit={runner.handleSubmitExam}
        onExit={() => {
          if (runner.isReviewMode) {
            router.push('/de-thi');
          } else {
            runner.setShowExitDialog(true);
          }
        }}
        onResetExam={() => runner.setShowResetDialog(true)}
        answeredCount={runner.answeredCount}
        totalQuestions={runner.totalQuestions}
        isPlaying={audio.isPlaying}
        onTogglePlay={audio.togglePlay}
        playbackSpeed={audio.playbackSpeed}
        onChangeSpeed={audio.setPlaybackSpeed}
        isReviewMode={runner.isReviewMode}
        onOpenScoreModal={() => runner.setShowResultModal(true)}
        reviewScore={runner.examResult?.totalScore ?? runner.reviewDetails?.totalScore}
      />

      {audio.isListeningPart && audio.activeAudioUrl && (
        <ExamFixedAudioPlayer
          audioUrl={audio.activeAudioUrl}
          partNumber={runner.currentQData?.partNumber}
          partName={runner.currentQData?.partName}
        />
      )}

      <main
        className={`w-full ${
          audio.isListeningPart && audio.activeAudioUrl ? 'pt-36 sm:pt-28' : 'pt-20'
        } bg-slate-50 min-h-screen`}
      >
        <ExamWorkspace
          examId={examId}
          examTitle={runner.examData?.title}
          currentQData={runner.currentQData}
          currentQuestion={runner.currentQuestion}
          totalQuestions={runner.totalQuestions}
          activeAudioUrl={audio.activeAudioUrl}
          playbackSpeed={audio.playbackSpeed}
          onChangeSpeed={audio.setPlaybackSpeed}
          currentOptions={runner.currentOptions}
          isFlagged={Boolean(runner.flaggedQuestions[runner.currentQuestion])}
          onToggleFlag={() => runner.toggleFlag(runner.currentQuestion)}
          selectedAnswer={runner.selectedAnswers[runner.currentQuestion]}
          onSelectAnswer={(ans) => runner.handleSelectAnswer(runner.currentQuestion, ans)}
          onPrevQuestion={runner.goToPrevQuestion}
          onNextQuestion={runner.goToNextQuestion}
          canGoPrev={runner.canGoPrev}
          canGoNext={runner.canGoNext}
          isReviewMode={runner.isReviewMode}
          correctOption={runner.currentReviewItem?.correctOption}
          explanation={runner.currentReviewItem?.explanation}
          transcript={runner.currentReviewItem?.transcript}
          answeredCount={runner.answeredCount}
          flaggedCount={runner.flaggedCount}
          selectedAnswers={runner.selectedAnswers}
          flaggedQuestions={runner.flaggedQuestions}
          onSelectQuestion={runner.setCurrentQuestion}
          matrixParts={runner.matrixParts}
          reviewAnswers={runner.reviewMap}
          correctCount={runner.examResult?.correctAnswers ?? runner.reviewDetails?.correctAnswers ?? 0}
          wrongCount={runner.examResult?.wrongAnswers ?? runner.reviewDetails?.wrongAnswers ?? 0}
          skippedCount={runner.examResult?.skippedAnswers ?? runner.reviewDetails?.skippedAnswers ?? 0}
        />
      </main>

      <ExamModalsContainer
        examResult={runner.examResult}
        showResultModal={runner.showResultModal}
        onCloseResultModal={handleCloseResultModal}
        onViewDetailedReview={
          canViewAnswers
            ? () => {
                runner.setShowResultModal(false);
                runner.setIsReviewMode(true);
              }
            : undefined
        }
        showExitDialog={runner.showExitDialog}
        onCloseExitDialog={() => runner.setShowExitDialog(false)}
        onSaveAndExit={() => {
          if (!runner.examResult) {
            runner.saveProgress();
          } else {
            try {
              localStorage.removeItem(runner.progressKey);
              localStorage.removeItem(`exam_progress_${examId}`);
            } catch {
              // noop
            }
          }
          router.push('/de-thi');
        }}
        onExitWithoutSaving={runner.handleExitWithoutSaving}
        showResetDialog={runner.showResetDialog}
        onCloseResetDialog={() => runner.setShowResetDialog(false)}
        onConfirmReset={runner.handleResetExam}
        timeRemaining={runner.timeRemaining}
        formatTime={runner.formatTime}
        answeredCount={runner.answeredCount}
        totalQuestions={runner.totalQuestions}
      />
    </div>
  );
}
