import { beforeEach, describe, expect, it } from 'vitest';
import React from 'react';
import { MemoryRouter } from 'react-router';

import { render, waitFor } from '@testing-library/react';

import { AccountMenu } from './account';

describe('AccountMenu', () => {
  let mountedWrapper: string | undefined;

  const authenticatedWrapper = async () => {
    if (!mountedWrapper) {
      const { container } = render(
        <MemoryRouter>
          <AccountMenu isAuthenticated />
        </MemoryRouter>,
      );
      await waitFor(() => expect(container.querySelector('.dropdown-menu')).not.toBeNull());
      mountedWrapper = container.innerHTML;
    }
    return mountedWrapper;
  };
  const guestWrapper = async () => {
    if (!mountedWrapper) {
      const { container } = render(
        <MemoryRouter>
          <AccountMenu />
        </MemoryRouter>,
      );
      await waitFor(() => expect(container.querySelector('.dropdown-menu')).not.toBeNull());
      mountedWrapper = container.innerHTML;
    }
    return mountedWrapper;
  };

  beforeEach(() => {
    mountedWrapper = undefined;
  });

  it('should render an authenticated AccountMenu component', async () => {
    const html = await authenticatedWrapper();

    expect(html).not.toContain('/login');
    expect(html).toContain('/logout');
  });

  it('should render a guest AccountMenu component', async () => {
    const html = await guestWrapper();

    expect(html).not.toContain('/logout');
  });
});
