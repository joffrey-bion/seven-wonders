package org.luxons.sevenwonders.game.resources;

import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;

public enum Provider {
    LEFT_PLAYER(RelativeBoardPosition.LEFT),
    RIGHT_PLAYER(RelativeBoardPosition.RIGHT);

    private final RelativeBoardPosition boardPosition;

    Provider(RelativeBoardPosition boardPosition) {
        this.boardPosition = boardPosition;
    }

    public RelativeBoardPosition getBoardPosition() {
        return boardPosition;
    }
}
