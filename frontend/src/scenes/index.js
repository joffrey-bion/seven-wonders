import React from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import { GameBrowser } from '../components/game-browser/GameBrowser';
import { Lobby } from './Lobby';
import { SplashScreen } from './SplashScreen';

export const Application = () => (
  <Switch>
    <Route path="/splash-screen" component={SplashScreen} />
    <Route path="/games" component={GameBrowser} />
    <Route path="/lobby" component={Lobby} />
    <Redirect from="*" to="/splash-screen" />
  </Switch>
);
