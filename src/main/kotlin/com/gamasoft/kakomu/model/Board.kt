package com.gamasoft.kakomu.model

class Board (val numCols: Int, val numRows: Int){

    companion object {
        val zobristTable = Zobrist.calcTable(19)

        val empytBoardHash = Zobrist.calcEmptyBoard(zobristTable)
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
        var newString = GoString(player, setOf(point), liberties)

        // 1. Merge any adjacent strings of the same color.
        for (sameColorString in adjacentSameColor) {
            newString = newString.mergeWith(sameColorString)
        }
        for (newStringPoint in newString.stones) {
            grid[newStringPoint] = newString
        }

        //Remove empty-point hash code.
        zHash = zHash.xor(zobristTable.get(point)!!.get(null)!!)
        //Add filled point hash code.
        zHash = zHash.xor(zobristTable.get(point)!!.get(player)!!)

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

    fun deepCopy(): Board {
        val newBoard = Board(numCols, numRows)

        for ((p, s) in grid) {
            //we need to copy the GoString and its liberties because is not immutable
            newBoard.grid.put(p, s.copy(liberties = s.liberties.toMutableSet()))
        }
        newBoard.zHash = zHash
        return newBoard
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

            //Remove stone from hash
            zHash = zHash.xor(zobristTable.get(point)!!.get(string.color)!!)
            zHash = zHash.xor(zobristTable.get(point)!!.get(null)!!)

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
        var result = numCols
        result = 31 * result + numRows
        result = 31 * result + grid.hashCode()
        return result
    }

    fun zobristHash(): Long {
        return zHash
    }


}