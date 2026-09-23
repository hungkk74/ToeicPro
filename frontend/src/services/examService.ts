import { fetchApi } from '@/lib/api-client';
import {
  ExamDTO,
  ExamTakeDTO,
  ExamSubmissionDTO,
  ExamResultDTO,
  ExamReviewDTO,
} from '@/types/backend';

/**
 * Lấy danh sách đề thi từ examservice qua Gateway (/api/exams)
 */
export async function fetchExamsFromBackend(): Promise<ExamDTO[]> {
  try {
    const data = await fetchApi<ExamDTO[]>('/api/exams?page=0&size=20', { skipAuth: true });
    if (Array.isArray(data) && data.length > 0) {
      return data;
    }
  } catch (err) {
    console.warn('Backend /api/exams unavailable or empty, fallback catalog active:', err);
  }
  return [];
}

/**
 * Tải đề thi đầy đủ câu hỏi để làm bài (/api/exams/:id/take)
 */
export async function fetchExamForTakingFromBackend(id: string | number): Promise<ExamTakeDTO | null> {
  try {
    const data = await fetchApi<ExamTakeDTO>(`/api/exams/${id}/take`, { skipAuth: true });
    if (data && data.parts && data.parts.length > 0) {
      return data;
    }
  } catch (err) {
    console.warn(`Backend /api/exams/${id}/take error, fallback simulator active:`, err);
  }
  return null;
}

/**
 * Khởi tạo lượt thi ExamAttempt trong DB (/api/exam-attempts)
 */
export async function createExamAttemptInBackend(
  examId: number,
  token?: string
): Promise<{ id: number } | null> {
  try {
    const res = await fetchApi<{ id: number }>(
      '/api/exam-attempts',
      {
        method: 'POST',
        body: JSON.stringify({ exam: { id: examId } }),
      },
      token
    );
    return res;
  } catch (err) {
    console.warn('Backend /api/exam-attempts initialization skipped (offline/guest mode):', err);
    return null;
  }
}

/**
 * Nộp bài thi và chấm điểm chuẩn ETS qua ToeicScoreConverter (/api/exam-attempts/:id/submit)
 */
export async function submitExamAttemptToBackend(
  attemptId: number,
  submission: ExamSubmissionDTO,
  token?: string
): Promise<ExamResultDTO> {
  return await fetchApi<ExamResultDTO>(
    `/api/exam-attempts/${attemptId}/submit`,
    {
      method: 'POST',
      body: JSON.stringify(submission),
    },
    token
  );
}

/**
 * Lấy chi tiết bài thi đã chấm và giải thích từng câu (/api/exam-attempts/:id/review)
 */
export async function fetchExamReviewFromBackend(
  attemptId: number,
  token?: string
): Promise<ExamReviewDTO | null> {
  try {
    return await fetchApi<ExamReviewDTO>(`/api/exam-attempts/${attemptId}/review`, {}, token);
  } catch (err) {
    console.warn(`Backend /api/exam-attempts/${attemptId}/review unavailable:`, err);
    return null;
  }
}

export interface MyExamHistoryItem {
  attemptId: number;
  examId: number;
  examTitle: string;
  totalScore: number;
  completedAt: string;
}

/**
 * Lấy lịch sử làm bài và điểm số của tài khoản đang đăng nhập (/api/exam-attempts/my-history)
 */
export async function fetchMyExamHistory(): Promise<MyExamHistoryItem[]> {
  try {
    const data = await fetchApi<MyExamHistoryItem[]>('/api/exam-attempts/my-history');
    if (Array.isArray(data)) {
      return data;
    }
  } catch {
    // Chưa đăng nhập hoặc offline
  }
  return [];
}

/**
 * Xoá đề thi khỏi hệ thống backend (/api/exams/:id)
 */
export async function deleteExamInBackend(id: number, token?: string): Promise<void> {
  await fetchApi(`/api/exams/${id}`, { method: 'DELETE' }, token);
}
