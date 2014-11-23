package at.cpickl.agrotlin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import at.cpickl.agrotlin.classes.Region;

public class RegionView extends View {

    public static final float RADIUS = 50.0F;
    public static final float SIZE = RADIUS * 2;
    private static final Paint ARMY_TEXT_PAINT = new Paint();
    {
        ARMY_TEXT_PAINT.setColor(Color.BLACK);
        ARMY_TEXT_PAINT.setTextSize(25.0f);
        ARMY_TEXT_PAINT.setFakeBoldText(true);
    }

    private final Region region;
    private final FloatPoint point;
    private final Paint paint = new Paint();
    private final float centerX;
    private final float centerY;

    public RegionView(Context context, final Region region, FloatPoint point) {
        super(context);
        this.region = region;
        this.point = point;
        centerX = point.x() + RADIUS;
        centerY = point.y() + RADIUS;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int color;
        if (region.getOwner() == null) {
            color = Color.WHITE;
        } else {
            color = region.getOwner().color();
        }
        paint.setColor(color);

        canvas.drawCircle(centerX, centerY, RADIUS, paint);

        if (region.getOwner() != null) {
            String text = String.valueOf(region.getArmies());
            float textWidth = ARMY_TEXT_PAINT.measureText(text);
            canvas.drawText(text, centerX - textWidth / 2, centerY + 10.0f, ARMY_TEXT_PAINT);
        }
    }

    public Region getRegion() {
        return region;
    }

    public boolean isWithinArea(FloatPoint search) {
        return search.x() >= point.x() && search.x() <= point.x() + SIZE &&
                search.y() >= point.y() && search.y() <= point.y() + SIZE;
    }
}
