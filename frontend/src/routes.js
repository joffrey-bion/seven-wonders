import { fork, take, cancel } from 'redux-saga/effects'
import usernameChoiceSaga from './sagas/usernameChoice'
import gameBrowserSaga from './sagas/gameBrowser'
import { LOCATION_CHANGE } from 'react-router-redux'

export const makeSagaRoutes = wsConnection => ({
  *'/'() {
    const saga = yield fork(usernameChoiceSaga, wsConnection)
    yield take(LOCATION_CHANGE)
    yield cancel(saga)
    yield console.log('canceled home')
  },
  *'/games'() {
    const saga = yield fork(gameBrowserSaga, wsConnection)
    yield take(LOCATION_CHANGE)
    yield cancel(saga)
    yield console.log('canceled games')
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
