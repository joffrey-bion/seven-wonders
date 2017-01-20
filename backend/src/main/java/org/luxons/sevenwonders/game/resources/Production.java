package org.luxons.sevenwonders.game.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class Production {

    private final Resources fixedResources = new Resources();

    private final List<Set<ResourceType>> alternativeResources = new ArrayList<>();

    public void addFixedResource(ResourceType type, int quantity) {
        fixedResources.add(type, quantity);
    }

    public void addChoice(ResourceType... options) {
        EnumSet<ResourceType> optionSet = EnumSet.noneOf(ResourceType.class);
        optionSet.addAll(Arrays.asList(options));
        alternativeResources.add(optionSet);
    }

    public void addAll(Resources resources) {
        fixedResources.addAll(resources);
    }

    public void addAll(Production production) {
        fixedResources.addAll(production.getFixedResources());
        alternativeResources.addAll(production.getAlternativeResources());
    }

    public Resources getFixedResources() {
        return fixedResources;
    }

    public List<Set<ResourceType>> getAlternativeResources() {
        return alternativeResources;
    }

    public boolean contains(Resources resources) {
        if (fixedResources.contains(resources)) {
            return true;
        }
        Resources remaining = resources.minus(fixedResources);
        return containedInAlternatives(remaining);
    }

    private boolean containedInAlternatives(Resources resources) {
        return containedInAlternatives(resources, alternativeResources);
    }

    private static boolean containedInAlternatives(Resources resources, List<Set<ResourceType>> alternatives) {
        if (resources.isEmpty()) {
            return true;
        }
        for (Entry<ResourceType, Integer> entry : resources.getQuantities().entrySet()) {
            ResourceType type = entry.getKey();
            int count = entry.getValue();
            if (count <= 0) {
                continue;
            }
            Set<ResourceType> candidate = findFirstAlternativeContaining(alternatives, type);
            if (candidate == null) {
                return false; // no alternative produces the resource of this entry
            }
            entry.setValue(count - 1);
            alternatives.remove(candidate);
            boolean remainingAreContainedToo = containedInAlternatives(resources, alternatives);
            entry.setValue(count);
            alternatives.add(candidate);
            if (remainingAreContainedToo) {
                return true;
            }
        }
        return false;
    }

    private static Set<ResourceType> findFirstAlternativeContaining(List<Set<ResourceType>> alternatives, ResourceType type) {
        return alternatives.stream().filter(a -> a.contains(type)).findAny().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Production that = (Production)o;
        return Objects.equals(fixedResources, that.fixedResources) && Objects.equals(alternativeResources,
                that.alternativeResources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fixedResources, alternativeResources);
    }
}
