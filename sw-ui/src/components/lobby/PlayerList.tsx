import { Classes, Icon } from '@blueprintjs/core'
import { List } from 'immutable';
import * as React from 'react';
import { Flex } from 'reflexbox';
import { ApiPlayer } from '../../api/model';

type PlayerListItemProps = {
  player: ApiPlayer,
  isOwner: boolean,
  isUser: boolean,
};

const PlayerListItem = ({player, isOwner, isUser}: PlayerListItemProps) => (
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

type PlayerListProps = {
  players: List<ApiPlayer>,
  owner: string,
  currentPlayer: ApiPlayer,
};

export const PlayerList = ({players, owner, currentPlayer}: PlayerListProps) => (
  <table className={Classes.HTML_TABLE}>
    <tbody>
      {players.map((player: ApiPlayer) => <PlayerListItem key={player.username}
                                             player={player}
                                             isOwner={player.username === owner}
                                             isUser={player.username === currentPlayer.username}/>)}
    </tbody>
  </table>
);
