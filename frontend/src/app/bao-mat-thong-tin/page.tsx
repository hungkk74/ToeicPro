import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'Bảo mật thông tin | TOEIC Pro',
};

export default function PrivacyPolicyPage() {
  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />
      <main className="w-full pt-28 pb-16 flex-1">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 bg-white p-8 sm:p-12 rounded-2xl shadow-sm border border-slate-200">
          <h1 className="text-3xl font-extrabold text-slate-900 mb-8">Chính Sách Bảo Mật Thông Tin</h1>
          
          <div className="space-y-6 text-slate-600 leading-relaxed">
            <p>TOEIC Pro coi trọng việc bảo vệ dữ liệu cá nhân của người dùng. Chính sách này mô tả cách chúng tôi thu thập và sử dụng dữ liệu của bạn.</p>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">1. Thu Thập Dữ Liệu</h2>
              <p>Chúng tôi chỉ thu thập các thông tin cần thiết phục vụ cho việc học tập: Email đăng nhập, lịch sử làm bài, điểm số, và tiến độ học tập để cá nhân hóa lộ trình cho bạn.</p>
            </section>
            
            <section>
              <h2 className="text-xl font-bold text-slate-800 mb-3">2. Lưu Trữ và Bảo Vệ</h2>
              <p>Dữ liệu được mã hóa và lưu trữ an toàn trên máy chủ của chúng tôi. Chúng tôi cam kết không bán, trao đổi hoặc cung cấp thông tin cá nhân của bạn cho bất kỳ bên thứ ba nào vì mục đích quảng cáo.</p>
            </section>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
