package com.gamasoft.kakomu.model

import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

sealed class MoveChain {}

data class MoveChainZHash(val previous: MoveChain, val zHash: Long, val move: Move): MoveChain()
object EmptyBoard: MoveChain()




data class GameState(val board: Board, val nextPlayer: Player, val moveInfo: MoveChain) {
//data class GameState(val board: Board, val nextPlayer: Player, val previous: GameState?, val lastMove: Move?) {

    companion object {

        fun newGame(boardSize: Int): GameState{
            assert(boardSize > 1)
            val board = Board.newBoard(boardSize)
            return GameState(board, Player.BLACK, EmptyBoard)
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

//    val previousState: Long  //SimpleKo
//
//    init{
//        if (previous == null)
//            previousState = 0
//        else {
//            previousState = previous.board.zobristHash()
//        }
//    }

    fun moveInfo(move: Move): MoveChain {
        return MoveChainZHash(moveInfo, board.zobristHash(), move)
    }

    fun applyPass(): GameState {
        return GameState(board, nextPlayer.other(), moveInfo(Move.Pass))
    }

    fun applyResign(): GameState {
        return GameState(board, nextPlayer.other(), moveInfo(Move.Resign))
    }

    fun applyMove(move: Move): GameState? =
        when (move){
            is Move.Play -> {
                val nextBoard = board.clone()
                nextBoard.placeStone(nextPlayer, move.point)
                if (doesMoveViolateKo(nextBoard))
                    null
                else
                    GameState(nextBoard, nextPlayer.other(), moveInfo(move))
            }
            else ->
                GameState(board, nextPlayer.other(), moveInfo(move))
        }



     fun isOver(): Boolean{
        return when (moveInfo){
            EmptyBoard -> false
            is MoveChainZHash -> {
                if(moveInfo.move is Move.Resign)
                    true
                else {
                    val previous = moveInfo.previous
                    when (previous){
                        EmptyBoard -> false
                        is MoveChainZHash -> moveInfo.move is Move.Pass && previous.move is Move.Pass
                    }
                }
            }
        }
     }


     fun doesMoveViolateKo(nextBoard: Board): Boolean {

         return when(moveInfo){
             EmptyBoard -> false
             is MoveChainZHash -> when (moveInfo.previous) {
                 EmptyBoard -> false
                 is MoveChainZHash -> nextBoard.zobristHash() == moveInfo.previous.zHash
             }
         }

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

    fun allMoves(): MutableList<Point> {  //TODO try to use a lazy Seq (in case with check valid)
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
        Collections.shuffle(moveList)

        moveList.addAll(0, almostAtari)
        moveList.addAll(0, atari)
        return moveList
    }


    fun isAnEye(point: Point): Boolean {
        return Evaluator.isAnEye(board, point, nextPlayer)
    }

    fun moveNumber():Int {
        var mn = 1
        var gs = moveInfo
        while (gs is MoveChainZHash){
            gs = gs.previous
            mn++
        }

        return mn
    }

    fun lastMove(): Move? {
        return (moveInfo as? MoveChainZHash)?.move
    }

    fun lastMoveDesc(): String {
        return lastMove()?.humanReadable().orEmpty()
    }

}