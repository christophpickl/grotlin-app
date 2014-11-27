package at.cpickl.agrotlin.view

import android.content.Context
import at.cpickl.grotlin.Region
import at.cpickl.agrotlin.FloatPoint
import android.view.View
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas


public class RegionView(context: Context, public val region: Region, private val upperLeft: FloatPoint) : View(context) {
    class object {
        public val RADIUS: Float = 50.0F
        public val SIZE: Float = RADIUS * 2
        private val ARMY_TEXT_PAINT = Paint()
        private val ARMY_BG_PAINT = Paint()
    }
    {
        ARMY_TEXT_PAINT.setColor(Color.WHITE)
        ARMY_TEXT_PAINT.setTextSize(40.0F)
        ARMY_TEXT_PAINT.setFakeBoldText(true)

        ARMY_BG_PAINT.setColor(Color.BLACK)
    }
    public val center: FloatPoint
    {
        center = FloatPoint(upperLeft.x + RADIUS, upperLeft.y + RADIUS)
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
        val paint = Paint()
        paint.setColor(color)

        canvas.drawCircle(center.x, center.y, RADIUS, paint)
        canvas.drawCircle(center.x, center.y, RADIUS - 10.0F, ARMY_BG_PAINT)


        if (region.owner != null) {
            val text = region.armies.toString()
            val textWidth = ARMY_TEXT_PAINT.measureText(text)
            canvas.drawText(text, center.x - textWidth / 2, center.y + 10.0.toFloat(), ARMY_TEXT_PAINT)
        }
    }

    public fun isWithinArea(search: FloatPoint): Boolean {
        return search.x >= upperLeft.x && search.x <= upperLeft.x + SIZE &&
                search.y >= upperLeft.y && search.y <= upperLeft.y + SIZE
    }

    override public fun toString(): String {
        return "RegionView[region=${region}]"
    }

}
