package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player

class AlfaBetaPruning {

    val MAX_SCORE = 999999
    val MIN_SCORE = -999999

    companion object {
        fun alphaBetaResult(gameState: GameState, maxDepth: Int, bestBlack: Int, bestWhite: Int,
                            evalFn: (game: GameState) -> Int): Int {
            //Find the best result that next_player can get from this game state.

            return 0
        }
    }

}