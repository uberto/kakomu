package com.gamasoft.kakomu.model

//Stones that are linked by a chain of connected stones of the
//same color.

data class GoString(val color: Player, val stones: Set<Point>, val liberties: MutableSet<Point>) {

    fun mergeWith(string: GoString): GoString {

        assert(string.color == this.color)
        val newStones = string.stones.union(this.stones)
        val newLiberties = mutableSetOf<Point>()
        newLiberties.addAll(string.liberties.union(this.liberties).minus(newStones))

        return GoString(this.color, newStones, newLiberties)
    }

    fun removeLiberty(point: Point) {
        liberties.remove(point)
    }

    fun addLiberty(point:Point){
        liberties.add(point)
    }

}