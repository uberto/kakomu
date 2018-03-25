package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import java.util.*

data class MCTSNode(val gameState: GameState, val parent: MCTSNode? = null) {


    val winCounts: MutableMap<Player, Int> = mutableMapOf(Pair(Player.BLACK, 0), Pair(Player.WHITE, 0))

    var rollouts = 0

    val children = mutableSetOf<MCTSNode>()

    val unvisitedMoves = gameState.allMoves() //TODO legal moves to a lazy seq


    fun addRandomChild(): MCTSNode {
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
        val newNode = MCTSNode(newGameState, this)
        children.add(newNode)
        return newNode
    }

    fun recordWin(winner: Player) {

        winCounts[winner] = 1 + winCounts[winner]!!
        rollouts += 1

//        println("winner $winner ${winCounts[winner]} $rollouts")

    }

    fun isTerminal(): Boolean {
        return gameState.isOver()
    }

    fun winningPct(player: Player): Double {
        return winCounts[player]!! / rollouts.toDouble()
    }


    fun completelyVisited(): Boolean {
        return unvisitedMoves.isEmpty()
    }

    fun getBestMoveSequence(): String {
        val bestMove = selectBestChild()
        return bestMove?.gameState?.lastMove?.humanReadable().orEmpty() + " " + bestMove?.getBestMoveSequence().orEmpty()
    }

    private fun selectBestChild(): MCTSNode? {
        var bestPct = -1.0
        var bestChild: MCTSNode? = null
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


}

/*
class MCTSNode(object):

    def __init__(self, game_state, parent=None, move=None):
        self.game_state = game_state
        self.parent = parent
        self.move = move
        self.win_counts = {
            Player.black: 0,
            Player.white: 0,
        }
        self.num_rollouts = 0
        self.children = []
        self.unvisited_moves = game_state.legal_moves()

    def add_random_child(self):
        index = random.randint(0, len(self.unvisited_moves) - 1)
        new_move = self.unvisited_moves.pop(index)
        new_game_state = self.game_state.apply_move(new_move)
        new_node = MCTSNode(new_game_state, self, new_move)
        self.children.append(new_node)
        return new_node

    def record_win(self, winner):
        self.win_counts[winner] += 1
        self.num_rollouts += 1

    def can_add_child(self):
        return len(self.unvisited_moves) > 0

    def is_terminal(self):
        return self.game_state.is_over()

    def winning_pct(self, player):
        return float(self.win_counts[player]) / float(self.num_rollouts)

*/