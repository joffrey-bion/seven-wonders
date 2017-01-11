package org.luxons.sevenwonders.game.api;

public enum Action {
    PLAY("Pick the card you want to play."),
    PLAY_2("Pick the card you want to play first. Note that you have the ability to play these 2 last cards. "
            + "You will choose how to play the last one during your next turn."),
    PLAY_LAST("You have the special ability to play your last card. Choose how you want to play it."),
    WAIT("Please wait for other players to perform extra actions.");

    private final String message;

    Action(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
