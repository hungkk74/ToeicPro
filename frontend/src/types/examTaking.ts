export interface FlattenedQuestion {
  id: number;
  questionNumber: number;
  content?: string;
  imageUrl?: string;
  audioUrl?: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  partNumber: number;
  partName: string;
  passageText?: string;
  groupImageUrl?: string;
  groupAudioUrl?: string;
}

export interface ReviewMapItem {
  isCorrect: boolean;
  selectedOption?: string;
  correctOption: string;
}
