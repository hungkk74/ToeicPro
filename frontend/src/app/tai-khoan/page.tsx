'use client';

import { useState, useEffect, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { getCurrentUser, updateUserAccount, UpdateAccountPayload } from '@/services/authService';
import { UserAccountDTO } from '@/types/backend';
import { Loader2 } from 'lucide-react';

export default function AccountProfilePage() {
  const router = useRouter();
  const [user, setUser] = useState<UserAccountDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');

  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    async function loadUser() {
      try {
        const currentUser = await getCurrentUser();
        if (currentUser) {
          setUser(currentUser);
          setFirstName(currentUser.firstName || '');
          setLastName(currentUser.lastName || '');
          setEmail(currentUser.email || '');
        } else {
          router.push('/dang-nhap');
        }
      } catch (err) {
        console.error('Failed to load user', err);
      } finally {
        setLoading(false);
      }
    }
    loadUser();
  }, [router]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setSaving(true);

    const payload: UpdateAccountPayload = {
      firstName,
      lastName,
      email,
    };

    try {
      const res = await updateUserAccount(payload);
      if (res.success) {
        setMessage({ type: 'success', text: res.message || 'Cập nhật thành công!' });
        
        // Cập nhật lại thông tin ở Header nếu có
        const updatedUser = await getCurrentUser();
        if (updatedUser) {
          setUser(updatedUser);
          window.dispatchEvent(new CustomEvent('auth-state-changed', { detail: { user: updatedUser } }));
        }
      } else {
        setMessage({ type: 'error', text: res.error || 'Cập nhật thất bại.' });
      }
    } catch (err) {
      setMessage({ type: 'error', text: 'Có lỗi xảy ra khi cập nhật tài khoản.' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-canvas flex flex-col items-center justify-center">
        <Loader2 className="w-8 h-8 text-primary animate-spin" />
        <span className="mt-4 text-sm font-semibold text-text-secondary">Đang tải thông tin...</span>
      </div>
    );
  }

  if (!user) {
    return null; // Will redirect
  }

  return (
    <div className="flex flex-col min-h-screen bg-canvas font-body-default text-text-body antialiased">
      <Navbar />

      <main className="flex-1 w-full max-w-[1440px] mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-space-xl">
        <div className="max-w-2xl mx-auto">
          <div className="mb-space-lg">
            <h1 className="font-headline-lg text-headline-lg font-bold text-text-primary tracking-tight">
              Hồ sơ tài khoản
            </h1>
            <p className="mt-2 text-text-secondary">
              Quản lý thông tin cá nhân và cài đặt bảo mật của bạn.
            </p>
          </div>

          <div className="bg-surface rounded-xl p-space-lg sm:p-space-xl shadow-sm border border-border-subtle">
            <div className="flex items-center gap-space-md mb-space-lg pb-space-lg border-b border-border-subtle">
              <div className="w-16 h-16 rounded-full bg-primary-container text-on-primary-container flex items-center justify-center font-headline-md text-headline-md font-bold">
                {firstName?.charAt(0) || user.login.charAt(0).toUpperCase()}
              </div>
              <div>
                <div className="font-headline-sm text-headline-sm font-bold text-text-primary">
                  {user.login}
                </div>
                <div className="text-sm text-text-secondary mt-1">
                  Vai trò: {user.authorities.includes('ROLE_ADMIN') ? 'Quản trị viên' : 'Học viên'}
                </div>
              </div>
            </div>

            {message && (
              <div
                className={`mb-space-lg p-space-md rounded-lg flex items-start gap-2 border ${
                  message.type === 'success'
                    ? 'bg-green-50 border-green-200 text-green-800'
                    : 'bg-red-50 border-red-200 text-red-800'
                }`}
              >
                <span className="material-symbols-outlined text-[20px] shrink-0 mt-0.5">
                  {message.type === 'success' ? 'check_circle' : 'error'}
                </span>
                <span className="font-label-md text-label-md font-medium leading-relaxed">
                  {message.text}
                </span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="flex flex-col gap-space-lg">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-space-lg">
                <div className="flex flex-col gap-2">
                  <label htmlFor="firstName" className="font-label-md text-label-md font-semibold text-text-primary">
                    Tên (First Name)
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    className="px-4 py-2.5 rounded-lg border border-border-subtle bg-surface focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors text-text-primary placeholder:text-text-placeholder"
                    placeholder="VD: Nam"
                  />
                </div>

                <div className="flex flex-col gap-2">
                  <label htmlFor="lastName" className="font-label-md text-label-md font-semibold text-text-primary">
                    Họ (Last Name)
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    className="px-4 py-2.5 rounded-lg border border-border-subtle bg-surface focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors text-text-primary placeholder:text-text-placeholder"
                    placeholder="VD: Nguyễn"
                  />
                </div>
              </div>

              <div className="flex flex-col gap-2">
                <label htmlFor="email" className="font-label-md text-label-md font-semibold text-text-primary">
                  Địa chỉ Email
                </label>
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="px-4 py-2.5 rounded-lg border border-border-subtle bg-surface focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors text-text-primary placeholder:text-text-placeholder"
                  placeholder="VD: nam.nguyen@example.com"
                  required
                />
              </div>

              <div className="flex flex-col gap-2 opacity-60">
                <label htmlFor="username" className="font-label-md text-label-md font-semibold text-text-primary flex items-center gap-1.5">
                  Tên đăng nhập (Username)
                  <span className="material-symbols-outlined text-[16px]" title="Không thể thay đổi">lock</span>
                </label>
                <input
                  id="username"
                  type="text"
                  value={user.login}
                  disabled
                  className="px-4 py-2.5 rounded-lg border border-border-subtle bg-surface-subtle text-text-secondary cursor-not-allowed"
                />
                <p className="text-xs text-text-secondary mt-1">
                  Tên đăng nhập là duy nhất và không thể thay đổi sau khi tạo.
                </p>
              </div>

              <div className="pt-space-md border-t border-border-subtle mt-2 flex items-center justify-end">
                <button
                  type="submit"
                  disabled={saving}
                  className="inline-flex items-center gap-2 px-6 py-2.5 rounded-lg bg-primary text-on-primary font-label-md text-label-md font-semibold shadow-sm hover:bg-primary-container hover:shadow transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {saving && <Loader2 className="w-4 h-4 animate-spin" />}
                  <span>{saving ? 'Đang lưu...' : 'Lưu thay đổi'}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
