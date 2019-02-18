package org.luxons.sevenwonders.game.boards

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.data.Age
import org.luxons.sevenwonders.game.effects.SpecialAbility
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.TradingRules
import org.luxons.sevenwonders.game.score.PlayerScore
import org.luxons.sevenwonders.game.score.ScoreCategory
import org.luxons.sevenwonders.game.wonders.Wonder

class Board internal constructor(val wonder: Wonder, val playerIndex: Int, settings: Settings) {

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
        private set

    var copiedGuild: Card? = null
        internal set(copiedGuild) {
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

    internal fun addCard(card: Card) {
        playedCards.add(card)
    }

    internal fun getNbCardsOfColor(colorFilter: List<Color>): Int = playedCards.count { colorFilter.contains(it.color) }

    fun isPlayed(cardName: String): Boolean = playedCards.count { it.name == cardName } > 0

    internal fun addGold(amount: Int) {
        this.gold += amount
    }

    internal fun removeGold(amount: Int) {
        if (gold < amount) {
            throw InsufficientFundsException(gold, amount)
        }
        this.gold -= amount
    }

    internal fun addSpecial(specialAbility: SpecialAbility) {
        specialAbilities.add(specialAbility)
    }

    fun hasSpecial(specialAbility: SpecialAbility): Boolean = specialAbilities.contains(specialAbility)

    fun canPlayFreeCard(age: Age): Boolean =
        hasSpecial(SpecialAbility.ONE_FREE_PER_AGE) && !consumedFreeCards.getOrDefault(age, false)

    fun consumeFreeCard(age: Age) {
        consumedFreeCards[age] = true
    }

    internal fun computeScore(player: Player): PlayerScore = PlayerScore(
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
            .map { it.computePoints(player) }
            .sum()

    private fun computeGoldPoints(): Int = gold / 3 * pointsPer3Gold

    internal class InsufficientFundsException(current: Int, required: Int) :
        IllegalStateException("Current balance is $current gold, but $required are required")
}
