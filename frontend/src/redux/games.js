import {fromJS} from 'immutable'

export const types = {
  UPDATE_GAMES: 'GAME/UPDATE_GAMES',
  REQUEST_CREATE_GAME: 'GAME/REQUEST_CREATE_GAME',
  REQUEST_JOIN_GAME: 'GAME/REQUEST_JOIN_GAME',
  ENTER_LOBBY: 'GAME/ENTER_LOBBY',
}

export const actions = {
  updateGames: (games) => ({ type: types.UPDATE_GAMES, games }),
  requestJoinGame: (id) => ({ type: types.REQUEST_JOIN_GAME, id }),
  requestCreateGame: (name) => ({ type: types.REQUEST_CREATE_GAME, name }),
  enterLobby: (lobby) => ({ type: types.ENTER_LOBBY, lobby }),
}

const initialState = fromJS({
  all: {},
  current: ''
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.UPDATE_GAMES:
      return state.setIn(['all'], state.get('all').mergeDeep(action.games))
    case types.ENTER_LOBBY:
      return state.set('current', action.lobby.get('id'))
    default:
      return state
  }
}

const getState = globalState => globalState.get('games')

export const getAllGamesById = globalState => getState(globalState).get('all')
export const getAllGames = globalState => getAllGamesById(globalState).toList()
export const getGame = (globalState, id) => getAllGamesById(globalState).get(id)
export const getCurrentGameId = globalState => getState(globalState).get('current')
export const getCurrentGame = globalState => getGame(globalState, getCurrentGameId(globalState))
