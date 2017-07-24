// @flow
import { Map, Record } from 'immutable';

export type PlayerShape = {
  username: string,
  displayName: string,
  index: number,
  ready: boolean
};
export type PlayerType = Record<PlayerShape>;

const PlayerRecord: PlayerType = Record({
  username: null,
  displayName: null,
  index: 0,
  ready: false,
});
// $FlowFixMe
export class Player extends PlayerRecord {}

export type PlayersShape = {
  all: Map<string, PlayerType>,
  current: string
};
export type PlayersType = Record<PlayersShape>;

const PlayersRecord: PlayersType = Record({
  all: new Map(),
  current: '',
});
// $FlowFixMe
export default class PlayerState extends PlayersRecord {
  addPlayer(p: PlayerShape) {
    const player: Player = new Player(p);
    const playerMap = new Map(({ [player.username]: player }: { [key: string]: Player }));
    return this.addPlayers(playerMap).set('current', player.username);
  }

  addPlayers(p: Map<string, PlayerShape>) {
    const players: Map<string, PlayerShape> = new Map(p);
    return this.mergeIn(['all'], players.map((player: PlayerShape): Player => new Player(player)));
  }
}
