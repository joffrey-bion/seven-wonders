export type CartesianCoords = {
  x: number,
  y: number,
}
type PolarCoords = {
  radius: number,
  angleDeg: number,
}

const toRad = (deg: number) => deg * (Math.PI / 180);
const roundedProjection = (radius: number, thetaRad: number, trigFn: (angle: number) => number) => Math.round(radius * trigFn(thetaRad));
const xProjection = (radius: number, thetaRad: number) => roundedProjection(radius, thetaRad, Math.cos);
const yProjection = (radius: number, thetaRad: number) => roundedProjection(radius, thetaRad, Math.sin);

const toCartesian = ({radius, angleDeg}: PolarCoords): CartesianCoords => ({
  x: xProjection(radius, toRad(angleDeg)),
  y: yProjection(radius, toRad(angleDeg)),
});

export type Direction = -1 | 1;
export const CLOCKWISE: Direction = -1;
export const COUNTERCLOCKWISE: Direction = 1;

export type RadialConfig = {
  radius: number,
  arc: number,
  offsetDegrees: number,
  direction: Direction,
}
const DEFAULT_CONFIG: RadialConfig = {
  radius: 120,
  arc: 360,
  offsetDegrees: 0,
  direction: CLOCKWISE,
};

const DEFAULT_START = 90; // Up

export function offsetsFromCenter(nbItems: number, radialConfig: RadialConfig = DEFAULT_CONFIG): Array<CartesianCoords> {
  return Array.from({length: nbItems}, (v, i) => itemCartesianOffsets(i, nbItems, radialConfig));
}

function itemCartesianOffsets(index: number, nbItems: number, {radius, arc, direction, offsetDegrees}: RadialConfig): CartesianCoords {
  const startAngle = DEFAULT_START + direction * offsetDegrees;
  const angleStep = arc / nbItems;
  const itemAngle = startAngle + direction * angleStep * index;
  return toCartesian({radius, angleDeg: itemAngle});
}
