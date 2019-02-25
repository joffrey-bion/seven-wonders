import React from 'react';
import type { ApiCountedResource, ApiProduction, ApiResourceType } from '../../api/model';
import './ProductionBar.css'

type ProductionBarProps = {
  production: ApiProduction,
}

export const ProductionBar = ({production}: ProductionBarProps) => {
  return <div className='production-bar'>
    <FixedResources resources={production.fixedResources}/>
    <AlternativeResources resources={production.alternativeResources}/>
  </div>;
};

type FixedResourcesProps = {
  resources: ApiCountedResource[],
}

const FixedResources = ({resources}: FixedResourcesProps) => {
  return <div className="fixed-resources">
    {resources.map(r => <ResourceCount key={r.type} resource={r}/>)}
  </div>
};

type ResourceCountProps = {
  resource: ApiCountedResource,
}

const ResourceCount = ({resource}: ResourceCountProps) => {
  return <div className="resource-with-count">
    <ResourceImage type={resource.type}/>
    <span className="resource-count">× {resource.count}</span>
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
  let typeImages = types.map(type => <ResourceImage key={type} type={type}/>);
  let separator = <span className="choice-separator">∕</span>;
  return <div className="resource-choice">
    {intersperce(typeImages, separator)}
  </div>
};

function intersperce(array: T[], separator: T): T[] {
  let result = array.reduce((acc, elt) => acc.concat(elt, separator), []);
  return result.slice(0, -1);
}

type ResourceImageProps = {
  type: ApiResourceType,
}

const ResourceImage = ({type}: ResourceImageProps) => {
  return <img src={getImagePath(type)} title={type} alt={type} className="resource-img"/>
};

function getImagePath(resourceType: ApiResourceType): string {
  return `/images/resources/${resourceType.toLowerCase()}.png`;
}
