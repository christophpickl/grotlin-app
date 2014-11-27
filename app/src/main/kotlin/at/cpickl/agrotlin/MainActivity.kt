package at.cpickl.agrotlin

import android.app.Activity
import android.os.Bundle
import at.cpickl.grotlin.Player
import android.graphics.Color
import at.cpickl.grotlin.Game
import at.cpickl.grotlin.Map
import java.util.Arrays
import at.cpickl.grotlin.RealDice
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.widget.RelativeLayout
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Context
import android.view.View
import at.cpickl.grotlin.BattleResult
import android.content.pm.ActivityInfo
import org.apache.http.client.HttpClient
import org.apache.http.impl.conn.DefaultClientConnection
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

public class MainActivity : Activity() {

    class object {
        private val LOG: Logg = Logg("MainActivity")
    }
    {
        // http://stackoverflow.com/questions/582185/android-disable-landscape-mode
        /*
        This does not absolve you of having to think about activity lifecycle events or properly
        saving/restoring state. There are plenty of things besides app rotation that can trigger
        an activity destruction/recreation, including unavoidable things like multitasking.
        There are no shortcuts; learn to use bundles and retainInstance fragments.
         */
    }

    private var pseudo: GamePseudoActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        super<Activity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (pseudo == null) {
            LOG.debug("Create new pseudo activity instance.")
            pseudo = createPseudo()
        }
        val container = findViewById(R.id.gameContainer) as ViewGroup
        container.addView(pseudo!!.gameView, AndroidUtil.centered())
    }
    override fun onDestroy() {
        LOG.info("onDestroy()")
        super.onDestroy()
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        LOG.info("onSaveInstanceState(bundle) pseudo=${pseudo}")
        super.onSaveInstanceState(bundle)

    }
    override fun onRestoreInstanceState(bundle: Bundle) {
        LOG.info("onRestoreInstanceState(bundle)")
        super.onRestoreInstanceState(bundle)

    }
    override fun onRestart() {
        LOG.info("onRestart()")
        super.onRestart()
    }
    override fun onStart() {
        LOG.info("onStart()")
        super.onStart()
    }
    override fun onStop() {
        LOG.info("onStop()")
        super.onStop()
    }

    private fun createPseudo() = GamePseudoActivity(this,
            findViewById(R.id.btnEndTurn) as Button,
            findViewById(R.id.txtCurrentPlayer) as TextView,
            findViewById(R.id.txtInfoMessage) as TextView)

    override fun onPause() {
        super.onPause()
        LOG.info("onPause()")
    }
    override fun onResume() {
        super.onResume()
        LOG.info("onResume()")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        LOG.info("onOptionsItemSelected(item=${item})")
        val id = item.getItemId()

        when (id) {
            R.id.menu_settings -> { LOG.info("Settings menu clicked"); return true }
            R.id.menu_restart -> { onMenuRestart(); return true }
            else -> throw IllegalArgumentException("Unhandled menu item ID: ${id} for menu item: ${item}")
        }

        return super.onOptionsItemSelected(item)
    }
    private fun onMenuRestart() {
        LOG.info("onMenuRestart()");
        val client = DefaultHttpClient()
        val get = HttpGet("http://10.0.1.12:8888/version")
        val response = client.execute(get)
        println("response.getStatusLine().getStatusCode()=" + response.getStatusLine().getStatusCode())
    }
}


class GamePseudoActivity(private val context: Context,
                         private val btnEndTurn: Button,
                         private val txtCurrentPlayer:TextView,
                         private val txtInfoMessage:TextView) {

    class object {
        private val LOG: Logg = Logg("GamePseudoActivity")
    }
    public val gameView: GameView
    private val game: Game
    private val mapView: MapView
    private val attackPhase: AttackPhase
    {
        val player1 = Player("Player 1", Color.RED)
        val player2 = Player("Player 2", Color.BLUE)

        val map = MiniMap()
        map.region1.ownedBy(player1, 2)
        map.region4.ownedBy(player2, 2)
        game = Game(map.map, listOf(player1, player2))
        mapView = MiniMapView(context, map)
        gameView = GameView(context, game, mapView)
        attackPhase = AttackPhase(context, game, gameView, txtInfoMessage)
                .setOnEndGameListener({ onEndGame() })
                .setOnAttackedListener { onAttacked(it) }
    }

    {
        btnEndTurn.setOnClickListener({ onEndTurn() })
        initPlayer()
    }

    private fun onAttacked(battle: BattleResult) {
        LOG.info("onAttacked(${battle})")
        if (!mapView.isAnyAttackSourceLeft(game.currentPlayer)) {
            txtInfoMessage.setText("No more attack sources left, END TURN.")
        }
    }

    private fun onEndTurn() {
        LOG.info("onEndTurn()")
        btnEndTurn.setEnabled(false)
        var dphase: DistributionPhase = DistributionPhase(context, game, gameView, {
            txtInfoMessage.setText("Distribute ${it} Unit(s)")
        }, {
            LOG.info("onEndTurn().distribute done. next player.")
            btnEndTurn.setEnabled(true)
            game.nextPlayer()
            initPlayer()
        })

        Toast.makeText(context, "Distribute Unit(s).", 5000).show()
        mapView.deselectAllRegions() // should actually call gameView, instead of mapView
        gameView.listener = dphase
    }

    private fun onEndGame() {
        LOG.info("onEndGame()")
        gameView.listener = EndGamePhase()
        txtInfoMessage.setText("Player '${game.currentPlayer.name}' won!!!")
    }

    private fun initPlayer() {
        gameView.listener = attackPhase
        txtCurrentPlayer.setText("${game.currentPlayer.name}'s turn!")
//        if (is source region available) {
           txtInfoMessage.setText("Choose source region..")
//        }
    }
    override public fun toString() = "GamePseudoActivity[game=${game}]"
}
