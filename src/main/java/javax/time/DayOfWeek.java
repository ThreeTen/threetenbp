/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time field representing a day of week.
 * <p>
 * DayOfWeek is an immutable time field that can only store a day of week.
 * It is a type-safe way of representing a day of week in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The day of week may be queried using getDayOfWeek().
 * <p>
 * DayOfWeek is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class DayOfWeek implements Comparable<DayOfWeek> {

    /**
     * The singleton instance for the day of week of Monday.
     */
    public static final DayOfWeek MONDAY = new DayOfWeek(1);
    /**
     * The singleton instance for the day of week of Tuesday.
     */
    public static final DayOfWeek TUESDAY = new DayOfWeek(2);
    /**
     * The singleton instance for the day of week of Wednesday.
     */
    public static final DayOfWeek WEDNESDAY = new DayOfWeek(3);
    /**
     * The singleton instance for the day of week of Thursday.
     */
    public static final DayOfWeek THURSDAY = new DayOfWeek(4);
    /**
     * The singleton instance for the day of week of Friday.
     */
    public static final DayOfWeek FRIDAY = new DayOfWeek(5);
    /**
     * The singleton instance for the day of week of Saturday.
     */
    public static final DayOfWeek SATURDAY = new DayOfWeek(6);
    /**
     * The singleton instance for the day of week of Sunday.
     */
    public static final DayOfWeek SUNDAY = new DayOfWeek(7);

    /**
     * The day of week being represented.
     */
    private final int dayOfWeek;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DayOfWeek</code>.
     *
     * @param dayOfWeek  the day of week to represent
     */
    public static DayOfWeek dayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            case 7:
                return SUNDAY;
            default:
                throw new IllegalArgumentException("DayOfWeek cannot have the value " + dayOfWeek);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of week.
     */
    private DayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of week value.
     *
     * @return the day of week
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day of week instance to another.
     * 
     * @param otherDayOfWeek  the other day of week instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDayOfWeek is null
     */
    public int compareTo(DayOfWeek otherDayOfWeek) {
        int thisValue = this.dayOfWeek;
        int otherValue = otherDayOfWeek.dayOfWeek;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this day of week instance greater than the specified day of week.
     * 
     * @param otherDayOfWeek  the other day of week instance, not null
     * @return true if this day of week is greater
     * @throws NullPointerException if otherDayOfWeek is null
     */
    public boolean isGreaterThan(DayOfWeek otherDayOfWeek) {
        return compareTo(otherDayOfWeek) > 0;
    }

    /**
     * Is this day of week instance less than the specified day of week.
     * 
     * @param otherDayOfWeek  the other day of week instance, not null
     * @return true if this day of week is less
     * @throws NullPointerException if otherDayOfWeek is null
     */
    public boolean isLessThan(DayOfWeek otherDayOfWeek) {
        return compareTo(otherDayOfWeek) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day of week.
     * 
     * @param otherDayOfWeek  the other day of week instance, null returns false
     * @return true if the day of week is the same
     */
    public boolean equals(Object otherDayOfWeek) {
        if (otherDayOfWeek instanceof DayOfWeek) {
            return dayOfWeek == ((DayOfWeek) otherDayOfWeek).dayOfWeek;
        }
        return false;
    }

    /**
     * A hashcode for the day of week object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return dayOfWeek;
    }

}
