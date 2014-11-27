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
