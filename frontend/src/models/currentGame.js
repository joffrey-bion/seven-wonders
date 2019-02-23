import { List } from 'immutable';
import type { ApiPlayerTurnInfo } from '../api/model';

export class CurrentGameState {
  readyUsernames: List<string> = new List();
  turnInfo: ApiPlayerTurnInfo | null = null
}
