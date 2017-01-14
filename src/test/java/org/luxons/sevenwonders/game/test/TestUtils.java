package org.luxons.sevenwonders.game.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.Science;
import org.luxons.sevenwonders.game.boards.ScienceType;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.effects.ScienceProgress;
import org.luxons.sevenwonders.game.resources.BoughtResources;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.Provider;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.wonders.Wonder;
import org.luxons.sevenwonders.game.wonders.WonderStage;

public class TestUtils {

    public static Table createTable(int nbPlayers) {
        return new Table(createBoards(nbPlayers));
    }

    public static List<Board> createBoards(int count) {
        List<Board> boards = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            boards.add(createBoard(ResourceType.WOOD));
        }
        return boards;
    }

    public static List<Player> createPlayers(int count) {
        List<Player> players = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String userName = "testUser" + i;
            String displayName = "Test User " + i;
            Player player = new Player(userName, displayName);
            players.add(player);
        }
        return players;
    }

    public static Board createBoard(ResourceType initialResource) {
        Settings settings = new Settings(5);
        Wonder wonder = createWonder(initialResource);

        String userName = "testUser" + initialResource.getSymbol();
        String displayName = "Test User " + initialResource.getSymbol();
        Player player = new Player(userName, displayName);

        return new Board(wonder, player, settings);
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
        WonderStage stage1 = new WonderStage();
        stage1.setRequirements(new Requirements());
        WonderStage stage2 = new WonderStage();
        stage1.setRequirements(new Requirements());
        WonderStage stage3 = new WonderStage();
        stage1.setRequirements(new Requirements());
        return new Wonder("Test Wonder " + initialResource.getSymbol(), initialResource, stage1, stage2, stage3);
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

    public static BoughtResources createBoughtResources(Provider provider, ResourceType... resources) {
        BoughtResources boughtResources = new BoughtResources();
        boughtResources.setProvider(provider);
        boughtResources.setResources(TestUtils.createResources(resources));
        return boughtResources;
    }

    public static List<Card> createSampleCards(int fromIndex, int nbCards) {
        List<Card> sampleCards = new ArrayList<>();
        for (int i = fromIndex; i < fromIndex + nbCards; i++) {
            sampleCards.add(createCard(i, Color.BLUE));
        }
        return sampleCards;
    }

    public static Card createCard(String name) {
        return new Card(name, Color.BLUE, new Requirements(), null, null, null, null);
    }

    private static Card createCard(int num, Color color) {
        return new Card("Test Card " + num, color, new Requirements(), null, null, null, null);
    }

    public static void addCards(Board board, int nbCardsOfColor, int nbOtherCards, Color color) {
        addCards(board, nbCardsOfColor, color);
        Color otherColor = getDifferentColorFrom(color);
        addCards(board, nbOtherCards, otherColor);
    }

    public static void addCards(Board board, int nbCards, Color color) {
        for (int i = 0; i < nbCards; i++) {
            board.addCard(createCard(i, color));
        }
    }

    public static Color getDifferentColorFrom(Color... colors) {
        List<Color> forbiddenColors = Arrays.asList(colors);
        for (Color color : Color.values()) {
            if (!forbiddenColors.contains(color)) {
                return color;
            }
        }
        throw new IllegalArgumentException("All colors are forbidden!");
    }

    public static ScienceProgress createScienceProgress(int compasses, int wheels, int tablets, int jokers) {
        ScienceProgress progress = new ScienceProgress();
        progress.setScience(TestUtils.createScience(compasses, wheels, tablets, jokers));
        return progress;
    }

    public static Science createScience(int compasses, int wheels, int tablets, int jokers) {
        Science science = new Science();
        if (compasses > 0) {
            science.add(ScienceType.COMPASS, compasses);
        }
        if (wheels > 0) {
            science.add(ScienceType.WHEEL, wheels);
        }
        if (tablets > 0) {
            science.add(ScienceType.TABLET, tablets);
        }
        if (jokers > 0) {
            science.addJoker(jokers);
        }
        return science;
    }
}
