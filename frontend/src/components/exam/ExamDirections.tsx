interface ExamDirectionsProps {
  currentQuestion: number;
  totalQuestions?: number;
  partNumber?: number;
  partName?: string;
  hasAudio?: boolean;
  audioUrl?: string;
  playbackSpeed: string;
  onChangeSpeed: (spd: string) => void;
}

export default function ExamDirections({
  currentQuestion,
  totalQuestions = 200,
  partNumber = 1,
  partName,
  hasAudio = false,
  playbackSpeed,
  onChangeSpeed,
}: ExamDirectionsProps) {
  const isReading = partNumber >= 5;

  const getPartDirections = () => {
    switch (partNumber) {
      case 5:
        return {
          title: 'Hướng dẫn làm bài: Part 5 — Incomplete Sentences (Hoàn thành câu)',
          text: 'Một từ hoặc cụm từ bị thiếu trong mỗi câu. Bốn phương án trả lời (A, B, C, D) được đưa ra dưới mỗi câu. Hãy chọn đáp án chính xác nhất để hoàn chỉnh câu văn theo ngữ pháp và ngữ nghĩa chuẩn TOEIC.',
        };
      case 6:
        return {
          title: 'Hướng dẫn làm bài: Part 6 — Text Completion (Điền đoạn văn)',
          text: 'Đọc kỹ đoạn văn bản bên dưới. Các chỗ trống được đánh số thứ tự tương ứng với từng câu hỏi. Bạn hãy chọn từ, cụm từ hoặc câu hoàn chỉnh phù hợp nhất để hoàn thiện văn bản một cách mạch lạc.',
        };
      case 7:
        return {
          title: 'Hướng dẫn làm bài: Part 7 — Reading Comprehension (Đọc hiểu văn bản)',
          text: 'Đọc các văn bản đơn hoặc đoạn văn kép/ba (email, thông báo, hóa đơn, báo chí...). Sau đó chọn câu trả lời đúng nhất cho từng câu hỏi suy luận và tìm kiếm thông tin chi tiết.',
        };
      case 2:
        return {
          title: 'Hướng dẫn làm bài: Part 2 — Question-Response (Hỏi & Đáp)',
          text: 'Bạn sẽ nghe một câu hỏi hoặc phát biểu, sau đó là 3 phương án phản hồi (A, B, C). Chọn phương án phản hồi hợp lý và tự nhiên nhất cho câu hỏi.',
        };
      case 3:
        return {
          title: 'Hướng dẫn làm bài: Part 3 — Short Conversations (Hội thoại ngắn)',
          text: 'Bạn sẽ nghe một đoạn hội thoại giữa 2 hoặc nhiều người. Với mỗi đoạn hội thoại, hãy trả lời 3 câu hỏi liên quan dựa trên thông tin nghe được.',
        };
      case 4:
        return {
          title: 'Hướng dẫn làm bài: Part 4 — Short Talks (Bài nói ngắn)',
          text: 'Bạn sẽ nghe một bài nói ngắn (thông báo sân bay, quảng cáo, dự báo thời tiết...). Hãy lắng nghe cẩn thận và trả lời 3 câu hỏi cho mỗi bài nói.',
        };
      case 1:
      default:
        return {
          title: 'Hướng dẫn làm bài: Part 1 — Photographs (Mô tả Tranh)',
          text: 'Trong phần này, bạn sẽ nghe 4 câu mô tả về một bức tranh hiển thị trên màn hình. Khi nghe các câu này, bạn hãy chọn phương án mô tả chính xác nhất những gì bạn nhìn thấy trong bức tranh.',
        };
    }
  };

  const directions = getPartDirections();

  return (
    <>
      {/* Header & Section Type */}
      <div className="bg-surface rounded-lg p-space-md shadow-sm border border-border-subtle">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-space-md pb-space-sm">
          <div>
            <div className={`flex items-center gap-space-xs ${isReading ? 'text-emerald-700' : 'text-primary'} font-label-sm text-label-sm uppercase tracking-wide font-semibold`}>
              <span className="material-symbols-outlined text-[18px]">
                {isReading ? 'menu_book' : 'headphones'}
              </span>
              <span>
                {isReading ? 'BÀI THI ĐỌC (READING)' : 'BÀI THI NGHE (LISTENING)'} — {partName || `Part ${partNumber}`}
              </span>
            </div>
            <h1 className="font-headline-sm text-headline-sm text-text-primary mt-0.5 font-bold">
              Hướng dẫn &amp; Câu hỏi {currentQuestion} / {totalQuestions}
            </h1>
          </div>
          <div className="flex items-center gap-space-xs bg-status-success-bg text-status-success px-2.5 py-1 rounded text-caption font-medium">
            <span className="w-2 h-2 rounded-full bg-status-success animate-pulse"></span>
            <span>{isReading ? 'Khu vực Thi Đọc hiểu — Không cần Audio' : 'Kênh Audio Trực Tuyến Đang Hoạt Động'}</span>
          </div>
        </div>

        {/* Embedded Track Player Card (Only shown if hasAudio) */}
        {hasAudio && (
          <div className="mt-space-sm bg-surface-subtle p-space-md rounded-lg flex flex-col gap-space-xs border border-border-subtle">
            <div className="flex items-center justify-between font-caption text-caption text-text-secondary">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-primary">graphic_eq</span>
                <span className="font-medium text-text-primary">Track_Part{partNumber}_Official.mp3</span>
                <span className="text-text-muted">• Âm thanh Chuẩn ETS</span>
              </div>
              <span className="tabular-nums font-numeric-metric text-text-secondary">00:42 / 03:15</span>
            </div>
            <div className="relative w-full h-1.5 bg-border-strong rounded-full overflow-hidden mt-1 cursor-pointer">
              <div className="absolute left-0 top-0 bottom-0 w-[21.5%] bg-primary rounded-full"></div>
            </div>
            <div className="flex items-center justify-between pt-1">
              <div className="flex items-center gap-space-sm">
                <button
                  className="p-1 rounded hover:bg-surface text-text-secondary hover:text-text-primary transition-colors"
                  type="button"
                  title="Lùi lại 5 giây"
                >
                  <span className="material-symbols-outlined text-[18px]">replay_5</span>
                </button>
                <button
                  className="p-1 rounded hover:bg-surface text-primary transition-colors"
                  type="button"
                  title="Tạm dừng / Phát tiếp"
                >
                  <span className="material-symbols-outlined text-[20px]">pause_circle</span>
                </button>
                <button
                  className="p-1 rounded hover:bg-surface text-text-secondary hover:text-text-primary transition-colors"
                  type="button"
                  title="Tua tới 5 giây"
                >
                  <span className="material-symbols-outlined text-[18px]">forward_5</span>
                </button>
                <span className="font-caption text-caption text-text-muted ml-1">Tốc độ:</span>
                <div className="inline-flex bg-surface rounded p-0.5 shadow-sm border border-border-subtle">
                  {['0.8x', '1.0x', '1.2x'].map((spd) => (
                    <button
                      key={spd}
                      className={`px-2 py-0.5 rounded text-[11px] ${
                        playbackSpeed === spd
                          ? 'font-semibold bg-primary text-on-primary'
                          : 'font-medium text-text-secondary'
                      }`}
                      type="button"
                      onClick={() => onChangeSpeed(spd)}
                    >
                      {spd}
                    </button>
                  ))}
                </div>
              </div>
              <div className="text-text-muted font-caption text-caption">Âm thanh Tối ưu hóa</div>
            </div>
          </div>
        )}
      </div>

      {/* Standard Directions Card */}
      <div className="bg-surface rounded-lg p-space-lg shadow-sm border border-border-subtle">
        <div className="flex items-center gap-space-xs text-text-primary font-headline-sm text-headline-sm mb-space-xs font-semibold">
          <span className="material-symbols-outlined text-primary text-[20px]">info</span>
          <span>{directions.title}</span>
        </div>
        <p className="font-body-reading text-body-reading text-text-body leading-[1.65]">
          {directions.text}
        </p>
      </div>
    </>
  );
}
