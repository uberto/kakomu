package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.Evaluator
import com.gamasoft.kakomu.model.Player
import com.gamasoft.kakomu.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class EvaluatorTest{

    companion object {
        fun <T> crono(msg: String, function: () -> T): T {
            val start = System.nanoTime()
            val res = function()
            val elapsed = (System.nanoTime() - start) / 1000000.0
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



    /*
    W W W W . .
    W B B W W .
    W B . B W W
    W B B . B W
    . W B B B W
    . W W W W W
     */
    @Test
    fun trueEyesWithSingleStone(){

        val board = Board(9,9)
        board.placeStone(Player.WHITE, Point(2, 2))
        board.placeStone(Player.WHITE, Point(2, 3))
        board.placeStone(Player.WHITE, Point(2, 4))
        board.placeStone(Player.WHITE, Point(2, 5))

        board.placeStone(Player.WHITE, Point(3, 2))
        board.placeStone(Player.BLACK, Point(3, 3))
        board.placeStone(Player.BLACK, Point(3, 4))
        board.placeStone(Player.BLACK, Point(3, 5))
        board.placeStone(Player.WHITE, Point(3, 6))
        board.placeStone(Player.WHITE, Point(3, 7))

        board.placeStone(Player.WHITE, Point(4, 2))
        board.placeStone(Player.BLACK, Point(4, 3))
        board.placeStone(Player.BLACK, Point(4, 5))
        board.placeStone(Player.BLACK, Point(4, 6))
        board.placeStone(Player.WHITE, Point(4, 7))

        board.placeStone(Player.WHITE, Point(5, 2))
        board.placeStone(Player.WHITE, Point(5, 3))
        board.placeStone(Player.BLACK, Point(5, 4))
        board.placeStone(Player.BLACK, Point(5, 6))
        board.placeStone(Player.WHITE, Point(5, 7))

        board.placeStone(Player.WHITE, Point(6, 3))
        board.placeStone(Player.WHITE, Point(6, 4))
        board.placeStone(Player.BLACK, Point(6, 5))
        board.placeStone(Player.BLACK, Point(6, 6))
        board.placeStone(Player.WHITE, Point(6, 7))

        board.placeStone(Player.WHITE, Point(7, 4))
        board.placeStone(Player.WHITE, Point(7, 5))
        board.placeStone(Player.WHITE, Point(7, 6))
        board.placeStone(Player.WHITE, Point(7, 7))

        val p1 = Point(4, 4)
        assert(board.isFree(p1))
        assert(Evaluator.isAnEye(board, p1, Player.BLACK))

        val p2 = Point(5, 5)
        assert(board.isFree(p2))
        assert(Evaluator.isAnEye(board, p2, Player.BLACK))

    }

    @Test
    fun selfGame(){
        val boardSize = 9
        val startingState = GameState.newGame(boardSize)
        val bots: Array<Agent> = arrayOf(RandomBot(boardSize), RandomBot(boardSize))

        val finalState = Evaluator.simulateRandomGame(startingState, bots).state
        printWholeMatch(finalState)

        Assertions.assertTrue(finalState.lastMove()!! is Move.Pass)
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
        val boardSize = 19
        val startingState = GameState.newGame(boardSize)
        val bots: Array<Agent> = arrayOf(RandomBot(boardSize), RandomBot(boardSize))
        //warmup
        for (i in (1..30000)) {
            Evaluator.simulateRandomGame(startingState, bots)
        }
        for (i in (1..10)) {
            val fixedBots: Array<Agent> = arrayOf(RandomBot(boardSize, 234345), RandomBot(boardSize, 767655))

            crono("play self game ${boardSize}x${boardSize}") {
                Evaluator.simulateRandomGame(startingState, fixedBots)
            }
        }
    }

/*

on my laptop i7 2Ghz (in millisec on 9x9 and 19x19) OpenJvm 1.8
10     160   validmove without deepcopy
6       65   immutable goStrings
3.5     38   neighbors map
1.5     31   faster isAnEye
1.3     27   simpleKo
1.25    25   selectMove returning MoveChain
0.7     12   remove System.nanotime
0.5    4.5   RandomBot evaluating single move
0.33   2.4   single swap instead of shuffle
0.27   2.0   array instead of map for winCount
0.26   1.8   no board in gameState

//JVM 10
0.33   2.3  G1
0.28   1.8  -XX:+UseConcMarkSweepGC

 */



}