import React from 'react';
import type { ApiCard } from '../../api/model';
import './CardImage.css'

type CardImageProps = {
  card: ApiCard,
  otherClasses: string,
  highlightColor?: string
}

export const CardImage = ({card, otherClasses, highlightColor}: CardImageProps) => {
  const style = highlightStyle(highlightColor);
  return <img src={`/images/cards/${card.image}`}
              title={card.name}
              alt={'Card ' + card.name}
              className={`card-img ${otherClasses}`}
              style={style}/>
};

function highlightStyle(highlightColor?: string) {
  if (highlightColor) {
    return { boxShadow: `0 0 1rem 0.1rem ${highlightColor}` };
  } else {
    return {};
  }
}
