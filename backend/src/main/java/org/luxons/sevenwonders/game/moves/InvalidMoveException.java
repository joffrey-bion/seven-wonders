package org.luxons.sevenwonders.game.moves;

public class InvalidMoveException extends IllegalArgumentException {

    public InvalidMoveException(String message) {
        super(message);
    }
}
