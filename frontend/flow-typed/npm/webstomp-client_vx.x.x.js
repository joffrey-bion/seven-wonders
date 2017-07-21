// flow-typed signature: d776c429ba6acfd6ff5187bd78efb074
// flow-typed version: <<STUB>>/webstomp-client_v^1.0.6/flow_v0.46.0

// declare module 'webstomp-client' {
//
//   declare module .exports: {
//     client(url: string): Client;
//     client(url: string, options: Object): Client;
//     over(ws: any): Client;
//     over(ws: any, options: Object): Client;
//   }
// }
//
//   declare type Frame = {
//     body: string,
//     command: string,
//     header: {
//       'heart-beat': number,
//       'user-name': string,
//       version: string
//     }
//   };
//
//   declare type FrameConsumer = (frame: Frame) => void;
//
//   declare type Runnable = () => void;
//
//   declare type Subscription = {
//     id: string,
//     unsubscribe: Runnable
//   };
//
//   declare class Client {
//     constructor(ws: any): Client;
//     constructor(ws: any, options: Object): Client;
//     debug(...args: string[]): void;
//     connect(headers: Object, connectCallback: FrameConsumer): void;
//     connect(headers: Object, connectCallback: FrameConsumer, errorCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer, errorCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer, errorCallback: FrameConsumer, host: string): void;
//     disconnect(disconnectCallback: Runnable): void;
//     disconnect(disconnectCallback: Runnable, headers: Object): void;
//     send(destination: string): void;
//     send(destination: string, body: string): void;
//     send(destination: string, body: string, headers: Object): void;
//     begin(): void;
//     begin(transaction: string): void;
//     commit(transaction: string): void;
//     abort(transaction: string): void;
//     ack(messageID: string, subscription: string): void;
//     ack(messageID: string, subscription: string, headers: Object): void;
//     nack(messageID: string, subscription: string): void;
//     nack(messageID: string, subscription: string, headers: Object): void;
//     subscribe(destination: string, callback: FrameConsumer): Subscription;
//     subscribe(destination: string, callback: FrameConsumer, headers: Object): Subscription;
//     unsubscribe(id: string): void;
//     unsubscribe(id: string, headers: Object): void;
//   }
// }

// declare module 'webstomp-client/dist/webstomp' {
//
//   declare export type Frame = {
//     body: string,
//     command: string,
//     header: {
//       'heart-beat': number,
//       'user-name': string,
//       version: string
//     }
//   };
//
//   declare export type FrameConsumer = (frame: Frame) => void;
//
//   declare export type Runnable = () => void;
//
//   declare export type Subscription = {
//     id: string,
//     unsubscribe: Runnable
//   };
//
//   declare export class Client {
//     constructor(ws: any): Client;
//     constructor(ws: any, options: Object): Client;
//     debug(...args: string[]): void;
//     connect(headers: Object, connectCallback: FrameConsumer): void;
//     connect(headers: Object, connectCallback: FrameConsumer, errorCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer, errorCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer, errorCallback: FrameConsumer, host: string): void;
//     disconnect(disconnectCallback: Runnable): void;
//     disconnect(disconnectCallback: Runnable, headers: Object): void;
//     send(destination: string): void;
//     send(destination: string, body: string): void;
//     send(destination: string, body: string, headers: Object): void;
//     begin(): void;
//     begin(transaction: string): void;
//     commit(transaction: string): void;
//     abort(transaction: string): void;
//     ack(messageID: string, subscription: string): void;
//     ack(messageID: string, subscription: string, headers: Object): void;
//     nack(messageID: string, subscription: string): void;
//     nack(messageID: string, subscription: string, headers: Object): void;
//     subscribe(destination: string, callback: FrameConsumer): Subscription;
//     subscribe(destination: string, callback: FrameConsumer, headers: Object): Subscription;
//     unsubscribe(id: string): void;
//     unsubscribe(id: string, headers: Object): void;
//   }
// }
//
// declare module 'webstomp-client/src/client' {
//
//   declare type Frame = {
//     body: string,
//     command: string,
//     header: {
//       'heart-beat': number,
//       'user-name': string,
//       version: string
//     }
//   };
//
//   declare type FrameConsumer = (frame: Frame) => void;
//
//   declare type Runnable = () => void;
//
//   declare type Subscription = {
//     id: string,
//     unsubscribe: Runnable
//   };
//
//   declare module.exports: {
//     constructor(ws: any): Client;
//     constructor(ws: any, options: Object): Client;
//     debug(...args: string[]): void;
//     connect(headers: Object, connectCallback: FrameConsumer): void;
//     connect(headers: Object, connectCallback: FrameConsumer, errorCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer, errorCallback: FrameConsumer): void;
//     connect(login: string, passcode: string, connectCallback: FrameConsumer, errorCallback: FrameConsumer, host: string): void;
//     disconnect(disconnectCallback: Runnable): void;
//     disconnect(disconnectCallback: Runnable, headers: Object): void;
//     send(destination: string): void;
//     send(destination: string, body: string): void;
//     send(destination: string, body: string, headers: Object): void;
//     begin(): void;
//     begin(transaction: string): void;
//     commit(transaction: string): void;
//     abort(transaction: string): void;
//     ack(messageID: string, subscription: string): void;
//     ack(messageID: string, subscription: string, headers: Object): void;
//     nack(messageID: string, subscription: string): void;
//     nack(messageID: string, subscription: string, headers: Object): void;
//     subscribe(destination: string, callback: FrameConsumer): Subscription;
//     subscribe(destination: string, callback: FrameConsumer, headers: Object): Subscription;
//     unsubscribe(id: string): void;
//     unsubscribe(id: string, headers: Object): void;
//   };
// }
