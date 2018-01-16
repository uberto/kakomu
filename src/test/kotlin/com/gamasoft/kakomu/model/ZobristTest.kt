package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.HelpersTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ZobristTest{


    @Test
    fun createFullTable(){

        val s = System.currentTimeMillis()
        val t = HelpersTest.crono("calculate table 19x19"){  Zobrist.calcTable(19)}

        val eb = Zobrist.calcEmptyBoard(t)
        assertTrue(eb > 0)

        println("empty board $eb")

        println(t)
    }


}