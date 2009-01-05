package javax.time.scale;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 23-Dec-2008
 * Time: 20:32:05
 * To change this template use File | Settings | File Templates.
 */
@Test
public class TestJulianDays {

    public void testJulianDayNumber() {
        assertEquals(Scale.julianDayNumber(2008, 12, 23), 2454824);
    }

    public void testModifiedJulianDay() {
        assertEquals(Scale.modifiedJulianDay(1858, 11, 17), 0);
        assertEquals(Scale.modifiedJulianDay(1970, 1, 1), 40587);
        assertEquals(Scale.modifiedJulianDay(1972, 1, 1), 41317);
        assertEquals(Scale.modifiedJulianDay(1958, 1, 1), 36204);
        assertEquals(Scale.modifiedJulianDay(1995, 9, 27), 49987);
    }
}
