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
    fun mergeStringsWhenUnited() {
        /*
         B1 .  B3 .
         .  B2 .  .
         .  B4 .
         .  .  .
         */
        val board = Board(9,9)
        board.placeStone(Player.BLACK,Point(1, 1))
        board.placeStone(Player.BLACK,Point(2, 2))
        board.placeStone(Player.BLACK,Point(3, 1))
        board.placeStone(Player.BLACK,Point(2, 3))

        assertEquals(2, board.getString(Point(1, 1))!!.liberties.size)
        assertEquals(3, board.getString(Point(3, 1))!!.liberties.size)
        assertEquals(6, board.getString(Point(2, 2))!!.liberties.size)
        assertEquals(2, board.getString(Point(2, 3))!!.stones.size)


        board.placeStone(Player.BLACK,Point(2, 1))
        val goString = board.getString(Point(2, 1))!!
        assertEquals(6, goString.liberties.size)
        assertEquals(5, goString.stones.size)
        assertEquals(goString, board.getString(Point(1, 1))!!)
        assertEquals(goString, board.getString(Point(3, 1))!!)
        assertEquals(goString, board.getString(Point(2, 2))!!)
        assertEquals(goString, board.getString(Point(2, 3))!!)
    }


    @Test
    fun removeLibertiesWhenAttached() {
        /*
         B1 B2 B3 .
         .  W4 .
         */
        val board = Board(9,9)
        board.placeStone(Player.BLACK,Point(1, 1))
        board.placeStone(Player.BLACK,Point(2, 1))
        board.placeStone(Player.BLACK,Point(3, 1))
        assertEquals(4, board.getString(Point(1, 1))!!.liberties.size)


        board.placeStone(Player.WHITE,Point(2, 2))
        assertEquals(3, board.getString(Point(2, 1))!!.liberties.size)
        assertEquals(3, board.getString(Point(2, 2))!!.liberties.size)
    }

    @Test
    fun addLibertiesWhenCapture() {
        /*
         B1 B3 W4 B5
         W2 W6 .  .
         .  .  .  .
         */
        val board = Board(9,9)
        board.placeStone(Player.BLACK,Point(1, 1))
        board.placeStone(Player.WHITE,Point(1, 2))
        board.placeStone(Player.BLACK,Point(2, 1))
        board.placeStone(Player.WHITE,Point(3, 1))
        board.placeStone(Player.BLACK,Point(4, 1))
        assertEquals(1, board.getString(Point(1, 1))!!.liberties.size)
        assertEquals(2, board.getString(Point(1, 2))!!.liberties.size)


        board.placeStone(Player.WHITE,Point(2, 2))
        assertTrue(board.isFree(Point(1, 1)))
        assertTrue(board.isFree(Point(2, 1)))

        assertEquals(5, board.getString(Point(2, 2))!!.liberties.size)
        assertEquals(2, board.getString(Point(3, 1))!!.liberties.size)
    }


    @Test
    fun removeStonesWhenSurrounded() {

        /*
         B1 W2 B3 W8
         W6 W4 B9 W10
         B5 B7 .
         */
        val board = Board(9,9)

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

    @Test
    fun deepCopyDuplicateTheGoStrings() {

        val board1 = Board(4,4)
        board1.placeStone(Player.BLACK, Point(1, 1))
        board1.placeStone(Player.BLACK, Point(2, 1))

        val board2 = board1.clone()
        val string2 = board2.getString(Point(1, 1))!!
        assertEquals(3, string2.liberties.size)

        board1.placeStone(Player.WHITE, Point(1, 2))

        val string1 = board1.getString(Point(1, 1))!!
        assertEquals(2, string1.liberties.size)
        assertEquals(2, string1.stones.size)

        assertEquals(3, string2.liberties.size)
        assertEquals(2, string2.stones.size)

    }
}