package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray


sealed class MCTS {

    object ROOT: MCTS()

    data class Node(val pos: Point, val gameState: GameState,
                    val parent: MCTS = ROOT): MCTS() {

        private val winCounts: AtomicIntegerArray = AtomicIntegerArray(2)

        private val rollouts: AtomicInteger = AtomicInteger()

        val children = mutableListOf<Node>() //ConcurrentHashMap<Point, Node>() //   mutableSetOf<Node>()

        private val unvisitedMoves = gameState.allMoves()

        fun addRandomChild(): Node {
            var newGameState: GameState? = null
            var point: Point = pos
            while (newGameState == null) {
                if (unvisitedMoves.isEmpty()) //no more children
                    return this
                point = unvisitedMoves.removeAt(0) //
                if (!gameState.isValidPointToPlay(point))
                    continue

                val newMove = Move.Play(point) //they are already random
                newGameState = gameState.applyMove(newMove)
            }
            val newNode = Node(point, newGameState, this)
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
            return bestMove?.gameState?.lastMoveDesc() + " " + bestMove?.getBestMoveSequence().orEmpty()
        }

        private fun selectBestChild(): Node? {
            var bestPct = -1.0
            var bestChild: Node? = null
            for (child in children) {
                val childPct = child.winningPct(gameState.nextPlayer)
                if (childPct > bestPct) {
                    bestPct = childPct
                    bestChild = child
                }
            }
            return bestChild
        }

        fun showMove(): String = gameState.lastMoveDesc()

        fun  rollouts(): Int = rollouts.get()



    }
}


