package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.COLS

//flyweight? alias of Int?
data class Point(val col: Int, val row: Int) {

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

        fun toCoords(point: Point): String{
            return COLS[point.col - 1] + point.row.toString()
        }
    }

}