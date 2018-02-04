package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move

//Interface for a go-playing bot.
interface Agent {
    fun playNextMove(gameState: GameState): GameState
}