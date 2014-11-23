package at.cpickl.agrotlin

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
                LOG.debug("onTouchEvent(event.x=" + event.getX() + ", event.y=" + event.getY() + ")")
                val selectedView = findRegionView(FloatPoint(event.getX(), event.getY()))
                if (selectedView == null) {
                    onDeselectedView()
                } else {
                    onSelectedView(selectedView)
                }
                return false
            }

        })
    }
    private var selectedSource: RegionView? = null

    private fun onDeselectedView() {
        if (selectedSource != null) {
            selectedSource!!.selectedAsSource = false
            selectedSource = null
        }
    }

    private fun onSelectedView(regionView: RegionView) {
        println("onSelectedView(regionView=${regionView})")
        if (selectedSource == null) {
            if (game.sourceRegionsForCurrentPlayer().contains(regionView.region)) {
                regionView.selectedAsSource = true
                selectedSource = regionView
            } else {
                LOG.debug("Selected source region is not a valid source attack region.")
            }
        } else {
//            println(selectedSource!!.region.adjacentAttackables().forEach { println("for ${selectedSource!!.region} adjacents: ${it}") })
            if (selectedSource!!.region.adjacentAttackables().contains(regionView.region)) {
                LOG.info("Attacking!")
                game.attack(selectedSource!!.region, regionView.region)
                onDeselectedView()
            } else {
                Toast.makeText(getContext(), "Region not attackable!", 500)
                println("not attackable")
            }
        }

        invalidate()
    }

    private fun findRegionView(point: FloatPoint): RegionView? {
        for (view in regionViews) {
            if (view.isWithinArea(point)) {
                LOG.debug("Found region " + view.region + " by point: " + point)
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

        private val LOG: Logg = Logg("MainActivity")

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
