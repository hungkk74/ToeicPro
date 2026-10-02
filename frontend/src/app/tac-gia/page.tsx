import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { ExternalLink } from 'lucide-react';

export const metadata = {
  title: 'Tác giả | TOEIC Pro',
  description: 'Tác giả Nguyễn Kiều Hưng - 1 dự án chân thật',
};

export default function AuthorPage() {
  const facebookUrl = 'https://www.facebook.com/hung.kiu.165/';

  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col">
      <Navbar />

      <main className="w-full pt-32 pb-20 flex-1 flex items-center justify-center">
        <div className="max-w-md w-full mx-auto px-4 text-center">
          <div className="bg-white rounded-3xl p-8 sm:p-10 shadow-lg shadow-slate-200/60 border border-slate-200/80 flex flex-col items-center space-y-4">
            <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
              Nguyễn Kiều Hưng
            </h1>

            <p className="text-sm font-medium text-slate-500 italic">
              1 dự án chân thật
            </p>

            <div className="pt-2 w-full">
              <a
                href={facebookUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center justify-center gap-2.5 w-full px-5 py-3 bg-[#1877F2] hover:bg-[#166fe5] text-white text-sm font-semibold rounded-xl shadow-md shadow-[#1877F2]/25 transition-all hover:scale-105 active:scale-95"
              >
                <svg className="w-4 h-4 fill-current shrink-0" viewBox="0 0 24 24">
                  <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                </svg>
                <span>Facebook: Nguyễn Kiều Hưng</span>
                <ExternalLink className="w-4 h-4 opacity-80" />
              </a>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
