package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.agent.HelpersTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ZobristTest{


    @Test
    fun createFullTable(){

        val t = HelpersTest.crono("calculate table 19x19"){  Zobrist.calcTable(19)}

        val eb = t.getValue(Point(1,1))
        assertTrue(eb > 0)


     //   println(t)
    }

    @Test
    fun saveAndLoadSmallTable(){

        val t = Zobrist.calcTable(5)

        val json = Zobrist.saveAsJson(t)
  //      println(json)

        val t2 = Zobrist.loadFromJson(json)
        assertEquals(t.toString(), t2.toString())
    }


}