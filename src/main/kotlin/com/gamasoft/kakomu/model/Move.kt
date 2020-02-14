package com.gamasoft.kakomu.model

sealed class Move {

    data class Play(val point:Point) : Move()

    object Pass : Move()

    object Resign : Move()

    fun humanReadable(): String {
        return when (this) {
            Pass -> "pass"
            Resign -> "resign"
            is Play -> this.point.toCoords()
        }
    }
}
