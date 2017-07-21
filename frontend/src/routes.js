// @flow
import { fork } from 'redux-saga/effects';
import homeSaga from './sagas/home';
import gameBrowserSaga from './sagas/gameBrowser';
import lobbySaga from './sagas/lobby';

import { HomeLayout, LobbyLayout } from './layouts';
import HomePage from './containers/home';
import GameBrowser from './containers/gameBrowser';
import Lobby from './containers/lobby';
import Error404 from './components/errors/Error404';
import { SevenWondersSession } from './api/sevenWondersApi';

export const makeSagaRoutes = (sevenWondersSession: SevenWondersSession) => ({
  *'/'() {
    yield fork(homeSaga, sevenWondersSession);
  },
  *'/games'() {
    yield fork(gameBrowserSaga, sevenWondersSession);
  },
  *'/lobby/*'() {
    yield fork(lobbySaga, sevenWondersSession);
  },
});

export const routes = [
  {
    path: '/',
    component: HomeLayout,
    indexRoute: { component: HomePage },
  },
  {
    path: '/',
    component: LobbyLayout,
    childRoutes: [{ path: '/games', component: GameBrowser }, { path: '/lobby/*', component: Lobby }],
  },
  {
    path: '*',
    component: Error404,
  },
];
