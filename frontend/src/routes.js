// @flow
import { fork } from 'redux-saga/effects';
import { SevenWondersSession } from './api/sevenWondersApi';
import Error404 from './components/errors/Error404';
import GameBrowser from './containers/gameBrowser';
import HomePage from './containers/home';
import Lobby from './containers/lobby';
import { HomeLayout, LobbyLayout } from './layouts';
import gameBrowserSaga from './sagas/gameBrowser';
import homeSaga from './sagas/home';
import lobbySaga from './sagas/lobby';

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
