package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState

//Interface for a go-playing bot.
interface Agent {

    fun playNextMove(gameState: GameState): GameState
}