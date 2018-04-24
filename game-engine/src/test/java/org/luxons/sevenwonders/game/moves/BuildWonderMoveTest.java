package org.luxons.sevenwonders.game.moves;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.resources.BoughtResources;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;

public class BuildWonderMoveTest {

    @Test
    public void validate_ok() {
        Table table = TestUtils.createTable(3);
        List<Card> hand = TestUtils.createSampleCards(0, 7);
        Card cardToUse = hand.get(0);
        Move move = createBuildWonderMove(cardToUse, Collections.emptyList());

        move.validate(table, hand);
    }

    @Test(expected = InvalidMoveException.class)
    public void validate_failsWhenCardNotInHand() {
        Table table = TestUtils.createTable(3);
        List<Card> hand = TestUtils.createSampleCards(0, 7);
        Card cardToUse = TestUtils.createCard("Card that is not in the hand");
        Move move = createBuildWonderMove(cardToUse, Collections.emptyList());

        move.validate(table, hand);
    }

    @Test
    public void place_ok() {
        Settings settings = TestUtils.createSettings(3);
        Table table = TestUtils.createTable(settings);
        List<Card> hand = TestUtils.createSampleCards(0, 7);
        Card cardToUse = hand.get(0);
        Move move = createBuildWonderMove(cardToUse, Collections.emptyList());

        int initialStage = table.getBoard(0).getWonder().getNbBuiltStages();

        move.place(table, Collections.emptyList(), settings);

        int newStage = table.getBoard(0).getWonder().getNbBuiltStages();

        // we need to see the level increase before activation so that other players
        assertEquals(initialStage + 1, newStage);
    }

    private static Move createBuildWonderMove(Card card, List<BoughtResources> boughtResources) {
        PlayerMove playerMove = createPlayerMove(card, MoveType.UPGRADE_WONDER, boughtResources);
        return MoveType.UPGRADE_WONDER.resolve(0, card, playerMove);
    }

    private static PlayerMove createPlayerMove(Card card, MoveType type, List<BoughtResources> boughtResources) {
        PlayerMove playerMove = new PlayerMove();
        playerMove.setCardName(card.getName());
        playerMove.setType(type);
        playerMove.setBoughtResources(boughtResources);
        return playerMove;
    }
}
