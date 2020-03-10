package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.Performance.Companion.simulateRandomGames
import com.gamasoft.kakomu.Performance.Companion.warmup
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

        val finalState = Evaluator.simulateRandomGame(startingState).state
        printWholeMatch(finalState)

        Assertions.assertTrue(finalState.lastMove()!! is Move.Pass)
        val scoreWhite = Evaluator.countTerritoryAndStones(finalState.board, Player.WHITE)
        val scoreBlack = Evaluator.countTerritoryAndStones(finalState.board, Player.BLACK)

        println("Final score black: $scoreBlack white: $scoreWhite")
        Assertions.assertEquals(boardSize * boardSize, scoreBlack + scoreWhite)

    }

//
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


    -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=50 -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40
    -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=10 -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:+UseJVMCICompiler
    -ea -XX:+UseConcMarkSweepGC -XX:+UnlockExperimentalVMOptions  -XX:+UseJVMCICompiler

    G1: c2 0.34  graal 0.34
    CMS: c2 0.28  graal 0.27
    Ser: c2 0.26 graal 0.25


    new pc 4Ghz median
    -Xms6g -Xmx6g -XX:+UseParallelOldGC
    graalvm rc16  0.1765
    jdk11         0.2268



    9x9 60 sec 2nd move (4gb) (runs greater is better):
    -Xms6g
    -Xmx6g
    -Xlog:gc:gc.log
    -Xlog:gc+age=trace
    -XX:-PrintGCDetails
    -XX:-PrintTenuringDistribution
    -XX:-PrintGCTimeStamps



    875932 -XX:+UseParallelOldGC
    766808 -XX:+UseParallelOldGC -XX:+UnlockExperimentalVMOptions  -XX:+UseJVMCICompiler
    430467 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC
    562714 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:ConcGCThreads=3
    600122 -XX:+UseShenandoahGC
    697724 -XX:+UseShenandoahGC -XX:+UnlockDiagnosticVMOptions -XX:ShenandoahGCHeuristics=passive
    645366 -XX:+UseG1GC
    655670 -XX:+UnlockExperimentalVMOptions  -XX:+UseJVMCICompiler -XX:+UseG1GC
    772507 -XX:+UseConcMarkSweepGC

     */
    @Test
    fun performanceSelfGame() {
        warmup(50000)

        simulateRandomGames(9)
    }


/*
-Xmx4g jvm 8
0.273 -XX:+UseParallelOldGC
0.276 -XX:+UseConcMarkSweepGC

jvm 11 18-09-2018

9x9 1000 times after 30k warmup. median (lower is better)

0.372 -XX:+UseG1GC
0.340 -XX:+UnlockExperimentalVMOptions  -XX:+UseJVMCICompiler
0.286 same with 1.8
0.277 -XX:+UseConcMarkSweepGC (doesn't work with graalvm)
0.279 -XX:+UseParallelOldGC
0.268  same with 1.8 output
0.255 -XX:+UseParallelOldGC -XX:+UnlockExperimentalVMOptions  -XX:+UseJVMCICompiler
0.239  same with 1.8 output

0.487 -XX:+UseShenandoahGC
0.366 -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC (only 10k warmup)
0.297 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC (no graal)

15/2/2020
-Xms6g -Xmx6g -XX:+UseParallelOldGC -XX:+UseJVMCICompiler
GraalVM 19.3.11

Best time 0.187801
Worst time 0.3197
Average time 0.1962556690000002
Median time 0.193885

-Xms6g -Xmx6g -XX:+UseParallelOldGC -XX:-UseJVMCICompiler
Best time 0.198384
Worst time 0.406209
Average time 0.23225534500000014
Median time 0.205275

GraalEE
-Xms6g -Xmx6g -XX:+UseParallelOldGC -XX:-UseJVMCICompiler
Best time 0.171832
Worst time 0.528295
Average time 0.2063537159999998
Median time 0.19625
*/

}