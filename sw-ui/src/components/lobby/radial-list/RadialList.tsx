import React, { ReactNode } from 'react';
import { CartesianCoords, RadialConfig } from './radial-math';
import { offsetsFromCenter, CLOCKWISE, COUNTERCLOCKWISE } from './radial-math';
import './RadialList.css';
import { RadialListItem } from './RadialListItem';

type RadialListProps = {
  items: Array<ReactNode>,
  centerElement?: ReactNode,
  radius?: number, // 120px by default
  offsetDegrees?: number, // defaults to 0 = 12 o'clock
  arc?: number, // defaults to 360 (full circle)
  clockwise?: boolean, // defaults to true
  itemWidth?: number,
  itemHeight?: number,
};

export const RadialList = ({items, centerElement, radius = 120, offsetDegrees = 0, arc = 360, clockwise = true, itemWidth = 20, itemHeight = 20}: RadialListProps) => {
  const diameter = radius * 2;
  const containerStyle = {
    width: diameter + itemWidth,
    height: diameter + itemHeight,
  };
  const direction = clockwise ? CLOCKWISE : COUNTERCLOCKWISE;
  const radialConfig: RadialConfig = {radius, arc, offsetDegrees, direction};

  return <div className='radial-list-container' style={containerStyle}>
    <RadialListItems items={items} radialConfig={radialConfig}/>
    <RadialListCenter centerElement={centerElement}/>
  </div>;
};

type RadialListItemsProps = {
  items: Array<React.ReactNode>,
  radialConfig: RadialConfig,
};

const RadialListItems = ({items, radialConfig}: RadialListItemsProps) => {
  const diameter = radialConfig.radius * 2;
  const ulStyle = {
    width: diameter,
    height: diameter,
  };
  const itemOffsets: Array<CartesianCoords> = offsetsFromCenter(items.length, radialConfig);

  return <ul className='radial-list absolute-center' style={ulStyle}>
    {items.map((item, i) => (<RadialListItem
            key={i}
            item={item}
            offsets={itemOffsets[i]}
    />))}
  </ul>;
};

type RadialListCenterProps = {
  centerElement?: ReactNode,
};

const RadialListCenter = ({centerElement}: RadialListCenterProps) => {
  if (!centerElement) {
    return null;
  }
  return <div className='radial-list-center absolute-center'>{centerElement}</div>;
};
