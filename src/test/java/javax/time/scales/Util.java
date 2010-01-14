/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.time.scales;

/** Convenience methods for TimeScale tests.
 * @author Mark Thornton
 */
public class Util {
    public static long date(int year, int month, int day) {
        return ScaleUtil.epochSeconds(year, month, day);
    }

    public static int time(int hour, int minute, int second) {
        return second+60*(minute+60*hour);
    }

    public static int time(int hour, int minute) {
        return time(hour, minute, 0);
    }
    
    public static int millis(int millis) {
        return millis*1000000;
    }
}
