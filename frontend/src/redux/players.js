import PlayerState, { Player } from '../models/players';

export const types = {
  REQUEST_CHOOSE_USERNAME: 'USER/REQUEST_CHOOSE_USERNAME',
  SET_CURRENT_PLAYER: 'USER/SET_CURRENT_PLAYER',
  UPDATE_PLAYERS: 'USER/UPDATE_PLAYERS',
};

export const actions = {
  chooseUsername: username => ({
    type: types.REQUEST_CHOOSE_USERNAME,
    username,
  }),
  setCurrentPlayer: player => ({
    type: types.SET_CURRENT_PLAYER,
    player,
  }),
  updatePlayers: players => ({
    type: types.UPDATE_PLAYERS,
    players,
  }),
};

export default (state = new PlayerState(), action) => {
  switch (action.type) {
    case types.SET_CURRENT_PLAYER:
      return state.addPlayer(action.player);
    case types.UPDATE_PLAYERS:
      return state.addPlayers(action.players);
    default:
      return state;
  }
};

export const getCurrentPlayer = players => players.all.get(players.current, new Player({ displayName: '[ERROR]' }));
export const getPlayer = (players, username) => players.all.get(username);
export const getPlayers = (players, usernames) => usernames.map(u => getPlayer(players, u));
