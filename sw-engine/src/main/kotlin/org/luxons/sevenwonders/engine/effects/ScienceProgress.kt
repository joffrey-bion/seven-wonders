package org.luxons.sevenwonders.engine.effects

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Science
import org.luxons.sevenwonders.engine.data.serializers.ScienceProgressSerializer

@Serializable(with = ScienceProgressSerializer::class)
internal class ScienceProgress(val science: Science) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.science.addAll(science)
}
