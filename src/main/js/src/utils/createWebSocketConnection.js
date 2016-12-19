import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
const wsURL = 'http://localhost:8080/seven-wonders-websocket'

let socket = null
export default () => {
  return new Promise((resolve, reject) => {
    socket = Stomp.over(new SockJS(wsURL))
    socket.connect({}, (frame) => {
      return resolve({ frame, socket })
    }, reject)
  })
}