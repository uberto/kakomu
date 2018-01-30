package com.gamasoft.kakomu.model

class Board (val numCols: Int, val numRows: Int,
             val neighborsMap: Map<Point, Set<Point>> = calculateNeighborsMap(numCols, numRows)){


    //TODO adding cornersLookupTable for quick isEye


    companion object {
        val zobristTable = Zobrist.calcTable(19)

        val empytBoardHash = 0L

        fun newBoard(size: Int): Board{
            val neighbors = calculateNeighborsMap(size, size)

            return Board(size, size, neighbors)
        }

        private fun calculateNeighborsMap(numCols: Int, numRows: Int): MutableMap<Point, Set<Point>> {
            val neighbors = mutableMapOf<Point, Set<Point>>()

            for (col in 1..numCols)
                for (row in 1..numRows)
                    neighbors.getOrPut(Point(col, row)) {
                        calculateNeighbors(Point(col, row), numCols, numRows)
                    }
            return neighbors
        }

        fun calculateNeighbors(point: Point, numCols: Int, numRows: Int):Set<Point>{

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

    private val grid = Grid(numCols, numRows)

    private var zHash = empytBoardHash

    fun placeStone(player: Player, point: Point) {
        assert(isOnTheGrid(point)){ println(point)}
        assert(isFree(point))

        //0. Examine the adjacent points.
        val adjacentSameColor = mutableListOf<GoString>()
        val adjacentOppositeColor = mutableListOf<GoString>()
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
        zHash = zHash.xor(zobristTable.getValue(Zobrist.PLAYER_BLACK)) //every move it switch
        zHash = zHash.xor(zobristTable.getValue(point))

        // 1. Merge any adjacent strings of the same color.
        val newString = GoString(player, setOf(point), liberties)
                .mergeWith(adjacentSameColor)
        updateStringOnGrid(newString)

        //2. Reduce liberties of any adjacent strings of the opposite color.
        for (otherColorString in adjacentOppositeColor) {
            if (otherColorString.libertiesCount() == 1 && otherColorString.liberties.contains(point)){
                removeString(otherColorString) //if it was the only liberty, remove the string
            } else {
                val attachedString = otherColorString.removeLiberty(point)
                updateStringOnGrid(attachedString) //otherwise remove liberty from the string
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
        newBoard.grid.copyFrom(grid)
        newBoard.zHash = zHash
        return newBoard
    }

    fun isOnTheGrid(p: Point): Boolean{
        return p.row in (1 .. numRows) && p.col in (1 ..numCols)
    }


    fun neighbors(point: Point): Set<Point>{
        return neighborsMap.getValue(point)
    }

    fun isFree(point: Point) = grid[point] == null

    private fun removeString(string: GoString){
//first pass remove the string
        for (point in string.stones) {
            grid[point] = null
            zHash = zHash.xor(zobristTable.getValue(point))
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