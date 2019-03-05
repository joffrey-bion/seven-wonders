// @flow
export type ApiError = {
  message: string,
  details: ApiErrorDetail[]
};

export type ApiErrorDetail = {
  message: string
};

export type ApiGameState = "LOBBY" | "PLAYING";

export type ApiPlayer = {
  username: string,
  displayName: string,
  index: number,
  gameOwner: Boolean,
  user: Boolean,
};

export type ApiLobby = {
  id: number,
  name: string,
  owner: string,
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

export type ApiTable = {
  boards: ApiBoard[],
  currentAge: number,
  handRotationDirection: HandRotationDirection,
  lastPlayedMoves: ApiPlayedMove[],
  nbPlayers: number,
};

export type ApiScience = {
  jokers: number,
  nbWheels: number,
  nbCompasses: number,
  nbTablets: number,
}

export type ApiMilitary = {
  nbShields: number,
  totalPoints: number,
  nbDefeatTokens: number,
}

export type ApiBoard = {
  playerIndex: number,
  wonder: ApiWonder,
  production: ApiProduction,
  publicProduction: ApiProduction,
  science: ApiScience,
  military: ApiMilitary,
  playedCards: ApiTableCard[][],
  gold: number,
};

export type ApiWonder = {
  name: string,
  initialResource: ApiResourceType,
  stages: ApiWonderStage[],
  image: string,
  nbBuiltStages: number,
  buildability: ApiWonderBuildability,
}

export type ApiWonderStage = {
  cardBack: ApiCardBack | null,
  isBuilt: boolean,
  requirements: ApiRequirements,
  builtDuringLastMove: boolean,
}

export type HandRotationDirection = 'LEFT' | 'RIGHT';

export type ApiAction = 'PLAY' | 'PLAY_2' | 'PLAY_LAST' | 'PICK_NEIGHBOR_GUILD' | 'WAIT';

export type Color = 'BLUE' | 'GREEN' | 'RED' | 'BROWN' | 'GREY' | 'PURPLE' | 'YELLOW';

export type ApiRequirements = {
  gold: number,
  resources: ApiResources
}

export type ApiCard = {
  name: string,
  color: Color,
  requirements: ApiRequirements,
  chainParent: String | null,
  chainChildren: String[],
  image: string,
  back: ApiCardBack
};

export type ApiHandCard = ApiCard & {
  playability: ApiPlayability,
};

export type ApiPlayability = {
  playable: boolean,
  chainable: boolean,
  minPrice: number,
};

export type ApiTableCard = ApiCard & {
  playedDuringLastMove: boolean,
};

export type ApiCardBack = {
  image: string,
};

export type ApiPreparedCard = {
  player: ApiPlayer,
  cardBack: ApiCardBack,
};

export type ApiPlayerTurnInfo = {
  playerIndex: number,
  table: ApiTable,
  currentAge: number,
  action: ApiAction,
  hand: ApiHandCard[],
  playedMove: ApiPlayedMove | null,
  neighbourGuildCards: ApiTableCard[],
  message: string,
  wonderBuildability: ApiWonderBuildability,
};

export type ApiWonderBuildability = {
  buildable: boolean
}

export type ApiMoveType = "PLAY" | "PLAY_FREE" | "UPGRADE_WONDER" | "DISCARD" | "COPY_GUILD";
export type ApiProvider = "LEFT_NEIGHBOUR" | "RIGHT_NEIGHBOUR";
export type ApiResourceType = "WOOD" | "STONE" | "ORE" | "CLAY" | "GLASS" | "PAPYRUS" | "LOOM";

export type ApiProduction = {
  fixedResources: ApiCountedResource[],
  alternativeResources: ApiResourceType[][],
}

export type ApiCountedResource = {
  type: ApiResourceType,
  count: number,
}

export type ApiResources = {
  quantities: Map<ApiResourceType, number>,
};

export type ApiBoughtResources = {
  provider: ApiProvider,
  resources: ApiResources,
};

export type ApiPlayerMove = {
  type: ApiMoveType,
  cardName: string,
  boughtResources: ApiBoughtResources[],
};

export type ApiPlayedMove = {
  playerIndex: number,
  type: ApiMoveType,
  card: ApiTableCard,
  boughtResources: ApiBoughtResources[],
};
