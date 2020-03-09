package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PointTest{

    @Test
    fun inline(){
        val p0 = Point.of(1,1)
        assertEquals(Point(257), p0)
        val p1 = Point.of(1,2)
        assertEquals(Point(258), p1)
        val p3 = Point.of(2,1)
        assertEquals(Point(513), p3)

        assertEquals(p3.row(), 2)
        assertEquals(p3.col(), 1)

    }



    @Test
    fun coordinatesLeftTopCorner(){
        val p = Point.of(1,1)
        assertEquals("A1", p.toCoords())
        assertEquals(p, Point.fromCoords("A1"))

    }

    @Test
    fun coordinatesStarPoint(){
        val p = Point.of(4,16)
        assertEquals("D16", p.toCoords())
        assertEquals(p, Point.fromCoords("D16"))

    }

    @Test
    fun coordinatesStarPointTopRight(){
        val p = Point.of(16,4)
        assertEquals("Q4", p.toCoords())
        assertEquals(p, Point.fromCoords("Q4"))

    }

    @Test
    fun coordinatesBottomRight(){
        val p = Point.of(19,19)
        assertEquals("T19", p.toCoords())
        assertEquals(p, Point.fromCoords("T19"))

    }

}

