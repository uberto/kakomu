package com.gamasoft.kakomu.model

data class GameState(val board: Board, val nextPlayer: Player, val previous: GameState?, val lastMove: Move?) {

    companion object {

        fun newGame(boardSize: Int): GameState{
            assert(boardSize > 1)
            val board = Board(boardSize, boardSize)
            return GameState(board, Player.BLACK, null, null)
        }
    }

    fun applyMove(player: Player, move: Move): GameState{
        assert (player == nextPlayer)

        val nextBoard = Board( board)

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
        val nextBoard = Board(board)
        nextBoard.placeStone(player, move.point)

        val newString = nextBoard.getString(move.point)
        if (newString == null)
            return true // throw exception?
        return newString.liberties.size == 0

    }

    fun doesMoveViolateKo(player: Player, move: Move): Boolean {
        if (move.point == null)
            return false
        val nextBoard = Board(board)
        nextBoard.placeStone(player, move.point)
        val nextSituation = Pair(player.other(), nextBoard)
        var pastState = previous
        while (pastState != null){
            val pastStateSituation = Pair<Player, Board>(pastState.nextPlayer.other(), pastState.board)
            if (pastStateSituation == nextSituation)
                return true
            pastState = pastState.previous
        }
        return false
    }

}