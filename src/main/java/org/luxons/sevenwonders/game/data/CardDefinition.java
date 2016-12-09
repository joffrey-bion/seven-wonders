package org.luxons.sevenwonders.game.data;

import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.Requirements;

public class CardDefinition {

    private String name;

    private Color color;

    private String effect;

    private Requirements requirements;

    private String chainParent;

    private List<String> chainChildren;

    private Map<Integer, Integer> countPerNbPlayer;

    public Card createCard() {
        return new Card(name, color, requirements, EffectParser.parse(effect));
    }

    private Requirements createRequirements() {
        Requirements req = new Requirements();

        return req;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public String getEffect() {
        return effect;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public String getChainParent() {
        return chainParent;
    }

    public List<String> getChainChildren() {
        return chainChildren;
    }

    public Map<Integer, Integer> getCountPerNbPlayer() {
        return countPerNbPlayer;
    }
}
