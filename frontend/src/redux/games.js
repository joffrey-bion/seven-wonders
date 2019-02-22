// @flow
import type { List, Map } from 'immutable';
import type { Game } from '../models/games';
import { GamesState } from '../models/games';
import type { Action } from './actions/all';
import { types } from './actions/lobby';

export const gamesReducer = (state: GamesState = new GamesState(), action: Action) => {
  switch (action.type) {
    case types.UPDATE_GAMES:
      return state.addGames(action.games);
    case types.ENTER_LOBBY:
      return state.set('current', action.gameId);
    default:
      return state;
  }
};

export const getAllGamesById = (games: GamesState): Map<string, Game> => games.all;
export const getAllGames = (games: GamesState): List<Game> => getAllGamesById(games).toList();
export const getGame = (games: GamesState, id: string | number): Game => getAllGamesById(games).get(`${id}`);
export const getCurrentGame = (games: GamesState): Game => getGame(games, games.current);
