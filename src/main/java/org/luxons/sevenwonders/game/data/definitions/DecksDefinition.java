package org.luxons.sevenwonders.game.data.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.Decks;

public class DecksDefinition implements Definition<Decks> {

    private List<CardDefinition> age1;

    private List<CardDefinition> age2;

    private List<CardDefinition> age3;

    private List<CardDefinition> guildCards;

    public List<CardDefinition> getAge1() {
        return age1;
    }

    public List<CardDefinition> getAge2() {
        return age2;
    }

    public List<CardDefinition> getAge3() {
        return age3;
    }

    public List<CardDefinition> getGuildCards() {
        return guildCards;
    }

    @Override
    public Decks create(Settings settings) {
        Map<Integer, List<Card>> cardsPerAge = new HashMap<>();
        cardsPerAge.put(1, prepareStandardDeck(age1, settings));
        cardsPerAge.put(2, prepareStandardDeck(age2, settings));
        cardsPerAge.put(3, prepareAge3Deck(settings));
        return new Decks(cardsPerAge);
    }

    private static List<Card> prepareStandardDeck(List<CardDefinition> defs, Settings settings) {
        List<Card> cards = createDeck(defs, settings);
        Collections.shuffle(cards, settings.getRandom());
        return cards;
    }

    private List<Card> prepareAge3Deck(Settings settings) {
        List<Card> age3deck = createDeck(age3, settings);
        age3deck.addAll(createGuildCards(guildCards, settings));
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
        return guild.subList(0, settings.getNbPlayers() + 2);
    }
}
