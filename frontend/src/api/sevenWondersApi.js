import { createJsonSubscriptionChannel, createStompSession } from './websocket';
import type { Client } from 'webstomp-client';
import type { Channel } from 'redux-saga';

const wsURL = '/seven-wonders-websocket';

export class SevenWondersSession {
  client: Client;

  constructor(client: Client) {
    this.client = client;
  }

  watchErrors(): Channel<ApiError> {
    return createJsonSubscriptionChannel(this.client, '/user/queue/errors');
  }

  chooseName(displayName: string): void {
    this.client.send('/app/chooseName', JSON.stringify({ playerName: displayName }));
  }

  watchNameChoice(): Channel<Object> {
    return createJsonSubscriptionChannel(this.client, '/user/queue/nameChoice');
  }

  watchGames(): Channel<Object> {
    return createJsonSubscriptionChannel(this.client, '/topic/games');
  }

  watchLobbyJoined(): Channel<Object> {
    return createJsonSubscriptionChannel(this.client, '/user/queue/lobby/joined');
  }

  watchLobbyUpdated(currentGameId: number): Channel<Object> {
    return createJsonSubscriptionChannel(this.client, `/topic/lobby/${currentGameId}/updated`);
  }

  watchGameStarted(currentGameId: number): Channel<Object> {
    return createJsonSubscriptionChannel(this.client, `/topic/lobby/${currentGameId}/started`);
  }

  createGame(gameName: string): void {
    this.client.send('/app/lobby/create', JSON.stringify({ gameName }));
  }

  joinGame(gameId: number): void {
    this.client.send('/app/lobby/join', JSON.stringify({ gameId }));
  }

  startGame(): void {
    this.client.send('/app/lobby/startGame', {});
  }
}

export function createSession(): Promise<SevenWondersSession> {
  return createStompSession(wsURL).then(client => new SevenWondersSession(client));
}

export class ApiError {
  message: string;
  details: ApiErrorDetail[];
}

export class ApiErrorDetail {
  message: string;
}
