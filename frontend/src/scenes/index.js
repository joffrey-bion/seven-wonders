import React from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';

import SplashScreen from './SplashScreen';
import Games from './Games';
import Lobby from './Lobby';

const Application = () => (
  <Switch>
    <Route path="/splash-screen" component={SplashScreen} />
    <Route path="/games" component={Games} />
    <Route path="/lobby" component={Lobby} />
    <Redirect from="*" to="/splash-screen" />
  </Switch>
);

export default Application;
