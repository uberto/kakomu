package com.gamasoft.kakomu.model

import com.gamasoft.kakomu.Performance.Companion.crono
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ZobristTest{


    @Test
    fun createFullTable(){

        val t = crono("calculate table 19x19"){  Zobrist.calcTable(19)}

        val eb = t.getValue(Point.of(1,1))
        assertTrue(eb > 0)

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