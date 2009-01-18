package javax.time.impl;

import javax.time.TimeSource;

/**
 * Created by IntelliJ IDEA.
 * User: mthornton
 * Date: 18-Jan-2009
 * Time: 11:07:08
 * To change this template use File | Settings | File Templates.
 */
public class TestWindowsSystemTime {
    public static void main(String[] args) {
        System.out.println(WindowsSystemTime.get());
        long adj = WindowsSystemTime.getAdjustment();
        if (adj == -1)
            System.out.println("getAdjustment failed");
        if (adj < 0)
            System.out.println("time adjustment disabled");
        System.out.println("timeAdjustment="+((adj>>>32)&0x7FFFFFFF));
        System.out.println("timeIncrement="+(adj&0xFFFFFFFFL));

        System.out.println(WindowsSystemTime.SOURCE.instant());
        System.out.println(TimeSource.java().instant());
        System.out.println(TimeSource.system().instant());

        System.out.println();
        // check calculation with 'negative' filetimes.
        System.out.println(WindowsSystemTime.instantFromFileTime(Long.MAX_VALUE));
        System.out.println(WindowsSystemTime.instantFromFileTime(Long.MIN_VALUE));
        System.out.println(WindowsSystemTime.instantFromFileTime(Long.MIN_VALUE+1));
    }
}
