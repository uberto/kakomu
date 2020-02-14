package com.gamasoft.kakomu

import com.gamasoft.kakomu.agent.*
import com.gamasoft.kakomu.model.Evaluator
import com.gamasoft.kakomu.model.GameState


class Performance {
    companion object {
        fun <T> crono(msg: String, function: () -> T): T {
            return crono(mutableListOf(), msg, function)
        }

        fun <T> crono(times:MutableList<Double>, msg: String, function: () -> T): T {
            val start = System.nanoTime()
            val res = function()
            val elapsed = (System.nanoTime() - start) / 1000000.0
            println("$msg in $elapsed millisec")
            times.add(elapsed)
            return res
        }

        fun warmup(iters: Int = 10000) {
            println("=====================================")
            println("==              Warmup             ==")
            println("=====================================")

            val boardSize = 9
            val startingState = GameState.newGame(boardSize)
            for (i in (1..iters)) {
                Evaluator.simulateRandomGame(startingState)
            }

            System.gc()
            println("Done $iters random rollouts on $boardSize board")
        }

        fun simulateRandomGames(boardSize: Int) {

            println("=====================================")
            println("==          Random Rollouts        ==")
            println("=====================================")

            val iter = 1000
            val times = ArrayList<Double>(iter)
            val startingState = GameState.newGame(boardSize)
            for (i in (1..iter)) {
                crono(times, "play random rollout ${boardSize}x${boardSize}") {
                    Evaluator.simulateRandomGame(startingState, 234345)  //767655
                }
            }

            times.sort()
            println("Best time ${times.first()}")
            println("Worst time ${times.last()}")
            println("Average time ${times.average()}")
            println("Median time ${times.get(times.size / 2)}")

        }

        fun cpuVsCpuRealGame(secondsForMove: Int = 60) {
            println("=====================================")
            println("==          CPU vs CPU game        ==")
            println("=====================================")
            val boardSize = 9
            val startingState = GameState.newGame(boardSize)

            val debugLevel = DebugLevel.INFO
            val fixedBots = Agents(MCTSAgent(secondsForMove, 1.8, debugLevel),
                    MCTSAgent(secondsForMove, 1.2, debugLevel))

            val res = crono("play self game ${boardSize}x${boardSize} with $secondsForMove sec for move") {
                playCompleteGame(startingState, fixedBots)
            }
            println("Game result: $res")
        }

    }

}