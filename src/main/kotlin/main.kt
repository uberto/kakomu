import com.gamasoft.kakomu.agent.Agent
import com.gamasoft.kakomu.agent.RandomBot
import com.gamasoft.kakomu.agent.printBoard
import com.gamasoft.kakomu.agent.printMove
import com.gamasoft.kakomu.model.GameState
import com.gamasoft.kakomu.model.Player

fun main(args : Array<String>) {
    println( "hello 囲む")

    val boardSize = 9
    var game = GameState.newGame(boardSize)
    val bots = mapOf(Player.BLACK to RandomBot(),
            Player.WHITE to RandomBot())

    while(!game.isOver()){
        Thread.sleep(300)
        //print(27.toChar() + "[2J")
        printBoard(game.board)
        val botMove = bots[game.nextPlayer]!!.selectMove(game)
        printMove(game.nextPlayer, botMove)
        game = game.applyMove(game.nextPlayer, botMove)

    }
}
