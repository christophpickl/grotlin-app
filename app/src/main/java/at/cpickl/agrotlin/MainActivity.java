package at.cpickl.agrotlin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;

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

        Game game = new Game(Arrays.asList(player1), map.getMap());
        GameView gameView = new GameView(this, game, map);
        ViewContainer container = new ViewContainer(this, gameView);
        setContentView(container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
