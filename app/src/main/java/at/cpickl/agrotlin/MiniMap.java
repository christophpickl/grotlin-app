package at.cpickl.agrotlin;

import java.util.Arrays;
import java.util.Collection;

import at.cpickl.agrotlin.classes.Map;
import at.cpickl.agrotlin.classes.Region;

public class MiniMap {
    public final Region region1 = new Region();
    public final Region region2 = new Region();
    public final Region region3 = new Region();
    public final Region region4 = new Region();
    public final Map map;

    public MiniMap() {
        map = new Map(Arrays.asList(region1, region2, region3, region4));
        region1.addOuts(region2, region3);
        region4.addOuts(region2, region3);
    }

}
