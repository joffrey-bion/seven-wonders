import { List } from 'immutable';
import type { ApiPlayerTurnInfo, ApiTable } from '../api/model';

export class CurrentGameState {
  readyUsernames: List<string> = new List();
  turnInfo: ApiPlayerTurnInfo | null = null;
  table: ApiTable | null = null;
}
