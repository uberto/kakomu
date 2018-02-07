package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*



fun playSelfGame(startingState: GameState, black: Agent, white: Agent, afterMove: (game: GameState) -> Unit): GameState {
    var game = startingState.clone()

    val bots = mapOf(Player.BLACK to black, Player.WHITE to white)

    while (!game.isOver()) {

        game = bots[game.nextPlayer]!!.playNextMove(game)

        afterMove(game)

    }
    return game
}

fun playAgainstHuman(boardSize: Int){
    var game = GameState.newGame(boardSize)
//    val bot = RandomBot(boardSize)
    val bot = MCTSAgent(30000, 1.5, boardSize)
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
                if (game.board.isOnTheGrid(point)) {
                    val move = Move.Play(point)
                    if (game.isValidMoveApartFromKo(move))
                        return move
                }
            }
        }
    }
}

fun printMoveAndBoard(game: GameState){
    println()
    println()
    drawBoard(game.board).forEach{println(it)}
    println(drawMove(game.nextPlayer, game.lastMove!!))
}


fun playAndPrintSelfGame(boardSize:Int){
    val printState: (GameState)->Unit =  {
        game ->
            Thread.sleep(100)
            printMoveAndBoard(game)

    }
    var game = GameState.newGame(boardSize)
    playSelfGame(game, RandomBot(boardSize), RandomBot(boardSize), printState)
}





