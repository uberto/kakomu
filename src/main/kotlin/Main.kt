
import com.gamasoft.kakomu.Performance.Companion.cpuVsCpuRealGame
import com.gamasoft.kakomu.Performance.Companion.simulateRandomGames
import com.gamasoft.kakomu.agent.playAgainstHuman
import com.gamasoft.kakomu.gtp.MCTSGoEngine
import org.lisoft.gonector.GoTextProtocol
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/*

Todo:
10 runout for node
channel size about 1000
still bug in unvisited nodes!!!  (loosing/never answer is possible???)
improve the code

-
profile with flame

optimizations:
remove coroutines for threads?
Point using inline class?
Non playble positions kept updated in board (faster select next node)

 */

fun main(args : Array<String>) {
    println("hello 囲む")

    if (args.size > 0 && args[0].toLowerCase() == "gtp") {
        gtp()
    } else if (args.size > 0 && args[0].toLowerCase() == "test") {
        performanceTest()
    } else {
        playAgainstHuman(9, 1)
    }

}

fun performanceTest() {
    cpuVsCpuRealGame(10)
    simulateRandomGames(9)
}


fun gtp(){
    // Create an instance of your go engine
    val engine = MCTSGoEngine(10, 2.0)

    // Create reader and writer for standard input and output
    val reader = BufferedReader(InputStreamReader(System.`in`));
    val writer = BufferedWriter(OutputStreamWriter(System.out));

    // Run the protocol parsing loop
    val gtp = GoTextProtocol(reader, writer, engine);
    gtp.call()

}







