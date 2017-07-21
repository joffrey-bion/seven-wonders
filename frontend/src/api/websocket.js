// @flow
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import type { Client, Frame, Subscription } from 'webstomp-client';

import { eventChannel } from 'redux-saga';
import type { Channel } from 'redux-saga';

function createStompClient(url: string): Client {
  return Stomp.over(new SockJS(url), {
    debug: process.env.NODE_ENV !== 'production',
  });
}

export function createStompSession(url: string, headers: Object = {}): Promise<Client> {
  return new Promise((resolve, reject) => {
    const client: Client = createStompClient(url);
    const onSuccess = (frame: Frame) => resolve(client);
    client.connect(headers, onSuccess, reject);
  });
}

export function createJsonSubscriptionChannel(client: Client, path: string): Channel {
  return eventChannel((emitter: (data: any) => void) => {
    const socketSubscription: Subscription = client.subscribe(path, (frame: Frame) => {
      // not all frames have a JSON body
      const value = frame && frame.body && JSON.parse(frame.body);
      emitter(value);
    });
    return () => socketSubscription.unsubscribe();
  });
}
