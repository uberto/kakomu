package com.gamasoft.kakomu.model


//Any action a player can play on a turn.
//Exactly one of is_play, is_pass, is_resign will be set.

sealed class Move {

    data class Play(val point:Point) : Move()

    object Pass : Move()

    object Resign : Move()

}
//data class Move private constructor (val point:Point? = null, val isPass:Boolean = false, val isResign:Boolean = false) {
//
//    companion object {
//        fun play(p: Point):Move {
//            return Move(point=p)
//        }
//
//        fun resign():Move {
//            return Move(isResign = true)
//        }
//
//        fun pass():Move {
//            return Move(isPass = true)
//        }
//
//
//    }
//
//    fun isPlay(): Boolean{
//        return point != null
//    }
//}