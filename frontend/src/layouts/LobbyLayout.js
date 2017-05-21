import React from 'react';
import { Banner } from 'rebass';
import logo from './logo-7-wonders.png';
import ErrorToastContainer from '../components/errors/errorToastContainer';

export default props => (
  <div>
    <Banner
      align="center"
      style={{ minHeight: '30vh', marginBottom: 0 }}
      backgroundImage="https://images.unsplash.com/photo-1431207446535-a9296cf995b1?dpr=1&auto=format&fit=crop&w=1199&h=799&q=80&cs=tinysrgb&crop="
    >
      <img src={logo} alt="Seven Wonders Logo" />
    </Banner>
    {props.children}
    <ErrorToastContainer />
  </div>
);
