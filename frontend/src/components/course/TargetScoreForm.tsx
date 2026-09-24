'use client';

import { useState } from 'react';
import { Target, ArrowRight } from 'lucide-react';

export default function TargetScoreForm() {
  const [currentScore, setCurrentScore] = useState('');
  const [targetScore, setTargetScore] = useState('');
  const [showRecommendation, setShowRecommendation] = useState(false);

  const handleRecommend = (e: React.FormEvent) => {
    e.preventDefault();
    if (currentScore && targetScore) {
      setShowRecommendation(true);
    }
  };

  return (
    <div className="bg-white rounded-2xl p-6 sm:p-8 shadow-sm border border-blue-100 bg-gradient-to-br from-white to-blue-50/50">
      <div className="flex items-center gap-3 mb-6">
        <div className="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center text-blue-600">
          <Target className="w-5 h-5" />
        </div>
        <div>
          <h2 className="text-xl font-bold text-slate-900">Tư Vấn Lộ Trình Cá Nhân Hóa</h2>
          <p className="text-sm text-slate-500">Nhập điểm hiện tại và mục tiêu để nhận được tư vấn từ giáo viên</p>
        </div>
      </div>

      <form onSubmit={handleRecommend} className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1">
          <label className="block text-sm font-medium text-slate-700 mb-1.5">Điểm TOEIC hiện tại (ước tính)</label>
          <input
            type="number"
            min="0"
            max="990"
            required
            value={currentScore}
            onChange={(e) => setCurrentScore(e.target.value)}
            className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-shadow"
            placeholder="Ví dụ: 300"
          />
        </div>
        <div className="flex-1">
          <label className="block text-sm font-medium text-slate-700 mb-1.5">Điểm mục tiêu mong muốn</label>
          <input
            type="number"
            min="0"
            max="990"
            required
            value={targetScore}
            onChange={(e) => setTargetScore(e.target.value)}
            className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-shadow"
            placeholder="Ví dụ: 650"
          />
        </div>
        <div className="flex items-end">
          <button
            type="submit"
            className="w-full sm:w-auto h-[46px] px-6 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium transition-colors flex items-center justify-center gap-2"
          >
            Nhận Lộ Trình <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </form>

      {showRecommendation && (
        <div className="mt-6 p-4 rounded-xl border border-green-200 bg-green-50 animate-in fade-in slide-in-from-bottom-2 duration-300">
          <h3 className="font-semibold text-green-800 mb-1">Đã gửi yêu cầu thành công!</h3>
          <p className="text-sm text-green-700">
            Giáo viên chuyên môn của chúng tôi đã ghi nhận thông tin (Mục tiêu tăng {Math.max(0, Number(targetScore) - Number(currentScore))} điểm) và sẽ liên hệ trực tiếp với bạn trong thời gian sớm nhất để tư vấn lộ trình học tối ưu nhất.
          </p>
        </div>
      )}
    </div>
  );
}
