interface ExamDirectionsProps {
  currentQuestion?: number;
  totalQuestions?: number;
  partNumber?: number;
  partName?: string;
  hasAudio?: boolean;
  audioUrl?: string;
  playbackSpeed?: string;
  onChangeSpeed?: (spd: string) => void;
}

export default function ExamDirections({
  currentQuestion: _currentQuestion,
  totalQuestions: _totalQuestions = 200,
  partNumber = 1,
  partName,
  hasAudio: _hasAudio = false,
  audioUrl: _audioUrl,
  playbackSpeed: _playbackSpeed,
  onChangeSpeed: _onChangeSpeed,
}: ExamDirectionsProps) {
  const isReading = partNumber >= 5;

  const getPartDirections = () => {
    switch (partNumber) {
      case 5:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Một từ hoặc cụm từ bị thiếu trong mỗi câu. Bốn phương án trả lời (A, B, C, D) được đưa ra dưới mỗi câu. Hãy chọn đáp án chính xác nhất để hoàn chỉnh câu văn theo ngữ pháp và ngữ nghĩa chuẩn TOEIC.',
        };
      case 6:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Đọc kỹ đoạn văn bản bên dưới. Các chỗ trống được đánh số thứ tự tương ứng với từng câu hỏi. Bạn hãy chọn từ, cụm từ hoặc câu hoàn chỉnh phù hợp nhất để hoàn thiện văn bản một cách mạch lạc.',
        };
      case 7:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Đọc các văn bản đơn hoặc đoạn văn kép/ba (email, thông báo, hóa đơn, báo chí...). Sau đó chọn câu trả lời đúng nhất cho từng câu hỏi suy luận và tìm kiếm thông tin chi tiết.',
        };
      case 2:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Bạn sẽ nghe một câu hỏi hoặc phát biểu, sau đó là 3 phương án phản hồi (A, B, C). Chọn phương án phản hồi hợp lý và tự nhiên nhất cho câu hỏi.',
        };
      case 3:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Bạn sẽ nghe một đoạn hội thoại giữa 2 hoặc nhiều người. Với mỗi đoạn hội thoại, hãy trả lời 3 câu hỏi liên quan dựa trên thông tin nghe được.',
        };
      case 4:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Bạn sẽ nghe một bài nói ngắn (thông báo sân bay, quảng cáo, dự báo thời tiết...). Hãy lắng nghe cẩn thận và trả lời 3 câu hỏi cho mỗi bài nói.',
        };
      case 1:
      default:
        return {
          title: 'Hướng dẫn làm bài',
          text: 'Trong phần này, bạn sẽ nghe 4 câu mô tả về một bức tranh hiển thị trên màn hình. Khi nghe các câu này, bạn hãy chọn phương án mô tả chính xác nhất những gì bạn nhìn thấy trong bức tranh.',
        };
    }
  };

  const directions = getPartDirections();

  return (
    <>
      {/* Standard Directions Card with Section Badge */}
      <div className="bg-surface rounded-lg p-space-md sm:p-space-lg shadow-sm border border-border-subtle">
        <div className="flex flex-wrap items-center justify-between gap-2 mb-space-sm pb-space-xs border-b border-border-subtle">
          <div className={`flex items-center gap-1.5 ${isReading ? 'text-emerald-700' : 'text-primary'} text-xs uppercase tracking-wide font-bold`}>
            <span className="material-symbols-outlined text-[18px]">
              {isReading ? 'menu_book' : 'headphones'}
            </span>
            <span>
              {isReading ? 'BÀI THI ĐỌC (READING)' : 'BÀI THI NGHE (LISTENING)'} — {partName || `Part ${partNumber}`}
            </span>
          </div>
        </div>

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
