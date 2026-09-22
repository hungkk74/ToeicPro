'use client';

import { useState, useRef, useEffect } from 'react';

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
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
    } else {
      audioRef.current.play().catch((e) => {
        console.warn('Audio play request interrupted or prevented:', e);
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
      setDuration(audio.duration || 0);
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
  }, [playbackSpeed]);

  // Format time (mm:ss or hh:mm:ss)
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
  const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0;

  return (
    <aside 
      aria-label="Thanh điều khiển âm thanh phòng thi"
      className="fixed top-14 left-0 right-0 z-40 bg-surface/95 backdrop-blur-md border-b border-border-subtle shadow-xs transition-all duration-200"
    >
      {/* Hidden native audio element */}
      <audio ref={audioRef} src={audioUrl} preload="auto" />

      <div className="max-w-[1440px] mx-auto px-3 sm:px-6 lg:px-8 py-2 flex flex-col sm:flex-row items-center justify-between gap-2 sm:gap-4">
        {/* Left: Track Information & Status Badge */}
        <div className="flex items-center gap-2 sm:gap-3 w-full sm:w-auto justify-between sm:justify-start">
          <div className="flex items-center gap-2.5 min-w-0">
            <div
              className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 transition-colors ${
                isPlaying
                  ? 'bg-primary text-on-primary shadow-xs ring-2 ring-primary/20'
                  : 'bg-surface-subtle text-text-secondary border border-border-subtle'
              }`}
            >
              <span className="material-symbols-outlined text-[18px]">
                {isPlaying ? 'graphic_eq' : 'headphones'}
              </span>
            </div>
            <div className="min-w-0 max-w-[140px] sm:max-w-[180px] md:max-w-xs truncate">
              <div className="flex items-center gap-1.5">
                <span className="font-semibold text-xs sm:text-sm text-text-primary truncate" title={fileName}>
                  {fileName}
                </span>
                <span className="bg-blue-100 text-primary text-[10px] font-bold px-1.5 py-0.2 rounded shrink-0">
                  {partName || `Part ${partNumber}`}
                </span>
              </div>
            </div>
          </div>

          {/* Quick Mobile Play Button */}
          <div className="sm:hidden flex items-center gap-1 shrink-0">
            <button
              type="button"
              onClick={handleTogglePlay}
              className="p-1.5 rounded-full bg-primary text-on-primary hover:bg-primary-dark transition-colors"
              title={isPlaying ? 'Tạm dừng' : 'Phát tiếp'}
            >
              <span className="material-symbols-outlined text-[20px]">
                {isPlaying ? 'pause' : 'play_arrow'}
              </span>
            </button>
          </div>
        </div>

        {/* Center: Controls, Scrubber Slider & Timestamps */}
        <div className="flex-1 min-w-0 w-full max-w-2xl flex flex-col gap-1 items-center">
          <div className="flex items-center justify-center gap-2 sm:gap-3 w-full">
            {/* Replay 5s */}
            <button
              type="button"
              onClick={handleReplay5}
              className="p-1 sm:p-1.5 rounded-md hover:bg-surface text-text-secondary hover:text-text-primary transition-colors cursor-pointer"
              title="Lùi lại 5 giây"
            >
              <span className="material-symbols-outlined text-[19px] sm:text-[21px]">replay_5</span>
            </button>

            {/* Play / Pause Primary Button */}
            <button
              type="button"
              onClick={handleTogglePlay}
              className="hidden sm:flex w-8 h-8 rounded-full bg-primary text-on-primary hover:bg-primary-dark transition-transform active:scale-95 shadow-xs items-center justify-center cursor-pointer"
              title={isPlaying ? 'Tạm dừng' : 'Phát tiếp'}
            >
              <span className="material-symbols-outlined text-[22px]">
                {isPlaying ? 'pause' : 'play_arrow'}
              </span>
            </button>

            {/* Forward 5s */}
            <button
              type="button"
              onClick={handleForward5}
              className="p-1 sm:p-1.5 rounded-md hover:bg-surface text-text-secondary hover:text-text-primary transition-colors cursor-pointer"
              title="Tua tới 5 giây"
            >
              <span className="material-symbols-outlined text-[19px] sm:text-[21px]">forward_5</span>
            </button>

            {/* Scrubber & Time */}
            <div className="flex items-center gap-2 flex-1 w-full max-w-lg">
              <span className="text-[11px] sm:text-xs font-mono font-medium text-text-primary tabular-nums shrink-0 w-11 sm:w-12 text-right">
                {formatTime(currentTime)}
              </span>

              {/* Range Track */}
              <div className="relative flex-1 flex items-center group py-1">
                <input
                  type="range"
                  min={0}
                  max={duration || 100}
                  step={0.5}
                  value={currentTime}
                  onChange={handleSeek}
                  aria-label="Thanh tua âm thanh bài thi"
                  className="w-full h-1.5 sm:h-2 bg-border-strong rounded-full appearance-none cursor-pointer accent-primary focus:outline-hidden"
                  style={{
                    background: `linear-gradient(to right, #0056D2 ${progressPercent}%, #E2E8F0 ${progressPercent}%)`,
                  }}
                />
              </div>

              <span className="text-[11px] sm:text-xs font-mono font-medium text-text-muted tabular-nums shrink-0 w-11 sm:w-12">
                {formatTime(duration)}
              </span>
            </div>
          </div>
        </div>

        {/* Right: Playback Speed & Volume */}
        <div className="hidden lg:flex items-center gap-3 shrink-0">
          {/* Playback Speed Chips */}
          <div className="inline-flex bg-surface-subtle p-0.5 rounded-md border border-border-subtle shadow-2xs">
            {['0.8x', '1.0x', '1.2x', '1.5x'].map((spd) => (
              <button
                key={spd}
                type="button"
                onClick={() => handleSpeedChange(spd)}
                className={`px-2 py-0.5 rounded text-[11px] font-medium transition-all ${
                  playbackSpeed === spd
                    ? 'bg-surface text-primary font-bold shadow-xs border border-border-subtle'
                    : 'text-text-secondary hover:text-text-primary'
                }`}
                title={`Tốc độ đọc: ${spd}`}
              >
                {spd}
              </button>
            ))}
          </div>

          {/* Volume Control */}
          <div className="flex items-center gap-1.5 text-text-secondary">
            <button
              type="button"
              onClick={handleToggleMute}
              className="p-1 rounded hover:text-primary transition-colors"
              title={isMuted ? 'Bật âm thanh' : 'Tắt âm thanh'}
            >
              <span className="material-symbols-outlined text-[18px]">
                {isMuted || volume === 0 ? 'volume_off' : volume < 0.5 ? 'volume_down' : 'volume_up'}
              </span>
            </button>
            <input
              type="range"
              min={0}
              max={1}
              step={0.05}
              value={isMuted ? 0 : volume}
              onChange={handleVolumeChange}
              aria-label="Âm lượng bài nghe"
              className="w-16 h-1 bg-border-strong rounded-lg appearance-none cursor-pointer accent-primary"
            />
          </div>
        </div>
      </div>
    </aside>
  );
}
