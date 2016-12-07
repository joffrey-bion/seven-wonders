package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.data.GameData;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class Game {

    private final GameData data;

    private final Settings settings;

    private List<Player> players;

    private List<Board> boards;

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
        boards.add(new Board(pickWonder(), settings));
        return playerId;
    }

    private boolean hasStarted() {
        return state == State.PLAYING;
    }

    private boolean maxPlayersReached() {
        return players.size() >= data.getMaxPlayers();
    }

    private Wonder pickWonder() {
        List<Wonder> availableWonders = new ArrayList<>(data.getWonders());
        removeAlreadyUsedWondersFrom(availableWonders);
        Collections.shuffle(availableWonders);
        return availableWonders.get(0);
    }

    private void removeAlreadyUsedWondersFrom(List<Wonder> wonders) {
        boards.stream().map(Board::getWonder).forEach(wonders::remove);
    }

    public synchronized void startGame() {
        if (!hasEnoughPlayers()) {
            throw new PlayerUnderflowException();
        }
        state = State.PLAYING;
    }

    private boolean hasEnoughPlayers() {
        return players.size() >= data.getMinPlayers();
    }

    public class GameAlreadyStartedException extends IllegalStateException {
    }

    public class PlayerOverflowException extends IllegalStateException {
    }

    public class PlayerUnderflowException extends IllegalStateException {
    }
}
