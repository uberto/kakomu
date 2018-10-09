package com.gamasoft.kakomu.model

import java.util.*

class RandomRun(val boardSize: Int, val seed: Long? = null) {

    private val random: Random
    private val points: Array<Point>
    private val orderTrys: Array<Int>
    private val tmpBoard: Board

    init {
        if (seed == null){
            random = Random()
        } else {
            random = Random(seed)
        }

        points = createPointsArray(boardSize)
        points.shuffle(random)
        orderTrys = Array(points.size){it}
        tmpBoard = Board.newBoard(boardSize)

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

    fun randomMoveOnBoard(gameState: GameState): GameState {
        val board = gameState.board
        board.copyOnto(tmpBoard)

        for (i in 0 until orderTrys.size) {
            if (orderTrys.size - i - 1 > 1) {
                val nextInt = random.nextInt(orderTrys.size - i - 1)
                swap(orderTrys, i, i + nextInt + 1)
            }
            val candidate = points[orderTrys[i]]
            if (gameState.isValidPointToPlay(candidate) &&
                    !gameState.isAnEye(candidate)) {

                board.placeStone(gameState.nextPlayer, candidate)
                if (!gameState.doesMoveViolateKo(board.zobristHash()))
                    return GameState(board, gameState.nextPlayer.other(), gameState.moveInfo(Move.Play(candidate)))
                else
                    tmpBoard.copyOnto(board) //restore board before ko
            }
        }

        return gameState.applyPass()
    }

}

private fun <T> Array<T>.shuffle(random: Random) {
    for (i in this.size - 1 downTo 1) {
        swap(this, i, random.nextInt(i + 1))
    }
}

private fun <T> swap(arr: Array<T>, i: Int, j: Int) {
    if (i != j) {
        val tmp = arr[i]
        arr[i] = arr[j]
        arr[j] = tmp
    }
}
