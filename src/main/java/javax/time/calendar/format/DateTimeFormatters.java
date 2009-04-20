/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.format;

import java.util.Locale;

import javax.time.calendar.ISOChronology;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * Provides common implementations of <code>DateTimeFormatter</code>.
 * <p>
 * DateTimeFormatters is a utility class.
 * All formatters returned are immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public class DateTimeFormatters {

    /**
     * Private constructor since this is a utility class
     */
    private DateTimeFormatters() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that prints/parses a local date without an offset,
     * such as '2007-12-03'.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>yyyy-MM-dd</code>
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
     *
     * @return the ISO date formatter, never null
     */
    public static DateTimeFormatter isoLocalDate() {
        return ISO_LOCAL_DATE;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_LOCAL_DATE;
    static {
        ISO_LOCAL_DATE = new DateTimeFormatterBuilder()
            .appendValue(ISOChronology.yearRule(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(ISOChronology.monthOfYearRule(), 2)
            .appendLiteral('-')
            .appendValue(ISOChronology.dayOfMonthRule(), 2)
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that prints/parses an offset date with an offset,
     * such as '2007-12-03+01:00'.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>yyyy-MM-ddZZ</code>
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
     * <p>
     * The offset will print and parse an offset with seconds even though that
     * is not part of the ISO-8601 standard.
     *
     * @return the ISO date formatter, never null
     */
    public static DateTimeFormatter isoOffsetDate() {
        return ISO_OFFSET_DATE;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_OFFSET_DATE;
    static {
        ISO_OFFSET_DATE = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE)
            .appendOffsetId()
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that prints/parses a date, with the
     * offset and zone if available, such as '2007-12-03', '2007-12-03+01:00'
     * or '2007-12-03+01:00[Europe/Paris]'.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>yyyy-MM-dd[ZZ['['{ZoneId}']']]</code>
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
     * <p>
     * The offset will print and parse an offset with seconds even though that
     * is not part of the ISO-8601 standard.
     *
     * @return the ISO date formatter, never null
     */
    public static DateTimeFormatter isoDate() {
        return ISO_DATE;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_DATE;
    static {
        ISO_DATE = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE)
            .optionalStart()
            .appendOffsetId()
            .optionalStart()
            .appendLiteral('[')
            .appendZoneId()
            .appendLiteral(']')
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO time formatter that prints/parses a local time, without an offset
     * such as '10:15:30'.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>HH:mm[:ss[.SSS]]</code>
     *
     * @return the ISO time formatter, never null
     */
    public static DateTimeFormatter isoLocalTime() {
        return ISO_LOCAL_TIME;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_LOCAL_TIME;
    static {
        ISO_LOCAL_TIME = new DateTimeFormatterBuilder()
            .appendValue(ISOChronology.hourOfDayRule(), 2)
            .appendLiteral(':')
            .appendValue(ISOChronology.minuteOfHourRule(), 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ISOChronology.secondOfMinuteRule(), 2)
            .optionalStart()
            .appendFraction(ISOChronology.nanoOfSecondRule(), 0, 9)
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO time formatter that prints/parses a local time, with an offset
     * such as '10:15:30+01:00'.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>HH:mm[:ss[.SSS]]</code>
     *
     * @return the ISO time formatter, never null
     */
    public static DateTimeFormatter isoOffsetTime() {
        return ISO_OFFSET_TIME;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_OFFSET_TIME;
    static {
        ISO_OFFSET_TIME = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_TIME)
            .appendOffsetId()
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO time formatter that prints/parses a time, with the
     * offset and zone if available, such as '10:15:30', '10:15:30+01:00'
     * or '10:15:30+01:00[Europe/Paris]'.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>HH:mm[:ss[.SSS]][ZZ['['{ZoneId}']']]</code>
     * <p>
     * The offset will print and parse an offset with seconds even though that
     * is not part of the ISO-8601 standard.
     *
     * @return the ISO date formatter, never null
     */
    public static DateTimeFormatter isoTime() {
        return ISO_TIME;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_TIME;
    static {
        ISO_TIME = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_TIME)
            .optionalStart()
            .appendOffsetId()
            .optionalStart()
            .appendLiteral('[')
            .appendZoneId()
            .appendLiteral(']')
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that prints/parses a date without an offset.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>yyyy-DDD</code>
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
     *
     * @return the ISO ordinal date formatter, never null
     */
    public static DateTimeFormatter isoOrdinalDate() {
        return ISO_ORDINAL_DATE;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_ORDINAL_DATE;
    static {
        ISO_ORDINAL_DATE = new DateTimeFormatterBuilder()
            .appendValue(ISOChronology.yearRule(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(ISOChronology.dayOfYearRule(), 3)
            .optionalStart()
            .appendOffset("Z", true, false)
            .optionalStart()
            .appendLiteral('[')
            .appendZoneId()
            .appendLiteral(']')
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that prints/parses a date without an offset.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>yyyy-Www-D</code>
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
     *
     * @return the ISO week date formatter, never null
     */
    public static DateTimeFormatter isoWeekDate() {
        return ISO_WEEK_DATE;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter ISO_WEEK_DATE;
    static {
        ISO_WEEK_DATE = new DateTimeFormatterBuilder()
            .appendValue(ISOChronology.weekBasedYearRule(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral("-W")
            .appendValue(ISOChronology.weekOfWeekBasedYearRule(), 2)
            .appendLiteral('-')
            .appendValue(ISOChronology.dayOfWeekRule(), 1)
            .optionalStart()
            .appendOffset("Z", true, false)
            .optionalStart()
            .appendLiteral('[')
            .appendZoneId()
            .appendLiteral(']')
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that prints/parses a date without an offset.
     * <p>
     * This is the ISO-8601 extended format:<br />
     * <code>yyyyMMdd</code>
     * <p>
     * The year is limited to printing and parsing 4 digits, as the lack of
     * separators makes it impossible to parse more than 4 digits.
     *
     * @return the ISO date formatter, never null
     */
    public static DateTimeFormatter basicIsoDate() {
        return BASIC_ISO_DATE;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter BASIC_ISO_DATE;
    static {
        BASIC_ISO_DATE = new DateTimeFormatterBuilder()
            .appendValue(ISOChronology.yearRule(), 4)
            .appendValue(ISOChronology.monthOfYearRule(), 2)
            .appendValue(ISOChronology.dayOfMonthRule(), 2)
            .optionalStart()
            .appendOffset("Z", false, false)
            .optionalStart()
            .appendLiteral('[')
            .appendZoneId()
            .appendLiteral(']')
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the RFC-1123 date-time formatter.
     * <p>
     * This is the RFC-1123 format: EEE, dd MMM yyyy HH:mm:ss Z.
     * This is the updated replacement for RFC-822 which had a two digit year.
     * <p>
     * The year will print 4 digits, and only the range 0000 to 9999 is supported.
     *
     * @return the ISO date formatter, never null
     */
    public static DateTimeFormatter rfc1123() {
        return RFC_1123_DATE_TIME;
    }

    /** Singleton date formatter. */
    private static final DateTimeFormatter RFC_1123_DATE_TIME;
    static {
        RFC_1123_DATE_TIME = new DateTimeFormatterBuilder()
            .appendText(ISOChronology.dayOfWeekRule(), TextStyle.SHORT)
            .appendLiteral(", ")
            .appendValue(ISOChronology.dayOfMonthRule(), 2)
            .appendLiteral(' ')
            .appendText(ISOChronology.monthOfYearRule(), TextStyle.SHORT)
            .appendLiteral(' ')
            .appendValue(ISOChronology.yearRule(), 4, 4, SignStyle.NOT_NEGATIVE)
            .appendLiteral(' ')
            .appendValue(ISOChronology.hourOfDayRule(), 2)
            .appendLiteral(':')
            .appendValue(ISOChronology.minuteOfHourRule(), 2)
            .appendLiteral(':')
            .appendValue(ISOChronology.secondOfMinuteRule(), 2)
            .appendLiteral(' ')
            .appendOffset("Z", false, false)
            .toFormatter()
            .withLocale(Locale.ENGLISH);
    }

}
