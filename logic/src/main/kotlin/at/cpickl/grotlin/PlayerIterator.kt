package at.cpickl.grotlin


class PlayerIterator(private val players: List<Player>) : Iterator<Player> {
    {
        if (players.empty) throw IllegalArgumentException("Players must not be empty!")
    }
    var currentPlayer = players.get(0)
        get() = $currentPlayer

    override fun hasNext(): Boolean {
        return players.count { it.isAlive } > 1
    }

    override fun next(): Player {
        val next = currentPlayer
        moveCursor()
        return next
    }

    private fun moveCursor() {
        val indexOfCurrent = players.indexOf(currentPlayer)
        val newIndex = if (indexOfCurrent == players.size - 1) 0 else indexOfCurrent + 1
        currentPlayer = players.get(newIndex)
    }

}
