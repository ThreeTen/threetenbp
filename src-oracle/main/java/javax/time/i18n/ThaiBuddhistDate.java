/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MonthOfYear;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.calendrical.InvalidCalendarFieldException;

/**
 * A date in the Thai Buddhist calendar system.
 * <p>
 * {@code ThaiBuddhistDate} is an immutable class that represents a date in the Thai Buddhist calendar system.
 * The rules of the calendar system are described in {@link ThaiBuddhistChronology}.
 * <p>
 * Instances of this class may be created from other date objects that implement {@code Calendrical}.
 * Notably this includes {@link LocalDate} and all other date classes from other calendar systems.
 * <p>
 * ThaiBuddhistDate is immutable and thread-safe.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class ThaiBuddhistDate
        implements Calendrical, Comparable<ThaiBuddhistDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -135957664026407129L;

    /**
     * The minimum valid year-of-era.
     */
    public static final int MIN_YEAR_OF_ERA = 1;
    /**
     * The maximum valid year-of-era.
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
     * Gets the rule for {@code ThaiBuddhistDate}.
     *
     * @return the rule for the date, never null
     */
    public static CalendricalRule<ThaiBuddhistDate> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from the Thai Buddhist year,
     * month-of-year and day-of-month. This uses the Thai Buddhist era.
     *
     * @param yearOfThaiBuddhistEra  the year to represent in the Thai Buddhist era, from 1 to 9999
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ThaiBuddhistDate of(int yearOfThaiBuddhistEra, MonthOfYear monthOfYear, int dayOfMonth) {
        return ThaiBuddhistDate.of(ThaiBuddhistEra.BUDDHIST, yearOfThaiBuddhistEra, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from the era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the era to represent, not null
     * @param yearOfEra  the year-of-era to represent, from 1 to 9999
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ThaiBuddhistDate of(ThaiBuddhistEra era, int yearOfEra, MonthOfYear monthOfYear, int dayOfMonth) {
        I18NUtil.checkNotNull(era, "ThaiBuddhistEra must not be null");
        ThaiBuddhistChronology.yearOfEraRule().checkValidValue(yearOfEra);
        I18NUtil.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        ThaiBuddhistChronology.dayOfMonthRule().checkValidValue(dayOfMonth);
        int year = yearOfEra;
        if (era == ThaiBuddhistEra.BEFORE_BUDDHIST) {
            year = 1 - yearOfEra;
        }
        year += ThaiBuddhistChronology.YEAR_OFFSET;
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new ThaiBuddhistDate(date);
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from a calendrical.
     * <p>
     * This can be used extract the date directly from any implementation
     * of {@code Calendrical}, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the Thai Buddhist date, never null
     * @throws CalendricalException if the date cannot be obtained
     */
    public static ThaiBuddhistDate of(Calendrical calendrical) {
        return rule().getValueChecked(calendrical);
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from a date.
     *
     * @param date  the date to use, not null
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the year is invalid
     */
    static ThaiBuddhistDate of(LocalDate date) {
        I18NUtil.checkNotNull(date, "LocalDate must not be null");
        int yearOfEra = date.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        if (yearOfEra < 0) {
            yearOfEra = 1 - yearOfEra;
        }
        ThaiBuddhistChronology.yearOfEraRule().checkValidValue(yearOfEra);
        return new ThaiBuddhistDate(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date.
     *
     * @param date  the date, validated in range, validated not null
     */
    private ThaiBuddhistDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns a new date based on this one, returning {@code this} where possible.
     *
     * @param date  the date to create with, not null
     */
    private ThaiBuddhistDate with(LocalDate date) {
        if (this.date == date) {
            return this;
        }
        return ThaiBuddhistDate.of(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this date uses, which is the Thai Buddhist calendar system.
     *
     * @return the Thai Buddhist chronology, never null
     */
    public ThaiBuddhistChronology getChronology() {
        return ThaiBuddhistChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        return CalendricalEngine.derive(ruleToDerive, rule(), date, null, null, null, getChronology(), null);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Thai Buddhist era field.
     *
     * @return the era, never null
     */
    public ThaiBuddhistEra getEra() {
        int year = date.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        return year < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST;
    }

    /**
     * Gets the Thai Buddhist year-of-era field.
     *
     * @return the year, from 1 to 9999
     */
    public int getYearOfEra() {
        int year = date.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        return year < 1 ? 1 - year : year;
    }

    /**
     * Gets the month-of-year field.
     *
     * @return the month-of-year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return date.getMonthOfYear();
    }

    /**
     * Gets the day-of-month field.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day-of-year field.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the Thai Buddhist calendar system rules.
     *
     * @return true if this date is in a leap year
     */
    public boolean isLeapYear() {
        return date.isLeapYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the year altered.
     * <p>
     * This method changes the year of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to set in the returned date, not null
     * @param yearOfEra  the year-of-era to set in the returned date, from 1 to 9999
     * @return a {@code ThaiBuddhistDate} based on this date with the requested year, never null
     * @throws IllegalCalendarFieldValueException if the year-of-era value is invalid
     */
    public ThaiBuddhistDate withYear(ThaiBuddhistEra era, int yearOfEra) {
        ThaiBuddhistChronology.yearOfEraRule().checkValidValue(yearOfEra);
        int year = yearOfEra;
        if (era == ThaiBuddhistEra.BEFORE_BUDDHIST) {
            year = 1 - yearOfEra;
        }
        year += ThaiBuddhistChronology.YEAR_OFFSET;
        return with(date.withYear(year));
    }

    /**
     * Returns a copy of this date with the year-of-era altered.
     * <p>
     * This method changes the year-of-era of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearOfEra  the year to set in the returned date, from 1 to 9999
     * @return a {@code ThaiBuddhistDate} based on this date with the requested year-of-era, never null
     * @throws IllegalCalendarFieldValueException if the year-of-era value is invalid
     */
    public ThaiBuddhistDate withYearOfEra(int yearOfEra) {
        return withYear(getEra(), yearOfEra);
    }

    /**
     * Returns a copy of this date with the month-of-year altered.
     * <p>
     * This method changes the month-of-year of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, not null
     * @return a {@code ThaiBuddhistDate} based on this date with the requested month, never null
     */
    public ThaiBuddhistDate withMonthOfYear(MonthOfYear monthOfYear) {
        I18NUtil.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return with(date.with(monthOfYear));
    }

    /**
     * Returns a copy of this date with the day-of-month altered.
     * <p>
     * This method changes the day-of-month of the date.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return a {@code ThaiBuddhistDate} based on this date with the requested day, never null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public ThaiBuddhistDate withDayOfMonth(int dayOfMonth) {
        ThaiBuddhistChronology.dayOfMonthRule().checkValidValue(dayOfMonth);
        return with(date.withDayOfMonth(dayOfMonth));
    }

    /**
     * Returns a copy of this date with the day-of-year altered.
     * <p>
     * This method changes the day-of-year of the date.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code ThaiBuddhistDate} based on this date with the requested day, never null
     * @throws IllegalCalendarFieldValueException if the day-of-year value is invalid
     * @throws InvalidCalendarFieldException if the day-of-year is invalid for the year
     */
    public ThaiBuddhistDate withDayOfYear(int dayOfYear) {
        ThaiBuddhistChronology.dayOfYearRule().checkValidValue(dayOfYear);
        return with(date.withDayOfYear(dayOfYear));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified number of years added.
     * <p>
     * This method adds the specified amount in years to the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the years added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate plusYears(int years) {
        return with(date.plusYears(years));
    }

    /**
     * Returns a copy of this date with the specified number of months added.
     * <p>
     * This method adds the specified amount in months to the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the months added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate plusMonths(int months) {
        return with(date.plusMonths(months));
    }

    /**
     * Returns a copy of this date with the specified number of weeks added.
     * <p>
     * This method adds the specified amount in weeks to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the weeks added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate plusWeeks(int weeks) {
        return with(date.plusWeeks(weeks));
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This method adds the specified amount in days to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the days added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate plusDays(long days) {
        return with(date.plusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified number of years subtracted.
     * <p>
     * This method subtracts the specified amount in years from the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the years subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate minusYears(int years) {
        return with(date.minusYears(years));
    }

    /**
     * Returns a copy of this date with the specified number of months subtracted.
     * <p>
     * This method subtracts the specified amount in months from the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the months subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate minusMonths(int months) {
        return with(date.minusMonths(months));
    }

    /**
     * Returns a copy of this date with the specified number of weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the weeks subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate minusWeeks(int weeks) {
        return with(date.minusWeeks(weeks));
    }

    /**
     * Returns a copy of this date with the specified number of days subtracted.
     * <p>
     * This method subtracts the specified amount in days from the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the days subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ThaiBuddhistDate minusDays(long days) {
        if (days == 0) {
            return this;
        }
        return with(date.minusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a {@code LocalDate}, which is the default representation
     * of a date, and provides values in the ISO-8601 calendar system.
     *
     * @return the equivalent date in the ISO-8601 calendar system, never null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(ThaiBuddhistDate other) {
        return date.compareTo(other.date);
    }

    /**
     * Checks if this date is after the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     */
    public boolean isAfter(ThaiBuddhistDate other) {
        return date.isAfter(other.date);
    }

    /**
     * Checks if this date is before the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is before the specified date
     */
    public boolean isBefore(ThaiBuddhistDate other) {
        return date.isBefore(other.date);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ThaiBuddhistDate) {
            ThaiBuddhistDate otherDate = (ThaiBuddhistDate) other;
            return this.date.equals(otherDate.date);
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
        return "ThaiBuddhistDate".hashCode() ^ date.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 2551-12-01 (ThaiBuddhist)}.
     * <p>
     * The output will be in the format {@code yyyy-MM-dd (ThaiBuddhist)}.
     * The year will be negative for the era BEFORE_BUDDHIST.
     * There is no year zero.
     *
     * @return the formatted date, never null
     */
    @Override
    public String toString() {
        boolean currentEra = getEra() == ThaiBuddhistEra.BUDDHIST;
        int yearValue = getYearOfEra();
        yearValue = Math.abs(currentEra ? yearValue : -yearValue);
        int monthValue = getMonthOfYear().getValue();
        int dayValue = getDayOfMonth();
        StringBuilder buf = new StringBuilder();
        return buf.append(currentEra ? "" : "-")
                .append(yearValue < 10 ? "0" : "").append(yearValue)
                .append(monthValue < 10 ? "-0" : "-").append(monthValue)
                .append(dayValue < 10 ? "-0" : "-").append(dayValue)
                .append(" (ThaiBuddhist)").toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<ThaiBuddhistDate> implements Serializable {
        private static final CalendricalRule<ThaiBuddhistDate> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(ThaiBuddhistDate.class, "ThaiBuddhistDate");
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected ThaiBuddhistDate deriveFrom(CalendricalEngine engine) {
            LocalDate ld = engine.getDate(true);
            return ld != null ? ThaiBuddhistDate.of(ld) : null;
        }
    }

}
