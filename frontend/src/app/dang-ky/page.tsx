'use client';

import { useState, useEffect, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { Lock, User, Mail, Eye, EyeOff, AlertCircle, Loader2 } from 'lucide-react';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { registerNewUser, getCurrentUser } from '@/services/authService';

function RegisterForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const redirectUrl = searchParams.get('redirect') || '/';

  const [fullName, setFullName] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    getCurrentUser().then((user) => {
      if (user) {
        router.replace(redirectUrl);
      }
    });
  }, [router, redirectUrl]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !email.trim() || !password) {
      setErrorMessage('Vui lòng nhập đầy đủ các trường thông tin bắt buộc.');
      return;
    }

    if (password.length < 4) {
      setErrorMessage('Mật khẩu phải chứa ít nhất 4 ký tự.');
      return;
    }

    if (password !== confirmPassword) {
      setErrorMessage('Mật khẩu xác nhận không khớp. Vui lòng kiểm tra lại.');
      return;
    }

    setIsLoading(true);
    setErrorMessage(null);

    const parts = fullName.trim().split(' ');
    const firstName = parts.slice(1).join(' ') || parts[0] || '';
    const lastName = parts.length > 1 ? parts[0] : '';

    const res = await registerNewUser({
      username: username.trim(),
      email: email.trim(),
      password,
      firstName,
      lastName,
    });
    setIsLoading(false);

    if (res.success) {
      router.push(redirectUrl);
    } else {
      setErrorMessage(res.error || 'Đăng ký tài khoản thất bại.');
    }
  };

  return (
    <div className="max-w-md w-full mx-auto bg-white rounded-xl border border-slate-200/90 shadow-xs p-6 sm:p-8 space-y-6">
      <div className="space-y-2 text-center sm:text-left">
        <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
          Gia Nhập TOEIC Pro
        </span>
        <h1 className="font-serif text-2xl sm:text-3xl font-bold text-slate-950 tracking-normal leading-snug">
          Đăng ký tài khoản
        </h1>
        <p className="text-xs sm:text-sm text-slate-600 leading-relaxed font-sans">
          Tạo tài khoản học viên để lưu tiến độ và thi thử miễn phí.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        {errorMessage && (
          <div className="p-3 rounded-lg bg-rose-50 border border-rose-200 flex items-start gap-2.5 text-rose-700 text-xs">
            <AlertCircle className="w-4 h-4 text-rose-600 shrink-0 mt-0.5" />
            <div className="flex-1">{errorMessage}</div>
          </div>
        )}

        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-700 block">
            Họ và tên
          </label>
          <div className="relative flex items-center">
            <div className="absolute left-3 pointer-events-none text-slate-400">
              <User className="w-4 h-4" />
            </div>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Ví dụ: Nguyễn Văn A"
              className="w-full pl-9 pr-3 py-2.5 bg-white border border-slate-300 rounded-lg text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
              autoComplete="name"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-700 block">
            Tên đăng nhập <span className="text-rose-500">*</span>
          </label>
          <div className="relative flex items-center">
            <div className="absolute left-3 pointer-events-none text-slate-400">
              <User className="w-4 h-4" />
            </div>
            <input
              type="text"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Tên đăng nhập viết liền không dấu"
              className="w-full pl-9 pr-3 py-2.5 bg-white border border-slate-300 rounded-lg text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
              autoComplete="username"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-700 block">
            Địa chỉ Email <span className="text-rose-500">*</span>
          </label>
          <div className="relative flex items-center">
            <div className="absolute left-3 pointer-events-none text-slate-400">
              <Mail className="w-4 h-4" />
            </div>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@gmail.com"
              className="w-full pl-9 pr-3 py-2.5 bg-white border border-slate-300 rounded-lg text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
              autoComplete="email"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-700 block">
            Mật khẩu <span className="text-rose-500">*</span>
          </label>
          <div className="relative flex items-center">
            <div className="absolute left-3 pointer-events-none text-slate-400">
              <Lock className="w-4 h-4" />
            </div>
            <input
              type={showPassword ? 'text' : 'password'}
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Tối thiểu 4 ký tự"
              className="w-full pl-9 pr-10 py-2.5 bg-white border border-slate-300 rounded-lg text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
              autoComplete="new-password"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-2.5 text-slate-400 hover:text-slate-700 p-1 focus:outline-none cursor-pointer"
              title={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
            >
              {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
        </div>

        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-700 block">
            Xác nhận mật khẩu <span className="text-rose-500">*</span>
          </label>
          <div className="relative flex items-center">
            <div className="absolute left-3 pointer-events-none text-slate-400">
              <Lock className="w-4 h-4" />
            </div>
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              required
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Nhập lại mật khẩu"
              className="w-full pl-9 pr-10 py-2.5 bg-white border border-slate-300 rounded-lg text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
              autoComplete="new-password"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-2.5 text-slate-400 hover:text-slate-700 p-1 focus:outline-none cursor-pointer"
              title={showConfirmPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
            >
              {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full inline-flex items-center justify-center gap-2 py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium text-xs sm:text-sm rounded-lg transition-colors cursor-pointer disabled:opacity-60 shadow-xs mt-2"
        >
          {isLoading && <Loader2 className="w-4 h-4 animate-spin" />}
          <span>{isLoading ? 'Đang tạo tài khoản...' : 'Đăng ký ngay'}</span>
        </button>
      </form>

      <div className="pt-4 border-t border-slate-100 text-center text-xs text-slate-600 space-y-2">
        <p>
          Đã có tài khoản TOEIC Pro?{' '}
          <Link
            href="/dang-nhap"
            className="font-semibold text-blue-600 hover:text-blue-700 underline underline-offset-2"
          >
            Đăng nhập ngay
          </Link>
        </p>
        <p>
          <Link
            href="/"
            className="text-slate-500 hover:text-slate-700 transition-colors"
          >
            ← Quay lại trang chủ
          </Link>
        </p>
      </div>
    </div>
  );
}

export default function RegisterPage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1 flex items-center justify-center px-4">
        <Suspense
          fallback={
            <div className="max-w-md w-full mx-auto bg-white rounded-xl border border-slate-200/90 shadow-xs p-8 text-center">
              <Loader2 className="w-6 h-6 text-blue-600 animate-spin mx-auto mb-2" />
              <span className="text-xs text-slate-500">Đang tải...</span>
            </div>
          }
        >
          <RegisterForm />
        </Suspense>
      </main>
      <Footer />
    </div>
  );
}
