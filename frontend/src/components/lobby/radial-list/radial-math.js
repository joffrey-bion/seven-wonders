//@flow

export type CartesianCoords = {
  x: number,
  y: number,
}
type CylindricalCoords = {
  radius: number,
  thetaDegrees: number,
}

const toRad = (deg) => deg * (Math.PI / 180);
const roundedProjection = (radius, theta, trigFn) => Math.round(radius * trigFn(theta));
const xProjection = (radius, theta) => roundedProjection(radius, theta, Math.cos);
const yProjection = (radius, theta) => roundedProjection(radius, theta, Math.sin);

const toCartesian = ({radius, thetaDegrees}: CylindricalCoords): CartesianCoords => ({
  x: xProjection(radius, toRad(thetaDegrees)),
  y: yProjection(radius, toRad(thetaDegrees)),
});

export type Direction = -1 | 1;
export const CLOCKWISE: Direction = -1;
export const COUNTERCLOCKWISE: Direction = 1;

export type RadialConfig = {|
  radius: number,
  arc: number,
  offsetDegrees: number,
  direction: Direction,
|}
const DEFAULT_CONFIG: RadialConfig = {
  radius: 120,
  arc: 360,
  offsetDegrees: 0,
  direction: CLOCKWISE,
};

const DEFAULT_START = 90; // Up

export function cartesianOffsets(nbItems: number, radialConfig: RadialConfig = DEFAULT_CONFIG): Array<CartesianCoords> {
  return Array.from({length: nbItems}, (v, i) => itemCartesianOffsets(i, nbItems, radialConfig));
}

function itemCartesianOffsets(index: number, nbItems: number, {radius, arc, direction, offsetDegrees}: RadialConfig): CartesianCoords {
  const startAngle = DEFAULT_START + direction * offsetDegrees;
  const angleStep = arc / nbItems;
  const itemAngle = startAngle + direction * angleStep * index;
  return toCartesian({radius, thetaDegrees: itemAngle});
}
