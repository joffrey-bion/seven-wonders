//@flow
import { Icon } from '@blueprintjs/core'
import { List } from 'immutable';
import * as React from 'react';
import { Flex } from 'reflexbox';
import { ApiPlayer } from '../../api/model';
import { RadialList } from './radial-list/RadialList';
import roundTable from './round-table.png';

type RadialPlayerListProps = {
  players: List<ApiPlayer>
};

const PlayerItem = ({player}) => (
  <Flex column align='center'>
    <UserIcon isOwner={player.gameOwner} isUser={player.user} title={player.gameOwner ? 'Game owner' : false}/>
    <h5 style={{margin: 0}}>{player.displayName}</h5>
  </Flex>
);

const PlayerPlaceholder = () => (
  <Flex column align='center' style={{opacity: 0.3}}>
    <UserIcon isOwner={false} isUser={false} title='Waiting for player...'/>
    <h5 style={{margin: 0}}>?</h5>
  </Flex>
);

const UserIcon = ({isUser, isOwner, title}) => {
  const icon = isOwner ? 'badge' : 'user';
  const intent = isUser ? 'warning' : 'none';
  return <Icon icon={icon} iconSize={50} intent={intent} title={title}/>;
};

export const RadialPlayerList = ({players}: RadialPlayerListProps) => {
  const orderedPlayers = placeUserFirst(players.toArray());
  const playerItems = orderedPlayers.map(player => <PlayerItem key={player.username} player={player}/>);
  const tableImg = <img src={roundTable} alt='Round table' style={{width: 200, height: 200}}/>;
  return <RadialList items={completeWithPlaceholders(playerItems)}
                     centerElement={tableImg}
                     radius={175}
                     offsetDegrees={180}
                     itemWidth={120}
                     itemHeight={100}/>;
};

function placeUserFirst(players: Array<ApiPlayer>): Array<ApiPlayer> {
  while (!players[0].user) {
    players.push(players.shift());
  }
  return players;
}

function completeWithPlaceholders(playerItems: Array<React.Node>): Array<React.Node> {
  while (playerItems.length < 3) {
    playerItems.push(<PlayerPlaceholder/>);
  }
  return playerItems;
}

