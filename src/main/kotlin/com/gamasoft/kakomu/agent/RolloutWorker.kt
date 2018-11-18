package com.gamasoft.kakomu.agent

import com.gamasoft.kakomu.model.Player

data class RolloutRespMessage(val node: MCTS.Node, val winner: Player)

data class RolloutMessage(val node: MCTS.Node)

