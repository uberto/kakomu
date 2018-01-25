package com.gamasoft.kakomu.model


data class GameState(val board: Board, val nextPlayer: Player, val previous: GameState?, val lastMove: Move?) {

    companion object {

        fun newGame(boardSize: Int): GameState{
            assert(boardSize > 1)
            val board = Board.newBoard(boardSize)
            return GameState(board, Player.BLACK, null, null)
        }
    }

    var previousStates: Set<Pair<Player, Long>>

    init{
        if (previous == null)
            previousStates = emptySet()
        else {
            previousStates = previous.previousStates.plus(Pair(previous.nextPlayer, previous.board.zobristHash()))
        }
    }

    fun applyMove(player: Player, move: Move): GameState{
        assert (player == nextPlayer)

        val nextBoard = board.clone()
        if (move.point != null){
            nextBoard.placeStone(player, move.point)
        }

        return GameState(nextBoard, player.other(), this, move)
    }


    fun isOver(): Boolean{
        if (lastMove == null || previous == null)
            return false
        if (lastMove.isResign)
            return true
        val secondLastMove = previous.lastMove
        if (secondLastMove == null)
            return false
        return lastMove.isPass && secondLastMove.isPass
    }

    fun isMoveSelfCapture(player: Player, move: Move): Boolean{
        if (move.point == null)
            return false

        //if one of neighbors is same color and with more than 1 liberty is not self capture
        //if one of neighbors is different color and with exactly 1 liberty is not self capture
        for (neighbor in board.neighbors(move.point)) {
                val string = board.getString(neighbor)
                if (string == null
                        || (string.color == player && string.liberties.size > 1)
                        || (string.color == player.other() && string.liberties.size == 1) )
                    return false
        }

        return true
//        val nextBoard = board.clone()
//        nextBoard.placeStone(player, move.point)
//
//        val newString = nextBoard.getString(move.point)
//        if (newString == null)
//            return true // throw exception?
//        return newString.liberties.size == 0

    }

    fun doesMoveViolateKo(player: Player, move: Move): Boolean {

        if (move.point == null)
            return false
        val nextBoard = board.clone()
        nextBoard.placeStone(player, move.point)

        val nextSituation = Pair(player.other(), nextBoard.zobristHash())
        return nextSituation in previousStates

// without ZobristCache
//        var pastState = previous
//        while (pastState != null){
//            if (pastState.nextPlayer.other() == player && pastState.board == nextBoard)
//                return true
//            pastState = pastState.previous
//        }
//        return false
    }

    fun isValidMoveApartFromKo(move: Move):Boolean {
        if (move.point == null)
            return true
        return (board.isFree(move.point) &&
                ! isMoveSelfCapture(nextPlayer, move))
    }

    fun isValidMoveIncludingSuperko(move: Move):Boolean {
        if (!isValidMoveApartFromKo(move))
            return false
        return !doesMoveViolateKo(nextPlayer, move)
    }

    fun clone(): GameState {

        return GameState(board, nextPlayer, previous, lastMove)
    }

}