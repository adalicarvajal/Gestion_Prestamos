import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Amortization from './amortization';
import AmortizationDetail from './amortization-detail';
import AmortizationUpdate from './amortization-update';
import AmortizationDeleteDialog from './amortization-delete-dialog';

const AmortizationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Amortization />} />
    <Route path="new" element={<AmortizationUpdate />} />
    <Route path=":id">
      <Route index element={<AmortizationDetail />} />
      <Route path="edit" element={<AmortizationUpdate />} />
      <Route path="delete" element={<AmortizationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AmortizationRoutes;
