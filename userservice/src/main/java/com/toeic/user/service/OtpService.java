package com.toeic.user.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final Duration OTP_TTL = Duration.ofMinutes(5); // Mã sống trong 5 phút

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Sinh mã OTP ngẫu nhiên 6 chữ số và lưu vào Redis kèm TTL
     */
    public String generateAndSaveOtp(String email) {
        // Sinh mã 6 chữ số an toàn và tối ưu tài nguyên (100000 -> 999999)
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        String redisKey = OTP_PREFIX + email.trim().toLowerCase();

        // Ghi đè mã mới nếu gửi lại và đặt thời gian sống là 5 phút
        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL);

        return otp;
    }

    /**
     * So khớp mã OTP người dùng nhập vào từ Redis
     */
    public boolean validateOtp(String email, String inputOtp) {
        if (email == null || inputOtp == null) {
            return false;
        }

        String redisKey = OTP_PREFIX + email.trim().toLowerCase();
        String cachedOtp = redisTemplate.opsForValue().get(redisKey);

        return cachedOtp != null && cachedOtp.equals(inputOtp.trim());
    }

    /**
     * Xóa mã OTP ngay sau khi xác thực thành công (tránh tái sử dụng mã)
     */
    public void deleteOtp(String email) {
        String redisKey = OTP_PREFIX + email.trim().toLowerCase();
        redisTemplate.delete(redisKey);
    }
}
