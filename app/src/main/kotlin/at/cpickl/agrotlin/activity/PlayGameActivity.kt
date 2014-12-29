package at.cpickl.agrotlin.activity

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
import android.os.AsyncTask
import org.apache.http.HttpStatus
import org.apache.http.util.EntityUtils
import android.content.Intent
import at.cpickl.agrotlin.BuildConfig
import at.cpickl.agrotlin.AndroidUtil
import at.cpickl.agrotlin.R
import at.cpickl.agrotlin.view.GameView
import at.cpickl.agrotlin.view.MiniMapView
import at.cpickl.agrotlin.service.AttackPhase
import at.cpickl.agrotlin.MiniMap
import at.cpickl.agrotlin.view.MapView
import at.cpickl.agrotlin.service.EndGamePhase
import at.cpickl.agrotlin.service.DistributionPhase
import roboguice.inject.ContentView
import roboguice.activity.RoboActivity
import roboguice.inject.InjectView
import android.widget.EditText
import org.slf4j.LoggerFactory

public class PlayGameActivity : SwirlActivity() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<PlayGameActivity>())

        public fun start(callingActivity: Activity) {
            val intent = Intent(callingActivity, javaClass<PlayGameActivity>())
            //        intent.putExtra()
            callingActivity.startActivity(intent)
        }
    }

    private var pseudo: PlayGamePseudoActivity? = null

    [InjectView(R.id.gameContainer)] private var container: ViewGroup? = null
    [InjectView(R.id.btnEndTurn)] private var btnEndTurn: Button? = null
    [InjectView(R.id.txtCurrentPlayer)] private var txtCurrentPlayer: TextView? = null
    [InjectView(R.id.txtInfoMessage)] private var txtInfoMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_play_game)

        if (pseudo == null) {
            LOG.debug("Create new pseudo activity instance.")
            pseudo = createPseudo()
        }
        container!!.addView(pseudo!!.gameView, AndroidUtil.centered())
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

    private fun createPseudo(): PlayGamePseudoActivity {
        if (btnEndTurn == null) { throw IllegalStateException("btnEndTurn == null") }
        if (txtCurrentPlayer == null) { throw IllegalStateException("txtCurrentPlayer == null") }
        if (txtInfoMessage == null) { throw IllegalStateException("txtInfoMessage == null") }
        return PlayGamePseudoActivity(this, btnEndTurn!!, txtCurrentPlayer!!, txtInfoMessage!!)
    }

    override fun onPause() {
        super.onPause()
        LOG.info("onPause()")
    }
    override fun onResume() {
        super.onResume()
        LOG.info("onResume()")
    }

}

class PlayGamePseudoActivity(private val context: Context,
                         private val btnEndTurn: Button,
                         private val txtCurrentPlayer:TextView,
                         private val txtInfoMessage:TextView) {

    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<PlayGamePseudoActivity>())
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
        game = Game("localFakeId", map.map, listOf(player1, player2))
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
