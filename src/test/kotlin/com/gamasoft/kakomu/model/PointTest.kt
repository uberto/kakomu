package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PointTest{

    @Test
    fun coordinatesLeftTopCorner(){
        val p = Point(1,1)
        assertEquals("A1", p.toCoords())
        assertEquals(p, Point.fromCoords("A1"))

    }

    @Test
    fun coordinatesStarPoint(){
        val p = Point(4,16)
        assertEquals("D16", p.toCoords())
        assertEquals(p, Point.fromCoords("D16"))

    }

    @Test
    fun coordinatesStarPointTopRight(){
        val p = Point(16,4)
        assertEquals("Q4", p.toCoords())
        assertEquals(p, Point.fromCoords("Q4"))

    }

    @Test
    fun coordinatesBottomRight(){
        val p = Point(19,19)
        assertEquals("T19", p.toCoords())
        assertEquals(p, Point.fromCoords("T19"))

    }

}

