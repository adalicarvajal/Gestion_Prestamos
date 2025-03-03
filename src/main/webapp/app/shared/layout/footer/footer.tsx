import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = () => (
  <div className="footer page-content d-flex justify-content-center text-center">
    <Row className="w-100">
      <Col md="12">
        <p>
          <Translate contentKey="footer">Copyright 2025</Translate>
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
