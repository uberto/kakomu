package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Evaluator
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import kotlinx.coroutines.experimental.*
import java.util.concurrent.atomic.AtomicInteger


class MCTSAgent(val secondsForMove: Int, val temperature: Double, val boardSize: Int) : Agent {
//colder will evaluate better but can miss completely the best move
    //1.5 is a good starting point temperature
//hotter will explore more moves but can mis-evaluate the most promising


    //for concurrency
    private val currentlyEvaluatingNodes: MutableSet<MCTS.Node> = mutableSetOf()

    val bots: Array<Agent>


    init {
        bots = arrayOf(RandomBot(boardSize), RandomBot(boardSize))
    }

    fun selectChild(node: MCTS.Node): MCTS.Node {
        //Select a child according to the upper confidence bound for trees (UCT) metric.

        val totalRollouts = node.rollouts()
        val logRollouts = Math.log(totalRollouts.toDouble())

        var bestScore = -1.0
        var bestChild: MCTS.Node = node
        //Loop over each child.
        for (child in node.children) {
            if (child in currentlyEvaluatingNodes)
                continue //skip this, someoneelse is already looking at it

            // Calculate the UCT score.
            val winPercentage = child.winningPct(node.gameState.nextPlayer)
            val explorationFactor = Math.sqrt(logRollouts / child.rollouts())
            val uctScore = winPercentage + temperature * explorationFactor
            // Check if this is the largest we've seen so far.
            if (uctScore > bestScore) {
                bestScore = uctScore
                bestChild = child
            }
        }

        return bestChild
    }

    private fun incRollouts(i: AtomicInteger) {
        i.incrementAndGet()
        if (i.get() % 5000 == 0) {
            print('.')
        }
    }


    override fun playNextMove(gameState: GameState): GameState {
        println()
        println("Thinking...")

        val root = MCTS.Node(gameState)

        val rolls = exploreTree(root)
        println("Done ${rolls} rollouts")

        //Having performed as many MCTS rounds as we have time for, we
        //now pick a move.
        var bestMove: GameState = gameState
        var bestPct = -1.0
        for (child in root.children.sortedBy { it.showMove() }) {
            val childPct = child.winningPct(gameState.nextPlayer)
            if (childPct > bestPct) {
                bestPct = childPct
                bestMove = child.gameState
            }
            println("    considered move ${child.showMove()} with win pct $childPct on ${child.rollouts()} rollouts. Best continuation: ${child.getBestMoveSequence()} ")
        }

        if (bestPct <= 0.15) //let's do the right thing and resign if hopeless
            bestMove = GameState(gameState.board, gameState.nextPlayer, gameState.previous, Move.Resign)

        println("Select move ${bestMove.lastMove?.humanReadable()} with win pct $bestPct")

        return bestMove

    }
//    private inline fun exploreTree(root: MCTS.Node): Int = exploreTreeNoConcurrency(root)
    private inline fun exploreTree(root: MCTS.Node): Int = exploreTreeConcurrency(root)

    private fun exploreTreeNoConcurrency(root: MCTS.Node): Int {
        val i = AtomicInteger(0)
        val start = System.currentTimeMillis()
        val maxMillis = secondsForMove * 1000
        while (System.currentTimeMillis() - start < maxMillis) {
            incRollouts(i)
            newRolloutAndRecordWin(root)
        }
        println(" ${i.get()} rollouts in ${System.currentTimeMillis() - start} millisecs")
        return i.get()
    }

    private fun exploreTreeConcurrency(root: MCTS.Node): Int {
        val i = AtomicInteger(0)
        val start = System.currentTimeMillis()
        val maxMillis = secondsForMove * 1000

        runBlocking {

            while (System.currentTimeMillis() - start < maxMillis) {
                val jobs = mutableListOf<Job>()
                repeat(10) {
                    incRollouts(i)
                    jobs.add(launch { newRolloutAndRecordWin(root) })
                }
                jobs.forEach{it.join()}
            }
        }
        return i.get()
    }

    private fun newRolloutAndRecordWin(root: MCTS.Node) {
        val node = selectNextNode(root)

        val winner = getWinnerOfRandomPlay(node)

        propagateResult(node, winner)
    }

    private fun propagateResult(node: MCTS.Node, winner: Player) {
        var node1 = node
        //Propagate scores back up the tree.
        while(true) {
            node1.recordWin(winner)
            val parent = node1.parent
            if (parent is MCTS.Node)
                node1 = parent
            else
                break
        }
        currentlyEvaluatingNodes.remove(node)

    }

    private fun selectNextNode(root: MCTS.Node): MCTS.Node {
        var node = root

        synchronized(this) {
            while (node.completelyVisited() && !node.isTerminal()) {
                node = selectChild(node)
            }
            node = node.addRandomChild()
            currentlyEvaluatingNodes.add(node)
        }
        return node
    }

    private fun getWinnerOfRandomPlay(node: MCTS.Node): Player {
        //Simulate a random game from this node.
        //        printMoveAndBoard(node.gameState)
//        println("getWinnerOfRandomPlay move number before ${node.gameState.moveNumber()}")
        val randomGame = Evaluator.simulateRandomGame(node.gameState, bots)
        val winner = randomGame.winner

//                println("getWinnerOfRandomPlay  $winner  move number after ${randomGame.state.moveNumber()}")
//                printMoveAndBoard(randomGame.state)
        return winner
    }

}
