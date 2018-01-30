package com.gamasoft.kakomu.model


//Any action a player can play on a turn.
//Exactly one of is_play, is_pass, is_resign will be set.

sealed class Move {

    data class Play(val point:Point) : Move()

    object Pass : Move()

    object Resign : Move()

}
