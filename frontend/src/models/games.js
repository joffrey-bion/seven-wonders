import { Record, Map, List } from 'immutable';

export type SettingsShape = {
  initialGold: number,
  lostPointsPerDefeat: number,
  timeLimitInSeconds: number,
  randomSeedForTests: number,
  discardedCardGold: number,
  defaultTradingCost: number,
  wonPointsPerVictoryPerAge: {
    "1": number,
    "2": number,
    "3": number
  },
  wonderSidePickMethod: "EACH_RANDOM" | "TODO",
  pointsPer3Gold: number
};
export type SettingsType = Record<SettingsShape>;

const SettingsRecord: SettingsType = Record({
  initialGold: 3,
  lostPointsPerDefeat: 1,
  timeLimitInSeconds: 45,
  randomSeedForTests: -1,
  discardedCardGold: 3,
  defaultTradingCost: 2,
  wonPointsPerVictoryPerAge: {
    '1': 1,
    '2': 3,
    '3': 5,
  },
  wonderSidePickMethod: 'EACH_RANDOM',
  pointsPer3Gold: 1,
});
export class Settings extends SettingsRecord {}

export type GameShape = {
  id: number,
  name: string | void,
  players: List<string>,
  settings: SettingsType,
  state: "LOBBY" | "TODO"
};
export type GameType = Record<GameShape>;
export type GameMapType = Map<string, GameShape>;
export type GameNormalMapType = { [string]: GameShape };

const GameRecord: GameType = Record({
  id: -1,
  name: null,
  players: new List(),
  settings: new Settings(),
  state: 'LOBBY',
});
export class Game extends GameRecord {}

export type GamesShape = {
  all: Map<Game>,
  current: string
};
export type GamesType = Record<GamesShape>;

const GamesRecord: GamesType = Record({
  all: new Map(),
  current: null,
});
export default class GamesState extends GamesRecord {
  addGame(g: GameShape) {
    const game: Game = new Game(g);
    return this.mergeDeepIn(['all', game.id], game);
  }
  addGames(games: GameNormalMapType) {
    return this.mergeIn(['all'], games.map((game: GameShape): Game => new Game(game)));
  }
}
