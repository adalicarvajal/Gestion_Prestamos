import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getLoans } from '../loan/loan.reducer';
import { getEntitiesByLoanId as getAmortizations } from '../amortization/amortization.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const Payment = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const pageLocation = useLocation();
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const loanList = useAppSelector(state => state.loan.entities);
  const amortizationList = useAppSelector(state => state.amortization.entities);
  const loading = useAppSelector(state => state.loan.loading);
  const totalItems = useAppSelector(state => state.loan.totalItems);
  const currentUserId = useAppSelector(state => state.authentication.account.id);

  const getAllEntities = () => {
    dispatch(
      getLoans({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const getAmortizationsForLoan = loanId => {
    dispatch(
      getAmortizations({
        loanId,
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  const transformStatus = status => {
    switch (status) {
      case 0:
        return 'Pendiente';
      case 1:
        return 'Aprobado';
      case 2:
        return 'Rechazado';
      case 3:
        return 'Pagado';
      case 4:
        return 'En mora';
      default:
        return 'Desconocido';
    }
  };

  const filteredLoanList = loanList.filter(loan => loan.user?.id === currentUserId);

  return (
    <div>
      <h2 id="loan-heading" data-cy="LoanHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.pagos.home.title">Pagos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.pagos.home.refreshListLabel">Actualizar lista</Translate>
          </Button>
          <Link to="/loan/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.pagos.home.createLabel">Crear nuevo Pago</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {filteredLoanList && filteredLoanList.length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th className="hand" onClick={sort('id')}>
                <Translate contentKey="jhipsterSampleApplicationApp.pagos.id">ID</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
              </th>
              <th className="hand" onClick={sort('requestedAmount')}>
                <Translate contentKey="jhipsterSampleApplicationApp.pagos.requestedAmount">Monto Solicitado</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('requestedAmount')} />
              </th>
              <th className="hand" onClick={sort('interestRate')}>
                Tasa de Interés <FontAwesomeIcon icon={getSortIconByFieldName('interestRate')} />
              </th>
              <th className="hand" onClick={sort('paymentTermMonths')}>
                <Translate contentKey="jhipsterSampleApplicationApp.pagos.paymentTermMonths">Plazo de Pago (Meses)</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('paymentTermMonths')} />
              </th>
              <th className="hand" onClick={sort('applicationDate')}>
                <Translate contentKey="jhipsterSampleApplicationApp.pagos.applicationDate">Fecha de Solicitud</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('applicationDate')} />
              </th>
              <th className="hand" onClick={sort('status')}>
                <Translate contentKey="jhipsterSampleApplicationApp.pagos.status">Estado</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
              </th>
              <th>
                <Translate contentKey="jhipsterSampleApplicationApp.pagos.user">Usuario</Translate> <FontAwesomeIcon icon="sort" />
              </th>
              <th />
            </tr>
            </thead>
            <tbody>
            {filteredLoanList.map((loan, i) => (
              <tr key={`entity-${i}`} data-cy="entityTable">
                <td>
                  <Button tag={Link} to={`/loan/${loan.id}`} color="link" size="sm">
                    {loan.id}
                  </Button>
                </td>
                <td>{loan.requestedAmount}</td>
                <td>{loan.interestRate}</td>
                <td>{loan.paymentTermMonths}</td>
                <td>
                  {loan.applicationDate ? <TextFormat type="date" value={loan.applicationDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                </td>
                <td>{transformStatus(loan.status)}</td>
                <td>{loan.user ? loan.user.login : 'N/A'}</td>
                <td className="text-end">
                  <div className="btn-group flex-btn-group-container">
                    <Button tag={Link} to={`/loan/${loan.id}/pay`} color="primary" size="sm" data-cy="entityPayButton">
                      <FontAwesomeIcon icon="money-bill" />{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.pay">Pagar</Translate>
                        </span>
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="jhipsterSampleApplicationApp.pagos.home.notFound">No se encontraron Pagos</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={loanList && loanList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={filteredLoanList.length} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Payment;
