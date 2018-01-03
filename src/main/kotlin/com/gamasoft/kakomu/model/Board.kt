package com.gamasoft.kakomu.model

class Board (val numCols: Int, val numRows: Int){

    constructor(board: Board) : this(board.numCols, board.numRows){
        grid.putAll( board.grid)
    }

    private val grid = mutableMapOf<Point, GoString>()

    fun placeStone(player: Player, point: Point) {
        assert(isOnTheGrid(point))
        assert(isFree(point))

        //0. Examine the adjacent points.
        val adjacentSameColor = mutableSetOf<GoString>()
        val adjacentOppositeColor = mutableSetOf<GoString>()
        val liberties = mutableSetOf<Point>()

        for (neighbor in point.neighbors()) {
            if (!isOnTheGrid(neighbor))
                continue
            val neighborString = grid[neighbor]
            if (neighborString == null) {
                liberties.add(neighbor)
            } else if (neighborString.color == player) {
                adjacentSameColor.add(neighborString)
            } else {
                adjacentOppositeColor.add(neighborString)
            }
        }
        var newString = GoString(player, setOf(point), liberties)

        // 1. Merge any adjacent strings of the same color.
        for (sameColorString in adjacentSameColor) {
            newString = newString.mergeWith(sameColorString)
        }
        for (newStringPoint in newString.stones) {
            grid[newStringPoint] = newString
        }

        //2. Reduce liberties of any adjacent strings of the opposite color.
        for (otherColorString in adjacentOppositeColor) {
            otherColorString.removeLiberty(point)
        }

        //3. If any opposite color strings now have zero liberties, remove them.
        for (otherColorString in adjacentOppositeColor){
            if (otherColorString.liberties.size == 0) {
                removeString(otherColorString)
            }
        }

    }

    fun isOnTheGrid(p: Point): Boolean{
        return p.row in (1 .. numRows) && p.col in (1 ..numCols)
    }


    fun isFree(point: Point) = !grid.containsKey(point)

    private fun removeString(string: GoString){
        for (point in string.stones){
            //Removing a string can create liberties for other strings.
            for (neighbor: Point in point.neighbors()){
                val neighborString = grid[neighbor]
                if (neighborString == null) {
                    continue
                }
                if (neighborString != string) {
                    neighborString.addLiberty(point)
                }
            }
            grid.remove(point)
        }
    }

    fun getString(point: Point): GoString? {
        return grid[point]
    }


}