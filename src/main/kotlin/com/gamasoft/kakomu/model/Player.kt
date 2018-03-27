package com.gamasoft.kakomu.model

enum class Player {
    BLACK {
        override fun toInt() = 0
        override fun other() = WHITE
    },
    WHITE {
        override fun toInt() = 1
        override fun other() = BLACK
    };

    abstract fun other(): Player
    abstract fun toInt(): Int

}