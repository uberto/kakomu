package com.gamasoft.kakomu.model

import java.lang.Math.abs

data class GameResult(val black: Int, val white: Int, val komi: Double) {
    fun winner():Player{
        if (black > white + komi)
            return Player.BLACK
        else
            return Player.WHITE
    }

    fun winningMargin(): Double{
        val totalWhite = white + komi
        return abs(black - totalWhite)
    }

    override fun toString(): String {
        return "${winner()}+${winningMargin()}"
    }


}
