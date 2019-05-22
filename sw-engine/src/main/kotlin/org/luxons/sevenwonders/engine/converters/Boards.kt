package org.luxons.sevenwonders.engine.converters

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Military
import org.luxons.sevenwonders.engine.boards.Science
import org.luxons.sevenwonders.engine.boards.ScienceType
import org.luxons.sevenwonders.engine.cards.Requirements
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.engine.resources.Resources
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.boards.ApiBoard
import org.luxons.sevenwonders.model.boards.ApiMilitary
import org.luxons.sevenwonders.model.boards.ApiProduction
import org.luxons.sevenwonders.model.boards.ApiRequirements
import org.luxons.sevenwonders.model.boards.ApiScience
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.wonders.ApiWonder
import org.luxons.sevenwonders.model.wonders.ApiWonderStage
import org.luxons.sevenwonders.engine.boards.Board as InternalBoard
import org.luxons.sevenwonders.engine.wonders.Wonder as InternalWonder
import org.luxons.sevenwonders.engine.wonders.WonderStage as InternalWonderStage

internal fun InternalBoard.toApiBoard(player: Player, lastMove: Move?): ApiBoard =
    ApiBoard(
        playerIndex = playerIndex,
        wonder = wonder.toApiWonder(player, lastMove),
        production = production.toApiProduction(),
        publicProduction = publicProduction.toApiProduction(),
        science = science.toApiScience(),
        military = military.toApiMilitary(),
        playedCards = getPlayedCards().map { it.toTableCard(lastMove) }.toColumns(),
        gold = gold
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

internal fun InternalWonder.toApiWonder(player: Player, lastMove: Move?): ApiWonder =
    ApiWonder(
        name = name,
        initialResource = initialResource,
        stages = stages.map { it.toApiWonderStage(lastBuiltStage == it, lastMove) },
        image = image,
        nbBuiltStages = nbBuiltStages,
        buildability = computeBuildabilityBy(player)
    )

internal fun InternalWonderStage.toApiWonderStage(
    isLastBuiltStage: Boolean,
    lastMove: Move?
): ApiWonderStage = ApiWonderStage(
    cardBack = cardBack,
    isBuilt = isBuilt,
    requirements = requirements.toApiRequirements(),
    builtDuringLastMove = lastMove?.type == MoveType.UPGRADE_WONDER && isLastBuiltStage
)

internal fun Production.toApiProduction(): ApiProduction =
    ApiProduction(
        fixedResources = getFixedResources().toCountedResourcesList(), alternativeResources = getAlternativeResources()
    )

internal fun Requirements.toApiRequirements(): ApiRequirements =
    ApiRequirements(
        gold = gold, resources = resources.toCountedResourcesList()
    )

internal fun Resources.toCountedResourcesList(): List<CountedResource> =
        quantities.filterValues { it > 0 }
            .map { (type, count) -> CountedResource(count, type) }
            .sortedBy { it.type }

internal fun Military.toApiMilitary(): ApiMilitary =
    ApiMilitary(nbShields, totalPoints, nbDefeatTokens)

internal fun Science.toApiScience(): ApiScience =
    ApiScience(
        jokers = jokers,
        nbWheels = getQuantity(ScienceType.WHEEL),
        nbCompasses = getQuantity(ScienceType.COMPASS),
        nbTablets = getQuantity(ScienceType.TABLET)
    )
