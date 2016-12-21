package org.luxons.sevenwonders.game.data.definitions;

import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.Requirements;

public class CardDefinition implements Definition<Card> {

    private String name;

    private Color color;

    private Requirements requirements;

    private EffectsDefinition effect;

    private String chainParent;

    private List<String> chainChildren;

    private Map<Integer, Integer> countPerNbPlayer;

    private String image;

    @Override
    public Card create(Settings settings) {
        return new Card(name, color, requirements, effect.create(settings), chainParent, chainChildren, image);
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Map<Integer, Integer> getCountPerNbPlayer() {
        return countPerNbPlayer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
