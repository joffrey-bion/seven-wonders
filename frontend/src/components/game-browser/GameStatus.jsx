//@flow
import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import type { ApiGameState } from '../../api/model';

type GameStatusProps = {
  state: ApiGameState,
}

export const GameStatus = ({state}: GameStatusProps) => (
  <Tag minimal intent={statusIntents[state]}>{state}</Tag>
);

const statusIntents = {
  'LOBBY': 'success',
  'PLAYING': 'warning',
};
