package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player

typealias StateEval = (game: GameState) -> Int

class AlfaBetaPruning {

    companion object {

        fun alphaBetaResult(gameState: GameState, maxDepth: Int, currentBestBlack: Int,
                            currentBestWhite: Int, evalFn: StateEval): Int {
            //Find the best result that next_player can get from this game state.
            var bestWhite = currentBestWhite
            var bestBlack = currentBestBlack


            if (gameState.isOver()) {
                if (gameState.winner() == gameState.nextPlayer)
                    return Evaluator.MAX_SCORE
                else
                    return Evaluator.MIN_SCORE
            }

            if (maxDepth == 0) {
                // We have reached our maximum search depth. Use our heuristic to
                // decide how good this sequence is.
                return evalFn(gameState)
            }

            var bestSoFar = Evaluator.MIN_SCORE

            // Loop over all valid moves.
            for (candidateMove in gameState.legalMoves()) {

                //See what the board would look like if we play this move.
                val nextState = gameState.applyMove(candidateMove)

                if (nextState == null) //ko invalid
                    continue


                //Find out our opponent's best result from that position.
                val opponentBestResult = alphaBetaResult(
                        nextState, maxDepth - 1,
                        bestBlack, bestWhite, evalFn)
                // Whatever our opponent wants, we want the opposite.
                val ourResult = -1 * opponentBestResult

                // See if this result is better than the best we've seen so far.
                if (ourResult > bestSoFar) {
                    bestSoFar = ourResult
                }

                if (gameState.nextPlayer == Player.WHITE) {
                    //Update our benchmark for white.
                    if (bestSoFar > bestWhite) {
                        bestWhite = bestSoFar
                    }
                    // We are picking a move for white; it only needs to be
                    // strong enough to eliminate black's previous move.
                    val outcomeForBlack = -1 * bestSoFar
                    if (outcomeForBlack < bestBlack) {
                        // candidate_move is strong enough to eliminate this move
                        return bestSoFar
                    }
                } else if (gameState.nextPlayer == Player.BLACK) {
                    // Update our benchmark for black.
                    if (bestSoFar > bestBlack) {
                        bestBlack = bestSoFar
                    }
                    // We are picking a move for black; it only needs to be
                    // strong enough to eliminate white's previous move.
                    val outcomeForWhite = -1 * bestSoFar
                    if (outcomeForWhite < bestWhite) {
                        return bestSoFar
                    }
                }

            }
            return bestSoFar
        }

    }
}
