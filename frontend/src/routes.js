// @flow
import React from 'react';

import Application from './scenes';
import Error404 from './components/errors/Error404';

import { Route, Switch } from 'react-router';

const Routes = () => (
  <Switch>
    <Route path="/" component={Application} />
    <Route path="*" component={Error404} />
  </Switch>
);

export default Routes;
// export const routes = [
//   {
//     path: '/',
//     component: HomeLayout,
//     indexRoute: { component: HomePage },
//   },
//   {
//     path: '/',
//     component: LobbyLayout,
//     childRoutes: [{ path: '/games', component: GameBrowser }, { path: '/lobby/*', component: Lobby }],
//   },
//   {
//     path: '*',
//     component: Error404,
//   },
// ];
