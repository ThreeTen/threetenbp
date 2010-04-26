/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MonthOfYear;

/**
 * A date in the Minguo calendar system.
 * <p>
 * MinguoDate is an immutable class that represents a date in the Minguo calendar system.
 * The rules of the calendar system are described in {@link MinguoChronology}.
 * <p>
 * Instances of this class may be created from any other object that implements
 * {@link DateProvider} including {@link LocalDate}. Similarly, instances of
 * this class may be passed into the factory method of any other implementation
 * of <code>DateProvider</code>.
 * <p>
 * MinguoDate is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class MinguoDate
        implements DateProvider, Calendrical, Comparable<MinguoDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -135957664026407129L;

    /**
     * The minimum valid year of era.
     * This is currently set to 1 but may be changed to increase the valid range
     * in a future version of the specification.
     */
    public static final int MIN_YEAR_OF_ERA = 1;
    /**
     * The maximum valid year of era.
     * This is currently set to 9999 but may be changed to increase the valid range
     * in a future version of the specification.
     */
    public static final int MAX_YEAR_OF_ERA = 9999;

    /**
     * The underlying date.
     */
    private final LocalDate date;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MinguoDate</code> from the Minguo year,
     * month-of-year and day-of-month. This uses the Minguo era.
     *
     * @param yearOfMinguoEra  the year to represent in the Minguo era, from 1 to 9999
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the created MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static MinguoDate of(int yearOfMinguoEra, MonthOfYear monthOfYear, int dayOfMonth) {
        return MinguoDate.of(MinguoEra.MINGUO, yearOfMinguoEra, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>MinguoDate</code> from the Minguo era,
     * Minguo year, month-of-year and day-of-month.
     *
     * @param era  the era to represent, not null
     * @param yearOfEra  the year to represent, from 1 to 9999
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the created MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static MinguoDate of(MinguoEra era, int yearOfEra, MonthOfYear monthOfYear, int dayOfMonth) {
        I18NUtil.checkNotNull(era, "MinguoEra must not be null");
        MinguoChronology.yearOfEraRule().checkValue(yearOfEra);
        I18NUtil.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        MinguoChronology.dayOfMonthRule().checkValue(dayOfMonth);
        int year = yearOfEra;
        if (era == MinguoEra.BEFORE_MINGUO) {
            year = 1 - yearOfEra;
        }
        year += MinguoChronology.YEAR_OFFSET;
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new MinguoDate(date);
    }

//    /**
//     * Obtains an instance of <code>MinguoDate</code> with the specified date.
//     *
//     * @param era  the era to represent, caller calulated, BM(Before-Minguo) or AM After-Minguo)
//     * @param year  the year to represent, caller calculated
//     * @param dayOfYear  the month-of-year to represent, caller calculated
//     * @return a MinguoDate object
//     */
//    static MinguoDate minguoDateFromDoY(MinguoEra era, int year, int dayOfYear) {
//        MinguoChronology.era().checkValue(era);
//        MinguoChronology.year().checkValue(year);
//        MinguoChronology.dayOfYear().checkValue(dayOfYear);
//        int[] gregorianEraYear = getGregorianEraYearFromLocalEraYear(era, year);
//        long julianDay = I18NUtil.getJulianDayFromGregorianDate(
//                gregorianEraYear[0], gregorianEraYear[1], 1,
//                dayOfYear);
//        return new MinguoDate(julianDay);
//    }

    /**
     * Obtains an instance of <code>MinguoDate</code> from a date provider.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the created MinguoDate instance, never null
     */
    public static MinguoDate of(DateProvider dateProvider) {
        LocalDate date = LocalDate.of(dateProvider);
        int yearOfEra = date.getYear() - MinguoChronology.YEAR_OFFSET;
        if (yearOfEra < 0) {
            yearOfEra = 1 - yearOfEra;
        }
        MinguoChronology.yearOfEraRule().checkValue(yearOfEra);
        return new MinguoDate(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance of <code>MinguoDate</code> with the specified date.
     *
     * @param date  the date, validated in range, validated not null
     */
    private MinguoDate(LocalDate date) {
        this.date = date;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that describes the calendar system rules for this date.
     *
     * @return the MinguoChronology, never null
     */
    public MinguoChronology getChronology() {
        return MinguoChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date then
     * <code>null</code> will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era value.
     *
     * @return the era, never null
     */
    public MinguoEra getEra() {
        int year = date.getYear() - MinguoChronology.YEAR_OFFSET;
        return year < 1 ? MinguoEra.BEFORE_MINGUO : MinguoEra.MINGUO;
    }

    /**
     * Gets the year value.
     *
     * @return the year, from 1 to 9999
     */
    public int getYearOfEra() {
        int year = date.getYear() - MinguoChronology.YEAR_OFFSET;
        return year < 1 ? 1 - year : year;
    }

    /**
     * Gets the month-of-year.
     *
     * @return the month-of-year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return date.getMonthOfYear();
    }

    /**
     * Gets the day-of-month value.
     *
     * @return the day-of-month, from 1 to 28-31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day-of-year value.
     *
     * @return the day-of-year, from 1 to 365-366
     */
    public int getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day-of-week.
     *
     * @return the day-of-week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    /**
     * Checks if the date represented is a leap year.
     *
     * @return true if this date is in a leap year
     */
    public boolean isLeapYear() {
        return date.toYear().isLeap();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MinguoDate with the year of era value altered.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to represent, not null
     * @param yearOfEra  the year to represent, from 1 to 9999
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the year is out of range
     */
    public MinguoDate withYear(MinguoEra era, int yearOfEra) {
        MinguoChronology.yearOfEraRule().checkValue(yearOfEra);
        int year = yearOfEra;
        if (era == MinguoEra.BEFORE_MINGUO) {
            year = 1 - yearOfEra;
        }
        year += MinguoChronology.YEAR_OFFSET;
        return MinguoDate.of(date.withYear(year));
    }

    /**
     * Returns a copy of this MinguoDate with the year value altered.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearOfEra  the year to represent, from 1 to 9999
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the year is out of range
     */
    public MinguoDate withYearOfEra(int yearOfEra) {
        return withYear(getEra(), yearOfEra);
    }

    /**
     * Returns a copy of this MinguoDate with the month-of-year value altered.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to represent, not nullo
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the month is out of range
     */
    public MinguoDate withMonthOfYear(MonthOfYear monthOfYear) {
        I18NUtil.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return MinguoDate.of(date.with(monthOfYear));
    }

    /**
     * Returns a copy of this MinguoDate with the day-of-month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to represent, from 1 to 28-31
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the day is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the year and month
     */
    public MinguoDate withDayOfMonth(int dayOfMonth) {
        MinguoChronology.dayOfMonthRule().checkValue(dayOfMonth);
        return MinguoDate.of(date.withDayOfMonth(dayOfMonth));
    }

    /**
     * Returns a copy of this MinguoDate with the day-of-year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to represent, from 1 to 365-366
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the day-of-year is out of range
     * @throws InvalidCalendarFieldException if the day-of-year is invalid for the year
     */
    public MinguoDate withDayOfYear(int dayOfYear) {
        MinguoChronology.dayOfYearRule().checkValue(dayOfYear);
        return MinguoDate.of(date.withDayOfYear(dayOfYear));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MinguoDate with the specified number of years added.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the year range is exceeded
     */
    public MinguoDate plusYears(int years) {
        if (years == 0) {
            return this;
        }
        return MinguoDate.of(date.plusYears(years));
    }

    /**
     * Returns a copy of this MinguoDate with the specified number of months added.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the year range is exceeded
     */
    public MinguoDate plusMonths(int months) {
        if (months == 0) {
            return this;
        }
        return MinguoDate.of(date.plusMonths(months));
    }

    /**
     * Returns a copy of this MinguoDate with the specified period in weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a new updated MinguoDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public MinguoDate plusWeeks(int weeks) {
        return plusDays(7L * weeks);
    }

    /**
     * Returns a copy of this MinguoDate with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a new updated MinguoDate instance, never null
     * @throws IllegalCalendarFieldValueException if the year range is exceeded
     */
    public MinguoDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return MinguoDate.of(date.plusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MinguoDate with the specified period in years subtracted.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a new updated MinguoDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public MinguoDate minusYears(int years) {
        if (years == 0) {
            return this;
        }
        return MinguoDate.of(date.minusYears(years));
    }

    /**
     * Returns a copy of this MinguoDate with the specified period in months subtracted.
     * <p>
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a new updated MinguoDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public MinguoDate minusMonths(int months) {
        if (months == 0) {
            return this;
        }
        return MinguoDate.of(date.minusMonths(months));
    }

    /**
     * Returns a copy of this MinguoDate with the specified period in weeks subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a new updated MinguoDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public MinguoDate minusWeeks(int weeks) {
        return minusDays(7L * weeks);
    }

    /**
     * Returns a copy of this MinguoDate with the specified number of days subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a new updated MinguoDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public MinguoDate minusDays(long days) {
        if (days == 0) {
            return this;
        }
        return MinguoDate.of(date.minusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to an ISO-8601 calendar system <code>LocalDate</code>.
     *
     * @return the equivalent date in the ISO-8601 calendar system, never null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(MinguoDate otherDate) {
        return date.compareTo(otherDate.date);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return true if this day is after the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isAfter(MinguoDate otherDate) {
        return date.isAfter(otherDate.date);
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return true if this day is before the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(MinguoDate otherDate) {
        return date.isBefore(otherDate.date);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date equal to the specified date.
     *
     * @param otherDate  the other date to compare to, null returns false
     * @return true if this point is equal to the specified date
     */
    @Override
    public boolean equals(Object otherDate) {
        if (this == otherDate) {
            return true;
        }
        if (otherDate instanceof MinguoDate) {
            MinguoDate other = (MinguoDate) otherDate;
            return this.date.equals(other.date);
        }
        return false;
    }

    /**
     * A hash code for this date.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return "MinguoDate".hashCode() ^ date.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the date as a <code>String</code>, such as '97-13-01 (Minguo)'.
     * <p>
     * The output will be in the format 'yy-MM-dd (Minguo)'.
     * The year will be negative for the era BEFORE_MINGUO.
     * There is no year zero.
     *
     * @return the formatted date string, never null
     */
    @Override
    public String toString() {
        boolean currentEra = getEra() == MinguoEra.MINGUO;
        int yearValue = getYearOfEra();
        yearValue = Math.abs(currentEra ? yearValue : -yearValue);
        int monthValue = getMonthOfYear().getValue();
        int dayValue = getDayOfMonth();
        StringBuilder buf = new StringBuilder();
        return buf.append(currentEra ? "" : "-")
                .append(yearValue < 10 ? "0" : "").append(yearValue)
                .append(monthValue < 10 ? "-0" : "-").append(monthValue)
                .append(dayValue < 10 ? "-0" : "-").append(dayValue)
                .append(" (Minguo)").toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for <code>MinguoDate</code>.
     *
     * @return the rule for the date, never null
     */
    public static CalendricalRule<MinguoDate> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<MinguoDate> implements Serializable {
        private static final CalendricalRule<MinguoDate> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(MinguoDate.class, ISOChronology.INSTANCE, "MinguoDate", MinguoChronology.periodDays(), null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected MinguoDate derive(Calendrical calendrical) {
            LocalDate ld = calendrical.get(LocalDate.rule());
            return ld != null ? MinguoDate.of(ld) : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            MinguoDate md = merger.getValue(this);
            merger.storeMerged(LocalDate.rule(), md.toLocalDate());
            merger.removeProcessed(this);
        }
    }

}
