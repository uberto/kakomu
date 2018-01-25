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
    fun drawBoard(){

        val board = Board(9,9)
        board.placeStone(Player.BLACK, Point(2, 1))
        board.placeStone(Player.BLACK, Point(2, 2))
        board.placeStone(Player.BLACK, Point(1, 2))

        val lines = drawBoard(board)

        assertEquals("  A B C D E F G H J ", lines[0])
        assertEquals("9 . . . . . . . . . 9", lines[1])
        assertEquals("2 x x . . . . . . . 2", lines[8])
        assertEquals("1 . x . . . . . . . 1", lines[9])
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
    fun pointFromCoords(){
        val p1 = Point.fromCoords("A1")
        assertEquals(Point(1,1), p1)
        val p2 = Point.fromCoords("c7")
        assertEquals(Point(3,7), p2)
        val p3 = Point.fromCoords("Z17")
        assertEquals(Point(25,17), p3)
    }

    @Test
    fun performanceSelfGame(){
        val boardSize = 19
        val startingState = GameState.newGame(boardSize)

        //warmup
        for ( i in (1..100)) {
            val finalState = playSelfGame(startingState, RandomBot(), RandomBot()) { _, _ -> Unit }
            assertTrue(finalState.lastMove!!.isPass)

        }
        for ( i in (1..10)) {
            val finalState = crono("play self game ${boardSize}x${boardSize}") {
                playSelfGame(startingState, RandomBot(143342332), RandomBot(443233456)) { _, _ -> Unit }
            }
            assertTrue(finalState.lastMove!!.isPass)
        }
    }
//on laptop i7 2Ghz
//with validmove without deepcopy:
//about 10 msec on 9x9 and 160 on 19x19
//with immutable goStrings:
//about 6 msec on 9x9 and 65 on 19x19
//with neighbors map:
//about 4 msec on 9x9 and 45 on 19x19


}

