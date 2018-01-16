import com.gamasoft.kakomu.agent.*
import com.gamasoft.kakomu.model.Board
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Move
import com.gamasoft.kakomu.model.Player

fun main(args : Array<String>) {
    println("hello 囲む")

    val boardSize = 9

    val printState: (Move, GameState)->Unit =  {
        move, game ->
            Thread.sleep(100)
            printBoard(move, game) //TODO make it lambda

    }

    playSelfGame(boardSize, RandomBot(), RandomBot(), printState)

}








