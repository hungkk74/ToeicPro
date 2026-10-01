package com.toeic.user.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final Logger LOG = LoggerFactory.getLogger(OtpService.class);
    private static final String OTP_PREFIX = "otp:";
    private static final String OTP_ATTEMPT_PREFIX = "otp_attempt:";
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Sinh mã OTP ngẫu nhiên 6 chữ số và lưu vào Redis kèm TTL.
     * Từ chối nếu OTP trước đó chưa hết hạn (chống spam).
     */
    public String generateAndSaveOtp(String email) {
        String redisKey = OTP_PREFIX + email.trim().toLowerCase();

        String existingOtp = redisTemplate.opsForValue().get(redisKey);
        if (existingOtp != null) {
            Long remainingTtl = redisTemplate.getExpire(redisKey);
            LOG.warn("OTP vẫn còn hiệu lực cho email: {}, TTL còn: {}s", email, remainingTtl);
            throw new IllegalStateException(
                "Mã OTP trước đó vẫn còn hiệu lực. Vui lòng chờ hết hạn trước khi yêu cầu mã mới."
            );
        }

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL);
        redisTemplate.delete(OTP_ATTEMPT_PREFIX + email.trim().toLowerCase());

        return otp;
    }

    /**
     * So khớp mã OTP người dùng nhập vào từ Redis.
     * Tự động xóa OTP sau khi xác thực thành công (1 lần dùng).
     * Chặn brute-force sau 5 lần nhập sai.
     */
    public boolean validateOtp(String email, String inputOtp) {
        if (email == null || inputOtp == null) {
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        String redisKey = OTP_PREFIX + normalizedEmail;
        String attemptKey = OTP_ATTEMPT_PREFIX + normalizedEmail;

        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey, OTP_TTL);
        }
        if (attempts != null && attempts > MAX_VERIFY_ATTEMPTS) {
            redisTemplate.delete(redisKey);
            redisTemplate.delete(attemptKey);
            LOG.warn("Quá {} lần nhập OTP sai cho email: {}, OTP đã bị hủy", MAX_VERIFY_ATTEMPTS, normalizedEmail);
            return false;
        }

        String cachedOtp = redisTemplate.opsForValue().get(redisKey);
        if (cachedOtp != null && cachedOtp.equals(inputOtp.trim())) {
            redisTemplate.delete(redisKey);
            redisTemplate.delete(attemptKey);
            return true;
        }
        return false;
    }

    /**
     * Xóa mã OTP thủ công (dùng cho cleanup khi cần)
     */
    public void deleteOtp(String email) {
        String redisKey = OTP_PREFIX + email.trim().toLowerCase();
        redisTemplate.delete(redisKey);
    }
}

