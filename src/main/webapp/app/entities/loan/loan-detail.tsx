import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './loan.reducer';

export const LoanDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const loanEntity = useAppSelector(state => state.loan.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="loanDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.prestamos.detail.title">Loan</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{loanEntity.id}</dd>
          <dt>
            <span id="requestedAmount">
              <Translate contentKey="jhipsterSampleApplicationApp.prestamos.requestedAmount">Requested Amount</Translate>
            </span>
          </dt>
          <dd>{loanEntity.requestedAmount}</dd>
          <dt>
            <span id="interestRate">
              <Translate contentKey="jhipsterSampleApplicationApp.prestamos.interestRate">Interest Rate</Translate>
            </span>
          </dt>
          <dd>{loanEntity.interestRate}</dd>
          <dt>
            <span id="paymentTermMonths">
              <Translate contentKey="jhipsterSampleApplicationApp.prestamos.paymentTermMonths">Payment Term Months</Translate>
            </span>
          </dt>
          <dd>{loanEntity.paymentTermMonths}</dd>
          <dt>
            <span id="applicationDate">
              <Translate contentKey="jhipsterSampleApplicationApp.prestamos.applicationDate">Application Date</Translate>
            </span>
          </dt>
          <dd>
            {loanEntity.applicationDate ? (
              <TextFormat value={loanEntity.applicationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="jhipsterSampleApplicationApp.prestamos.status">Status</Translate>
            </span>
          </dt>
          <dd>{loanEntity.status}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.prestamos.user">User</Translate>
          </dt>
          <dd>{loanEntity.user ? loanEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/loan" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/loan/${loanEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LoanDetail;
