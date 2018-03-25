package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.model.Evaluator.Companion.computeGameResultFullBoard


data class GameState(val board: Board, val nextPlayer: Player, val previous: GameState?, val lastMove: Move?) {

    companion object {

        fun newGame(boardSize: Int): GameState{
            assert(boardSize > 1)
            val board = Board.newBoard(boardSize)
            return GameState(board, Player.BLACK, null, null)
        }
    }


// Full SuperKo
//    val previousStates: Set<Long>
//
//    init{
//        if (previous == null)
//            previousStates = emptySet()
//        else {
//            previousStates = previous.previousStates.plus(previous.board.zobristHash())
//        }
//    }

    val previousState: Long  //SimpleKo

    init{
        if (previous == null)
            previousState = 0
        else {
            previousState = previous.board.zobristHash()
        }
    }

     fun applyMove(move: Move): GameState? {

        val nextBoard = board.clone()
        if (move is Move.Play) {
            nextBoard.placeStone(nextPlayer, move.point)
            if (doesMoveViolateKo(nextBoard))
                return null
        }
        return GameState(nextBoard, nextPlayer.other(), this, move)
    }


     fun isOver(): Boolean{
        if (lastMove == null || previous == null)
            return false
        if (lastMove is Move.Resign)
            return true
        val secondLastMove = previous.lastMove
        if (secondLastMove == null)
            return false
        return (lastMove is Move.Pass) && (secondLastMove is Move.Pass)
    }


     fun doesMoveViolateKo(nextBoard: Board): Boolean {

        return previousState != 0L && nextBoard.zobristHash() == previousState //simpleKo
//        return nextBoard.zobristHash() in previousStates   //superKo

// without ZobristCache
//        var pastState = previous
//        while (pastState != null){
//            if (pastState.nextPlayer.other() == player && pastState.board == nextBoard)
//                return true
//            pastState = pastState.previous
//        }
//        return false
    }

     fun isValidPointToPlay(point: Point):Boolean {
        return (board.isFree(point) &&
                ! Evaluator.isSelfCapture(board, point, nextPlayer))
    }

    fun isValidMove(move: Move):Boolean {
        if (move !is Move.Play)
            return false
        return isValidPointToPlay(move.point) &&
                applyMove(move) != null  //Ko check
    }

    fun allMoves(): MutableList<Point> {
        val atari = mutableSetOf<Point>()
        val almostAtari = mutableSetOf<Point>()
        val moves = mutableSetOf<Point>()
        for (row in 1 .. board.numRows){
            for (col in 1 .. board.numCols){
                val point = Point(row, col)
                val string = board.getString(point)
                if (string == null) {
                    moves.add(point)
                } else if (string.libertiesCount() == 1) {
                    atari.addAll(string.liberties)
                } else if (string.libertiesCount() == 2) {
                    almostAtari.addAll(string.liberties)
                }
            }
        }

        almostAtari.removeAll(atari)
        moves.removeAll(atari)
        moves.removeAll(almostAtari)

        val moveList = moves.toMutableList()
        moveList.shuffle()

        moveList.addAll(0, almostAtari)
        moveList.addAll(0, atari)
        return moveList
    }

    fun winner(): Player? {
        if (!isOver())
            return null
        if (lastMove is Move.Resign)
            return nextPlayer

        val gameResult = computeGameResultFullBoard(this)
        return gameResult.winner()
    }

    fun isAnEye(point: Point): Boolean {
        return Evaluator.isAnEye(board, point, nextPlayer)
    }

}