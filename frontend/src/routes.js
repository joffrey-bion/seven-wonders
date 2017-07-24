// @flow
import { HomeLayout, LobbyLayout } from './layouts';
import GameBrowser from './containers/gameBrowser';
import HomePage from './containers/home';
import Lobby from './containers/lobby';
import Error404 from './components/errors/Error404';

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
