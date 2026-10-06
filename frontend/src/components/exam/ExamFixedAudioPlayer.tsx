'use client';

import { useState, useRef, useEffect } from 'react';
import {
  Play,
  Pause,
  RotateCcw,
  Volume2,
  VolumeX,
  Headphones,
} from 'lucide-react';

interface ExamFixedAudioPlayerProps {
  audioUrl: string;
  partNumber?: number;
  partName?: string;
}

export default function ExamFixedAudioPlayer({
  audioUrl,
  partNumber = 1,
  partName,
}: ExamFixedAudioPlayerProps) {
  const audioRef = useRef<HTMLAudioElement | null>(null);

  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [playbackSpeed, setPlaybackSpeed] = useState('1.0x');
  const [volume, setVolume] = useState(1);
  const [isMuted, setIsMuted] = useState(false);
  const [currentSrc, setCurrentSrc] = useState(audioUrl);
  const [hasError, setHasError] = useState(false);

  // Reload audio when audioUrl prop changes
  useEffect(() => {
    setCurrentSrc(audioUrl);
    setHasError(false);
    setCurrentTime(0);
    setIsPlaying(false);

    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.load();
    }

    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
      }
    };
  }, [audioUrl]);

  // Handle media loading error
  const handleAudioError = (e: React.SyntheticEvent<HTMLAudioElement, Event>) => {
    console.warn('Audio playback error on URL:', currentSrc, e);
    // If external R2 failed, attempt local fallback /audio/<filename>
    const cleanFileName = currentSrc.split('/').pop()?.split('?')[0];
    if (cleanFileName && currentSrc.startsWith('http') && !currentSrc.startsWith('/audio/')) {
      const localPath = `/audio/${cleanFileName}`;
      console.info('Attempting fallback to local audio:', localPath);
      setCurrentSrc(localPath);
      return;
    }
    setHasError(true);
    setIsPlaying(false);
  };

  // Sync playback speed when changed
  const handleSpeedChange = (spd: string) => {
    setPlaybackSpeed(spd);
    const rate = parseFloat(spd.replace('x', ''));
    if (audioRef.current && !isNaN(rate)) {
      audioRef.current.playbackRate = rate;
    }
  };

  // Toggle Play / Pause
  const handleTogglePlay = () => {
    if (!audioRef.current || hasError) return;
    if (isPlaying) {
      audioRef.current.pause();
    } else {
      audioRef.current.play().catch((err) => {
        console.warn('Audio play request interrupted or prevented:', err);
        setIsPlaying(false);
        setHasError(true);
      });
    }
  };

  // Replay 5 seconds
  const handleReplay5 = () => {
    if (!audioRef.current) return;
    audioRef.current.currentTime = Math.max(0, audioRef.current.currentTime - 5);
  };

  // Forward 5 seconds
  const handleForward5 = () => {
    if (!audioRef.current) return;
    audioRef.current.currentTime = Math.min(duration || Infinity, audioRef.current.currentTime + 5);
  };

  // Seek bar
  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newTime = parseFloat(e.target.value);
    setCurrentTime(newTime);
    if (audioRef.current) {
      audioRef.current.currentTime = newTime;
    }
  };

  // Volume
  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newVol = parseFloat(e.target.value);
    setVolume(newVol);
    setIsMuted(newVol === 0);
    if (audioRef.current) {
      audioRef.current.volume = newVol;
      audioRef.current.muted = newVol === 0;
    }
  };

  const handleToggleMute = () => {
    if (!audioRef.current) return;
    const nextMuted = !isMuted;
    setIsMuted(nextMuted);
    audioRef.current.muted = nextMuted;
  };

  // Audio event listeners
  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;

    const onPlay = () => setIsPlaying(true);
    const onPause = () => setIsPlaying(false);
    const onTimeUpdate = () => setCurrentTime(audio.currentTime);
    const onLoadedMetadata = () => {
      const dur = audio.duration;
      setDuration(isFinite(dur) ? dur : 0);
      const rate = parseFloat(playbackSpeed.replace('x', ''));
      if (!isNaN(rate)) audio.playbackRate = rate;
    };
    const onEnded = () => setIsPlaying(false);

    audio.addEventListener('play', onPlay);
    audio.addEventListener('pause', onPause);
    audio.addEventListener('timeupdate', onTimeUpdate);
    audio.addEventListener('loadedmetadata', onLoadedMetadata);
    audio.addEventListener('ended', onEnded);

    return () => {
      audio.removeEventListener('play', onPlay);
      audio.removeEventListener('pause', onPause);
      audio.removeEventListener('timeupdate', onTimeUpdate);
      audio.removeEventListener('loadedmetadata', onLoadedMetadata);
      audio.removeEventListener('ended', onEnded);
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [audioUrl]);

  const formatTime = (seconds: number) => {
    if (isNaN(seconds) || seconds < 0) return '00:00';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = Math.floor(seconds % 60);
    if (h > 0) {
      return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    }
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const fileName = decodeURIComponent(audioUrl.split('/').pop()?.split('?')[0] || 'Listening_Track.mp3');

  return (
    <aside
      aria-label="Thanh điều khiển âm thanh phòng thi"
      className="fixed top-14 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/90 transition-all duration-200"
    >
      <audio
        ref={audioRef}
        src={currentSrc}
        preload="auto"
        onError={handleAudioError}
      />

      <div className="max-w-[1440px] mx-auto px-4 sm:px-6 lg:px-8 py-2.5 flex flex-col sm:flex-row items-center justify-between gap-2 sm:gap-4">
        {/* Left: Track Info */}
        <div className="flex items-center gap-3 w-full sm:w-auto justify-between sm:justify-start">
          <div className="flex items-center gap-2.5 min-w-0">
            <div
              className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 transition-colors ${
                hasError
                  ? 'bg-rose-100 text-rose-600 border border-rose-200'
                  : isPlaying
                  ? 'bg-blue-600 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-700 border border-slate-200'
              }`}
            >
              <Headphones className="w-4 h-4" />
            </div>
            <div className="min-w-0 max-w-[140px] sm:max-w-[180px] md:max-w-xs truncate">
              <div className="flex items-center gap-1.5">
                <span className="font-medium text-xs sm:text-sm text-slate-900 truncate" title={fileName}>
                  {fileName}
                </span>
                {hasError ? (
                  <span className="bg-rose-50 text-rose-700 text-[10px] font-semibold px-2 py-0.5 rounded-full shrink-0 border border-rose-200/70">
                    Không tải được audio
                  </span>
                ) : (
                  <span className="bg-slate-100 text-slate-700 text-[10px] font-semibold px-2 py-0.5 rounded-full shrink-0 border border-slate-200">
                    {partName || `Part ${partNumber}`}
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Quick Mobile Play Button */}
          <div className="sm:hidden flex items-center gap-1 shrink-0">
            <button
              type="button"
              disabled={hasError}
              onClick={handleTogglePlay}
              className={`p-2 rounded-full text-white transition-colors ${
                hasError
                  ? 'bg-slate-300 text-slate-400 cursor-not-allowed'
                  : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800 shadow-xs'
              }`}
              title={hasError ? 'File audio lỗi hoặc không tồn tại' : isPlaying ? 'Tạm dừng' : 'Phát tiếp'}
            >
              {isPlaying ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4" />}
            </button>
          </div>
        </div>

        {/* Center: Controls & Scrubber Slider */}
        <div className="flex-1 min-w-0 w-full max-w-2xl flex flex-col gap-1 items-center">
          <div className="flex items-center justify-center gap-3 w-full">
            <button
              type="button"
              disabled={hasError}
              onClick={handleReplay5}
              className={`p-1.5 rounded-lg transition-colors ${
                hasError
                  ? 'text-slate-300 cursor-not-allowed'
                  : 'hover:bg-slate-100 text-slate-600 hover:text-slate-900 cursor-pointer'
              }`}
              title="Lùi lại 5 giây"
            >
              <RotateCcw className="w-4 h-4" />
            </button>

            <button
              type="button"
              disabled={hasError}
              onClick={handleTogglePlay}
              className={`hidden sm:flex w-8 h-8 rounded-full text-white transition-transform active:scale-95 items-center justify-center ${
                hasError
                  ? 'bg-slate-300 text-slate-400 cursor-not-allowed'
                  : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800 shadow-xs cursor-pointer'
              }`}
              title={hasError ? 'File audio lỗi hoặc không tồn tại' : isPlaying ? 'Tạm dừng' : 'Phát tiếp'}
            >
              {isPlaying ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 fill-white translate-x-0.5" />}
            </button>

            <button
              type="button"
              disabled={hasError}
              onClick={handleForward5}
              className={`p-1.5 rounded-lg transition-colors rotate-180 ${
                hasError
                  ? 'text-slate-300 cursor-not-allowed'
                  : 'hover:bg-slate-100 text-slate-600 hover:text-slate-900 cursor-pointer'
              }`}
              title="Tua tới 5 giây"
            >
              <RotateCcw className="w-4 h-4" />
            </button>

            <div className="hidden sm:flex items-center gap-1.5 ml-2">
              {['0.75x', '1.0x', '1.25x'].map((spd) => (
                <button
                  key={spd}
                  type="button"
                  onClick={() => handleSpeedChange(spd)}
                  className={`px-2 py-0.5 rounded-md text-[11px] font-medium transition-colors cursor-pointer ${
                    playbackSpeed === spd
                      ? 'bg-blue-600 text-white font-semibold shadow-xs'
                      : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                  }`}
                >
                  {spd}
                </button>
              ))}
            </div>
          </div>

          {/* Time scrubber */}
          <div className="w-full flex items-center gap-2 text-[11px] text-slate-500 font-mono tabular-nums">
            <span className="w-10 text-right">{formatTime(currentTime)}</span>
            <div className="flex-1 relative flex items-center">
              <input
                type="range"
                min="0"
                max={duration || 100}
                value={currentTime}
                disabled={hasError}
                onChange={handleSeek}
                className={`w-full h-1 rounded-lg appearance-none transition-colors ${
                  hasError
                    ? 'bg-slate-200 cursor-not-allowed'
                    : 'bg-slate-200 cursor-pointer accent-blue-600'
                }`}
              />
            </div>
            <span className="w-10 text-left">{formatTime(duration)}</span>
          </div>
        </div>

        {/* Right: Volume */}
        <div className="hidden md:flex items-center gap-2 w-32 justify-end">
          <button
            type="button"
            onClick={handleToggleMute}
            className="p-1 text-slate-500 hover:text-slate-800 transition-colors"
          >
            {isMuted || volume === 0 ? <VolumeX className="w-4 h-4" /> : <Volume2 className="w-4 h-4" />}
          </button>
          <input
            type="range"
            min="0"
            max="1"
            step="0.05"
            value={isMuted ? 0 : volume}
            onChange={handleVolumeChange}
            className="w-16 h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
          />
        </div>
      </div>
    </aside>
  );
}
