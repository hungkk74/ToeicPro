export type ExamCategory = 'all' | 'full' | 'mini' | 'listening' | 'reading';
export type ExamStatus = 'untaken' | 'in_progress' | 'completed';

export interface ExamItem {
  id: string;
  title: string;
  description: string;
  tag1: string; // e.g. "ETS 2026"
  tag1Type: 'standard' | 'economy' | 'hackers' | 'special';
  tag2: string; // e.g. "Độ khó cao"
  tag2Type: 'normal' | 'hard' | 'popular';
  durationMinutes: number;
  totalQuestions: number;
  takenCount: number;
  averageScore: number;
  category: ExamCategory;
  source: string; // "ETS Authentic", "Economy", "Hackers"
  targetScore: string; // "550+", "750+", "850+", "900+"
  status: ExamStatus;
  userScore?: number; // e.g. 750 (nếu đã làm) -> "Đã làm: 750/990"
  userProgress?: string; // e.g. "85/200" (nếu đang làm dở) -> "Đang làm: 85/200"
  audioAccents?: string; // "4 Giọng đọc (US, UK, AU, CA)"
  listeningQuestions: number;
  readingQuestions: number;
}

export interface FilterState {
  category: ExamCategory;
  source: string;
  targetScore: string;
  status: string;
  sortBy: 'recent' | 'popular' | 'hardest';
  searchQuery: string;
}
