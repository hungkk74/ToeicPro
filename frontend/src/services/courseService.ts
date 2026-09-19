import { fetchApi } from '@/lib/api-client';
import { CourseDTO } from '@/types/backend';

export async function fetchCoursesFromBackend(): Promise<CourseDTO[]> {
  try {
    const data = await fetchApi<CourseDTO[]>('/api/courses?page=0&size=10');
    if (Array.isArray(data) && data.length > 0) {
      return data;
    }
  } catch (err) {
    console.warn('Backend /api/courses unavailable or empty, using fallback courses:', err);
  }
  return [];
}
