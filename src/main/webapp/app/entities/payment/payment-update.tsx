import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity as getLoan, updateEntity as updateLoan } from '../loan/loan.reducer';
import { getEntities as getAmortizations, createEntity as createAmortization } from '../amortization/amortization.reducer';

export interface IQueryParams {
  page?: number;
  size?: number;
  sort?: string;
  loanId?: string; // Añadir esta línea para incluir loanId
}

export const PaymentUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const loanEntity = useAppSelector(state => state.loan.entity);
  const amortizationList = useAppSelector(state => state.amortization.entities);
  const loading = useAppSelector(state => state.loan.loading);
  const updating = useAppSelector(state => state.loan.updating);
  const currentUser = useAppSelector(state => state.authentication.account);

  const [paymentAmount, setPaymentAmount] = useState(0);

  useEffect(() => {
    dispatch(getLoan(id));
    const queryParams: IQueryParams = { loanId: id }; // Usar el tipo actualizado
    dispatch(getAmortizations(queryParams));
  }, [id]);

  useEffect(() => {
    if (loanEntity && loanEntity.paymentTermMonths) {
      const monthlyPayment = loanEntity.requestedAmount / loanEntity.paymentTermMonths;
      setPaymentAmount(monthlyPayment);
    }
  }, [loanEntity]);

  const handleClose = () => {
    navigate('/payment');
  };

  const savePayment = values => {
    const amortization = {
      ...values,
      loan: { id },
    };

    dispatch(createAmortization(amortization)).then(() => {
      const updatedLoan = {
        ...loanEntity,
        requestedAmount: loanEntity.requestedAmount - values.paymentAmount,
      };
      dispatch(updateLoan(updatedLoan));
      handleClose();
    });
  };

  const defaultValues = () => ({
    paymentAmount,
  });

  const filteredAmortizations = amortizationList.filter(amortization =>
    amortization.loan.id === id
  );

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.pagos.home.createOrEditLabel" data-cy="LoanCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.pagos.home.createOrEditLabel">Pagar Préstamo</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={savePayment}>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.pagos.paymentAmount')}
                id="amortization-paymentAmount"
                name="paymentAmount"
                data-cy="paymentAmount"
                type="text"
                value={paymentAmount}
                onChange={e => setPaymentAmount(Number(e.target.value))}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Pagar</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="amortization-heading" data-cy="AmortizationHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.amortization.home.title">Amortizaciones</Translate>
          </h2>
          <div className="table-responsive">
            {filteredAmortizations && filteredAmortizations.length > 0 ? (
              <Table responsive>
                <thead>
                <tr>
                  <th>
                    <Translate contentKey="jhipsterSampleApplicationApp.amortization.installmentNumber">Número de Cuota</Translate>
                  </th>
                  <th>
                    <Translate contentKey="jhipsterSampleApplicationApp.amortization.dueDate">Fecha de Vencimiento</Translate>
                  </th>
                  <th>
                    <Translate contentKey="jhipsterSampleApplicationApp.amortization.remainingBalance">Saldo Restante</Translate>
                  </th>
                  <th>
                    <Translate contentKey="jhipsterSampleApplicationApp.amortization.principal">Principal</Translate>
                  </th>
                  <th>
                    <Translate contentKey="jhipsterSampleApplicationApp.amortization.paymentAmount">Monto de Pago</Translate>
                  </th>
                  <th>
                    <Translate contentKey="jhipsterSampleApplicationApp.amortization.penaltyInterest">Interés de Penalización</Translate>
                  </th>
                </tr>
                </thead>
                <tbody>
                {filteredAmortizations.map((amortization, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>{amortization.installmentNumber}</td>
                    <td>{amortization.dueDate}</td>
                    <td>{amortization.remainingBalance}</td>
                    <td>{amortization.principal}</td>
                    <td>{amortization.paymentAmount}</td>
                    <td>{amortization.penaltyInterest}</td>
                  </tr>
                ))}
                </tbody>
              </Table>
            ) : (
              <div className="alert alert-warning">
                <Translate contentKey="jhipsterSampleApplicationApp.amortization.home.notFound">No se encontraron Amortizaciones</Translate>
              </div>
            )}
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default PaymentUpdate;