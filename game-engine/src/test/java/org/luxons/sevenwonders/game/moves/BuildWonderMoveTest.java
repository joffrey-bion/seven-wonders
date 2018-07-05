package org.luxons.sevenwonders.game.moves;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BuildWonderMoveTest {

    @Test(expected = InvalidMoveException.class)
    public void validate_failsWhenCardNotInHand() {
        Table table = TestUtilsKt.testTable(3);
        List<Card> hand = TestUtilsKt.createSampleCards(0, 7);
        Card anotherCard = TestUtilsKt.testCard("Card that is not in the hand");
        Move move = TestUtilsKt.createMove(0, anotherCard, MoveType.UPGRADE_WONDER);

        move.validate(table, hand);
    }

    @Test(expected = InvalidMoveException.class)
    public void validate_failsWhenWonderIsCompletelyBuilt() {
        Settings settings = TestUtilsKt.testSettings(3);
        Table table = TestUtilsKt.testTable(settings);
        List<Card> hand = TestUtilsKt.createSampleCards(0, 7);

        fillPlayerWonderLevels(settings, table, hand);

        // should fail because the wonder is already full
        buildOneWonderLevel(settings, table, hand, 4);
    }

    private static void fillPlayerWonderLevels(Settings settings, Table table, List<Card> hand) {
        try {
            int nbLevels = table.getBoard(0).getWonder().getStages().size();
            for (int i = 0; i < nbLevels; i++) {
                buildOneWonderLevel(settings, table, hand, i);
            }
        } catch (InvalidMoveException e) {
            fail("Building wonder levels should not fail before being full");
        }
    }

    private static void buildOneWonderLevel(Settings settings, Table table, List<Card> hand, int cardIndex) {
        Card card = hand.get(cardIndex);
        Move move = TestUtilsKt.createMove(0, card, MoveType.UPGRADE_WONDER);
        move.validate(table, hand);
        move.place(table, Collections.emptyList(), settings);
        move.activate(table, Collections.emptyList(), settings);
    }

    @Test
    public void place_increasesWonderLevel() {
        Settings settings = TestUtilsKt.testSettings(3);
        Table table = TestUtilsKt.testTable(settings);
        List<Card> hand = TestUtilsKt.createSampleCards(0, 7);
        Card cardToUse = hand.get(0);
        Move move = TestUtilsKt.createMove(0, cardToUse, MoveType.UPGRADE_WONDER);
        move.validate(table, hand); // should not fail

        int initialStage = table.getBoard(0).getWonder().getNbBuiltStages();

        move.place(table, Collections.emptyList(), settings);

        int newStage = table.getBoard(0).getWonder().getNbBuiltStages();

        // we need to see the level increase before activation so that other players
        assertEquals(initialStage + 1, newStage);
    }

}
