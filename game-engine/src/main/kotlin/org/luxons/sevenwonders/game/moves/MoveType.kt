package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card

enum class MoveType {
    PLAY {
        override fun resolve(playerIndex: Int, card: Card, move: PlayerMove) = PlayCardMove(playerIndex, card, move)
    },
    PLAY_FREE {
        override fun resolve(playerIndex: Int, card: Card, move: PlayerMove) = PlayFreeCardMove(playerIndex, card, move)
    },
    UPGRADE_WONDER {
        override fun resolve(playerIndex: Int, card: Card, move: PlayerMove) = BuildWonderMove(playerIndex, card, move)
    },
    DISCARD {
        override fun resolve(playerIndex: Int, card: Card, move: PlayerMove) = DiscardMove(playerIndex, card, move)
    },
    COPY_GUILD {
        override fun resolve(playerIndex: Int, card: Card, move: PlayerMove) = CopyGuildMove(playerIndex, card, move)
    };

    abstract fun resolve(playerIndex: Int, card: Card, move: PlayerMove): Move
}
