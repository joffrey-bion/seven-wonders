// @flow
import * as React from 'react';
import { Flex } from 'reflexbox';
import { ChooseNameForm } from './ChooseNameForm';
import './Home.css'
import logo from './logo-7-wonders.png';

export const Home = () => (
  <Flex className='homeRoot fullscreen' column align='center' justify='center'>
      <img src={logo} alt="Seven Wonders"/>
      <ChooseNameForm/>
  </Flex>
);
