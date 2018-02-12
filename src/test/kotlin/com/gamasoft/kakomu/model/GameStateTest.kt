package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GameStateTest {

    @Test
    fun previousZobrist() {
        val start = GameState.newGame(9)
        val gs1= start.applyMove(Move.Play(Point(2,1)))!!
        val gs2= gs1.applyMove(Move.Play(Point(3,1)))!!
        val gs3= gs2.applyMove(Move.Play(Point(1,2)))!!
        val gs4= gs3.applyMove(Move.Play(Point(1,3)))!!

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

        val first = start.applyMove(Move.Pass)!!
        assertFalse(first.isOver())

        val second = first.applyMove(Move.Pass)!!
        assertTrue(second.isOver())

    }

    @Test
    fun isOverForResignation() {
        val start = GameState.newGame(9)

        assertFalse(start.isOver())

        val first = start.applyMove(Move.Play(Point(5,5)))!!
        assertFalse(first.isOver())

        val second = first.applyMove(Move.Resign)!!
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
             .applyMove( Move.Play(Point(2,1)))!!
             .applyMove(Move.Play(Point(3,1)))!!
             .applyMove(Move.Play(Point(1,2)))!!
             .applyMove(Move.Play(Point(1,3)))!!

        val point = Point(1, 1)
        assertTrue(Evaluator.isSelfCapture(autoCapt.board, point, Player.WHITE))

        assertFalse(Evaluator.isSelfCapture(autoCapt.board, point, Player.BLACK))

        assertTrue(autoCapt.isValidPointToPlay(point))
        assertTrue(autoCapt.isValidMove(Move.Play(point)))

    }


    /*
    B . W
    B W .
    W . B
 */
    @Test
    fun isAutoCapture() {
        val autoCapt = GameState.newGame(9)
                .applyMove(Move.Play(Point(2,1)))!!
                .applyMove(Move.Play(Point(3,1)))!!
                .applyMove(Move.Play(Point(1,2)))!!
                .applyMove(Move.Play(Point(1,3)))!!
                .applyMove(Move.Play(Point(3,3)))!!
                .applyMove(Move.Play(Point(2,2)))!!

        val point = Point(1, 1)
        assertFalse(Evaluator.isSelfCapture(autoCapt.board, point, Player.WHITE))

        assertTrue(Evaluator.isSelfCapture(autoCapt.board, point, Player.BLACK))

    }

    /*
    . B W
    B W B
    . . .
     */
    @Test
    fun doesMoveViolateKo() {
        val koViol = GameState.newGame(9)
                .applyMove(Move.Play(Point(2,1)))!!
                .applyMove(Move.Play(Point(3,1)))!!
                .applyMove(Move.Play(Point(1,2)))!!
                .applyMove(Move.Play(Point(2,2)))!!
                .applyMove(Move.Play(Point(3,2)))!!
                .applyMove(Move.Play(Point(1,1)))!!  //capture


        assertFalse(Evaluator.isSelfCapture(koViol.board, Point(2, 3),  Player.BLACK))

        assertFalse(koViol.isValidPointToPlay(Point(1, 1)))
        assertFalse(koViol.isValidMove(Move.Play(Point(1, 1))))
    }



}