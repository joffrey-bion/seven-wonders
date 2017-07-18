// @flow
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import { eventChannel } from 'redux-saga';

const wsURL = '/seven-wonders-websocket';

export type FrameType = {
  body: string,
  command: string,
  header: {
    'heart-beat': number,
    'user-name': string,
    version: string
  }
};
export type SocketType = {
  connect: (headers: Object, onSucces: (frame: FrameType) => void, onReject: (error: any) => void) => void,
  subscribe: (path: string, callback: (event: any) => void) => Object
};
export type SocketSubscriptionType = {
  id: string,
  unsubscribe: () => void
};
export type SocketEventType = {
  body: string
};
export type SocketObjectType = {
  frame: FrameType,
  socket: SocketType
};

export const createWsConnection = (headers: Object = {}): Promise<SocketObjectType> =>
  new Promise((resolve, reject) => {
    let socket: SocketType = Stomp.over(new SockJS(wsURL), {
      debug: process.env.NODE_ENV !== 'production',
    });
    socket.connect(headers, (frame: FrameType) => resolve({ frame, socket }), reject);
  });

export const createSubscriptionChannel = (socket: SocketType, path: string) => {
  return eventChannel((emitter: (data: any) => void) => {
    const socketSubscription: SocketSubscriptionType = socket.subscribe(path, (event: SocketEventType) => {
      // not all events have a body
      const value = event.body && JSON.parse(event.body);
      emitter(value);
    });
    return () => socketSubscription.unsubscribe();
  });
};
