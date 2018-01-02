package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MoveTest {
    @Test
    fun isPlay() {
        assertTrue(Move.play(Point(6,6)).isPlay())

        assertFalse(Move.resign().isPlay())

        assertFalse(Move.pass().isPlay())

    }

}