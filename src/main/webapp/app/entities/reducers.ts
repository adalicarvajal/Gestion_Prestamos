import amortization from 'app/entities/amortization/amortization.reducer';
import loan from 'app/entities/loan/loan.reducer';
import userData from 'app/entities/user-data/user-data.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  amortization,
  loan,
  userData,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
