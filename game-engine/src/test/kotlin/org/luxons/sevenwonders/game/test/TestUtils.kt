package org.luxons.sevenwonders.game.test

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
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
import org.luxons.sevenwonders.game.resources.noTransactions
import org.luxons.sevenwonders.game.resources.resourcesOf
import org.luxons.sevenwonders.game.wonders.Wonder
import org.luxons.sevenwonders.game.wonders.WonderStage
import java.util.Arrays

private const val SEED: Long = 42

@JvmOverloads
fun testCustomizableSettings(initialGold: Int = 0): CustomizableSettings {
    return CustomizableSettings(randomSeedForTests = SEED).copy(initialGold = initialGold)
}

internal fun testSettings(nbPlayers: Int): Settings {
    return Settings(nbPlayers, testCustomizableSettings())
}

fun testTable(nbPlayers: Int): Table {
    return testTable(testSettings(nbPlayers))
}

internal fun testTable(settings: Settings): Table {
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
    return Wonder("Test Wonder " + initialResource.symbol!!, initialResource, listOf(stage1, stage2, stage3), "")
}

private fun createWonderStage(vararg effects: Effect): WonderStage {
    return WonderStage(Requirements(), Arrays.asList(*effects))
}

fun fixedProduction(vararg producedTypes: ResourceType): Production {
    val production = Production()
    production.addAll(resourcesOf(*producedTypes))
    return production
}

infix fun Int.of(type: ResourceType): Resources {
    return resourcesOf(type to this)
}

internal fun createTransactions(provider: Provider, vararg resources: ResourceType): ResourceTransactions {
    val transaction = createTransaction(provider, *resources)
    return listOf(transaction)
}

internal fun createTransactions(vararg transactions: ResourceTransaction): ResourceTransactions {
    return transactions.toList()
}

fun createTransaction(provider: Provider, vararg resources: ResourceType): ResourceTransaction {
    return ResourceTransaction(provider, resourcesOf(*resources))
}

fun createRequirements(vararg types: ResourceType): Requirements {
    val resources = resourcesOf(*types)
    return Requirements(resources = resources)
}

fun sampleCards(fromIndex: Int, nbCards: Int): List<Card> {
    return List(nbCards) { i -> testCard(i + fromIndex, Color.BLUE) }
}

fun testCard(name: String): Card {
    return testCard(name, Color.BLUE)
}

fun testCard(color: Color): Card {
    return testCard("Test Card", color)
}

internal fun testCard(color: Color, effect: Effect): Card {
    return testCard("Test Card", color, effect)
}

private fun testCard(num: Int, color: Color): Card {
    return testCard("Test Card $num", color)
}

fun createGuildCards(count: Int): List<Card> {
    return IntRange(0, count).map { createGuildCard(it) }
}

internal fun createGuildCard(num: Int, effect: Effect? = null): Card {
    return testCard("Test Guild $num", Color.PURPLE, effect)
}

internal fun testCard(
    name: String = "Test Card",
    color: Color = Color.BLUE,
    effect: Effect? = null,
    requirements: Requirements = Requirements()
): Card {
    val effects = if (effect == null) emptyList() else listOf(effect)
    return Card(name, color, requirements, effects, null, emptyList(), "path/to/card/image", createCardBack())
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

internal fun createScienceProgress(compasses: Int, wheels: Int, tablets: Int, jokers: Int): ScienceProgress {
    return ScienceProgress(createScience(compasses, wheels, tablets, jokers))
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

internal fun playCardWithEffect(player: Player, color: Color, effect: Effect) {
    val card = testCard(color, effect)
    player.board.addCard(card)
    card.applyTo(player, noTransactions())
}

internal fun createMove(
    context: PlayerContext,
    card: Card,
    type: MoveType,
    vararg transactions: ResourceTransaction
): Move {
    val playerMove = PlayerMove(type, card.name, Arrays.asList(*transactions))
    return type.create(playerMove, card, context)
}

internal fun singleBoardPlayer(board: Board): Player {
    return object : Player {
        override val index = 0
        override val board = board
        override fun getBoard(relativePosition: RelativeBoardPosition): Board = board
    }
}
