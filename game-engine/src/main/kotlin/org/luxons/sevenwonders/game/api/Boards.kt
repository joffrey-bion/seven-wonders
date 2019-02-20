package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.boards.Military
import org.luxons.sevenwonders.game.boards.Science
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.boards.Board as InternalBoard
import org.luxons.sevenwonders.game.wonders.Wonder as InternalWonder
import org.luxons.sevenwonders.game.wonders.WonderStage as InternalWonderStage

data class Board(
    val playerIndex: Int,
    val wonder: Wonder,
    val production: ApiProduction,
    val publicProduction: ApiProduction,
    val science: ApiScience,
    val military: ApiMilitary,
    val playedCards: List<TableCard>,
    val gold: Int
)

internal fun InternalBoard.toApiBoard(): Board = Board(
    playerIndex = playerIndex,
    wonder = wonder.toApiWonder(),
    production = production.toApiProduction(),
    publicProduction = publicProduction.toApiProduction(),
    science = science.toApiScience(),
    military = military.toApiMilitary(),
    playedCards = getPlayedCards().map { it.toTableCard() },
    gold = gold
)

data class Wonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<WonderStage>,
    val image: String,
    val nbBuiltStages: Int
)

internal fun InternalWonder.toApiWonder(): Wonder = Wonder(
    name = name,
    initialResource = initialResource,
    stages = stages.map { it.toApiWonderStage() },
    image = image,
    nbBuiltStages = nbBuiltStages
)

data class WonderStage(
    val cardBack: CardBack?,
    val isBuilt: Boolean
)

internal fun InternalWonderStage.toApiWonderStage(): WonderStage = WonderStage(
    cardBack = cardBack,
    isBuilt = isBuilt
)

data class ApiProduction(
    val fixedResources: Resources,
    val aternativeResources: Set<Set<ResourceType>>
)

internal fun Production.toApiProduction(): ApiProduction = ApiProduction(getFixedResources(), getAlternativeResources())

data class ApiMilitary(
    var nbShields: Int,
    var totalPoints: Int,
    var nbDefeatTokens: Int
)

internal fun Military.toApiMilitary(): ApiMilitary = ApiMilitary(nbShields, totalPoints, nbDefeatTokens)

data class ApiScience(
    var jokers: Int,
    var nbWheels: Int,
    var nbCompasses: Int,
    var nbTablets: Int
)

internal fun Science.toApiScience(): ApiScience = ApiScience(
    jokers = jokers,
    nbWheels = getQuantity(ScienceType.WHEEL),
    nbCompasses = getQuantity(ScienceType.COMPASS),
    nbTablets = getQuantity(ScienceType.TABLET)
)
