package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*
import java.util.*


typealias ActorChannels = Pair<Channel<RolloutMessage>, Channel<RolloutRespMessage>>

class MCTSAgent(val secondsForMove: Int, val temperature: Double, val boardSize: Int, val debugLevel: DebugLevel = DebugLevel.INFO) : Agent {
//colder will evaluate better but can miss completely the best move
// 1.5 is a good starting point temperature
//hotter will explore more moves but can mis-evaluate the most promising


//        private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, nop, rolloutsSingleThread)
//private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, nop, rolloutsParallels)
//        private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, nop, rolloutsLoops)
    private fun exploreTree(root: MCTS.Node): Int = exploreTree(root, prepareActors, rolloutsActors)


    fun selectChild(node: MCTS.Node): MCTS.Node {
        //Select a child according to the upper confidence bound for trees (UCT) metric.

        val totalRollouts = node.rollouts()
        val logRollouts = Math.log(totalRollouts.toDouble())

        var bestScore = -1.0
        var bestChild: MCTS.Node = node
        //Loop over each child.
        for (child in node.children) {
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
        printDebug(DebugLevel.INFO,"Let me think for $secondsForMove seconds...")

        val root = MCTS.Node(Point(0,0), gameState) //TODO startPoint

        val rolls = exploreTree(root)

        //Having performed as many MCTS rounds as we have time for, we
        //now pick a move.
        var bestMove: GameState = gameState
        var bestPct = -1.0
        var expectedCont = ""
        for (child in root.children.sortedBy { it.showMove() }) {
            val childPct = child.winningPct(gameState.nextPlayer)
            if (childPct > bestPct) {
                bestPct = childPct
                bestMove = child.gameState
                expectedCont = child.getBestMoveSequence()
            }
            printDebug(DebugLevel.TRACE, "    considered move ${child.showMove()} with win pct $childPct on ${child.rollouts()} rollouts. Best continuation: ${child.getBestMoveSequence()} ")
        }

        if (bestPct <= 0.25) //let's do the right thing and resign if hopeless
            bestMove = gameState.applyResign()

        printDebug(DebugLevel.DEBUG, "Done ${rolls} rollouts")
        printDebug(DebugLevel.DEBUG,"Select move ${bestMove.lastMoveDesc()} with win pct $bestPct")
        printDebug(DebugLevel.DEBUG,"Best continuation expected $expectedCont")

        return bestMove

    }

    private fun printDebug(level: DebugLevel, msg: String) {
        if (debugLevel >= level)
            println(msg)
    }

    private fun <T> exploreTree(root: MCTS.Node, init: () -> T, rolloutsMicroBatch: suspend (T, MCTS.Node) -> Int ): Int {
        var iter = 0
        val start = System.currentTimeMillis()
        val expectedEnd = start + secondsForMove * 1000
        var lastSec = start

        val context = init()

        runBlocking {

            while (System.currentTimeMillis() < expectedEnd) {
                iter += rolloutsMicroBatch(context, root)
                lastSec = updateRolloutsStatus(expectedEnd, iter, lastSec)
            }

            coroutineContext.cancelChildren()
        }
        return iter
    }

    val rolloutsSingleThread: suspend (Unit, MCTS.Node) -> Int = {_, root: MCTS.Node ->
        newRolloutAndRecordWin(root)
        1
    }

    val rolloutsParallels: suspend (Unit, MCTS.Node) -> Int = {_, root: MCTS.Node ->
        val jobs = mutableListOf<Job>()
        repeat(20) {
            jobs.add(launch { newRolloutAndRecordWin(root) })
        }
        jobs.forEach { it.join() }
        20
    }



    val rolloutsLoops: suspend (Unit, MCTS.Node) -> Int = {_, root: MCTS.Node ->
        val jobs = mutableListOf<Job>()
        val batchForWorker = 250
        val workers = 4

        repeat(workers) {
            jobs.add(launch {
                repeat(batchForWorker){newRolloutAndRecordWin(root)}
            })
        }
        jobs.forEach { it.join() }

        batchForWorker*workers
    }


    val rolloutsActors: suspend (ActorChannels, MCTS.Node) -> Int = {
        (workChannel, respChannel), root: MCTS.Node ->


        val end = System.currentTimeMillis() + 99
        var iter = 0
        while (System.currentTimeMillis() < end) {
            while (!workChannel.isFull) {
                val node = selectNextNode(root)
                workChannel.send(RolloutMessage(node))
            }

            while (!respChannel.isEmpty) {
                val resp = respChannel.receive()
                propagateResult(resp.node, resp.winner)
                iter++
            }
        }
        iter
    }

    private val nop: () -> Unit = {}

    private val prepareActors: () -> ActorChannels = {
        val workers = Runtime.getRuntime().availableProcessors() //heuristic

        val workChannel = Channel<RolloutMessage>(workers * 2)

        val respChannel = Channel<RolloutRespMessage>(workers * 2)

        (1..workers).map { buildRolloutActor("actor $it", workChannel, respChannel) }

        ActorChannels(workChannel, respChannel)
    }

    private val UPDATE_INTERVAL_MS = 1000
    private fun updateRolloutsStatus(expectedEnd: Long, iterations: Int, lastUpdate: Long): Long {
        if (System.currentTimeMillis() - lastUpdate >= UPDATE_INTERVAL_MS) {
            val remSec = (expectedEnd - lastUpdate) / UPDATE_INTERVAL_MS
            printDebug(DebugLevel.INFO,"$remSec...   runouts $iterations")
            return lastUpdate + UPDATE_INTERVAL_MS
        } else {
            return lastUpdate
        }
    }

    fun buildRolloutActor(id:String, requestChannel: ReceiveChannel<RolloutMessage>, respChannel: SendChannel<RolloutRespMessage>) = launch {

        requestChannel.consumeEach {
            val winner = getWinnerOfRandomPlay(it.node)
            respChannel.send(RolloutRespMessage(it.node, winner))
        }

        printDebug(DebugLevel.TRACE,"Actor $id has finished!")
    }

    fun buildStubActor(id:String, requestChannel: ReceiveChannel<RolloutMessage>, respChannel: SendChannel<RolloutRespMessage>) = launch {
        val rnd = Random()

        requestChannel.consumeEach {
            val winner = if (rnd.nextDouble() > 0.5) Player.WHITE else Player.BLACK
            respChannel.send(RolloutRespMessage(it.node, winner))
        }

    }

    private fun newRolloutAndRecordWin(root: MCTS.Node) {
        val node = selectNextNode(root)

        val winner = getWinnerOfRandomPlay(node)

        propagateResult(node, winner)
    }

    tailrec private fun propagateResult(node: MCTS.Node, winner: Player) {
        node.recordWin(winner)

        if (node.parent is MCTS.Node)
            propagateResult(node.parent, winner)
    }

    tailrec private fun selectNextNode(node: MCTS.Node): MCTS.Node {
        return if (node.completelyVisited() && !node.isTerminal()) {
            selectNextNode(selectChild(node))
        } else {
            node.addRandomChild()
        }
    }

    private fun getWinnerOfRandomPlay(node: MCTS.Node): Player {
        //Simulate a random game from this node.
        //        printMoveAndBoard(node.gameState)
//        printDebug("getWinnerOfRandomPlay move number before ${node.gameState.moveNumber()}")
        val randomGame = Evaluator.simulateRandomGame(node.gameState)

//                printDebug("getWinnerOfRandomPlay  $winner  move number after ${randomGame.state.moveNumber()}")
//                printMoveAndBoard(randomGame.state)
        return randomGame.winner
    }

}
