export class ApiError {
  message: string;
  details: ApiErrorDetail[];
}

export class ApiErrorDetail {
  message: string;
}

export type ApiGameState = "LOBBY" | "PLAYING";

export class ApiLobby {
  id: number;
  name: string;
  owner: ApiPlayer;
  players: ApiPlayer[];
  settings: ApiSettings;
  state: ApiGameState;
}

export type ApiWonderSidePickMethod = "EACH_RANDOM" | "ALL_A" | "ALL_B" | "SAME_RANDOM_FOR_ALL";

export class ApiSettings {
  randomSeedForTests: number;
  timeLimitInSeconds: number;
  wonderSidePickMethod: ApiWonderSidePickMethod;
  initialGold: number;
  discardedCardGold: number;
  defaultTradingCost: number;
  pointsPer3Gold: number;
  lostPointsPerDefeat: number;
  wonPointsPerVictoryPerAge: Map<number, number>;
}

export class ApiPlayer {
  username: string;
  displayName: string;
  index: number;
  ready: boolean;
}

export class ApiTable {}

export class ApiHandCard {}

export class ApiCard {}

export class ApiPreparedCard {}

export class ApiPlayerTurnInfo {
  playerIndex: number;
  table: ApiTable;
  currentAge: number;
  action: Action;
  hand: ApiHandCard[];
  neighbourGuildCards: ApiCard[];
  message: string;
}

export type ApiMoveType = "PLAY" | "PLAY_FREE" | "UPGRADE_WONDER" | "DISCARD" | "COPY_GUILD";
export type ApiProvider = "LEFT_NEIGHBOUR" | "RIGHT_NEIGHBOUR";
export type ApiResourceType = "WOOD" | "STONE" | "ORE" | "CLAY" | "GLASS" | "PAPYRUS" | "LOOM";

export class ApiResources {
  quantities: Map<ApiResourceType, number>;
}
export class ApiBoughtResources {
  provider: ApiProvider;
  resources: ApiResources;
}

export class ApiPlayerMove {
  type: ApiMoveType;
  cardName: string;
  boughtResources: ApiBoughtResources[];
}
