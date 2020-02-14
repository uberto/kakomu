package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Player


data class BatchResult(val blackWin: Int, val whiteWin: Int) {
    fun add(player: Player): BatchResult = when (player){
        Player.BLACK -> copy(blackWin = blackWin + 1)
        Player.WHITE -> copy(whiteWin = whiteWin + 1)
    }

    val size = blackWin + whiteWin
}
