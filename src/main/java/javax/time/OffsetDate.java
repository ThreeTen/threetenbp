/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;

import java.io.Serializable;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;
import javax.time.format.CalendricalParseException;
import javax.time.format.DateTimeFormatters;
import javax.time.zone.ZoneResolvers;

/**
 * A date with a zone offset from UTC in the ISO-8601 calendar system,
 * such as {@code 2007-12-03+01:00}.
 * <p>
 * {@code OffsetDate} is an immutable calendrical that represents a date, often viewed
 * as year-month-day-offset. This object can also access other date fields such as
 * day-of-year, day-of-week and week-of-year.
 * <p>
 * This class does not store or represent a time.
 * For example, the value "2nd October 2007 +02:00" can be stored
 * in a {@code OffsetDate}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class OffsetDate
        implements AdjustableDateTime, DateTimeAdjuster, Comparable<OffsetDate>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -3618963189L;

    /**
     * The date.
     */
    private final LocalDate date;
    /**
     * The zone offset.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, not null
     */
    public static OffsetDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     */
    public static OffsetDate now(Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from a year, month and day.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return the offset date, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     */
    public static OffsetDate of(int year, Month month, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDate} from a year, month and day.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return the offset date, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     */
    public static OffsetDate of(int year, int month, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDate} from a local date and an offset.
     *
     * @param date  the local date, not null
     * @param offset  the zone offset, not null
     * @return the offset date, not null
     */
    public static OffsetDate of(LocalDate date, ZoneOffset offset) {
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from an {@code Instant}.
     * <p>
     * This conversion drops the time component of the instant effectively
     * converting at midnight at the start of the day.
     *
    * @param instant  the instant to create the date from, not null
     * @param offset  the zone offset to use, not null
     * @return the offset date, not null
     * @throws CalendricalException if the instant exceeds the supported date range
     */
    public static OffsetDate ofInstant(Instant instant, ZoneOffset offset) {
        DateTimes.checkNotNull(instant, "Instant must not be null");
        DateTimes.checkNotNull(offset, "ZoneOffset must not be null");
        long epochSec = instant.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later
        long epochDay = DateTimes.floorDiv(epochSec, DateTimes.SECONDS_PER_DAY);
        LocalDate date = LocalDate.ofEpochDay(epochDay);
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code OffsetDate}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the offset date, not null
     * @throws CalendricalException if unable to convert to an {@code OffsetDate}
     */
    public static OffsetDate from(DateTime calendrical) {
        ZoneOffset offset = calendrical.extract(ZoneOffset.class);
        DateTimes.ensureNotNull(offset, "Unable to convert calendrical to ZoneOffset: ", calendrical.getClass());

        try {
            LocalDate date = LocalDate.from(calendrical);
            return of(date, offset);
        } catch (CalendricalException cex_ignore) {
            Instant instant = calendrical.extract(Instant.class);
            if (instant != null) {
                return OffsetDate.ofInstant(instant, offset);
            }
        }

        throw new CalendricalException("Unable to convert calendrical to OffsetDate: " + calendrical.getClass());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from a text string such as {@code 2007-12-03+01:00}.
     * <p>
     * The string must represent a valid date and is parsed using
     * {@link javax.time.format.DateTimeFormatters#isoOffsetDate()}.
     *
     * @param text  the text to parse such as "2007-12-03+01:00", not null
     * @return the parsed offset date, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static OffsetDate parse(CharSequence text) {
        return parse(text, DateTimeFormatters.isoOffsetDate());
    }

    /**
     * Obtains an instance of {@code OffsetDate} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset date, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static OffsetDate parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, OffsetDate.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param date  the date, validated as not null
     * @param offset  the zone offset, validated as not null
     */
    private OffsetDate(LocalDate date, ZoneOffset offset) {
        if (date == null) {
            throw new NullPointerException("LocalDate must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        this.date = date;
        this.offset = offset;
    }

    /**
     * Returns a new date based on this one, returning {@code this} where possible.
     *
     * @param date  the date to create with, not null
     * @param offset  the zone offset to create with, not null
     */
    private OffsetDate with(LocalDate date, ZoneOffset offset) {
        if (this.date == date && this.offset.equals(offset)) {
            return this;
        }
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return date.get(field);
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset.
     *
     * @return the zone offset, not null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified offset.
     * <p>
     * This method returns an object with the same {@code LocalDate} and the specified {@code ZoneOffset}.
     * No calculation is needed or performed.
     * For example, if this time represents {@code 2007-12-03+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 2007-12-03+03:00}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetDate} based on this date with the requested offset, not null
     */
    public OffsetDate withOffset(ZoneOffset offset) {
        DateTimes.checkNotNull(offset, "ZoneOffset must not be null");
        return with(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return date.getYear();
    }

    /**
     * Gets the month-of-year field from 1 to 12.
     * <p>
     * This method returns the month as an {@code int} from 1 to 12.
     * Application code is frequently clearer if the enum {@link Month}
     * is used by calling {@link #getMonth()}.
     *
     * @return the month-of-year, from 1 to 12
     * @see #getMonth()
     */
    public int getMonthValue() {
        return date.getMonthValue();
    }

    /**
     * Gets the month-of-year field using the {@code Month} enum.
     * <p>
     * This method returns the enum {@link Month} for the month.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link Month#getValue() int value}.
     *
     * @return the month-of-year, not null
     * @see #getMonthValue()
     */
    public Month getMonth() {
        return date.getMonth();
    }

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
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
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted date based on this date.
     * <p>
     * This adjusts the date according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * A selection of common adjustments is provided in {@link DateTimeAdjusters}.
     * These include finding the "last day of the month" and "next Wednesday".
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * In addition, all principal classes implement the {@link DateTimeAdjuster} interface,
     * including this one. For example, {@link Month} implements the adjuster interface.
     * As such, this code will compile and run:
     * <pre>
     *  date.with(Month.JULY);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return an {@code OffsetDate} based on this date with the adjustment made, not null
     * @throws CalendricalException if the adjustment cannot be made
     */
    public OffsetDate with(DateTimeAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return with((LocalDate) adjuster, offset);
        } else if (adjuster instanceof OffsetDate) {
            return with(((OffsetDate) adjuster).toLocalDate(), offset);
        }
        return (OffsetDate) adjuster.doAdjustment(this);
    }

    /**
     * Returns a copy of this date with the specified field altered.
     * <p>
     * This method returns a new date based on this date with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * In some cases, changing the specified field can cause the resulting date to become invalid,
     * such as changing the month from January to February would make the day-of-month 31 invalid.
     * In cases like this, the field is responsible for resolving the date. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return an {@code OffsetDate} based on this date with the specified field set, not null
     * @throws CalendricalException if the value is invalid
     */
    public OffsetDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            return with(date.with(field, newValue), offset);
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDate} with the year altered.
     * The offset does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @return an {@code OffsetDate} based on this date with the requested year, not null
     * @throws CalendricalException if the year value is invalid
     */
    public OffsetDate withYear(int year) {
        return with(date.withYear(year), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the month-of-year altered.
     * The offset does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @return an {@code OffsetDate} based on this date with the requested month, not null
     * @throws CalendricalException if the month-of-year value is invalid
     */
    public OffsetDate withMonth(int month) {
        return with(date.withMonth(month), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the day-of-month altered.
     * If the resulting date is invalid, an exception is thrown.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return an {@code OffsetDate} based on this date with the requested day, not null
     * @throws CalendricalException if the day-of-month value is invalid
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     */
    public OffsetDate withDayOfMonth(int dayOfMonth) {
        return with(date.withDayOfMonth(dayOfMonth), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the day-of-year altered.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return an {@code OffsetDate} based on this date with the requested day, not null
     * @throws CalendricalException if the day-of-year value is invalid
     * @throws CalendricalException if the day-of-year is invalid for the year
     */
    public OffsetDate withDayOfYear(int dayOfYear) {
        return with(date.withDayOfYear(dayOfYear), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This method returns a new date based on this date with the specified period added.
     * The calculation is delegated to the unit within the period.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return an {@code OffsetDate} based on this date with the period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetDate plus(Period period) {
        return plus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This method returns a new date based on this date with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to add to the returned date, not null
     * @param unit  the unit of the period to add, not null
     * @return an {@code OffsetDate} based on this date with the specified period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetDate plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            return with(date.plus(periodAmount, unit), offset);
        }
        return unit.doAdd(this, periodAmount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return an {@code OffsetDate} based on this date with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusYears(long years) {
        return with(date.plusYears(years), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return an {@code OffsetDate} based on this date with the months added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusMonths(long months) {
        return with(date.plusMonths(months), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return an {@code OffsetDate} based on this date with the weeks added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusWeeks(long weeks) {
        return with(date.plusWeeks(weeks), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return an {@code OffsetDate} based on this date with the days added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusDays(long days) {
        return with(date.plusDays(days), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This method returns a new date based on this date with the specified period subtracted.
     * The calculation is delegated to the unit within the period.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return an {@code OffsetDate} based on this date with the period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetDate minus(Period period) {
        return minus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This method returns a new date based on this date with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to subtract from the returned date, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an {@code OffsetDate} based on this date with the specified period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public OffsetDate minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2007-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2007-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return an {@code OffsetDate} based on this date with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusYears(long years) {
        return with(date.minusYears(years), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-02-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return an {@code OffsetDate} based on this date with the months subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusMonths(long months) {
        return with(date.minusMonths(months), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-07 minus one week would result in 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return an {@code OffsetDate} based on this date with the weeks subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusWeeks(long weeks) {
        return with(date.minusWeeks(weeks), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the specified number of days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field decrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-01 minus one day would result in 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return an {@code OffsetDate} based on this date with the days subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusDays(long days) {
        return with(date.minusDays(days), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an offset date-time formed from this date at the specified time.
     * <p>
     * This merges the two objects - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * If the offset of the time differs from the offset of the date, then the
     * result will have the offset of the date and the time will be adjusted to match.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to use, not null
     * @return the offset date-time formed from this date and the specified time, not null
     */
    public OffsetDateTime atTime(OffsetTime time) {
        return date.atTime(time.withOffsetSameInstant(offset));
    }

    /**
     * Returns an offset date-time formed from this date at the specified time.
     * <p>
     * This merges the two objects - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to use, not null
     * @return the offset date-time formed from this date and the specified time, not null
     */
    public OffsetDateTime atTime(LocalTime time) {
        return OffsetDateTime.of(date, time, offset);
    }

    /**
     * Returns an offset date-time formed from this date at the specified time.
     * <p>
     * This merges the three values - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to use, from 0 to 23
     * @param minute  the minute-of-hour to use, from 0 to 59
     * @return the offset date-time formed from this date and the specified time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public OffsetDateTime atTime(int hour, int minute) {
        return atTime(LocalTime.of(hour, minute));
    }

    /**
     * Returns an offset date-time formed from this date at the specified time.
     * <p>
     * This merges the four values - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to use, from 0 to 23
     * @param minute  the minute-of-hour to use, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return the offset date-time formed from this date and the specified time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public OffsetDateTime atTime(int hour, int minute, int second) {
        return atTime(LocalTime.of(hour, minute, second));
    }

    /**
     * Returns an offset date-time formed from this date at the specified time.
     * <p>
     * This merges the five values - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to use, from 0 to 23
     * @param minute  the minute-of-hour to use, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the offset date-time formed from this date and the specified time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public OffsetDateTime atTime(int hour, int minute, int second, int nanoOfSecond) {
        return atTime(LocalTime.of(hour, minute, second, nanoOfSecond));
    }

    /**
     * Returns an offset date-time formed from this date at the time of midnight.
     * <p>
     * This merges the two objects - {@code this} and {@link LocalTime#MIDNIGHT} -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the offset date-time formed from this date and the time of midnight, not null
     */
    public OffsetDateTime atMidnight() {
        return OffsetDateTime.ofMidnight(date, offset);
    }

    /**
     * Returns a zoned date-time from this date at the earliest valid time according
     * to the rules in the time-zone ignoring the current offset.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date is in a gap or overlap according to
     * the rules then a resolver is used to determine the resultant local time and offset.
     * This method uses the {@link ZoneResolvers#postGapPreOverlap() post-gap pre-overlap} resolver.
     * This selects the date-time immediately after a gap and the earlier offset in overlaps.
     * This combination chooses the earliest valid local time on the date, typically midnight.
     * <p>
     * To convert to a specific time in a given time-zone call {@link #atTime(LocalTime)}
     * followed by {@link OffsetDateTime#atZoneSimilarLocal(ZoneId)}.
     * <p>
     * The offset from this date is ignored during the conversion.
     * This ensures that the resultant date-time has the same date as this.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, not null
     */
    public ZonedDateTime atStartOfDayInZone(ZoneId zone) {
        return ZonedDateTime.of(date, LocalTime.MIDNIGHT, zone, ZoneResolvers.postGapPreOverlap());
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>LocalDate
     * <li>OffsetDate
     * <li>ZoneOffset
     * <li>DateTimeBuilder
     * <li>Class, returning {@code OffsetDate}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == OffsetDate.class) {
            return (R) this;
        } else if (type == LocalDate.class) {
            return (R) date;
        } else if (type == ZoneOffset.class) {
            return (R) offset;
        } else if (type == Class.class) {
            return (R) OffsetDate.class;
        } else if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(this);
        }
        return null;
    }

    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        return calendrical.with(EPOCH_DAY, date.toEpochDay());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to an {@code Instant} at midnight.
     * <p>
     * This conversion treats the time component as midnight at the start of the day.
     *
     * @return an instant equivalent to midnight at the start of this day, not null
     */
    public Instant toInstant() {
        long epochSec = toEpochSecond();
        return Instant.ofEpochSecond(epochSec, 0);
    }

    /**
     * Converts this date to a {@code LocalDate}.
     *
     * @return a local date with the same date as this instance, not null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Converts this date to midnight at the start of day in epoch seconds.
     * 
     * @return the epoch seconds value
     */
    private long toEpochSecond() {
        long epochDay = date.toEpochDay();
        long secs = epochDay * DateTimes.SECONDS_PER_DAY;
        return secs - offset.getTotalSeconds();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code OffsetDate} to another date based on the UTC equivalent
     * dates then local date.
     * <p>
     * This ordering is consistent with {@code equals()}.
     * For example, the following is the comparator order:
     * <ol>
     * <li>2008-06-29-11:00</li>
     * <li>2008-06-29-12:00</li>
     * <li>2008-06-30+12:00</li>
     * <li>2008-06-29-13:00</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local date is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with {@code equals()}.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(OffsetDate other) {
        if (offset.equals(other.offset)) {
            return date.compareTo(other.date);
        }
        int compare = DateTimes.safeCompare(toEpochSecond(), other.toEpochSecond());
        if (compare == 0) {
            compare = date.compareTo(other.date);
        }
        return compare;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant of midnight at the start of this {@code OffsetDate}
     * is after midnight at the start of the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the date. This is equivalent to using
     * {@code date1.toInstant().isAfter(date2.toInstant());}.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the instant of the specified date
     */
    public boolean isAfter(OffsetDate other) {
        return toEpochSecond() > other.toEpochSecond();
    }

    /**
     * Checks if the instant of midnight at the start of this {@code OffsetDate}
     * is before midnight at the start of the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the date. This is equivalent to using
     * {@code date1.toInstant().isBefore(date2.toInstant());}.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is before the instant of the specified date
     */
    public boolean isBefore(OffsetDate other) {
        return toEpochSecond() < other.toEpochSecond();
    }

    /**
     * Checks if the instant of midnight at the start of this {@code OffsetDate}
     * equals midnight at the start of the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals}
     * in that it only compares the instant of the date. This is equivalent to using
     * {@code date1.toInstant().equals(date2.toInstant());}.
     *
     * @param other  the other date to compare to, not null
     * @return true if the instant equals the instant of the specified date
     */
    public boolean equalInstant(OffsetDate other) {
        return toEpochSecond() == other.toEpochSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to another date.
     * <p>
     * The comparison is based on the local-date and the offset.
     * To compare for the same instant on the time-line, use {@link #equalInstant}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof OffsetDate) {
            OffsetDate other = (OffsetDate) obj;
            return date.equals(other.date) && offset.equals(other.offset);
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
        return date.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 2007-12-03+01:00}.
     * <p>
     * The output will be in the ISO-8601 format {@code yyyy-MM-ddXXXXX}.
     *
     * @return a string representation of this date, not null
     */
    @Override
    public String toString() {
        return date.toString() + offset.toString();
    }

    /**
     * Outputs this date as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
