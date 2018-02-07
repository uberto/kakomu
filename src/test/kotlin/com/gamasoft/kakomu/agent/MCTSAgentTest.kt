package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Evaluator.Companion.simulateRandomGame
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player
import org.junit.jupiter.api.Test

internal class MCTSAgentTest {

    @Test
    fun performanceSelfGame() {
        val boardSize = 9
        val startingState = GameState.newGame(boardSize)
        val bots = mapOf(Player.BLACK to RandomBot(boardSize, 234345), Player.WHITE to RandomBot(boardSize, 767655))
        //warmup
        for (i in (1..1000)) {
            simulateRandomGame(startingState, bots)
        }
        for (i in (1..10)) {
            HelpersTest.crono("play self game ${boardSize}x${boardSize}") {
                simulateRandomGame(startingState, bots)
            }
        }
    }
}
