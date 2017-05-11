import { call, put, take, apply } from 'redux-saga/effects'
import { eventChannel} from 'redux-saga'
import { fromJS } from 'immutable'
import { push } from 'react-router-redux'

import { normalize } from 'normalizr'
import { game, gameList } from '../schemas/games'

import { actions as gameActions, types } from '../redux/games'
import { actions as playerActions } from '../redux/players'

function gameBrowserChannel(socket) {
  return eventChannel(emit => {

    const makeHandler = type => event => {
      const response = JSON.parse(event.body)
      emit({ type, response })
    }

    const newGame = socket.subscribe('/topic/games', makeHandler(types.UPDATE_GAMES))
    const joinGame = socket.subscribe('/user/queue/lobby/joined', makeHandler(types.ENTER_LOBBY))

    return () => {
      newGame.unsubscribe()
      joinGame.unsubscribe()
    }
  })
}

export function *watchGames({ socket }) {
  const socketChannel = gameBrowserChannel(socket)

  try {
    while (true) {
      const { type, response } = yield take(socketChannel)

      switch (type) {
        case types.UPDATE_GAMES:
          const normGameList = normalize(response, gameList)
          yield put(playerActions.updatePlayers(fromJS(normGameList.entities.players)))
          yield put(gameActions.updateGames(fromJS(normGameList.entities.games)))
          break
        case types.ENTER_LOBBY:
          const normGame = normalize(response, game)
          yield put(gameActions.enterLobby(fromJS(normGame.entities.games[normGame.result])))
          socketChannel.close()
          yield put(push('/lobby'))
          break
        default:
          console.error('Unknown type')
      }
    }
  } finally {
    console.info('gameBrowserChannel closed')
  }
}

export function *createGame({ socket }) {
  const { name } = yield take(types.REQUEST_CREATE_GAME)

  yield apply(socket, socket.send, ["/app/lobby/create", JSON.stringify({ gameName: name }), {}])
}

export function *joinGame({ socket }) {
  const { id } = yield take(types.REQUEST_JOIN_GAME)

  yield apply(socket, socket.send, ["/app/lobby/join", JSON.stringify({ gameId: id }), {}])
}

export function *gameBrowserSaga(socketConnection) {
  yield [
    call(watchGames, socketConnection),
    call(createGame, socketConnection),
    call(joinGame, socketConnection)
  ]
}

export default gameBrowserSaga
