// @flow
import SockJS from 'sockjs-client';
import type { Client, Frame, Message, Options, Subscription } from 'webstomp-client';
import * as Stomp from 'webstomp-client';

const DEFAULT_DEBUG_OPTIONS = {
  debug: process.env.NODE_ENV !== 'production',
};

export type Callback<T> = (value: T) => void;
export type UnsubscribeFn = () => void;
export type SubscribeFn<T> = (callback: Callback<T>) => UnsubscribeFn;

export class JsonStompClient {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  connect(headers: Object = {}): Promise<Frame | void> {
    return new Promise((resolve, reject) => {
      this.client.connect(headers, resolve, reject);
    });
  }

  subscribe<T>(path: string, callback: Callback<T>): UnsubscribeFn {
    const socketSubscription: Subscription = this.client.subscribe(path, (message: Message) => {
      // not all frames have a JSON body
      const value: T | void = message && JsonStompClient.parseBody(message);
      callback(value || {});
    });
    return () => socketSubscription.unsubscribe();
  }

  static parseBody<T>(message: Message): T | void {
    try {
      return message.body ? JSON.parse(message.body) : undefined;
    } catch (jsonParseError) {
      throw new Error('Cannot parse websocket message as JSON: ' + jsonParseError.message);
    }
  }

  subscriber<T>(path: string): SubscribeFn<T> {
    return (callback: Callback<T>) => this.subscribe(path, callback);
  }

  send(url: string, body?: Object) {
    const strBody = body ? JSON.stringify(body) : '';
    this.client.send(url, strBody);
  }
}

function createStompClient(url: string, options: Options = {}): Client {
  const optionsWithDebug = Object.assign({}, DEFAULT_DEBUG_OPTIONS, options);
  return Stomp.over(new SockJS(url), optionsWithDebug);
}

export function createJsonStompClient(url: string, options: Options = {}): JsonStompClient {
  return new JsonStompClient(createStompClient(url, options));
}
