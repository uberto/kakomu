package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*



fun playAgainstHuman(boardSize: Int){
    var game = GameState.newGame(boardSize)
//    val bot = RandomBot(boardSize)
    val bot = MCTSAgent(50000, 1.5, boardSize)
    while (!game.isOver()) {
        drawBoard(game.board, game.lastMove).forEach{ println(it)}

        val player = game.nextPlayer
        val move = if (player == Player.BLACK){
            askMove(game)
        } else {
            val nextMove = bot.playNextMove(game).lastMove!!
            println(drawMove(player, nextMove))
            nextMove
        }

        val newGame = game.applyMove(move)

        if (newGame != null)
            game = newGame

    }
}

private fun askMove(game: GameState): Move {
    while (true) {
        print("insert move coords:")
        val humanMove = readLine()!!.trim().toLowerCase()
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

fun validMove(humanMove: String): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}







