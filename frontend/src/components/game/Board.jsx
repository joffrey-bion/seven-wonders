import React from 'react';
import type { ApiBoard, ApiTableCard, ApiWonder } from '../../api/model';
import './Board.css'
import { CardImage } from './CardImage';

// card offsets in % of their size when displayed in columns
const xOffset = 20;
const yOffset = 23;

type BoardProps = {
  board: ApiBoard,
}

export const Board = ({board}: BoardProps) => {
  return <div className='board'>
    <TableCards cards={board.playedCards}/>
    <Wonder wonder={board.wonder}/>
  </div>;
};

type TableCardsProps = {
  cards: ApiTableCard[],
}

const TableCards = ({cards}: TableCardsProps) => {
  // TODO split cards into multiple columns
  return <div className="cards">
    <TableCardColumn cards={cards}/>
  </div>
};

type TableCardColumnProps = {
  cards: ApiTableCard[]
}

const TableCardColumn = ({cards}: TableCardColumnProps) => {
  return <div className="card-column">
    {cards.map((c, i) => <TableCard key={c.name} card={c} indexInColumn={i}/>)}
  </div>
};

type TableCardProps = {
  card: ApiTableCard,
  indexInColumn: number,
}

const TableCard = ({card, indexInColumn}: TableCardProps) => {
  let style = {
    transform: `translate(${indexInColumn * xOffset}%, ${indexInColumn * yOffset}%)`,
    zIndex: indexInColumn,
  };
  return <div className="card" style={style}>
    <CardImage card={card} otherClasses="table-card-img"/>
  </div>
};

type WonderProps = {
  wonder: ApiWonder,
}

const Wonder = ({wonder}: WonderProps) => {
  return <div className="wonder">
    <img src={`/images/wonders/${wonder.image}`}
         title={wonder.name}
         alt={`Wonder ${wonder.name}`}
         className="wonder-img"/>
  </div>
};
