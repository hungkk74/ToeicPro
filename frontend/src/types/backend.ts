export interface ExamDTO {
  id: number;
  code: string;
  title: string;
  durationMinutes: number;
  totalQuestions: number;
  audioFullUrl?: string;
  parts?: PartTakeDTO[];
}

export interface QuestionTakeDTO {
  id: number;
  questionNumber: number;
  content?: string;
  imageUrl?: string;
  audioUrl?: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
}

export interface QuestionGroupTakeDTO {
  id: number;
  title?: string;
  passageText?: string;
  imageUrl?: string;
  audioUrl?: string;
  questions: QuestionTakeDTO[];
}

export interface PartTakeDTO {
  id: number;
  partNumber: number;
  name: string;
  totalQuestions: number;
  groups?: QuestionGroupTakeDTO[];
  standaloneQuestions?: QuestionTakeDTO[];
}

export interface ExamTakeDTO {
  id: number;
  code: string;
  title: string;
  durationMinutes: number;
  totalQuestions: number;
  audioFullUrl?: string;
  parts: PartTakeDTO[];
}

export interface CourseDTO {
  id: number;
  title: string;
  description?: string;
  price?: number;
  thumbnailUrl?: string;
}

export interface QuestionAnswerSubmissionDTO {
  questionId: number;
  selectedOption: 'A' | 'B' | 'C' | 'D';
  timeSpentSeconds?: number;
}

export interface ExamSubmissionDTO {
  timeSpentSeconds?: number;
  answers: QuestionAnswerSubmissionDTO[];
}

export interface ExamResultDTO {
  attemptId: number;
  examId?: number;
  examTitle?: string;
  status?: string;
  listeningScore: number;
  readingScore: number;
  totalScore: number;
  correctAnswers?: number;
  wrongAnswers?: number;
  skippedAnswers?: number;
  timeSpentSeconds?: number;
  completedAt?: string;
}

export interface QuestionReviewDTO {
  questionId: number;
  questionNumber: number;
  partNumber: number;
  content?: string;
  imageUrl?: string;
  audioUrl?: string;
  selectedOption?: string;
  correctOption: string;
  isCorrect: boolean;
  explanation?: string;
  transcript?: string;
}

export interface PartScoreSummaryDTO {
  partNumber: number;
  partName: string;
  correctQuestions: number;
  totalQuestions: number;
  accuracyPercentage: number;
}

export interface ExamReviewDTO {
  attemptId: number;
  examId?: number;
  examTitle?: string;
  userId?: string;
  status?: string;
  totalScore: number;
  listeningScore: number;
  readingScore: number;
  correctAnswers: number;
  wrongAnswers: number;
  skippedAnswers: number;
  timeSpentSeconds?: number;
  startedAt?: string;
  completedAt?: string;
  partSummaries?: PartScoreSummaryDTO[];
  questions?: QuestionReviewDTO[];
}

export interface UserAccountDTO {
  id?: string;
  login: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  imageUrl?: string;
  authorities: string[];
}
