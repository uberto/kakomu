package com.gamasoft.kakomu.model

data class EndGame(val state: GameState){

    val winner:Player

    init{
        assert(state.isOver())
        winner = if (state.lastMove is Move.Resign)
            state.nextPlayer
        else
            Evaluator.computeGameResultFullBoard(state).winner()
    }

}
