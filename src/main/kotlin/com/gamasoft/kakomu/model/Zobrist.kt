package com.gamasoft.kakomu.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import java.util.concurrent.ThreadLocalRandom


class Zobrist {

    companion object {

        val MAX63 = 0x7fffffffffffffff

        val PLAYER_BLACK = Point.of(-1, -1)

        fun calcTable(boardSize: Int): Map<Point, Long> {
            val table = mutableMapOf<Point, Long>()

            val players = setOf(Player.BLACK, Player.WHITE)

            for (row in 1..boardSize) {
                for (col in 1..boardSize) {
                    for (player in players) {
                        val code = ThreadLocalRandom.current().nextLong(0, MAX63)

                        table[Point.of(row, col)] = code
                    }
                }
            }

            table[PLAYER_BLACK] = ThreadLocalRandom.current().nextLong(0, MAX63)


            return table
        }

        fun saveAsJson(table: Map<Point, Long>): String {

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