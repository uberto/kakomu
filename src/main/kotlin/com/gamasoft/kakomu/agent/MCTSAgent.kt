package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Evaluator
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import java.util.concurrent.atomic.AtomicInteger


class MCTSAgent(val secondsForMove: Int, val temperature: Double, val boardSize: Int, val debug: Boolean = true) : Agent {
//colder will evaluate better but can miss completely the best move
// 1.5 is a good starting point temperature
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


    override fun playNextMove(gameState: GameState): GameState {
        printDebug("")
        printDebug("Let me think for $secondsForMove seconds...")

        val root = MCTS.Node(gameState)

        val rolls = exploreTree(root)
        printDebug("Done ${rolls} rollouts")

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
            printDebug("    considered move ${child.showMove()} with win pct $childPct on ${child.rollouts()} rollouts. Best continuation: ${child.getBestMoveSequence()} ")
        }

        if (bestPct <= 0.15) //let's do the right thing and resign if hopeless
            bestMove = GameState(gameState.board, gameState.nextPlayer, gameState.previous, Move.Resign)

        printDebug("Select move ${bestMove.lastMove?.humanReadable()} with win pct $bestPct")

        return bestMove

    }

    private fun printDebug(msg: String) {
        if (debug)
            println(msg)
    }

//        private fun exploreTree(root: MCTS.Node): Int = exploreTreeNoConcurrency(root)
    private fun exploreTree(root: MCTS.Node): Int = exploreTreeConcurrency(root)

    private fun exploreTreeNoConcurrency(root: MCTS.Node): Int {
        val i = AtomicInteger(0)
        val start = System.currentTimeMillis()
        val maxMillis = secondsForMove * 1000
        while (System.currentTimeMillis() - start < maxMillis) {
            i.incrementAndGet()
            newRolloutAndRecordWin(root)
        }
        printDebug(" ${i.get()} rollouts in ${System.currentTimeMillis() - start} millisecs")
        return i.get()
    }

    private fun exploreTreeConcurrency(root: MCTS.Node): Int {
        val i = AtomicInteger(0)
        val start = System.currentTimeMillis()
        val maxMillis = secondsForMove * 1000

        runBlocking {

            while (System.currentTimeMillis() - start < maxMillis) {
                val jobs = mutableListOf<Job>()
                repeat(100) {
                    i.incrementAndGet()
                    jobs.add(launch { newRolloutAndRecordWin(root) })
                }
                jobs.forEach{it.join()}
            }
        }
        return i.get()
    }

    private fun exploreTreeActors(root: MCTS.Node): Int {
        val i = AtomicInteger(0)
        val start = System.currentTimeMillis()
        val maxMillis = secondsForMove * 1000


        //prepare 10 actors

        val rollouters = Array(10){buildRolloutWorker(it.toString())}

        runBlocking {

            while (System.currentTimeMillis() - start < maxMillis) {
                //while free actors...
                //launch { actor.send(root) }
                            }
        }


        //kill them actor.close()

        return i.get()
    }

    fun buildRolloutWorker(id:String) = actor<RolloutMessage> {

        for (msg in channel) {
            newRolloutAndRecordWin(msg.rootNode)
            //send the response
            //val response = CompletableDeferred<Int>()
            //counter.send(GetCounter(response))

        }

        printDebug("Rollout worker $id has finished!")
//        var counter = 0 // actor state
//        for (msg in channel) { // iterate over incoming messages
//            when (msg) {
//                is IncCounter -> counter++
//                is GetCounter -> msg.response.complete(counter)
//            }
//        }
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
//        printDebug("getWinnerOfRandomPlay move number before ${node.gameState.moveNumber()}")
        val randomGame = Evaluator.simulateRandomGame(node.gameState, bots)
        val winner = randomGame.winner

//                printDebug("getWinnerOfRandomPlay  $winner  move number after ${randomGame.state.moveNumber()}")
//                printMoveAndBoard(randomGame.state)
        return winner
    }

}
