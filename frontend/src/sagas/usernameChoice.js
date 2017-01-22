import { call, take, put } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'
import { push } from 'react-router-redux'

import { actions, types } from '../redux/user'

function usernameValidationChannel(socket) {
  return eventChannel(emitter => {
    const receiveUsernameHandler = socket.subscribe('/user/queue/nameChoice', event => {
      emitter(JSON.parse(event.body))
    })

    const unsubscribe = () => receiveUsernameHandler.unsubscribe()

    return unsubscribe
  })
}

function *usernameValidation({ socket }) {
  const usernameChannel = usernameValidationChannel(socket)

  const user = yield take(usernameChannel)
  yield put(actions.setUsername(user.username, user.displayName, user.index))
  usernameChannel.close()
  yield put(push('/games'))
}

function *sendUsername({ socket }) {
  const { username } = yield take(types.CHOOSE_USERNAME)

  yield socket.send('/app/chooseName', JSON.stringify({
    playerName: username
  }))
}

function *usernameChoiceSaga(wsConnection) {
  // TODO: Run sendUsername in loop when we have the ability to cancel saga on route change
  yield [
    call(sendUsername, wsConnection),
    call(usernameValidation, wsConnection),
  ]
}

export default usernameChoiceSaga
