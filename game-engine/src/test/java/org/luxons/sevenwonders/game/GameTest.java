package org.luxons.sevenwonders.game;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.game.moves.Move;
import org.luxons.sevenwonders.game.moves.MoveType;
import org.luxons.sevenwonders.game.resources.BestPriceCalculator;
import org.luxons.sevenwonders.game.resources.ResourceTransaction;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GameTest {

    @Test
    public void testFullGame() {
        int nbPlayers = 5;
        Game game = createGame(nbPlayers);

        for (int age = 1; age <= 3; age++) {
            playAge(nbPlayers, game, age);
        }
    }

    private void playAge(int nbPlayers, Game game, int age) {
        for (int i = 0; i < 6; i++) {
            playTurn(nbPlayers, game, age, 7 - i);
        }
    }

    private static Game createGame(int nbPlayers) {
        CustomizableSettings settings = TestUtilsKt.testCustomizableSettings();
        return new GameDefinitionLoader().getGameDefinition().initGame(0, settings, nbPlayers);
    }

    private static void playTurn(int nbPlayers, Game game, int ageToCheck, int handSize) {
        Collection<PlayerTurnInfo> turnInfos = game.getCurrentTurnInfo();
        assertEquals(nbPlayers, turnInfos.size());

        Map<Integer, PlayerMove> sentMoves = new HashMap<>(turnInfos.size());
        for (PlayerTurnInfo turnInfo : turnInfos) {
            assertEquals(ageToCheck, turnInfo.getCurrentAge());
            assertEquals(handSize, turnInfo.getHand().size());
            PlayerMove move = getFirstAvailableMove(turnInfo);
            if (move != null) {
                game.prepareMove(turnInfo.getPlayerIndex(), move);
                sentMoves.put(turnInfo.getPlayerIndex(), move);
            }
        }
        assertTrue(game.allPlayersPreparedTheirMove());
        Table table = game.playTurn();
        checkLastPlayedMoves(sentMoves, table);
    }

    private static PlayerMove getFirstAvailableMove(PlayerTurnInfo turnInfo) {
        switch (turnInfo.getAction()) {
        case PLAY:
        case PLAY_2:
        case PLAY_LAST:
            return createPlayCardMove(turnInfo);
        case PICK_NEIGHBOR_GUILD:
            return createPickGuildMove(turnInfo);
        case WAIT:
        default:
            return null;
        }
    }

    private static PlayerMove createPlayCardMove(PlayerTurnInfo turnInfo) {
        for (HandCard handCard : turnInfo.getHand()) {
            if (handCard.isPlayable()) {
                Set<ResourceTransaction> resourcesToBuy = findResourcesToBuyFor(handCard, turnInfo);
                return new PlayerMove(MoveType.PLAY, handCard.getCard().getName(), resourcesToBuy);
            }
        }
        HandCard firstCardInHand = turnInfo.getHand().get(0);
        return new PlayerMove(MoveType.DISCARD, firstCardInHand.getCard().getName());
    }

    private static Set<ResourceTransaction> findResourcesToBuyFor(HandCard handCard, PlayerTurnInfo turnInfo) {
        if (handCard.isFree()) {
            return Collections.emptySet();
        }
        Resources requiredResources = handCard.getCard().getRequirements().getResources();
        Table table = turnInfo.getTable();
        int playerIndex = turnInfo.getPlayerIndex();
        ResourceTransactions transactions = BestPriceCalculator.bestSolution(requiredResources, table, playerIndex);
        return transactions.toTransactions();
    }

    private static PlayerMove createPickGuildMove(PlayerTurnInfo turnInfo) {
        List<Card> neighbourGuilds = turnInfo.getNeighbourGuildCards();
        assertNotNull(neighbourGuilds);
        assertFalse(neighbourGuilds.isEmpty());
        String cardName = neighbourGuilds.get(0).getName();
        return new PlayerMove(MoveType.COPY_GUILD, cardName);
    }

    private static void checkLastPlayedMoves(Map<Integer, PlayerMove> sentMoves, Table table) {
        for (Move move : table.getLastPlayedMoves()) {
            PlayerMove sentMove = sentMoves.get(move.getPlayerIndex());
            assertNotNull(sentMove);
            assertNotNull(move.getCard());
            assertEquals(sentMove.getCardName(), move.getCard().getName());
            assertSame(sentMove.getType(), move.getType());
        }
    }
}
