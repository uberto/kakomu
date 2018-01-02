package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class PlayerTest {
    @Test
    fun otherReturnTheOtherPlayer() {
        assertEquals(Player.BLACK, Player.WHITE.other())
        assertEquals(Player.WHITE, Player.BLACK.other())
    }

}