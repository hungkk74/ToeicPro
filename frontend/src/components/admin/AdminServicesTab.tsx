'use client';

import {
  Server,
  Activity,
  CheckCircle2,
  ExternalLink,
  Shield,
  Cpu,
  Database,
  Radio,
  Layers,
} from 'lucide-react';

interface ServiceItem {
  name: string;
  category: 'Gateway' | 'Business Microservice' | 'Security & IAM' | 'Infrastructure';
  port: number | string;
  tech: string;
  status: 'online' | 'warning';
  uptime: string;
  url?: string;
  description: string;
}

const SERVICES_DATA: ServiceItem[] = [
  {
    name: 'API Gateway',
    category: 'Gateway',
    port: 8080,
    tech: 'Spring Cloud Gateway • Reactive WebFlux',
    status: 'online',
    uptime: '99.98%',
    url: 'http://localhost:8080',
    description: 'Cổng định tuyến trung tâm, Token Relay & Circuit Breaker',
  },
  {
    name: 'User Service',
    category: 'Business Microservice',
    port: 8081,
    tech: 'Spring Boot 3.4 • MySQL • Redis',
    status: 'online',
    uptime: '99.95%',
    description: 'Quản lý thông tin học viên & hồ sơ cá nhân',
  },
  {
    name: 'Exam Service',
    category: 'Business Microservice',
    port: 8082,
    tech: 'Spring Boot 3.4 • MySQL • Audio Storage',
    status: 'online',
    uptime: '99.99%',
    description: 'Ngân hàng đề thi TOEIC, âm thanh Part 1-4, chấm điểm tự động',
  },
  {
    name: 'Course Service',
    category: 'Business Microservice',
    port: 8083,
    tech: 'Spring Boot 3.4 • MySQL • Redis',
    status: 'online',
    uptime: '99.92%',
    description: 'Lộ trình bài giảng, chương học và tiến độ học viên',
  },
  {
    name: 'Subscription Service',
    category: 'Business Microservice',
    port: 8084,
    tech: 'Spring Boot 3.4 • MySQL',
    status: 'online',
    uptime: '99.90%',
    description: 'Quản lý gói thi thử VIP, thời hạn tài khoản học viên',
  },
  {
    name: 'Payment Service',
    category: 'Business Microservice',
    port: 8085,
    tech: 'Spring Boot 3.4 • MySQL • Kafka Producer',
    status: 'online',
    uptime: '99.95%',
    description: 'Cổng thanh toán VnPay / Momo và xử lý giao dịch',
  },
  {
    name: 'Notification Service',
    category: 'Business Microservice',
    port: 8086,
    tech: 'Spring Boot 3.4 • Kafka Consumer • Mail',
    status: 'online',
    uptime: '99.90%',
    description: 'Thông báo kết quả thi, nhắc nhở học tập qua sự kiện Kafka',
  },
  {
    name: 'Keycloak IAM',
    category: 'Security & IAM',
    port: 9080,
    tech: 'Keycloak 26.7.2 • OAuth2 / OpenID Connect',
    status: 'online',
    uptime: '100.0%',
    url: 'http://localhost:9080/admin/master/console/#/jhipster',
    description: 'Máy chủ định danh trung tâm, phân quyền Realm jhipster',
  },
  {
    name: 'Consul Service Discovery',
    category: 'Infrastructure',
    port: 8500,
    tech: 'HashiCorp Consul 2.0.3',
    status: 'online',
    uptime: '100.0%',
    url: 'http://localhost:8500',
    description: 'Khám phá dịch vụ & quản lý cấu hình phân tán',
  },
  {
    name: 'Kafka Message Broker',
    category: 'Infrastructure',
    port: 9092,
    tech: 'Apache Kafka 3.8.0',
    status: 'online',
    uptime: '100.0%',
    url: 'http://localhost:8989',
    description: 'Hàng đợi sự kiện phi tập trung: payment-completed, exam-finished',
  },
  {
    name: 'MySQL Database Cluster',
    category: 'Infrastructure',
    port: 3306,
    tech: 'MySQL 8.4 Enterprise Container',
    status: 'online',
    uptime: '100.0%',
    description: 'Cơ sở dữ liệu quan hệ lưu trữ đề thi, tài khoản, giao dịch',
  },
  {
    name: 'Redis Cache Server',
    category: 'Infrastructure',
    port: 6379,
    tech: 'Redis 8.10.1 in-memory',
    status: 'online',
    uptime: '100.0%',
    description: 'Bộ nhớ đệm phân tán L2 tăng tốc độ truy vấn đề thi & phiên',
  },
];

export default function AdminServicesTab() {
  return (
    <div className="space-y-5">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200 shadow-2xs">
        <div className="flex items-center gap-2">
          <Server className="w-5 h-5 text-blue-600" />
          <div>
            <h2 className="text-sm font-bold text-slate-900">Giám Sát Hạ Tầng Microservices</h2>
            <p className="text-[11px] text-slate-500">12 thành phần dịch vụ phân tán đang hoạt động</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-50 text-emerald-700 font-semibold text-xs rounded-lg border border-emerald-200">
            <Activity className="w-3.5 h-3.5 text-emerald-600 animate-pulse" />
            <span>Tất cả dịch vụ Online (100% Sẵn sàng)</span>
          </span>
        </div>
      </div>

      {/* Services Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider text-[10px]">
                <th className="py-3 px-4">Tên Dịch Vụ</th>
                <th className="py-3 px-4">Phân Loại</th>
                <th className="py-3 px-4">Cổng (Port)</th>
                <th className="py-3 px-4">Công Nghệ / Nền Tảng</th>
                <th className="py-3 px-4">Tình Trạng (Health)</th>
                <th className="py-3 px-4 text-right">Bảng Điều Khiển</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {SERVICES_DATA.map((svc) => (
                <tr key={svc.name} className="hover:bg-slate-50/60 transition-colors">
                  <td className="py-3 px-4">
                    <span className="font-bold text-slate-900 block">{svc.name}</span>
                    <span className="text-[11px] text-slate-500">{svc.description}</span>
                  </td>
                  <td className="py-3 px-4">
                    <span
                      className={`px-2 py-0.5 rounded text-[10px] font-semibold border ${
                        svc.category === 'Gateway'
                          ? 'bg-purple-50 text-purple-700 border-purple-200'
                          : svc.category === 'Business Microservice'
                          ? 'bg-blue-50 text-blue-700 border-blue-200'
                          : svc.category === 'Security & IAM'
                          ? 'bg-amber-50 text-amber-700 border-amber-200'
                          : 'bg-slate-100 text-slate-700 border-slate-200'
                      }`}
                    >
                      {svc.category}
                    </span>
                  </td>
                  <td className="py-3 px-4 font-mono font-bold text-slate-800">
                    :{svc.port}
                  </td>
                  <td className="py-3 px-4 text-slate-600 font-medium">
                    {svc.tech}
                  </td>
                  <td className="py-3 px-4">
                    <span className="inline-flex items-center gap-1 text-[11px] font-medium text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                      <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                      Online ({svc.uptime})
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    {svc.url ? (
                      <a
                        href={svc.url}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 text-xs font-semibold text-blue-600 hover:text-blue-800 hover:bg-blue-50 px-2.5 py-1 rounded-lg transition-colors cursor-pointer"
                      >
                        <span>Mở Console</span>
                        <ExternalLink className="w-3 h-3" />
                      </a>
                    ) : (
                      <span className="text-slate-400 text-[11px]">Nội bộ (:8080 LB)</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
