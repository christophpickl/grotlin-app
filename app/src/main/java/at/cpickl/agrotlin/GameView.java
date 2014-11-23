package at.cpickl.agrotlin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Collection;

import at.cpickl.agrotlin.classes.Game;
import at.cpickl.agrotlin.classes.Region;

public class GameView extends LinearLayout {

    private static final Paint LINE_PAINT = new Paint();

    {
        LINE_PAINT.setColor(Color.GREEN);
        LINE_PAINT.setStrokeWidth(8.0f);
    }

    private final Game game;
    private final MiniMap map;

    private final Collection<RegionView> regionViews;

    public GameView(Context context, final Game game, final MiniMap map) {
        super(context);
        this.game = game;
        this.map = map;
        this.regionViews = initRegionViews(context, map);
        setBackgroundColor(Color.LTGRAY);
        setMinimumWidth(640);
        setMinimumHeight(480);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                Log.i("RegionView", "onTouchEvent(event.x=" + event.getX() + ", event.y=" + event.getY() + ")");
                RegionView selectedView = findRegionView(new FloatPoint(event.getX(), event.getY()));
                if (selectedView == null) {
                    return false;
                }
                selectedView.getRegion().ownedBy(game.getPlayers().iterator().next(), 3);
                invalidate();
                return false;
            }

        });
    }

    private static Collection<RegionView> initRegionViews(Context context, MiniMap map) {
        float padding = 30.0f;
        float marginY = 50.0f;
        float marginX = 150.0f;
        float line1y = padding;
        float line2y = line1y + RegionView.SIZE + marginY;
        float line3y = line2y + RegionView.SIZE + marginY;
        float line1x = padding;
        float line2x = line1x + RegionView.SIZE + marginX;
        float line3x = line2x + RegionView.SIZE + marginY;

        return Arrays.asList(newRegionView(context, map.region1, line1x, line2y),
                newRegionView(context, map.region2, line2x, line1y),
                newRegionView(context, map.region3, line2x, line3y),
                newRegionView(context, map.region4, line3x, line2y)
        );
    }

    private RegionView findRegionView(FloatPoint point) {
        for (RegionView view : regionViews) {
            if (view.isWithinArea(point)) {
                Log.d("GameView", "Found region " + view.getRegion() + " by point: " + point);
                return view;
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawLine(line1x + RegionView.RADIUS, line2y + RegionView.RADIUS,
//                line2x + RegionView.RADIUS, line1y + RegionView.RADIUS,
//                LINE_PAINT);
        for (RegionView view : regionViews) {
            view.draw(canvas);
        }
    }


    private static RegionView newRegionView(Context context, Region region, float x, float y) {
        return new RegionView(context, region, new FloatPoint(x, y));
    }

}
