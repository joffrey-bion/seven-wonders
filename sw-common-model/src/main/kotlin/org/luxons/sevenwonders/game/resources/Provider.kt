package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.boards.RelativeBoardPosition

enum class Provider(val boardPosition: RelativeBoardPosition) {
    LEFT_PLAYER(RelativeBoardPosition.LEFT),
    RIGHT_PLAYER(RelativeBoardPosition.RIGHT)
}
