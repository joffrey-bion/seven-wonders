import usernameChoiceSaga from './sagas/usernameChoice'
import gameBrowserSaga from './sagas/gameBrowser'

export const makeSagaRoutes = wsConnection => ({
  *'/'() {
    yield usernameChoiceSaga(wsConnection)
  },
  *'/games'() {
    yield gameBrowserSaga(wsConnection)
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
