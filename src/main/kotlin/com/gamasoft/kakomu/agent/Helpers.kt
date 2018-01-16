package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*


fun isAnEye(board: Board, point: Point, color: Player): Boolean {

    //Must be empty
    if (!board.isFree(point)) {
        return false
    }

    //All adjacent points must contain friendly stones.
    for (neighbor in point.neighbors()) {
        if (board.isOnTheGrid(neighbor)) {
            val string = board.getString(neighbor)
            if (string == null || string.color != color)
                return false
        }
    }


    // We must control 3 out of 4 corners if the point is in the middle
    // of the board; on the edge we must control all corners.
    var friendlyCorners = 0
    var offBoardCorners = 0
    val corners = listOf<Point>(
            Point(point.row - 1, point.col - 1),
            Point(point.row - 1, point.col + 1),
            Point(point.row + 1, point.col - 1),
            Point(point.row + 1, point.col + 1)
    )
    for (corner in corners) {
        if (board.isOnTheGrid(corner)) {
            val cornerColor = board.getString(corner)?.color

            if (cornerColor == color)
                friendlyCorners += 1
        } else {
            offBoardCorners += 1
        }
    }


    if (offBoardCorners > 0) {
        // Point is on the edge or corner.
        return offBoardCorners + friendlyCorners == 4
    }

    // Point is in the middle.
    return friendlyCorners >= 3

}

val COLS = "ABCDEFGHJKLMNOPQRSTUVWXYZ"

val STONE_TO_CHAR = mapOf<Player?, Char>(
    null to '.',
    Player.BLACK to 'x',
    Player.WHITE to 'o'
)

fun printMove(player: Player, move: Move) {
    if (move.isPass) {
        println("$player passes")
    } else if (move.point == null) {
        println("$player resign")
    } else {
        println("$player " + COLS[move.point.col - 1] + move.point.row )
    }
}

fun drawBoard(board: Board): List<String> {
    val out = mutableListOf<String>()
    out.add("  " + COLS.substring(0, board.numCols))
    for (row in (board.numRows downTo 1)){
        val line = StringBuilder()
        for (col in (1 ..board.numCols)){
            val stone = board.getString(Point(row=row, col=col))?.color
            line.append(STONE_TO_CHAR[stone])
        }
        out.add("$row $line")
    }
    return out
}



fun printBoard(board: Board){
    drawBoard(board).forEach{l -> println(l)}
}


fun playSelfGame(boardSize: Int, black: Agent, white: Agent, delayMillis: Long): GameState {
    var game = GameState.newGame(boardSize)
    val bots = mapOf(Player.BLACK to black,
            Player.WHITE to white)

    while (!game.isOver()) {
        val botMove = bots[game.nextPlayer]!!.selectMove(game)
        game = game.applyMove(game.nextPlayer, botMove)

        Thread.sleep(delayMillis)
        printBoard(botMove, game.board, game.nextPlayer) //TODO make it lambda
    }
    return game
}

fun printBoard(move: Move, board: Board, nextPlayer: Player){
    println(27.toChar() + "[2J")
    printBoard(board)
    printMove(nextPlayer, move)
}


