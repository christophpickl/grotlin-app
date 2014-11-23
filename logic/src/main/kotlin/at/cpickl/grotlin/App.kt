package at.cpickl.grotlin

fun main(args: Array<String>) {
    val map = Simple4RegionsMap()
    val p1 = Player("p1")
    val p2 = Player("p2")
    map.r1.ownedBy(p1, 2)
    map.r4.ownedBy(p2, 2)
    App(Game(map, linkedListOf(p1, p2))).play()

    println("==================================")
    println("END")
}

class App(private val game: Game) : GameListener {


    public fun play() {
        println("Starting new GROTLIN game!")
        println()
        GameEngine(game, this).start()
        println()
        println("GAME OVER")
    }

    override fun onPlayersTurn(player: Player) {
        println()
        println("---------------- ${player.name}'s turn ----------------")
        printMap()
    }

    override fun continueAttacking(player: Player): Boolean {
        println("Do you want to continue attacking? (y/N)")
        return prompt() == "y"
    }

    override fun doAttack(player: Player): Pair<Region, Region> {
        println()
        printMap()
        val source = promptRegion("Choose your source region:", game.sourceRegionsForCurrentPlayer())
        val target = promptRegion("Choose your destination region:", source.adjacentAttackables())
        return Pair(source, target)
    }

    override fun onBattleResult(result: BattleResult) {
        println("The battle was ${if (result.winner == game.currentPlayer) "WON" else "LOST"}!")
        printMap()
    }

    override fun doDistribute(player: Player, biggestConnectedEmpireSize: Int): Region {
        return promptRegion("Distribute ${biggestConnectedEmpireSize} unit(s): ", game.distributableRegionsFor(player))
    }

    private fun printMap() {
        println(game.map.toPrettyString())
        println()
    }

    private fun promptRegion(text: String, regions: Collection<Region>): Region {
        println(text)
        printRegions(regions)
        return game.map.regionByLabel(prompt())
    }

    private fun printRegions(regions: Collection<Region>) {
        println(regions.fold("  ", {(pre, source) -> pre + " - " + source.label }))
    }

    private fun prompt(): String {
        print(">>> ")
        val line = readLine()!!
        println()
        return line
    }
}

private fun Map.regionByLabel(label: String): Region {
    return regions.first { it.label == label }
}

private fun Region.toPrettyString(): String =
        label + "=" + if (owner == null) "----" else "${owner!!.name}/${armies}"



object Contract {
    fun mustBePositive(value: Int): Int {
        if (value < 1) {
            throw RuntimeException("Value ${value} must be positive!")
        }
        return value
    }
}
