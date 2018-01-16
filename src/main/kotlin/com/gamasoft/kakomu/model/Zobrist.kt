package com.gamasoft.kakomu.model

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Zobrist {

    companion object {

        val MAX63 = 0x7fffffffffffffff

        fun calcTable(boardSize: Int): Map<Point, Map<Player?, Long>> {
            val table = mutableMapOf<Point, Map<Player?, Long>>()

            val states = setOf(null, Player.BLACK, Player.WHITE)

            for (row in 1..boardSize) {
                for (col in 1..boardSize) {
                    val innerMap = mutableMapOf<Player?, Long>()
                    for (state in states) {
                        val code = ThreadLocalRandom.current().nextLong(0, MAX63)
                        innerMap[state] = code

                    }
                    table[Point(row, col)] = innerMap
                }
            }

            return table
        }

        fun calcEmptyBoard(table: Map<Point, Map<Player?, Long>>): Long {

            var emptyBoard = 0L

            for (v in table.values) {
                val code = v.get(null)!!
                emptyBoard = emptyBoard.xor(code)
            }
            return emptyBoard
        }
    }

}