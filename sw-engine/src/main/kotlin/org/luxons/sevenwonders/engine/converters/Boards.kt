package org.luxons.sevenwonders.engine.converters

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.ScienceType
import org.luxons.sevenwonders.engine.effects.RawPointsIncrease
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.engine.resources.Resources
import org.luxons.sevenwonders.model.Age
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.wonders.ApiWonder
import org.luxons.sevenwonders.model.wonders.ApiWonderStage
import org.luxons.sevenwonders.engine.boards.Board as InternalBoard
import org.luxons.sevenwonders.engine.boards.Military as InternalMilitary
import org.luxons.sevenwonders.engine.boards.Science as InternalScience
import org.luxons.sevenwonders.engine.cards.Requirements as InternalRequirements
import org.luxons.sevenwonders.engine.resources.Production as InternalProduction
import org.luxons.sevenwonders.engine.wonders.Wonder as InternalWonder
import org.luxons.sevenwonders.engine.wonders.WonderStage as InternalWonderStage
import org.luxons.sevenwonders.model.boards.Board as ApiBoard
import org.luxons.sevenwonders.model.boards.Military as ApiMilitary
import org.luxons.sevenwonders.model.boards.Production as ApiProduction
import org.luxons.sevenwonders.model.boards.Requirements as ApiRequirements
import org.luxons.sevenwonders.model.boards.Science as ApiScience

internal fun InternalBoard.toApiBoard(player: Player, lastMove: Move?, currentAge: Age): ApiBoard = ApiBoard(
    playerIndex = playerIndex,
    wonder = wonder.toApiWonder(player, lastMove),
    production = production.toApiProduction(),
    publicProduction = publicProduction.toApiProduction(),
    science = science.toApiScience(),
    military = military.toApiMilitary(),
    playedCards = getPlayedCards().map { it.toTableCard(lastMove) }.toColumns(),
    gold = gold,
    bluePoints = getPlayedCards().filter { it.color == Color.BLUE }
        .flatMap { it.effects.filterIsInstance<RawPointsIncrease>() }
        .sumOf { it.points },
    canPlayAnyCardForFree = canPlayFreeCard(currentAge),
)

internal fun List<TableCard>.toColumns(): List<List<TableCard>> {
    val cardsByColor = this.groupBy { it.color }
    val (resourceCardsCols, otherCols) = cardsByColor.values.partition { it[0].color.isResource }
    val resourceCardsCol = resourceCardsCols.flatten()
    val otherColsSorted = otherCols.sortedBy { it[0].color }
    if (resourceCardsCol.isEmpty()) {
        return otherColsSorted // we want only non-empty columns
    }
    return listOf(resourceCardsCol) + otherColsSorted
}

internal fun InternalWonder.toApiWonder(player: Player, lastMove: Move?): ApiWonder = ApiWonder(
    name = name,
    initialResource = initialResource,
    stages = stages.map { it.toApiWonderStage(lastBuiltStage == it, lastMove) },
    image = image,
    nbBuiltStages = nbBuiltStages,
    buildability = computeBuildabilityBy(player),
)

internal fun InternalWonderStage.toApiWonderStage(isLastBuiltStage: Boolean, lastMove: Move?): ApiWonderStage =
    ApiWonderStage(
        cardBack = cardBack,
        isBuilt = isBuilt,
        requirements = requirements.toApiRequirements(),
        builtDuringLastMove = lastMove?.type == MoveType.UPGRADE_WONDER && isLastBuiltStage,
    )

internal fun InternalProduction.toApiProduction(): ApiProduction = ApiProduction(
    fixedResources = getFixedResources().toCountedResourcesList(),
    alternativeResources = getAlternativeResources().sortedBy { it.size },
)

internal fun InternalRequirements.toApiRequirements(): ApiRequirements =
    ApiRequirements(gold = gold, resources = resources.toCountedResourcesList())

internal fun Resources.toCountedResourcesList(): List<CountedResource> = //
    quantities.filterValues { it > 0 } //
        .map { (type, count) -> CountedResource(count, type) } //
        .sortedBy { it.type }

internal fun InternalMilitary.toApiMilitary(): ApiMilitary =
    ApiMilitary(nbShields, victoryPoints, totalPoints, nbDefeatTokens)

internal fun InternalScience.toApiScience(): ApiScience = ApiScience(
    jokers = jokers,
    nbWheels = getQuantity(ScienceType.WHEEL),
    nbCompasses = getQuantity(ScienceType.COMPASS),
    nbTablets = getQuantity(ScienceType.TABLET),
)
