package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Evaluator
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player


class MCTSAgent(val numRounds: Int, val temperature: Double, val boardSize: Int): Agent {
//1.5 is a good starting point temperature
//hotter will explore more not so promising moves
//colder will evaluate better most promising moves
    val bots: Map<Player, Agent>

    init {
        bots = mapOf<Player, Agent>(
                Player.BLACK to RandomBot(boardSize),
                Player.WHITE to RandomBot(boardSize))
    }


    fun selectChild(node: MCTSNode): MCTSNode {
        //Select a child according to the upper confidence bound for trees (UCT) metric.

        val totalRollouts = node.children.sumBy { c -> c.rollouts }
        val logRollouts = Math.log(totalRollouts.toDouble())

        var bestScore = -1.0
        var bestChild: MCTSNode? = null
        //Loop over each child.
        for (child in node.children) {
            // Calculate the UCT score.
            val winPercentage = child.winningPct(node.gameState.nextPlayer)
            val explorationFactor = Math.sqrt(logRollouts / child.rollouts)
            val uctScore = winPercentage + temperature * explorationFactor
            // Check if this is the largest we've seen so far.
            if (uctScore > bestScore) {
                bestScore = uctScore
                bestChild = child
            }
        }

        return bestChild!!
    }

    override fun playNextMove(gameState: GameState): GameState {

        val root = MCTSNode(gameState)

        for (i in 1..numRounds) {
            var node = root

            while (!node.canAddChild() && !node.isTerminal()) {
                node = selectChild(node)
            }

            //Add a new child node into the tree.
            if (node.canAddChild()) {
                node = node.addRandomChild()
            }

            //Simulate a random game from this node.
            val winner = Evaluator.simulateRandomGame(node.gameState, bots)

            var parent: MCTSNode? = node
            //Propagate scores back up the tree.
            while (parent != null) {
                parent.recordWin(winner)
                parent = parent.parent
            }

            if (i % 5000 == 0)
                println(i)
        }

//        val scoredMoves = root.children.map { c -> ScoreMove(c.winningPct(gameState.nextPlayer), c.gameState.lastMove!!, c.rollouts ) }
//        scoredMoves.sortedBy { sm -> sm.winningPct }
//                    .take(10)
//                    .forEach {println("analysis $it")}

        //Having performed as many MCTS rounds as we have time for, we
        //now pick a move.
        var bestMove: GameState = gameState
        var bestPct = -1.0
        for (child in root.children) {
            val childPct = child.winningPct(gameState.nextPlayer)
            if (childPct > bestPct) {
                bestPct = childPct
                bestMove = child.gameState
            }
            println("    considered move ${child.gameState.lastMove!!.humanReadable()} with win pct $childPct")
        }

        if (bestPct <= 0.15) //let's do the right thing
            bestMove = GameState(gameState.board, gameState.nextPlayer,gameState.previous, Move.Resign)

        println("Select move ${bestMove.lastMove!!.humanReadable()} with win pct $bestPct")
        return bestMove

    }
}

/*
    def __init__(self, ):
        self.num_rounds = num_rounds
        self.temperature = temperature

    def select_move(self, game_state):
        root = MCTSNode(game_state)

        for i in range(self.num_rounds):
            node = root
            while (not node.can_add_child()) and (not node.is_terminal()):
                node = self.select_child(node)

            # Add a new child node into the tree.
            if node.can_add_child():
                node = node.add_random_child()

            # Simulate a random game from this node.
            winner = self.simulate_random_game(node.game_state)

            # Propagate scores back up the tree.
            while node is not None:
                node.record_win(winner)
                node = node.parent

        scored_moves = [
            (child.winning_pct(game_state.next_player), child.move, child.num_rollouts)
            for child in root.children
        ]
        scored_moves.sort(key=lambda x: x[0], reverse=True)
        for s, m, n in scored_moves[:10]:
            print('%s - %.3f (%d)' % (m, s, n))

        # Having performed as many MCTS rounds as we have time for, we
        # now pick a move.
        best_move = None
        best_pct = -1.0
        for child in root.children:
            child_pct = child.winning_pct(game_state.next_player)
            if child_pct > best_pct:
                best_pct = child_pct
                best_move = child.move
        print('Select move %s with win pct %.3f' % (best_move, best_pct))
        return best_move

    def select_child(self, node):
        """Select a child according to the upper confidence bound for
        trees (UCT) metric.
        """
        total_rollouts = sum(child.num_rollouts for child in node.children)
        log_rollouts = math.log(total_rollouts)

        best_score = -1
        best_child = None
        # Loop over each child.
        for child in node.children:
            # Calculate the UCT score.
            win_percentage = child.winning_pct(node.game_state.next_player)
            exploration_factor = math.sqrt(log_rollouts / child.num_rollouts)
            uct_score = win_percentage + self.temperature * exploration_factor
            # Check if this is the largest we've seen so far.
            if uct_score > best_score:
                best_score = uct_score
                best_child = child
        return best_child


    def simulate_random_game(self, game):
        bots = {
            Player.black: agent.FastRandomBot(),
            Player.white: agent.FastRandomBot(),
        }
        while not game.is_over():
            bot_move = bots[game.next_player].select_move(game)
            game = game.apply_move(bot_move)
        return game.winner()

*/