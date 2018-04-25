package org.luxons.sevenwonders.game.resources;

import java.util.Objects;

public class BoughtResources {

    private Provider provider;

    private Resources resources;

    public BoughtResources() {
    }

    public BoughtResources(Provider provider, Resources resources) {
        this.provider = provider;
        this.resources = resources;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoughtResources that = (BoughtResources) o;
        return provider == that.provider && Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, resources);
    }

    @Override
    public String toString() {
        return "BoughtResources{" + "provider=" + provider + ", resources=" + resources + '}';
    }
}
