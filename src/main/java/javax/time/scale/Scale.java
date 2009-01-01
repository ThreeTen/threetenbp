package javax.time.scale;

/** Julian Days, and other utility methods.
 * @author Mark Thornton
 */
public class Scale {
    public static int julianDayNumber(int year, int month, int day) {
        int a = (14-month)/12;
        int y = year+4800-a;
        int m = month+12*a-3;
        return day + (153*m+2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
    }

    public static int modifiedJulianDay(int year, int month, int day) {
        return julianDayNumber(year, month, day) - 2400001;
    }
}
