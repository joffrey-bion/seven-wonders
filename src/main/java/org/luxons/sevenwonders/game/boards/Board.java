package org.luxons.sevenwonders.game.boards;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.luxons.sevenwonders.game.Player;
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

    private final Player player;

    private final List<Card> playedCards = new ArrayList<>();

    private final Production production = new Production();

    private final Science science = new Science();

    private final TradingRules tradingRules;

    private final Military military;

    private final Set<SpecialAbility> specialAbilities = EnumSet.noneOf(SpecialAbility.class);



    private int gold;

    public Board(Wonder wonder, Player player, Settings settings) {
        this.wonder = wonder;
        this.player = player;
        this.gold = settings.getInitialGold();
        this.tradingRules = new TradingRules(settings.getDefaultTradingCost());
        this.military = new Military(settings);
        production.addFixedResource(wonder.getInitialResource(), 1);
    }

    public Wonder getWonder() {
        return wonder;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void addCard(Card card) {
        playedCards.add(card);
    }

    public int getNbCardsOfColor(List<Color> colorFilter) {
        return (int) playedCards.stream().filter(c -> colorFilter.contains(c.getColor())).count();
    }

    public boolean isPlayed(String cardName) {
        return getPlayedCards().stream().map(Card::getName).filter(name -> name.equals(cardName)).count() > 0;
    }

    public Production getProduction() {
        return production;
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

    public PlayerScore computePoints(Table table) {
        PlayerScore score = new PlayerScore(player, gold);
        score.put(ScoreCategory.CIVIL, computePointsForCards(table, Color.BLUE));
        score.put(ScoreCategory.MILITARY, military.getTotalPoints());
        score.put(ScoreCategory.SCIENCE, science.computePoints());
        score.put(ScoreCategory.TRADE, computePointsForCards(table, Color.YELLOW));
        score.put(ScoreCategory.GUILD, computePointsForCards(table, Color.PURPLE));
        score.put(ScoreCategory.WONDER, wonder.computePoints(table, player.getIndex()));
        return score;
    }

    private int computePointsForCards(Table table, Color color) {
        return playedCards.stream()
                .filter(c -> c.getColor() == color)
                .flatMap(c -> c.getEffects().stream())
                .mapToInt(e -> e.computePoints(table, player.getIndex()))
                .sum();
    }

    static class InsufficientFundsException extends RuntimeException {
        InsufficientFundsException(int current, int required) {
            super(String.format("Current balance is %d gold, but %d are required", current, required));
        }
    }
}
