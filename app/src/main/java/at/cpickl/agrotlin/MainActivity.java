package at.cpickl.agrotlin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.RelativeLayout;

import java.util.Arrays;

import at.cpickl.agrotlin.classes.Game;
import at.cpickl.agrotlin.classes.Player;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Player player1 = new Player("Player 1", Color.RED);
        Player player2 = new Player("Player 2", Color.BLUE);

        MiniMap map = new MiniMap();
        map.region1.ownedBy(player1, 2);
        map.region4.ownedBy(player2, 2);

        Game game = new Game(Arrays.asList(player1), map.map);
        GameView gameView = new GameView(this, game, map);
        ViewContainer container = new ViewContainer(this, gameView);
        setContentView(container);
    }

    public static class ViewContainer extends RelativeLayout {

        public ViewContainer(Context context, View child) {
            super(context);
            RelativeLayout.LayoutParams centerLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            centerLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
            setBackgroundColor(Color.BLACK);
            addView(child, centerLayout);
        }
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
