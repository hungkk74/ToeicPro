import React, { Suspense } from 'react';
import { Route } from 'react-router';

import EntitiesRoutes from 'app/entities/routes';
import Home from 'app/modules/home/home';
import LoginRedirect from 'app/modules/login/login-redirect';
import Logout from 'app/modules/login/logout';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import { Authority } from 'app/shared/jhipster/constants';

const loading = <div>loading ...</div>;

const Admin = React.lazy(() => import('app/modules/administration'));
const AppRoutes = () => {
  return (
    <div className="view-routes">
      <Suspense fallback={loading}>
        <ErrorBoundaryRoutes>
          <Route index element={<Home />} />
          <Route path="logout" element={<Logout />} />
          <Route
            path="admin/*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
                <Admin />
              </PrivateRoute>
            }
          />
          <Route path="sign-in" element={<LoginRedirect />} />
          <Route
            path="*"
            element={
              <PrivateRoute hasAnyAuthorities={[Authority.USER]}>
                <EntitiesRoutes />
              </PrivateRoute>
            }
          />
        </ErrorBoundaryRoutes>
      </Suspense>
    </div>
  );
};

export default AppRoutes;
