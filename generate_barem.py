import os

# ETS standard TOEIC conversion table (Raw score 0-100 -> Listening, Reading)
scores = [
    (0, 5, 5), (1, 5, 5), (2, 5, 5), (3, 5, 5), (4, 5, 5),
    (5, 5, 5), (6, 5, 5), (7, 10, 5), (8, 15, 5), (9, 20, 5),
    (10, 25, 10), (11, 30, 15), (12, 35, 20), (13, 40, 25), (14, 45, 30),
    (15, 50, 35), (16, 55, 40), (17, 60, 45), (18, 65, 50), (19, 70, 55),
    (20, 75, 60), (21, 80, 65), (22, 85, 70), (23, 90, 75), (24, 95, 80),
    (25, 100, 85), (26, 110, 90), (27, 115, 95), (28, 120, 100), (29, 125, 105),
    (30, 130, 110), (31, 135, 115), (32, 140, 120), (33, 145, 125), (34, 150, 130),
    (35, 155, 135), (36, 160, 140), (37, 165, 145), (38, 170, 150), (39, 175, 155),
    (40, 180, 160), (41, 185, 165), (42, 190, 170), (43, 195, 175), (44, 200, 180),
    (45, 210, 185), (46, 215, 190), (47, 220, 195), (48, 230, 200), (49, 240, 210),
    (50, 245, 215), (51, 250, 220), (52, 255, 225), (53, 260, 230), (54, 265, 235),
    (55, 270, 240), (56, 275, 245), (57, 280, 250), (58, 285, 255), (59, 290, 260),
    (60, 295, 265), (61, 300, 270), (62, 305, 275), (63, 310, 280), (64, 320, 285),
    (65, 325, 290), (66, 330, 295), (67, 335, 300), (68, 340, 305), (69, 345, 310),
    (70, 355, 315), (71, 360, 320), (72, 365, 325), (73, 370, 330), (74, 375, 335),
    (75, 380, 340), (76, 385, 345), (77, 390, 350), (78, 395, 355), (79, 400, 360),
    (80, 405, 365), (81, 410, 370), (82, 415, 375), (83, 420, 380), (84, 425, 385),
    (85, 430, 390), (86, 435, 395), (87, 440, 400), (88, 445, 405), (89, 450, 410),
    (90, 455, 415), (91, 460, 420), (92, 465, 425), (93, 470, 430), (94, 475, 440),
    (95, 480, 450), (96, 485, 460), (97, 490, 470), (98, 495, 480), (99, 495, 490),
    (100, 495, 495)
]

sql_lines = [
    "USE `examservice`;",
    "",
    "CREATE TABLE IF NOT EXISTS `score_conversion` (",
    "    `id` INT AUTO_INCREMENT PRIMARY KEY,",
    "    `correct_count` INT NOT NULL UNIQUE,",
    "    `listening_score` INT NOT NULL,",
    "    `reading_score` INT NOT NULL",
    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;",
    "",
    "INSERT INTO `score_conversion` (`correct_count`, `listening_score`, `reading_score`) VALUES"
]

values = []
for cnt, lis, read in scores:
    values.append(f"({cnt}, {lis}, {read})")

sql_lines.append(",\n".join(values) + "\nON DUPLICATE KEY UPDATE `listening_score` = VALUES(`listening_score`), `reading_score` = VALUES(`reading_score`);")

with open(r"d:\Projects\Toeic_Pro\examservice_barem.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(sql_lines))

print("Created examservice_barem.sql successfully with 101 rows!")
