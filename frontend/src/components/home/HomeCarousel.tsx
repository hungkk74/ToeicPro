'use client';

import { useState, useEffect } from 'react';
import Image from 'next/image';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const images = [
  '/banners/banner.png',
  '/banners/class_study.png',
  '/banners/disscussion.png',
  '/banners/girl_study.png',
  '/banners/teacher.png',
];

export default function HomeCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0);

  // Auto-play
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % images.length);
    }, 5000);
    return () => clearInterval(timer);
  }, []);

  const goToPrevious = () => {
    setCurrentIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const goToNext = () => {
    setCurrentIndex((prev) => (prev + 1) % images.length);
  };

  return (
    <div className="relative w-full h-[250px] sm:h-[350px] md:h-[450px] mb-8 rounded-3xl overflow-hidden shadow-lg group">
      {/* Images container */}
      <div 
        className="flex w-full h-full transition-transform duration-700 ease-in-out"
        style={{ transform: `translateX(-${currentIndex * 100}%)` }}
      >
        {images.map((src, index) => (
          <div key={index} className="w-full h-full shrink-0 relative">
            <Image
              src={src}
              alt={`Banner ${index + 1}`}
              fill
              className="object-cover transition-transform duration-700 group-hover:scale-105"
              priority={index === 0}
            />
          </div>
        ))}
      </div>

      {/* Overlay Navigation Buttons */}
      <button 
        onClick={goToPrevious}
        className="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-white/30 hover:bg-white/70 backdrop-blur-sm flex items-center justify-center text-slate-800 transition-all opacity-0 group-hover:opacity-100 focus:opacity-100"
        aria-label="Previous banner"
      >
        <ChevronLeft className="w-6 h-6" />
      </button>

      <button 
        onClick={goToNext}
        className="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-white/30 hover:bg-white/70 backdrop-blur-sm flex items-center justify-center text-slate-800 transition-all opacity-0 group-hover:opacity-100 focus:opacity-100"
        aria-label="Next banner"
      >
        <ChevronRight className="w-6 h-6" />
      </button>

      {/* Dots */}
      <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
        {images.map((_, index) => (
          <button
            key={index}
            onClick={() => setCurrentIndex(index)}
            className={`w-2.5 h-2.5 rounded-full transition-all ${
              currentIndex === index 
                ? 'bg-blue-600 w-8' 
                : 'bg-white/60 hover:bg-white'
            }`}
            aria-label={`Go to slide ${index + 1}`}
          />
        ))}
      </div>
    </div>
  );
}
