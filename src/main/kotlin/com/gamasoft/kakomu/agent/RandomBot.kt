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
                points.add(Point(r, c))
            }
        }
        return points.toTypedArray()
    }

    override fun playNextMove(gameState: GameState): GameState {

        val newPoints: Array<Int> = Array(points.size){it}

        for (i in newPoints.size - 1 downTo 1) {
            swap(newPoints, i, random.nextInt(i + 1))

            val candidate = points[newPoints[i]]
            if (gameState.isValidPointToPlay( candidate) &&
                   !gameState.isAnEye(candidate)){
                val newState = gameState.applyMove(Move.Play(candidate))
                if (newState != null)
                    return newState
            }
        }

//        println("playNextMove  $winner  move number after ${randomGame.state.moveNumber()}")
//        printMoveAndBoard(randomGame.state)
//        println("")

        return gameState.applyPass()
    }

}

private fun <T> Array<T>.shuffle(random: Random) {
    for (i in this.size - 1 downTo 1) {
        swap(this, i, random.nextInt(i + 1))
    }
}

private fun <T> swap(arr: Array<T>, i: Int, j: Int) {
    val tmp = arr[i]
    arr[i] = arr[j]
    arr[j] = tmp
}