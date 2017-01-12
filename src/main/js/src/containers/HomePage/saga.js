import { call, put, take } from 'redux-saga/effects'
import { push } from 'react-router-redux'
import { ENTER_GAME } from './actions'
import { setUsername } from '../UserRepo/actions'

function* initGameSession(socketConnection) {
  const { username: playerName } = yield take(ENTER_GAME)
  const { socket } = socketConnection

  socket.send("/app/chooseName", JSON.stringify({
    playerName
  }), {})
  // TODO: get response from server to continue
  // TODO: handle case where username is taken

  yield put(setUsername(playerName))
  yield put(push('/lobby'))
}

function* homeSaga(socketConnection) {
  yield call(initGameSession, socketConnection)
}

export default homeSaga
