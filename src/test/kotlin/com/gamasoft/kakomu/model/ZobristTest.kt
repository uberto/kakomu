package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ZobristTest{


    @Test
    fun createFullTable(){

        val s = System.currentTimeMillis()
        val t = Zobrist.calcTable(19)

        println("done " + (System.currentTimeMillis() - s))

        val eb = Zobrist.calcEmptyBoard(t)
        assertTrue(eb > 0)

        println("empty board $eb")

        println(t)
    }


}