package com.gamasoft.kakomu.model


//Any action a player can play on a turn.
//Exactly one of is_play, is_pass, is_resign will be set.

sealed class Move {

    data class Play(val point:Point) : Move()

    object Pass : Move()

    object Resign : Move()

    fun humanReadable(): String {
        return when (this) {
            Pass -> "pass"
            Resign -> "resign"
            is Play -> Point.toCoords(this.point)
        }
    }


}
