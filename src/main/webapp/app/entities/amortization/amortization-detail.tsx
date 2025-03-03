import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './amortization.reducer';

export const AmortizationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const amortizationEntity = useAppSelector(state => state.amortization.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="amortizationDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.amortization.detail.title">Amortization</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{amortizationEntity.id}</dd>
          <dt>
            <span id="installmentNumber">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.installmentNumber">Installment Number</Translate>
            </span>
          </dt>
          <dd>{amortizationEntity.installmentNumber}</dd>
          <dt>
            <span id="dueDate">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.dueDate">Due Date</Translate>
            </span>
          </dt>
          <dd>
            {amortizationEntity.dueDate ? (
              <TextFormat value={amortizationEntity.dueDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="remainingBalance">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.remainingBalance">Remaining Balance</Translate>
            </span>
          </dt>
          <dd>{amortizationEntity.remainingBalance}</dd>
          <dt>
            <span id="principal">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.principal">Principal</Translate>
            </span>
          </dt>
          <dd>{amortizationEntity.principal}</dd>
          <dt>
            <span id="paymentDate">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.paymentDate">Payment Date</Translate>
            </span>
          </dt>
          <dd>
            {amortizationEntity.paymentDate ? (
              <TextFormat value={amortizationEntity.paymentDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="paymentAmount">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.paymentAmount">Payment Amount</Translate>
            </span>
          </dt>
          <dd>{amortizationEntity.paymentAmount}</dd>
          <dt>
            <span id="penaltyInterest">
              <Translate contentKey="jhipsterSampleApplicationApp.amortization.penaltyInterest">Penalty Interest</Translate>
            </span>
          </dt>
          <dd>{amortizationEntity.penaltyInterest}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.amortization.loan">Loan</Translate>
          </dt>
          <dd>{amortizationEntity.loan ? amortizationEntity.loan.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/amortization" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/amortization/${amortizationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AmortizationDetail;
