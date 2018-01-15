package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Point
import java.util.*

class RandomBot: Agent {
    override fun selectMove(gameState: GameState): Move {

        val candidates = mutableListOf<Point>()
        for (r in (1..gameState.board.numRows + 1)){
            for (c in (1 .. gameState.board.numCols + 1)){
                val candidate = Point(row =r, col = c)
                if (gameState.isValidMove(Move.play(candidate)) && !isAnEye(gameState.board, candidate, gameState.nextPlayer)){
                    candidates.add(candidate)
                }
            }
        }
        if (candidates.isEmpty())
            return Move.pass()
        else {
            val random = Random()
            return Move.play(candidates.get(random.nextInt(candidates.size)))
        }

    }
}