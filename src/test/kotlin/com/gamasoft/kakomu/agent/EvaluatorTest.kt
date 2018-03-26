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

        val finalState = Evaluator.simulateRandomGame(startingState, bots).state
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



    @Test
    fun performanceSelfGame() {
        val boardSize = 9
        val startingState = GameState.newGame(boardSize)
        val bots = mapOf(Player.BLACK to RandomBot(boardSize), Player.WHITE to RandomBot(boardSize))
        //warmup
        for (i in (1..100)) {
            Evaluator.simulateRandomGame(startingState, bots)
        }
        for (i in (1..10)) {
            val fixedBots = mapOf(Player.BLACK to RandomBot(boardSize, 234345), Player.WHITE to RandomBot(boardSize, 767655))
            PrintTest.crono("play self game ${boardSize}x${boardSize}") {
                Evaluator.simulateRandomGame(startingState, fixedBots)
            }
        }
    }

    //on my laptop i7 2Ghz
//with validmove without deepcopy:
//about 10 msec on 9x9 and 160 on 19x19
//with immutable goStrings:
//about 6 msec on 9x9 and 65 on 19x19
//with neighbors map:
//about 3.5 msec on 9x9 and 38 on 19x19
//with faster isAnEye:
//about 1.5 msec on 9x9 and 31 on 19x19
//with simpleKo:
//about 1.3 msec on 9x9 and 27 on 19x19
//with selectMove returning State:
//about 1.25 msec on 9x9 and 25 on 19x19
//without System.nanotime:
//about 0.7 msec on 9x9 and 12 on 19x19
//with fastRandomBot:
//about 0.5 msec on 9x9 and 4.5 on 19x19
//with single swap instead of shuffle:
//about 0.33 msec on 9x9 and 2.4 on 19x19




}