// @flow
import type { Node } from 'react';
import React from 'react';
import { ErrorToastContainer } from '../../../components/errors/errorToastContainer';
import background from './background-zeus-temple.jpg';
import logo from './logo-7-wonders.png';

export type HomeLayoutProps = {
  children?: Node,
}

export const HomeLayout = ({children}: HomeLayoutProps) => (
  <div style={{backgroundImage: `url(${background})`}}>
    <img src={logo} alt="Seven Wonders"/>
    {children}
    <ErrorToastContainer/>
  </div>
);
