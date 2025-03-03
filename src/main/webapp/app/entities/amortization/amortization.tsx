import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntitiesByCurrentUser } from './amortization.reducer';

export const Amortization = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const amortizationList = useAppSelector(state => state.amortization.entities);
  const loading = useAppSelector(state => state.amortization.loading);
  const totalItems = useAppSelector(state => state.amortization.totalItems);

  useEffect(() => {
    dispatch(getEntitiesByCurrentUser({ page: 0, size: 20, sort: 'id,asc' }));
  }, []);

  const getAllEntities = () => {
    dispatch(
      getEntitiesByCurrentUser({ page: 0, size: 20, sort: 'id,asc' })
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

  return (
    <div>
      <h2 id="amortization-heading" data-cy="AmortizationHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.amortization.home.title">Amortizations</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.amortization.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/amortization/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.amortization.home.createLabel">Create new Amortization</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {amortizationList && amortizationList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('installmentNumber')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.installmentNumber">Installment Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('installmentNumber')} />
                </th>
                <th className="hand" onClick={sort('dueDate')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.dueDate">Due Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dueDate')} />
                </th>
                <th className="hand" onClick={sort('remainingBalance')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.remainingBalance">Remaining Balance</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remainingBalance')} />
                </th>
                <th className="hand" onClick={sort('principal')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.principal">Principal</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('principal')} />
                </th>
                <th className="hand" onClick={sort('paymentDate')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.paymentDate">Payment Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('paymentDate')} />
                </th>
                <th className="hand" onClick={sort('paymentAmount')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.paymentAmount">Payment Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('paymentAmount')} />
                </th>
                <th className="hand" onClick={sort('penaltyInterest')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.penaltyInterest">Penalty Interest</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('penaltyInterest')} />
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.amortization.loan">Loan</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {amortizationList.map((amortization, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/amortization/${amortization.id}`} color="link" size="sm">
                      {amortization.id}
                    </Button>
                  </td>
                  <td>{amortization.installmentNumber}</td>
                  <td>
                    {amortization.dueDate ? <TextFormat type="date" value={amortization.dueDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>{amortization.remainingBalance}</td>
                  <td>{amortization.principal}</td>
                  <td>
                    {amortization.paymentDate ? (
                      <TextFormat type="date" value={amortization.paymentDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{amortization.paymentAmount}</td>
                  <td>{amortization.penaltyInterest}</td>
                  <td>{amortization.loan ? <Link to={`/loan/${amortization.loan.id}`}>{amortization.loan.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/amortization/${amortization.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/amortization/${amortization.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/amortization/${amortization.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              No se encontraron Amortizaciones
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={amortizationList && amortizationList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={amortizationList.length} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
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

export default Amortization;
