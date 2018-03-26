package com.gamasoft.kakomu.model

enum class Player {
    WHITE {
        override fun toInt() = 1
        override fun other() = BLACK
    },

    BLACK {
        override fun other() = WHITE
        override fun toInt() = 0
    };

    abstract fun other(): Player
    abstract fun toInt(): Int

}