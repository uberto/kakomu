package com.gamasoft.kakomu.model

import io.vavr.collection.HashSet
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GoStringTest {
    @Test
    fun twoStonesMerge(){
        val b = Board(9,9)

        val p1 = Point(2,2)
        val s1 = GoString(Player.BLACK, stones = setOf(p1), liberties = b.neighbors(p1))


        val p2 = Point(2,3)
        val s2 = GoString(Player.BLACK, stones = setOf(p2), liberties = b.neighbors(p2))

        val newString = s2.mergeWith(s1)

        assertEquals(2, newString.stones.size)
        assertTrue( newString.stones.contains(p1))
        assertTrue( newString.stones.contains(p2))
        assertEquals(6, newString.libertiesCount())
    }

    @Test
    fun fiveStonesMerge(){
        val b = Board(9,9)

        val p0 = Point(3,3)
        val s0 = GoString(Player.BLACK, stones = setOf(p0), liberties = b.neighbors(p0))

        val p1 = Point(3,2)
        val s1 = GoString(Player.BLACK, stones = setOf(p1), liberties = b.neighbors(p1))

        val p2 = Point(4,3)
        val s2 = GoString(Player.BLACK, stones = setOf(p2), liberties = b.neighbors(p2))

        val p3 = Point(3,4)
        val s3 = GoString(Player.BLACK, stones = setOf(p3), liberties = b.neighbors(p3))

        val p4 = Point(2,3)
        val s4 = GoString(Player.BLACK, stones = setOf(p4), liberties = b.neighbors(p4))



        val newString = s0.mergeWith(s1).mergeWith(s2).mergeWith(s3).mergeWith(s4)

        println("Final liberties ${newString.liberties}")

        assertEquals(5, newString.stones.size)
        assertTrue( newString.stones.contains(p1))
        assertTrue( newString.stones.contains(p2))
        assertTrue( newString.stones.contains(p3))
        assertTrue( newString.stones.contains(p4))
        assertTrue( newString.stones.contains(p0))
        assertEquals(8, newString.libertiesCount())
    }


}