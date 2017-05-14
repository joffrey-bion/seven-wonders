import Immutable from 'seamless-immutable'

export const types = {
  UPDATE_GAMES: 'GAME/UPDATE_GAMES',
  REQUEST_CREATE_GAME: 'GAME/REQUEST_CREATE_GAME',
  REQUEST_JOIN_GAME: 'GAME/REQUEST_JOIN_GAME',
  REQUEST_START_GAME: 'GAME/REQUEST_JOIN_GAME',
  ENTER_LOBBY: 'GAME/ENTER_LOBBY',
  ENTER_GAME: 'GAME/ENTER_GAME',
}

export const actions = {
  updateGames: (games) => ({ type: types.UPDATE_GAMES, games: Immutable(games) }),
  requestJoinGame: (gameId) => ({ type: types.REQUEST_JOIN_GAME, gameId }),
  requestCreateGame: (gameName) => ({ type: types.REQUEST_CREATE_GAME, gameName }),
  requestStartGame: () => ({ type: types.REQUEST_START_GAME }),
  enterLobby: (lobby) => ({ type: types.ENTER_LOBBY, lobby: Immutable(lobby) }),
  enterGame: () => ({ type: types.ENTER_GAME }),
}

const initialState = Immutable.from({
  all: {},
  current: ''
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.UPDATE_GAMES:
      return Immutable.merge(state, {all: action.games}, {deep: true})
    case types.ENTER_LOBBY:
      return state.set('current', action.lobby.id)
    default:
      return state
  }
}

export const getAllGamesById = state => state.games.all
export const getAllGames = state => {
  let gamesById = getAllGamesById(state)
  return Object.keys(gamesById).map(k => gamesById[k]);
}
export const getGame = (state, id) => getAllGamesById(state)[id]
export const getCurrentGame = state => getGame(state, state.games.current)
