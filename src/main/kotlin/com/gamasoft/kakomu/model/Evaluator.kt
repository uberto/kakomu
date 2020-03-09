package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.Agent


object Evaluator {

        val MAX_SCORE = 999999
        val MIN_SCORE = -999999

        fun countTerritoryAndStones(board: Board, player: Player): Int {
            //count all stones and eyes, works only after all possible stones are placed
            var tot = 0

            for (col in 1..board.numCols) {
                for (row in 1..board.numRows) {
                    val point = Point.of(col, row)
                    val string = board.getString(point)
                    if (string == null) {
                        if (isAnEye(board, point, player))
                            tot++
                    } else if (string.color == player)
                        tot++
                }
            }
            return tot
        }

        fun computeGameResultFullBoard(gameState: GameState): GameResult {
            val black = countTerritoryAndStones(gameState.board, Player.BLACK)
            val white = countTerritoryAndStones(gameState.board, Player.WHITE)
            return GameResult(black, white, komi = 7.5)  //TODO komi configurable
        }


        fun isAnEye(board: Board, point: Point, color: Player): Boolean {

            //Must be empty
            if (!board.isFree(point)) {
                return false
            }

            //All adjacent points must contain friendly stones and not be capturable
            for (neighbor in board.neighbors(point)) {
                val string = board.getString(neighbor)
                if (string == null || string.color != color || string.libertiesCount() == 1)
                    return false
            }

            return true
        }

        fun isSelfCapture(board: Board, point: Point, player: Player): Boolean {
            //if one of neighbors is same color and with more than 1 liberty is not self capture
            //if one of neighbors is different color and with exactly 1 liberty is not self capture
            for (neighbor in board.neighbors(point)) {
                val string = board.getString(neighbor)
                if (string == null)
                    return false
                else {
                    val color = string.color
                    val libertiesCount = string.libertiesCount()
                    if ((color == player && libertiesCount > 1) || (color == player.other() && libertiesCount == 1))
                        return false
                }
            }

            return true
        }

        fun simulateRandomGame(game: GameState, seed: Long? = null): EndGame {

            val runner = RandomRun(game.board.numCols, seed)
            var currGame = game.clone()

            runner.run {
                while (!currGame.isOver()) {
                    currGame = randomMoveOnBoard(currGame)
                }
            }

            return EndGame(currGame)
        }

    fun simulateAutoGame(game: GameState, bots: Array<Agent>): EndGame {
        var currGame = game
        while (!currGame.isOver()) {
            currGame = bots[game.nextPlayer.toInt()].playNextMove(currGame)
        }

        return EndGame(currGame)
    }
}
