import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { Route } from 'react-router';
import Amortization from './amortization';
import Payment from './payment';
import Loan from './loan';
import UserData from './user-data';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="amortization/*" element={<Amortization />} />
        <Route path="loan/*" element={<Loan />} />
        <Route path="payment/*" element={<Payment />} />
        <Route path="user-data/*" element={<UserData />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
