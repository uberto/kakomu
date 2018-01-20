package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*
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
    fun drawMoveResign(){
        val resign = drawMove(Player.BLACK, Move.resign())

        assertEquals("BLACK resigns", resign)
    }

    @Test
    fun drawMove(){
        val tenten = drawMove(Player.WHITE, Move.play(Point(10, 10)))

        assertEquals("WHITE K10", tenten)
    }

    @Test
    fun selfGame(){
        val finalState = playSelfGame(5, RandomBot(), RandomBot()) {_, _ -> Unit}

        assertTrue(finalState.lastMove!!.isPass)
    }


    @Test
    fun perfSelfGame(){
        val boardSize = 19
        //warmup
        for ( i in (1..1000)) {
            val finalState = playSelfGame(boardSize, RandomBot(), RandomBot()) { _, _ -> Unit }
            assertTrue(finalState.lastMove!!.isPass)

        }
        for ( i in (1..10)) {
            val finalState = crono("play self game ${boardSize}x${boardSize}") {
                playSelfGame(boardSize, RandomBot(), RandomBot()) { _, _ -> Unit }
            }
            assertTrue(finalState.lastMove!!.isPass)
        }
    }
//with validmove without deepcopy:
//about 10 msec on 9x9 and 160 on 19x19
//with immutable goStrings:
//about 6 msec on 9x9 and 65 on 19x19


}

