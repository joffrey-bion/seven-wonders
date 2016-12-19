import { call, put, take } from 'redux-saga/effects'
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
const wsURL = '/seven-wonders-websocket'
let socket = null;

const openSocket = () => {
    socket = Stomp.over(new SockJS(wsURL))
    socket.connect({}, (frame) => {
        console.log('Connected')

        socket.subscribe('/user/queue/errors', (msg) =>
            console.error('/user/queue/errors', msg)
        )

        socket.subscribe('/topic/games', (msg) => {
            const game = JSON.parse(msg)
            console.error('/topic/games', game)
        })

        socket.subscribe('/user/queue/join-game', (msg) => {
            const game = JSON.parse(msg)
            console.error('/user/queue/join-game', game)
        })
    })
}

const disconnect = () => {
    if (!socket) {
        socket.disconnect()
        console.log('Disconnected')
    }
}

export default function* wsSagas() {
    const channel = yield call(openSocket)

    while (true) {
        const action = yield take(channel)
        yield put(action)
    }
}
