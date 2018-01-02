package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GoStringTest {
    @Test
    fun simplestMerge(){
        val p1 = Point(2,2)
        val s1 = GoString(Player.BLACK, stones = setOf(p1), liberties = p1.neighbors())


        val p2 = Point(2,3)
        val s2 = GoString(Player.BLACK, stones = setOf(p2), liberties = p2.neighbors())

        val newString = s2.mergeWith(s1)

        assertEquals(2, newString.stones.size)
        assertTrue( newString.stones.contains(p1))
        assertTrue( newString.stones.contains(p2))
        assertEquals(6, newString.liberties.size)
    }
}