import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'Chính sách bảo hành | TOEIC Pro',
  description: 'Chính sách bảo hành khóa học và cam kết chất lượng tại TOEIC Pro.',
};

export default function WarrantyPolicyPage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 bg-white p-8 sm:p-12 rounded-2xl shadow-sm border border-slate-200">
          <h1 className="text-3xl font-extrabold text-slate-900 mb-8">Chính Sách Bảo Hành & Cam Kết</h1>
          
          <div className="space-y-6 text-slate-600 leading-relaxed">
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">1. Cam Kết Đầu Ra</h2>
              <p>
                TOEIC Pro cam kết 100% học viên tham gia khóa học đạt mục tiêu đề ra nếu tuân thủ đầy đủ lộ trình và hoàn thành tối thiểu 90% bài tập được giao. Nếu không đạt, học viên sẽ được học lại hoàn toàn miễn phí hoặc hoàn trả học phí theo quy định.
              </p>
            </section>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">2. Điều Kiện Áp Dụng</h2>
              <ul className="list-disc pl-5 space-y-2">
                <li>Hoàn thành tất cả các bài thi thử (Mock Test) bắt buộc.</li>
                <li>Học viên tham gia thi thật trong vòng 30 ngày kể từ khi kết thúc khóa học.</li>
                <li>Cung cấp bảng điểm thi thật hợp lệ từ IIG Việt Nam.</li>
              </ul>
            </section>

            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">3. Hỗ Trợ Kỹ Thuật</h2>
              <p>
                Trong quá trình sử dụng hệ thống thi thử trực tuyến, nếu gặp bất kỳ lỗi kỹ thuật nào (mất kết nối, lỗi audio, sai lệch điểm số), hệ thống cam kết khắc phục trong vòng 24 giờ và bù đắp các đặc quyền tương đương.
              </p>
            </section>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
