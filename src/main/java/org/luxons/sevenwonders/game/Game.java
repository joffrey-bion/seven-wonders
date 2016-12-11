package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class Game {

    private final GameData data;

    private final Settings settings;

    private final List<Player> players;

    private final List<Board> boards;

    private State state = State.LOBBY;

    public Game(Settings settings, GameData data) {
        this.settings = settings;
        this.data = data;
        this.players = new ArrayList<>(3);
        this.boards = new ArrayList<>(3);
    }

    public synchronized int addPlayer(Player player) {
        if (hasStarted()) {
            throw new GameAlreadyStartedException();
        }
        if (maxPlayersReached()) {
            throw new PlayerOverflowException();
        }
        int playerId = players.size();
        players.add(player);
        return playerId;
    }

    public synchronized void startGame() {
        if (!hasEnoughPlayers()) {
            throw new PlayerUnderflowException();
        }
        state = State.PLAYING;
        randomizeBoards();
    }

    private boolean hasStarted() {
        return state == State.PLAYING;
    }

    private boolean maxPlayersReached() {
        return players.size() >= data.getMaxPlayers();
    }

    private boolean hasEnoughPlayers() {
        return players.size() >= data.getMinPlayers();
    }

    private void randomizeBoards() {
        List<Wonder> randomizedWonders = new ArrayList<>(data.getWonders());
        Collections.shuffle(randomizedWonders, settings.getRandom());
        randomizedWonders.stream().map(w -> new Board(w, settings)).forEach(boards::add);
    }

    public class GameAlreadyStartedException extends IllegalStateException {
    }

    public class PlayerOverflowException extends IllegalStateException {
    }

    public class PlayerUnderflowException extends IllegalStateException {
    }
}
