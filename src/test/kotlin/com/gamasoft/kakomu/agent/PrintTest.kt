package com.gamasoft.kakomu.agent

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PrintTest {



    @Test
    fun drawBoard(){

        val board = Board(9,9)
        board.placeStone(Player.BLACK, Point.of(2, 1))
        board.placeStone(Player.BLACK, Point.of(2, 2))
        board.placeStone(Player.BLACK, Point.of(1, 2))

        val lines = drawBoard(board)

        assertEquals("   A B C D E F G H J ", lines[0])
        assertEquals(" 9 . . . . . . . . .  9", lines[1])
        assertEquals(" 2 x x . . . . . . .  2", lines[8])
        assertEquals(" 1 . x . . . . . . .  1", lines[9])
    }


    @Test
    fun drawMoveResign(){
        val resign = drawMove(Player.BLACK, "resigns")

        assertEquals("BLACK resigns", resign)
    }

    @Test
    fun drawMove(){
        val tenten = drawMove(Player.WHITE, Move.Play(Point.of(10, 10)).humanReadable())

        assertThat(tenten).isEqualTo("WHITE K10")
    }

    @Test
    fun pointFromCoords(){
        val p1 = Point.fromCoords("A1")
        assertEquals(Point.of(1,1), p1)
        val p23 = Point.fromCoords("B3")
        assertEquals(Point.of(2,3), p23)

        assertEquals(Point.of(2,3), p23)

        val p2 = Point.fromCoords("c7")
        assertEquals(Point.of(3,7), p2)
        val p3 = Point.fromCoords("Z17")
        assertEquals(Point.of(25,17), p3)
    }

}

