package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*
import kotlinx.coroutines.experimental.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


class MCTSAgent(val secondsForMove: Int, val temperature: Double, val boardSize: Int, val debug: Boolean = true) : Agent {
//colder will evaluate better but can miss completely the best move
// 1.5 is a good starting point temperature
//hotter will explore more moves but can mis-evaluate the most promising


    //for concurrency
    private val currentlyEvaluatingNodes: MutableMap<Point, MCTS.Node> = ConcurrentHashMap<Point, MCTS.Node>()// mutableSetOf()

    val bots: Array<Agent>

    init {
        bots = arrayOf(RandomBot(boardSize), RandomBot(boardSize))
    }

//        private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, rolloutsWorkers)
//        private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, rolloutsSingleThread)
    private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, rolloutsParallels)


    fun selectChild(node: MCTS.Node): MCTS.Node {
        //Select a child according to the upper confidence bound for trees (UCT) metric.

        val totalRollouts = node.rollouts()
        val logRollouts = Math.log(totalRollouts.toDouble())

        var bestScore = -1.0
        var bestChild: MCTS.Node = node
        //Loop over each child.
        for (child in node.children.elements()) {
            if (child.pos in currentlyEvaluatingNodes)
                continue //skip this, another task is already looking at it

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


    override fun playNextMove(gameState: GameState): GameState {
        printDebug("Move ${gameState.moveNumber()}")
        printDebug("Let me think for $secondsForMove seconds...")

        val root = MCTS.Node(Point(0,0), gameState) //TODO startPoint

        val rolls = exploreTree(root)

        //Having performed as many MCTS rounds as we have time for, we
        //now pick a move.
        var bestMove: GameState = gameState
        var bestPct = -1.0
        var expectedCont = ""
        for (child in root.children.elements().toList().sortedBy { it.showMove() }) {
            val childPct = child.winningPct(gameState.nextPlayer)
            if (childPct > bestPct) {
                bestPct = childPct
                bestMove = child.gameState
                expectedCont = child.getBestMoveSequence()
            }
            printDebug("    considered move ${child.showMove()} with win pct $childPct on ${child.rollouts()} rollouts. Best continuation: ${child.getBestMoveSequence()} ")
        }

        if (bestPct <= 0.25) //let's do the right thing and resign if hopeless
            bestMove = gameState.applyResign()

        printDebug("Done ${rolls} rollouts")
        printDebug("Select move ${bestMove.lastMoveDesc()} with win pct $bestPct")
        printDebug("Best continuation expected $expectedCont")

        return bestMove

    }

    private fun printDebug(msg: String) {
        if (debug)
        println(msg)

    }

    private fun exploreTree(root: MCTS.Node, rolloutsMicroBatch: suspend (MCTS.Node) -> Int ): Int {
        var iter = 0
        val start = System.currentTimeMillis()
        val expectedEnd = start + secondsForMove * 1000
        var lastSec = start

        runBlocking {

            while (System.currentTimeMillis() < expectedEnd) {

                iter += rolloutsMicroBatch(root)

                lastSec = updateRolloutsStatus(expectedEnd, iter, lastSec)
            }
        }
        return iter
    }




    val rolloutsSingleThread: suspend (MCTS.Node) -> Int = { root: MCTS.Node ->
        newRolloutAndRecordWin(root)
        1
    }

    val rolloutsParallels: suspend (MCTS.Node) -> Int = {root: MCTS.Node ->
        val jobs = mutableListOf<Job>()
        repeat(20) {
            jobs.add(launch { newRolloutAndRecordWin(root) })
        }
        jobs.forEach { it.join() }
        20
    }


    val rolloutsWorkers: suspend (MCTS.Node) -> Int = {root: MCTS.Node ->
        val jobs = mutableListOf<Job>()
        repeat(4) {
            jobs.add(launch {
                repeat(200){newRolloutAndRecordWin(root)}
            })
        }
        jobs.forEach { it.join() }

        400
    }


    private val UPDATE_INTERVAL_MS = 1000

    private fun updateRolloutsStatus(expectedEnd: Long, iterations: Int, lastUpdate: Long): Long {
        if (System.currentTimeMillis() - lastUpdate >= UPDATE_INTERVAL_MS) {
            val remSec = (expectedEnd - lastUpdate) / UPDATE_INTERVAL_MS
            printDebug("$remSec...   runouts $iterations")
            return lastUpdate + UPDATE_INTERVAL_MS
        } else {
            return lastUpdate
        }
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
        currentlyEvaluatingNodes.remove(node.pos)

    }

    private fun selectNextNode(root: MCTS.Node): MCTS.Node {
        var node = root

//        synchronized(this) {
            while (node.completelyVisited() && !node.isTerminal()) {
                node = selectChild(node)
            }
            node = node.addRandomChild()
            currentlyEvaluatingNodes.put(node.pos, node)
//        }
        return node
    }

    private fun getWinnerOfRandomPlay(node: MCTS.Node): Player {
        //Simulate a random game from this node.
        //        printMoveAndBoard(node.gameState)
//        printDebug("getWinnerOfRandomPlay move number before ${node.gameState.moveNumber()}")
        val randomGame = Evaluator.simulateRandomGame(node.gameState, bots)
        val winner = randomGame.winner

//                printDebug("getWinnerOfRandomPlay  $winner  move number after ${randomGame.state.moveNumber()}")
//                printMoveAndBoard(randomGame.state)
        return winner
    }

}
