package at.cpickl.grotlin;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

@Test
public class EmpireFinderTest {
    private final Player p1 = new Player("Player 1", 0);
    private final Player p2 = new Player("Player 2", 0);
    private Simple4RegionsMap map;
    private Region r1;
    private Region r2;
    private Region r3;
    private Region r4;

    @BeforeMethod
    public void resetState() {
        map = new Simple4RegionsMap();
        r1 = map.getR1();
        r2 = map.getR2();
        r3 = map.getR3();
        r4 = map.getR4();
    }

    public void unoccupiedMapReturnsEmptyEmpire() {
        assertThat(empireFor(p1), empty());
    }

    public void oneSingleRegionBelongsToUs() {
        owned(r1, p1);
        assertEmpire(p1, r1);
    }

    public void regionOneAndFourAreNotConnected() {
        owned(r1, p1);
        owned(r4, p1);
        assertEmpire(p1, r1);
    }

    public void adjacentButDifferentPlayer() {
        owned(r1, p1);
        owned(r2, p2);
        assertEmpire(p1, r1);
    }

    public void fullMapBelongsToUs() {
        owned(r1, p1);
        owned(r2, p1);
        owned(r3, p1);
        owned(r4, p1);
        assertEmpire(p1, r1, r2, r3, r4);
    }

    private void assertEmpire(Player player, Region... regions) {
        assertThat(empireFor(player), containsInAnyOrder(regions));
    }

    private void owned(Region region, Player player) {
        region.ownedBy(player, 1);
    }

    private Collection<Region> empireFor(Player player) {
        return new EmpireFinder().biggestEmpire(player, map);
    }
}
