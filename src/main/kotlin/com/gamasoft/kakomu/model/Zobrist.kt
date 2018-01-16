package com.gamasoft.kakomu.model

import com.google.gson.Gson
import java.util.concurrent.ThreadLocalRandom
import com.google.gson.LongSerializationPolicy
import com.google.gson.GsonBuilder



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

        fun saveAsJson(table: Map<Point, Map<Player?, Long>>): String {

            val gsonBuilder = GsonBuilder()
            gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING)
            gsonBuilder.disableHtmlEscaping()
            val gson = gsonBuilder.create()

            return gson.toJson(table)
        }

        fun loadFromJson(json: String): Map<*,*> {
            val gson = Gson()
            return gson.fromJson(json, Map::class.java)
        }

    }

}