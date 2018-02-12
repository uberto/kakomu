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
        val bots = mapOf(Player.BLACK to RandomBot(boardSize), Player.WHITE to RandomBot(boardSize))
        //warmup
        for (i in (1..1000)) {
            simulateRandomGame(startingState, bots)
        }
        for (i in (1..10)) {
            val fixedBots = mapOf(Player.BLACK to RandomBot(boardSize, 234345), Player.WHITE to RandomBot(boardSize, 767655))
            PrintTest.crono("play self game ${boardSize}x${boardSize}") {
                simulateRandomGame(startingState, fixedBots)
            }
        }
    }

    //on my laptop i7 2Ghz
//with validmove without deepcopy:
//about 10 msec on 9x9 and 160 on 19x19
//with immutable goStrings:
//about 6 msec on 9x9 and 65 on 19x19
//with neighbors map:
//about 3.5 msec on 9x9 and 38 on 19x19
//with faster isAnEye:
//about 1.5 msec on 9x9 and 31 on 19x19
//with simpleKo:
//about 1.3 msec on 9x9 and 27 on 19x19
//with selectMove returning State:
//about 1.25 msec on 9x9 and 25 on 19x19
//without System.nanotime:
//about 0.7 msec on 9x9 and 12 on 19x19
//with fastRandomBot:
//about 0.5 msec on 9x9 and 4.5 on 19x19
//with single swap instead of shuffle:
//about 0.33 msec on 9x9 and 2.4 on 19x19
}
