# Hệ sinh thái Microservices TOEIC Pro - Sơ đồ Luồng hoạt động Tổng quan
> Tài liệu kiến trúc và sơ đồ luồng dữ liệu (Data & Event Flows). Có thể xem trực tiếp bằng các công cụ Mermaid Viewer (VSCode, IntelliJ Markdown Plugin, hoặc [Mermaid Live Editor](https://mermaid.live)).

---

## 1. Sơ đồ Kiến trúc Hệ sinh thái Tổng thể (System Architecture Overview)

```mermaid
flowchart TB
    subgraph CLIENT_LAYER["Tầng Thiết bị & Người dùng"]
        WebClient["Web Browser (React / Next.js)"]
        MobileClient["Mobile App (Flutter / React Native)"]
    end

    subgraph CDN_STORAGE["Lưu trữ & Phân phối File Media (0 VNĐ)"]
        CDN["Cloudflare CDN / AWS CloudFront"]
        ObjectStorage[("Cloudflare R2 / AWS S3<br>(Audio MP3 đề thi, Video bài giảng)")]
        CDN --> ObjectStorage
    end

    subgraph AUTH_LAYER["Bảo mật & Định danh Tập trung"]
        Keycloak["Keycloak OIDC Server (:9080)<br>Single Sign-On & JWT Token"]
    end

    subgraph GATEWAY_LAYER["Cổng giao tiếp tập trung (API Gateway)"]
        Gateway["Spring Cloud Gateway (:8080)<br>Định tuyến, Token Relay, Circuit Breakers"]
    end

    subgraph SERVICES_LAYER["Tầng Nghiệp vụ Microservices (Core Services)"]
        UserService["User Service (:8081)<br>Hồ sơ học viên, Streak, EXP, Cấp bậc"]
        SubService["Subscription Service (:8082)<br>Gói hội viên VIP, Thời hạn"]
        PaymentService["Payment Service (:8083)<br>Giao dịch VNPay/MoMo, Webhook IPN"]
        ExamService["Exam Service (:8084)<br>Ngân hàng đề ETS, Chấm điểm 990"]
        CourseService["Course Service (:8085)<br>Khóa học, Chương, Video, Tiến độ"]
        NotiService["Notification Service (:8086)<br>In-App Inbox, Email, Push Thông báo"]
    end

    subgraph EVENT_BROKER["Trục điều phối Sự kiện Bất đồng bộ"]
        Kafka[("Apache Kafka (:9092)<br>Topics: payment-events, exam-events, activity-events")]
    end

    subgraph PERSISTENCE_CACHE["Tầng Lưu trữ Bền vững & Bộ đệm"]
        MySQL[("MySQL 8 Database (:3306)<br>Database-per-Service")]
        Redis[("Redis Cache & Leaderboard (:6379)<br>ZSET BXH, Session Cache")]
        Consul["HashiCorp Consul (:8500)<br>Service Discovery & Registry"]
    end

    %% Client Connections
    WebClient -->|HTTPS API Requests| Gateway
    MobileClient -->|HTTPS API Requests| Gateway
    WebClient -.->|Nghe Audio / Xem Video trực tiếp| CDN
    MobileClient -.->|Nghe Audio / Xem Video trực tiếp| CDN

    %% Gateway & Auth
    Gateway <-->|Xác thực OIDC| Keycloak
    Gateway -->|Token Relay /api/user-profiles/**| UserService
    Gateway -->|Token Relay /api/subscriptions/**| SubService
    Gateway -->|Token Relay /api/payment-transactions/**| PaymentService
    Gateway -->|Token Relay /api/exams/**| ExamService
    Gateway -->|Token Relay /api/courses/**| CourseService
    Gateway -->|Token Relay /api/notifications/**| NotiService

    %% Service to Infrastructure
    UserService --- MySQL
    SubService --- MySQL
    PaymentService --- MySQL
    ExamService --- MySQL
    CourseService --- MySQL
    NotiService --- MySQL

    UserService --- Redis
    ExamService --- Redis
    Gateway --- Consul

    %% Event Connections
    PaymentService -->|Bắn PaymentCompletedEvent| Kafka
    ExamService -->|Bắn ExamFinishedEvent| Kafka
    CourseService -->|Bắn CourseProgressEvent| Kafka

    Kafka -->|Kích hoạt mở khóa học| CourseService
    Kafka -->|Kích hoạt mở gói VIP| SubService
    Kafka -->|Gửi Email hóa đơn & kết quả| NotiService
    Kafka -->|Cộng điểm EXP & Streak| UserService
```

---

## 2. Luồng Nghiệp vụ 1: Mua Khóa học / Gói VIP & Tự động Kích hoạt (Payment Flow)

```mermaid
sequenceDiagram
    autonumber
    actor Student as Học viên (Web/App)
    participant GW as API Gateway (:8080)
    participant PS as Payment Service (:8083)
    participant PG as Cổng VNPay / MoMo
    participant K as Kafka Broker (:9092)
    participant CS as Course Service (:8085)
    participant SS as Subscription Service (:8082)
    participant NS as Notification Service (:8086)

    Student->>GW: 1. Bấm mua khóa học (POST /api/payment-transactions)
    GW->>PS: Chuyển tiếp request kèm JWT Token
    PS->>PS: Tạo bản ghi Transaction (Trạng thái: PENDING)
    PS->>PG: Khởi tạo URL giao dịch VNPay/MoMo
    PG-->>PS: Trả về Payment URL
    PS-->>Student: Điều hướng học viên sang trang thanh toán

    Student->>PG: 2. Học viên quét mã QR / Chuyển khoản ngân hàng
    PG->>PS: 3. Cổng gửi Webhook IPN (Xác nhận tiền đã vào tài khoản)
    PS->>PS: Cập nhật Transaction (Trạng thái: COMPLETED)
    
    %% Bất đồng bộ qua Kafka
    PS-)K: 4. Bắn sự kiện "PaymentCompletedEvent" vào Kafka
    PS-->>PG: Phản hồi IPN: {"RspCode": "00", "Message": "Confirm Success"}

    par Kích hoạt Khóa học
        K-)CS: Tiêu thụ event (Nếu loại sản phẩm = COURSE)
        CS->>CS: Tạo bản ghi "course_enrollment" (Mở khóa học)
    and Kích hoạt Gói VIP
        K-)SS: Tiêu thụ event (Nếu loại sản phẩm = SUBSCRIPTION)
        SS->>SS: Gia hạn gói VIP (Mở quyền thi thử không giới hạn)
    and Gửi Thông báo & Hóa đơn
        K-)NS: Tiêu thụ event
        NS->>NS: Lưu In-App Notification (Có thông báo mới trên chuông)
        NS->>Student: Gửi Email biên lai hóa đơn điện tử
    end
```

---

## 3. Luồng Nghiệp vụ 2: Thi thử TOEIC & Chấm điểm Tự động (Exam Flow)

```mermaid
sequenceDiagram
    autonumber
    actor Student as Học viên (Web/App)
    participant GW as API Gateway (:8080)
    participant ES as Exam Service (:8084)
    participant CDN as CDN (Audio & Ảnh đề thi)
    participant K as Kafka Broker (:9092)
    participant US as User Service (:8081)
    participant NS as Notification Service (:8086)

    Student->>GW: 1. Chọn đề thi TOEIC ETS (GET /api/exams/1/full)
    GW->>ES: Chuyển tiếp request
    ES-->>Student: Trả về bộ 200 câu hỏi (kèm link CDN audio .mp3 & hình ảnh)
    
    Note over Student,CDN: Học viên nghe audio và xem tranh trực tiếp từ CDN<br>(Không tốn băng thông của Backend)

    Student->>GW: 2. Nộp bài thi (POST /api/exam-attempts/submit)
    GW->>ES: Gửi mảng 200 câu trả lời của học viên
    
    rect rgb(240, 248, 255)
        Note over ES: Chấm điểm tự động:<br>1. So khớp đáp án đúng Part 1-7<br>2. Đếm số câu đúng Listening & Reading<br>3. Tra bảng "score_conversion" (Barem ETS chuẩn)<br>4. Ra điểm số quy đổi chuẩn (thang điểm 10 - 990)
    end

    ES-->>Student: Trả về kết quả tức thì (Báo cáo điểm & Lời giải chi tiết)

    %% Bất đồng bộ qua Kafka
    ES-)K: 3. Bắn sự kiện "ExamFinishedEvent" vào Kafka

    par Cập nhật Gamification
        K-)US: Tiêu thụ event
        US->>US: Kiểm tra chuỗi Streak (Hôm nay đã học chưa?)
        US->>US: Cộng điểm EXP theo điểm thi vào Redis ZSET
    and Gửi Báo cáo Điểm thi
        K-)NS: Tiêu thụ event
        NS->>NS: Tạo In-App Notification: "Bạn vừa đạt XXX/990 điểm!"
        NS->>Student: Gửi Email báo cáo năng lực chi tiết
    end
```

---

## 4. Luồng Nghiệp vụ 3: Tích lũy Điểm EXP & Bảng xếp hạng Siêu tốc (Gamification Flow)

```mermaid
sequenceDiagram
    autonumber
    actor Student as Học viên (Web/App)
    participant K as Kafka Broker (:9092)
    participant US as User Service (:8081)
    participant Redis as Redis Cache (:6379)
    participant DB as MySQL Database

    Note over K: Nhận sự kiện từ ExamService / CourseService
    K-)US: Sự kiện: Học viên hoàn thành hoạt động (+150 EXP)
    
    US->>DB: 1. Lưu bản ghi audit "point_history" & cộng dồn "total_exp"
    US->>Redis: 2. Lệnh "ZINCRBY leaderboard:weekly:2026-37 150 userId"
    US->>Redis: 3. Lệnh "ZINCRBY leaderboard:all_time 150 userId"

    Note over Student,Redis: Khi học viên vào xem Bảng xếp hạng (Leaderboard):
    Student->>US: GET /api/leaderboard?type=weekly&limit=10
    US->>Redis: Lệnh "ZREVRANGE leaderboard:weekly:2026-37 0 9 WITHSCORES"
    Redis-->>US: Trả về danh sách Top 10 trong 1 miligiây
    US-->>Student: Trả về bảng xếp hạng hiển thị tức thì
```
