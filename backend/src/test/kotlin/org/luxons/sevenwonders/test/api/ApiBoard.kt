package org.luxons.sevenwonders.test.api

import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.effects.SpecialAbility
import java.util.Objects

class ApiBoard {

    var wonder: ApiWonder? = null

    var playerIndex: Int = 0

    var playedCards: List<ApiCard>? = null

    var production: ApiProduction? = null

    var publicProduction: ApiProduction? = null

    var science: ApiScience? = null

    var tradingRules: ApiTradingRules? = null

    var military: ApiMilitary? = null

    var specialAbilities: Set<SpecialAbility>? = null

    var consumedFreeCards: Map<Int, Boolean>? = null

    var copiedGuild: Card? = null

    var gold: Int = 0

    var pointsPer3Gold: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val apiBoard = other as ApiBoard?
        return (playerIndex == apiBoard!!.playerIndex && gold == apiBoard.gold && pointsPer3Gold == apiBoard.pointsPer3Gold && wonder == apiBoard.wonder && playedCards == apiBoard.playedCards && production == apiBoard.production && publicProduction == apiBoard.publicProduction && science == apiBoard.science && tradingRules == apiBoard.tradingRules && military == apiBoard.military && specialAbilities == apiBoard.specialAbilities && consumedFreeCards == apiBoard.consumedFreeCards && copiedGuild == apiBoard.copiedGuild)
    }

    override fun hashCode(): Int {
        return Objects.hash(
            wonder,
            playerIndex,
            playedCards,
            production,
            publicProduction,
            science,
            tradingRules,
            military,
            specialAbilities,
            consumedFreeCards,
            copiedGuild,
            gold,
            pointsPer3Gold
        )
    }
}
