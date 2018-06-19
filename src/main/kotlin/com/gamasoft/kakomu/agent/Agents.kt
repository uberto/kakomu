package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player

data class Agents (val black: Agent, val white: Agent) {

    fun play(player: Player, gameState: GameState): GameState {
        val agent = if (player == Player.WHITE) white else black
        return agent.playNextMove(gameState)
    }

}
