import React from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import { GameBrowser } from '../components/game-browser/GameBrowser';
import { Lobby } from './Lobby';
import { Home } from '../components/home/Home';

export const Application = () => (
  <Switch>
    <Route path="/games" component={GameBrowser} />
    <Route path="/lobby" component={Lobby} />
    <Route path="/" component={Home} />
    <Redirect to="/" />
  </Switch>
);
