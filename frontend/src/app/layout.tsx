import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'TOEIC Pro - Hệ Thống Luyện Thi & Thi Thử TOEIC Trực Tuyến',
  description: 'Nền tảng luyện thi TOEIC chuẩn format ETS mới nhất với ngân hàng đề thi phong phú, chấm điểm tự động và phân tích AI chi tiết.',
  keywords: ['TOEIC Pro', 'Luyện thi TOEIC', 'Thi thử TOEIC online', 'TOEIC ETS', 'Học TOEIC online'],
  authors: [{ name: 'TOEIC Pro Team' }],
  icons: {
    icon: '/icon.png',
    shortcut: '/icon.png',
    apple: '/icon.png',
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="vi">
      <body>
        <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
          {children}
        </div>
      </body>
    </html>
  );
}
