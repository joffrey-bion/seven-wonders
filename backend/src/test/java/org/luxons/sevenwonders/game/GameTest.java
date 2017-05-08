package org.luxons.sevenwonders.game;

import java.util.Collection;

import org.junit.Test;
import org.luxons.sevenwonders.game.api.Action;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.game.moves.MoveType;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;

public class GameTest {

    @Test
    public void test() {
        int nbPlayers = 5;
        Game game = createGame(nbPlayers);
        Collection<PlayerTurnInfo> turnInfos = game.getCurrentTurnInfo();

        assertEquals(nbPlayers, turnInfos.size());

        for (PlayerTurnInfo turnInfo : turnInfos) {
            Action action = turnInfo.getAction();
            assertEquals(Action.PLAY, action);
            assertEquals(1, turnInfo.getCurrentAge());
            assertEquals(7, turnInfo.getHand().size());
            PlayerMove move = getFirstAvailableMove(turnInfo);
            game.prepareMove(turnInfo.getPlayerIndex(), move);
        }
    }

    private PlayerMove getFirstAvailableMove(PlayerTurnInfo turnInfo) {
        for (HandCard handCard : turnInfo.getHand()) {
            if (handCard.isPlayable()) {
                return createMove(handCard, MoveType.PLAY);
            }
        }
        return createMove(turnInfo.getHand().get(0), MoveType.DISCARD);
    }

    private PlayerMove createMove(HandCard handCard, MoveType play) {
        PlayerMove move = new PlayerMove();
        move.setCardName(handCard.getCard().getName());
        move.setType(play);
        return move;
    }

    private static Game createGame(int nbPlayers) {
        CustomizableSettings settings = TestUtils.createCustomizableSettings();
        return new GameDefinitionLoader().getGameDefinition().initGame(0, settings, nbPlayers);
    }
}
