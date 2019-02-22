import type { ApiPlayerTurnInfo } from '../api/model';

export class CurrentGameState {
  playersReadiness: Map<string, boolean> = new Map();
  turnInfo: ApiPlayerTurnInfo | null = null
}
