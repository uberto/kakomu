package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*


val COLS = "ABCDEFGHJKLMNOPQRSTUVWXYZ"

val STONE_TO_CHAR = mapOf<Player?, Char>(
        null to '.',
        Player.BLACK to 'x',
        Player.WHITE to 'o'
)

fun drawMove(player: Player, move: Move): String {
    return when (move) {
        is Move.Pass -> "$player passes"
        is Move.Resign -> "$player resigns"
        is Move.Play -> "$player ${move.humanReadable()}"
    }
}


fun drawBoard(board: Board, lastMove: Move? = null): List<String> {
    val out = mutableListOf<String>()
    out.add("   " + COLS.substring(0, board.numCols).map { c -> c + " " }.joinToString(separator = ""))
    for (row in (board.numRows downTo 1)){
        val line = StringBuilder()
        for (col in (1 ..board.numCols)){
            val stone = board.getString(Point(row=row, col=col))?.color
            line.append(STONE_TO_CHAR[stone])
            if (lastMove is Move.Play &&
                    lastMove.point.row == row &&
                    lastMove.point.col == col)
                line.append("←")
            else
                line.append(" ")
        }
        val prow = "$row".padStart(2)
        out.add("$prow $line$prow")
    }
    out.add("   " + COLS.substring(0, board.numCols).map { c -> c + " " }.joinToString(separator = ""))
    return out
}

fun printMoveAndBoard(game: GameState){
    println()
    println()
    drawBoard(game.board).forEach{println(it)}
    println(drawMove(game.nextPlayer, game.lastMove!!))
}


fun printWholeMatch(finalState: GameState){
    val states = mutableListOf<GameState>()
    var cs: GameState? = finalState
    while (cs != null){
        states.add(cs)
        cs = cs.previous
    }

    states.reverse()
    for (gs in states){
        drawBoard(gs.board, gs.lastMove).forEach{println(it)}
        println()
        println()
    }


}
