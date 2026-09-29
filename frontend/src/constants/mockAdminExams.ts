export interface ExamAdminItem {
  id: number;
  title: string;
  category: string;
  year: string;
  totalQuestions: number;
  durationMinutes: number;
  attemptsCount: number;
  status: 'published' | 'draft';
  partsDetail: { part: number; name: string; questions: number }[];
}

export const MOCK_ADMIN_EXAMS: ExamAdminItem[] = [
  {
    id: 1,
    title: 'ETS TOEIC 2026 - Test 01 (Official ETS Standard)',
    category: 'ETS Format',
    year: '2026',
    totalQuestions: 200,
    durationMinutes: 120,
    attemptsCount: 428,
    status: 'published',
    partsDetail: [
      { part: 1, name: 'Photographs', questions: 6 },
      { part: 2, name: 'Question-Response', questions: 25 },
      { part: 3, name: 'Short Conversations', questions: 39 },
      { part: 4, name: 'Short Talks', questions: 30 },
      { part: 5, name: 'Incomplete Sentences', questions: 30 },
      { part: 6, name: 'Text Completion', questions: 16 },
      { part: 7, name: 'Reading Comprehension', questions: 54 },
    ],
  },
  {
    id: 2,
    title: 'ETS TOEIC 2026 - Test 02 (Full Listening & Reading)',
    category: 'ETS Format',
    year: '2026',
    totalQuestions: 200,
    durationMinutes: 120,
    attemptsCount: 312,
    status: 'published',
    partsDetail: [
      { part: 1, name: 'Photographs', questions: 6 },
      { part: 2, name: 'Question-Response', questions: 25 },
      { part: 3, name: 'Short Conversations', questions: 39 },
      { part: 4, name: 'Short Talks', questions: 30 },
      { part: 5, name: 'Incomplete Sentences', questions: 30 },
      { part: 6, name: 'Text Completion', questions: 16 },
      { part: 7, name: 'Reading Comprehension', questions: 54 },
    ],
  },
  {
    id: 3,
    title: 'ETS TOEIC 2024 - Test 01 (Cọ Xát Đề Thi Thật)',
    category: 'ETS Format',
    year: '2024',
    totalQuestions: 200,
    durationMinutes: 120,
    attemptsCount: 284,
    status: 'published',
    partsDetail: [
      { part: 1, name: 'Photographs', questions: 6 },
      { part: 2, name: 'Question-Response', questions: 25 },
      { part: 3, name: 'Short Conversations', questions: 39 },
      { part: 4, name: 'Short Talks', questions: 30 },
      { part: 5, name: 'Incomplete Sentences', questions: 30 },
      { part: 6, name: 'Text Completion', questions: 16 },
      { part: 7, name: 'Reading Comprehension', questions: 54 },
    ],
  },
  {
    id: 4,
    title: 'Hacker TOEIC 2024 - Actual Test 01 (Độ Khó Cao 800+)',
    category: 'Hacker TOEIC',
    year: '2024',
    totalQuestions: 200,
    durationMinutes: 120,
    attemptsCount: 176,
    status: 'published',
    partsDetail: [
      { part: 1, name: 'Photographs', questions: 6 },
      { part: 2, name: 'Question-Response', questions: 25 },
      { part: 3, name: 'Short Conversations', questions: 39 },
      { part: 4, name: 'Short Talks', questions: 30 },
      { part: 5, name: 'Incomplete Sentences', questions: 30 },
      { part: 6, name: 'Text Completion', questions: 16 },
      { part: 7, name: 'Reading Comprehension', questions: 54 },
    ],
  },
];
