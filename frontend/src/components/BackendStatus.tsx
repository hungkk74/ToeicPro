'use client';

import { useState, useEffect } from 'react';
import { getBaseApiUrl } from '@/lib/api-client';

export default function BackendStatus() {
  const [status, setStatus] = useState<'checking' | 'connected' | 'offline'>('checking');

  useEffect(() => {
    const checkBackend = async () => {
      try {
        const baseUrl = getBaseApiUrl();
        const res = await fetch(`${baseUrl}/management/health`, {
          method: 'GET',
          headers: { Accept: 'application/json' },
        });
        if (res.ok) {
          setStatus('connected');
        } else {
          setStatus('offline');
        }
      } catch {
        setStatus('offline');
      }
    };

    checkBackend();
    const interval = setInterval(checkBackend, 15000);
    return () => clearInterval(interval);
  }, []);

  const config = {
    checking: {
      color: 'bg-amber-400 animate-pulse',
      ring: 'ring-amber-200',
      label: 'Đang kết nối Gateway (:8080)...',
    },
    connected: {
      color: 'bg-emerald-500',
      ring: 'ring-emerald-200',
      label: 'Hệ thống Gateway hoạt động bình thường (:8080)',
    },
    offline: {
      color: 'bg-slate-400',
      ring: 'ring-slate-200',
      label: 'Gateway ngoại tuyến (Đang chạy Mock Mode)',
    },
  }[status];

  return (
    <div className="relative group flex items-center">
      <button
        type="button"
        aria-label={config.label}
        className="relative flex items-center justify-center p-1 rounded-full hover:bg-slate-100 transition-colors focus:outline-none"
      >
        <span className={`w-2.5 h-2.5 rounded-full ${config.color} ring-2 ${config.ring}`}></span>
      </button>

      {/* Modern SaaS Tooltip */}
      <div className="absolute right-0 top-full mt-2 hidden group-hover:flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 text-white text-xs rounded-lg shadow-xl whitespace-nowrap z-50 pointer-events-none transition-all duration-150">
        <span className={`w-2 h-2 rounded-full ${config.color}`}></span>
        <span>{config.label}</span>
      </div>
    </div>
  );
}
