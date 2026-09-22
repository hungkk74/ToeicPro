# Thư mục lưu trữ file Audio nội bộ (Local / Dev)

Bạn có thể chép các file `.mp3` đề thi vào thư mục này:
Ví dụ:
- `ets2023_test1_full.mp3` (Toàn bộ 45 phút Listening Part 1 - Part 4)
- `q1.mp3`, `q2.mp3` (Từng câu Part 1, Part 2)
- `part3_c1.mp3` (Đoạn hội thoại Part 3)

Next.js sẽ phục vụ file này tại đường dẫn:
`/audio/<tên-file>.mp3`

Ví dụ cấu hình vào MySQL:
```sql
-- Cho toàn bài thi:
UPDATE examservice.exam 
SET audio_full_url = '/audio/ets2023_test1_full.mp3' 
WHERE id = 1;
```
