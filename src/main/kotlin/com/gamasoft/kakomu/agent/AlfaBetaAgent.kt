package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import java.util.*

class AlfaBetaAgent(val maxDepth: Int, val evalFn: StateEval): Agent {

    val random: Random

    init {
        random = Random()
    }

    override fun playNextMove(gameState: GameState): GameState {
        var bestMoves = mutableListOf<GameState>()
        var bestScore: Int? = null
        var bestBlack = Evaluator.MIN_SCORE
        var bestWhite = Evaluator.MIN_SCORE


        // Loop over all legal moves.
        for (possibleMove in gameState.legalMoves()) {
            // Calculate the game state if we select this move.
            val nextState = gameState.applyMove(possibleMove)
            if (nextState == null)
                continue

            // Since our opponent plays next, figure out their best
            // possible outcome from there.
            val opponentBestOutcome = AlfaBetaPruning.alphaBetaResult(
                    nextState, maxDepth,
                    bestBlack, bestWhite,
                    evalFn)
            // Our outcome is the opposite of our opponent's outcome.
            val ourBestOutcome = -1 * opponentBestOutcome
            if (bestMoves.isEmpty() || bestScore == null || (ourBestOutcome > bestScore)) {
                // This is the best move so far.
                bestMoves = mutableListOf(nextState)
                bestScore = ourBestOutcome
                if (gameState.nextPlayer == Player.BLACK) {
                    bestBlack = bestScore
                } else if (gameState.nextPlayer == Player.WHITE) {
                    bestWhite = bestScore
                }
            } else if (ourBestOutcome == bestScore) {
                // This is as good as our previous best move.
                bestMoves.add(nextState)
            }
        }
        // For variety, randomly select among all equally good moves.
        return bestMoves.get(random.nextInt(bestMoves.size))

    }
}
