/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time field representing a day of month.
 * <p>
 * DayOfMonth is an immutable time field that can only store a day of month.
 * It is a type-safe way of representing a day of month in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The day of month may be queried using getDayOfMonth().
 * <p>
 * DayOfMonth is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class DayOfMonth implements Comparable<DayOfMonth> {

    /**
     * The day of month being represented.
     */
    private final int dayOfMonth;

    /**
     * Obtains an instance of <code>DayOfMonth</code>.
     *
     * @param dayOfMonth  the day of month to represent
     */
    public static DayOfMonth dayOfMonth(int dayOfMonth) {
        return new DayOfMonth(dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of month.
     */
    private DayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of month value.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of month represented by this instance.
     * 
     * @param otherDayOfMonth  the other day of month instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDayOfMonth is null
     */
    public int compareTo(DayOfMonth otherDayOfMonth) {
        int thisValue = this.dayOfMonth;
        int otherValue = otherDayOfMonth.dayOfMonth;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this day of month instance greater than the specified day of month.
     * 
     * @param otherDayOfMonth  the other day of month instance, not null
     * @return true if this day of month is greater
     * @throws NullPointerException if otherDayOfMonth is null
     */
    public boolean isGreaterThan(DayOfMonth otherDayOfMonth) {
        return compareTo(otherDayOfMonth) > 0;
    }

    /**
     * Is this day of month instance less than the specified day of month.
     * 
     * @param otherDayOfMonth  the other day of month instance, not null
     * @return true if this day of month is less
     * @throws NullPointerException if otherDayOfMonth is null
     */
    public boolean isLessThan(DayOfMonth otherDayOfMonth) {
        return compareTo(otherDayOfMonth) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day of month.
     * 
     * @param otherDayOfMonth  the other day of month instance, null return false
     * @return true if the day of month is the same
     */
    public boolean equals(Object otherDayOfMonth) {
        if (otherDayOfMonth instanceof DayOfMonth) {
            return dayOfMonth == ((DayOfMonth) otherDayOfMonth).dayOfMonth;
        }
        return false;
    }

    /**
     * A hashcode for the day of month object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return dayOfMonth;
    }

}
