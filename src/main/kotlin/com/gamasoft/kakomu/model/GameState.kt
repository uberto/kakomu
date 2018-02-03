package com.gamasoft.kakomu.model


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

    fun applyMove(player: Player, move: Move): GameState? {
        assert (player == nextPlayer)

        val nextBoard = board.clone()
        if (move is Move.Play) {
            nextBoard.placeStone(player, move.point)
            if (doesMoveViolateKo(nextBoard))
                return null
        }
        return GameState(nextBoard, player.other(), this, move)
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

    fun isMoveSelfCapture(player: Player, move: Move): Boolean{
        if (move !is Move.Play)
            return false

        //if one of neighbors is same color and with more than 1 liberty is not self capture
        //if one of neighbors is different color and with exactly 1 liberty is not self capture
        for (neighbor in board.neighbors(move.point)) {
                val string = board.getString(neighbor)
                if (string == null
                        || (string.color == player && string.libertiesCount() > 1)
                        || (string.color == player.other() && string.libertiesCount() == 1) )
                    return false
        }

        return true
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

    fun isValidMoveApartFromKo(move: Move):Boolean {
        if (move !is Move.Play)
            return false
        return (board.isFree(move.point) &&
                ! isMoveSelfCapture(nextPlayer, move))
    }


    fun clone(): GameState {

        return GameState(board, nextPlayer, previous, lastMove)
    }

    fun legalMoves(): Set<Move> {
        val moves = mutableSetOf<Move>()
        for (row in 1 .. board.numRows){
            for (col in 1 .. board.numCols){
                val move = Move.Play(Point(row, col))
                if (isValidMoveApartFromKo(move)){
                    moves.add(move)
                }
            }
        }
        // These two moves are always legal .
        moves.add(Move.Pass)
        moves.add(Move.Resign)

        return moves
    }

    fun winner(): Player?{
        if (!isOver())
            return null
        if (lastMove is Move.Resign)
            return nextPlayer

        val gameResult = computeGameResult(this)
        return gameResult.winner()
    }

}