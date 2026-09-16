
CREATE DATABASE IF NOT EXISTS userservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE userservice;

DROP TABLE IF EXISTS user_profile;

CREATE TABLE user_profile (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY ux_user_profile__user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO user_profile (id, user_id, name, email, phone, address) VALUES
(1, 'user1', 'Nguyen Van An (User 1)', 'user1@toeicpro.com', '0901000001', 'So 1 Pho Hue, Hoan Kiem, Ha Noi'),
(2, 'user2', 'Tran Thi Binh (User 2)', 'user2@toeicpro.com', '0901000002', 'So 2 Cau Giay, Cau Giay, Ha Noi'),
(3, 'user3', 'Le Hoang Cuong (User 3)', 'user3@toeicpro.com', '0901000003', 'So 3 Nguyen Hue, Quan 1, TP. Ho Chi Minh'),
(4, 'user4', 'Pham Hong Dung (User 4)', 'user4@toeicpro.com', '0901000004', 'So 4 Le Duan, Hai Chau, Da Nang'),
(5, 'user5', 'Hoang Minh Duc (User 5)', 'user5@toeicpro.com', '0901000005', 'So 5 Tran Phu, TP. Nha Trang, Khanh Hoa'),
(6, 'user6', 'Vu Thi Hanh (User 6)', 'user6@toeicpro.com', '0901000006', 'So 6 Quang Trung, TP. Vinh, Nghe An'),
(7, 'user7', 'Dang Tuan Kiet (User 7)', 'user7@toeicpro.com', '0901000007', 'So 7 Hung Vuong, TP. Hue, Thua Thien Hue'),
(8, 'user8', 'Bui Mai Lan (User 8)', 'user8@toeicpro.com', '0901000008', 'So 8 Ly Thuong Kiet, Ninh Kieu, Can Tho'),
(9, 'user9', 'Do Quoc Nam (User 9)', 'user9@toeicpro.com', '0901000009', 'So 9 Vo Van Ngan, TP. Thu Duc, TP. Ho Chi Minh'),
(10, 'user10', 'Ngo Phuong Thao (User 10)', 'user10@toeicpro.com', '0901000010', 'So 10 Nguyen Trai, Thanh Xuan, Ha Noi');
