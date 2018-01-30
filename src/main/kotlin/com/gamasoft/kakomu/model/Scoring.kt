package com.gamasoft.kakomu.model




enum class TerritoryEnum {BLACK, WHITE, TerritoryB, TerritoryW, Dame }



fun evaluateTerritory(board:Board): Territory {
    /*Map a board into territory and dame.

    Any points that are completely surrounded by a single color are
    counted as territory; it makes no attempt to identify even
    trivially dead groups.
    */

    val status = mutableMapOf<Point, TerritoryEnum>()

    return Territory(status)
}
/*
    status = {}
    for r in range(1, board.num_rows + 1):
        for c in range(1, board.num_cols + 1):
            p = Point(row=r, col=c)
            if p in status:
                # Already visited this as part of a different group.
                continue
            stone = board.get(p)
            if stone is not None:
                # It's a stone.
                status[p] = board.get(p)
            else:
                group, neighbors = _collect_region(p, board)
                if len(neighbors) == 1:
                    # Completely surrounded by black or white.
                    neighbor_stone = neighbors.pop()
                    stone_str = 'b' if neighbor_stone == Player.black else 'w'
                    fill_with = 'territory_' + stone_str
                else:
                    # Dame.
                    fill_with = 'dame'
                for pos in group:
                    status[pos] = fill_with
    return Territory(status)



def _collect_region(start_pos, board, visited=None):
    """Find the contiguous section of a board containing a point. Also
    identify all the boundary points.
    """
    if visited is None:
        visited = {}
    if start_pos in visited:
        return [], set()
    all_points = [start_pos]
    all_borders = set()
    visited[start_pos] = True
    here = board.get(start_pos)
    deltas = [(-1, 0), (1, 0), (0, -1), (0, 1)]
    for delta_r, delta_c in deltas:
        next_p = Point(row=start_pos.row + delta_r, col=start_pos.col + delta_c)
        if not board.is_on_grid(next_p):
            continue
        neighbor = board.get(next_p)
        if neighbor == here:
            points, borders = _collect_region(next_p, board, visited)
            all_points += points
            all_borders |= borders
        else:
            all_borders.add(neighbor)
    return all_points, all_borders
*/

fun computeGameResult(gameState: GameState): GameResult {
    val territory = evaluateTerritory(gameState.board)
    return GameResult(
            territory.numBlackTerritory + territory.numBlackStones,
            territory.numWhiteTerritory + territory.numWhiteStones,
            komi = 7.5)
}