//@flow
import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import type { GameState } from '../../models/games';

export type GameStatusProps = {
  state: GameState,
}

export const GameStatus = ({state}: GameStatusProps) => (
  <Tag minimal intent={statusIntents[state]}>{state}</Tag>
);

const statusIntents = {
  'LOBBY': 'success',
  'PLAYING': 'warning',
};
