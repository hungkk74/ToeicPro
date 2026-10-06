import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

interface UseScoreCountdownOptions {
  isOpen: boolean;
  activeTab: string;
  initialSeconds?: number;
  redirectUrl?: string;
  autoStart?: boolean;
}

export function useScoreCountdown({
  isOpen,
  activeTab,
  initialSeconds = 60,
  redirectUrl = '/de-thi',
  autoStart = false,
}: UseScoreCountdownOptions) {
  const router = useRouter();
  const [countdown, setCountdown] = useState(initialSeconds);
  const [isPaused, setIsPaused] = useState(!autoStart);

  useEffect(() => {
    if (!isOpen || isPaused || activeTab !== 'summary') return;

    if (countdown <= 0) {
      router.push(redirectUrl);
      return;
    }

    const timer = setInterval(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [isOpen, isPaused, countdown, router, activeTab, redirectUrl]);

  const togglePause = () => setIsPaused((prev) => !prev);
  const pause = () => setIsPaused(true);
  const resume = () => setIsPaused(false);

  return {
    countdown,
    isPaused,
    togglePause,
    pause,
    resume,
    redirectToTarget: () => router.push(redirectUrl),
  };
}
