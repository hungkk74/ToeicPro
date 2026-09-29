export interface CefrInfo {
  level: string;
  title: string;
  desc: string;
  color: string;
}

export function formatTimeSpent(sec?: number): string {
  if (sec == null || sec <= 0) return 'Dưới 1 phút';
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  if (m === 0) return `${s} giây`;
  if (s === 0) return `${m} phút`;
  return `${m} phút ${s} giây`;
}

export function getCefrBadge(score: number, max: number): CefrInfo {
  const normalized = max > 0 ? (score / max) * 990 : score;
  if (normalized >= 850) {
    return {
      level: 'C1',
      title: 'C1 - Cao cấp',
      desc: 'Có khả năng giao tiếp thành thạo, làm việc chuyên môn cao.',
      color: 'text-emerald-700 bg-emerald-50 border-emerald-200',
    };
  }
  if (normalized >= 785) {
    return {
      level: 'B2',
      title: 'B2 - Thành thạo',
      desc: 'Đạt tiêu chuẩn tuyển dụng của hầu hết các tập đoàn đa quốc gia.',
      color: 'text-blue-700 bg-blue-50 border-blue-200',
    };
  }
  if (normalized >= 550) {
    return {
      level: 'B1',
      title: 'B1 - Độc lập',
      desc: 'Đạt chuẩn đầu ra Đại học, giao tiếp cơ bản công sở.',
      color: 'text-indigo-700 bg-indigo-50 border-indigo-200',
    };
  }
  return {
    level: 'A2',
    title: 'A2 - Cơ bản',
    desc: 'Cần củng cố thêm từ vựng và ngữ pháp nền tảng.',
    color: 'text-amber-700 bg-amber-50 border-amber-200',
  };
}
