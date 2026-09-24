import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'Quy chế khảo thí | TOEIC Pro',
  description: 'Các quy định và quy chế khảo thí khi tham gia thi thử TOEIC trên hệ thống.',
};

export default function ExamRegulationsPage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 bg-white p-8 sm:p-12 rounded-2xl shadow-sm border border-slate-200">
          <h1 className="text-3xl font-extrabold text-slate-900 mb-8">Quy Chế Khảo Thí</h1>
          
          <div className="space-y-6 text-slate-600 leading-relaxed">
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">1. Quy định chung</h2>
              <p>Học viên cần đảm bảo kết nối internet ổn định và thiết bị (máy tính, tai nghe) hoạt động tốt trước khi bắt đầu bài thi. Hệ thống sẽ tính giờ tự động ngay khi học viên chọn "Bắt đầu làm bài".</p>
            </section>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">2. Trong quá trình thi</h2>
              <ul className="list-disc pl-5 space-y-2">
                <li>Phần thi Nghe (Listening) sẽ phát audio liên tục, không được phép tạm dừng hoặc tua lại để đảm bảo tính công bằng như thi thật.</li>
                <li>Không thoát toàn màn hình hoặc mở tab khác trong quá trình thi. Hệ thống sẽ cảnh báo nếu phát hiện gian lận.</li>
                <li>Bài thi sẽ tự động nộp khi hết thời gian quy định (120 phút).</li>
              </ul>
            </section>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
