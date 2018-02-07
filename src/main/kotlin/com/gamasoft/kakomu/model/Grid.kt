package com.gamasoft.kakomu.model

class Grid(val cols:Int, val rows:Int) {

    val matrix: Array<GoString?>

    init {
        matrix = Array(rows*cols){null}
    }

    operator fun get(point: Point): GoString? {
        val col = point.col - 1
        val row = point.row - 1
        return matrix[pointToInt(row, col)]
    }

    operator fun set(point: Point, goString: GoString?) {
        val col = point.col - 1
        val row = point.row - 1
        matrix[pointToInt(row, col)] = goString
    }

    inline private fun pointToInt(row: Int, col: Int) = row + col * rows

    fun copyFrom(grid: Grid) {
        System.arraycopy(grid.matrix, 0, matrix, 0, matrix.size)
    }


}