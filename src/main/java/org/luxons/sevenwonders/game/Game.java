package org.luxons.sevenwonders.game;

import org.luxons.sevenwonders.game.boards.Board;

import java.util.List;

public class Game {

    private final Settings settings;

    private final List<Board> boards;

    private final Decks decks;

    public Game(Settings settings, List<Board> boards, Decks decks) {
        this.settings = settings;
        this.boards = boards;
        this.decks = decks;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public Decks getDecks() {
        return decks;
    }

}
