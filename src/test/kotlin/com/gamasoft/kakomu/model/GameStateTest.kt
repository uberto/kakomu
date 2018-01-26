package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GameStateTest {

    @Test
    fun isOverForPassing() {
        val start = GameState.newGame(9)

        assertFalse(start.isOver())
        assertEquals(0, start.previousStates.size)

        val first = start.applyMove(Player.BLACK, Move.Pass)
        assertFalse(first.isOver())
        assertEquals(1, first.previousStates.size)

        val second = first.applyMove(Player.WHITE, Move.Pass)
        assertTrue(second.isOver())
        assertEquals(2, second.previousStates.size)

    }

    @Test
    fun isOverForResignation() {
        val start = GameState.newGame(9)

        assertFalse(start.isOver())

        val first = start.applyMove(Player.BLACK, Move.Play(Point(5,5)))
        assertFalse(first.isOver())

        val second = first.applyMove(Player.WHITE, Move.Resign)
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
             .applyMove(Player.BLACK, Move.Play(Point(2,1)))
             .applyMove(Player.WHITE, Move.Play(Point(3,1)))
             .applyMove(Player.BLACK, Move.Play(Point(1,2)))
             .applyMove(Player.WHITE, Move.Play(Point(1,3)))

        assertTrue(autoCapt.isMoveSelfCapture(Player.WHITE, Move.Play(Point(1, 1))))

        assertFalse(autoCapt.isMoveSelfCapture(Player.BLACK, Move.Play(Point(1, 1))))

        assertTrue(autoCapt.isValidMoveIncludingSuperko(Move.Play(Point(1, 1))))

    }


    /*
    B . W
    B W .
    W . B
 */
    @Test
    fun isAutoCapture() {
        val autoCapt = GameState.newGame(9)
                .applyMove(Player.BLACK, Move.Play(Point(2,1)))
                .applyMove(Player.WHITE, Move.Play(Point(3,1)))
                .applyMove(Player.BLACK, Move.Play(Point(1,2)))
                .applyMove(Player.WHITE, Move.Play(Point(1,3)))
                .applyMove(Player.BLACK, Move.Play(Point(3,3)))
                .applyMove(Player.WHITE, Move.Play(Point(2,2)))

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
                .applyMove(Player.BLACK, Move.Play(Point(2,1)))
                .applyMove(Player.WHITE, Move.Play(Point(3,1)))
                .applyMove(Player.BLACK, Move.Play(Point(1,2)))
                .applyMove(Player.WHITE, Move.Play(Point(2,2)))
                .applyMove(Player.BLACK, Move.Play(Point(3,2)))
                .applyMove(Player.WHITE, Move.Play(Point(1,1)))  //capture

        assertTrue(koViol.doesMoveViolateKo(Player.BLACK, Move.Play(Point(2, 1))))

        assertFalse(koViol.isMoveSelfCapture(Player.BLACK, Move.Play(Point(2, 3))))

        assertFalse(koViol.isValidMoveIncludingSuperko(Move.Play(Point(1, 1))))
    }



}