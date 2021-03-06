package at.cpickl.agrotlin.view

import android.content.Context
import at.cpickl.grotlin.Game
import android.widget.LinearLayout
import android.graphics.Color
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import java.util.Arrays
import at.cpickl.grotlin.Region
import at.cpickl.grotlin.Battle
import android.widget.Toast
import at.cpickl.agrotlin.FloatPoint
import org.slf4j.LoggerFactory

public trait GameViewListener {
    fun onSelectedView(regionView: RegionView)
    fun onDeselectedView()
}

public class GameView(
        context: Context,
        private val game: Game,
        private val map: MapView
) : LinearLayout(context) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<GameView>())
    }
    public var listener: GameViewListener? = null
    {
        setBackgroundColor(Color.parseColor("#3E3E3E"))
        setMinimumWidth(640)
        setMinimumHeight(480)

        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false
                }
                LOG.debug("onTouchEvent(event.x=" + event.getX() + ", event.y=" + event.getY() + ")")
                val selectedView = map.findRegionView(FloatPoint(event.getX(), event.getY()))
                if (selectedView == null) {
                    listener?.onDeselectedView()
                } else {
                    listener?.onSelectedView(selectedView)
                }
                return false
            }

        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        map.draw(canvas)
    }

}
