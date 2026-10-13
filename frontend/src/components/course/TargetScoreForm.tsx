'use client';

import { useState } from 'react';
import { Target } from 'lucide-react';

export default function TargetScoreForm() {
  const [currentScore, setCurrentScore] = useState('');
  const [targetScore, setTargetScore] = useState('');
  const [recommendation, setRecommendation] = useState<{
    gap: number;
    stage: string;
    description: string;
  } | null>(null);

  const handleRecommend = (e: React.FormEvent) => {
    e.preventDefault();
    const curr = Number(currentScore);
    const target = Number(targetScore);
    if (!Number.isFinite(curr) || !Number.isFinite(target)) return;

    const gap = Math.max(0, target - curr);
    let stage = 'Lộ trình Nền tảng (500–650+)';
    let description = 'Tập trung ngữ pháp cốt lõi Part 5 & 6, 600 từ vựng và phản xạ nghe tranh ảnh Part 1 & hỏi đáp Part 2.';

    if (target > 850) {
      stage = 'Lộ trình Chuyên sâu & Luyện đề ETS';
      description = 'Rèn chiến thuật bẫy đề nghe Part 3 & 4 đa ngữ điệu, tối ưu tốc độ đọc hiểu văn bản phức hợp Part 7.';
    } else if (target > 650) {
      stage = 'Lộ trình Bứt phá (750–850+)';
      description = 'Nâng cao kỹ năng nghe hiểu đoạn văn hội thoại, làm chủ ngữ pháp nâng cao và kỹ thuật đọc quét skimming/scanning.';
    }

    setRecommendation({ gap, stage, description });
  };

  const scrollToCourses = () => {
    const el = document.getElementById('course-catalog-section');
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className="bg-white rounded-xl p-6 sm:p-7 border border-slate-200/90 shadow-xs">
      <div className="flex items-center gap-3 mb-5">
        <div className="w-9 h-9 rounded-lg bg-blue-50 border border-blue-200/60 flex items-center justify-center text-blue-600">
          <Target className="w-4 h-4" />
        </div>
        <div>
          <h2 className="text-lg font-semibold text-slate-950 font-serif">Xác Định Lộ Trình Mục Tiêu</h2>
          <p className="text-xs text-slate-500">Đối chiếu khoảng cách điểm số và gợi ý khối kiến thức trọng tâm cần tập trung</p>
        </div>
      </div>

      <form onSubmit={handleRecommend} className="grid grid-cols-1 sm:grid-cols-12 gap-3.5 items-end">
        <div className="sm:col-span-5 space-y-1">
          <label className="block text-xs font-medium text-slate-700">Điểm hiện tại (ước tính)</label>
          <input
            type="number"
            min="0"
            max="990"
            required
            value={currentScore}
            onChange={(e) => setCurrentScore(e.target.value)}
            className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
            placeholder="Ví dụ: 450"
          />
        </div>
        <div className="sm:col-span-5 space-y-1">
          <label className="block text-xs font-medium text-slate-700">Điểm mục tiêu</label>
          <input
            type="number"
            min="0"
            max="990"
            required
            value={targetScore}
            onChange={(e) => setTargetScore(e.target.value)}
            className="w-full px-3.5 py-2 rounded-lg border border-slate-200 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
            placeholder="Ví dụ: 750"
          />
        </div>
        <div className="sm:col-span-2">
          <button
            type="submit"
            className="w-full h-[38px] px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white rounded-lg text-xs font-medium transition-colors cursor-pointer shadow-xs"
          >
            Đánh giá
          </button>
        </div>
      </form>

      {recommendation && (
        <div className="mt-5 p-4 rounded-lg border border-slate-200 bg-slate-50 space-y-2">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <span className="text-xs font-semibold text-slate-900 uppercase tracking-wider">
              {recommendation.stage}
            </span>
            <span className="text-xs text-slate-600">
              Khoảng cách: <strong className="text-blue-700 tabular-nums">+{recommendation.gap} điểm</strong>
            </span>
          </div>
          <p className="text-xs text-slate-600 leading-relaxed">
            {recommendation.description}
          </p>
          <div className="pt-1">
            <button
              type="button"
              onClick={scrollToCourses}
              className="text-xs font-medium text-blue-600 hover:text-blue-700 hover:underline cursor-pointer"
            >
              Xem danh sách khóa học phù hợp bên dưới
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
