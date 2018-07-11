package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card

enum class MoveType(val create: (move: PlayerMove, card: Card, context: PlayerContext) -> Move) {
    PLAY(::PlayCardMove),
    PLAY_FREE(::PlayFreeCardMove),
    UPGRADE_WONDER(::BuildWonderMove),
    DISCARD(::DiscardMove),
    COPY_GUILD(::CopyGuildMove);

    internal fun resolve(move: PlayerMove, card: Card, context: PlayerContext): Move {
        return create(move, card, context)
    }
}
