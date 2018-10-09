package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*


val COLS = "ABCDEFGHJKLMNOPQRSTUVWXYZ"

val STONE_TO_CHAR = mapOf<Player?, Char>(
        null to '.',
        Player.BLACK to 'x',
        Player.WHITE to 'o'
)

fun drawMove(player: Player, moveDesc: String): String = "$player $moveDesc"


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
                line.append(")")
            else if (lastMove is Move.Play &&
                    lastMove.point.row == row &&
                    lastMove.point.col == col + 1)
                line.append("(")
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
    println(drawMove(game.nextPlayer, game.lastMoveDesc()))
}


fun printWholeMatch(finalState: GameState) {
    val moves = getMovesList(finalState)

    drawBoard(finalState.board).forEach { println(it) }
    println()
    println()

    var board = finalState.board.cloneEmpty()
    var p = Player.BLACK
    for (m in moves) {
        println("$p playing at ${m.humanReadable()}")
        if (m is Move.Play) {
            board.placeStone(p, m.point)
        }
        p = p.other()

        drawBoard(board, m).forEach { println(it) }
        println()
        println()
    }
}

private fun getMovesList(finalState: GameState): MutableList<Move> {
    val moves = mutableListOf<Move>()
    var moveInfo: MoveChain = finalState.moveInfo
    var pl = finalState.nextPlayer
    while (moveInfo is MoveChainZHash) {
        moveInfo.move.humanReadable()
        pl = pl.other()
        //  println("$pl ${cs.move.humanReadable()}")
        moves.add(moveInfo.move)
        moveInfo = moveInfo.previous
    }
    moves.reverse()
    return moves
}


