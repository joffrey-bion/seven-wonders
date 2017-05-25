package org.luxons.sevenwonders.test.api;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.effects.SpecialAbility;

public class ApiBoard {

    private ApiWonder wonder;

    private int playerIndex;

    private List<ApiCard> playedCards;

    private ApiProduction production;

    private ApiProduction publicProduction;

    private ApiScience science;

    private ApiTradingRules tradingRules;

    private ApiMilitary military;

    private Set<SpecialAbility> specialAbilities;

    private Map<Integer, Boolean> consumedFreeCards;

    private Card copiedGuild;

    private int gold;

    private int pointsPer3Gold;

    public ApiWonder getWonder() {
        return wonder;
    }

    public void setWonder(ApiWonder wonder) {
        this.wonder = wonder;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public List<ApiCard> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<ApiCard> playedCards) {
        this.playedCards = playedCards;
    }

    public ApiProduction getProduction() {
        return production;
    }

    public void setProduction(ApiProduction production) {
        this.production = production;
    }

    public ApiProduction getPublicProduction() {
        return publicProduction;
    }

    public void setPublicProduction(ApiProduction publicProduction) {
        this.publicProduction = publicProduction;
    }

    public ApiScience getScience() {
        return science;
    }

    public void setScience(ApiScience science) {
        this.science = science;
    }

    public ApiTradingRules getTradingRules() {
        return tradingRules;
    }

    public void setTradingRules(ApiTradingRules tradingRules) {
        this.tradingRules = tradingRules;
    }

    public ApiMilitary getMilitary() {
        return military;
    }

    public void setMilitary(ApiMilitary military) {
        this.military = military;
    }

    public Set<SpecialAbility> getSpecialAbilities() {
        return specialAbilities;
    }

    public void setSpecialAbilities(Set<SpecialAbility> specialAbilities) {
        this.specialAbilities = specialAbilities;
    }

    public Map<Integer, Boolean> getConsumedFreeCards() {
        return consumedFreeCards;
    }

    public void setConsumedFreeCards(Map<Integer, Boolean> consumedFreeCards) {
        this.consumedFreeCards = consumedFreeCards;
    }

    public Card getCopiedGuild() {
        return copiedGuild;
    }

    public void setCopiedGuild(Card copiedGuild) {
        this.copiedGuild = copiedGuild;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getPointsPer3Gold() {
        return pointsPer3Gold;
    }

    public void setPointsPer3Gold(int pointsPer3Gold) {
        this.pointsPer3Gold = pointsPer3Gold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiBoard apiBoard = (ApiBoard) o;
        return playerIndex == apiBoard.playerIndex && gold == apiBoard.gold && pointsPer3Gold == apiBoard.pointsPer3Gold
                && Objects.equals(wonder, apiBoard.wonder) && Objects.equals(playedCards, apiBoard.playedCards)
                && Objects.equals(production, apiBoard.production) && Objects.equals(publicProduction,
                apiBoard.publicProduction) && Objects.equals(science, apiBoard.science) && Objects.equals(tradingRules,
                apiBoard.tradingRules) && Objects.equals(military, apiBoard.military) && Objects.equals(
                specialAbilities, apiBoard.specialAbilities) && Objects.equals(consumedFreeCards,
                apiBoard.consumedFreeCards) && Objects.equals(copiedGuild, apiBoard.copiedGuild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wonder, playerIndex, playedCards, production, publicProduction, science, tradingRules,
                military, specialAbilities, consumedFreeCards, copiedGuild, gold, pointsPer3Gold);
    }
}
