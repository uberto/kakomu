package com.gamasoft.kakomu.model

enum class Player {
    WHITE {
        override fun other() = BLACK
    },

    BLACK {
        override fun other() = WHITE
    };

    abstract fun other(): Player
}