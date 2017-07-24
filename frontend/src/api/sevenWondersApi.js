import { createJsonStompClient, JsonStompClient, Callback } from './websocket';
import { ApiError, ApiLobby, ApiPlayer, ApiPlayerMove, ApiPlayerTurnInfo, ApiPreparedCard, ApiTable } from './model';

const wsURL = '/seven-wonders-websocket';

export class SevenWondersSession {
  client: JsonStompClient;

  constructor(client: JsonStompClient) {
    this.client = client;
  }

  watchErrors(callback: Callback<ApiError>): void {
    return this.client.subscribe('/user/queue/errors', callback);
  }

  chooseName(displayName: string): void {
    this.client.send('/app/chooseName', { playerName: displayName });
  }

  watchNameChoice(callback: Callback<ApiPlayer>): void {
    return this.client.subscribe('/user/queue/nameChoice', callback);
  }

  watchGames(callback: Callback<ApiLobby[]>): void {
    return this.client.subscribe('/topic/games', callback);
  }

  watchLobbyJoined(callback: Callback<Object>): void {
    return this.client.subscribe('/user/queue/lobby/joined', callback);
  }

  watchLobbyUpdated(currentGameId: number, callback: Callback<Object>): void {
    return this.client.subscribe(`/topic/lobby/${currentGameId}/updated`, callback);
  }

  watchGameStarted(currentGameId: number, callback: Callback<Object>): void {
    return this.client.subscribe(`/topic/lobby/${currentGameId}/started`, callback);
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

  watchPlayerReady(currentGameId: number, callback: Callback<string>): void {
    return this.client.subscribe(`/topic/game/${currentGameId}/playerReady`, callback);
  }

  watchTableUpdates(currentGameId: number, callback: Callback<ApiTable>): void {
    return this.client.subscribe(`/topic/game/${currentGameId}/tableUpdates`, callback);
  }

  watchPreparedMove(currentGameId: number, callback: Callback<ApiPreparedCard>): void {
    return this.client.subscribe(`/topic/game/${currentGameId}/prepared`, callback);
  }

  watchTurnInfo(callback: Callback<ApiPlayerTurnInfo>): void {
    return this.client.subscribe('/user/queue/game/turnInfo', callback);
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
