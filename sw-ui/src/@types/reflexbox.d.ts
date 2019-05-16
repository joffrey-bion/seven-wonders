declare module 'reflexbox' {

  import { HTMLAttributes } from 'react';
  import * as React from 'react'

  export interface BoxProps {
    w?: number | string,
    h?: number | string,

    flex?: boolean,
    wrap?: boolean,
    column?: boolean,
    auto?: boolean,
    order?: number,
    align?: "flex-start" | "flex-end" | "center" | "stretch" | "baseline",
    justify?: "flex-start" | "flex-end" | "center" | "space-between" | "space-around" | "space-evenly",

    m?: number | string,
    mx?: number | string,
    my?: number | string,
    mt?: number | string,
    mb?: number | string,
    ml?: number | string,
    mr?: number | string,

    p?: number | string,
    px?: number | string,
    py?: number | string,
    pt?: number | string,
    pb?: number | string,
    pl?: number | string,
    pr?: number | string,
  }

  export class Flex extends React.Component<HTMLAttributes & BoxProps> { }
  export class Box extends React.Component<HTMLAttributes & BoxProps> { }
}
