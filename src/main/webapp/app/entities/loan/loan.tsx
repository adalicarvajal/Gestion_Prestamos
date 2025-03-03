import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import {APP_LOCAL_DATE_FORMAT, AUTHORITIES} from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';

import { getEntities } from './loan.reducer';
import {hasAnyAuthority} from "app/shared/auth/private-route";

export const Loan = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const loanList = useAppSelector(state => state.loan.entities);
  const loading = useAppSelector(state => state.loan.loading);
  const totalItems = useAppSelector(state => state.loan.totalItems);
  const users = useAppSelector(state => state.userManagement.users);
  const currentUserId = useAppSelector(state => state.authentication.account.id);
  const currentUserIsAdmin = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN])
  );
  const userHasActiveLoan = !currentUserIsAdmin && loanList.some(loan => loan.user?.id === currentUserId && loan.status !== 3); // Status 3 es 'Pagado'

  const getAllEntities = () => {
    dispatch(
      getEntities({
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
    const params = { page: 0, size: 10, sort: 'id,asc' }; // Pasar valores predeterminados
    dispatch(getUsers(params)); // Cargar la lista de usuarios al montar el componente
  }, [dispatch]);

  const getUserLogin = userId => {
    const user = users.find(u => u.id === userId);
    return user ? user.login : 'N/A';
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
        order: sortSplit[1],
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

  const transformStatus = (status) => {
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

  const filteredLoanList = currentUserIsAdmin
    ? loanList
    : loanList.filter((loan) => loan.user?.id === currentUserId);

  return (
    <div>
      <h2 id="loan-heading" data-cy="LoanHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.prestamos.home.title">Préstamos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.prestamos.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Button
            tag={Link}
            to="/loan/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            disabled={userHasActiveLoan} // Deshabilita el botón si el usuario ya tiene un préstamo activo
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.prestamos.home.createLabel">Create new Loan</Translate>
          </Button>
        </div>
      </h2>
      <div className="table-responsive">
        {filteredLoanList  && filteredLoanList .length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th className="hand" onClick={sort('id')}>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.id">ID</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
              </th>
              <th className="hand" onClick={sort('requestedAmount')}>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.requestedAmount">Requested Amount</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('requestedAmount')} />
              </th>
              <th className="hand" onClick={sort('interestRate')}>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.interestRate">Interest Rate</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('interestRate')} />
              </th>
              <th className="hand" onClick={sort('paymentTermMonths')}>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.paymentTermMonths">Payment Term Months</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('paymentTermMonths')} />
              </th>
              <th className="hand" onClick={sort('applicationDate')}>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.applicationDate">Application Date</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('applicationDate')} />
              </th>
              <th className="hand" onClick={sort('status')}>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.status">Status</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
              </th>
              <th>
                <Translate contentKey="jhipsterSampleApplicationApp.prestamos.user">User</Translate> <FontAwesomeIcon icon="sort" />
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
                <td>{loan.user ? getUserLogin(loan.user.id) : 'N/A'}</td>
                <td className="text-end">
                <div className="btn-group flex-btn-group-container">
                    <Button tag={Link} to={`/loan/${loan.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                      <FontAwesomeIcon icon="eye" />{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                    </Button>
                  {currentUserIsAdmin && (
                    <Button
                      tag={Link}
                      to={`/loan/${loan.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                      color="primary"
                      size="sm"
                      data-cy="entityEditButton"
                    >
                      <FontAwesomeIcon icon="pencil-alt" />{' '}
                      <span className="d-none d-md-inline">
                        <Translate contentKey="entity.action.edit">Edit</Translate>
                      </span>
                    </Button>
                  )}
                  {currentUserIsAdmin && (
                    <Button
                      onClick={() =>
                        (window.location.href = `/loan/${loan.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                      }
                      color="danger"
                      size="sm"
                      data-cy="entityDeleteButton"
                    >
                      <FontAwesomeIcon icon="trash" />{' '}
                      <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </span>
                    </Button>
                  )}
                </div>
                </td>
              </tr>
            ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="jhipsterSampleApplicationApp.prestamos.home.notFound">No Loans found</Translate>
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

export default Loan;
