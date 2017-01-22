import { spawn } from 'redux-saga/effects'
import usernameChoiceSaga from './sagas/usernameChoice'
import gameBrowserSaga from './sagas/gameBrowser'

// TODO: Remove spawn saga when redux-saga-router has cancelable saga on route change
export const makeSagaRoutes = wsConnection => ({
  *'/'() {
    // FIXME: spawn is a memory whore because the saga lives forever
    yield spawn(usernameChoiceSaga, wsConnection)
  },
  *'/games'() {
    yield spawn(gameBrowserSaga, wsConnection)
  }
})

import { LobbyLayout } from './layouts'
import HomePage from './containers/home'
import GameBrowser from './containers/gameBrowser'
import Error404 from './components/errors/Error404'

export const routes = [
  {
    path: '/',
    component: LobbyLayout,
    indexRoute: { component: HomePage },
    childRoutes: [
      { path: '/games', component: GameBrowser }
    ]
  },
  {
    path: '*',
    component: Error404,
  }
]
