package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray


sealed class MCTS {

    data class Node(val gameState: GameState, val parent: MCTS = ROOT): MCTS() {

        object ROOT : MCTS()


        private val winCounts: AtomicIntegerArray = AtomicIntegerArray(2)

        private val rollouts: AtomicInteger = AtomicInteger()

        val children = mutableSetOf<Node>()

        private val unvisitedMoves = gameState.allMoves() //TODO legal moves to a lazy seq


        fun addRandomChild(): Node {
            var newGameState: GameState? = null
            while (newGameState == null) {
                if (unvisitedMoves.isEmpty()) //no more children
                    return this
                val point = unvisitedMoves.removeAt(0)
                if (!gameState.isValidPointToPlay(point))
                    continue

                val newMove = Move.Play(point) //they are already random
                newGameState = gameState.applyMove(newMove)
            }
            val newNode = Node(newGameState, this)
            children.add(newNode)
            return newNode
        }

        fun recordWin(winner: Player) {

            winCounts.incrementAndGet(winner.toInt())
            rollouts.incrementAndGet()
//        println("winner $winner ${winCounts[winner]} $rollouts")

        }

        fun isTerminal(): Boolean {
            return gameState.isOver()
        }

        fun winningPct(player: Player): Double {
            return winCounts[player.toInt()] / rollouts.toDouble()
        }


        fun completelyVisited(): Boolean {
            return unvisitedMoves.isEmpty()
        }

        fun getBestMoveSequence(): String {
            val bestMove = selectBestChild()
            return bestMove.gameState.lastMove?.humanReadable().orEmpty() + " " + bestMove.getBestMoveSequence().orEmpty()
        }

        private fun selectBestChild(): Node {
            var bestPct = -1.0
            var bestChild: Node = this
            for (child in children) {
                val childPct = child.winningPct(gameState.nextPlayer)
                if (childPct > bestPct) {
                    bestPct = childPct
                    bestChild = child
                }
            }
            return bestChild
        }

        fun showMove(): String = gameState.lastMove?.humanReadable().orEmpty()

        fun rollouts(): Int = rollouts.get()



    }
}


