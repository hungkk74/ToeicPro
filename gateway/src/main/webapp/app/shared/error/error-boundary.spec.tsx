import { beforeEach, describe, expect, it, vi } from 'vitest';
import React from 'react';

import { render } from '@testing-library/react';

import ErrorBoundary from 'app/shared/error/error-boundary';

const ErrorComp = () => {
  throw new Error('test');
};

describe('error component', () => {
  beforeEach(() => {
    // ignore console and jsdom errors
    vi.spyOn((globalThis as any)._virtualConsole, 'emit').mockImplementation(() => false);
    vi.spyOn((globalThis as any).console, 'error').mockImplementation(() => false);
  });

  it('should throw an error when component is not enclosed in Error Boundary', () => {
    expect(() => render(<ErrorComp />)).toThrow(Error);
  });

  it('should call Error Boundary componentDidCatch method', () => {
    const spy = vi.spyOn(ErrorBoundary.prototype, 'componentDidCatch');
    render(
      <ErrorBoundary>
        <ErrorComp />
      </ErrorBoundary>,
    );
    expect(spy).toHaveBeenCalled();
  });
});
