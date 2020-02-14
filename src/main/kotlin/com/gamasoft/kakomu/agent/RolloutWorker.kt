package com.gamasoft.kakomu.agent

data class RolloutRespMessage(val node: MCTS.Node, val batchResult: BatchResult)

data class RolloutMessage(val node: MCTS.Node)

