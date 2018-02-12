package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.*
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

    /*
    B W . B
    B B B B
     */
    @Test
    fun notAnEyeOnTheBorder(){

        val board = Board(9,9)
        board.placeStone(Player.BLACK, Point(1, 1))
        board.placeStone(Player.WHITE, Point(2, 1))
        board.placeStone(Player.BLACK, Point(4, 1))
        board.placeStone(Player.BLACK, Point(1, 2))
        board.placeStone(Player.BLACK, Point(2, 2))
        board.placeStone(Player.BLACK, Point(3, 2))
        board.placeStone(Player.BLACK, Point(4, 2))

        val p = Point(3, 1)
        Assertions.assertTrue(board.isFree(p))
        Assertions.assertFalse(Evaluator.isAnEye(board, p, Player.BLACK))

    }

    @Test
    fun selfGame(){
        val boardSize = 19
        val startingState = GameState.newGame(boardSize)

        val bots = mapOf(Player.BLACK to RandomBot(boardSize), Player.WHITE to RandomBot(boardSize))

        val finalState = Evaluator.simulateRandomGame(startingState, bots)
        printWholeMatch(finalState)

        Assertions.assertTrue(finalState.lastMove!! is Move.Pass)
        val scoreWhite = Evaluator.countTerritoryAndStones(finalState.board, Player.WHITE)
        val scoreBlack = Evaluator.countTerritoryAndStones(finalState.board, Player.BLACK)

        println("Final score black: $scoreBlack white: $scoreWhite")
        Assertions.assertEquals(boardSize * boardSize, scoreBlack + scoreWhite)

    }


//    @Test
//    fun selfGameValuation(){
//
//        var blackWins = 0
//        for (times in 1 .. 100000) {
//
//            val finalState = playSelfGame(9, RandomBot(), RandomBot()) { _, _ -> Unit }
//            val scoreWhite = countTerritoryAndStones(finalState.board, Player.WHITE)
//            val scoreBlack = countTerritoryAndStones(finalState.board, Player.BLACK)
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