export type ExamCategory = 'all' | 'full' | 'mini' | 'listening' | 'reading';
export type ExamStatus = 'untaken' | 'in_progress' | 'completed';

export interface ExamItem {
  id: string;
  title: string;
  description: string;
  tag1: string;
  tag1Type: 'standard' | 'economy' | 'hackers' | 'special';
  tag2: string;
  tag2Type: 'normal' | 'hard' | 'popular';
  durationMinutes: number;
  totalQuestions: number;
  takenCount: number;
  averageScore: number;
  category: ExamCategory;
  source: string;
  targetScore: string;
  status: ExamStatus;
  userScore?: number;
  userProgress?: string;
  audioAccents?: string;
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
