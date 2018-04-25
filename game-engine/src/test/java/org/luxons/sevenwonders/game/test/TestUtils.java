package org.luxons.sevenwonders.game.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.Science;
import org.luxons.sevenwonders.game.boards.ScienceType;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.effects.ScienceProgress;
import org.luxons.sevenwonders.game.moves.Move;
import org.luxons.sevenwonders.game.moves.MoveType;
import org.luxons.sevenwonders.game.resources.BoughtResources;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.Provider;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.wonders.Wonder;
import org.luxons.sevenwonders.game.wonders.WonderStage;

public class TestUtils {

    private static final long SEED = 42;

    public static CustomizableSettings createCustomizableSettings() {
        CustomizableSettings customizableSettings = new CustomizableSettings();
        customizableSettings.setRandomSeedForTests(SEED);
        return customizableSettings;
    }

    public static Settings createSettings(int nbPlayers) {
        return new Settings(nbPlayers, createCustomizableSettings());
    }

    public static Table createTable(int nbPlayers) {
        return createTable(createSettings(nbPlayers));
    }

    public static Table createTable(Settings settings) {
        return new Table(createBoards(settings.getNbPlayers(), settings));
    }

    private static List<Board> createBoards(int count, Settings settings) {
        List<Board> boards = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            boards.add(createBoard(ResourceType.WOOD, settings));
        }
        return boards;
    }

    private static Board createBoard(ResourceType initialResource, Settings settings) {
        Wonder wonder = createWonder(initialResource);
        return new Board(wonder, 0, settings);
    }

    public static Board createBoard(ResourceType initialResource) {
        return createBoard(initialResource, createSettings(5));
    }

    private static Board createBoard(ResourceType initialResource, ResourceType... production) {
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
        WonderStage stage1 = createWonderStage();
        WonderStage stage2 = createWonderStage();
        WonderStage stage3 = createWonderStage();
        return new Wonder("Test Wonder " + initialResource.getSymbol(), initialResource, stage1, stage2, stage3);
    }

    private static WonderStage createWonderStage(Effect... effects) {
        return new WonderStage(new Requirements(), Arrays.asList(effects));
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

    public static Requirements createRequirements(ResourceType... types) {
        Resources resources = createResources(types);
        Requirements requirements = new Requirements();
        requirements.setResources(resources);
        return requirements;
    }

    public static List<Card> createSampleCards(int fromIndex, int nbCards) {
        List<Card> sampleCards = new ArrayList<>();
        for (int i = fromIndex; i < fromIndex + nbCards; i++) {
            sampleCards.add(createCard(i, Color.BLUE));
        }
        return sampleCards;
    }

    public static Card createCard(String name) {
        return createCard(name, Color.BLUE);
    }

    public static Card createCard(Color color) {
        return createCard("Test Card", color);
    }

    public static Card createCard(Color color, Effect effect) {
        return createCard("Test Card", color, effect);
    }

    private static Card createCard(int num, Color color) {
        return createCard("Test Card " + num, color);
    }

    public static Card createGuildCard(int num, Effect effect) {
        return createCard("Test Guild " + num, Color.PURPLE, effect);
    }

    private static Card createCard(String name, Color color, Effect... effects) {
        Card card = new Card(name, color, new Requirements(), Arrays.asList(effects), null, null, "path/to/card/image");
        card.setBack(createCardBack());
        return card;
    }

    private static CardBack createCardBack() {
        return new CardBack("image-III");
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

    public static void playCardWithEffect(Table table, int playerIndex, Color color, Effect effect) {
        Card card = createCard(color, effect);
        Board board = table.getBoard(playerIndex);
        board.addCard(card);
        card.applyTo(table, playerIndex, Collections.emptyList());
    }

    public static Move createMove(int playerIndex, Card card, MoveType type, BoughtResources... boughtResources) {
        PlayerMove playerMove = createPlayerMove(card, type, boughtResources);
        return type.resolve(playerIndex, card, playerMove);
    }

    private static PlayerMove createPlayerMove(Card card, MoveType type, BoughtResources... boughtResources) {
        PlayerMove playerMove = new PlayerMove();
        playerMove.setCardName(card.getName());
        playerMove.setType(type);
        playerMove.setBoughtResources(Arrays.asList(boughtResources));
        return playerMove;
    }
}
