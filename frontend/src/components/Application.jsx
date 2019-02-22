import React from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import { GameBrowser } from './game-browser/GameBrowser';
import { GameScene } from './game/GameScene';
import { Lobby } from './lobby/Lobby';
import { Home } from './home/Home';

export const Application = () => (
  <Switch>
    <Route path="/game" component={GameScene} />
    <Route path="/games" component={GameBrowser} />
    <Route path="/lobby" component={Lobby} />
    <Route path="/" component={Home} />
    <Redirect to="/" />
  </Switch>
);
