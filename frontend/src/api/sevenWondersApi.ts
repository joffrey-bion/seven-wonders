import {
  ApiError,
  ApiLobby,
  ApiPlayer,
  ApiPlayerMove,
  ApiPlayerTurnInfo,
  ApiPreparedCard,
  ApiSettings,
  ApiTable,
} from './model';
import { JsonStompClient, SubscribeFn } from './websocket';
import { createJsonStompClient } from './websocket';

const WS_URL = '/seven-wonders-websocket';

export class SevenWondersSession {
  client: JsonStompClient;

  constructor(client: JsonStompClient) {
    this.client = client;
  }

  watchErrors(): SubscribeFn<ApiError> {
    return this.client.subscriber('/user/queue/errors');
  }

  watchNameChoice(): SubscribeFn<ApiPlayer> {
    return this.client.subscriber('/user/queue/nameChoice');
  }

  chooseName(displayName: string): void {
    this.client.send('/app/chooseName', { playerName: displayName });
  }

  watchGames(): SubscribeFn<ApiLobby[]> {
    return this.client.subscriber('/topic/games');
  }

  watchLobbyJoined(): SubscribeFn<Object> {
    return this.client.subscriber('/user/queue/lobby/joined');
  }

  createGame(gameName: string): void {
    this.client.send('/app/lobby/create', { gameName });
  }

  joinGame(gameId: number): void {
    this.client.send('/app/lobby/join', { gameId });
  }

  watchLobbyUpdated(currentGameId: number): SubscribeFn<Object> {
    return this.client.subscriber(`/topic/lobby/${currentGameId}/updated`);
  }

  watchGameStarted(currentGameId: number): SubscribeFn<Object> {
    return this.client.subscriber(`/topic/lobby/${currentGameId}/started`);
  }

  leave(): void {
    this.client.send('/app/lobby/leave');
  }

  reorderPlayers(orderedPlayers: Array<string>): void {
    this.client.send('/app/lobby/reorderPlayers', { orderedPlayers });
  }

  updateSettings(settings: ApiSettings): void {
    this.client.send('/app/lobby/updateSettings', { settings });
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

  watchPreparedCards(currentGameId: number): SubscribeFn<ApiPreparedCard> {
    return this.client.subscriber(`/topic/game/${currentGameId}/prepared`);
  }

  watchTurnInfo(): SubscribeFn<ApiPlayerTurnInfo> {
    return this.client.subscriber('/user/queue/game/turn');
  }

  sayReady(): void {
    this.client.send('/app/game/sayReady');
  }

  prepareMove(move: ApiPlayerMove): void {
    this.client.send('/app/game/prepareMove', { move });
  }
}

export async function connectToGame(): Promise<SevenWondersSession> {
  const jsonStompClient: JsonStompClient = createJsonStompClient(WS_URL);
  await jsonStompClient.connect();
  return new SevenWondersSession(jsonStompClient);
}
