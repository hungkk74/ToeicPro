# Unified Design System: TOEIC Prep & Course Platform

## 1. Global Tokens (Dùng chung toàn bộ hệ thống)
- Typography: Primary font `Inter`, fallback `system-ui, -apple-system, sans-serif`.
- Color System:
    - Neutral: Slate scale (Slate-50 `#F8FAFC` to Slate-900 `#0F172A`).
    - Brand/Primary: `#2563EB` (Royal Blue) - tạo cảm giác tin cậy, công nghệ.
    - Success (Đúng/Đã làm): `#10B981` (Emerald).
    - Error (Sai/Cảnh báo): `#EF4444` (Rose/Red).
    - Highlight (Câu gắn cờ Flagged): `#F59E0B` (Amber).
- Elevation: Đường viền mảnh `border border-slate-200 dark:border-slate-800` thay vì dùng đổ bóng (shadow) đậm.

---

## 2. Context A: Marketing & Course Commerce (Stripe Style)
Áp dụng cho: Trang chủ, Trang chi tiết khóa học, Bảng giá, Giỏ hàng/Checkout.

- Tinh thần: Thoáng đãng, đáng tin cậy, phân cấp thông tin rõ ràng, thúc đẩy chuyển đổi.
- Bố cục & Khoảng cách:
    - Spacing rộng rãi (`gap-8`, `py-16` đến `py-24`).
    - Card khóa học dùng nền trắng `#FFFFFF`, viền mờ `border-slate-200/80`, hover nhẹ nhàng (`transition-all duration-200 hover:-translate-y-0.5`).
    - Bảng so sánh tính năng và lộ trình học dùng cấu trúc Bento grid cân đối.
- Buttons & CTA:
    - Nút chính (Mua khóa học, Đăng ký): Nền đậm (`bg-slate-900 text-white` hoặc `bg-blue-600 text-white`), bo góc chuẩn (`rounded-lg`), font-weight vừa phải (`font-medium`).
    - Huy hiệu (Badges): Nhỏ gọn, nền pastel (`bg-blue-50 text-blue-700 text-xs px-2.5 py-1`).

---

## 3. Context B: Exam Room & Testing Engine (Linear Style)
Áp dụng cho: Giao diện làm đề thi thử (Part 1 - 7), Bảng ma trận 200 câu hỏi, Dashboard phân tích điểm.

- Tinh thần: Mật độ thông tin cao (High-density), không gây xao nhãng (Distraction-free), chuẩn xác từng pixel.
- Quy tắc giao diện:
    - Ẩn hoàn toàn Global Header/Footer và thanh điều hướng mạng xã hội khi vào chế độ làm bài.
    - Header phòng thi cố định (Sticky): Hiển thị đồng hồ đếm ngược (monospace font `font-mono`), thanh tiến độ làm bài (% hoàn thành) và nút nộp bài.
    - Bảng câu hỏi (Question Matrix): Lưới ô vuông câu hỏi nhỏ gọn (`w-8 h-8 rounded text-xs`), màu sắc thay đổi rõ ràng theo trạng thái:
        - Chưa làm: Nền xám nhạt `bg-slate-100 text-slate-600`.
        - Đã chọn đáp án: Nền xanh `bg-blue-600 text-white`.
        - Gắn cờ xem lại: Viền cam đậm `border-2 border-amber-500`.
    - Phím tắt (Keyboard Navigation): Hỗ trợ chuyển câu bằng phím mũi tên hoặc phím số.

---

## 4. Context C: Reading Passage & Explanations (Notion Style)
Áp dụng cho: Bài đọc hiểu Part 6, Part 7, Bài viết giáo trình và Màn hình xem giải thích chi tiết.

- Tinh thần: Tối ưu cho việc đọc lướt (skimming) và tiếp thu kiến thức dài mà không mỏi mắt.
- Typography cho đoạn văn bản:
    - Chiều rộng khối văn bản tối đa: `max-w-prose` (khoảng 65–75 ký tự một dòng).
    - Cỡ chữ: `text-[15px]` hoặc `text-base`, khoảng cách dòng thoáng `leading-relaxed` (1.65–1.75).
    - Khối trích dẫn (Email, Memorandum, Bảng tin trong Part 7): Đóng khung viền xám nhẹ, nền `#F8FAFC`, phân biệt rõ ràng với câu hỏi trắc nghiệm.
- Hộp giải thích (Explanation Callouts):
    - Khối callout bo góc `rounded-md`, viền trái dày 3px phân loại theo:
        - Từ vựng mới: Border xanh dương.
        - Bẫy ngữ pháp cần tránh: Border vàng cam.
        - Dẫn chứng đoạn văn: Text highlight vàng nhạt (`bg-yellow-100/60`).

---

## 5. Anti-Slop Safeguards
- Cấm dùng gradient sặc sỡ kiểu Web3/Crypto tím-hồng.
- Cấm icon 3D bóng bẩy hoặc hình minh họa trẻ con dạng sticker.
- Không dùng hiệu ứng chuyển động (animations) thừa thãi trong phòng thi; animation chỉ dùng cho tooltip và dropdown với thời gian phản hồi dưới 150ms.