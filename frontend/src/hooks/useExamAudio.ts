'use client';

import { useState } from 'react';
import { FlattenedQuestion } from '@/types/examTaking';
import { ExamTakeDTO } from '@/types/backend';

export function useExamAudio(
  currentQData: FlattenedQuestion | undefined,
  examData: ExamTakeDTO | null
) {
  const [isPlaying, setIsPlaying] = useState(false);
  const [playbackSpeed, setPlaybackSpeed] = useState('1.0x');

  const isListeningPart = Boolean(currentQData?.partNumber && currentQData.partNumber <= 4);

  const isValidAudio = (url?: string): boolean =>
    Boolean(
      url &&
        (url.startsWith('http://') ||
          url.startsWith('https://') ||
          url.startsWith('/audio/') ||
          url.startsWith('blob:') ||
          url.startsWith('data:'))
    );

  const specificAudio = isValidAudio(currentQData?.audioUrl)
    ? currentQData?.audioUrl
    : isValidAudio(currentQData?.groupAudioUrl)
    ? currentQData?.groupAudioUrl
    : undefined;

  const activeAudioUrl =
    specificAudio || (isListeningPart ? examData?.audioFullUrl : undefined);

  const togglePlay = () => setIsPlaying((prev) => !prev);

  return {
    isPlaying,
    setIsPlaying,
    togglePlay,
    playbackSpeed,
    setPlaybackSpeed,
    isListeningPart,
    activeAudioUrl,
  };
}
