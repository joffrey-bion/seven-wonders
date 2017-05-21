import { schema } from 'normalizr';

const player = new schema.Entity(
  'players',
  {},
  {
    idAttribute: 'username',
  }
);

export const game = new schema.Entity('games', {
  players: [player],
});

export const gameList = [game];
