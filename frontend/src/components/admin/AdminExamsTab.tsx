'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import {
  FileText,
  Search,
  Plus,
  Eye,
  CheckCircle2,
  Clock,
  HelpCircle,
  Trash2,
} from 'lucide-react';
import { deleteExamInBackend, fetchExamsFromBackend } from '@/services/examService';
import { ExamAdminItem } from '@/constants/mockAdminExams';
import { AdminExamDetailModal } from '@/components/admin/AdminExamDetailModal';

export default function AdminExamsTab() {
  const [exams, setExams] = useState<ExamAdminItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedExamForModal, setSelectedExamForModal] = useState<ExamAdminItem | null>(null);

  useEffect(() => {
    async function loadData() {
      try {
        const data = await fetchExamsFromBackend();
        const mapped: ExamAdminItem[] = data.map((d) => ({
          id: d.id,
          title: d.title,
          category: d.category || 'Chưa phân loại',
          year: d.createdAt ? new Date(d.createdAt).getFullYear().toString() : '2026',
          totalQuestions: d.totalQuestions,
          durationMinutes: d.durationMinutes,
          attemptsCount: 0,
          status: d.isPublished !== false ? 'published' : 'draft',
          partsDetail: d.parts
            ? d.parts.map((p) => ({ part: p.partNumber, name: p.name, questions: p.totalQuestions }))
            : [],
        }));
        setExams(mapped);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const handleDeleteExam = async (id: number, title: string) => {
    if (window.confirm(`Bạn có chắc chắn muốn xoá đề thi "${title}" không? Hành động này không thể hoàn tác.`)) {
      try {
        await deleteExamInBackend(id);
        setExams((prev) => prev.filter((exam) => exam.id !== id));
        alert('Đã xoá đề thi thành công trên hệ thống!');
      } catch (error: any) {
        console.error('Lỗi khi xoá đề thi:', error);
        alert(`Lỗi Backend khi xoá đề thi: ${error.message}\n(Có thể do rào cản khoá ngoại - đề thi đã có câu hỏi hoặc đã có người thi nên không thể xoá, hoặc bạn chưa đăng nhập tài khoản Admin).`);
      }
    }
  };

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
                      <button
                        type="button"
                        onClick={() => handleDeleteExam(exam.id, exam.title)}
                        className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors cursor-pointer"
                        title="Xoá đề thi"
                      >
                        <Trash2 className="w-4 h-4" />
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
        <AdminExamDetailModal
          exam={selectedExamForModal}
          onClose={() => setSelectedExamForModal(null)}
        />
      )}
    </div>
  );
}
