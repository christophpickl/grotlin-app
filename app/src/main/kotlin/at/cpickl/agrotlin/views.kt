package at.cpickl.agrotlin

import android.graphics.Paint
import android.content.Context
import at.cpickl.grotlin.Region
import android.view.View
import android.graphics.Color
import android.graphics.Canvas
import at.cpickl.grotlin.Map
import java.util.Arrays
import android.widget.LinearLayout
import at.cpickl.grotlin.Game
import android.view.MotionEvent
import android.util.Log
import android.app.Activity
import android.view.WindowManager
import android.view.Window
import android.os.Bundle
import at.cpickl.grotlin.Player
import android.widget.RelativeLayout
import android.view.Menu
import at.cpickl.agrotlin.R
import android.view.MenuItem
import at.cpickl.agrotlin.AndroidUtil
import android.widget.RelativeLayout.LayoutParams
import at.cpickl.grotlin.Battle

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

    public var selectedAsSource:Boolean = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val color: Int
        if (region.owner == null) {
            color = Color.WHITE
        } else if (selectedAsSource) {
            color = Color.YELLOW // MINOR derive brighter version of player's chosen color
        } else {
            color = region.owner!!.color
        }
        paint.setColor(color)

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

    override public fun toString(): String {
        return "RegionView[region=${region}]"
    }

    class object {
        public val RADIUS: Float = 50.0.toFloat()
        public val SIZE: Float = RADIUS * 2
        private val ARMY_TEXT_PAINT = Paint()
    }
}


public class MiniMap {
    public val region1: Region = Region(label = "r1")
    public val region2: Region = Region(label = "r2")
    public val region3: Region = Region(label = "r3")
    public val region4: Region = Region(label = "r4")
    public val map: Map

    {
        map = Map(listOf(region1, region2, region3, region4))
        region1.addBidirectional(region2, region3)
        region4.addBidirectional(region2, region3)
    }

}
