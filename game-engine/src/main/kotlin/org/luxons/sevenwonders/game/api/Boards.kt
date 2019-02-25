package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Military
import org.luxons.sevenwonders.game.boards.Science
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.wonders.WonderBuildability
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

internal fun InternalBoard.toApiBoard(player: Player, lastMove: Move?): Board = Board(
    playerIndex = playerIndex,
    wonder = wonder.toApiWonder(player, lastMove),
    production = production.toApiProduction(),
    publicProduction = publicProduction.toApiProduction(),
    science = science.toApiScience(),
    military = military.toApiMilitary(),
    playedCards = getPlayedCards().map { it.toTableCard(lastMove) },
    gold = gold
)

data class Wonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<WonderStage>,
    val image: String,
    val nbBuiltStages: Int,
    val buildability: WonderBuildability
)

internal fun InternalWonder.toApiWonder(player: Player, lastMove: Move?): Wonder = Wonder(
    name = name,
    initialResource = initialResource,
    stages = stages.map { it.toApiWonderStage(lastBuiltStage == it, lastMove) },
    image = image,
    nbBuiltStages = nbBuiltStages,
    buildability = computeBuildabilityBy(player)
)

data class WonderStage(
    val cardBack: CardBack?,
    val isBuilt: Boolean,
    val requirements: Requirements,
    val builtDuringLastMove: Boolean
)

internal fun InternalWonderStage.toApiWonderStage(
    isLastBuiltStage: Boolean,
    lastMove: Move?
): WonderStage =
    WonderStage(
        cardBack = cardBack,
        isBuilt = isBuilt,
        requirements = requirements,
        builtDuringLastMove = lastMove?.type == MoveType.UPGRADE_WONDER && isLastBuiltStage
    )

data class ApiProduction(
    val fixedResources: List<ApiCountedResource>,
    val alternativeResources: Set<Set<ResourceType>>
)

internal fun Production.toApiProduction(): ApiProduction = ApiProduction(
        fixedResources = getFixedResources().toCountedResourcesList(),
        alternativeResources = getAlternativeResources()
)

data class ApiCountedResource(
    val count: Int,
    val type: ResourceType
)

internal fun Resources.toCountedResourcesList(): List<ApiCountedResource> =
        quantities.map { (type, count) -> ApiCountedResource(count, type) }.sortedBy { it.type }

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
