package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Point
import java.util.*

class RandomBot(val seed: Long = 0): Agent {

    val random: Random

    init {
        if (seed == 0L){
            random = Random()
        } else {
            random = Random(seed)
        }
    }

    override fun selectMove(gameState: GameState): GameState {

//        var teye = 0L
//        var tval = 0L
//        var tmov = 0L
//        var tcre = 0L
    //    val t0 = System.nanoTime()

        val candidates = mutableListOf<Point>()
        for (r in (1..gameState.board.numRows)){
            for (c in (1 .. gameState.board.numCols)){
      //          val st8 = System.nanoTime()
                val candidate = Point(row = r, col = c)
      //          val st9 = System.nanoTime()
                val move = Move.Play(candidate)
      //          val st0 = System.nanoTime()
                val validMove = gameState.isValidMoveApartFromKo(move)
//                val st1 = System.nanoTime()

//                tval += (st1-st0)
//                tmov += (st0-st9)
//                tcre += (st9-st8)

                if (validMove) {
                    val isAnEye = Evaluator.isAnEye(gameState.board, candidate, gameState.nextPlayer)
//                    val st2 = System.nanoTime()
//                    teye += (st2-st1)
                    if (!isAnEye) {
                        candidates.add(candidate)
                    }
                }
            }
        }


   //     val t1 = System.nanoTime()

        var nextMove = getNextMove(candidates)

        var nextState = gameState.applyMove(gameState.nextPlayer, nextMove)

        while (nextState == null) {

            if (nextMove is Move.Play) {
                candidates.remove(nextMove.point)
            }
            nextMove = getNextMove(candidates)
            nextState = gameState.applyMove(gameState.nextPlayer, nextMove)
        }

     //   val t2 = System.nanoTime()

//        println("generate candidates ${t1- t0} play the move ${t2- t1}    pointcreation $tcre eyes $teye move $tmov  valid $tval  ")

        return nextState
    }

    private fun getNextMove(candidates: MutableList<Point>): Move {
        if (candidates.isEmpty())
            return Move.Pass
        else {

            return Move.Play(candidates.get(random.nextInt(candidates.size)))
        }
    }
}