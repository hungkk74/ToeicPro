'use client';

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
    </div>
  );
}
