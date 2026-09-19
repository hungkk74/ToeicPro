'use client';

import { useState, useEffect } from 'react';
import {
  Users,
  Search,
  RefreshCw,
  ShieldCheck,
  ShieldAlert,
  User,
  Mail,
  CheckCircle2,
  Loader2,
  AlertCircle,
} from 'lucide-react';

interface KeycloakUserItem {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  enabled: boolean;
  createdTimestamp?: number;
  isAdmin: boolean;
  roles: string[];
}

export default function AdminUsersTab() {
  const [users, setUsers] = useState<KeycloakUserItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [updatingUserId, setUpdatingUserId] = useState<string | null>(null);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const fetchUsers = async () => {
    setIsLoading(true);
    setMessage(null);
    try {
      const res = await fetch('/api/admin/users');
      const data = await res.json();
      if (data.success && data.users) {
        setUsers(data.users);
      } else {
        setMessage({ type: 'error', text: data.error || 'Lỗi khi tải danh sách người dùng' });
      }
    } catch {
      setMessage({ type: 'error', text: 'Không thể kết nối đến máy chủ Keycloak Admin' });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleAdmin = async (user: KeycloakUserItem) => {
    const newAdminState = !user.isAdmin;
    const confirmMsg = newAdminState
      ? `Bạn có chắc muốn cấp quyền Quản Trị Viên (Admin) cho tài khoản '${user.username}' không?`
      : `Bạn có chắc muốn gỡ quyền Admin của tài khoản '${user.username}' và chuyển về Học viên không?`;

    if (!window.confirm(confirmMsg)) return;

    setUpdatingUserId(user.id);
    setMessage(null);

    try {
      const res = await fetch('/api/admin/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: user.id, makeAdmin: newAdminState }),
      });
      const data = await res.json();

      if (data.success) {
        setMessage({ type: 'success', text: data.message });
        // Cập nhật trạng thái ngay trên UI
        setUsers((prev) =>
          prev.map((u) =>
            u.id === user.id
              ? {
                  ...u,
                  isAdmin: newAdminState,
                  roles: newAdminState
                    ? Array.from(new Set([...u.roles, 'ROLE_ADMIN']))
                    : u.roles.filter((r) => r !== 'ROLE_ADMIN'),
                }
              : u
          )
        );
      } else {
        setMessage({ type: 'error', text: data.error || 'Thao tác thất bại' });
      }
    } catch {
      setMessage({ type: 'error', text: 'Lỗi kết nối khi cập nhật quyền' });
    } finally {
      setUpdatingUserId(null);
    }
  };

  const filteredUsers = users.filter(
    (u) =>
      u.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.fullName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-5">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200 shadow-2xs">
        <div className="flex items-center gap-2">
          <Users className="w-5 h-5 text-blue-600" />
          <div>
            <h2 className="text-sm font-bold text-slate-900">Quản Lý Tài Khoản &amp; Học Viên</h2>
            <p className="text-[11px] text-slate-500">
              Đồng bộ dữ liệu thời gian thực từ Keycloak Realm &#39;jhipster&#39;
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm theo username, email..."
              className="pl-8 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg w-52 sm:w-64 focus:bg-white focus:outline-none focus:border-blue-600 transition-colors"
            />
          </div>

          <button
            type="button"
            onClick={fetchUsers}
            disabled={isLoading}
            className="p-1.5 text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg border border-slate-200 transition-colors cursor-pointer disabled:opacity-50"
            title="Làm mới danh sách"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Message feedback */}
      {message && (
        <div
          className={`p-3 rounded-xl border flex items-center gap-2 text-xs animate-in fade-in ${
            message.type === 'success'
              ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
              : 'bg-rose-50 border-rose-200 text-rose-800'
          }`}
        >
          {message.type === 'success' ? (
            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
          ) : (
            <AlertCircle className="w-4 h-4 text-rose-600 shrink-0" />
          )}
          <span className="font-medium">{message.text}</span>
        </div>
      )}

      {/* Users Data Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xs overflow-hidden">
        {isLoading ? (
          <div className="p-12 flex flex-col items-center justify-center gap-3 text-slate-500">
            <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
            <span className="text-xs font-medium">Đang tải danh sách người dùng từ Keycloak...</span>
          </div>
        ) : filteredUsers.length === 0 ? (
          <div className="p-12 text-center text-slate-500 text-xs">
            Không tìm thấy người dùng nào phù hợp với từ khóa tìm kiếm.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider text-[10px]">
                  <th className="py-3 px-4">Tài Khoản / Họ Tên</th>
                  <th className="py-3 px-4">Địa Chỉ Email</th>
                  <th className="py-3 px-4">Vai Trò Hệ Thống</th>
                  <th className="py-3 px-4">Trạng Thái</th>
                  <th className="py-3 px-4 text-right">Phân Quyền Admin</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredUsers.map((u) => {
                  const isUpdating = updatingUserId === u.id;
                  return (
                    <tr key={u.id} className="hover:bg-slate-50/60 transition-colors">
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-2.5">
                          <div
                            className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs text-white shrink-0 ${
                              u.isAdmin ? 'bg-amber-600' : 'bg-slate-800'
                            }`}
                          >
                            {(u.firstName?.[0] || u.username[0] || 'U').toUpperCase()}
                          </div>
                          <div>
                            <span className="font-bold text-slate-900 block">{u.username}</span>
                            <span className="text-[11px] text-slate-500">{u.fullName}</span>
                          </div>
                        </div>
                      </td>
                      <td className="py-3 px-4 text-slate-600">
                        <div className="flex items-center gap-1.5">
                          <Mail className="w-3.5 h-3.5 text-slate-400" />
                          <span>{u.email}</span>
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-1.5 flex-wrap">
                          {u.isAdmin ? (
                            <span className="inline-flex items-center gap-1 text-[11px] font-bold bg-amber-50 text-amber-800 border border-amber-300 px-2 py-0.5 rounded shadow-2xs">
                              <ShieldCheck className="w-3 h-3 text-amber-600" />
                              Quản trị viên (Admin)
                            </span>
                          ) : (
                            <span className="inline-flex items-center gap-1 text-[11px] font-medium bg-slate-100 text-slate-700 border border-slate-200 px-2 py-0.5 rounded">
                              <User className="w-3 h-3 text-slate-500" />
                              Học viên (User)
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <span className="inline-flex items-center gap-1 text-[11px] font-medium text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                          <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                          Hoạt động
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <button
                          type="button"
                          disabled={isUpdating || u.username === 'admin'}
                          onClick={() => handleToggleAdmin(u)}
                          className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed ${
                            u.isAdmin
                              ? 'bg-rose-50 text-rose-700 hover:bg-rose-100 border border-rose-200'
                              : 'bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200'
                          }`}
                          title={
                            u.username === 'admin'
                              ? 'Tài khoản Root Admin mặc định không thể hạ quyền'
                              : u.isAdmin
                              ? 'Hạ quyền xuống học viên thông thường'
                              : 'Cấp quyền Quản trị viên'
                          }
                        >
                          {isUpdating ? (
                            <Loader2 className="w-3.5 h-3.5 animate-spin" />
                          ) : u.isAdmin ? (
                            <ShieldAlert className="w-3.5 h-3.5 text-rose-600" />
                          ) : (
                            <ShieldCheck className="w-3.5 h-3.5 text-blue-600" />
                          )}
                          <span>
                            {isUpdating
                              ? 'Đang lưu...'
                              : u.isAdmin
                              ? 'Hạ quyền Admin'
                              : 'Cấp quyền Admin'}
                          </span>
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
