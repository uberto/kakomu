package com.gamasoft.kakomu.model

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Zobrist {

    /*
    MAX63 = 0x7fffffffffffffff
    table = {}
    empty_board = 0
    for row in range(1, 20):
        for col in range(1, 20):
            for state in (None, Player.black, Player.white):
                code = random.randint(0, MAX63)
                table[Point(row, col), state] = code
                if state is None:
                    empty_board ^= code


     */

    val MAX63 = 0x7fffffffffffffff

    fun calcTable(boardSize: Int): Map<Point, Map<Player?, Long>> {
        val table = mutableMapOf<Point, Map<Player?, Long>>()
        var emptyBoard = 0L
        val states = setOf(null, Player.BLACK, Player.WHITE)

        for (row in 1 .. boardSize){
            for (col in 1 .. boardSize){
                val innerMap = mutableMapOf<Player?, Long>()
                for (state in states){
                    val code = ThreadLocalRandom.current().nextLong(0, MAX63)
                    innerMap[state] = code
                    if (state == null)
                        emptyBoard = emptyBoard.xor(code)
                }
                table[Point(row, col)] = innerMap
            }
        }

        return table
    }
}