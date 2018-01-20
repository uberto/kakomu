package com.gamasoft.kakomu.model

class Board (val numCols: Int, val numRows: Int){

    companion object {
        val zobristTable = Zobrist.calcTable(19)

        val empytBoardHash = 0L
    }

    private val grid = mutableMapOf<Point, GoString>()

    private var zHash = empytBoardHash

    //it should probably return a new immutable board
    fun placeStone(player: Player, point: Point) {
        assert(isOnTheGrid(point)){ println(point)}
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

        //Update hash code.
        zHash = zHash.xor(zobristTable.getValue(Pair(player, point)))

        // 1. Merge any adjacent strings of the same color.
        var newString = GoString(player, setOf(point), liberties)
        for (sameColorString in adjacentSameColor) {
            newString = newString.mergeWith(sameColorString)
        }
        updateStringOnGrid(newString)

        //2. Reduce liberties of any adjacent strings of the opposite color.
        for (otherColorString in adjacentOppositeColor) {
            val attachedString =  otherColorString.removeLiberty(point)

            if (attachedString.liberties.size == 0) { //If now have zero liberties, remove it.
                removeString(otherColorString)
            } else {
                updateStringOnGrid(attachedString) //otherwise update the grid
            }
        }

    }

    private fun updateStringOnGrid(newString: GoString) {
        for (newStringPoint in newString.stones) {
            grid[newStringPoint] = newString
        }
    }

    fun deepCopy(): Board {
        val newBoard = Board(numCols, numRows)
        newBoard.grid.putAll(grid)
        newBoard.zHash = zHash
        return newBoard
    }

    fun isOnTheGrid(p: Point): Boolean{
        return p.row in (1 .. numRows) && p.col in (1 ..numCols)
    }


    fun isFree(point: Point) = !grid.containsKey(point)

    private fun removeString(string: GoString){
//first pass remove the string
        for (point in string.stones) {
            grid.remove(point)
            zHash = zHash.xor(zobristTable.getValue(Pair(string.color, point)))
        }
//then add the liberties around
        for (point in string.stones){
            val neighborStrings = mutableSetOf<GoString>()

            for (neighbor: Point in point.neighbors()){
                val neighborString = grid[neighbor]
                if (neighborString == null) {
                    continue
                }
                neighborStrings.add(neighborString)
            }

            for (neighborString in neighborStrings) {
                val newString = neighborString.addLiberty(point)
                updateStringOnGrid(newString)
            }
        }
    }

    fun getString(point: Point): GoString? {
        return grid[point]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (numCols != other.numCols) return false
        if (numRows != other.numRows) return false
        if (grid != other.grid) return false

        return true
    }

    override fun hashCode(): Int {
        return zHash.hashCode()
    }

    fun zobristHash(): Long {
        return zHash
    }


}