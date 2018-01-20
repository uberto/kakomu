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

fun drawMove(player: Player, move: Move): String {
    if (move.isPass) {
        return "$player passes"
    } else if (move.point == null) {
        return "$player resigns"
    } else {
        return "$player ${COLS[move.point.col - 1]}${move.point.row}"
    }
}


fun drawBoard(board: Board): List<String> {
    val out = mutableListOf<String>()
    out.add("  " + COLS.substring(0, board.numCols).map { c -> c + " " }.joinToString(separator = ""))
    for (row in (board.numRows downTo 1)){
        val line = StringBuilder()
        for (col in (1 ..board.numCols)){
            val stone = board.getString(Point(row=row, col=col))?.color
            line.append(STONE_TO_CHAR[stone])
            line.append(" ")
        }
        out.add("$row $line $row")
    }
    out.add("  " + COLS.substring(0, board.numCols).map { c -> c + " " }.joinToString(separator = ""))
    return out
}



fun printBoard(board: Board){
    drawBoard(board).forEach{ println(it)}
}


fun playSelfGame(boardSize: Int, black: Agent, white: Agent, afterMove: (move: Move, game: GameState) -> Unit): GameState {
    var game = GameState.newGame(boardSize)
    val bots = mapOf(Player.BLACK to black, Player.WHITE to white)

    while (!game.isOver()) {
        val botMove = bots[game.nextPlayer]!!.selectMove(game)
        game = game.applyMove(game.nextPlayer, botMove)

        afterMove(botMove, game)
    }
    return game
}

fun playAgainstHuman(boardSize: Int){
    var game = GameState.newGame(boardSize)
    val bot = RandomBot()
    while (!game.isOver()) {
        printBoard(game.board)

        val move = if (game.nextPlayer == Player.BLACK){
            askMove(game)
        }
        else {
            val move = bot.selectMove(game)
            println(drawMove(game.nextPlayer, move))
            move
        }
        game = game.applyMove(game.nextPlayer, move)

    }
}

private fun askMove(game: GameState): Move {
    while (true) {
        print("insert move coords:")
        val humanMove = readLine()!!
        val point = Point.fromCoords(humanMove.trim())
        val move = Move.play(point)
        if (game.isValidMoveIncludingSuperko(move))
            return move
    }
}

fun printMoveAndBoard(move: Move, game: GameState){
    println()
    println()
    printBoard(game.board)
    println(drawMove(game.nextPlayer, move))
}


