package javax.time.scale;

import org.testng.annotations.Test;

import javax.time.TimeScale;
import javax.time.Instant;
import static javax.time.scale.TestScale.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 24-Dec-2008
 * Time: 20:12:52
 * To change this template use File | Settings | File Templates.
 */
@Test
public class TestTimeScale {
    public void testForName() {
        System.out.println(TimeScale.getAvailableNames());
        assertEquals(TimeScale.forName("TAI"), TAI.SCALE);
        assertEquals(TimeScale.forName("UTC"), UTC.SCALE);
        assertEquals(TimeScale.forName("TrueUTC"), TrueUTC.SCALE);
        for (String name: TimeScale.getAvailableNames()) {
            assertEquals(TimeScale.forName(name).getName(), name);
        }
    }
}
