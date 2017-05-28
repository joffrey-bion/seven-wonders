import { Record, Map } from 'immutable';

const PlayerRecord = Record({
  username: null,
  displayName: null,
  index: 0,
  ready: false,
});
export class Player extends PlayerRecord {}

const PlayersRecord = Record({
  all: new Map(),
  current: '',
});
export default class PlayerState extends PlayersRecord {
  addPlayer(p) {
    const player = new Player(p);
    const playerMap = new Map({ [player.username]: player });
    return this.addPlayers(playerMap).set('current', player.username);
  }

  addPlayers(p) {
    const players = new Map(p);
    return this.mergeIn(['all'], players.map(player => new Player(player)));
  }
}
