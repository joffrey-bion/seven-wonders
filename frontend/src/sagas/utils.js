import { SevenWondersSession } from '../api/sevenWondersApi';
import { eventChannel } from 'redux-saga';

type Emitter = (value: any) => void;

export function createChannel(session: SevenWondersSession, methodWithCallback: () => void, ...args: Array<any>) {
  return eventChannel((emitter: Emitter) => {
    return methodWithCallback.call(session, ...args, emitter);
  });
}
