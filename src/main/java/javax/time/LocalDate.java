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

import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.io.Serializable;
import java.util.Objects;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.ChronoLocalDate;
import javax.time.chrono.Era;
import javax.time.chrono.ISOChronology;
import javax.time.format.CalendricalFormatter;
import javax.time.format.DateTimeFormatters;
import javax.time.format.DateTimeParseException;
import javax.time.zone.ZoneResolvers;

/**
 * A date without a time-zone in the ISO-8601 calendar system,
 * such as {@code 2007-12-03}.
 * <p>
 * {@code LocalDate} is an immutable date-time object that represents a date, often viewed
 * as year-month-day. This object can also access other date fields such as
 * day-of-year, day-of-week and week-of-year.
 * <p>
 * This class does not store or represent a time or time-zone.
 * For example, the value "2nd October 2007" can be stored in a {@code LocalDate}.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which todays's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * <p>
 * However, any application that makes use of historical dates and requires them
 * to be accurate will find the ISO-8601 rules unsuitable. In this case, the
 * application code should use {@code HistoricDate} and define an explicit
 * cutover date between the Julian and Gregorian calendar systems.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class LocalDate implements ChronoLocalDate<ISOChronology>,
        DateTime, WithAdjuster, Comparable<ChronoLocalDate<ISOChronology>>, Serializable {

    /**
     * Constant for the minimum date on the proleptic ISO calendar system, -999999999-01-01.
     * This could be used by an application as a "far past" date.
     */
    public static final LocalDate MIN_DATE = LocalDate.of(DateTimes.MIN_YEAR, 1, 1);
    /**
     * Constant for the maximum date on the proleptic ISO calendar system, +999999999-12-31.
     * This could be used by an application as a "far future" date.
     */
    public static final LocalDate MAX_DATE = LocalDate.of(DateTimes.MAX_YEAR, 12, 31);

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of days in a 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);

    /**
     * The year.
     */
    private final int year;
    /**
     * The month-of-year, not null.
     */
    private final short month;
    /**
     * The day-of-month.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static LocalDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, not null
     */
    public static LocalDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     */
    public static LocalDate now(Clock clock) {
        Objects.requireNonNull(clock, "Clock");
        // inline OffsetDate factory to avoid creating object and InstantProvider checks
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long epochSec = now.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later
        long epochDay = DateTimes.floorDiv(epochSec, DateTimes.SECONDS_PER_DAY);
        return LocalDate.ofEpochDay(epochDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDate} from a year, month and day.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the local date, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static LocalDate of(int year, Month month, int dayOfMonth) {
        YEAR.checkValidValue(year);
        Objects.requireNonNull(month, "Month");
        DAY_OF_MONTH.checkValidValue(dayOfMonth);
        return create(year, month, dayOfMonth);
    }

    /**
     * Obtains an instance of {@code LocalDate} from a year, month and day.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the local date, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static LocalDate of(int year, int month, int dayOfMonth) {
        YEAR.checkValidValue(year);
        MONTH_OF_YEAR.checkValidValue(month);
        DAY_OF_MONTH.checkValidValue(dayOfMonth);
        return create(year, Month.of(month), dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDate} from a year and day-of-year.
     * <p>
     * The day-of-year must be valid for the year, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param dayOfYear  the day-of-year to represent, from 1 to 366
     * @return the local date, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-year is invalid for the month-year
     */
    public static LocalDate ofYearDay(int year, int dayOfYear) {
        YEAR.checkValidValue(year);
        DAY_OF_YEAR.checkValidValue(dayOfYear);
        boolean leap = DateTimes.isLeapYear(year);
        if (dayOfYear == 366 && leap == false) {
            throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + year + "' is not a leap year");
        }
        Month moy = Month.of((dayOfYear - 1) / 31 + 1);
        int monthEnd = moy.firstDayOfYear(leap) + moy.length(leap) - 1;
        if (dayOfYear > monthEnd) {
            moy = moy.plus(1);
        }
        int dom = dayOfYear - moy.firstDayOfYear(leap) + 1;
        return create(year, moy, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDate} from the epoch day count.
     * <p>
     * The Epoch Day count is a simple incrementing count of days
     * where day 0 is 1970-01-01. Negative numbers represent earlier days.
     *
     * @param epochDay  the Epoch Day to convert, based on the epoch 1970-01-01
     * @return the local date, not null
     * @throws DateTimeException if the epoch days exceeds the supported date range
     */
    public static LocalDate ofEpochDay(long epochDay) {
        long zeroDay = epochDay + DAYS_0000_TO_1970;
        // find the march-based year
        zeroDay -= 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
        long adjust = 0;
        if (zeroDay < 0) {
            // adjust negative years to positive for calculation
            long adjustCycles = (zeroDay + 1) / DAYS_PER_CYCLE - 1;
            adjust = adjustCycles * 400;
            zeroDay += -adjustCycles * DAYS_PER_CYCLE;
        }
        long yearEst = (400 * zeroDay + 591) / DAYS_PER_CYCLE;
        long doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        if (doyEst < 0) {
            // fix estimate
            yearEst--;
            doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        }
        yearEst += adjust;  // reset any negative year
        int marchDoy0 = (int) doyEst;
        
        // convert march-based values back to january-based
        int marchMonth0 = (marchDoy0 * 5 + 2) / 153;
        int month = (marchMonth0 + 2) % 12 + 1;
        int dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1;
        yearEst += marchMonth0 / 10;
        
        // check year now we are certain it is correct
        int year = YEAR.checkValidIntValue(yearEst);
        return new LocalDate(year, month, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDate} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code LocalDate}.
     * 
     * @param dateTime  the date-time object to convert, not null
     * @return the local date, not null
     * @throws DateTimeException if unable to convert to a {@code LocalDate}
     */
    public static LocalDate from(DateTimeAccessor dateTime) {
        LocalDate obj = dateTime.extract(LocalDate.class);
        if (obj == null) {
            return ofEpochDay(dateTime.getLong(LocalDateTimeField.EPOCH_DAY));
        }
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to LocalDate: ", dateTime.getClass());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDate} from a text string such as {@code 2007-12-03}.
     * <p>
     * The string must represent a valid date and is parsed using
     * {@link javax.time.format.DateTimeFormatters#isoLocalDate()}.
     *
     * @param text  the text to parse such as "2007-12-03", not null
     * @return the parsed local date, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parse(CharSequence text) {
        return parse(text, DateTimeFormatters.isoLocalDate());
    }

    /**
     * Obtains an instance of {@code LocalDate} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed local date, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parse(CharSequence text, CalendricalFormatter formatter) {
        Objects.requireNonNull(formatter, "CalendricalFormatter");
        return formatter.parse(text, LocalDate.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a local date from the year, month and day fields.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, validated not null
     * @param dayOfMonth  the day-of-month to represent, validated from 1 to 31
     * @return the local date, not null
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    private static LocalDate create(int year, Month month, int dayOfMonth) {
        if (dayOfMonth > 28 && dayOfMonth > month.length(DateTimes.isLeapYear(year))) {
            if (dayOfMonth == 29) {
                throw new DateTimeException("Invalid date 'February 29' as '" + year + "' is not a leap year");
            } else {
                throw new DateTimeException("Invalid date '" + month.name() + " " + dayOfMonth + "'");
            }
        }
        return new LocalDate(year, month.getValue(), dayOfMonth);
    }

    /**
     * Resolves the date, resolving days past the end of month.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, validated from 1 to 12
     * @param dayOfMonth  the day-of-month to represent, validated from 1 to 31
     * @return the resolved date, not null
     */
    private static LocalDate resolvePreviousValid(int year, int month, int day) {
        switch (month) {
            case 2:
                day = Math.min(day, DateTimes.isLeapYear(year) ? 29 : 28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = Math.min(day, 30);
                break;
        }
        return LocalDate.of(year, month, day);
    }

    /**
     * Constructor, previously validated.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, valid for year-month, from 1 to 31
     */
    private LocalDate(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return ((LocalDateTimeField) field).isDateField();
        }
        return field != null && field.doIsSupported(this);
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case DAY_OF_MONTH: return DateTimeValueRange.of(1, lengthOfMonth());
                case DAY_OF_YEAR: return DateTimeValueRange.of(1, lengthOfYear());
                case ALIGNED_WEEK_OF_MONTH: return DateTimeValueRange.of(1,
                            getMonth() == Month.FEBRUARY && isLeapYear() == false ? 4 : 5);
                case WEEK_OF_MONTH: throw new UnsupportedOperationException("TODO");
                case WEEK_OF_WEEK_BASED_YEAR: throw new UnsupportedOperationException("TODO");
                case WEEK_OF_YEAR: throw new UnsupportedOperationException("TODO");
                case YEAR_OF_ERA: return (getYear() <= 0 ?
                        DateTimeValueRange.of(1, DateTimes.MAX_YEAR + 1) : DateTimeValueRange.of(1, DateTimes.MAX_YEAR));
            }
            return field.range();
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return get0(field);
        }
        return field.range().checkValidIntValue(getLong(field), field);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            if (field == EPOCH_DAY) {
                return toEpochDay();
            }
            if (field == EPOCH_MONTH) {
                return getEpochMonth();
            }
            return get0(field);
        }
        return field.doGet(this);
    }

    private int get0(DateTimeField field) {
        switch ((LocalDateTimeField) field) {
            case DAY_OF_WEEK: return getDayOfWeek().getValue();
            case ALIGNED_DAY_OF_WEEK_IN_MONTH: return ((day - 1) % 7) + 1;
            case ALIGNED_DAY_OF_WEEK_IN_YEAR: return ((getDayOfYear() - 1) % 7) + 1;
            case DAY_OF_MONTH: return day;
            case DAY_OF_YEAR: return getDayOfYear();
            case EPOCH_DAY: throw new DateTimeException("Field too large for an int: " + field);
            case ALIGNED_WEEK_OF_MONTH: return ((day - 1) / 7) + 1;
            case WEEK_OF_MONTH: throw new UnsupportedOperationException("TODO");
            case WEEK_OF_WEEK_BASED_YEAR: throw new UnsupportedOperationException("TODO");
            case ALIGNED_WEEK_OF_YEAR: return ((getDayOfYear() - 1) / 7) + 1;
            case WEEK_OF_YEAR: throw new UnsupportedOperationException("TODO");
            case MONTH_OF_YEAR: return month;
            case EPOCH_MONTH: throw new DateTimeException("Field too large for an int: " + field);
            case WEEK_BASED_YEAR: throw new UnsupportedOperationException("TODO");
            case YEAR_OF_ERA: return (year >= 1 ? year : 1 - year);
            case YEAR: return year;
            case ERA: return (year >= 1 ? 1 : 0);
        }
        throw new DateTimeException("Unsupported field: " + field.getName());
    }

    private long getEpochMonth() {
        return ((year - 1970) * 12L) + (month - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the ISO calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The ISO-8601 calendar system is the modern civil calendar system used today
     * in most of the world. It is equivalent to the proleptic Gregorian calendar
     * system, in which todays's rules for leap years are applied for all time.
     *
     * @return the ISO chronology, not null
     */
    @Override
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    /**
     * Gets the era, as defined by the calendar system.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 (ISO) must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * All correctly implemented {@code Era} classes are singletons, thus it
     * is valid code to write {@code date.getEra() == SomeEra.ERA_NAME)}.
     *
     * @return the era, of the correct type for this chronology, not null
     */
    @Override
    public Era<ISOChronology> getEra() {
        return getChronology().eraOf(DateTimes.safeToInt(get(LocalDateTimeField.ERA)));
    }

    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
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
        return month;
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
        return Month.of(month);
    }

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return day;
    }

    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return getMonth().firstDayOfYear(isLeapYear()) + day - 1;
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
        int dow0 = DateTimes.floorMod(toEpochDay() + 3, 7);
        return DayOfWeek.of(dow0 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the ISO proleptic
     * calendar system rules.
     * <p>
     * This method applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @return true if the year is leap, false otherwise
     */
    @Override
    public boolean isLeapYear() {
        return DateTimes.isLeapYear(year);
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * For example, a date in January would return 31.
     *
     * @return the length of the month in days
     */
    @Override
    public int lengthOfMonth() {
        switch (month) {
            case 2:
                return (isLeapYear() ? 29 : 28);
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Returns the length of the year represented by this date.
     * <p>
     * This returns the length of the year in days, either 365 or 366.
     *
     * @return 366 if the year is leap, 365 otherwise
     */
    @Override
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
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
     * In addition, all principal classes implement the {@link WithAdjuster} interface,
     * including this one. For example, {@link Month} implements the adjuster interface.
     * As such, this code will compile and run:
     * <pre>
     *  date.with(Month.JULY);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a {@code LocalDate} based on this date with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     */
    @Override
    public LocalDate with(WithAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return (LocalDate) adjuster;
        }
        return (LocalDate) adjuster.doWithAdjustment(this);
    }

    /**
     * Returns a copy of this date with the specified field altered.
     * <p>
     * This method returns a new date based on this date with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
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
     * @return a {@code LocalDate} based on this date with the specified field set, not null
     * @throws DateTimeException if the value is invalid
     */
    @Override
    public LocalDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case DAY_OF_WEEK: return plusDays(newValue - getDayOfWeek().getValue());
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: return plusDays(newValue - getLong(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: return plusDays(newValue - getLong(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case DAY_OF_MONTH: return withDayOfMonth((int) newValue);
                case DAY_OF_YEAR: return withDayOfYear((int) newValue);
                case EPOCH_DAY: return LocalDate.ofEpochDay(newValue);
                case ALIGNED_WEEK_OF_MONTH: return plusWeeks(newValue - getLong(ALIGNED_WEEK_OF_MONTH));
                case WEEK_OF_MONTH: throw new UnsupportedOperationException("TODO");
                case WEEK_OF_WEEK_BASED_YEAR: throw new UnsupportedOperationException("TODO");
                case ALIGNED_WEEK_OF_YEAR: return plusWeeks(newValue - getLong(ALIGNED_WEEK_OF_YEAR));
                case WEEK_OF_YEAR: throw new UnsupportedOperationException("TODO");
                case MONTH_OF_YEAR: return withMonth((int) newValue);
                case EPOCH_MONTH: return plusMonths(newValue - getLong(EPOCH_MONTH));
                case WEEK_BASED_YEAR: throw new UnsupportedOperationException("TODO");
                case YEAR_OF_ERA: return withYear((int) (year >= 1 ? newValue : 1 - newValue));
                case YEAR: return withYear((int) newValue);
                case ERA: return (getLong(ERA) == newValue ? this : withYear(1 - year));
            }
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDate} with the year altered.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @return a {@code LocalDate} based on this date with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    public LocalDate withYear(int year) {
        if (this.year == year) {
            return this;
        }
        YEAR.checkValidValue(year);
        return resolvePreviousValid(year, month, day);
    }

    /**
     * Returns a copy of this {@code LocalDate} with the month-of-year altered.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @return a {@code LocalDate} based on this date with the requested month, not null
     * @throws DateTimeException if the month-of-year value is invalid
     */
    public LocalDate withMonth(int month) {
        if (this.month == month) {
            return this;
        }
        MONTH_OF_YEAR.checkValidValue(month);
        return resolvePreviousValid(year, month, day);
    }

    /**
     * Returns a copy of this {@code LocalDate} with the day-of-month altered.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return a {@code LocalDate} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-month value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public LocalDate withDayOfMonth(int dayOfMonth) {
        if (this.day == dayOfMonth) {
            return this;
        }
        return of(year, month, dayOfMonth);
    }

    /**
     * Returns a copy of this {@code LocalDate} with the day-of-year altered.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code LocalDate} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-year value is invalid
     * @throws DateTimeException if the day-of-year is invalid for the year
     */
    public LocalDate withDayOfYear(int dayOfYear) {
        if (this.getDayOfYear() == dayOfYear) {
            return this;
        }
        return ofYearDay(year, dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This method returns a new date based on this date with the specified period added.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link javax.time.calendrical.DateTime.PlusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #plus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDate} based on this date with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public LocalDate plus(PlusAdjuster adjuster) {
        return (LocalDate) adjuster.doPlusAdjustment(this);
    }

    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This method returns a new date based on this date with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the unit to add to the returned date, not null
     * @param unit  the unit of the period to add, not null
     * @return a {@code LocalDate} based on this date with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    @Override
    public LocalDate plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            switch (f) {
                case DAYS: return plusDays(amountToAdd);
                case WEEKS: return plusWeeks(amountToAdd);
                case MONTHS: return plusMonths(amountToAdd);
                case QUARTER_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 6);  // no overflow (256 is multiple of 2)
                case WEEK_BASED_YEARS: throw new UnsupportedOperationException("TODO");
                case YEARS: return plusYears(amountToAdd);
                case DECADES: return plusYears(DateTimes.safeMultiply(amountToAdd, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(amountToAdd, 100));
                case MILLENNIA: return plusYears(DateTimes.safeMultiply(amountToAdd, 1000));
                case ERAS: return with(ERA, DateTimes.safeAdd(getLong(ERA), amountToAdd));
            }
            throw new DateTimeException("Unsupported unit: " + unit.getName());
        }
        return unit.doAdd(this, amountToAdd);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDate} with the specified period in years added.
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
     * @param yearsToAdd  the years to add, may be negative
     * @return a {@code LocalDate} based on this date with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(year + yearsToAdd);  // safe overflow
        return resolvePreviousValid(newYear, month, day);
    }

    /**
     * Returns a copy of this {@code LocalDate} with the specified period in months added.
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
     * @param monthsToAdd  the months to add, may be negative
     * @return a {@code LocalDate} based on this date with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long monthCount = year * 12L + (month - 1);
        long calcMonths = monthCount + monthsToAdd;  // safe overflow
        int newYear = YEAR.checkValidIntValue(DateTimes.floorDiv(calcMonths, 12));
        int newMonth = DateTimes.floorMod(calcMonths, 12) + 1;
        return resolvePreviousValid(newYear, newMonth, day);
    }

    /**
     * Returns a copy of this {@code LocalDate} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeksToAdd  the weeks to add, may be negative
     * @return a {@code LocalDate} based on this date with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate plusWeeks(long weeksToAdd) {
        return plusDays(DateTimes.safeMultiply(weeksToAdd, 7));
    }

    /**
     * Returns a copy of this {@code LocalDate} with the specified number of days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToAdd  the days to add, may be negative
     * @return a {@code LocalDate} based on this date with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate plusDays(long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        long mjDay = DateTimes.safeAdd(toEpochDay(), daysToAdd);
        return LocalDate.ofEpochDay(mjDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This method returns a new date based on this date with the specified period subtracted.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link javax.time.calendrical.DateTime.MinusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #minus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDate} based on this date with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public LocalDate minus(MinusAdjuster adjuster) {
        return (LocalDate) adjuster.doMinusAdjustment(this);
    }

    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This method returns a new date based on this date with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount of the unit to subtract from the returned date, not null
     * @param unit  the unit of the period to subtract, not null
     * @return a {@code LocalDate} based on this date with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    @Override
    public LocalDate minus(long amountToSubtract, PeriodUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDate} with the specified period in years subtracted.
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
     * @param yearsToSubtract  the years to subtract, may be negative
     * @return a {@code LocalDate} based on this date with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate minusYears(long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
    }

    /**
     * Returns a copy of this {@code LocalDate} with the specified period in months subtracted.
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
     * @param monthsToSubtract  the months to subtract, may be negative
     * @return a {@code LocalDate} based on this date with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate minusMonths(long monthsToSubtract) {
        return (monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract));
    }

    /**
     * Returns a copy of this {@code LocalDate} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-07 minus one week would result in 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeksToSubtract  the weeks to subtract, may be negative
     * @return a {@code LocalDate} based on this date with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate minusWeeks(long weeksToSubtract) {
        return (weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeksToSubtract));
    }

    /**
     * Returns a copy of this {@code LocalDate} with the specified number of days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field decrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-01 minus one day would result in 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToSubtract  the days to subtract, may be negative
     * @return a {@code LocalDate} based on this date with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDate minusDays(long daysToSubtract) {
        return (daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a local date-time formed from this date at the specified offset time.
     * <p>
     * This merges the two objects - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offsetTime  the offset time to use, not null
     * @return the offset date-time formed from this date and the specified time, not null
     */
    public OffsetDateTime atTime(OffsetTime offsetTime) {
        return OffsetDateTime.of(this, offsetTime);
    }

    /**
     * Returns a local date-time formed from this date at the specified time.
     * <p>
     * This merges the two objects - {@code this} and the specified time -
     * to form an instance of {@code LocalDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param localTime  the local time to use, not null
     * @return the local date-time formed from this date and the specified time, not null
     */
    @Override
    public LocalDateTime atTime(LocalTime localTime) {
        return LocalDateTime.of(this, localTime);
    }

    /**
     * Returns a local date-time formed from this date at the specified time.
     * <p>
     * This merges the three values - {@code this} and the specified time -
     * to form an instance of {@code LocalDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to use, from 0 to 23
     * @param minute  the minute-of-hour to use, from 0 to 59
     * @return the local date-time formed from this date and the specified time, not null
     * @throws DateTimeException if the value of any field is out of range
     */
    public LocalDateTime atTime(int hour, int minute) {
        return atTime(LocalTime.of(hour, minute));
    }

    /**
     * Returns a local date-time formed from this date at the specified time.
     * <p>
     * This merges the four values - {@code this} and the specified time -
     * to form an instance of {@code LocalDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to use, from 0 to 23
     * @param minute  the minute-of-hour to use, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return the local date-time formed from this date and the specified time, not null
     * @throws DateTimeException if the value of any field is out of range
     */
    public LocalDateTime atTime(int hour, int minute, int second) {
        return atTime(LocalTime.of(hour, minute, second));
    }

    /**
     * Returns a local date-time formed from this date at the specified time.
     * <p>
     * This merges the five values - {@code this} and the specified time -
     * to form an instance of {@code LocalDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to use, from 0 to 23
     * @param minute  the minute-of-hour to use, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the local date-time formed from this date and the specified time, not null
     * @throws DateTimeException if the value of any field is out of range
     */
    public LocalDateTime atTime(int hour, int minute, int second, int nanoOfSecond) {
        return atTime(LocalTime.of(hour, minute, second, nanoOfSecond));
    }

    /**
     * Returns an offset date formed from this time and the specified offset.
     * <p>
     * This merges the two objects - {@code this} and the specified offset -
     * to form an instance of {@code OffsetDate}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset date formed from this date and the specified offset, not null
     */
    public OffsetDate atOffset(ZoneOffset offset) {
        return OffsetDate.of(this, offset);
    }

    /**
     * Returns a zoned date-time from this date at the earliest valid time according
     * to the rules in the time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date is in a gap or overlap according to
     * the rules then a resolver is used to determine the resultant local time and offset.
     * This method uses the {@link ZoneResolvers#postGapPreOverlap() post-gap pre-overlap} resolver.
     * This selects the date-time immediately after a gap and the earlier offset in overlaps.
     * This combination chooses the earliest valid local time on the date, typically midnight.
     * <p>
     * To convert to a specific time in a given time-zone call {@link #atTime(LocalTime)}
     * followed by {@link LocalDateTime#atZone(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, not null
     */
    public ZonedDateTime atStartOfDayInZone(ZoneId zone) {
        return ZonedDateTime.of(this, LocalTime.MIDNIGHT, zone, ZoneResolvers.postGapPreOverlap());
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTimeAccessor} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>LocalDate
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == LocalDate.class) {
            return (R) this;
        }
        return null;
    }

    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime.with(EPOCH_DAY, toEpochDay());
    }

    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof LocalDate == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        LocalDate end = (LocalDate) endDateTime;
        if (unit instanceof LocalPeriodUnit) {
            switch ((LocalPeriodUnit) unit) {
                case DAYS: return daysUntil(end);
                case WEEKS: return daysUntil(end) / 7;
                case MONTHS: return monthsUntil(end);
                case QUARTER_YEARS: return monthsUntil(end) / 3;
                case HALF_YEARS: return monthsUntil(end) / 6;
                case WEEK_BASED_YEARS: throw new UnsupportedOperationException("TODO");
                case YEARS: return monthsUntil(end) / 12;
                case DECADES: return monthsUntil(end) / 120;
                case CENTURIES: return monthsUntil(end) / 1200;
                case MILLENNIA: return monthsUntil(end) / 12000;
                case ERAS: return end.getLong(ERA) - getLong(ERA);
            }
            throw new DateTimeException("Unsupported unit: " + unit.getName());
        }
        return unit.between(this, endDateTime).getAmount();
    }

    long daysUntil(LocalDate end) {
        return end.toEpochDay() - toEpochDay();  // no overflow
    }

    private long monthsUntil(LocalDate end) {
        long packed1 = getEpochMonth() * 32L + getDayOfMonth();  // no overflow
        long packed2 = end.getEpochMonth() * 32L + end.getDayOfMonth();  // no overflow
        return (packed2 - packed1) / 32;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this {@code LocalDate} to Epoch Days.
     * <p>
     * The Epoch Day count is a simple incrementing count of days
     * where day 0 is 1970-01-01.
     *
     * @return the Epoch Day equivalent to this date
     */
    private long toEpochDay() {
        long y = year;
        long m = month;
        long total = 0;
        total += 365 * y;
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
        } else {
            total -= y / -4 - y / -100 + y / -400;
        }
        total += ((367 * m - 362) / 12);
        total += day - 1;
        if (m > 2) {
            total--;
            if (isLeapYear() == false) {
                total--;
            }
        }
        return total - DAYS_0000_TO_1970;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code LocalDate} to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(ChronoLocalDate<ISOChronology> other) {
        LocalDate otherDate = (LocalDate)other;
        int cmp = (year - otherDate.year);
        if (cmp == 0) {
            cmp = (month - otherDate.month);
            if (cmp == 0) {
                cmp = (day - otherDate.day);
            }
        }
        return cmp;
    }

    /**
     * Checks if this {@code LocalDate} is after the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     */
    @Override
    public boolean isAfter(ChronoLocalDate<ISOChronology> other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this {@code LocalDate} is before the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is before the specified date
     */
    @Override
    public boolean isBefore(ChronoLocalDate<ISOChronology> other) {
        return compareTo(other) < 0;
    }

    /**
     * Checks if the underlying date of this {@code ChronoLocalDate} is equal to the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.toLocalDate().equals(date2.toLocalDate())}.
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is equal to the specified date
     */
    @Override
    public boolean equalDate(ChronoLocalDate<ISOChronology> other) {
        return this.getLong(LocalDateTimeField.EPOCH_DAY) == other.getLong(LocalDateTimeField.EPOCH_DAY);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     * <p>
     * Only objects of type {@code LocalDate} are compared, other types return false.
     * To compare the date of two {@code DateTimeAccessor} instances, use
     * {@link LocalDateTimeField#EPOCH_DAY} as a comparator.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LocalDate) {
            LocalDate other = (LocalDate) obj;
            return (year == other.year && month == other.month && day == other.day);
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
        int yearValue = year;
        int monthValue = month;
        int dayValue = day;
        return (yearValue & 0xFFFFF800) ^ ((yearValue << 11) + (monthValue << 6) + (dayValue));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 2007-12-03}.
     * <p>
     * The output will be in the ISO-8601 format {@code yyyy-MM-dd}.
     *
     * @return a string representation of this date, not null
     */
    @Override
    public String toString() {
        int yearValue = year;
        int monthValue = month;
        int dayValue = day;
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(10);
        if (absYear < 1000) {
            if (yearValue < 0) {
                buf.append(yearValue - 10000).deleteCharAt(1);
            } else {
                buf.append(yearValue + 10000).deleteCharAt(0);
            }
        } else {
            if (yearValue > 9999) {
                buf.append('+');
            }
            buf.append(yearValue);
        }
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .append(dayValue < 10 ? "-0" : "-")
            .append(dayValue)
            .toString();
    }

    /**
     * Outputs this date as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws DateTimeException if an error occurs during printing
     */
    @Override
    public String toString(CalendricalFormatter formatter) {
        Objects.requireNonNull(formatter, "CalendricalFormatter");
        return formatter.print(this);
    }

}
