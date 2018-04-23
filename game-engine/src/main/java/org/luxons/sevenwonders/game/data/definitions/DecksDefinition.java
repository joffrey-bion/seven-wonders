package org.luxons.sevenwonders.game.data.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.cards.Decks;

@SuppressWarnings("unused,MismatchedQueryAndUpdateOfCollection") // the fields are injected by Gson
public class DecksDefinition implements Definition<Decks> {

    private List<CardDefinition> age1;

    private List<CardDefinition> age2;

    private List<CardDefinition> age3;

    private String age1Back;

    private String age2Back;

    private String age3Back;

    private List<CardDefinition> guildCards;

    @Override
    public Decks create(Settings settings) {
        Map<Integer, List<Card>> cardsPerAge = new HashMap<>();
        cardsPerAge.put(1, prepareStandardDeck(age1, settings, age1Back));
        cardsPerAge.put(2, prepareStandardDeck(age2, settings, age2Back));
        cardsPerAge.put(3, prepareAge3Deck(settings));
        return new Decks(cardsPerAge);
    }

    private static List<Card> prepareStandardDeck(List<CardDefinition> defs, Settings settings, String backImage) {
        CardBack back = new CardBack(backImage);
        List<Card> cards = createDeck(defs, settings, back);
        Collections.shuffle(cards, settings.getRandom());
        return cards;
    }

    private List<Card> prepareAge3Deck(Settings settings) {
        CardBack back = new CardBack(age3Back);
        List<Card> age3deck = createDeck(age3, settings, back);
        age3deck.addAll(createGuildCards(settings, back));
        Collections.shuffle(age3deck, settings.getRandom());
        return age3deck;
    }

    private static List<Card> createDeck(List<CardDefinition> defs, Settings settings, CardBack back) {
        List<Card> cards = new ArrayList<>();
        for (CardDefinition def : defs) {
            for (int i = 0; i < def.getCountPerNbPlayer().get(settings.getNbPlayers()); i++) {
                Card card = def.create(settings);
                card.setBack(back);
                cards.add(card);
            }
        }
        return cards;
    }

    private List<Card> createGuildCards(Settings settings, CardBack back) {
        List<Card> guild = guildCards.stream()
                                     .map((def) -> def.create(settings))
                                     .peek(c -> c.setBack(back))
                                     .collect(Collectors.toList());
        Collections.shuffle(guild, settings.getRandom());
        return guild.subList(0, settings.getNbPlayers() + 2);
    }
}
