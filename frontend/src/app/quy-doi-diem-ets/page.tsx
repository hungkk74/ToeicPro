import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'Phương pháp quy đổi điểm ETS | TOEIC Pro',
  description: 'Bảng quy đổi điểm thi TOEIC chuẩn của viện khảo thí giáo dục Hoa Kỳ ETS.',
};

export default function ScoreConversionPage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 bg-white p-8 sm:p-12 rounded-2xl shadow-sm border border-slate-200">
          <h1 className="text-3xl font-extrabold text-slate-900 mb-8">Phương Pháp Quy Đổi Điểm ETS</h1>
          
          <div className="space-y-6 text-slate-600 leading-relaxed">
            <p>
              Bảng quy đổi điểm TOEIC là công cụ giúp thí sinh tính được điểm số dự kiến của mình sau khi làm bài thi. Nền tảng TOEIC Pro áp dụng thuật toán chấm điểm và quy đổi mô phỏng chính xác nhất theo tiêu chuẩn của ETS (Viện Khảo thí Giáo dục Hoa Kỳ).
            </p>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">Thang Điểm</h2>
              <ul className="list-disc pl-5 space-y-2">
                <li>Phần thi Nghe (Listening): 100 câu hỏi, điểm tối đa 495 điểm.</li>
                <li>Phần thi Đọc (Reading): 100 câu hỏi, điểm tối đa 495 điểm.</li>
                <li>Tổng điểm TOEIC cao nhất là 990 điểm.</li>
              </ul>
              <p className="mt-4">
                Điểm số không được cộng dồn tuyến tính (ví dụ: mỗi câu không phải là 5 điểm). Hệ số quy đổi sẽ phụ thuộc vào độ khó của từng bộ đề do ETS quy định. Nền tảng của chúng tôi sẽ tự động áp dụng hệ số này ngay khi bạn nộp bài.
              </p>
            </section>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
