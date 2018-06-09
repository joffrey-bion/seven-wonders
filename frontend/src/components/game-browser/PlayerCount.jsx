//@flow
import { Icon } from '@blueprintjs/core';
import * as React from 'react';
import './PlayerCount.css';

export type PlayerCountProps = {
  nbPlayers: number,
}

export const PlayerCount = ({nbPlayers}: PlayerCountProps) => <div title='Number of players'>
  <Icon icon="people" title={false} />
  <span className='playerCount'> {nbPlayers}</span>
</div>;
