package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Move


data class ScoreMove(val winningPct: Double, val move: Move, val rollouts: Int)
