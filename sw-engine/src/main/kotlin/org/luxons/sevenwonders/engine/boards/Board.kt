package org.luxons.sevenwonders.engine.boards

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.Settings
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.effects.SpecialAbility
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.engine.resources.TradingRules
import org.luxons.sevenwonders.engine.resources.mutableResourcesOf
import org.luxons.sevenwonders.engine.wonders.Wonder
import org.luxons.sevenwonders.model.Age
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.model.score.PlayerScore
import org.luxons.sevenwonders.model.score.ScoreCategory

internal class Board(val wonder: Wonder, val playerIndex: Int, settings: Settings) {

    val production = Production(mutableResourcesOf(wonder.initialResource))
    val publicProduction = Production(mutableResourcesOf(wonder.initialResource))
    val science = Science()
    val military: Military = Military(settings.lostPointsPerDefeat, settings.wonPointsPerVictoryPerAge)
    val tradingRules: TradingRules = TradingRules(settings.defaultTradingCost)

    private val pointsPer3Gold: Int = settings.pointsPer3Gold

    private val playedCards: MutableList<Card> = mutableListOf()
    private val specialAbilities: MutableSet<SpecialAbility> = hashSetOf()
    private val consumedFreeCards: MutableMap<Age, Boolean> = mutableMapOf()

    var gold: Int = settings.initialGold
        private set

    var copiedGuild: Card? = null
        internal set(copiedGuild) {
            if (copiedGuild!!.color !== Color.PURPLE) {
                throw IllegalArgumentException("The given card '$copiedGuild' is not a Guild card")
            }
            field = copiedGuild
        }

    fun getPlayedCards(): List<Card> = playedCards

    fun addCard(card: Card) {
        playedCards.add(card)
    }

    fun getNbCardsOfColor(colorFilter: List<Color>): Int = playedCards.count { colorFilter.contains(it.color) }

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

    fun removeSpecial(specialAbility: SpecialAbility) {
        specialAbilities.remove(specialAbility)
    }

    fun hasSpecial(specialAbility: SpecialAbility): Boolean = specialAbilities.contains(specialAbility)

    fun canPlayFreeCard(age: Age): Boolean =
        hasSpecial(SpecialAbility.ONE_FREE_PER_AGE) && !consumedFreeCards.getOrDefault(age, false)

    fun consumeFreeCard(age: Age) {
        consumedFreeCards[age] = true
    }

    fun computeScore(player: Player): PlayerScore = PlayerScore(
        playerIndex = playerIndex,
        boardGold = gold,
        pointsByCategory = mapOf(
            ScoreCategory.CIVIL to computePointsForCards(player, Color.BLUE),
            ScoreCategory.MILITARY to military.totalPoints,
            ScoreCategory.SCIENCE to science.computePoints(),
            ScoreCategory.TRADE to computePointsForCards(player, Color.YELLOW),
            ScoreCategory.GUILD to computePointsForCards(player, Color.PURPLE),
            ScoreCategory.WONDER to wonder.computePoints(player),
            ScoreCategory.GOLD to computeGoldPoints()
        )
    )

    private fun computePointsForCards(player: Player, color: Color): Int =
        playedCards.filter { it.color === color }
            .flatMap { it.effects }
            .sumBy { it.computePoints(player) }

    private fun computeGoldPoints(): Int = gold / 3 * pointsPer3Gold

    internal class InsufficientFundsException(current: Int, required: Int) :
        IllegalStateException("Current balance is $current gold, but $required are required")
}
