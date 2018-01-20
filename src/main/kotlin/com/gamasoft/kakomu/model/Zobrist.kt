package com.gamasoft.kakomu.model

import com.google.gson.Gson
import java.util.concurrent.ThreadLocalRandom
import com.google.gson.LongSerializationPolicy
import com.google.gson.GsonBuilder



class Zobrist {

    companion object {

        val MAX63 = 0x7fffffffffffffff

        fun calcTable(boardSize: Int): Map<Pair<Player, Point>, Long> {
            val table = mutableMapOf<Pair<Player, Point>, Long>()

            val players = setOf(Player.BLACK, Player.WHITE)

            for (row in 1..boardSize) {
                for (col in 1..boardSize) {
                    for (player in players) {
                        val code = ThreadLocalRandom.current().nextLong(0, MAX63)

                        val key = Pair(player, Point(row, col))
                        table[key] = code

                    }
                }
            }

            return table
        }

        fun saveAsJson(table: Map<Pair<Player, Point>, Long>): String {

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