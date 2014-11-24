package at.cpickl.agrotlin

import android.content.Context
import at.cpickl.grotlin.Game
import android.widget.Toast
import at.cpickl.grotlin.BattleResult
import android.widget.TextView


trait GamePhase : GameViewListener {

}

class EndGamePhase : GamePhase {
    override fun onSelectedView(regionView: RegionView) {
    }

    override fun onDeselectedView() {
    }

}
class AttackPhase(val context: Context,
                  val game: Game,
                  val view: GameView,
                  val txtInfoMessage: TextView) : GamePhase {

    class object {
        private val LOG: Logg = Logg("AttackPhase")
    }
    {
        txtInfoMessage.setText("Choose source region.")
    }

    private var selectedSource: RegionView? = null
    private var onEndGameListener: () -> Unit = {}
    private var onAttackedListener: (battle: BattleResult) -> Unit = {}

    override fun onSelectedView(regionView: RegionView) {
        println("onSelectedView(regionView=${regionView})")
        if (selectedSource == null) {
            if (game.sourceRegionsForCurrentPlayer().contains(regionView.region)) {
                regionView.selectedAsSource = true
                selectedSource = regionView
                txtInfoMessage.setText("Choose target region.")
            } else {
                LOG.debug("Selected source region is not a valid source attack region.")
            }
        } else {
            //            println(selectedSource!!.region.adjacentAttackables().forEach { println("for ${selectedSource!!.region} adjacents: ${it}") })
            if (selectedSource!!.region.adjacentAttackables().contains(regionView.region)) {
                LOG.info("Attacking!")
                val battle = game.attack(selectedSource!!.region, regionView.region)
                txtInfoMessage.setText("Attack was ${if(battle.winner == game.currentPlayer) "WON" else "LOST"}.")
                onAttackedListener(battle)
                if (game.isGameOver()) {
                    onEndGameListener()
                }
                deselectCurrentRegion()
            } else {
                Toast.makeText(context, "Region not attackable!", 5000).show()
                println("not attackable")
            }
        }

        view.invalidate()
    }

    override fun onDeselectedView() {
        if (selectedSource != null) {
            deselectCurrentRegion()
        }
    }

    private fun deselectCurrentRegion() {
        selectedSource!!.selectedAsSource = false
        selectedSource = null
        txtInfoMessage.setText("Choose source region.")
        view.invalidate()
    }

    public fun setOnEndGameListener(listener: () -> Unit): AttackPhase {
        onEndGameListener = listener
        return this
    }
    public fun setOnAttackedListener(listener: (battle: BattleResult) -> Unit): AttackPhase {
        onAttackedListener = listener
        return this
    }

}

class DistributionPhase(private val context: Context,
                        private val game:Game,
                        val view: GameView,
                        val onArmiesToSpan: (armies: Int) -> Unit,
                        val onFinished: () -> Unit) : GamePhase {
    private val totalArmiesToSpawn = game.distributableRegionsFor(game.currentPlayer).size
    private var armiesSpawned = 0
    {
        onArmiesToSpan(totalArmiesToSpawn)
    }
    override fun onSelectedView(regionView: RegionView) {
        if (regionView.region.owner == game.currentPlayer && regionView.region.armies < 8) {
            regionView.region.armies++
            armiesSpawned++
            onArmiesToSpan(totalArmiesToSpawn - armiesSpawned)
            view.invalidate()

            if (armiesSpawned == totalArmiesToSpawn) {
                onFinished()
            }
        }
    }
    override fun onDeselectedView() {
        // do nothing
    }
}
