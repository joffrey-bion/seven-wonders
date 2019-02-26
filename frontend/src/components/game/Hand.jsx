import { Button, ButtonGroup, Classes, Intent } from '@blueprintjs/core';
import React from 'react';
import type { ApiHandCard, ApiPlayerMove } from '../../api/model';
import './Hand.css'
import { CardImage } from './CardImage';

type HandProps = {
  cards: ApiHandCard[],
  wonderUpgradable: boolean,
  prepareMove: (move: ApiPlayerMove) => void
}

export const Hand = ({cards, wonderUpgradable, prepareMove}: HandProps) => {
  return <div className='hand'>{cards.map((c, i) => <HandCard key={i} card={c}
                                                              wonderUpgradable={wonderUpgradable}
                                                              prepareMove={prepareMove}/>)}</div>;
};

type HandCardProps = {
  card: ApiHandCard,
  wonderUpgradable: boolean,
  prepareMove: (move: ApiPlayerMove) => void
}

const HandCard = ({card, wonderUpgradable, prepareMove}: HandCardProps) => {
  let playableClass = card.playability.playable ? '' : 'unplayable';
  return <div className={`hand-card ${playableClass}`}>
    <CardImage card={card} otherClasses="hand-card-img"/>
    <ActionButtons card={card} wonderUpgradable={wonderUpgradable} prepareMove={prepareMove} />
  </div>
};

const ActionButtons = ({card, wonderUpgradable, prepareMove}) => <ButtonGroup className="action-buttons">
  <Button title="PLAY" className={Classes.LARGE} intent={Intent.SUCCESS} icon='play'
          disabled={!card.playability.playable}
          onClick={() => prepareMove({type: 'PLAY', cardName: card.name, boughtResources: []})}/>
  <Button title="BUILD WONDER" className={Classes.LARGE} intent={Intent.PRIMARY} icon='key-shift'
          disabled={!wonderUpgradable}
          onClick={() => prepareMove({type: 'UPGRADE_WONDER', cardName: card.name, boughtResources: []})}/>
  <Button title="DISCARD" className={Classes.LARGE} intent={Intent.DANGER} icon='cross'
          onClick={() => prepareMove({type: 'DISCARD', cardName: card.name, boughtResources: []})}/>
</ButtonGroup>;
