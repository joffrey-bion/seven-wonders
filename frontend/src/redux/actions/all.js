import type { GameAction } from './game';
import type { LobbyAction } from './lobby';
import type { PlayerAction } from './user';

export type Action = PlayerAction | LobbyAction | GameAction
