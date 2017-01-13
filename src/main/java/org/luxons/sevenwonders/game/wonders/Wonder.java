package org.luxons.sevenwonders.game.wonders;

import java.util.Arrays;
import java.util.List;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.resources.BoughtResources;
import org.luxons.sevenwonders.game.resources.ResourceType;

public class Wonder {

    private String name;

    private ResourceType initialResource;

    private List<WonderStage> stages;

    private String image;

    public Wonder() {
    }

    public Wonder(String name, ResourceType initialResource, WonderStage... stages) {
        this.name = name;
        this.initialResource = initialResource;
        this.stages = Arrays.asList(stages);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getInitialResource() {
        return initialResource;
    }

    public void setInitialResource(ResourceType initialResource) {
        this.initialResource = initialResource;
    }

    public List<WonderStage> getStages() {
        return stages;
    }

    public void setStages(List<WonderStage> stages) {
        this.stages = stages;
    }

    public int getNbBuiltStages() {
        return (int)stages.stream().filter(WonderStage::isBuilt).count();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isNextStageBuildable(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        int nextLevel = getNbBuiltStages();
        if (nextLevel == stages.size()) {
            return false;
        }
        return getNextStage().isBuildable(table, playerIndex, boughtResources);
    }

    public void buildLevel(CardBack cardBack) {
        getNextStage().build(cardBack);
    }

    private WonderStage getNextStage() {
        int nextLevel = getNbBuiltStages();
        if (nextLevel == stages.size()) {
            throw new IllegalStateException("This wonder has already reached its maximum level");
        }
        return stages.get(nextLevel);
    }

    public void activateLastBuiltStage(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        getLastBuiltStage().activate(table, playerIndex, boughtResources);
    }

    private WonderStage getLastBuiltStage() {
        int lastLevel = getNbBuiltStages() - 1;
        return stages.get(lastLevel);
    }

    public int computePoints(Table table, int playerIndex) {
        return stages.stream()
                .filter(WonderStage::isBuilt)
                .flatMap(c -> c.getEffects().stream())
                .mapToInt(e -> e.computePoints(table, playerIndex))
                .sum();
    }
}
