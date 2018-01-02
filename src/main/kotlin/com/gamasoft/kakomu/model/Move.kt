package com.gamasoft.kakomu.model


//"""Any action a player can play on a turn.
//Exactly one of is_play, is_pass, is_resign will be set.
//"""
//def __init__(self, point=None, is_pass=False, is_resign=False):
//assert (point is not None) ^ is_pass ^ is_resign
//self.point = point
//self.is_play = (self.point is not None)
//self.is_pass = is_pass
//self.is_resign = is_resign
//@classmethod
//def play(cls, point):
//"""A move that places a stone on the board."""
//return Move(point=point)
//@classmethod
//def pass_turn(cls):
//return Move(is_pass=True)
//@classmethod
//def resign(cls):
//return Move(is_resign=True)

data class Move private constructor (val point:Point? = null, val isPass:Boolean = false, val isResign:Boolean = false) {
    companion object {
        fun play(p: Point):Move {
            return Move(point=p)
        }

        fun resign():Move {
            return Move(isResign = true)
        }

        fun pass():Move {
            return Move(isPass = true)
        }


    }

    fun isPlay(): Boolean{
        return point != null
    }
}