// @flow
import type {
  ApiError,
  ApiLobby,
  ApiPlayer,
  ApiPlayerMove,
  ApiPlayerTurnInfo,
  ApiPreparedCard,
  ApiTable,
} from './model';
import type { JsonStompClient, SubscribeFn } from './websocket';
import { createJsonStompClient } from './websocket';

const wsURL = '/seven-wonders-websocket';

export class SevenWondersSession {
  client: JsonStompClient;

  constructor(client: JsonStompClient) {
    this.client = client;
  }

  watchErrors(): SubscribeFn<ApiError> {
    return this.client.subscriber('/user/queue/errors');
  }

  chooseName(displayName: string): void {
    this.client.send('/app/chooseName', { playerName: displayName });
  }

  watchNameChoice(): SubscribeFn<ApiPlayer> {
    return this.client.subscriber('/user/queue/nameChoice');
  }

  watchGames(): SubscribeFn<ApiLobby[]> {
    return this.client.subscriber('/topic/games');
  }

  watchLobbyJoined(): SubscribeFn<Object> {
    return this.client.subscriber('/user/queue/lobby/joined');
  }

  watchLobbyUpdated(currentGameId: number): SubscribeFn<Object> {
    return this.client.subscriber(`/topic/lobby/${currentGameId}/updated`);
  }

  watchGameStarted(currentGameId: number): SubscribeFn<Object> {
    return this.client.subscriber(`/topic/lobby/${currentGameId}/started`);
  }

  createGame(gameName: string): void {
    this.client.send('/app/lobby/create', { gameName });
  }

  joinGame(gameId: number): void {
    this.client.send('/app/lobby/join', { gameId });
  }

  startGame(): void {
    this.client.send('/app/lobby/startGame');
  }

  watchPlayerReady(currentGameId: number): SubscribeFn<string> {
    return this.client.subscriber(`/topic/game/${currentGameId}/playerReady`);
  }

  watchTableUpdates(currentGameId: number): SubscribeFn<ApiTable> {
    return this.client.subscriber(`/topic/game/${currentGameId}/tableUpdates`);
  }

  watchPreparedMove(currentGameId: number): SubscribeFn<ApiPreparedCard> {
    return this.client.subscriber(`/topic/game/${currentGameId}/prepared`);
  }

  watchTurnInfo(): SubscribeFn<ApiPlayerTurnInfo> {
    return this.client.subscriber('/user/queue/game/turnInfo');
  }

  sayReady(): void {
    this.client.send('/app/game/sayReady');
  }

  prepareMove(move: ApiPlayerMove): void {
    this.client.send('/app/game/sayReady', { move });
  }
}

export async function connectToGame(): Promise<SevenWondersSession> {
  const jsonStompClient: JsonStompClient = createJsonStompClient(wsURL);
  await jsonStompClient.connect();
  return new SevenWondersSession(jsonStompClient);
}
