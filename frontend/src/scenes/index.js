import React from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';

import { SplashScreen } from './SplashScreen';
import { GameBrowser } from './GameBrowser';
import { Lobby } from './Lobby';

export const Application = () => (
  <Switch>
    <Route path="/splash-screen" component={SplashScreen} />
    <Route path="/games" component={GameBrowser} />
    <Route path="/lobby" component={Lobby} />
    <Redirect from="*" to="/splash-screen" />
  </Switch>
);
