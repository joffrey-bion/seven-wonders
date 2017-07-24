/* eslint-disable */

// Type definitions for webstomp-client v1.0.x
// Project: https://github.com/JSteunou/webstomp-client
// Definitions by: Jimi Charalampidis <https://github.com/JimiC>
declare module 'webstomp-client' {
  declare export class Frame {
    constructor(command: string, headers?: {}, body?: string): Frame,

    toString(): string,
    sizeOfUTF8(s: string): number,
    unmarshall(datas: any): any,
    marshall(command: string, headers?: {}, body?: string): any
 }

  declare type VERSIONS = {
    V1_0: string,
    V1_1: string,
    V1_2: string,
    // Versions of STOMP specifications supported
    supportedVersions: () => string,
    supportedProtocols: () => Array<string>
  };

  declare export type Heartbeat = {
    outgoing: number,
    incoming: number
  };

  declare export type Subscription = {
    id: string,
    unsubscribe: () => void
  };

  declare export type Message = {
    command: string,
    body: string,
    headers: ExtendedHeaders,
    ack(headers?: AckHeaders): any,
    nack(headers?: NackHeaders): any
  };

  declare export type Options = {
    protocols?: Array<string>,
    ...ClientOptions
  };

  declare export type ClientOptions = {
    binary: boolean,
    heartbeat: Heartbeat | boolean,
    debug: boolean
  };

  declare export type ConnectionHeaders = {
    login: string,
    passcode: string,
    host?: string
  };

  declare export type DisconnectHeaders = {
    'receipt'?: string
  };

  declare export type StandardHeaders = {
    'content-length'?: string,
    'content-type'?: string,
    ...DisconnectHeaders
  };

  declare export type ExtendedHeaders = {
    'amqp-message-id'?: string,
    'app-id'?: string,
    'content-encoding'?: string,
    'correlation-id'?: string,
    custom?: string,
    destination?: string,
    'message-id'?: string,
    persistent?: string,
    redelivered?: string,
    'reply-to'?: string,
    subscription?: string,
    timestamp?: string,
    type?: string,
    ...StandardHeaders
  };

  declare export type UnsubscribeHeaders = {
    id?: string,
    ...StandardHeaders
  };

  declare export type SubscribeHeaders = {
    ack?: string,
    ...UnsubscribeHeaders
  };

  declare export type AckHeaders = {
    transaction?: string,
    ...UnsubscribeHeaders
  };

  declare export type NackHeaders = {
    ...AckHeaders
  };

  declare function client(url: string, options?: Options): Client;

  declare function over(socketType: any, options?: Options): Client;

  declare export type Stomp = {
    VERSIONS: VERSIONS,
    client(url: string, options?: Options): Client,
    over(socketType: any, options?: Options): Client
  }

  declare export default Stomp

  declare export class Client {
    connect(
      headers: ConnectionHeaders,
      connectCallback: (frame?: Frame) => any,
      errorCallback?: (error: string) => any
    ): void,
    connect(
      login: string,
      passcode: string,
      connectCallback: (frame?: Frame) => any,
      errorCallback?: (error: string) => any,
      host?: string
    ): void,

    disconnect(disconnectCallback: () => any, headers?: DisconnectHeaders): void,

    send(destination: string, body?: string, headers?: ExtendedHeaders): void,

    subscribe(destination: string, callback?: (message: Message) => any, headers?: SubscribeHeaders): Subscription,

    unsubscribe(id: string, header?: UnsubscribeHeaders): void,

    begin(transaction: string): void,

    commit(transaction: string): void,

    abort(transaction: string): void,

    ack(messageID: string, subscription: Subscription, headers?: AckHeaders): void,

    nack(messageID: string, subscription: Subscription, headers?: NackHeaders): void
  }
}
