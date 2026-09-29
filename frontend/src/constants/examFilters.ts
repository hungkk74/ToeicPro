import { ExamCategory, FilterState } from '@/types/examList';

export interface DropdownOption {
  value: string;
  label: string;
}

export const CATEGORY_TABS: { key: ExamCategory; label: string }[] = [
  { key: 'all', label: 'Tất cả đề' },
  { key: 'full', label: 'Đề đầy đủ (200 câu)' },
  { key: 'listening', label: 'Nghe (Part 1–4)' },
  { key: 'reading', label: 'Đọc (Part 5–7)' },
];

export const SORT_OPTIONS: { value: FilterState['sortBy']; label: string }[] = [
  { value: 'recent', label: 'Mới nhất' },
  { value: 'popular', label: 'Lượt thi nhiều nhất' },
  { value: 'hardest', label: 'Độ khó cao nhất' },
];

export const SOURCE_OPTIONS: DropdownOption[] = [
  { value: 'all', label: 'Tất cả nguồn' },
  { value: 'ETS Authentic', label: 'ETS Authentic 2026' },
  { value: 'Economy', label: 'Economy Toeic' },
  { value: 'Hackers', label: 'Hackers Practice' },
  { value: 'ETS Authentic 2023', label: 'ETS Authentic 2023' },
];

export const SCORE_OPTIONS: DropdownOption[] = [
  { value: 'all', label: 'Tất cả mức điểm' },
  { value: '550+', label: 'Mục tiêu 550+ (Cơ bản)' },
  { value: '750+', label: 'Mục tiêu 750+ (Khá giỏi)' },
  { value: '850+', label: 'Mục tiêu 850+ (Chuyên sâu)' },
  { value: '900+', label: 'Mục tiêu 900+ (Mastery)' },
];

export const STATUS_OPTIONS: DropdownOption[] = [
  { value: 'all', label: 'Tất cả trạng thái' },
  { value: 'untaken', label: 'Chưa làm' },
  { value: 'in_progress', label: 'Đang làm dở' },
  { value: 'completed', label: 'Đã làm' },
];
