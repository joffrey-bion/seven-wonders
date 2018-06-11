//@flow
import * as React from 'react';
import type { CartesianCoords, RadialConfig } from './radial-math';
import { cartesianOffsets, CLOCKWISE, COUNTERCLOCKWISE } from './radial-math';
import './RadialList.css';
import { RadialListItem } from './RadialListItem';

type RadialListProps = {
  items: Array<React.Node>,
  centerElement?: React.Node,
  diameter?: number, // 240px by default
  offsetDegrees?: number, // defaults to 0 = 12 o'clock
  arc?: number, // defaults to 360 (full circle)
  clockwise?: boolean, // defaults to 360 (full circle)
  itemWidth?: number,
  itemHeight?: number,
};

export const RadialList = ({items, centerElement, diameter = 240, offsetDegrees = 0, arc = 360, clockwise = true, itemWidth = 20, itemHeight = 20}: RadialListProps) => {
  const containerStyle = {
    width: diameter + itemWidth,
    height: diameter + itemHeight,
  };
  const radius = diameter / 2;
  const direction = clockwise ? CLOCKWISE : COUNTERCLOCKWISE;
  const radialConfig: RadialConfig = {radius, arc, offsetDegrees, direction};

  return <div className='radial-list-container' style={containerStyle}>
    <RadialListItems items={items} radialConfig={radialConfig}/>
    <RadialListCenter centerElement={centerElement}/>
  </div>;
};

type RadialListItemsProps = {
  items: Array<React.Node>,
  radialConfig: RadialConfig,
};

const RadialListItems = ({items, radialConfig}: RadialListItemsProps) => {
  const diameter = radialConfig.radius * 2;
  const ulStyle = {
    width: diameter,
    height: diameter,
  };
  const itemOffsets: Array<CartesianCoords> = cartesianOffsets(items.length, radialConfig);

  return <ul className='radial-list absolute-center' style={ulStyle}>
    {items.map((item, i) => (<RadialListItem
            key={i}
            item={item}
            offsets={itemOffsets[i]}
    />))}
  </ul>;
};

type RadialListCenterProps = {
  centerElement?: React.Node,
};

const RadialListCenter = ({centerElement}: RadialListCenterProps) => {
  if (!centerElement) {
    return null;
  }
  return <div className='radial-list-center absolute-center'>{centerElement}</div>;
};
