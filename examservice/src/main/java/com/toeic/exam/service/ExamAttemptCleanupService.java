package com.toeic.exam.service;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.repository.UserAnswerRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for cleaning up old and abandoned exam attempts to prevent database bloat.
 */
@Service
public class ExamAttemptCleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptCleanupService.class);

    private final ExamAttemptRepository examAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;

    public ExamAttemptCleanupService(ExamAttemptRepository examAttemptRepository, UserAnswerRepository userAnswerRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    /**
     * Chạy định kỳ mỗi đêm lúc 2:00 sáng.
     * Xoá các lượt làm bài (ExamAttempt) đang làm dở (IN_PROGRESS) quá 7 ngày.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupAbandonedExamAttempts() {
        LOG.info("Starting cleanup of abandoned IN_PROGRESS ExamAttempts...");
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        // Lấy danh sách các bài thi dở dang quá hạn
        List<ExamAttempt> abandonedAttempts = examAttemptRepository.findByStatusAndStartedAtBefore(AttemptStatus.IN_PROGRESS, sevenDaysAgo);

        if (abandonedAttempts.isEmpty()) {
            LOG.info("No abandoned attempts found to clean up.");
            return;
        }

        LOG.info("Found {} abandoned attempts to delete.", abandonedAttempts.size());

        for (ExamAttempt attempt : abandonedAttempts) {
            // Xoá UserAnswers trước (để không bị lỗi khoá ngoại)
            userAnswerRepository.deleteByExamAttemptId(attempt.getId());
            // Xoá ExamAttempt
            examAttemptRepository.delete(attempt);
        }

        LOG.info("Cleanup of abandoned ExamAttempts completed.");
    }
}
