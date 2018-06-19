package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Point

class HumanPlayerAgent: Agent{

    override fun playNextMove(gameState: GameState): GameState {

        val m = askMove(gameState)
        return gameState.applyMove(m)!!
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

}