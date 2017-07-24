// @flow
import type { Children } from 'react';
import React from 'react';
import { Banner } from 'rebass';
import ErrorToastContainer from '../components/errors/errorToastContainer';
import background from './background-zeus-temple.jpg';
import logo from './logo-7-wonders.png';

export default ({ children }: { children: Children }) => (
  <div>
    <Banner align="center" backgroundImage={background}>
      <img src={logo} alt="Seven Wonders" />
      {children}
    </Banner>
    <ErrorToastContainer />
  </div>
);
