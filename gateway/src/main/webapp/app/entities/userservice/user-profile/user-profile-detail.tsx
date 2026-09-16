import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-profile.reducer';

export const UserProfileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const userProfileEntity = useAppSelector(state => state.gateway.userProfile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userProfileDetailsHeading">
          <Translate contentKey="gatewayApp.userserviceUserProfile.detail.title">UserProfile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="gatewayApp.userserviceUserProfile.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.userId}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.userserviceUserProfile.name">Name</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.name}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="gatewayApp.userserviceUserProfile.email">Email</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="gatewayApp.userserviceUserProfile.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.phone}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="gatewayApp.userserviceUserProfile.address">Address</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.address}</dd>
        </dl>
        <Button as={Link as any} to="/user-profile" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/user-profile/${userProfileEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserProfileDetail;
