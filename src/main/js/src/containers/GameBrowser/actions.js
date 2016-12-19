import { NEW_GAME, JOIN_GAME } from './constants'

export const newGame = (game) => ({
  type: NEW_GAME,
  game
})

export const joinGame = (id) => ({
  type: JOIN_GAME,
  id
})
