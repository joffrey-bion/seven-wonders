import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import { eventChannel } from 'redux-saga'

const wsURL = '/seven-wonders-websocket'

export const createWsConnection = (headers = {}) => new Promise((resolve, reject) => {
    let socket = Stomp.over(new SockJS(wsURL), {
      debug: process.env.NODE_ENV !== "production"
    })
    socket.connect(headers, (frame) => {
      return resolve({ frame, socket })
    }, reject)
})

export const createSubscriptionChannel = (socket, path) => {
  return eventChannel(emitter => {
    const receiveUsernameHandler = socket.subscribe(path, event => {
      emitter(JSON.parse(event.body))
    })
    return () => receiveUsernameHandler.unsubscribe()
  })
}
