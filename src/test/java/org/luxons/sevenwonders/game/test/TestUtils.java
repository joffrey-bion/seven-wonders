package org.luxons.sevenwonders.game.test;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class TestUtils {

    public static Board createBoard(ResourceType initialResource) {
        Settings settings = new Settings();
        Wonder wonder = new Wonder("Test Wonder " + initialResource.getSymbol(), initialResource);
        return new Board(wonder, settings);
    }

    public static Board createBoard(ResourceType initialResource, ResourceType... production) {
        Board board = createBoard(initialResource);
        board.getProduction().addAll(createFixedProduction(production));
        return board;
    }

    public static Board createBoard(ResourceType initialResource, int gold, ResourceType... production) {
        Board board = createBoard(initialResource, production);
        board.setGold(gold);
        return board;
    }

    public static Wonder createWonder() {
        return createWonder(ResourceType.WOOD);
    }

    public static Wonder createWonder(ResourceType initialResource) {
        return new Wonder("Test Wonder " + initialResource.getSymbol(), initialResource);
    }

    public static Production createFixedProduction(ResourceType... producedTypes) {
        Production production = new Production();
        Resources fixedProducedResources = production.getFixedResources();
        fixedProducedResources.addAll(createResources(producedTypes));
        return production;
    }

    public static Resources createResources(ResourceType... types) {
        Resources resources = new Resources();
        for (ResourceType producedType : types) {
            resources.add(producedType, 1);
        }
        return resources;
    }
}
