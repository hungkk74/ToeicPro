package com.toeic.exam.repository;

import com.toeic.exam.domain.UserAnswer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository tối ưu hiệu năng cao cho batch insert UserAnswer.
 */
@Repository
public class UserAnswerBatchRepository {

    private static final String INSERT_SQL =
        "INSERT INTO user_answer (selected_option, is_correct, time_spent_seconds, exam_attempt_id, question_id) VALUES (?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public UserAnswerBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(Long attemptId, List<UserAnswer> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
            INSERT_SQL,
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    UserAnswer ua = userAnswers.get(i);
                    if (ua.getSelectedOption() != null) {
                        ps.setString(1, ua.getSelectedOption().name());
                    } else {
                        ps.setNull(1, Types.VARCHAR);
                    }
                    if (ua.getIsCorrect() != null) {
                        ps.setBoolean(2, ua.getIsCorrect());
                    } else {
                        ps.setNull(2, Types.BOOLEAN);
                    }
                    ps.setInt(3, ua.getTimeSpentSeconds() != null ? ua.getTimeSpentSeconds() : 0);
                    ps.setLong(4, attemptId);
                    ps.setLong(5, ua.getQuestion().getId());
                }

                @Override
                public int getBatchSize() {
                    return userAnswers.size();
                }
            }
        );
    }
}
