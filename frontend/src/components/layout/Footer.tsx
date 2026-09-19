export default function Footer() {
  return (
    <footer className="border-t border-border-subtle bg-surface py-8 mt-auto">
      <div className="max-w-7xl mx-auto px-margin-desktop flex flex-col sm:flex-row items-center justify-between gap-4 text-caption text-text-secondary font-caption">
        <p>© 2026 Nền tảng ToeicPro. Hệ thống Đào tạo & Thi thử TOEIC Trực tuyến.</p>
        <div className="flex items-center gap-space-md">
          <span className="hover:text-primary cursor-pointer">Quy chế Khảo thí</span>
          <span>•</span>
          <span className="hover:text-primary cursor-pointer">Phương pháp Quy đổi Điểm ETS</span>
          <span>•</span>
          <span className="hover:text-primary cursor-pointer">Hạ tầng Âm thanh Cloudflare R2</span>
        </div>
      </div>
    </footer>
  );
}
