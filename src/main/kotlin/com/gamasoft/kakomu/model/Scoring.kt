package com.gamasoft.kakomu.model

enum class TerritoryEnum {BLACK, WHITE, TerritoryB, TerritoryW, Dame }

fun evaluateTerritory(board:Board): Territory {
    /*Map a board into territory and dame.

    Any points that are completely surrounded by a single color are
    counted as territory; it makes no attempt to identify even
    trivially dead groups.
    */

    val status = mutableMapOf<Point, TerritoryEnum>()

    for (r in 1.. board.numRows){
        for (c in 1.. board.numCols){
            val p = Point(r, c)
            if (p in status){
                continue //already done
            }
            val string = board.getString(p)
            if (string != null){
                status[p] = when (string.color){
                    Player.WHITE -> TerritoryEnum.WHITE
                    Player.BLACK -> TerritoryEnum.BLACK
                }
            } else {
                assignRegionToPlayer(p, board, status)
            }

        }

    }

    return Territory(status)
}

private fun assignRegionToPlayer(startingPoint: Point, board: Board, status: MutableMap<Point, TerritoryEnum>) {
    val (group, neighbours) = collectRegion(startingPoint, board)
    val fillWith = if (neighbours.size == 1) {//Completely surrended by black or white
        if (neighbours.first() == Player.WHITE)
            TerritoryEnum.TerritoryW
        else
            TerritoryEnum.TerritoryB
    } else {
        TerritoryEnum.Dame
    }
    for (pos in group) {
        status[pos] = fillWith
    }
}

fun collectRegion(startPos: Point, board: Board, visited: MutableMap<Point, Boolean> = mutableMapOf()): Pair<Set<Point>, Set<Player>> {
    //Find the contiguous section of a board containing a point.
    // Also identify all the boundary points.
    if (startPos in visited){
        return Pair(emptySet(), emptySet())
    }
    val allPoints = mutableSetOf<Point>()
    val allBorders = mutableSetOf<Player>()
    visited[startPos] = true
    val here = board.getString(startPos)?.color //can be null
    val deltas = listOf(Pair(-1,0), Pair(1,0), Pair(0, -1), Pair(0, 1))
    for (d in deltas){
        val deltaRow = d.first
        val deltaCol = d.second
        val nextPoint = Point(startPos.row + deltaRow,
                                startPos.col + deltaCol)
        if (board.isOnTheGrid(nextPoint)) {
            val neighbor = board.getString(nextPoint)?.color
            if (neighbor == here) {
                val pb = collectRegion(nextPoint, board, visited)
                allPoints.addAll(pb.first)
                allBorders.addAll(pb.second)
            } else {
                neighbor?.let { allBorders.add(it) }
            }
        }
    }
    return Pair(allPoints, allBorders)
}

fun computeGameResult(gameState: GameState): GameResult {
    val territory = evaluateTerritory(gameState.board)
    return GameResult(
            territory.numBlackTerritory + territory.numBlackStones,
            territory.numWhiteTerritory + territory.numWhiteStones,
            komi = 7.5)
}