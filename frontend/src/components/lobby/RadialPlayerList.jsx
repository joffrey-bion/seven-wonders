//@flow
import { Icon } from '@blueprintjs/core'
import { List } from 'immutable';
import * as React from 'react';
import { Flex } from 'reflexbox';
import { Player } from '../../models/players';
import { RadialList } from './radial-list/RadialList';
import roundTable from './round-table.png';

type RadialPlayerListProps = {
  players: List<Player>,
  owner: string,
  currentPlayer: Player,
};

const PlayerItem = ({player, isOwner, isUser}) => (
  <Flex column align='center'>
    <UserIcon isOwner={isOwner} isUser={isUser}/>
    <h5 style={{margin: 0}}>{player.displayName}</h5>
  </Flex>
);

const PlayerPlaceholder = () => (
  <Flex column align='center' style={{opacity: 0.3}}>
    <UserIcon isOwner={false} isUser={false}/>
    <h5 style={{margin: 0}}>?</h5>
  </Flex>
);

const UserIcon = ({isUser, isOwner}) => {
  const icon = isOwner ? 'badge' : 'user';
  const title = isOwner ? 'Game owner' : false;
  const intent = isUser ? 'warning' : 'none';
  return <Icon icon={icon} iconSize={50} intent={intent} title={title}/>;
};

export const RadialPlayerList = ({players, owner, currentPlayer}: RadialPlayerListProps) => {
  const orderedPlayers = placeFirst(players.toArray(), currentPlayer.username);
  const playerItems = orderedPlayers.map(player => <PlayerItem key={player.username}
                                                               player={player}
                                                               isOwner={player.username === owner}
                                                               isUser={player.username === currentPlayer.username}/>);
  const tableImg = <img src={roundTable} alt='Round table' style={{width: 200, height: 200}}/>;
  return <RadialList items={completeWithPlaceholders(playerItems)}
                     centerElement={tableImg}
                     diameter={350}
                     offsetDegrees={180}
                     itemWidth={120}
                     itemHeight={100}/>;
};

function placeFirst(players: Array<Player>, targetFirstUsername: string): Array<Player> {
  while (players[0].username !== targetFirstUsername) {
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

