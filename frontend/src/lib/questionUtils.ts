/**
 * Utility functions xử lý và làm sạch câu hỏi & đáp án TOEIC
 * Loại bỏ hoàn toàn trùng lặp hiển thị A, B, C, D giữa Stem và Options
 */

export interface ParsedQuestionResult {
  cleanContent: string;
  extractedOptions: Record<'A' | 'B' | 'C' | 'D', string> | null;
}

const OPTION_PREFIX_REGEX =
  /^(?:(?:đáp\s*án|lựa\s*chọn|câu|phương\s*án|option)\s*(?:[A-Da-d]\s*[.)\-:]?|[.)\-:])|(?:\(\s*[A-Da-d]\s*\)|\[\s*[A-Da-d]\s*\]|[A-Da-d]\s*[.)\-:]))\s*/i;

const BARE_OPTION_KEY_REGEX =
  /^(?:(?:đáp\s*án|lựa\s*chọn|câu|phương\s*án|option)\s*(?:[A-Da-d]\s*[.)\-:]?|[.)\-:])|(?:\(\s*[A-Da-d]\s*\)|\[\s*[A-Da-d]\s*\]|[A-Da-d]\s*[.)\-:]?))$/i;

/**
 * Tách nội dung câu hỏi (stem) và trích xuất đáp án nếu content bị dính kèm đáp án A, B, C, D
 */
export function parseQuestionContent(rawContent?: string): ParsedQuestionResult {
  if (!rawContent || !rawContent.trim()) {
    return { cleanContent: '', extractedOptions: null };
  }

  const trimmed = rawContent.trim();

  // Regex tìm vị trí xuất hiện của marker A, B, C, D (tránh nhầm lẫn chữ viết tắt như a.m./p.m.)
  const markerARegex = /(?:^|[\r\n\s]+|<br\s*\/?>)(?:\([Aa]\)|\[[Aa]\]|[Aa]\.(?!\w)|[Aa][)\-:])\s*/g;
  const markerBRegex = /(?:^|[\r\n\s]+|<br\s*\/?>)(?:\([Bb]\)|\[[Bb]\]|[Bb]\.(?!\w)|[Bb][)\-:])\s*/g;
  const markerCRegex = /(?:^|[\r\n\s]+|<br\s*\/?>)(?:\([Cc]\)|\[[Cc]\]|[Cc]\.(?!\w)|[Cc][)\-:])\s*/g;
  const markerDRegex = /(?:^|[\r\n\s]+|<br\s*\/?>)(?:\([Dd]\)|\[[Dd]\]|[Dd]\.(?!\w)|[Dd][)\-:])\s*/g;

  const matchA = markerARegex.exec(trimmed);
  if (!matchA) {
    return { cleanContent: trimmed, extractedOptions: null };
  }

  markerBRegex.lastIndex = markerARegex.lastIndex;
  const matchB = markerBRegex.exec(trimmed);
  if (!matchB) {
    return { cleanContent: trimmed, extractedOptions: null };
  }

  markerCRegex.lastIndex = markerBRegex.lastIndex;
  const matchC = markerCRegex.exec(trimmed);
  if (!matchC) {
    return { cleanContent: trimmed, extractedOptions: null };
  }

  markerDRegex.lastIndex = markerCRegex.lastIndex;
  const matchD = markerDRegex.exec(trimmed);

  const stem = trimmed.slice(0, matchA.index).trim();
  const optA = trimmed.slice(matchA.index + matchA[0].length, matchB.index).trim();

  let optB = '';
  let optC = '';
  let optD = '';

  if (matchD) {
    optB = trimmed.slice(matchB.index + matchB[0].length, matchC.index).trim();
    optC = trimmed.slice(matchC.index + matchC[0].length, matchD.index).trim();
    optD = trimmed.slice(matchD.index + matchD[0].length).trim();
  } else {
    optB = trimmed.slice(matchB.index + matchB[0].length, matchC.index).trim();
    optC = trimmed.slice(matchC.index + matchC[0].length).trim();
  }

  return {
    cleanContent: stem,
    extractedOptions: {
      A: cleanOptionText(optA),
      B: cleanOptionText(optB),
      C: cleanOptionText(optC),
      D: cleanOptionText(optD),
    },
  };
}

/**
 * Xóa tiền tố thừa (A), [A], A., A), A -, Đáp án A:... khỏi nội dung text của từng đáp án
 * Nếu text chỉ là ký tự đáp án thuần túy ("A", "(A)", "Đáp án A") thì trả về rỗng để không trùng lặp
 */
export function cleanOptionText(text?: string): string {
  if (!text) return '';
  const trimmed = text.trim();
  if (!trimmed) return '';

  if (BARE_OPTION_KEY_REGEX.test(trimmed)) {
    return '';
  }

  const cleaned = trimmed.replace(OPTION_PREFIX_REGEX, '').trim();

  if (BARE_OPTION_KEY_REGEX.test(cleaned)) {
    return '';
  }

  return cleaned;
}

/**
 * Kiểm tra câu hỏi có thuộc Part 1 hoặc Part 2 TOEIC (phương án hoàn toàn bằng Audio, không in chữ)
 */
export function isToeicAudioOnlyPart(
  partNumber?: number | string,
  questionNumber?: number | string
): boolean {
  const pNum = partNumber != null ? Number(partNumber) : NaN;
  const qNum = questionNumber != null ? Number(questionNumber) : NaN;
  if (pNum === 1 || pNum === 2) return true;
  if (!isNaN(qNum) && qNum >= 1 && qNum <= 31) return true;
  return false;
}

/**
 * Kiểm tra câu hỏi có thuộc Part 2 TOEIC (chỉ có 3 đáp án A, B, C) hay không
 */
export function isToeicPart2(
  partNumber?: number | string,
  questionNumber?: number | string
): boolean {
  const pNum = partNumber != null ? Number(partNumber) : NaN;
  const qNum = questionNumber != null ? Number(questionNumber) : NaN;
  if (pNum === 2) return true;
  if (!isNaN(qNum) && qNum >= 7 && qNum <= 31) return true;
  return false;
}

/**
 * Hiển thị text đáp án sạch sẽ, loại bỏ triệt để trùng lặp ký tự A, B, C, D trên cùng một dòng
 */
export function formatOptionDisplay(optKey: string, cleanedText?: string): string {
  if (!cleanedText) return '';
  const cleaned = cleanOptionText(cleanedText);
  if (!cleaned) return '';

  const upper = cleaned.toUpperCase().trim();
  const targetKey = optKey.toUpperCase().trim();

  // Không hiển thị text nếu nó trùng lặp với optKey hoặc chỉ là dạng [A], (A), Đáp án A
  if (
    upper === targetKey ||
    upper === `(${targetKey})` ||
    upper === `[${targetKey}]` ||
    BARE_OPTION_KEY_REGEX.test(upper)
  ) {
    return '';
  }

  return cleaned;
}
