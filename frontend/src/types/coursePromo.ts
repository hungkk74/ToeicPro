export interface CoursePromoItem {
  id: string;
  badge: string;
  badgeType: 'breakthrough' | 'speed' | 'mentor';
  title: string;
  subtitle?: string;
  durationHours: number;
  liveSessions: number;
  reviewSessions: number;
  highlights: string[];
  originalPrice: number;
  discountedPrice: number;
  discountPercent: number;
  tagline: string;
}
