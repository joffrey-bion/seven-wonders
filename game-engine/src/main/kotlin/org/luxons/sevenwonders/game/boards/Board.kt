package org.luxons.sevenwonders.game.boards

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.data.Age
import org.luxons.sevenwonders.game.effects.SpecialAbility
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.TradingRules
import org.luxons.sevenwonders.game.scoring.PlayerScore
import org.luxons.sevenwonders.game.scoring.ScoreCategory
import org.luxons.sevenwonders.game.wonders.Wonder

class Board(val wonder: Wonder, val playerIndex: Int, settings: Settings) {

    val production = Production()
    val publicProduction = Production()
    val science = Science()
    val tradingRules: TradingRules = TradingRules(settings.defaultTradingCost)
    val military: Military = Military(settings.lostPointsPerDefeat, settings.wonPointsPerVictoryPerAge)
    private val pointsPer3Gold: Int = settings.pointsPer3Gold

    private val playedCards: MutableList<Card> = arrayListOf()
    private val specialAbilities: MutableSet<SpecialAbility> = hashSetOf()
    private val consumedFreeCards: MutableMap<Age, Boolean> = mutableMapOf()

    var gold: Int = settings.initialGold

    var copiedGuild: Card? = null
        set(copiedGuild) {
            if (copiedGuild!!.color !== Color.PURPLE) {
                throw IllegalArgumentException("The given card '$copiedGuild' is not a Guild card")
            }
            field = copiedGuild
        }

    init {
        this.production.addFixedResource(wonder.initialResource, 1)
        this.publicProduction.addFixedResource(wonder.initialResource, 1)
    }

    fun getPlayedCards(): List<Card> = playedCards

    fun addCard(card: Card) {
        playedCards.add(card)
    }

    internal fun getNbCardsOfColor(colorFilter: List<Color>): Int = playedCards.count { colorFilter.contains(it.color) }

    fun isPlayed(cardName: String): Boolean = playedCards.count { it.name == cardName } > 0

    fun addGold(amount: Int) {
        this.gold += amount
    }

    fun removeGold(amount: Int) {
        if (gold < amount) {
            throw InsufficientFundsException(gold, amount)
        }
        this.gold -= amount
    }

    fun addSpecial(specialAbility: SpecialAbility) {
        specialAbilities.add(specialAbility)
    }

    fun hasSpecial(specialAbility: SpecialAbility): Boolean = specialAbilities.contains(specialAbility)

    fun canPlayFreeCard(age: Age): Boolean =
        hasSpecial(SpecialAbility.ONE_FREE_PER_AGE) && !consumedFreeCards.getOrDefault(age, false)

    fun consumeFreeCard(age: Age) {
        consumedFreeCards[age] = true
    }

    fun computePoints(table: Table): PlayerScore {
        val score = PlayerScore(gold)
        score.put(ScoreCategory.CIVIL, computePointsForCards(table, Color.BLUE))
        score.put(ScoreCategory.MILITARY, military.totalPoints)
        score.put(ScoreCategory.SCIENCE, science.computePoints())
        score.put(ScoreCategory.TRADE, computePointsForCards(table, Color.YELLOW))
        score.put(ScoreCategory.GUILD, computePointsForCards(table, Color.PURPLE))
        score.put(ScoreCategory.WONDER, wonder.computePoints(table, playerIndex))
        score.put(ScoreCategory.GOLD, computeGoldPoints())
        return score
    }

    private fun computePointsForCards(table: Table, color: Color): Int = playedCards
        .filter { (_, color1) -> color1 === color }
        .flatMap { (_, _, _, effects) -> effects }
        .map { e -> e.computePoints(table, playerIndex) }
        .sum()

    private fun computeGoldPoints(): Int = gold / 3 * pointsPer3Gold

    internal class InsufficientFundsException(current: Int, required: Int) :
        RuntimeException(String.format("Current balance is %d gold, but %d are required", current, required))
}
