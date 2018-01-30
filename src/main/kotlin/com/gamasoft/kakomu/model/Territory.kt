package com.gamasoft.kakomu.model

data class Territory(val territoryMap: Map<Point, TerritoryEnum>) {

    val numBlackTerritory: Int
    val numWhiteTerritory: Int
    val numBlackStones: Int
    val numWhiteStones: Int
    val numDame: Int
    val damePoints: Set<Point>

    init {
        var bt = 0
        var wt = 0
        var bs = 0
        var ws = 0
        var dt = 0
        var dp = mutableSetOf<Point>()

        for ((point, status) in territoryMap){
            when (status){
                TerritoryEnum.BLACK -> bs++
                TerritoryEnum.WHITE -> ws++
                TerritoryEnum.TerritoryB -> bt++
                TerritoryEnum.TerritoryW -> wt++
                TerritoryEnum.Dame -> {
                    dt++
                    dp.add(point)
                }
            }

        }
        numBlackTerritory = bt
        numWhiteTerritory = wt
        numBlackStones = bs
        numWhiteStones = ws
        numDame = dt
        damePoints = dp
    }
}