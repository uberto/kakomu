package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Point
import java.util.*

class RandomBot: Agent {
    override fun selectMove(gameState: GameState): Move {

        var teye = 0L
        var tval = 0L
        var tmov = 0L
        val t0 = System.nanoTime()

        val candidates = mutableListOf<Point>()
        for (r in (1..gameState.board.numRows)){
            for (c in (1 .. gameState.board.numCols)){
                val candidate = Point(row =r, col = c)
                val st9 = System.nanoTime()
                val move = Move.play(candidate)
                val st0 = System.nanoTime()
                val validMove = gameState.isValidMoveApartFromKo(move)

                val st1 = System.nanoTime()
                val isAnEye = isAnEye(gameState.board, candidate, gameState.nextPlayer)
                val st2 = System.nanoTime()
                teye += (st2-st1)
                tval += (st1-st0)
                tmov += (st0-st9)

                if (validMove && !isAnEye){
                    candidates.add(candidate)
                }
            }
        }


        val t1 = System.nanoTime()

        var nextMove = getNextMove(candidates)

        while (gameState.doesMoveViolateKo(gameState.nextPlayer, nextMove)){
            candidates.remove(nextMove.point)
            nextMove = getNextMove(candidates)
        }

        val t2 = System.nanoTime()

//        println("generate candidates ${t1- t0} play the move ${t2- t1}    eyes $teye move $tmov  valid $tval  ")



        return nextMove
    }

    private fun getNextMove(candidates: MutableList<Point>): Move {
        if (candidates.isEmpty())
            return Move.pass()
        else {
            val random = Random()
            return Move.play(candidates.get(random.nextInt(candidates.size)))
        }
    }
}