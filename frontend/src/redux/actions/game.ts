import { ApiPlayerMove, ApiPlayerTurnInfo, ApiPreparedCard, ApiTable } from '../../api/model';

export const REQUEST_SAY_READY = 'GAME/REQUEST_SAY_READY';
export const REQUEST_PREPARE_MOVE = 'GAME/REQUEST_PREPARE_MOVE';
export const PLAYER_READY_RECEIVED = 'GAME/PLAYER_READY_RECEIVED';
export const TABLE_UPDATE_RECEIVED = 'GAME/TABLE_UPDATE_RECEIVED';
export const PREPARED_CARD_RECEIVED = 'GAME/PREPARED_CARD_RECEIVED';
export const TURN_INFO_RECEIVED = 'GAME/TURN_INFO_RECEIVED';

export type SayReadyAction = { type: typeof REQUEST_SAY_READY };
export type PrepareMoveAction = { type: typeof REQUEST_PREPARE_MOVE, move: ApiPlayerMove };
export type PlayerReadyEvent = { type: typeof PLAYER_READY_RECEIVED, username: string };
export type TableUpdateEvent = { type: typeof TABLE_UPDATE_RECEIVED, table: ApiTable };
export type PreparedCardEvent = { type: typeof PREPARED_CARD_RECEIVED, card: ApiPreparedCard };
export type TurnInfoEvent = { type: typeof TURN_INFO_RECEIVED, turnInfo: ApiPlayerTurnInfo };

export type GameAction =
        SayReadyAction
        | PrepareMoveAction
        | PlayerReadyEvent
        | TableUpdateEvent
        | PreparedCardEvent
        | TurnInfoEvent;

export const actions = {
  sayReady: () => ({ type: REQUEST_SAY_READY }),
  prepareMove: (move: ApiPlayerMove) => ({ type: REQUEST_PREPARE_MOVE, move }),
  receivePlayerReady: (username: string) => ({ type: PLAYER_READY_RECEIVED, username }),
  receiveTableUpdate: (table: ApiTable) => ({ type: TABLE_UPDATE_RECEIVED, table }),
  receivePreparedCard: (card: ApiPreparedCard) => ({ type: PREPARED_CARD_RECEIVED, card }),
  receiveTurnInfo: (turnInfo: ApiPlayerTurnInfo) => ({ type: TURN_INFO_RECEIVED, turnInfo }),
};
