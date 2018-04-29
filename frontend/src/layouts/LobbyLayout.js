import type { Children } from 'react';
import React from 'react';
import { Banner } from 'rebass';
import { ErrorToastContainer } from '../components/errors/errorToastContainer';
import logo from './logo-7-wonders.png';

export const LobbyLayout = ({ children }: { children: Children }) => (
  <div>
    <Banner
      align="center"
      style={{ minHeight: '30vh', marginBottom: 0 }}
      backgroundImage="https://images.unsplash.com/photo-1431207446535-a9296cf995b1?dpr=1&auto=format&fit=crop&w=1199&h=799&q=80&cs=tinysrgb&crop="
    >
      <img src={logo} alt="Seven Wonders Logo" />
    </Banner>
    {children}
    <ErrorToastContainer />
  </div>
);
