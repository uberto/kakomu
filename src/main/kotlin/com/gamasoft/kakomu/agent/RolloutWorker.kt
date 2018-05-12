package com.gamasoft.kakomu.agent

import kotlinx.coroutines.experimental.channels.SendChannel

typealias RolloutWorker = SendChannel<RolloutMessage>

data class RolloutMessage(val rootNode: MCTS.Node)

