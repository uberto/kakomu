package com.gamasoft.kakomu.model

//Stones that are linked by a chain of connected stones of the same color.

data class GoString(val color: Player, val stones: Set<Point>, val liberties: Set<Point>) {

    fun mergeWith(strings: List<GoString>): GoString {
        val newStones = stones.toMutableSet()
        val newLiberties = liberties.toMutableSet()

        for (string in strings){
            newStones.addAll(string.stones)
            newLiberties.addAll(string.liberties)
        }
        newLiberties.removeAll(newStones)

        return GoString(this.color, newStones, newLiberties)
    }


    fun removeLiberty(point: Point): GoString {
        return GoString(color, stones, liberties.minus(point))
    }

    fun addLiberty(point:Point): GoString{
        return GoString(color, stones, liberties.plus(point))
    }

    fun libertiesCount(): Int {
        return liberties.size
    }

}
