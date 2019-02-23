import React from 'react';
import type { ApiHandCard } from '../../api/model';
import './Hand.css'

type HandCardProps = {
  card: ApiHandCard,
  isSelected: boolean,
  onClick: () => void
}

const HandCard = ({card, isSelected, onClick}: HandCardProps) => {
  let playableClass = card.playability.playable ? '' : 'hand-card-unplayable';
  let selectedClass = isSelected ? 'hand-card-img-selected' : '';
  return <div className={`hand-card ${playableClass}`}
              onClick={() => card.playability.playable && onClick()}
              aria-disabled={!card.playability.playable}>
    <img src={`/images/cards/${card.image}`}
         title={card.name}
         alt={'Card ' + card.name}
         className={`hand-card-img ${selectedClass}`}/>
  </div>
};

type HandProps = {
  cards: ApiHandCard[],
  selectedCard: ApiHandCard,
  onClick: (card: ApiHandCard) => void
}

export const Hand = ({cards, selectedCard, onClick}: HandProps) => {
  return <div className='hand'>{cards.map((c, i) => <HandCard key={i} card={c}
                                                              isSelected={selectedCard === c}
                                                              onClick={() => onClick(c)}/>)}</div>;
}
