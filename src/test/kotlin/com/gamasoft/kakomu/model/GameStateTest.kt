package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GameStateTest {
    @Test
    fun isOver() {
        val start = GameState.newGame(9)

        assertFalse(start.isOver())

        val first = start.applyMove(Player.BLACK, Move.pass())
        assertFalse(first.isOver())

        val second = first.applyMove(Player.WHITE, Move.pass())
        assertTrue(second.isOver())

    }

}