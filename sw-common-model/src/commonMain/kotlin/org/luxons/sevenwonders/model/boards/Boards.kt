package org.luxons.sevenwonders.model.boards

import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.wonders.ApiWonder

data class Board(
    val playerIndex: Int,
    val wonder: ApiWonder,
    val production: Production,
    val publicProduction: Production,
    val science: Science,
    val military: Military,
    val playedCards: List<List<TableCard>>,
    val gold: Int
)

data class Requirements(
    val gold: Int = 0,
    val resources: List<CountedResource> = emptyList()
)

data class Production(
    val fixedResources: List<CountedResource>,
    val alternativeResources: Set<Set<ResourceType>>
)

data class Military(
    val nbShields: Int,
    val totalPoints: Int,
    val nbDefeatTokens: Int
)

data class Science(
    val jokers: Int,
    val nbWheels: Int,
    val nbCompasses: Int,
    val nbTablets: Int
)
