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
    private static final String OTP_COOLDOWN_PREFIX = "otp_cooldown:";
    private static final String OTP_ATTEMPT_PREFIX = "otp_attempt:";
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration COOLDOWN_TTL = Duration.ofSeconds(60);
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Sinh mã OTP ngẫu nhiên 6 chữ số và lưu vào Redis kèm TTL.
     * Áp dụng cooldown 60s giữa các lần gửi lại mã.
     */
    public String generateAndSaveOtp(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        String normalizedEmail = email.trim().toLowerCase();
        String cooldownKey = OTP_COOLDOWN_PREFIX + normalizedEmail;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            Long remainingTtl = redisTemplate.getExpire(cooldownKey);
            LOG.warn("OTP cooldown vẫn còn hiệu lực cho email: {}, còn: {}s", normalizedEmail, remainingTtl);
            throw new IllegalStateException(
                "Vui lòng chờ %d giây trước khi yêu cầu mã OTP mới.".formatted(remainingTtl != null ? remainingTtl : 60)
            );
        }

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        String redisKey = OTP_PREFIX + normalizedEmail;
        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL);
        redisTemplate.opsForValue().set(cooldownKey, "1", COOLDOWN_TTL);
        redisTemplate.delete(OTP_ATTEMPT_PREFIX + normalizedEmail);

        return otp;
    }

    /**
     * So khớp mã OTP người dùng nhập vào từ Redis.
     * Tự động xóa OTP sau khi xác thực thành công (1 lần dùng).
     * Chặn brute-force sau 5 lần nhập sai.
     */
    public boolean validateOtp(String email, String inputOtp) {
        if (email == null || email.isBlank() || inputOtp == null || inputOtp.isBlank()) {
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
            LOG.warn("Quá {} lần nhập OTP sai cho email: {}, OTP đã bị hủy", MAX_VERIFY_ATTEMPTS, normalizedEmail);
            return false;
        }

        String cachedOtp = redisTemplate.opsForValue().get(redisKey);
        if (cachedOtp != null && cachedOtp.equals(inputOtp.trim())) {
            redisTemplate.delete(redisKey);
            redisTemplate.delete(attemptKey);
            redisTemplate.delete(OTP_COOLDOWN_PREFIX + normalizedEmail);
            return true;
        }
        return false;
    }

    /**
     * Xóa mã OTP thủ công (dùng cho cleanup khi cần)
     */
    public void deleteOtp(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        String normalizedEmail = email.trim().toLowerCase();
        redisTemplate.delete(OTP_PREFIX + normalizedEmail);
        redisTemplate.delete(OTP_COOLDOWN_PREFIX + normalizedEmail);
        redisTemplate.delete(OTP_ATTEMPT_PREFIX + normalizedEmail);
    }
}

