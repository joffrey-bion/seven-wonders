package org.luxons.sevenwonders.model.wonders

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.boards.Requirements
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.PlayabilityLevel
import org.luxons.sevenwonders.model.resources.ResourceTransactionOptions
import org.luxons.sevenwonders.model.resources.ResourceType
import kotlin.random.Random

typealias WonderName = String

@Serializable
data class PreGameWonder(
    val name: WonderName,
    val images: Map<WonderSide, String>,
)

@Serializable
data class AssignedWonder(
    val name: WonderName,
    val side: WonderSide,
)

enum class WonderSide {
    A,
    B
}

fun List<PreGameWonder>.deal(nbPlayers: Int, random: Random = Random): List<AssignedWonder> =
    shuffled(random).take(nbPlayers).map { it.withRandomSide(random) }

fun PreGameWonder.withRandomSide(random: Random = Random): AssignedWonder = withSide(WonderSide.entries.random(random))

fun PreGameWonder.withSide(side: WonderSide): AssignedWonder = AssignedWonder(name, side)

@Serializable
data class ApiWonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<ApiWonderStage>,
    val image: String,
    val nbBuiltStages: Int,
    val buildability: WonderBuildability,
)

@Serializable
data class ApiWonderStage(
    val cardBack: CardBack?,
    val isBuilt: Boolean,
    val requirements: Requirements,
    val builtDuringLastMove: Boolean,
)

@Serializable
data class WonderBuildability(
    val isBuildable: Boolean,
    val minPrice: Int,
    val transactionsOptions: ResourceTransactionOptions,
    val playabilityLevel: PlayabilityLevel,
) {
    val isFree: Boolean = minPrice == 0
}
