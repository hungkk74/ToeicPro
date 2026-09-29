'use client';

import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser } from '@/services/authService';
import {
  fetchExamForTakingFromBackend,
  createExamAttemptInBackend,
  submitExamAttemptToBackend,
  cancelExamAttemptInBackend,
  fetchExamReviewFromBackend,
} from '@/services/examService';
import {
  ExamResultDTO,
  ExamReviewDTO,
  ExamTakeDTO,
  QuestionAnswerSubmissionDTO,
  UserAccountDTO,
} from '@/types/backend';
import { MatrixPartItem } from '@/components/exam/QuestionMatrix';
import { FlattenedQuestion, ReviewMapItem } from '@/types/examTaking';

export function useExamRunner(examId: string, reviewAttemptIdParam: string | null) {
  const router = useRouter();

  const [currentUser, setCurrentUser] = useState<UserAccountDTO | null>(null);
  const userScope = currentUser?.login ? `user_${currentUser.login}` : 'guest';
  const progressKey = `exam_progress_${userScope}_${examId}`;

  const [examData, setExamData] = useState<ExamTakeDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [backendAttemptId, setBackendAttemptId] = useState<number | null>(null);
  const pendingAttemptPromiseRef = useRef<Promise<{ id: number } | null> | null>(null);
  const [currentQuestion, setCurrentQuestion] = useState(1);
  const [selectedAnswers, setSelectedAnswers] = useState<Record<number, string>>({});
  const [flaggedQuestions, setFlaggedQuestions] = useState<Record<number, boolean>>({});
  const [timeRemaining, setTimeRemaining] = useState(4500); // 75 mins default
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [examResult, setExamResult] = useState<ExamResultDTO | null>(null);
  const [showResultModal, setShowResultModal] = useState(false);
  const [showExitDialog, setShowExitDialog] = useState(false);
  const [showResetDialog, setShowResetDialog] = useState(false);
  const [isReviewMode, setIsReviewMode] = useState(false);
  const [reviewDetails, setReviewDetails] = useState<ExamReviewDTO | null>(null);

  // Reset exam to start over
  const handleResetExam = useCallback(async () => {
    setIsReviewMode(false);
    setReviewDetails(null);
    setExamResult(null);
    setSelectedAnswers({});
    setFlaggedQuestions({});
    setCurrentQuestion(1);
    if (examData?.durationMinutes) {
      setTimeRemaining(examData.durationMinutes * 60);
    } else {
      setTimeRemaining(4500);
    }

    try {
      localStorage.removeItem(progressKey);
      localStorage.removeItem(`exam_progress_${examId}`);
    } catch {
      // noop
    }

    setBackendAttemptId(null);
    pendingAttemptPromiseRef.current = null;
    setShowResetDialog(false);
  }, [examData, progressKey, examId]);

  // Load exam data and restore saved progress
  useEffect(() => {
    let isMounted = true;
    async function initExam() {
      try {
        setLoading(true);

        // 1. Resolve current user identity
        const user = await getCurrentUser();
        if (!isMounted) return;
        setCurrentUser(user);

        const scope = user?.login ? `user_${user.login}` : 'guest';
        const activeKey = `exam_progress_${scope}_${examId}`;

        try {
          localStorage.removeItem(`exam_progress_${examId}`);
        } catch {
          // noop
        }

        const data = await fetchExamForTakingFromBackend(examId);
        if (!isMounted) return;

        if (data) {
          setExamData(data);

          // Review mode
          if (reviewAttemptIdParam) {
            const revId = Number(reviewAttemptIdParam);
            if (!isNaN(revId) && revId > 0) {
              try {
                const review = await fetchExamReviewFromBackend(revId);
                if (review && isMounted) {
                  if (review.canViewAnswers === false) {
                    alert('Bài thi này bạn hoàn thành dưới 80% số câu hỏi nên không thể xem lại chi tiết đáp án và lời giải.');
                    router.push('/de-thi');
                    return;
                  }
                  setReviewDetails(review);
                  setIsReviewMode(true);
                  const answers: Record<number, string> = {};
                  review.questions?.forEach((q) => {
                    if (q.selectedOption) {
                      answers[q.questionNumber] = q.selectedOption;
                    }
                  });
                  setSelectedAnswers(answers);
                  setExamResult({
                    attemptId: review.attemptId,
                    examId: review.examId,
                    examTitle: review.examTitle,
                    status: review.status,
                    listeningScore: review.listeningScore,
                    readingScore: review.readingScore,
                    totalScore: review.totalScore,
                    correctAnswers: review.correctAnswers,
                    wrongAnswers: review.wrongAnswers,
                    skippedAnswers: review.skippedAnswers,
                    timeSpentSeconds: review.timeSpentSeconds,
                    completedAt: review.completedAt,
                    canViewAnswers: review.canViewAnswers,
                  });

                  if (data.parts && data.parts.length > 0) {
                    const firstPart = data.parts[0];
                    const firstQ =
                      firstPart.standaloneQuestions?.[0]?.questionNumber ||
                      firstPart.groups?.[0]?.questions?.[0]?.questionNumber;
                    if (firstQ) setCurrentQuestion(firstQ);
                  }
                  return;
                }
              } catch (err) {
                console.warn('Could not load review from review query param:', err);
              }
            }
          }

          // Restore saved progress
          let restored = false;
          try {
            const saved = localStorage.getItem(activeKey);
            if (saved) {
              const progress = JSON.parse(saved);
              if (progress.savedAt && Date.now() - progress.savedAt < 24 * 60 * 60 * 1000) {
                if (progress.selectedAnswers) setSelectedAnswers(progress.selectedAnswers);
                if (progress.flaggedQuestions) setFlaggedQuestions(progress.flaggedQuestions);
                if (progress.currentQuestion) setCurrentQuestion(progress.currentQuestion);
                if (typeof progress.timeRemaining === 'number') setTimeRemaining(progress.timeRemaining);
                if (progress.backendAttemptId) setBackendAttemptId(progress.backendAttemptId);
                restored = true;
              } else {
                localStorage.removeItem(activeKey);
              }
            }
          } catch {
            // noop
          }

          if (!restored) {
            let firstQNum: number | null = null;
            if (data.parts && data.parts.length > 0) {
              for (const part of data.parts) {
                if (part.standaloneQuestions && part.standaloneQuestions.length > 0) {
                  firstQNum = part.standaloneQuestions[0].questionNumber;
                  break;
                }
                if (part.groups && part.groups.length > 0) {
                  for (const group of part.groups) {
                    if (group.questions && group.questions.length > 0) {
                      firstQNum = group.questions[0].questionNumber;
                      break;
                    }
                  }
                  if (firstQNum !== null) break;
                }
              }
            }

            if (firstQNum !== null) {
              setCurrentQuestion(firstQNum);
            }
            if (data.durationMinutes) {
              setTimeRemaining(data.durationMinutes * 60);
            }
          }
        }
      } catch (err) {
        console.warn('Failed to load exam details:', err);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    initExam();
    return () => {
      isMounted = false;
    };
  }, [examId, reviewAttemptIdParam, router]);

  // Flatten all questions
  const flattenedQuestions: FlattenedQuestion[] = useMemo(() => {
    if (!examData || !examData.parts) return [];
    const list: FlattenedQuestion[] = [];

    examData.parts.forEach((p) => {
      if (p.standaloneQuestions) {
        p.standaloneQuestions.forEach((q) => {
          list.push({
            id: q.id,
            questionNumber: q.questionNumber,
            content: q.content,
            imageUrl: q.imageUrl,
            audioUrl: q.audioUrl,
            optionA: q.optionA,
            optionB: q.optionB,
            optionC: q.optionC,
            optionD: q.optionD,
            partNumber: p.partNumber,
            partName: p.name,
          });
        });
      }
      if (p.groups) {
        p.groups.forEach((g) => {
          if (g.questions) {
            g.questions.forEach((q) => {
              list.push({
                id: q.id,
                questionNumber: q.questionNumber,
                content: q.content,
                imageUrl: q.imageUrl,
                audioUrl: q.audioUrl,
                optionA: q.optionA,
                optionB: q.optionB,
                optionC: q.optionC,
                optionD: q.optionD,
                partNumber: p.partNumber,
                partName: p.name,
                passageText: g.passageText,
                groupImageUrl: g.imageUrl,
                groupAudioUrl: g.audioUrl,
              });
            });
          }
        });
      }
    });

    return list.sort((a, b) => a.questionNumber - b.questionNumber);
  }, [examData]);

  // Question Matrix Parts
  const matrixParts: MatrixPartItem[] | undefined = useMemo(() => {
    if (!examData || !examData.parts || examData.parts.length === 0) return undefined;
    return examData.parts.map((p) => {
      const qNums: number[] = [];
      if (p.standaloneQuestions) {
        p.standaloneQuestions.forEach((q) => qNums.push(q.questionNumber));
      }
      if (p.groups) {
        p.groups.forEach((g) => {
          g.questions?.forEach((q) => qNums.push(q.questionNumber));
        });
      }
      qNums.sort((a, b) => a - b);
      return {
        partNumber: p.partNumber,
        name: p.name,
        questions: qNums,
      };
    });
  }, [examData]);

  // Countdown timer
  useEffect(() => {
    if (examResult || isReviewMode) return;
    const interval = setInterval(() => {
      setTimeRemaining((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [examResult, isReviewMode]);

  const formatTime = (seconds: number) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const toggleFlag = (qNum: number) => {
    if (isReviewMode) return;
    setFlaggedQuestions((prev) => ({ ...prev, [qNum]: !prev[qNum] }));
  };

  const handleSelectAnswer = (qNum: number, answer: string) => {
    if (isReviewMode || examResult) return;
    setSelectedAnswers((prev) => ({ ...prev, [qNum]: answer }));
    if (!backendAttemptId && !pendingAttemptPromiseRef.current) {
      pendingAttemptPromiseRef.current = createExamAttemptInBackend(Number(examId))
        .then((attempt) => {
          if (attempt?.id) {
            setBackendAttemptId(attempt.id);
          }
          return attempt;
        })
        .catch((err) => {
          console.warn('Failed to initialize exam attempt on first answer:', err);
          pendingAttemptPromiseRef.current = null;
          return null;
        });
    }
  };

  const handleSubmitExam = async () => {
    setIsSubmitting(true);
    const answersList: QuestionAnswerSubmissionDTO[] = Object.entries(selectedAnswers).map(
      ([qNum, ans]) => {
        const qItem = flattenedQuestions.find((q) => q.questionNumber === Number(qNum));
        return {
          questionId: qItem ? qItem.id : Number(qNum),
          selectedOption: ans as 'A' | 'B' | 'C' | 'D',
        };
      }
    );

    if (answersList.length === 0) {
      alert('Bạn chưa trả lời câu hỏi nào. Vui lòng chọn ít nhất một đáp án trước khi nộp bài!');
      setIsSubmitting(false);
      return;
    }

    try {
      let attemptIdToUse = backendAttemptId;
      if (!attemptIdToUse && pendingAttemptPromiseRef.current) {
        const inFlight = await pendingAttemptPromiseRef.current;
        if (inFlight?.id) {
          attemptIdToUse = inFlight.id;
          setBackendAttemptId(inFlight.id);
        }
      }
      if (!attemptIdToUse) {
        pendingAttemptPromiseRef.current = createExamAttemptInBackend(Number(examId));
        const newAttempt = await pendingAttemptPromiseRef.current;
        if (newAttempt?.id) {
          attemptIdToUse = newAttempt.id;
          setBackendAttemptId(newAttempt.id);
        }
      }

      if (!attemptIdToUse) {
        throw new Error('Không thể khởi tạo phiên làm bài thi. Vui lòng đăng nhập và thử lại!');
      }

      const totalDurationSec = examData?.durationMinutes ? examData.durationMinutes * 60 : 7200;
      const actualElapsedSeconds = Math.max(1, totalDurationSec - timeRemaining);

      const result = await submitExamAttemptToBackend(attemptIdToUse, {
        timeSpentSeconds: actualElapsedSeconds,
        answers: answersList,
      });

      setExamResult(result);
      setShowResultModal(true);

      try {
        const review = await fetchExamReviewFromBackend(result.attemptId);
        if (review) {
          setReviewDetails(review);
        }
      } catch (err) {
        console.warn('Could not prefetch review details:', err);
      }

      try {
        const latestResultItem = {
          attemptId: result.attemptId,
          examId: Number(examId),
          totalScore: result.totalScore,
          listeningScore: result.listeningScore,
          readingScore: result.readingScore,
          correctAnswers: result.correctAnswers,
          wrongAnswers: result.wrongAnswers,
          skippedAnswers: result.skippedAnswers,
          timeSpentSeconds: result.timeSpentSeconds,
          completedAt: new Date().toISOString(),
        };
        localStorage.setItem(`toeic_latest_attempt_${examId}`, JSON.stringify(latestResultItem));
        localStorage.setItem(`toeic_latest_attempt_${userScope}_${examId}`, JSON.stringify(latestResultItem));
      } catch {
        // noop
      }

      setBackendAttemptId(null);
      pendingAttemptPromiseRef.current = null;
      try {
        localStorage.removeItem(progressKey);
        localStorage.removeItem(`exam_progress_${examId}`);
      } catch {
        // noop
      }
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new Event('exam-progress-updated'));
        window.dispatchEvent(new Event('exam-history-updated'));
      }
    } catch (err: unknown) {
      console.error('Submit attempt to backend failed:', err);
      const message = err instanceof Error ? err.message : 'Lỗi kết nối máy chủ hoặc hệ thống. Vui lòng thử lại!';
      alert(`Nộp bài thi thất bại: ${message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const answeredCount = Object.keys(selectedAnswers).length;
  const flaggedCount = Object.values(flaggedQuestions).filter(Boolean).length;
  const totalQuestions = flattenedQuestions.length > 0 ? flattenedQuestions.length : (examData?.totalQuestions || 100);

  const saveProgress = useCallback(() => {
    if (examResult) return;
    try {
      const answered = Object.keys(selectedAnswers).length;
      const progress = {
        selectedAnswers,
        flaggedQuestions,
        currentQuestion,
        timeRemaining,
        backendAttemptId,
        totalQuestions,
        answeredCount: answered,
        savedAt: Date.now(),
      };
      localStorage.setItem(progressKey, JSON.stringify(progress));
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new Event('exam-progress-updated'));
      }
    } catch {
      // noop
    }
  }, [selectedAnswers, flaggedQuestions, currentQuestion, timeRemaining, backendAttemptId, progressKey, examResult, totalQuestions]);

  const handleExitWithoutSaving = useCallback(async () => {
    if (backendAttemptId) {
      try {
        await cancelExamAttemptInBackend(backendAttemptId);
      } catch (err) {
        console.warn('Failed to cancel backend attempt on exit without saving:', err);
      }
    }
    try {
      localStorage.removeItem(progressKey);
      localStorage.removeItem(`exam_progress_${examId}`);
    } catch {
      // noop
    }
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new Event('exam-progress-updated'));
    }
    router.push('/de-thi');
  }, [progressKey, examId, backendAttemptId, router]);

  const currentIndex = flattenedQuestions.findIndex((q) => q.questionNumber === currentQuestion);
  const currentQData = currentIndex >= 0 ? flattenedQuestions[currentIndex] : flattenedQuestions[0];
  const canGoPrev = currentIndex > 0;
  const canGoNext = flattenedQuestions.length > 0 && currentIndex < flattenedQuestions.length - 1;

  const goToPrevQuestion = () => {
    if (canGoPrev && currentIndex > 0) {
      setCurrentQuestion(flattenedQuestions[currentIndex - 1].questionNumber);
    }
  };

  const goToNextQuestion = () => {
    if (canGoNext && currentIndex >= 0) {
      setCurrentQuestion(flattenedQuestions[currentIndex + 1].questionNumber);
    } else if (currentIndex === -1 && flattenedQuestions.length > 0) {
      setCurrentQuestion(flattenedQuestions[0].questionNumber);
    }
  };

  const reviewMap: Record<number, ReviewMapItem> | undefined = useMemo(() => {
    if (!reviewDetails?.questions) return undefined;
    const map: Record<number, ReviewMapItem> = {};
    reviewDetails.questions.forEach((q) => {
      map[q.questionNumber] = {
        isCorrect: Boolean(q.isCorrect),
        selectedOption: q.selectedOption || undefined,
        correctOption: q.correctOption || '',
      };
    });
    return map;
  }, [reviewDetails]);

  const currentReviewItem = reviewDetails?.questions?.find(
    (q) => q.questionNumber === currentQuestion
  );

  const currentOptions = currentQData
    ? [
        { key: 'A', text: currentQData.optionA },
        { key: 'B', text: currentQData.optionB },
        { key: 'C', text: currentQData.optionC },
        { key: 'D', text: currentQData.optionD },
      ]
    : undefined;

  return {
    currentUser,
    userScope,
    progressKey,
    examData,
    loading,
    currentQuestion,
    setCurrentQuestion,
    selectedAnswers,
    flaggedQuestions,
    timeRemaining,
    formatTime,
    isSubmitting,
    examResult,
    showResultModal,
    setShowResultModal,
    showExitDialog,
    setShowExitDialog,
    showResetDialog,
    setShowResetDialog,
    isReviewMode,
    setIsReviewMode,
    reviewDetails,
    flattenedQuestions,
    matrixParts,
    currentIndex,
    currentQData,
    canGoPrev,
    canGoNext,
    currentOptions,
    reviewMap,
    currentReviewItem,
    answeredCount,
    flaggedCount,
    totalQuestions,
    toggleFlag,
    handleSelectAnswer,
    handleSubmitExam,
    handleResetExam,
    saveProgress,
    handleExitWithoutSaving,
    goToPrevQuestion,
    goToNextQuestion,
  };
}
