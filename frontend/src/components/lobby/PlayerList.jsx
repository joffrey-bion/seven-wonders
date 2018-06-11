//@flow
import { Icon } from '@blueprintjs/core'
import { List } from 'immutable';
import * as React from 'react';
import { Flex } from 'reflexbox';
import { Player } from '../../models/players';

type PlayerListProps = {
  players: List<Player>,
  owner: string,
  currentPlayer: Player,
};

const PlayerListItem = ({player, isOwner, isUser}) => (
  <tr>
    <td>
      <Flex align='center'>
        {isOwner && <Icon icon='badge' title='Game owner'/>}
        {isUser && <Icon icon='user' title='This is you'/>}
      </Flex>
    </td>
    <td>{player.displayName}</td>
    <td>{player.username}</td>
  </tr>
);

export const PlayerList = ({players, owner, currentPlayer}: PlayerListProps) => (
  <table className='pt-html-table'>
    <tbody>
      {players.map(player => <PlayerListItem key={player.username}
                                             player={player}
                                             isOwner={player.username === owner}
                                             isUser={player.username === currentPlayer.username}/>)}
    </tbody>
  </table>
);
