-- =============================================================================
-- Khởi tạo tự động 7 Database cho nền tảng TOEIC Pro
-- File này được MySQL mount vào /docker-entrypoint-initdb.d/ và chạy 1 lần
-- duy nhất khi container MySQL khởi tạo volume data lần đầu tiên.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS gateway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS userservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS subscriptionservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS paymentservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS examservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS courseservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notificationservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
