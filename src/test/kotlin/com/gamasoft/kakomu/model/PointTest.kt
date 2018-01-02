package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PointTest {
    @Test
    fun neighborsOfAPoint() {
        val n = Point(3,5).neighbors()

        assertEquals(4, n.size)
        assertTrue(n.contains(Point(3,4)))
        assertTrue(n.contains(Point(3,6)))
        assertTrue(n.contains(Point(2,5)))
        assertTrue(n.contains(Point(4,5)))

    }

}