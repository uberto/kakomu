package com.gamasoft.kakomu.model

class Board (val numCols: Int, val numRows: Int,
             val neighborsMap: MutableMap<Point, Set<Point>> = mutableMapOf()
){

    companion object {
        val zobristTable = Zobrist.calcTable(19)

        val empytBoardHash = 0L

        fun newBoard(size: Int): Board{
            val neighbors = mutableMapOf<Point, Set<Point>> ()

            for (col in 1.. size)
                for (row in 1..size)
                    neighbors.getOrPut(Point(col, row)){
                        calculateNeighbors(Point(col, row), size, size)}

            return Board(size, size, neighbors)
        }

        fun calculateNeighbors(point: Point, numRows: Int, numCols: Int):Set<Point>{

            fun isOnTheGrid(p: Point): Boolean{
                return p.row in (1 .. numRows) && p.col in (1 ..numCols)
            }

            val col = point.col
            val row = point.row

            val p1 = Point(col, row - 1)
            val p2 = Point(col, row + 1)
            val p3 = Point(col - 1, row)
            val p4 = Point(col + 1, row)

            val set = mutableSetOf<Point>()
            if (isOnTheGrid(p1))
                set.add(p1)
            if (isOnTheGrid(p2))
                set.add(p2)
            if (isOnTheGrid(p3))
                set.add(p3)
            if (isOnTheGrid(p4))
                set.add(p4)

            return set
        }


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

        for (neighbor in neighbors(point)) {
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

    fun clone(): Board {
        val newBoard = Board(numCols, numRows, neighborsMap)
        newBoard.grid.putAll(grid)
        newBoard.zHash = zHash
        return newBoard
    }

    fun isOnTheGrid(p: Point): Boolean{
        return p.row in (1 .. numRows) && p.col in (1 ..numCols)
    }


    fun neighbors(point: Point): Set<Point>{
        return neighborsMap.getOrPut(point){ calculateNeighbors(point, numRows, numCols)}
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

            for (neighbor: Point in neighbors(point)){
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