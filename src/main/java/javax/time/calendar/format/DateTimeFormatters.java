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

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;
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
     * Returns the ISO date formatter that formats a date without an offset.
     * <p>
     * This is the ISO-8601 extended format: yyyy-MM-dd.
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
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
            .appendValue(Year.rule(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MonthOfYear.rule(), 2)
            .appendLiteral('-')
            .appendValue(DayOfMonth.rule(), 2)
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that formats a date without an offset.
     * <p>
     * This is the ISO-8601 extended format: yyyy-DDD.
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
            .appendValue(Year.rule(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(DayOfYear.rule(), 3)
            .toFormatter();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the ISO date formatter that formats a date without an offset.
     * <p>
     * This is the ISO-8601 basic format: yyyyMMdd.
     * <p>
     * The year will print 4 digits, unless this is insufficient, in which
     * case the full year will be printed together with a positive/negative sign.
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
            .appendValue(Year.rule(), 4, 10, SignStyle.EXCEEDS_PAD)
            .appendValue(MonthOfYear.rule(), 2)
            .appendValue(DayOfMonth.rule(), 2)
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
            .appendText(DayOfWeek.rule(), TextStyle.SHORT)
            .appendLiteral(", ")
            .appendValue(DayOfMonth.rule(), 2)
            .appendLiteral(' ')
            .appendText(MonthOfYear.rule(), TextStyle.SHORT)
            .appendLiteral(' ')
            .appendValue(Year.rule(), 4, 4, SignStyle.NEGATIVE_ERROR)
            .appendLiteral(' ')
            .appendValue(HourOfDay.rule(), 2)
            .appendLiteral(':')
            .appendValue(MinuteOfHour.rule(), 2)
            .appendLiteral(':')
            .appendValue(SecondOfMinute.rule(), 2)
            .appendLiteral(' ')
            .appendOffset("Z", false, true)
            .toFormatter()
            .withLocale(Locale.ENGLISH);
    }

}
