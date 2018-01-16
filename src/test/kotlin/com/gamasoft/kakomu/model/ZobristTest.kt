package com.gamasoft.kakomu.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ZobristTest{


    @Test
    fun createFullTable(){

        val z = Zobrist()

        val s = System.currentTimeMillis()
        val t =z.calcTable(19)

        println("done " + (System.currentTimeMillis() - s))

        println(t)
    }
}