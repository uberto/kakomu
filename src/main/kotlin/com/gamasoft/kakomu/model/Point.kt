package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.COLS

//flyweight? alias of Int?
data class Point(val col: Int, val row: Int) {

    fun toCoords(): String = "${COLS[col]}${row + 1}"

    companion object {
        fun fromCoords(coords: String): Point? {

            if (coords.length < 2 || coords.length > 3)
                return null

            val colS = coords[0].toUpperCase()
            val rowS = coords.substring(1)

            val col = COLS.indexOf(colS) + 1
            val row = rowS.toIntOrNull()
            if (row == null)
                return null

            return Point(row = row, col = col)

        }

    }

}