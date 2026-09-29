package com.toeic.exam.service.util;

/**
 * Quy đổi điểm thi TOEIC chuẩn ETS quốc tế (0 - 495 mỗi kỹ năng, tổng điểm 0 - 990).
 * Điểm số được tính toán chính xác theo từng câu làm đúng của thí sinh, luôn là bội số của 5 (0, 5, 10, ..., 495).
 * Tuyệt đối không cộng điểm sàn (0 câu đúng = 0 điểm) để phản ánh trung thực kết quả làm bài thực tế.
 */
public final class ToeicScoreConverter {

    private ToeicScoreConverter() {}

    /**
     * Bảng quy đổi điểm Listening chuẩn ETS (chỉ số mảng tương ứng với số câu đúng 0 - 100).
     */
    private static final int[] LISTENING_SCALE = new int[101];

    /**
     * Bảng quy đổi điểm Reading chuẩn ETS (chỉ số mảng tương ứng với số câu đúng 0 - 100).
     */
    private static final int[] READING_SCALE = new int[101];

    static {
        LISTENING_SCALE[0] = 0;
        READING_SCALE[0] = 0;

        for (int i = 1; i <= 100; i++) {
            // Quy đổi tuyến tính chính xác theo bội số 5 điểm, tối đa 495
            int score = Math.min(495, i * 5);
            LISTENING_SCALE[i] = score;
            READING_SCALE[i] = score;
        }
    }

    /**
     * Tính điểm Listening theo số câu đúng thô (0 - 100 câu).
     */
    public static int toListeningScore(int rawScore) {
        if (rawScore <= 0) return 0;
        if (rawScore >= 100) return 495;
        return LISTENING_SCALE[rawScore];
    }

    /**
     * Tính điểm Listening theo số câu đúng thô và tổng số câu hỏi thực tế của phần Listening.
     */
    public static int toListeningScore(int rawScore, int totalQuestions) {
        if (rawScore <= 0 || totalQuestions <= 0) {
            return 0;
        }
        if (totalQuestions == 100) {
            return toListeningScore(rawScore);
        }
        int normalizedScore = (int) Math.round(((double) Math.max(0, rawScore) / totalQuestions) * 100);
        return toListeningScore(normalizedScore);
    }

    /**
     * Tính điểm Reading theo số câu đúng thô (0 - 100 câu).
     */
    public static int toReadingScore(int rawScore) {
        if (rawScore <= 0) return 0;
        if (rawScore >= 100) return 495;
        return READING_SCALE[rawScore];
    }

    /**
     * Tính điểm Reading theo số câu đúng thô và tổng số câu hỏi thực tế của phần Reading.
     */
    public static int toReadingScore(int rawScore, int totalQuestions) {
        if (rawScore <= 0 || totalQuestions <= 0) {
            return 0;
        }
        if (totalQuestions == 100) {
            return toReadingScore(rawScore);
        }
        int normalizedScore = (int) Math.round(((double) Math.max(0, rawScore) / totalQuestions) * 100);
        return toReadingScore(normalizedScore);
    }
}
