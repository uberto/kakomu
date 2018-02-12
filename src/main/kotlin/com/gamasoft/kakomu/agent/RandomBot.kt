package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Point
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

        for (i in points.size - 1 downTo 1) {
            swap(points, i, random.nextInt(i + 1))

            val candidate = points[i]
            if (gameState.isValidPointToPlay(candidate) &&
                   !gameState.isAnEye(candidate)){
                val newState = gameState.applyMove(Move.Play(candidate))
                if (newState != null)
                    return newState
            }
        }

        return gameState.applyMove(Move.Pass)!!
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