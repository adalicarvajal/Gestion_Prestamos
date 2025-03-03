import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Button, Col, Container, Row } from 'reactstrap';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Container fluid className="d-flex align-items-center justify-content-center">
      <Row className="w-100">
        <Col md="12" className="text-center">
          <h1 className="display-4">
            <Translate contentKey="home.title">Bienvenido a Banco Intercap</Translate>
          </h1>
          <p className="lead">
            <Translate contentKey="home.subtitle">This is your homepage</Translate>
          </p>
          {account?.login ? (
            <>
              <Alert color="success">
                <Translate contentKey="home.logged.message" interpolate={{ username: account.login }}>
                  You are logged in as user {account.login}.
                </Translate>
              </Alert>
              <div className="mt-4">
                <Button color="primary" tag={Link} to="/loan" className="me-3 btn-lg">
                  <Translate contentKey="home.loan">Hacer un préstamo</Translate>
                </Button>
                <Button color="warning" tag={Link} to="/payment" className="me-3 btn-lg">
                  <Translate contentKey="home.payment">Realizar un pago</Translate>
                </Button>
                <Button color="success" tag={Link} to="/amortization" className="btn-lg">
                  <Translate contentKey="home.amortization">Amortizaciones</Translate>
                </Button>
              </div>
            </>
          ) : (
            <>
              <Alert color="warning">
                <Translate contentKey="global.messages.info.authenticated.prefix">
                  If you want to
                </Translate>{' '}
                <Link to="/login" className="alert-link">
                  <Translate contentKey="global.messages.info.authenticated.link"> sign in</Translate>
                </Link>
                <Translate contentKey="global.messages.info.authenticated.suffix">
                  , you can try the default accounts:
                  <br />- Administrator (login=&quot;admin&quot; and password=&quot;admin&quot;)
                  <br />- User (login=&quot;user&quot; and password=&quot;user&quot;).
                </Translate>
              </Alert>

              <Alert color="warning">
                <Translate contentKey="global.messages.info.register.noaccount">
                  You do not have an account yet?
                </Translate>{' '}
                <Link to="/account/register" className="alert-link">
                  <Translate contentKey="global.messages.info.register.link">
                    Register a new account
                  </Translate>
                </Link>
              </Alert>
            </>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default Home;
