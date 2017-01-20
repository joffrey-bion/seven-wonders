package org.luxons.sevenwonders.game.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.Table;

public class Hands {

    private final int nbPlayers;

    private Map<Integer, List<Card>> hands;

    Hands(Map<Integer, List<Card>> hands, int nbPlayers) {
        this.hands = hands;
        this.nbPlayers = nbPlayers;
    }

    public List<Card> get(int playerIndex) {
        if (!hands.containsKey(playerIndex)) {
            throw new PlayerIndexOutOfBoundsException(playerIndex);
        }
        return hands.get(playerIndex);
    }

    public List<HandCard> createHand(Table table, int playerIndex) {
        return hands.get(playerIndex)
                    .stream()
                    .map(c -> new HandCard(c, table, playerIndex))
                    .collect(Collectors.toList());
    }

    public Hands rotate(HandRotationDirection direction) {
        Map<Integer, List<Card>> newHands = new HashMap<>(hands.size());
        for (int i = 0; i < nbPlayers; i++) {
            int newIndex = Math.floorMod(i + direction.getIndexOffset(), nbPlayers);
            newHands.put(newIndex, hands.get(i));
        }
        return new Hands(newHands, nbPlayers);
    }

    public boolean isEmpty() {
        return hands.values().stream().allMatch(List::isEmpty);
    }

    public boolean maxOneCardRemains() {
        return hands.values().stream().mapToInt(List::size).max().orElse(0) <= 1;
    }

    public List<Card> gatherAndClear() {
        List<Card> remainingCards = hands.values().stream().flatMap(List::stream).collect(Collectors.toList());
        hands.clear();
        return remainingCards;
    }

    class PlayerIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException {
        PlayerIndexOutOfBoundsException(int index) {
            super(index);
        }
    }
}
