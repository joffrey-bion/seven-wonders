import React from 'react';
import type { ApiCard } from '../../api/model';
import './Hand.css'

type CardImageProps = {
  card: ApiCard,
  otherClasses: string
}

export const CardImage = ({card, otherClasses}: CardImageProps) => {
  return <img src={`/images/cards/${card.image}`}
              title={card.name}
              alt={'Card ' + card.name}
              className={`card-img ${otherClasses}`}/>
};
