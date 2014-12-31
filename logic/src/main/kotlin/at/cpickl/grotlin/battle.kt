package at.cpickl.grotlin


class Battle(val attacker: Region, val defender: Region, private val dice: Dice) {

    fun fight(): BattleResult {
        if (attacker.owner == null || defender.owner == null)
            throw IllegalArgumentException("attacker and/or defender must not be null!")
        val rollAttacker = rollDicesTimes(attacker.armies);
        val rollDefender = rollDicesTimes(defender.armies);
        val winner: Player = if (rollAttacker > rollDefender) attacker.owner!! else defender.owner!!
        return BattleResult(winner, attacker, defender, rollAttacker, rollDefender)
    }

    private fun rollDicesTimes(rollCount: Int): Int {
        var result = 0
        rollCount.times { result += dice.roll() }
        return result
    }

}

data class BattleResult(val winner: Player,
                        val attackerRegion: Region, val defenderRegion: Region,
                        val attackerDiceRoll: Int, val defenderDiceRoll: Int) {
}
