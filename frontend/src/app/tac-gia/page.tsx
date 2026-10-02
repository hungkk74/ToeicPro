import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import Link from 'next/link';
import { ExternalLink, Code2, Sparkles, Mail, Globe, Layers } from 'lucide-react';

export const metadata = {
  title: 'Tác giả | TOEIC Pro',
  description: 'Thông tin tác giả và nhà phát triển nền tảng TOEIC Pro - Nguyễn Kiều Hưng.',
};

export default function AuthorPage() {
  const facebookUrl = 'https://www.facebook.com/hung.kiu.165/';

  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />

      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Header */}
          <div className="text-center mb-8">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200/80 mb-3 shadow-xs">
              <Sparkles className="w-3.5 h-3.5 text-blue-600" />
              <span>Nhà Phát Triển Nền Tảng</span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
              Tác Giả
            </h1>
          </div>

          {/* Author Profile Card */}
          <div className="bg-white rounded-3xl p-8 sm:p-10 shadow-lg shadow-slate-200/60 border border-slate-200/80 relative overflow-hidden">
            {/* Background Accent Gradients */}
            <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl pointer-events-none" />
            <div className="absolute bottom-0 left-0 w-64 h-64 bg-indigo-500/5 rounded-full blur-3xl pointer-events-none" />

            <div className="relative z-10 flex flex-col sm:flex-row items-center sm:items-start gap-6 sm:gap-8 text-center sm:text-left">
              {/* Avatar placeholder / Initial */}
              <div className="relative group shrink-0">
                <div className="w-28 h-28 sm:w-32 sm:h-32 rounded-2xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-blue-500 text-white flex items-center justify-center text-4xl font-extrabold shadow-xl shadow-blue-600/25 ring-4 ring-white">
                  NKH
                </div>
                <div className="absolute -bottom-2 -right-2 bg-emerald-500 text-white text-[11px] font-bold px-2 py-0.5 rounded-full border-2 border-white shadow-xs">
                  Active
                </div>
              </div>

              {/* Author Info */}
              <div className="flex-1 space-y-3">
                <div>
                  <h2 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
                    Nguyễn Kiều Hưng
                  </h2>
                  <p className="text-sm sm:text-base font-medium text-blue-600 mt-0.5">
                    Tác giả &amp; Kỹ sư phát triển hệ thống TOEIC Pro
                  </p>
                </div>

                <p className="text-sm text-slate-600 leading-relaxed max-w-xl">
                  Xây dựng và vận hành toàn bộ kiến trúc nền tảng TOEIC Pro từ thiết kế Microservices phân tán (Spring Boot, Spring Cloud Gateway, Kafka, Keycloak) đến trải nghiệm giao diện người dùng Next.js 14 App Router.
                </p>

                {/* Social Connect Button */}
                <div className="pt-2 flex flex-wrap items-center justify-center sm:justify-start gap-3">
                  <a
                    href={facebookUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-2.5 px-5 py-2.5 bg-[#1877F2] hover:bg-[#166fe5] text-white text-sm font-semibold rounded-xl shadow-md shadow-[#1877F2]/25 transition-all hover:scale-105 active:scale-95"
                  >
                    <svg className="w-4 h-4 fill-current" viewBox="0 0 24 24">
                      <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                    </svg>
                    <span>Facebook: Nguyễn Kiều Hưng</span>
                    <ExternalLink className="w-3.5 h-3.5 opacity-80" />
                  </a>

                  <Link
                    href="/"
                    className="inline-flex items-center gap-2 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-sm font-medium rounded-xl transition-all"
                  >
                    <Globe className="w-4 h-4" />
                    <span>Trang chủ</span>
                  </Link>
                </div>
              </div>
            </div>

            {/* Direct Link Display */}
            <div className="mt-8 pt-6 border-t border-slate-100 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-500 bg-slate-50/70 -mx-8 sm:-mx-10 -mb-8 sm:-mb-10 p-6 rounded-b-3xl">
              <span className="font-medium text-slate-600">Facebook cá nhân:</span>
              <a
                href={facebookUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-600 hover:text-blue-700 hover:underline font-mono break-all font-semibold flex items-center gap-1"
              >
                {facebookUrl}
                <ExternalLink className="w-3 h-3" />
              </a>
            </div>
          </div>

          {/* Platform Highlights */}
          <div className="mt-8 grid sm:grid-cols-2 gap-4">
            <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-start gap-3.5">
              <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center shrink-0">
                <Layers className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-sm font-bold text-slate-900">Kiến Trúc Microservices</h4>
                <p className="text-xs text-slate-500 mt-1">
                  8 services độc lập, phân quyền OIDC Keycloak, xử lý sự kiện qua Apache Kafka và CDN Cloudflare R2.
                </p>
              </div>
            </div>

            <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-start gap-3.5">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center shrink-0">
                <Code2 className="w-5 h-5" />
              </div>
              <div>
                <h4 className="text-sm font-bold text-slate-900">Thiết Kế Tối Ưu Tải</h4>
                <p className="text-xs text-slate-500 mt-1">
                  Next.js App Router, R2DBC Reactive Gateway, Redis Caching và chống lộ đáp án đề thi trắc nghiệm.
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
