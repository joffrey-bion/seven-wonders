package org.luxons.sevenwonders.game.test

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.Science
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.effects.ScienceProgress
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.Provider
import org.luxons.sevenwonders.game.resources.ResourceTransaction
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.wonders.Wonder
import org.luxons.sevenwonders.game.wonders.WonderStage
import java.util.Arrays

private const val SEED: Long = 42

@JvmOverloads
fun testCustomizableSettings(initialGold: Int = 0): CustomizableSettings {
    return CustomizableSettings(randomSeedForTests = SEED).copy(initialGold = initialGold)
}

fun testSettings(nbPlayers: Int): Settings {
    return Settings(nbPlayers, testCustomizableSettings())
}

fun testTable(nbPlayers: Int): Table {
    return testTable(testSettings(nbPlayers))
}

fun testTable(settings: Settings): Table {
    return Table(testBoards(settings.nbPlayers, settings))
}

private fun testBoards(count: Int, settings: Settings): List<Board> {
    val boards = ArrayList<Board>(count)
    for (i in 0 until count) {
        boards.add(testBoard(ResourceType.WOOD, settings))
    }
    return boards
}

private fun testBoard(initialResource: ResourceType, settings: Settings): Board {
    val wonder = testWonder(initialResource)
    return Board(wonder, 0, settings)
}

fun testBoard(initialResource: ResourceType): Board {
    return testBoard(initialResource, testSettings(5))
}

private fun testBoard(initialResource: ResourceType, vararg production: ResourceType): Board {
    val board = testBoard(initialResource)
    board.production.addAll(fixedProduction(*production))
    return board
}

fun testBoard(initialResource: ResourceType, gold: Int, vararg production: ResourceType): Board {
    val board = testBoard(initialResource, *production)
    board.gold = gold
    return board
}

@JvmOverloads
fun testWonder(initialResource: ResourceType = ResourceType.WOOD): Wonder {
    val stage1 = createWonderStage()
    val stage2 = createWonderStage()
    val stage3 = createWonderStage()
    return Wonder("Test Wonder " + initialResource.symbol!!, initialResource, stage1, stage2, stage3)
}

private fun createWonderStage(vararg effects: Effect): WonderStage {
    return WonderStage(Requirements(), Arrays.asList(*effects))
}

fun fixedProduction(vararg producedTypes: ResourceType): Production {
    val production = Production()
    val fixedProducedResources = production.fixedResources
    fixedProducedResources.addAll(createResources(*producedTypes))
    return production
}

fun createResources(vararg types: ResourceType): Resources {
    val resources = Resources()
    for (producedType in types) {
        resources.add(producedType, 1)
    }
    return resources
}

fun createTransactions(provider: Provider, vararg resources: ResourceType): ResourceTransactions {
    val transaction = createTransaction(provider, *resources)
    return ResourceTransactions(listOf(transaction))
}

fun createTransactions(vararg transactions: ResourceTransaction): ResourceTransactions {
    return ResourceTransactions(Arrays.asList(*transactions))
}

fun createTransaction(provider: Provider, vararg resources: ResourceType): ResourceTransaction {
    return ResourceTransaction(provider, createResources(*resources))
}

fun createRequirements(vararg types: ResourceType): Requirements {
    val resources = createResources(*types)
    val requirements = Requirements()
    requirements.resources = resources
    return requirements
}

fun createSampleCards(fromIndex: Int, nbCards: Int): List<Card> {
    val sampleCards = ArrayList<Card>()
    for (i in fromIndex until fromIndex + nbCards) {
        sampleCards.add(testCard(i, Color.BLUE))
    }
    return sampleCards
}

fun testCard(name: String): Card {
    return testCard(name, Color.BLUE)
}

fun testCard(color: Color): Card {
    return testCard("Test Card", color)
}

fun testCard(color: Color, effect: Effect): Card {
    return testCard("Test Card", color, effect)
}

private fun testCard(num: Int, color: Color): Card {
    return testCard("Test Card $num", color)
}

fun createGuildCards(count: Int): List<Card> {
    return IntRange(0, count).map { createGuildCard(it) }
}

fun createGuildCard(num: Int, vararg effects: Effect): Card {
    return testCard("Test Guild $num", Color.PURPLE, *effects)
}

private fun testCard(name: String, color: Color, vararg effects: Effect): Card {
    val card = Card(name, color, Requirements(), Arrays.asList(*effects), null, null, "path/to/card/image")
    card.back = createCardBack()
    return card
}

private fun createCardBack(): CardBack {
    return CardBack("image-III")
}

fun addCards(board: Board, nbCardsOfColor: Int, nbOtherCards: Int, color: Color) {
    addCards(board, nbCardsOfColor, color)
    val otherColor = getDifferentColorFrom(color)
    addCards(board, nbOtherCards, otherColor)
}

fun addCards(board: Board, nbCards: Int, color: Color) {
    for (i in 0 until nbCards) {
        board.addCard(testCard(i, color))
    }
}

fun getDifferentColorFrom(vararg colors: Color): Color {
    val forbiddenColors = Arrays.asList(*colors)
    for (color in Color.values()) {
        if (!forbiddenColors.contains(color)) {
            return color
        }
    }
    throw IllegalArgumentException("All colors are forbidden!")
}

fun createScienceProgress(compasses: Int, wheels: Int, tablets: Int, jokers: Int): ScienceProgress {
    val progress = ScienceProgress()
    progress.science = createScience(compasses, wheels, tablets, jokers)
    return progress
}

fun createScience(compasses: Int, wheels: Int, tablets: Int, jokers: Int): Science {
    val science = Science()
    if (compasses > 0) {
        science.add(ScienceType.COMPASS, compasses)
    }
    if (wheels > 0) {
        science.add(ScienceType.WHEEL, wheels)
    }
    if (tablets > 0) {
        science.add(ScienceType.TABLET, tablets)
    }
    if (jokers > 0) {
        science.addJoker(jokers)
    }
    return science
}

fun playCardWithEffect(table: Table, playerIndex: Int, color: Color, effect: Effect) {
    val card = testCard(color, effect)
    val board = table.getBoard(playerIndex)
    board.addCard(card)
    card.applyTo(table, playerIndex, ResourceTransactions())
}

fun createMove(playerIndex: Int, card: Card, type: MoveType, vararg transactions: ResourceTransaction): Move {
    val playerMove = PlayerMove(type, card.name, Arrays.asList(*transactions))
    return type.resolve(playerIndex, card, playerMove)
}

@JvmOverloads
fun createPlayerMove(
    type: MoveType,
    cardName: String?,
    transactions: Collection<ResourceTransaction> = emptySet()
): PlayerMove {
    return PlayerMove(type, cardName, transactions)
}
