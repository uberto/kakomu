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

    @Test
    fun placeStoneRemoveStonesWhenSurrounded() {

        /*
         1 2 3 8
         6 4 9 a
         5 7 .
         */
        val board = Board(4,4)

        //1
        board.placeStone(Player.BLACK,Point(1, 1))
        assertFalse(board.isFree(Point(1, 1)))
        board.placeStone(Player.WHITE,Point(2, 1))

        //3
        board.placeStone(Player.BLACK,Point(3, 1))
        board.placeStone(Player.WHITE,Point(2, 2))

        //5
        board.placeStone(Player.BLACK,Point(1, 3))
        board.placeStone(Player.WHITE,Point(1, 2))
        assertTrue(board.isFree(Point(1, 1)))

        //7
        board.placeStone(Player.BLACK,Point(2, 3))
        board.placeStone(Player.WHITE,Point(4, 1))

        //9
        board.placeStone(Player.BLACK,Point(3, 2))
        board.placeStone(Player.WHITE,Point(4, 2))

        //11
        board.placeStone(Player.BLACK,Point(1, 1))
        assertFalse(board.isFree(Point(1, 1)))
        assertTrue(board.isFree(Point(2, 2)))
        assertTrue(board.isFree(Point(1, 2)))
        assertTrue(board.isFree(Point(2, 1)))
    }


}