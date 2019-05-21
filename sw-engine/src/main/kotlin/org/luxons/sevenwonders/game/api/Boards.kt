package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.api.boards.ApiBoard
import org.luxons.sevenwonders.game.api.boards.ApiMilitary
import org.luxons.sevenwonders.game.api.boards.ApiProduction
import org.luxons.sevenwonders.game.api.boards.ApiRequirements
import org.luxons.sevenwonders.game.api.boards.ApiScience
import org.luxons.sevenwonders.game.boards.Military
import org.luxons.sevenwonders.game.boards.Science
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.api.cards.TableCard
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.api.resources.CountedResource
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.api.wonders.ApiWonder
import org.luxons.sevenwonders.game.api.wonders.ApiWonderStage
import org.luxons.sevenwonders.game.boards.Board as InternalBoard
import org.luxons.sevenwonders.game.wonders.Wonder as InternalWonder
import org.luxons.sevenwonders.game.wonders.WonderStage as InternalWonderStage

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
