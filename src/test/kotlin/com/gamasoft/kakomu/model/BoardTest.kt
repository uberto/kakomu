package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BoardTest {
    @Test
    fun isOnTheGrid() {

        val board = Board(9,9)

        assertTrue(board.isOnTheGrid(Point(5,5)))
        assertTrue(board.isOnTheGrid(Point(1,1)))
        assertTrue(board.isOnTheGrid(Point(1,9)))
        assertTrue(board.isOnTheGrid(Point(9,9)))
        assertTrue(board.isOnTheGrid(Point(9,1)))

        assertFalse(board.isOnTheGrid(Point(9,0)))
        assertFalse(board.isOnTheGrid(Point(10,3)))
        assertFalse(board.isOnTheGrid(Point(0,4)))
        assertFalse(board.isOnTheGrid(Point(5,10)))
    }

}