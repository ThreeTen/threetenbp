/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import java.io.Serializable;

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.period.Periods;

/**
 * A month-day without a time zone in the ISO-8601 calendar system,
 * such as '3rd December'.
 * <p>
 * MonthDay is an immutable calendrical that represents a month-day combination.
 * This class does not store or represent a year, time or time zone.
 * Thus, for example, the value "2nd October" can be stored in a MonthDay.
 * <p>
 * A MonthDay does not posses a year, thus the 29th of February is considered valid.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * MonthDay is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class MonthDay
        implements CalendricalProvider, Comparable<MonthDay>, Serializable, DateAdjustor, DateMatcher {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -254395108L;
    /**
     * A sample year that can be used to seed calculations.
     * This is a leap year to enable the 29th of February.
     */
    private static final Year SAMPLE_YEAR = Year.isoYear(2000);

    /**
     * The month of year, not null.
     */
    private final MonthOfYear month;
    /**
     * The day of month, not null.
     */
    private final DayOfMonth day;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthDay</code>.
     * <p>
     * The day of month must be valid for the month within a leap year.
     * Hence, for February, day 29 is valid.
     * <p>
     * For example, passing in April and day 31 will throw an exception, as
     * there can never be a 31st April in any year. Alternately, passing in
     * 29th February is valid, as that month-day can be valid.
     *
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @return a MonthDay object, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month
     */
    public static MonthDay monthDay(MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        if (monthOfYear == null) {
            throw new NullPointerException("MonthOfYear must not be null");
        }
        if (dayOfMonth == null) {
            throw new NullPointerException("DayOfMonth must not be null");
        }
        if (dayOfMonth.isValid(SAMPLE_YEAR, monthOfYear) == false) {
            throw new InvalidCalendarFieldException("Illegal value for DayOfMonth field, value " + dayOfMonth.getValue() +
                    " is not valid for month " + monthOfYear.name(), DayOfMonth.rule());
        }
        return new MonthDay(monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>MonthDay</code>.
     * <p>
     * The day of month must be valid for the month within a leap year.
     * Hence, for February, day 29 is valid.
     * <p>
     * For example, passing in April and day 31 will throw an exception, as
     * there can never be a 31st April in any year. Alternately, passing in
     * 29th February is valid, as that month-day can be valid.
     *
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a MonthDay object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month
     */
    public static MonthDay monthDay(MonthOfYear monthOfYear, int dayOfMonth) {
        return monthDay(monthOfYear, DayOfMonth.dayOfMonth(dayOfMonth));
    }

    /**
     * Obtains an instance of <code>MonthDay</code>.
     * <p>
     * The day of month must be valid for the month within a leap year.
     * Hence, for month 2 (February), day 29 is valid.
     * <p>
     * For example, passing in month 4 (April) and day 31 will throw an exception, as
     * there can never be a 31st April in any year. Alternately, passing in
     * 29th February is valid, as that month-day can be valid.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a MonthDay object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month
     */
    public static MonthDay monthDay(int monthOfYear, int dayOfMonth) {
        return monthDay(MonthOfYear.monthOfYear(monthOfYear), DayOfMonth.dayOfMonth(dayOfMonth));
    }

    /**
     * Obtains an instance of <code>MonthDay</code> from a Calendrical.
     * <p>
     * This method will create a MonthDay from the Calendrical using either
     * the fields or the date. If both are present, the values in the field-value
     * map must match those in the date.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return a MonthDay object, never null
     * @throws UnsupportedCalendarFieldException if either field cannot be found
     * @throws InvalidCalendarFieldException if the either field is invalid
     */
    public static MonthDay monthDay(Calendrical calendrical) {
        int month = calendrical.getValue(MonthOfYear.rule());
        int dom = calendrical.getValue(DayOfMonth.rule());
        return monthDay(month, dom);
    }

    /**
     * Obtains an instance of <code>MonthDay</code> from a date provider.
     * <p>
     * This can be used extract a month-day object directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return a MonthDay object, never null
     */
    public static MonthDay monthDay(DateProvider dateProvider) {
        LocalDate date = LocalDate.date(dateProvider);
        return new MonthDay(date.getMonthOfYear(), date.getDayOfMonth());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, previously validated.
     *
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, valid for month, not null
     */
    private MonthDay(MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    /**
     * Returns a copy of this month-day with the new month and day, checking
     * to see if a new object is in fact required.
     *
     * @param newMonth  the month of year to represent, from 1 (January) to 12 (December)
     * @param newDay  the day of month to represent, from 1 to 31
     * @return the month-day, never null
     */
    private MonthDay withMonthDay(MonthOfYear newMonth, DayOfMonth newDay) {
        if (month == newMonth && day == newDay) {
            return this;
        }
        return new MonthDay(newMonth, newDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>MonthDay</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(DateTimeFieldRule field) {
        return field.isSupported(Periods.DAYS, Periods.YEARS);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param field  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     * @throws InvalidCalendarFieldException if the value for the field is invalid
     */
    public int get(DateTimeFieldRule field) {
        return toCalendrical().getValue(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year field.
     * <p>
     * This method provides access to an object representing the month field.
     * This can be used to access the {@link MonthOfYear#getValue() int value}.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return month;
    }

    /**
     * Gets the day of month field.
     * <p>
     * This method provides access to an object representing the day of month field.
     * This can be used to access the {@link DayOfMonth#getValue() int value}.
     *
     * @return the day of month, never null
     */
    public DayOfMonth getDayOfMonth() {
        return day;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay with the month of year altered.
     * <p>
     * If the day of month is invalid for the specified month, the day will
     * be adjusted to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, not null
     * @return a new updated MonthDay, never null
     */
    public MonthDay with(MonthOfYear monthOfYear) {
        if (day.isValid(SAMPLE_YEAR, monthOfYear) == false) {
            return withMonthDay(monthOfYear, monthOfYear.getLastDayOfMonth(SAMPLE_YEAR));
        }
        return withMonthDay(monthOfYear, day);
    }

    /**
     * Returns a copy of this MonthDay with the day of month altered.
     * <p>
     * If the day of month is invalid for the current month, an exception
     * will be thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, not null
     * @return a new updated MonthDay, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month
     */
    public MonthDay with(DayOfMonth dayOfMonth) {
        if (dayOfMonth.isValid(SAMPLE_YEAR, month) == false) {
            throw new InvalidCalendarFieldException("Day of month cannot be changed to " +
                    dayOfMonth + " for the month " + month, DayOfMonth.rule());
        }
        return withMonthDay(month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay with the month of year value altered.
     * <p>
     * If the day of month is invalid for the specified month, the day will
     * be adjusted to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated MonthDay, never null
     * @throws IllegalCalendarFieldValueException if the month value is invalid
     */
    public MonthDay withMonthOfYear(int monthOfYear) {
        return with(MonthOfYear.monthOfYear(monthOfYear));
    }

    /**
     * Returns a copy of this MonthDay with the day of month value altered.
     * <p>
     * If the day of month is invalid for the current month, an exception
     * will be thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated MonthDay, never null
     * @throws IllegalCalendarFieldValueException if the day of month value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month
     */
    public MonthDay withDayOfMonth(int dayOfMonth) {
        return with(DayOfMonth.dayOfMonth(dayOfMonth));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay rolling the month of year field by the
     * specified number of months.
     * <p>
     * This method will add the specified number of months to the month-day,
     * rolling from December back to January if necessary.
     * <p>
     * If the day of month is invalid for the specified month in the result,
     * the day will be adjusted to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to roll by, positive or negative
     * @return a new updated MonthDay, never null
     */
    public MonthDay rollMonthOfYear(int months) {
        if (months == 0) {
            return this;
        }
        int newMonth0 = (months % 12) + (month.getValue() - 1);
        newMonth0 = (newMonth0 + 12) % 12;
        return withMonthOfYear(++newMonth0);
    }

    /**
     * Returns a copy of this MonthDay rolling the day of month field by the
     * specified number of days.
     * <p>
     * This method will add the specified number of days to the month-day,
     * rolling from last day of month to the first if necessary.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to roll by, positive or negative
     * @return a new updated MonthDay, never null
     */
    public MonthDay rollDayOfMonth(int days) {
        int monthLength = month.lengthInDays(SAMPLE_YEAR);
        if (days == 0) {
            return this;
        }
        int newDOM0 = (days % monthLength) + (day.getValue() - 1);
        newDOM0 = (newDOM0 + monthLength) % monthLength;
        return withMonthOfYear(++newDOM0);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the value of this month-day, returning a new date.
     * <p>
     * If the day of month is invalid for the new year then an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return adjustDate(date, DateResolvers.strict());
    }

    /**
     * Adjusts a date to have the value of this month-day, using a resolver to
     * handle the case when the day of month becomes invalid.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @param resolver  the date resolver to use if the day of month is invalid, not null
     * @return the adjusted date, never null
     * @throws IllegalCalendarFieldValueException if the date cannot be resolved using the resolver
     */
    public LocalDate adjustDate(LocalDate date, DateResolver resolver) {
        if (month == date.getMonthOfYear() && day == date.getDayOfMonth()) {
            return date;
        }
        LocalDate resolved = resolver.resolveDate(date.getYear(), month, day);
        if (resolved == null) {
            throw new NullPointerException("The implementation of DateResolver must not return null");
        }
        return resolved;
    }

    /**
     * Checks if the month-day represented by this object matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return month == date.getMonthOfYear() && day == date.getDayOfMonth();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(MonthOfYear.rule(), month.getValue(), DayOfMonth.rule(), day.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this month-day to another month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(MonthDay other) {
        int cmp = month.compareTo(other.month);
        if (cmp == 0) {
            cmp = day.compareTo(other.day);
        }
        return cmp;
    }

    /**
     * Is this month-day after the specified month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return true if this is after the specified month-day
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(MonthDay other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this month-day before the specified month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return true if this point is before the specified month-day
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(MonthDay other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this month-day equal to the specified month-day.
     *
     * @param other  the other month-day to compare to, null returns false
     * @return true if this point is equal to the specified month-day
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MonthDay) {
            MonthDay otherMD = (MonthDay) other;
            return month == otherMD.month && day.equals(otherMD.day);
        }
        return false;
    }

    /**
     * A hashcode for this month-day.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return (month.getValue() << 6) + day.getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the month-day as a <code>String</code>.
     * <p>
     * The output will be in the format '--MM-dd':
     *
     * @return the string form of the month-day
     */
    @Override
    public String toString() {
        int monthValue = month.getValue();
        int dayValue = day.getValue();
        return new StringBuilder(10).append("--")
            .append(monthValue < 10 ? "-0" : "-").append(monthValue)
            .append(dayValue < 10 ? "-0" : "-").append(dayValue)
            .toString();
    }

}
