package org.luxons.sevenwonders.game.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.api.Table;

public class ResourceTransactions {

    private final Map<Provider, Resources> resourcesByProvider;

    public ResourceTransactions() {
        this.resourcesByProvider = new HashMap<>();
    }

    public ResourceTransactions(Collection<ResourceTransaction> transactions) {
        this();
        transactions.forEach(t -> add(t.getProvider(), t.getResources()));
    }

    public void add(Provider provider, Resources resources) {
        resourcesByProvider.putIfAbsent(provider, new Resources());
        resourcesByProvider.merge(provider, resources, Resources::addAll);
    }

    public void remove(Provider provider, Resources resources) {
        resourcesByProvider.compute(provider, (p, prevResources) -> {
            if (prevResources == null) {
                throw new IllegalStateException("Cannot remove resources from resource transactions");
            }
            return prevResources.minus(resources);
        });
    }

    public void execute(Table table, int playerIndex) {
        toTransactions().forEach(t -> t.execute(table, playerIndex));
    }

    public Set<ResourceTransaction> toTransactions() {
        return resourcesByProvider.entrySet()
                                  .stream()
                                  .filter(e -> !e.getValue().isEmpty())
                                  .map(e -> new ResourceTransaction(e.getKey(), e.getValue()))
                                  .collect(Collectors.toSet());
    }

    public Resources asResources() {
        return resourcesByProvider.values().stream().reduce(new Resources(), Resources::addAll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceTransactions that = (ResourceTransactions) o;
        return Objects.equals(resourcesByProvider, that.resourcesByProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourcesByProvider);
    }
}
