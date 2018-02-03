package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GameStateTest {

    @Test
    fun previousZobrist() {
        val start = GameState.newGame(9)
        val gs1= start.applyMove(Player.BLACK, Move.Play(Point(2,1)))!!
        val gs2= gs1.applyMove(Player.WHITE, Move.Play(Point(3,1)))!!
        val gs3= gs2.applyMove(Player.BLACK, Move.Play(Point(1,2)))!!
        val gs4= gs3.applyMove(Player.WHITE, Move.Play(Point(1,3)))!!

        assertEquals(0, start.board.zobristHash())
        assertNotEquals(0, gs1.board.zobristHash())
        assertNotEquals(0, gs2.board.zobristHash())
        assertNotEquals(0, gs3.board.zobristHash())
        assertNotEquals(0, gs4.board.zobristHash())

//        assertEquals(0, start.previousStates.size)
//        assertEquals(1, gs1.previousStates.size)
//        assertEquals(2, gs2.previousStates.size)
//        assertEquals(3, gs3.previousStates.size)
//        assertEquals(4, gs4.previousStates.size)


    }


    @Test
    fun isOverForPassing() {
        val start = GameState.newGame(9)
        assertFalse(start.isOver())

        val first = start.applyMove(Player.BLACK, Move.Pass)!!
        assertFalse(first.isOver())

        val second = first.applyMove(Player.WHITE, Move.Pass)!!
        assertTrue(second.isOver())

    }

    @Test
    fun isOverForResignation() {
        val start = GameState.newGame(9)

        assertFalse(start.isOver())

        val first = start.applyMove(Player.BLACK, Move.Play(Point(5,5)))!!
        assertFalse(first.isOver())

        val second = first.applyMove(Player.WHITE, Move.Resign)!!
        assertTrue(second.isOver())

    }



    /*
    . B W
    B . .
    W . .
     */
    @Test
    fun isAutoCaptureSingle() {
        val autoCapt = GameState.newGame(9)
             .applyMove(Player.BLACK, Move.Play(Point(2,1)))!!
             .applyMove(Player.WHITE, Move.Play(Point(3,1)))!!
             .applyMove(Player.BLACK, Move.Play(Point(1,2)))!!
             .applyMove(Player.WHITE, Move.Play(Point(1,3)))!!

        assertTrue(autoCapt.isMoveSelfCapture(Player.WHITE, Move.Play(Point(1, 1))))

        assertFalse(autoCapt.isMoveSelfCapture(Player.BLACK, Move.Play(Point(1, 1))))

        assertTrue(autoCapt.isValidMoveApartFromKo(Move.Play(Point(1, 1))))
//        assertTrue(autoCapt.isValidMoveIncludingSuperko(Move.Play(Point(1, 1))))

    }


    /*
    B . W
    B W .
    W . B
 */
    @Test
    fun isAutoCapture() {
        val autoCapt = GameState.newGame(9)
                .applyMove(Player.BLACK, Move.Play(Point(2,1)))!!
                .applyMove(Player.WHITE, Move.Play(Point(3,1)))!!
                .applyMove(Player.BLACK, Move.Play(Point(1,2)))!!
                .applyMove(Player.WHITE, Move.Play(Point(1,3)))!!
                .applyMove(Player.BLACK, Move.Play(Point(3,3)))!!
                .applyMove(Player.WHITE, Move.Play(Point(2,2)))!!

        assertFalse(autoCapt.isMoveSelfCapture(Player.WHITE, Move.Play(Point(1, 1))))

        assertTrue(autoCapt.isMoveSelfCapture(Player.BLACK, Move.Play(Point(1, 1))))

    }

    /*
    . B W
    B W B
    . . .
     */
    @Test
    fun doesMoveViolateKo() {
        val koViol = GameState.newGame(9)
                .applyMove(Player.BLACK, Move.Play(Point(2,1)))!!
                .applyMove(Player.WHITE, Move.Play(Point(3,1)))!!
                .applyMove(Player.BLACK, Move.Play(Point(1,2)))!!
                .applyMove(Player.WHITE, Move.Play(Point(2,2)))!!
                .applyMove(Player.BLACK, Move.Play(Point(3,2)))!!
                .applyMove(Player.WHITE, Move.Play(Point(1,1)))!!  //capture

//        assertTrue(koViol.doesMoveViolateKo(Player.BLACK, Move.Play(Point(2, 1))))

        assertFalse(koViol.isMoveSelfCapture(Player.BLACK, Move.Play(Point(2, 3))))

        assertFalse(koViol.isValidMoveApartFromKo(Move.Play(Point(1, 1))))
//        assertFalse(koViol.isValidMoveIncludingSuperko(Move.Play(Point(1, 1))))
    }



}