package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HelpersTest {

    @Test
    fun trueEyeOnTheCorner(){

        val board = Board(9,9)
        board.placeStone(Player.BLACK, Point(2, 1))
        board.placeStone(Player.BLACK, Point(2, 2))
        board.placeStone(Player.BLACK, Point(1, 2))

        assertTrue(isAnEye(board, Point(1,1), Player.BLACK))
        assertFalse(isAnEye(board, Point(1,1), Player.WHITE))

    }

    @Test
    fun drawBoard(){

        val board = Board(9,9)
        board.placeStone(Player.BLACK, Point(2, 1))
        board.placeStone(Player.BLACK, Point(2, 2))
        board.placeStone(Player.BLACK, Point(1, 2))

        val lines = drawBoard(board)

        assertEquals("  ABCDEFGHJ", lines[0])
        assertEquals("9 .........", lines[1])
        assertEquals("2 xx.......", lines[8])
        assertEquals("1 .x.......", lines[9])
    }


    @Test
    fun drawMove(){

    }

    @Test
    fun selfGame(){
        val finalState = playSelfGame(5, RandomBot(), RandomBot()) {m, g -> Unit}

        assertTrue(finalState.lastMove!!.isPass)

    }


}

