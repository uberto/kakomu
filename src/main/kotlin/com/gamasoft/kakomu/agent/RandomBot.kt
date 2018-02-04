package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Point
import java.util.*
import java.util.Random



class RandomBot(val boardSize: Int, val seed: Long = 0): Agent {
    val random: Random
    val points: Array<Point>

    init {
        if (seed == 0L){
            random = Random()
        } else {
            random = Random(seed)
        }
        points = createPointsArray(boardSize)
        points.shuffle(random)
        points.shuffle(random)
    }

    private fun createPointsArray(boardSize: Int): Array<Point> {
        val points = mutableListOf<Point>()
        for (r in 1 .. boardSize){
            for (c in 1 ..boardSize) {
                points.add(Point(row = r, col = c))
            }
        }
        return points.toTypedArray()
    }

    override fun playNextMove(gameState: GameState): GameState {

        points.shuffle(random)

        for (candidate in points) {
            val move = Move.Play(candidate)
            if (gameState.isValidMoveApartFromKo(move) &&
                   !Evaluator.isAnEye(gameState.board, candidate, gameState.nextPlayer)){
                val newState = gameState.applyMove(move)
                if (newState != null)
                    return newState
            }
        }

        return gameState.applyMove(Move.Pass)!!
    }

    /*

class FastRandomBot(Agent):
    def __init__(self):
        self.dim = None
        self.point_cache = []

    def _update_cache(self, dim):
        self.dim = dim
        rows, cols = dim
        self.point_cache = []
        for r in range(1, rows + 1):
            for c in range(1, cols + 1):
                self.point_cache.append(Point(row=r, col=c))

    def select_move(self, game_state):
        """Choose a random valid move that preserves our own eyes."""
        dim = (game_state.board.num_rows, game_state.board.num_cols)
        if dim != self.dim:
            self._update_cache(dim)

        idx = np.arange(len(self.point_cache))
        np.random.shuffle(idx)
        for i in idx:
            p = self.point_cache[i]
            if game_state.is_valid_move(Move.play(p)) and \
                    not is_point_an_eye(game_state.board,
                                        p,
                                        game_state.next_player):
                return Move.play(p)
        return Move.pass_turn()
     */


    fun playNextMoveSlow(gameState: GameState): GameState {

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

        var nextState = gameState.applyMove(nextMove)

        while (nextState == null) {

            if (nextMove is Move.Play) {
                candidates.remove(nextMove.point)
            }
            nextMove = getNextMove(candidates)
            nextState = gameState.applyMove(nextMove)
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

private inline fun <T> Array<T>.shuffle(random: Random) {
    for (i in this.size - 1 downTo 1) {
        swap(this, i, random.nextInt(i + 1))
    }
}

private inline fun <T> swap(arr: Array<T>, i: Int, j: Int) {
    val tmp = arr[i]
    arr[i] = arr[j]
    arr[j] = tmp
}