import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { createEntity, getEntity, reset, updateEntity } from './user-data.reducer';

export const UserDataUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const userDataEntity = useAppSelector(state => state.userData.entity);
  const loading = useAppSelector(state => state.userData.loading);
  const updating = useAppSelector(state => state.userData.updating);
  const updateSuccess = useAppSelector(state => state.userData.updateSuccess);

  const handleClose = () => {
    navigate('/user-data');
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
    if (values.salary !== undefined && typeof values.salary !== 'number') {
      values.salary = Number(values.salary);
    }
    if (values.familyLoad !== undefined && typeof values.familyLoad !== 'number') {
      values.familyLoad = Number(values.familyLoad);
    }
    if (values.rentCost !== undefined && typeof values.rentCost !== 'number') {
      values.rentCost = Number(values.rentCost);
    }
    if (values.yearsOfEmployment !== undefined && typeof values.yearsOfEmployment !== 'number') {
      values.yearsOfEmployment = Number(values.yearsOfEmployment);
    }
    if (values.employmentStatus !== undefined && typeof values.employmentStatus !== 'number') {
      values.employmentStatus = Number(values.employmentStatus);
    }

    const entity = {
      ...userDataEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
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
        ...userDataEntity,
        user: userDataEntity?.user?.id,
      };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.userData.home.createOrEditLabel" data-cy="UserDataCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.userData.home.createOrEditLabel">Create or edit a UserData</Translate>
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
                  id="user-data-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                id="user-data-user"
                name="user"
                data-cy="user"
                label={translate('jhipsterSampleApplicationApp.userData.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.login}
                    </option>
                  ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.salary')}
                id="user-data-salary"
                name="salary"
                data-cy="salary"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.familyLoad')}
                id="user-data-familyLoad"
                name="familyLoad"
                data-cy="familyLoad"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.workplace')}
                id="user-data-workplace"
                name="workplace"
                data-cy="workplace"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.housingType')}
                id="user-data-housingType"
                name="housingType"
                data-cy="housingType"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.rentCost')}
                id="user-data-rentCost"
                name="rentCost"
                data-cy="rentCost"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.yearsOfEmployment')}
                id="user-data-yearsOfEmployment"
                name="yearsOfEmployment"
                data-cy="yearsOfEmployment"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.userData.employmentStatus')}
                id="user-data-employmentStatus"
                name="employmentStatus"
                data-cy="employmentStatus"
                type="select"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              >
                <option value="0">Activo</option>
                <option value="1">Inactivo</option>
                <option value="2">Suspendido</option>
                <option value="3">Terminado</option>
                <option value="4">En licencia</option>
                <option value="5">Retirado</option>
                <option value="6">En entrenamiento</option>
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-data" replace color="info">
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

export default UserDataUpdate;
