package at.cpickl.agrotlin

import android.graphics.Paint
import android.content.Context
import at.cpickl.agrotlin.Region
import android.view.View
import android.graphics.Color
import android.graphics.Canvas
import at.cpickl.agrotlin.Map
import java.util.Arrays
import android.widget.LinearLayout
import at.cpickl.agrotlin.Game
import android.view.MotionEvent
import android.util.Log
import android.app.Activity
import android.view.WindowManager
import android.view.Window
import android.os.Bundle
import at.cpickl.agrotlin.Player
import android.widget.RelativeLayout
import android.view.Menu
import at.cpickl.agrotlin.R
import android.view.MenuItem
import at.cpickl.agrotlin.AndroidUtil
import android.widget.RelativeLayout.LayoutParams

public class ViewContainer(context: Context, child: View) : RelativeLayout(context) {
    {
        setBackgroundColor(Color.BLACK)
        addView(child, AndroidUtil.centered())
    }
}


public class FloatPoint(public val x: Float, public val y: Float) {
    override fun toString(): String {
        return "FloatPoint[" + x + "/" + y + "]"
    }
}

public class RegionView(context: Context, public val region: Region, private val point: FloatPoint) : View(context) {
    {
        ARMY_TEXT_PAINT.setColor(Color.BLACK)
        ARMY_TEXT_PAINT.setTextSize(25.0.toFloat())
        ARMY_TEXT_PAINT.setFakeBoldText(true)
    }
    private val paint = Paint()
    private val centerX: Float
    private val centerY: Float

    {
        centerX = point.x + RADIUS
        centerY = point.y + RADIUS
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.setColor(if (region.owner == null) Color.WHITE else region.owner!!.color)

        canvas.drawCircle(centerX, centerY, RADIUS, paint)

        if (region.owner != null) {
            val text = region.armies.toString()
            val textWidth = ARMY_TEXT_PAINT.measureText(text)
            canvas.drawText(text, centerX - textWidth / 2, centerY + 10.0.toFloat(), ARMY_TEXT_PAINT)
        }
    }

    public fun isWithinArea(search: FloatPoint): Boolean {
        return search.x >= point.x && search.x <= point.x + SIZE &&
               search.y >= point.y && search.y <= point.y + SIZE
    }

    class object {
        public val RADIUS: Float = 50.0.toFloat()
        public val SIZE: Float = RADIUS * 2
        private val ARMY_TEXT_PAINT = Paint()
    }
}


public class MiniMap {
    public val region1: Region = Region()
    public val region2: Region = Region()
    public val region3: Region = Region()
    public val region4: Region = Region()
    public val map: Map

    {
        map = Map(listOf(region1, region2, region3, region4))
        region1.addOuts(region2, region3)
        region4.addOuts(region2, region3)
    }

}

public class GameView(context: Context, private val game: Game, private val map: MiniMap) : LinearLayout(context) {
    {
        LINE_PAINT.setColor(Color.GREEN)
        LINE_PAINT.setStrokeWidth(8.0.toFloat())
    }

    private val regionViews: Collection<RegionView>

    {
        this.regionViews = initRegionViews(context, map)
        setBackgroundColor(Color.LTGRAY)
        setMinimumWidth(640)
        setMinimumHeight(480)

        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false
                }
                Log.i("RegionView", "onTouchEvent(event.x=" + event.getX() + ", event.y=" + event.getY() + ")")
                val selectedView = findRegionView(FloatPoint(event.getX(), event.getY()))
                if (selectedView == null) {
                    return false
                }
                selectedView.region.ownedBy(game.players.iterator().next(), 3)
                invalidate()
                return false
            }

        })
    }

    private fun findRegionView(point: FloatPoint): RegionView? {
        for (view in regionViews) {
            if (view.isWithinArea(point)) {
                Log.d("GameView", "Found region " + view.region + " by point: " + point)
                return view
            }
        }
        return null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //        canvas.drawLine(line1x + RegionView.RADIUS, line2y + RegionView.RADIUS,
        //                line2x + RegionView.RADIUS, line1y + RegionView.RADIUS,
        //                LINE_PAINT);
        for (view in regionViews) {
            view.draw(canvas)
        }
    }

    class object {

        private val LINE_PAINT = Paint()

        private fun initRegionViews(context: Context, map: MiniMap): Collection<RegionView> {
            val padding = 30.0.toFloat()
            val marginY = 50.0.toFloat()
            val marginX = 150.0.toFloat()
            val line1y = padding
            val line2y = line1y + RegionView.SIZE + marginY
            val line3y = line2y + RegionView.SIZE + marginY
            val line1x = padding
            val line2x = line1x + RegionView.SIZE + marginX
            val line3x = line2x + RegionView.SIZE + marginY

            return Arrays.asList<RegionView>(newRegionView(context, map.region1, line1x, line2y), newRegionView(context, map.region2, line2x, line1y), newRegionView(context, map.region3, line2x, line3y), newRegionView(context, map.region4, line3x, line2y))
        }


        private fun newRegionView(context: Context, region: Region, x: Float, y: Float): RegionView {
            return RegionView(context, region, FloatPoint(x, y))
        }
    }

}
