package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.COLS

data class Point(val col: Int, val row: Int) {

    companion object {
        fun fromCoords(coords: String): Point {
            val colS = coords[0].toUpperCase()
            val rowS = coords.substring(1)

            val col = COLS.indexOf(colS) + 1
            val row = rowS.toInt()
            return Point(row = row, col = col)
        }
    }

}