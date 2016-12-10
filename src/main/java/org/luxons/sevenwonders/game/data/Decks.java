package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.data.definitions.CardDefinition;
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition;

public class Decks {

    private Map<Integer, List<Card>> cardsPerAge = new HashMap<>();

    public Decks(DecksDefinition decksDefinition, Settings settings) {
        cardsPerAge.put(1, prepareStandardDeck(decksDefinition.getAge1(), settings));
        cardsPerAge.put(2, prepareStandardDeck(decksDefinition.getAge2(), settings));
        cardsPerAge.put(3, prepareAge3Deck(decksDefinition, settings));
    }

    public List<Card> getCards(int age) {
        return cardsPerAge.getOrDefault(age, Collections.emptyList());
    }

    private static List<Card> prepareStandardDeck(List<CardDefinition> defs, Settings settings) {
        List<Card> cards = createDeck(defs, settings);
        Collections.shuffle(cards, settings.getRandom());
        return cards;
    }

    private static List<Card> prepareAge3Deck(DecksDefinition decksDefinition, Settings settings) {
        List<Card> age3deck = createDeck(decksDefinition.getAge3(), settings);
        age3deck.addAll(createGuildCards(decksDefinition.getGuildCards(), settings));
        Collections.shuffle(age3deck, settings.getRandom());
        return age3deck;
    }

    private static List<Card> createDeck(List<CardDefinition> defs, Settings settings) {
        List<Card> cards = new ArrayList<>();
        for (CardDefinition def : defs) {
            for (int i = 0; i < def.getCountPerNbPlayer().get(settings.getNbPlayers()); i++) {
                cards.add(def.create(settings));
            }
        }
        return cards;
    }

    private static List<Card> createGuildCards(List<CardDefinition> defs, Settings settings) {
        List<Card> guild = defs.stream().map((def) -> def.create(settings)).collect(Collectors.toList());
        Collections.shuffle(guild, settings.getRandom());
        return guild.subList(0, settings.getNbPlayers());
    }
}
