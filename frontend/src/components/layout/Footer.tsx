import Link from 'next/link';
import { BookOpen, ShieldCheck, Mail, MapPin, Phone } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-slate-900 text-slate-300 py-12 mt-auto border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 lg:gap-12 pb-8 border-b border-slate-800">
          {/* Cột 1: Branding */}
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <span className="text-xl font-bold text-white tracking-tight">
                Toeic<span className="text-blue-500">Pro</span>
              </span>
            </div>
            <p className="text-sm text-slate-400 leading-relaxed max-w-xs">
              Hệ thống đào tạo và thi thử TOEIC trực tuyến hàng đầu. Giúp bạn chinh phục mục tiêu TOEIC nhanh chóng bằng lộ trình cá nhân hóa và AI phân tích.
            </p>
          </div>

          {/* Cột 2: Liên kết nhanh */}
          <div className="space-y-4">
            <h3 className="text-sm font-semibold text-white uppercase tracking-wider">Liên Kết Nhanh</h3>
            <ul className="space-y-3 text-sm">
              <li>
                <Link href="/tac-gia" className="hover:text-blue-400 transition-colors flex items-center gap-2">
                  <BookOpen className="w-4 h-4 text-slate-500" /> Về Đội Ngũ Tác Giả
                </Link>
              </li>
              <li>
                <Link href="/chinh-sach-bao-hanh" className="hover:text-blue-400 transition-colors flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4 text-slate-500" /> Chính Sách Bảo Hành
                </Link>
              </li>
              <li>
                <Link href="/quy-che-khao-thi" className="hover:text-blue-400 transition-colors flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4 text-slate-500" /> Quy Chế Khảo Thí
                </Link>
              </li>
              <li>
                <Link href="/quy-doi-diem-ets" className="hover:text-blue-400 transition-colors flex items-center gap-2">
                  <BookOpen className="w-4 h-4 text-slate-500" /> Quy Đổi Điểm ETS
                </Link>
              </li>
            </ul>
          </div>

          {/* Cột 3: Liên hệ */}
          <div className="space-y-4">
            <h3 className="text-sm font-semibold text-white uppercase tracking-wider">Liên Hệ</h3>
            <ul className="space-y-3 text-sm">
              <li className="flex items-start gap-2">
                <MapPin className="w-4 h-4 mt-0.5 text-slate-500 shrink-0" />
                <span>123 Đường Công Nghệ, Phường Cầu Giấy, Hà Nội</span>
              </li>
              <li className="flex items-center gap-2">
                <Phone className="w-4 h-4 text-slate-500 shrink-0" />
                <span>+84 123 456 789</span>
              </li>
              <li className="flex items-center gap-2">
                <Mail className="w-4 h-4 text-slate-500 shrink-0" />
                <span>support@toeicpro.vn</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom */}
        <div className="pt-8 flex flex-col md:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <p>© 2026 Nền tảng ToeicPro. Đã đăng ký Bản quyền.</p>
          <div className="flex gap-4">
            <Link href="/dieu-khoan-su-dung" className="hover:text-white transition-colors">Điều Khoản Sử Dụng</Link>
            <Link href="/bao-mat-thong-tin" className="hover:text-white transition-colors">Bảo Mật Thông Tin</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
