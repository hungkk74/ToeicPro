import { BookOpen, Headphones, Info } from 'lucide-react';

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
          title: 'Hướng dẫn làm bài (Part 5)',
          text: 'Một từ hoặc cụm từ bị thiếu trong mỗi câu. Bốn phương án trả lời (A, B, C, D) được đưa ra dưới mỗi câu. Hãy chọn đáp án chính xác nhất để hoàn chỉnh câu văn theo ngữ pháp và ngữ nghĩa chuẩn TOEIC.',
        };
      case 6:
        return {
          title: 'Hướng dẫn làm bài (Part 6)',
          text: 'Đọc kỹ đoạn văn bản bên dưới. Các chỗ trống được đánh số thứ tự tương ứng với từng câu hỏi. Hãy chọn từ, cụm từ hoặc câu hoàn chỉnh phù hợp nhất để hoàn thiện văn bản một cách mạch lạc.',
        };
      case 7:
        return {
          title: 'Hướng dẫn làm bài (Part 7)',
          text: 'Đọc các văn bản đơn hoặc đoạn văn kép/ba (email, thông báo, hóa đơn, báo chí...). Sau đó chọn câu trả lời đúng nhất cho từng câu hỏi suy luận và tìm kiếm thông tin chi tiết.',
        };
      case 2:
        return {
          title: 'Hướng dẫn làm bài (Part 2)',
          text: 'Bạn sẽ nghe một câu hỏi hoặc phát biểu, sau đó là 3 phương án phản hồi (A, B, C). Chọn phương án phản hồi hợp lý và tự nhiên nhất cho câu hỏi.',
        };
      case 3:
        return {
          title: 'Hướng dẫn làm bài (Part 3)',
          text: 'Bạn sẽ nghe một đoạn hội thoại giữa 2 hoặc nhiều người. Với mỗi đoạn hội thoại, hãy trả lời 3 câu hỏi liên quan dựa trên thông tin nghe được.',
        };
      case 4:
        return {
          title: 'Hướng dẫn làm bài (Part 4)',
          text: 'Bạn sẽ nghe một bài nói ngắn (thông báo sân bay, quảng cáo, dự báo thời tiết...). Hãy lắng nghe cẩn thận và trả lời 3 câu hỏi cho mỗi bài nói.',
        };
      case 1:
      default:
        return {
          title: 'Hướng dẫn làm bài (Part 1)',
          text: 'Trong phần này, bạn sẽ nghe 4 câu mô tả về một bức tranh hiển thị trên màn hình. Khi nghe các câu này, bạn hãy chọn phương án mô tả chính xác nhất những gì bạn nhìn thấy trong bức tranh.',
        };
    }
  };

  const directions = getPartDirections();

  return (
    <div className="bg-white rounded-xl p-4 border border-slate-200/90 shadow-xs mb-4">
      <div className="flex flex-wrap items-center justify-between gap-2 pb-2.5 mb-2.5 border-b border-slate-100">
        <div className="flex items-center gap-1.5 text-slate-900 text-xs font-semibold tracking-wide">
          {isReading ? (
            <BookOpen className="w-3.5 h-3.5 text-slate-700" />
          ) : (
            <Headphones className="w-3.5 h-3.5 text-slate-700" />
          )}
          <span>
            {isReading ? 'BÀI THI ĐỌC (READING)' : 'BÀI THI NGHE (LISTENING)'} — {partName || `Part ${partNumber}`}
          </span>
        </div>
      </div>

      <div className="flex items-center gap-1.5 text-slate-900 text-xs sm:text-sm font-semibold mb-1">
        <Info className="w-3.5 h-3.5 text-slate-600" />
        <span className="font-serif">{directions.title}</span>
      </div>
      <p className="text-slate-600 text-xs sm:text-sm leading-relaxed">
        {directions.text}
      </p>
    </div>
  );
}
