package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BoardTest {

    @Test
    fun isOnTheGrid() {

        val board = Board(9,9)

        assertTrue(board.isOnTheGrid(Point.of(5,5)))
        assertTrue(board.isOnTheGrid(Point.of(1,1)))
        assertTrue(board.isOnTheGrid(Point.of(1,9)))
        assertTrue(board.isOnTheGrid(Point.of(9,9)))
        assertTrue(board.isOnTheGrid(Point.of(9,1)))

        assertFalse(board.isOnTheGrid(Point.of(9,0)))
        assertFalse(board.isOnTheGrid(Point.of(10,3)))
        assertFalse(board.isOnTheGrid(Point.of(0,4)))
        assertFalse(board.isOnTheGrid(Point.of(5,10)))
    }

    @Test
    fun neighborsOfAPoint() {
        val p1 = Point.of(3,5)
        val b = Board(9,9)
        val n = b.neighbors(p1)

        assertEquals(4, n.size)
        assertTrue(n.contains(Point.of(3,4)))
        assertTrue(n.contains(Point.of(3,6)))
        assertTrue(n.contains(Point.of(2,5)))
        assertTrue(n.contains(Point.of(4,5)))

    }

    @Test
    fun neighborsOfCorners() {
        val p1 = Point.of(3,5)
        val b = Board(9,9)

        assertEquals(2, b.neighbors(Point.of(1,1)).size)
        assertEquals(2, b.neighbors(Point.of(1,9)).size)
        assertEquals(2, b.neighbors(Point.of(9,1)).size)
        assertEquals(2, b.neighbors(Point.of(9,9)).size)

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
        board.placeStone(Player.BLACK,Point.of(1, 1))
        board.placeStone(Player.BLACK,Point.of(2, 2))
        board.placeStone(Player.BLACK,Point.of(3, 1))
        board.placeStone(Player.BLACK,Point.of(2, 3))

        assertEquals(2, board.getString(Point.of(1, 1))!!.libertiesCount())
        assertEquals(3, board.getString(Point.of(3, 1))!!.libertiesCount())
        assertEquals(6, board.getString(Point.of(2, 2))!!.libertiesCount())
        assertEquals(2, board.getString(Point.of(2, 3))!!.stones.size)


        board.placeStone(Player.BLACK,Point.of(2, 1))
        val goString = board.getString(Point.of(2, 1))!!
        assertEquals(6, goString.libertiesCount())
        assertEquals(5, goString.stones.size)
        assertEquals(goString, board.getString(Point.of(1, 1))!!)
        assertEquals(goString, board.getString(Point.of(3, 1))!!)
        assertEquals(goString, board.getString(Point.of(2, 2))!!)
        assertEquals(goString, board.getString(Point.of(2, 3))!!)
    }


    @Test
    fun removeLibertiesWhenAttached() {
        /*
         B1 B2 B3 .
         .  W4 .
         */
        val board = Board(9,9)
        board.placeStone(Player.BLACK,Point.of(1, 1))
        board.placeStone(Player.BLACK,Point.of(2, 1))
        board.placeStone(Player.BLACK,Point.of(3, 1))
        assertEquals(4, board.getString(Point.of(1, 1))!!.libertiesCount())


        board.placeStone(Player.WHITE,Point.of(2, 2))
        assertEquals(3, board.getString(Point.of(2, 1))!!.libertiesCount())
        assertEquals(3, board.getString(Point.of(2, 2))!!.libertiesCount())
    }

    @Test
    fun addLibertiesWhenCapture() {
        /*
         B1 B3 W4 B5
         W2 W6 .  .
         .  .  .  .
         */
        val board = Board(9,9)
        board.placeStone(Player.BLACK,Point.of(1, 1))
        assertEquals(2, board.getString(Point.of(1, 1))!!.libertiesCount())
        board.placeStone(Player.WHITE,Point.of(1, 2))
        assertEquals(1, board.getString(Point.of(1, 1))!!.libertiesCount())
        board.placeStone(Player.BLACK,Point.of(2, 1))
        assertEquals(2, board.getString(Point.of(1, 1))!!.libertiesCount())
        board.placeStone(Player.WHITE,Point.of(3, 1))
        assertEquals(1, board.getString(Point.of(1, 1))!!.libertiesCount())
        board.placeStone(Player.BLACK,Point.of(4, 1))
        assertEquals(2, board.getString(Point.of(1, 2))!!.libertiesCount())


        board.placeStone(Player.WHITE,Point.of(2, 2))
        assertTrue(board.isFree(Point.of(1, 1)))
        assertTrue(board.isFree(Point.of(2, 1)))

        assertEquals(5, board.getString(Point.of(2, 2))!!.libertiesCount())
        assertEquals(2, board.getString(Point.of(3, 1))!!.libertiesCount())
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
        board.placeStone(Player.BLACK,Point.of(1, 1))
        assertFalse(board.isFree(Point.of(1, 1)))
        board.placeStone(Player.WHITE,Point.of(2, 1))

        //3
        board.placeStone(Player.BLACK,Point.of(3, 1))
        board.placeStone(Player.WHITE,Point.of(2, 2))

        //5
        board.placeStone(Player.BLACK,Point.of(1, 3))
        board.placeStone(Player.WHITE,Point.of(1, 2))
        assertTrue(board.isFree(Point.of(1, 1)))

        //7
        board.placeStone(Player.BLACK,Point.of(2, 3))
        board.placeStone(Player.WHITE,Point.of(4, 1))

        //9
        board.placeStone(Player.BLACK,Point.of(3, 2))
        board.placeStone(Player.WHITE,Point.of(4, 2))

        //11
        board.placeStone(Player.BLACK,Point.of(1, 1))
        assertFalse(board.isFree(Point.of(1, 1)))
        assertTrue(board.isFree(Point.of(2, 2)))
        assertTrue(board.isFree(Point.of(1, 2)))
        assertTrue(board.isFree(Point.of(2, 1)))
    }

    @Test
    fun deepCopyDuplicateTheGoStrings() {

        val board1 = Board(4,4)
        board1.placeStone(Player.BLACK, Point.of(1, 1))
        board1.placeStone(Player.BLACK, Point.of(2, 1))

        val board2 = board1.clone()
        val string2 = board2.getString(Point.of(1, 1))!!
        assertEquals(3, string2.libertiesCount())

        board1.placeStone(Player.WHITE, Point.of(1, 2))

        val string1 = board1.getString(Point.of(1, 1))!!
        assertEquals(2, string1.libertiesCount())
        assertEquals(2, string1.stones.size)

        assertEquals(3, string2.libertiesCount())
        assertEquals(2, string2.stones.size)

    }
}