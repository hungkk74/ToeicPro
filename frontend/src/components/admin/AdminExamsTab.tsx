'use client';

import { useState } from 'react';
import Link from 'next/link';
import {
  FileText,
  Search,
  Plus,
  Eye,
  CheckCircle2,
  Clock,
  HelpCircle,
  X,
  Sparkles,
} from 'lucide-react';

interface ExamAdminItem {
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

const MOCK_ADMIN_EXAMS: ExamAdminItem[] = [
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
  {
    id: 5,
    title: 'Mini Test Nhanh 30 Phút - Đánh Giá Năng Lực Khởi Đầu',
    category: 'Mini Test',
    year: '2026',
    totalQuestions: 50,
    durationMinutes: 30,
    attemptsCount: 80,
    status: 'published',
    partsDetail: [
      { part: 1, name: 'Photographs', questions: 3 },
      { part: 2, name: 'Question-Response', questions: 7 },
      { part: 3, name: 'Short Conversations', questions: 10 },
      { part: 5, name: 'Incomplete Sentences', questions: 15 },
      { part: 7, name: 'Reading Comprehension', questions: 15 },
    ],
  },
];

export default function AdminExamsTab() {
  const [exams, setExams] = useState<ExamAdminItem[]>(MOCK_ADMIN_EXAMS);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedExamForModal, setSelectedExamForModal] = useState<ExamAdminItem | null>(null);

  const filteredExams = exams.filter((e) =>
    e.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    e.category.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-5">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200 shadow-2xs">
        <div className="flex items-center gap-2">
          <FileText className="w-5 h-5 text-blue-600" />
          <div>
            <h2 className="text-sm font-bold text-slate-900">Quản Lý Ngân Hàng Đề Thi</h2>
            <p className="text-[11px] text-slate-500">Đồng bộ với ExamService (:8082) qua API Gateway</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm kiếm tên đề..."
              className="pl-8 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg w-48 sm:w-60 focus:bg-white focus:outline-none focus:border-blue-600 transition-colors"
            />
          </div>

          <button
            type="button"
            onClick={() => alert('Chức năng nhập bộ đề mới (Import Excel/Audio JSON) qua ExamService sẽ được hỗ trợ trong phiên bản tới!')}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white font-medium text-xs rounded-lg shadow-xs transition-colors cursor-pointer"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Thêm Đề Mới</span>
          </button>
        </div>
      </div>

      {/* Data Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider text-[10px]">
                <th className="py-3 px-4">Tên Đề Thi</th>
                <th className="py-3 px-4">Phân Loại</th>
                <th className="py-3 px-4">Số Câu / Thời Gian</th>
                <th className="py-3 px-4">Lượt Nộp Bài</th>
                <th className="py-3 px-4">Trạng Thái</th>
                <th className="py-3 px-4 text-right">Thao Tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredExams.map((exam) => (
                <tr key={exam.id} className="hover:bg-slate-50/60 transition-colors">
                  <td className="py-3 px-4">
                    <span className="font-bold text-slate-900 block">{exam.title}</span>
                    <span className="text-[11px] text-slate-400">ETS Actual Standard Format • {exam.year}</span>
                  </td>
                  <td className="py-3 px-4">
                    <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-slate-100 text-slate-700 border border-slate-200">
                      {exam.category}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-slate-600">
                    <div className="flex items-center gap-1.5">
                      <HelpCircle className="w-3.5 h-3.5 text-slate-400" />
                      <span>{exam.totalQuestions} câu</span>
                      <span className="text-slate-300">•</span>
                      <Clock className="w-3.5 h-3.5 text-slate-400" />
                      <span>{exam.durationMinutes} phút</span>
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <span className="font-semibold text-blue-700 bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
                      {exam.attemptsCount} lượt
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span className="inline-flex items-center gap-1 text-[11px] font-medium text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                      <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                      Công khai
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        type="button"
                        onClick={() => setSelectedExamForModal(exam)}
                        className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors cursor-pointer"
                        title="Xem cấu trúc Parts"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <Link
                        href={`/exam/${exam.id}`}
                        target="_blank"
                        className="px-2.5 py-1 text-xs font-semibold text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                      >
                        Vào Thi Thử &rarr;
                      </Link>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Xem Cấu Trúc Chi Tiết Đề Thi */}
      {selectedExamForModal && (
        <div
          className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in"
          onClick={() => setSelectedExamForModal(null)}
        >
          <div
            className="bg-white rounded-2xl max-w-lg w-full border border-slate-200 shadow-2xl p-6 space-y-4"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-blue-600" />
                <h3 className="font-bold text-slate-900 text-sm">
                  Cấu Trúc Đề: {selectedExamForModal.title}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setSelectedExamForModal(null)}
                className="text-slate-400 hover:text-slate-700 p-1"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="space-y-2">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">
                Phân bố 7 phần thi TOEIC tiêu chuẩn:
              </span>
              <div className="divide-y divide-slate-100 border border-slate-200 rounded-xl overflow-hidden">
                {selectedExamForModal.partsDetail.map((p) => (
                  <div key={p.part} className="flex items-center justify-between p-2.5 text-xs">
                    <span className="font-semibold text-slate-800">Part {p.part}: {p.name}</span>
                    <span className="font-bold text-blue-600 bg-blue-50 px-2 py-0.5 rounded">
                      {p.questions} câu
                    </span>
                  </div>
                ))}
              </div>
            </div>

            <div className="pt-2 flex justify-end">
              <button
                type="button"
                onClick={() => setSelectedExamForModal(null)}
                className="px-4 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition-colors cursor-pointer"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
