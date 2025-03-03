import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-data.reducer';

export const UserDataDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userDataEntity = useAppSelector(state => state.userData.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userDataDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.userData.detail.title">UserData</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.id}</dd>
          <dt>
            <span id="salary">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.salary">Salary</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.salary}</dd>
          <dt>
            <span id="familyLoad">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.familyLoad">Family Load</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.familyLoad}</dd>
          <dt>
            <span id="workplace">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.workplace">Workplace</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.workplace}</dd>
          <dt>
            <span id="housingType">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.housingType">Housing Type</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.housingType}</dd>
          <dt>
            <span id="rentCost">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.rentCost">Rent Cost</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.rentCost}</dd>
          <dt>
            <span id="yearsOfEmployment">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.yearsOfEmployment">Years Of Employment</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.yearsOfEmployment}</dd>
          <dt>
            <span id="employmentStatus">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.employmentStatus">Employment Status</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.employmentStatus}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.userData.user">User</Translate>
          </dt>
          <dd>{userDataEntity.user ? userDataEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-data" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-data/${userDataEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserDataDetail;
