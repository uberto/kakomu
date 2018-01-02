package com.gamasoft.kakomu.model

data class Point(val col: Int, val row: Int) {

    fun neighbors(): Set<Point> {
        return setOf(
                    Point(col, row - 1),
                    Point(col, row + 1),
                    Point(col - 1, row),
                    Point(col + 1, row)
                )

    }
}