package org.luxons.sevenwonders.model.boards

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.wonders.ApiWonder

@Serializable
data class Board(
    val playerIndex: Int,
    val wonder: ApiWonder,
    val production: Production,
    val publicProduction: Production,
    val science: Science,
    val military: Military,
    val playedCards: List<List<TableCard>>,
    val gold: Int,
    val bluePoints: Int,
    val canPlayAnyCardForFree: Boolean
)

@Serializable
data class Requirements(
    val gold: Int = 0,
    val resources: List<CountedResource> = emptyList()
)

@Serializable
data class Production(
    val fixedResources: List<CountedResource>,
    val alternativeResources: List<Set<ResourceType>>
)

@Serializable
data class Military(
    val nbShields: Int,
    val victoryPoints: Int,
    val totalPoints: Int,
    val nbDefeatTokens: Int
)

@Serializable
data class Science(
    val jokers: Int,
    val nbWheels: Int,
    val nbCompasses: Int,
    val nbTablets: Int
)
