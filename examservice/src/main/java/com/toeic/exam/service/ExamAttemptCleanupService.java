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


@Service
public class ExamAttemptCleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptCleanupService.class);
    private static final int BATCH_SIZE = 500;

    private final ExamAttemptRepository examAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;

    public ExamAttemptCleanupService(ExamAttemptRepository examAttemptRepository, UserAnswerRepository userAnswerRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cleanupAbandonedExamAttempts() {
        LOG.info("Starting cleanup of abandoned IN_PROGRESS ExamAttempts older than 24 hours...");
        Instant twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS);

        List<Long> abandonedIds = examAttemptRepository.findIdsByStatusAndStartedAtBefore(AttemptStatus.IN_PROGRESS, twentyFourHoursAgo);

        if (abandonedIds.isEmpty()) {
            LOG.info("No abandoned attempts found to clean up.");
            return;
        }

        LOG.info("Found {} abandoned attempts to delete.", abandonedIds.size());

        for (int i = 0; i < abandonedIds.size(); i += BATCH_SIZE) {
            List<Long> batch = abandonedIds.subList(i, Math.min(i + BATCH_SIZE, abandonedIds.size()));
            userAnswerRepository.deleteByExamAttemptIdIn(batch);
            examAttemptRepository.deleteAllByIdIn(batch);
        }

        LOG.info("Cleanup of abandoned ExamAttempts completed.");
    }
}
