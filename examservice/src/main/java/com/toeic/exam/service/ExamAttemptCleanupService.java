package com.toeic.exam.service;

import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.repository.UserAnswerRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

// Scheduled background cleanup of abandoned exam attempts
@Service
public class ExamAttemptCleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptCleanupService.class);
    private static final int BATCH_SIZE = 50;
    private static final int MAX_ROUNDS_PER_RUN = 20;

    private final ExamAttemptRepository examAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final TransactionTemplate transactionTemplate;

    public ExamAttemptCleanupService(
        ExamAttemptRepository examAttemptRepository,
        UserAnswerRepository userAnswerRepository,
        TransactionTemplate transactionTemplate
    ) {
        this.examAttemptRepository = examAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupAbandonedExamAttempts() {
        LOG.info("Starting cleanup of abandoned IN_PROGRESS ExamAttempts older than 24 hours...");
        Instant twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS);

        int totalDeleted = 0;
        for (int round = 0; round < MAX_ROUNDS_PER_RUN; round++) {
            List<Long> batch = transactionTemplate.execute(status ->
                examAttemptRepository.findIdsByStatusAndStartedAtBefore(
                    AttemptStatus.IN_PROGRESS,
                    twentyFourHoursAgo,
                    PageRequest.of(0, BATCH_SIZE)
                )
            );

            if (batch == null || batch.isEmpty()) {
                break;
            }

            transactionTemplate.executeWithoutResult(status -> {
                userAnswerRepository.deleteByExamAttemptIdIn(batch);
                examAttemptRepository.deleteAllByIdIn(batch);
            });

            totalDeleted += batch.size();
            if (batch.size() < BATCH_SIZE) {
                break;
            }
        }

        LOG.info("Cleanup completed. Total abandoned ExamAttempts deleted: {}", totalDeleted);
    }
}

