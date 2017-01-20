import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
const wsURL = 'http://localhost:8080/seven-wonders-websocket'

const createConnection = (headers = {}) => new Promise((resolve, reject) => {
    let socket = Stomp.over(new SockJS(wsURL), {
      debug: process.env.NODE_ENV !== "production"
    })
    socket.connect(headers, (frame) => {
      return resolve({ frame, socket })
    }, reject)
})

export default createConnection
