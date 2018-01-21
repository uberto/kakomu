package com.gamasoft.kakomu.model

//Stones that are linked by a chain of connected stones of the same color.

data class GoString(val color: Player, val stones: Set<Point>, val liberties: Set<Point>) {
    
    fun mergeWith(string: GoString): GoString {

        val newStones = string.stones.plus(this.stones)
        val newLiberties = string.liberties.plus(this.liberties).minus(newStones)

        return GoString(this.color, newStones, newLiberties)
    }

    fun removeLiberty(point: Point): GoString {
        return GoString(color, stones, liberties.minus(point))
    }

    fun addLiberty(point:Point): GoString{
        return GoString(color, stones, liberties.plus(point))
    }

}