package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.RandomRun


class RandomBot(val randomRun: RandomRun): Agent {

    override fun playNextMove(gameState: GameState): GameState = randomRun.randomMoveOnBoard(gameState.clone())
}
