package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.COLS


inline class Point(val pos: Int){

    fun col() = pos % 256
    fun row() = pos shr 8

    fun toCoords(): String = "${COLS[col() - 1]}${row() + 1}"

    companion object {

        fun of(row: Int, col:Int)=
            Point((row shl 8) + col)

        fun fromCoords(coords: String): Point? {

            if (coords.length < 2 || coords.length > 3)
                return null

            val colS = coords[0].toUpperCase()
            val rowS = coords.substring(1)

            val col = COLS.indexOf(colS) + 1
            val row = rowS.toIntOrNull() ?: return null

            return of(row = row, col = col)

        }

    }
}

//
//data class Point(val col: Int, val row: Int) {
//
//    fun toCoords(): String = "${COLS[col]}${row + 1}"
//
//    companion object {
//        fun fromCoords(coords: String): Point? {
//
//            if (coords.length < 2 || coords.length > 3)
//                return null
//
//            val colS = coords[0].toUpperCase()
//            val rowS = coords.substring(1)
//
//            val col = COLS.indexOf(colS) + 1
//            val row = rowS.toIntOrNull()
//            if (row == null)
//                return null
//
//            return Point.of(row = row, col = col)
//
//        }
//
//    }
//
//}