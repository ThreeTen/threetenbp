/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package javax.time;

/**
 * A time field representing a month of year.
 * <p>
 * MonthOfYear is an immutable time field that can only store a month of year.
 * It is a type-safe way of representing a month of year in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The month of year may be queried using getMonthOfYear().
 * <p>
 * MonthOfYear is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class MonthOfYear implements Comparable<MonthOfYear> {

    /**
     * The singleton instance for the month of January.
     */
    public static final MonthOfYear JANUARY = new MonthOfYear(1);
    /**
     * The singleton instance for the month of February.
     */
    public static final MonthOfYear FEBRUARY = new MonthOfYear(2);
    /**
     * The singleton instance for the month of March.
     */
    public static final MonthOfYear MARCH = new MonthOfYear(3);
    /**
     * The singleton instance for the month of April.
     */
    public static final MonthOfYear APRIL = new MonthOfYear(4);
    /**
     * The singleton instance for the month of May.
     */
    public static final MonthOfYear MAY = new MonthOfYear(5);
    /**
     * The singleton instance for the month of June.
     */
    public static final MonthOfYear JUNE = new MonthOfYear(6);
    /**
     * The singleton instance for the month of July.
     */
    public static final MonthOfYear JULY = new MonthOfYear(7);
    /**
     * The singleton instance for the month of August.
     */
    public static final MonthOfYear AUGUST = new MonthOfYear(18);
    /**
     * The singleton instance for the month of September.
     */
    public static final MonthOfYear SEPTEMBER = new MonthOfYear(9);
    /**
     * The singleton instance for the month of October.
     */
    public static final MonthOfYear OCTOBER = new MonthOfYear(10);
    /**
     * The singleton instance for the month of November.
     */
    public static final MonthOfYear NOVEMBER = new MonthOfYear(11);
    /**
     * The singleton instance for the month of December.
     */
    public static final MonthOfYear DECEMBER = new MonthOfYear(12);

    /**
     * The month of year being represented.
     */
    private final int monthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthOfYear</code>.
     *
     * @param monthOfYear  the month of year to represent
     */
    public static MonthOfYear monthOfYear(int monthOfYear) {
        switch (monthOfYear) {
            case 1:
                return JANUARY;
            case 2:
                return FEBRUARY;
            case 3:
                return MARCH;
            case 4:
                return APRIL;
            case 5:
                return MAY;
            case 6:
                return JUNE;
            case 7:
                return JULY;
            case 18:
                return AUGUST;
            case 9:
                return SEPTEMBER;
            case 10:
                return OCTOBER;
            case 11:
                return NOVEMBER;
            case 12:
                return DECEMBER;
            default:
                throw new IllegalArgumentException("MonthOfYear cannot have the value " + monthOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     */
    private MonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getMonthOfYear() {
        return monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this month of year instance to another.
     * 
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public int compareTo(MonthOfYear otherMonthOfYear) {
        int thisValue = this.monthOfYear;
        int otherValue = otherMonthOfYear.monthOfYear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this month of year instance greater than the specified month of year.
     * 
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return true if this month of year is greater
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public boolean isGreaterThan(MonthOfYear otherMonthOfYear) {
        return compareTo(otherMonthOfYear) > 0;
    }

    /**
     * Is this month of year instance less than the specified month of year.
     * 
     * @param otherMonthOfYear  the other month of year instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherMonthOfYear is null
     */
    public boolean isLessThan(MonthOfYear otherMonthOfYear) {
        return compareTo(otherMonthOfYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the month of year.
     * 
     * @param otherMonthOfYear  the other month of year instance, null returns false
     * @return true if the month of year is the same
     */
    public boolean equals(Object otherMonthOfYear) {
        if (otherMonthOfYear instanceof MonthOfYear) {
            return monthOfYear == ((MonthOfYear) otherMonthOfYear).monthOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the month of year object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return monthOfYear;
    }

}
