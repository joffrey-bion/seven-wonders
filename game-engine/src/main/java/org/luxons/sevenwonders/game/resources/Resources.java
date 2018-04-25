package org.luxons.sevenwonders.game.resources;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Resources {

    private final Map<ResourceType, Integer> quantities = new EnumMap<>(ResourceType.class);

    public static Resources of(ResourceType... types) {
        Resources resources = new Resources();
        for (ResourceType type : types) {
            resources.add(type, 1);
        }
        return resources;
    }

    public void add(ResourceType type, int quantity) {
        quantities.merge(type, quantity, (x, y) -> x + y);
    }

    public void remove(ResourceType type, int quantity) {
        if (getQuantity(type) < quantity) {
            throw new NoSuchElementException(String.format("Can't remove %d resources of type %s", quantity, type));
        }
        quantities.computeIfPresent(type, (t, oldQty) -> oldQty - quantity);
    }

    public void addAll(Resources resources) {
        resources.quantities.forEach(this::add);
    }

    public int getQuantity(ResourceType type) {
        return quantities.getOrDefault(type, 0);
    }

    public List<ResourceType> asList() {
        return quantities.entrySet()
                         .stream()
                         .flatMap(e -> Stream.generate(e::getKey).limit(e.getValue()))
                         .collect(Collectors.toList());
    }

    public boolean contains(Resources resources) {
        return resources.quantities.entrySet().stream().allMatch(this::hasAtLeast);
    }

    private boolean hasAtLeast(Entry<ResourceType, Integer> quantity) {
        return quantity.getValue() <= getQuantity(quantity.getKey());
    }

    /**
     * Creates new {@link Resources} object containing these resources minus the given resources.
     *
     * @param resources
     *         the resources to subtract from these resources
     *
     * @return a new {@link Resources} object containing these resources minus the given resources.
     */
    public Resources minus(Resources resources) {
        Resources diff = new Resources();
        quantities.forEach((type, count) -> {
            int remainder = count - resources.getQuantity(type);
            diff.quantities.put(type, Math.max(0, remainder));
        });
        return diff;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return quantities.values().stream().reduce(0, Integer::sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resources resources = (Resources) o;
        return Objects.equals(quantities, resources.quantities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantities);
    }

    @Override
    public String toString() {
        return "Resources{" + "quantities=" + quantities + '}';
    }
}
