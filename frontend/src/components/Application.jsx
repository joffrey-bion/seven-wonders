import React from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import { GameBrowser } from './game-browser/GameBrowser';
import { Lobby } from './lobby/Lobby';
import { Home } from './home/Home';

export const Application = () => (
  <Switch>
    <Route path="/games" component={GameBrowser} />
    <Route path="/lobby" component={Lobby} />
    <Route path="/" component={Home} />
    <Redirect to="/" />
  </Switch>
);
