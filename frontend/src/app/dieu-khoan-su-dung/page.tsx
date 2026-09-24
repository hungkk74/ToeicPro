import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'Điều khoản sử dụng | TOEIC Pro',
};

export default function TermsOfUsePage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 bg-white p-8 sm:p-12 rounded-2xl shadow-sm border border-slate-200">
          <h1 className="text-3xl font-extrabold text-slate-900 mb-8">Điều Khoản Sử Dụng</h1>
          
          <div className="space-y-6 text-slate-600 leading-relaxed">
            <p>Chào mừng bạn đến với hệ thống TOEIC Pro. Bằng việc đăng ký tài khoản và sử dụng dịch vụ, bạn đồng ý với các điều khoản dưới đây:</p>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">1. Bản Quyền Nội Dung</h2>
              <p>Mọi nội dung (bài thi, hình ảnh, âm thanh, giải thích) trên TOEIC Pro đều thuộc bản quyền của hệ thống. Nghiêm cấm mọi hành vi sao chép, phát tán, hoặc sử dụng cho mục đích thương mại khi chưa có sự cho phép.</p>
            </section>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">2. Trách Nhiệm Người Dùng</h2>
              <p>Người dùng phải cung cấp thông tin chính xác khi đăng ký, có trách nhiệm bảo mật tài khoản cá nhân. Không được chia sẻ tài khoản cho nhiều người sử dụng cùng lúc.</p>
            </section>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
