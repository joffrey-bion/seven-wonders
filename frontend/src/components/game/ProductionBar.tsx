import React from 'react';
import { ApiCountedResource, ApiProduction, ApiResourceType } from '../../api/model';
import './ProductionBar.css'

type ProductionBarProps = {
  gold: number,
  production: ApiProduction,
}

export const ProductionBar = ({gold, production}: ProductionBarProps) => {
  return <div className='production-bar'>
    <GoldIndicator amount={gold}/>
    <FixedResources resources={production.fixedResources}/>
    <AlternativeResources resources={production.alternativeResources}/>
  </div>;
};

type GoldIndicatorProps = {
  amount: number,
}
const GoldIndicator = ({amount}: GoldIndicatorProps) => {
  return <TokenWithCount tokenName="coin" count={amount} otherClasses="gold-indicator"/>
};

type FixedResourcesProps = {
  resources: ApiCountedResource[],
}
const FixedResources = ({resources}: FixedResourcesProps) => {
  return <div className="fixed-resources">
    {resources.map(r => <TokenWithCount key={r.type}
                                        tokenName={getTokenName(r.type)}
                                        count={r.count}
                                        otherClasses="resource-with-count"/>)}
  </div>
};

type AlternativeResourcesProps = {
  resources: ApiResourceType[][],
}
const AlternativeResources = ({resources}: AlternativeResourcesProps) => {
  return <div className="alternative-resources">
    {resources.map((types, i) => <ResourceChoice key={i} types={types}/>)}
  </div>
};

type ResourceChoiceProps = {
  types: ApiResourceType[],
}
const ResourceChoice = ({types}: ResourceChoiceProps) => {
  let typeImages = types.map(type => <TokenImage key={type} tokenName={getTokenName(type)}/>);
  let separator = <span className="choice-separator">∕</span>;
  return <div className="resource-choice">
    {intersperce(typeImages, separator)}
  </div>
};

function intersperce<T>(array: T[], separator: T): T[] {
  let result = array.reduce((acc: T[], elt: T) => acc.concat(elt, separator), []);
  return result.slice(0, -1); // remove extra separator at the end
}

type TokenWithCountProps = {
  tokenName: string,
  count: number,
  otherClasses?: string,
}
const TokenWithCount = ({tokenName, count, otherClasses = ""}: TokenWithCountProps) => {
  return <div className={`token-with-count ${otherClasses}`}>
    <TokenImage tokenName={tokenName}/>
    <span className="token-count">× {count}</span>
  </div>
};

type TokenImageProps = {
  tokenName: string,
}
const TokenImage = ({tokenName}: TokenImageProps) => {
  return <img src={getTokenImagePath(tokenName)} title={tokenName} alt={tokenName} className="token-img"/>
};

function getTokenImagePath(tokenName: string): string {
  return `/images/tokens/${tokenName}.png`;
}

function getTokenName(resourceType: ApiResourceType): string {
  return `resources/${resourceType.toLowerCase()}`;
}
