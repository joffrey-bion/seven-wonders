package org.luxons.sevenwonders.game.api;

import org.luxons.sevenwonders.game.boards.Provider;
import org.luxons.sevenwonders.game.resources.Resources;

public class BoughtResources {

    private Provider provider;

    private Resources resources;

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
}
