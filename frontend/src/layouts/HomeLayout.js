// @flow
import React, { type Children } from 'react';
import { Banner } from 'rebass';
import logo from './logo-7-wonders.png';
import background from './background-zeus-temple.jpg';
import ErrorToastContainer from '../components/errors/errorToastContainer';

export default ({ children }: { children: Children }) => (
  <div>
    <Banner align="center" backgroundImage={background}>
      <img src={logo} alt="Seven Wonders" />
      {children}
    </Banner>
    <ErrorToastContainer />
  </div>
);
