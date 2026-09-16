import { useEffect } from 'react';
import { useLocation } from 'react-router';

import { REDIRECT_URL } from 'app/shared/util/url-utils';

export const LoginRedirect = () => {
  const pageLocation = useLocation();

  useEffect(() => {
    localStorage.setItem(REDIRECT_URL, pageLocation.state.from.pathname);
    globalThis.location.href = '/oauth2/authorization/oidc';
  });

  return null;
};

export default LoginRedirect;
