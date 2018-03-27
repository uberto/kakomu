package com.gamasoft.kakomu.gtp

import com.gamasoft.kakomu.agent.MCTSAgent
import com.gamasoft.kakomu.model.GameState
import org.lisoft.gonector.GoEngine

import org.lisoft.gonector.Move
import org.lisoft.gonector.Player
class MCTSGoEngine : GoEngine {

    var boardSize: Int = 9
    val secondsForMove = 10
    var game = GameState.newGame(boardSize)
    var bot = MCTSAgent(secondsForMove, 2.0, boardSize)

    override fun nextMove(aPlayer: Player?): Move {
    //    https://github.com/EmilyBjoerk/gonector

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newGame() {
        game = GameState.newGame(boardSize)
        bot = MCTSAgent(secondsForMove, 2.0, boardSize)

    }

    override fun getName(): String = "Kakomu MCTS"

    override fun getVersion(): String = "1.0-SNAPSHOT"

    override fun addMove(aMove: Move?, aPlayer: Player?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resizeBoard(aSize: Int): Boolean {
        boardSize = aSize
        return true
    }

    override fun setKomi(aKomi: Float) {
        //nothing to do for the moment
    }

}
