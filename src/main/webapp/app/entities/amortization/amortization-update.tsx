import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getLoans } from 'app/entities/loan/loan.reducer';
import { createEntity, getEntity, reset, updateEntity } from './amortization.reducer';

export const AmortizationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const loans = useAppSelector(state => state.loan.entities);
  const amortizationEntity = useAppSelector(state => state.amortization.entity);
  const loading = useAppSelector(state => state.amortization.loading);
  const updating = useAppSelector(state => state.amortization.updating);
  const updateSuccess = useAppSelector(state => state.amortization.updateSuccess);

  const handleClose = () => {
    navigate(`/amortization${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getLoans({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.installmentNumber !== undefined && typeof values.installmentNumber !== 'number') {
      values.installmentNumber = Number(values.installmentNumber);
    }
    if (values.remainingBalance !== undefined && typeof values.remainingBalance !== 'number') {
      values.remainingBalance = Number(values.remainingBalance);
    }
    if (values.principal !== undefined && typeof values.principal !== 'number') {
      values.principal = Number(values.principal);
    }
    if (values.paymentAmount !== undefined && typeof values.paymentAmount !== 'number') {
      values.paymentAmount = Number(values.paymentAmount);
    }
    if (values.penaltyInterest !== undefined && typeof values.penaltyInterest !== 'number') {
      values.penaltyInterest = Number(values.penaltyInterest);
    }

    const entity = {
      ...amortizationEntity,
      ...values,
      loan: loans.find(it => it.id.toString() === values.loan?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...amortizationEntity,
          loan: amortizationEntity?.loan?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.amortization.home.createOrEditLabel" data-cy="AmortizationCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.amortization.home.createOrEditLabel">
              Create or edit a Amortization
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="amortization-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.installmentNumber')}
                id="amortization-installmentNumber"
                name="installmentNumber"
                data-cy="installmentNumber"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.dueDate')}
                id="amortization-dueDate"
                name="dueDate"
                data-cy="dueDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.remainingBalance')}
                id="amortization-remainingBalance"
                name="remainingBalance"
                data-cy="remainingBalance"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.principal')}
                id="amortization-principal"
                name="principal"
                data-cy="principal"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.paymentDate')}
                id="amortization-paymentDate"
                name="paymentDate"
                data-cy="paymentDate"
                type="date"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.paymentAmount')}
                id="amortization-paymentAmount"
                name="paymentAmount"
                data-cy="paymentAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.amortization.penaltyInterest')}
                id="amortization-penaltyInterest"
                name="penaltyInterest"
                data-cy="penaltyInterest"
                type="text"
              />
              <ValidatedField
                id="amortization-loan"
                name="loan"
                data-cy="loan"
                label={translate('jhipsterSampleApplicationApp.amortization.loan')}
                type="select"
              >
                <option value="" key="0" />
                {loans
                  ? loans.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/amortization" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AmortizationUpdate;
