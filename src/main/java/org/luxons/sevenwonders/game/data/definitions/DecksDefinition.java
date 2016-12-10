package org.luxons.sevenwonders.game.data.definitions;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.data.Decks;

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
        return new Decks(this, settings);
    }
}
