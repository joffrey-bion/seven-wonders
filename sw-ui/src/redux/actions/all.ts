import { GameAction } from './game';
import { LobbyAction } from './lobby';
import { PlayerAction } from './user';

export type Action = PlayerAction | LobbyAction | GameAction
