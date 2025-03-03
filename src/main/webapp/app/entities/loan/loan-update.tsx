import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import { createEntity as createAmortization } from '../amortization/amortization.reducer';
import dayjs from 'dayjs';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { toast } from 'react-toastify';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { createEntity, getEntity, reset, updateEntity } from './loan.reducer';

export const LoanUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const loanEntity = useAppSelector(state => state.loan.entity);
  const loading = useAppSelector(state => state.loan.loading);
  const updating = useAppSelector(state => state.loan.updating);
  const updateSuccess = useAppSelector(state => state.loan.updateSuccess);
  const currentUser = useAppSelector(state => state.authentication.account);

  const handleClose = () => {
    navigate(`/loan${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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
    if (values.requestedAmount !== undefined && typeof values.requestedAmount !== 'number') {
      values.requestedAmount = Number(values.requestedAmount);
    }
    if (values.interestRate !== undefined && typeof values.interestRate !== 'number') {
      values.interestRate = Number(values.interestRate);
    }
    if (values.paymentTermMonths !== undefined && typeof values.paymentTermMonths !== 'number') {
      values.paymentTermMonths = Number(values.paymentTermMonths);
    }

    const entity = {
      ...loanEntity,
      ...values,
      user: currentUser,
      status: values.status || 0,
    };

    if (isNew) {
      dispatch(createEntity(entity)).unwrap().then(response => {
        const responseStr = JSON.stringify(response);
        const loanId = JSON.parse(responseStr).data.id;
        if (loanId) {
          const amortization = {
            installmentNumber: 1,
            dueDate: dayjs().add(1, 'month'),
            remainingBalance: values.requestedAmount,
            principal: values.requestedAmount,
            paymentAmount: values.requestedAmount + (values.requestedAmount * values.interestRate / 100),
            penaltyInterest: 0,
            loan: { id: loanId },
          };
          dispatch(createAmortization(amortization));
        }
      });
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
        ...loanEntity,
        user: loanEntity?.user?.id,
      };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.prestamos.home.createOrEditLabel" data-cy="LoanCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.prestamos.home.createOrEditLabel">Create or edit a Loan</Translate>
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
                  id="loan-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                id="loan-requestedAmount"
                label={translate('jhipsterSampleApplicationApp.prestamos.requestedAmount')}
                name="requestedAmount"
                data-cy="requestedAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.interestRate')}
                id="loan-interestRate"
                name="interestRate"
                data-cy="interestRate"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.paymentTermMonths')}
                id="loan-paymentTermMonths"
                name="paymentTermMonths"
                data-cy="paymentTermMonths"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.applicationDate')}
                id="loan-applicationDate"
                name="applicationDate"
                data-cy="applicationDate"
                type="date"
                defaultValue={new Date().toISOString().split('T')[0]}
                readOnly
              />
              <input type="hidden" name="user" value={currentUser.id} />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/loan" replace color="info">
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

export default LoanUpdate;
