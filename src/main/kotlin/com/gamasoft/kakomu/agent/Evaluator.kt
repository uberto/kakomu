package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point

class Evaluator {

    companion object {

        val MAX_SCORE = 999999
        val MIN_SCORE = -999999

        fun countTerritory(board: Board, player: Player): Int {
            //count all stones and eyes, works only after all possible stones are placed
            var tot = 0

            for(col in 1 .. board.numCols){
                for (row in 1.. board.numRows){
                    val point = Point(col, row)
                    val string = board.getString(point)
                    if (string == null) {
                        if (isAnEye(board, point, player))
                            tot++
                    } else if (string.color == player)
                        tot++
                }
            }
            return tot
        }


        fun isAnEye(board: Board, point: Point, color: Player): Boolean {

            //Must be empty
            if (!board.isFree(point)) {
                return false
            }

            //All adjacent points must contain friendly stones and not be capturable
            for (neighbor in board.neighbors(point)) {
                val string = board.getString(neighbor)
                if (string == null || string.color != color || string.libertiesCount() == 1)
                    return false
            }

            return true
        }
    }

}
