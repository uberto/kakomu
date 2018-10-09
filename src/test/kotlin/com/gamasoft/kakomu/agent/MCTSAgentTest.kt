package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Evaluator.simulateAutoGame
import com.gamasoft.kakomu.model.Evaluator.simulateRandomGame
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player
import org.junit.jupiter.api.Test

internal class MCTSAgentTest {

    val matchesNumber = 5
    val boardSize = 9
    val startingState = GameState.newGame(boardSize)
    val secondsForMove = 1

    /*

    TODO add test to evaluate a specific position with count of rollover

    @Test
    fun comparingStrenght() {

        collectBotsMatches(MCTSAgent(secondsForMove, 3.0, boardSize),
                           MCTSAgent(secondsForMove, 1.5, boardSize), startingState, matchesNumber)


    }

    @Test
    fun comparingStrenght2() {

        collectBotsMatches(MCTSAgent(secondsForMove, 1.1, boardSize),
                MCTSAgent(secondsForMove, 1.5, boardSize), startingState, matchesNumber)

    }

    @Test
    fun comparingStrenght3() {

        collectBotsMatches(MCTSAgent(secondsForMove, 1.4, boardSize),
                MCTSAgent(secondsForMove, 1.6, boardSize), startingState, matchesNumber)


    }

*/

    private fun collectBotsMatches(mctsAgentHot: MCTSAgent, mctsAgentCold: MCTSAgent, startingState: GameState, matchesNumber: Int) {
        val bots: Array<Agent> = arrayOf(mctsAgentHot, mctsAgentCold)
        val botsReversed: Array<Agent> = arrayOf(mctsAgentCold, mctsAgentHot)

        val tots = mutableMapOf<String, Int>()

        for (i in (1..matchesNumber)) {
            val res1 = simulateAutoGame(startingState, bots)
            val winner1 = if (res1.winner == Player.BLACK) "first" else "second"
            tots.compute(winner1) { p, v -> 1 + (v ?: 0) }
            println(winner1)
            val res2 = simulateAutoGame(startingState, botsReversed)
            val winner2 = if (res2.winner == Player.WHITE) "first" else "second"
            tots.compute(winner2) { _, v -> 1 + (v ?: 0) }
            println(winner2)

        }

        println("total win for first bot ${tots["first"]}")
        println("total win for second bot ${tots["second"]}")
    }

}
