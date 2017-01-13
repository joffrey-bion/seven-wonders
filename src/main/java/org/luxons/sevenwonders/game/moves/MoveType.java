package org.luxons.sevenwonders.game.moves;

import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.cards.Card;

public enum MoveType {
    PLAY {
        @Override
        public Move resolve(int playerIndex, Card card, PlayerMove move) {
            return new PlayCardMove(playerIndex, card, move);
        }
    },
    UPGRADE_WONDER {
        @Override
        public Move resolve(int playerIndex, Card card, PlayerMove move) {
            return new BuildWonderMove(playerIndex, card, move);
        }
    },
    DISCARD {
        @Override
        public Move resolve(int playerIndex, Card card, PlayerMove move) {
            return new DiscardMove(playerIndex, card, move);
        }
    },
    COPY_GUILD {
        @Override
        public Move resolve(int playerIndex, Card card, PlayerMove move) {
            return new CopyGuildMove(playerIndex, card, move);
        }
    };

    public abstract Move resolve(int playerIndex, Card card, PlayerMove move);
}
