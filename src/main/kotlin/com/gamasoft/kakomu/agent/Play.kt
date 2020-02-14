package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*
import com.gamasoft.kakomu.model.Evaluator.computeGameResultFullBoard


fun playAgainstHuman(boardSize: Int, secondsForMove: Int){
    val game = GameState.newGame(boardSize)
    val dl = DebugLevel.TRACE
    val bot = MCTSAgent(secondsForMove, 1.5, dl)
    val human = HumanPlayerAgent()

    playCompleteGame(game, Agents(human, bot))
}

fun playCompleteGame(startState: GameState, bots: Agents): GameResult {
    var gameState = startState
    while (!gameState.isOver()) {
        println("\n\nMove ${gameState.moveNumber()} ${gameState.nextPlayer}")

        drawBoard(gameState.board, gameState.lastMove()).forEach { println(it) }

        val player = gameState.nextPlayer

        val nextMove = bots.play(player, gameState)
        println(drawMove(player, nextMove.lastMoveDesc()))
        val move = nextMove.lastMove()!! //TODO have a better GameState with move

        gameState = gameState.applyMove(move) ?: gameState
    }
    return computeGameResultFullBoard(gameState)
}








