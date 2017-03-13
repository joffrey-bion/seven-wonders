package org.luxons.sevenwonders.game.boards;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.effects.SpecialAbility;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.TradingRules;
import org.luxons.sevenwonders.game.scoring.PlayerScore;
import org.luxons.sevenwonders.game.scoring.ScoreCategory;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class Board {

    private final Wonder wonder;

    private final int playerIndex;

    private final List<Card> playedCards = new ArrayList<>();

    private final Production production = new Production();

    private final Production publicProduction = new Production();

    private final Science science = new Science();

    private final TradingRules tradingRules;

    private final Military military;

    private final Set<SpecialAbility> specialAbilities = EnumSet.noneOf(SpecialAbility.class);

    private Map<Integer, Boolean> consumedFreeCards = new HashMap<>();

    private Card copiedGuild;

    private int gold;

    private int pointsPer3Gold;

    public Board(Wonder wonder, int playerIndex, Settings settings) {
        this.wonder = wonder;
        this.playerIndex = playerIndex;
        this.gold = settings.getInitialGold();
        this.tradingRules = new TradingRules(settings.getDefaultTradingCost());
        this.military = new Military(settings.getLostPointsPerDefeat(), settings.getWonPointsPerVictoryPerAge());
        this.pointsPer3Gold = settings.getPointsPer3Gold();
        this.production.addFixedResource(wonder.getInitialResource(), 1);
        this.publicProduction.addFixedResource(wonder.getInitialResource(), 1);
    }

    public Wonder getWonder() {
        return wonder;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void addCard(Card card) {
        playedCards.add(card);
    }

    int getNbCardsOfColor(List<Color> colorFilter) {
        return (int) playedCards.stream().filter(c -> colorFilter.contains(c.getColor())).count();
    }

    public boolean isPlayed(String cardName) {
        return getPlayedCards().stream().map(Card::getName).filter(name -> name.equals(cardName)).count() > 0;
    }

    public Production getProduction() {
        return production;
    }

    public Production getPublicProduction() {
        return publicProduction;
    }

    public TradingRules getTradingRules() {
        return tradingRules;
    }

    public Science getScience() {
        return science;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int amount) {
        this.gold = amount;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void removeGold(int amount) {
        if (gold < amount) {
            throw new InsufficientFundsException(gold, amount);
        }
        this.gold -= amount;
    }

    public Military getMilitary() {
        return military;
    }

    public void addSpecial(SpecialAbility specialAbility) {
        specialAbilities.add(specialAbility);
    }

    public boolean hasSpecial(SpecialAbility specialAbility) {
        return specialAbilities.contains(specialAbility);
    }

    public boolean canPlayFreeCard(int age) {
        return hasSpecial(SpecialAbility.ONE_FREE_PER_AGE) && !consumedFreeCards.getOrDefault(age, false);
    }

    public void consumeFreeCard(int age) {
        consumedFreeCards.put(age, true);
    }

    public void setCopiedGuild(Card copiedGuild) {
        if (copiedGuild.getColor() != Color.PURPLE) {
            throw new IllegalArgumentException("The given card '" + copiedGuild + "' is not a Guild card");
        }
        this.copiedGuild = copiedGuild;
    }

    public Card getCopiedGuild() {
        return copiedGuild;
    }

    public PlayerScore computePoints(Table table) {
        PlayerScore score = new PlayerScore(gold);
        score.put(ScoreCategory.CIVIL, computePointsForCards(table, Color.BLUE));
        score.put(ScoreCategory.MILITARY, military.getTotalPoints());
        score.put(ScoreCategory.SCIENCE, science.computePoints());
        score.put(ScoreCategory.TRADE, computePointsForCards(table, Color.YELLOW));
        score.put(ScoreCategory.GUILD, computePointsForCards(table, Color.PURPLE));
        score.put(ScoreCategory.WONDER, wonder.computePoints(table, playerIndex));
        score.put(ScoreCategory.GOLD, computeGoldPoints());
        return score;
    }

    private int computePointsForCards(Table table, Color color) {
        return playedCards.stream()
                          .filter(c -> c.getColor() == color)
                          .flatMap(c -> c.getEffects().stream())
                          .mapToInt(e -> e.computePoints(table, playerIndex))
                          .sum();
    }

    private int computeGoldPoints() {
        return gold / 3 * pointsPer3Gold;
    }

    static class InsufficientFundsException extends RuntimeException {
        InsufficientFundsException(int current, int required) {
            super(String.format("Current balance is %d gold, but %d are required", current, required));
        }
    }
}
