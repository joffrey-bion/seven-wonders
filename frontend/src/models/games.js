import { Record, Map, List } from 'immutable';

const SettingsRecord = Record({
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

const GameRecord = Record({
  id: -1,
  name: null,
  players: new List(),
  settings: new Settings(),
  state: 'LOBBY',
});
export class Game extends GameRecord {}

const GamesRecord = Record({
  all: new Map(),
  current: '',
});
export default class GamesState extends GamesRecord {
  addGame(g) {
    const game = new Game(g);
    return this.mergeDeepIn(['all', game.id], game);
  }
  addGames(games) {
    return this.mergeIn(['all'], games.map(game => new Game(game)));
  }
}
