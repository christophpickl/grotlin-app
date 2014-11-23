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

public class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_main);

        val player1 = Player("Player 1", Color.RED)
        val player2 = Player("Player 2", Color.BLUE)

        val map = MiniMap()
        map.region1.ownedBy(player1, 2)
        map.region4.ownedBy(player2, 2)

        val game = Game(map.map, listOf(player1, player2))
        val gameView = GameView(this, game, map)
        val container = ViewContainer(this, gameView)
        setContentView(container)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("main", "onOptionsItemSelected(item=" + item + ")")
        val id = item.getItemId()

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
