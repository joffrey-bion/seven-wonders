import { schema } from 'normalizr'

const player = new schema.Entity('players', {}, {
  idAttribute: 'username'
})

const game = new schema.Entity('games', {
  players: [ player ]
})

export default [ game ]
