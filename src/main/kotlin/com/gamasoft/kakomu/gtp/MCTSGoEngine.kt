package com.gamasoft.kakomu.gtp

import com.gamasoft.kakomu.agent.MCTSAgent
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Point
import org.lisoft.gonector.GoEngine

import org.lisoft.gonector.Move
import org.lisoft.gonector.Player

class MCTSGoEngine(val secondsForMove: Int, val temperature: Double) : GoEngine {

    var boardSize: Int = 9
    var game = GameState.newGame(boardSize)
    var bot = MCTSAgent(this.secondsForMove, temperature, boardSize, false)

    override fun newGame() {
        game = GameState.newGame(boardSize)
        bot = MCTSAgent(this.secondsForMove, temperature, boardSize, false)

    }

    override fun nextMove(aPlayer: Player): Move {
    //    https://github.com/EmilyBjoerk/gonector
        if (game.isOver())
            return Move.RESIGN

        val nextMove = bot.playNextMove(game).lastMove()!!
        //    println(drawMove(player, nextMove))

        game = game.applyMove(nextMove)?:game
        return translateMove(nextMove)
    }

    private fun translateMove(kakomuMove: com.gamasoft.kakomu.model.Move): Move =
        when (kakomuMove){
            com.gamasoft.kakomu.model.Move.Pass -> Move.PASS
            com.gamasoft.kakomu.model.Move.Resign -> Move.RESIGN
            is com.gamasoft.kakomu.model.Move.Play -> Move(kakomuMove.point.col - 1, kakomuMove.point.row - 1)
        }

    private fun translateMove(gtpMove: Move): com.gamasoft.kakomu.model.Move? =
        when (gtpMove){
            Move.PASS -> com.gamasoft.kakomu.model.Move.Pass
            Move.RESIGN -> com.gamasoft.kakomu.model.Move.Resign
            else -> {
                val p = Point(gtpMove.x + 1, gtpMove.y + 1)
                if (!game.board.isOnTheGrid(p) || !game.isValidPointToPlay(p))
                    null
                else
                    com.gamasoft.kakomu.model.Move.Play(p)
            }
        }



    override fun addMove(aMove: Move, aPlayer: Player): Boolean {
        val move = translateMove(aMove)

        if (move == null){
            return false
        }

        game = game.applyMove(move)?:game
        return true
    }


    override fun getName(): String = "Kakomu MCTS"

    override fun getVersion(): String = "1.0-SNAPSHOT"

    override fun resizeBoard(aSize: Int): Boolean {
        boardSize = aSize
        return true
    }

    override fun setKomi(aKomi: Float) {
        //nothing to do for the moment
    }

}
