// @flow
export type ApiError = {
  message: string,
  details: ApiErrorDetail[]
};

export type ApiErrorDetail = {
  message: string
};

export type ApiGameState = "LOBBY" | "PLAYING";

export type ApiLobby = {
  id: number,
  name: string,
  owner: ApiPlayer,
  players: ApiPlayer[],
  settings: ApiSettings,
  state: ApiGameState
};

export type ApiWonderSidePickMethod = "EACH_RANDOM" | "ALL_A" | "ALL_B" | "SAME_RANDOM_FOR_ALL";

export type ApiSettings = {
  randomSeedForTests: number,
  timeLimitInSeconds: number,
  wonderSidePickMethod: ApiWonderSidePickMethod,
  initialGold: number,
  discardedCardGold: number,
  defaultTradingCost: number,
  pointsPer3Gold: number,
  lostPointsPerDefeat: number,
  wonPointsPerVictoryPerAge: Map<number, number>
};

export type ApiPlayer = {
  username: string,
  displayName: string,
  index: number,
  gameOwner: Boolean,
  user: Boolean,
};

export type ApiTable = {

};

export type ApiAction = {};

export type ApiHandCard = {};

export type ApiTableCard = {};

export type ApiCardBack = {
  image: string
};

export type ApiPreparedCard = {
  player: ApiPlayer,
  cardBack: ApiCardBack
};

export type ApiPlayerTurnInfo = {
  playerIndex: number,
  table: ApiTable,
  currentAge: number,
  action: ApiAction,
  hand: ApiHandCard[],
  neighbourGuildCards: ApiTableCard[],
  message: string
};

export type ApiMoveType = "PLAY" | "PLAY_FREE" | "UPGRADE_WONDER" | "DISCARD" | "COPY_GUILD";
export type ApiProvider = "LEFT_NEIGHBOUR" | "RIGHT_NEIGHBOUR";
export type ApiResourceType = "WOOD" | "STONE" | "ORE" | "CLAY" | "GLASS" | "PAPYRUS" | "LOOM";

export type ApiResources = {
  quantities: Map<ApiResourceType, number>
};
export type ApiBoughtResources = {
  provider: ApiProvider,
  resources: ApiResources
};

export type ApiPlayerMove = {
  type: ApiMoveType,
  cardName: string,
  boughtResources: ApiBoughtResources[]
};
