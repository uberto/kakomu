package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HelpersTest {

    companion object {
        fun <T> crono(msg: String, function: () -> T): T {
            val start = System.currentTimeMillis()
            val res = function()
            val elapsed = System.currentTimeMillis() - start
            println("$msg in $elapsed millisec")
            return res
        }
    }

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


    @Test
    fun perfSelfGame(){
        val finalState = crono("play self game 9x9") {
            playSelfGame(9, RandomBot(), RandomBot()) { m, g -> Unit }
        }
        assertTrue(finalState!!.lastMove!!.isPass)

    }




}

