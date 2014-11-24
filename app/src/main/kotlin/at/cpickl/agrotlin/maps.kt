package at.cpickl.agrotlin

import at.cpickl.grotlin.Region
import android.content.Context
import java.util.Arrays
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import at.cpickl.grotlin.Player

public class MiniMap {
    public val region1: Region = Region(label = "r1")
    public val region2: Region = Region(label = "r2")
    public val region3: Region = Region(label = "r3")
    public val region4: Region = Region(label = "r4")
    public val map: at.cpickl.grotlin.Map

    {
        map = at.cpickl.grotlin.Map(listOf(region1, region2, region3, region4))
        region1.addBidirectional(region2, region3)
        region4.addBidirectional(region2, region3)
    }

}

trait MapView {
    fun findRegionView(point: FloatPoint): RegionView?
    fun draw(canvas: Canvas)
    fun deselectAllRegions()
    fun isAnyAttackSourceLeft(owner: Player): Boolean
}

public class MiniMapView(context: Context, private val map: MiniMap) : MapView {
    class object {
        private val LOG: Logg = Logg("MiniMapView")
        private val LINE_PAINT = Paint()
    }
    {
        LINE_PAINT.setColor(Color.WHITE)
        LINE_PAINT.setStrokeWidth(8.0.toFloat())
    }
    private val regionViews: Collection<RegionView> = initRegionViews(context);


    override fun draw(canvas: Canvas) {
        for (view in regionViews) {
            for (outRegion in view.region.adjacent) {

                val outView = regionViews.first { it.region == outRegion }
                canvas.drawLine(view.center.x, view.center.y,
                        outView.center.x, outView.center.y,
                        LINE_PAINT);

            }
        }
        for (view in regionViews) {
            view.draw(canvas)
        }
    }

    override fun isAnyAttackSourceLeft(owner: Player): Boolean = attackSources(owner).isNotEmpty()

    fun attackSources(owner: Player): Collection<Region> {
        return regionViews.map { it.region}.filter { it.owner == owner && it.isPotentialAttackSource() }
    }

    override fun deselectAllRegions() {
        for (view in regionViews) {
            view.selectedAsSource = false
        }
        // invalidate??
    }

    override fun findRegionView(point: FloatPoint): RegionView? {
        for (view in regionViews) {
            if (view.isWithinArea(point)) {
                LOG.debug("Found region " + view.region + " by point: " + point)
                return view
            }
        }
        return null
    }

    private fun initRegionViews(context: Context): Collection<RegionView> {
        val padding = 30.0.toFloat()
        val marginY = 50.0.toFloat()
        val marginX = 150.0.toFloat()
        val line1y = padding
        val line2y = line1y + RegionView.SIZE + marginY
        val line3y = line2y + RegionView.SIZE + marginY
        val line1x = padding
        val line2x = line1x + RegionView.SIZE + marginX
        val line3x = line2x + RegionView.SIZE + marginX

        return Arrays.asList<RegionView>(
                RegionView(context, map.region1, FloatPoint(line1x, line2y)),
                RegionView(context, map.region2, FloatPoint(line2x, line1y)),
                RegionView(context, map.region3, FloatPoint(line2x, line3y)),
                RegionView(context, map.region4, FloatPoint(line3x, line2y))
        )
    }

}
