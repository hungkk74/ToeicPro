import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

interface UseScoreCountdownOptions {
  isOpen: boolean;
  activeTab: string;
  initialSeconds?: number;
  redirectUrl?: string;
}

export function useScoreCountdown({
  isOpen,
  activeTab,
  initialSeconds = 10,
  redirectUrl = '/de-thi',
}: UseScoreCountdownOptions) {
  const router = useRouter();
  const [countdown, setCountdown] = useState(initialSeconds);
  const [isPaused, setIsPaused] = useState(false);

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

  return {
    countdown,
    isPaused,
    togglePause,
    pause,
    redirectToTarget: () => router.push(redirectUrl),
  };
}
