import { describe, expect, it, vi } from 'vitest';

import axios from 'axios';

import setupAxiosInterceptors from './axios-interceptor';

describe('Axios Interceptor', () => {
  describe('setupAxiosInterceptors', () => {
    const onUnauthenticated = vi.fn();
    setupAxiosInterceptors(onUnauthenticated);

    it('onRequestSuccess is called on fulfilled request', () => {
      expect((axios.interceptors.request as any).handlers[0].fulfilled({ data: 'foo', url: '/test' })).toMatchObject({
        data: 'foo',
      });
    });
    it('onResponseSuccess is called on fulfilled response', () => {
      expect((axios.interceptors.response as any).handlers[0].fulfilled({ data: 'foo' })).toEqual({ data: 'foo' });
    });
    it('onResponseError is called on rejected response', async () => {
      const rejectError = {
        response: {
          statusText: 'NotFound',
          status: 401,
          data: { message: 'Page not found' },
        },
      };
      await expect((axios.interceptors.response as any).handlers[0].rejected(rejectError)).rejects.toEqual(rejectError);
      expect(onUnauthenticated).toHaveBeenCalledTimes(1);
    });
  });
});
