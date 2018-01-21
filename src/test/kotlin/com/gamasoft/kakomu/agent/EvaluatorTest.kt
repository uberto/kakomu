package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.Point
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class EvaluatorTest{

    @Test
    fun trueEyeOnTheCorner(){

        val board = Board(9,9)
        board.placeStone(Player.BLACK, Point(2, 1))
        board.placeStone(Player.BLACK, Point(2, 2))
        board.placeStone(Player.BLACK, Point(1, 2))

        Assertions.assertTrue(Evaluator.isAnEye(board, Point(1, 1), Player.BLACK))
        Assertions.assertFalse(Evaluator.isAnEye(board, Point(1, 1), Player.WHITE))

    }

    @Test
    fun selfGame(){
        val startingState = GameState.newGame(9)

        val finalState = playSelfGame(startingState, RandomBot(), RandomBot()) {_, _ -> Unit}

        Assertions.assertTrue(finalState.lastMove!!.isPass)
        val scoreWhite = Evaluator.countTerritory(finalState.board, Player.WHITE)
        val scoreBlack = Evaluator.countTerritory(finalState.board, Player.BLACK)

        println("Final score black: $scoreBlack white: $scoreWhite")
        Assertions.assertEquals(81, scoreBlack + scoreWhite)

    }


//    @Test
//    fun selfGameValuation(){
//
//        var blackWins = 0
//        for (times in 1 .. 100000) {
//
//            val finalState = playSelfGame(9, RandomBot(), RandomBot()) { _, _ -> Unit }
//            val scoreWhite = countTerritory(finalState.board, Player.WHITE)
//            val scoreBlack = countTerritory(finalState.board, Player.BLACK)
//            if (scoreBlack - 5 > scoreWhite)
//                blackWins++
//            println("play number $times black wins $blackWins")
//
//        }
//        println("Final black wins $blackWins")
//    }
    //tengen 5,5 53531
    //3,3  50154 50394
    //4,4  52378

}