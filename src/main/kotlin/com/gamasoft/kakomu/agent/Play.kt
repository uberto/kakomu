package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*



fun playAgainstHuman(boardSize: Int, secondsForMove: Int){
    var game = GameState.newGame(boardSize)
    val bot = MCTSAgent(secondsForMove, 2.0, boardSize)
    while (!game.isOver()) {
        drawBoard(game.board, game.lastMove()).forEach{ println(it)}

        val player = game.nextPlayer
        val move = if (player == Player.BLACK){
            askMove(game)
        } else {
            val nextMove = bot.playNextMove(game)
            println(drawMove(player, nextMove.lastMoveDesc()))
            nextMove.lastMove()!! //TODO have a better GameState with move
        }

        game = game.applyMove(move)?:game

    }
}

private fun askMove(game: GameState): Move {
    while (true) {
        print("insert move coords:")
        val humanMove = readLine()?.trim()?.toLowerCase().orEmpty()
        when (humanMove) {
            "resign" -> return Move.Resign
            "pass" -> return Move.Pass
            else -> {
                val point = Point.fromCoords(humanMove)
                if (point != null && game.board.isOnTheGrid(point)) {
                    if (game.isValidPointToPlay(point))
                        return Move.Play(point)
                }
            }
        }
    }
}








