import * as React from 'react';
import { ReactNode } from 'react';
import { CartesianCoords } from './radial-math';
import './RadialListItem.css';

type RadialListItemProps = {
  item: ReactNode,
  offsets: CartesianCoords,
};

export const RadialListItem = ({item, offsets}: RadialListItemProps) => {
  // Y-axis points down, hence the minus sign
  const liStyle = {
    transform: `translate(${offsets.x}px, ${-offsets.y}px) translate(-50%, -50%)`,
  };

  return <li className='radial-list-item' style={liStyle}>{item}</li>;
};
