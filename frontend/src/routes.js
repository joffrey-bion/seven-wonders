import { fork } from 'redux-saga/effects'
import usernameChoiceSaga from './sagas/usernameChoice'
import gameBrowserSaga from './sagas/gameBrowser'

export const makeSagaRoutes = wsConnection => ({
  *'/'() {
    yield fork(usernameChoiceSaga, wsConnection)
  },
  *'/games'() {
    yield fork(gameBrowserSaga, wsConnection)
  }
})

import { HomeLayout, LobbyLayout } from './layouts'
import HomePage from './containers/home'
import GameBrowser from './containers/gameBrowser'
import Error404 from './components/errors/Error404'

export const routes = [
  {
    path: '/',
    component: HomeLayout,
    indexRoute: { component: HomePage }
  },
  {
    path: '/',
    component: LobbyLayout,
    childRoutes: [
      { path: '/games', component: GameBrowser }
    ]
  },
  {
    path: '*',
    component: Error404,
  }
]
