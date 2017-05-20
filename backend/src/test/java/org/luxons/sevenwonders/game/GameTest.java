package org.luxons.sevenwonders.game;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.game.moves.MoveType;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        CustomizableSettings settings = TestUtils.createCustomizableSettings();
        return new GameDefinitionLoader().getGameDefinition().initGame(0, settings, nbPlayers);
    }

    private static void playTurn(int nbPlayers, Game game, int ageToCheck, int handSize) {
        Collection<PlayerTurnInfo> turnInfos = game.getCurrentTurnInfo();
        assertEquals(nbPlayers, turnInfos.size());
        for (PlayerTurnInfo turnInfo : turnInfos) {
            assertEquals(ageToCheck, turnInfo.getCurrentAge());
            assertEquals(handSize, turnInfo.getHand().size());
            PlayerMove move = getFirstAvailableMove(turnInfo);
            if (move != null) {
                game.prepareMove(turnInfo.getPlayerIndex(), move);
            }
        }
        assertTrue(game.allPlayersPreparedTheirMove());
        game.playTurn();
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
            if (handCard.isFree()) {
                return createMove(handCard, MoveType.PLAY);
            }
        }
        return createMove(turnInfo.getHand().get(0), MoveType.DISCARD);
    }

    private static PlayerMove createPickGuildMove(PlayerTurnInfo turnInfo) {
        List<Card> neighbourGuilds = turnInfo.getNeighbourGuildCards();
        assertNotNull(neighbourGuilds);
        String cardName = neighbourGuilds.isEmpty() ? null : neighbourGuilds.get(0).getName();
        PlayerMove move = new PlayerMove();
        move.setCardName(cardName);
        move.setType(MoveType.COPY_GUILD);
        return move;
    }

    private static PlayerMove createMove(HandCard handCard, MoveType type) {
        PlayerMove move = new PlayerMove();
        move.setCardName(handCard.getCard().getName());
        move.setType(type);
        return move;
    }
}
