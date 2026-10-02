'use client';

import React, { useRef, useState, MouseEvent } from 'react';
import Link from 'next/link';
import { Wrench, RefreshCw, Sparkles, ArrowLeft } from 'lucide-react';

interface Props {
  className?: string;
  showBackHome?: boolean;
}

export default function BackendUnavailableNotice({ className = '', showBackHome = false }: Props) {
  const cardRef = useRef<HTMLDivElement>(null);
  const [rotateX, setRotateX] = useState(0);
  const [rotateY, setRotateY] = useState(0);
  const [glarePos, setGlarePos] = useState({ x: 50, y: 50, opacity: 0 });
  const [isHovered, setIsHovered] = useState(false);

  const handleMouseMove = (e: MouseEvent<HTMLDivElement>) => {
    if (!cardRef.current) return;
    const rect = cardRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    const rotX = -((y - centerY) / centerY) * 14;
    const rotY = ((x - centerX) / centerX) * 14;

    setRotateX(rotX);
    setRotateY(rotY);
    setGlarePos({
      x: (x / rect.width) * 100,
      y: (y / rect.height) * 100,
      opacity: 0.35,
    });
  };

  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
    setRotateX(0);
    setRotateY(0);
    setGlarePos((prev) => ({ ...prev, opacity: 0 }));
  };

  return (
    <div className={`w-full flex items-center justify-center py-16 px-4 ${className}`}>
      {/* 3D Perspective Viewport */}
      <div
        className="w-full max-w-lg cursor-pointer select-none"
        style={{ perspective: '1000px' }}
        onMouseMove={handleMouseMove}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        <div
          ref={cardRef}
          className="relative rounded-3xl p-8 sm:p-10 bg-gradient-to-br from-white via-slate-50 to-blue-50/60 border border-slate-200/90 overflow-hidden transition-transform duration-200 ease-out"
          style={{
            transform: `rotateX(${rotateX}deg) rotateY(${rotateY}deg) ${isHovered ? 'scale3d(1.03, 1.03, 1.03)' : 'scale3d(1, 1, 1)'}`,
            transformStyle: 'preserve-3d',
            boxShadow: isHovered
              ? `${-rotateY * 2.5}px ${rotateX * 2.5 + 24}px 40px -8px rgba(30, 58, 138, 0.2), 0 10px 15px -3px rgba(0, 0, 0, 0.05)`
              : '0 12px 30px -5px rgba(0, 0, 0, 0.06), 0 8px 10px -6px rgba(0, 0, 0, 0.04)',
          }}
        >
          {/* 3D Dynamic Specular Light */}
          <div
            className="pointer-events-none absolute inset-0 rounded-3xl transition-opacity duration-300"
            style={{
              background: `radial-gradient(circle at ${glarePos.x}% ${glarePos.y}%, rgba(255, 255, 255, 0.85) 0%, rgba(255, 255, 255, 0) 65%)`,
              opacity: glarePos.opacity,
            }}
          />

          {/* Decorative Background Orbs */}
          <div className="absolute -top-10 -right-10 w-36 h-36 bg-blue-500/10 rounded-full blur-2xl pointer-events-none" />
          <div className="absolute -bottom-10 -left-10 w-36 h-36 bg-amber-500/10 rounded-full blur-2xl pointer-events-none" />

          {/* Floating 3D Content Layers */}
          <div
            className="flex flex-col items-center text-center relative z-10"
            style={{ transform: 'translateZ(40px)', transformStyle: 'preserve-3d' }}
          >
            {/* 3D Icon */}
            <div
              className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-amber-500 to-orange-400 text-white flex items-center justify-center shadow-lg shadow-amber-500/30 mb-5 transition-transform duration-300"
              style={{
                transform: `translateZ(65px) ${isHovered ? 'scale(1.12) rotate(8deg)' : 'scale(1)'}`,
              }}
            >
              <Wrench className="w-8 h-8 animate-pulse" />
            </div>

            {/* Badge */}
            <div
              className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-800 border border-amber-200/80 mb-4 shadow-sm"
              style={{ transform: 'translateZ(35px)' }}
            >
              <Sparkles className="w-3.5 h-3.5 text-amber-500" />
              <span>Chưa kết nối Backend</span>
            </div>

            {/* Main Message */}
            <h2
              className="text-lg sm:text-xl font-bold text-slate-800 tracking-tight leading-snug mb-3"
              style={{ transform: 'translateZ(55px)' }}
            >
              xin lỗi bạn, tác giả đang bận đi làm nên chưa kịp fix
            </h2>

            {/* Subtitle */}
            <p
              className="text-sm text-slate-500 max-w-sm mb-6 leading-relaxed"
              style={{ transform: 'translateZ(25px)' }}
            >
              Dữ liệu đề thi từ Backend Microservices chưa sẵn sàng. Vui lòng thử lại sau khi hệ thống hoàn tất khởi chạy!
            </p>

            {/* 3D Actions */}
            <div
              className="flex items-center gap-3"
              style={{ transform: 'translateZ(50px)' }}
            >
              <button
                type="button"
                onClick={() => window.location.reload()}
                className="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-600/25 transition-all hover:scale-105 active:scale-95"
              >
                <RefreshCw className="w-4 h-4" />
                <span>Thử tải lại</span>
              </button>

              {showBackHome && (
                <Link
                  href="/de-thi"
                  className="inline-flex items-center gap-2 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-sm font-medium rounded-xl transition-all hover:scale-105 active:scale-95"
                >
                  <ArrowLeft className="w-4 h-4" />
                  <span>Về danh sách đề</span>
                </Link>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
