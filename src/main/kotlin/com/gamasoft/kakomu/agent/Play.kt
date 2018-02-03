package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*



fun playSelfGame(startingState: GameState, black: Agent, white: Agent, afterMove: (move: Move, game: GameState) -> Unit): GameState {
    var game = startingState.clone()

    val bots = mapOf(Player.BLACK to black, Player.WHITE to white)

    while (!game.isOver()) {
        val botMove = bots[game.nextPlayer]!!.selectMove(game)
        game = game.applyMove(game.nextPlayer, botMove)

        afterMove(botMove, game)
    }
    return game
}

fun playAgainstHuman(boardSize: Int){
    var game = GameState.newGame(boardSize)
    val bot = RandomBot()
    while (!game.isOver()) {
        printBoard(game.board)

        val move = if (game.nextPlayer == Player.BLACK){
            askMove(game)
        }
        else {
            val move = bot.selectMove(game)
            println(drawMove(game.nextPlayer, move))
            move
        }
        game = game.applyMove(game.nextPlayer, move)

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
                val move = Move.Play(point)
                if (game.isValidMoveIncludingSuperko(move))
                    return move
            }
        }
    }
}

fun printMoveAndBoard(move: Move, game: GameState){
    println()
    println()
    printBoard(game.board)
    println(drawMove(game.nextPlayer, move))
}


fun playAndPrintSelfGame(boardSize:Int){
    val printState: (Move, GameState)->Unit =  {
        move, game ->
            Thread.sleep(100)
            printMoveAndBoard(move, game)

    }
    var game = GameState.newGame(boardSize)
    playSelfGame(game, RandomBot(), RandomBot(), printState)
}




