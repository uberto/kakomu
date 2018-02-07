package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point


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
    out.add("  " + COLS.substring(0, board.numCols).map { c -> c + " " }.joinToString(separator = ""))
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
        out.add("$row $line$row")
    }
    out.add("  " + COLS.substring(0, board.numCols).map { c -> c + " " }.joinToString(separator = ""))
    return out
}


