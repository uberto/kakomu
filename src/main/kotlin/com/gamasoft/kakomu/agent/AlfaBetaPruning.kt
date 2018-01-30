package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player

class AlfaBetaPruning {

    companion object {

        val MAX_SCORE = 999999
        val MIN_SCORE = -999999

        fun alphaBetaResult(gameState: GameState, maxDepth: Int, currentBestBlack: Int,
                            currentBestWhite: Int, evalFn: (game: GameState) -> Int): Int {
            //Find the best result that next_player can get from this game state.
            var bestWhite = currentBestWhite
            var bestBlack = currentBestBlack


            if (gameState.isOver()) {
                if (gameState.winner() == gameState.nextPlayer)
                    return MAX_SCORE
                else
                    return MIN_SCORE
            }

            if (maxDepth == 0) {
                // We have reached our maximum search depth. Use our heuristic to
                // decide how good this sequence is.
                return evalFn(gameState)
            }

            var bestSoFar = MIN_SCORE

            // Loop over all valid moves.
            for (candidateMove in gameState.legalMoves()) {

                //See what the board would look like if we play this move.
                val nextState = gameState.applyMove(gameState.nextPlayer, candidateMove)
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

/*
   """Find the best result that next_player can get from this game
    state.
    """
    if game_state.is_over():
        # Game is already over.
        if game_state.winner() == game_state.next_player:
            return MAX_SCORE
        else:
            return MIN_SCORE

    if max_depth == 0:
        # We have reached our maximum search depth. Use our heuristic to
        # decide how good this sequence is.
        return eval_fn(game_state)

    best_so_far = MIN_SCORE
    # Loop over all valid moves.
    for candidate_move in game_state.legal_moves():
        # See what the board would look like if we play this move.
        next_state = game_state.apply_move(candidate_move)
        # Find out our opponent's best result from that position.
        opponent_best_result = alpha_beta_result(
            next_state, max_depth - 1,
            best_black, best_white,
            eval_fn)
        # Whatever our opponent wants, we want the opposite.
        our_result = -1 * opponent_best_result

        # See if this result is better than the best we've seen so far.
        if our_result > best_so_far:
            best_so_far = our_result

        if game_state.next_player == Player.white:
            # Update our benchmark for white.
            if best_so_far > best_white:
                best_white = best_so_far
            # We are picking a move for white; it only needs to be
            # strong enough to eliminate black's previous move.
            outcome_for_black = -1 * best_so_far
            if outcome_for_black < best_black:
                # candidate_move is strong enough to eliminate this move
                return best_so_far

        elif game_state.next_player == Player.black:
            # Update our benchmark for black.
            if best_so_far > best_black:
                best_black = best_so_far
            # We are picking a move for black; it only needs to be
            # strong enough to eliminate white's previous move.
            outcome_for_white = -1 * best_so_far
            if outcome_for_white < best_white:
                return best_so_far

    return best_so_far




# tag::alpha-beta-agent[]
class AlphaBetaAgent(Agent):
    def __init__(self, max_depth, eval_fn):
        self.max_depth = max_depth
        self.eval_fn = eval_fn

    def select_move(self, game_state):
        best_moves = []
        best_score = None
        best_black = MIN_SCORE
        best_white = MIN_SCORE
        # Loop over all legal moves.
        for possible_move in game_state.legal_moves():
            # Calculate the game state if we select this move.
            next_state = game_state.apply_move(possible_move)
            # Since our opponent plays next, figure out their best
            # possible outcome from there.
            opponent_best_outcome = alpha_beta_result(
                next_state, self.max_depth,
                best_black, best_white,
                self.eval_fn)
            # Our outcome is the opposite of our opponent's outcome.
            our_best_outcome = -1 * opponent_best_outcome
            if (not best_moves) or our_best_outcome > best_score:
                # This is the best move so far.
                best_moves = [possible_move]
                best_score = our_best_outcome
                if game_state.next_player == Player.black:
                    best_black = best_score
                elif game_state.next_player == Player.white:
                    best_white = best_score
            elif our_best_outcome == best_score:
                # This is as good as our previous best move.
                best_moves.append(possible_move)
        # For variety, randomly select among all equally good moves.
        return random.choice(best_moves)

 */