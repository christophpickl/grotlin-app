package at.cpickl.agrotlin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import at.cpickl.grotlin.Game;
import at.cpickl.grotlin.Map;
import at.cpickl.grotlin.Player;
import at.cpickl.grotlin.RealDice;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Player player1 = new Player("Player 1", Color.RED);
        Player player2 = new Player("Player 2", Color.BLUE);

        MiniMap map = new MiniMap();
        map.getRegion1().ownedBy(player1, 2);
        map.getRegion4().ownedBy(player2, 2);

        Game game = newGame(map.getMap(), player1, player2);
        GameView gameView = new GameView(this, game, map);
        ViewContainer container = new ViewContainer(this, gameView);
        setContentView(container);
    }

    private static Game newGame(Map map, Player... players) {
        return new Game(map, Arrays.asList(players), new RealDice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("main", "onOptionsItemSelected(item=" + item + ")");
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
