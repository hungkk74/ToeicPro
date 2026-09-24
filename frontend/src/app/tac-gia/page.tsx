import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'Tác giả & Đội ngũ | TOEIC Pro',
  description: 'Đội ngũ giáo viên và kỹ sư phát triển nền tảng TOEIC Pro.',
};

export default function AuthorPage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-4">Về Đội Ngũ Tác Giả</h1>
            <p className="text-slate-600 max-w-2xl mx-auto">
              TOEIC Pro được xây dựng bởi tâm huyết của các chuyên gia giáo dục và kỹ sư phần mềm hàng đầu, với mục tiêu mang lại giải pháp ôn thi hiệu quả nhất cho sinh viên và người đi làm.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
              <div className="w-16 h-16 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center font-bold text-2xl mb-4">
                👨‍🏫
              </div>
              <h3 className="text-xl font-bold text-slate-900 mb-2">Đội Ngũ Học Thuật</h3>
              <p className="text-slate-600">
                Các giảng viên sở hữu chứng chỉ TOEIC 950+ với nhiều năm kinh nghiệm giảng dạy. Trực tiếp biên soạn, số hóa và giải thích chi tiết hàng ngàn câu hỏi sát với đề thi thật của ETS.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
              <div className="w-16 h-16 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center font-bold text-2xl mb-4">
                💻
              </div>
              <h3 className="text-xl font-bold text-slate-900 mb-2">Đội Ngũ Kỹ Sư</h3>
              <p className="text-slate-600">
                Nhóm phát triển phần mềm sử dụng các công nghệ tiên tiến nhất (Next.js, Spring Boot, Microservices) để mang lại trải nghiệm thi trực tuyến mượt mà, chịu tải cao và phân tích dữ liệu thông minh.
              </p>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
