
import com.gamasoft.kakomu.Performance.Companion.cpuVsCpuRealGame
import com.gamasoft.kakomu.Performance.Companion.simulateRandomGames
import com.gamasoft.kakomu.Performance.Companion.warmup
import com.gamasoft.kakomu.agent.*
import com.gamasoft.kakomu.gtp.MCTSGoEngine
import org.lisoft.gonector.GoTextProtocol
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter


fun main(args : Array<String>) {
    println("hello 囲む")

    if (args.size > 0 && args[0].toLowerCase() == "gtp") {
        gtp()
    } else if (args.size > 0 && args[0].toLowerCase() == "test") {
        performanceTest()
    } else {
        playAgainstHuman(9, 30)
    }

}

fun performanceTest() {
    //warmup()
    cpuVsCpuRealGame()
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







