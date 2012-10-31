/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;

import sun.util.calendar.CalendarSystem;
import sun.util.calendar.LocalGregorianCalendar;

/**
 * The Japanese Imperial calendar system.
 * <p>
 * This chronology defines the rules of the Japanese Imperial calendar system.
 * This calendar system is primarily used in Japan.
 * The Japanese Imperial calendar system is the same as the ISO calendar system
 * apart from the era-based year numbering.
 * <p>
 * Only Meiji (1865-04-07 - 1868-09-07) and later eras are supported.
 * Older eras are handled as an unknown era where the year-of-era is the ISO year.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class JapaneseChronology extends Chronology<JapaneseChronology> implements Serializable {
    // TODO: definition for unknown era may break requirement that year-of-era >= 1

    static final LocalGregorianCalendar JCAL
        = (LocalGregorianCalendar) CalendarSystem.forName("japanese");

    // Locale for creating a JapaneseImpericalCalendar.
    static final Locale LOCALE = Locale.forLanguageTag("ja-JP-u-ca-japanese");

    /**
     * Singleton instance for Japanese Chronology.
     */
    public static final JapaneseChronology INSTANCE = new JapaneseChronology();

    /**
     * The singleton instance for the before Meiji era ( - 1868-09-07)
     * which has the value -2.
     */
    public static final Era<JapaneseChronology> SEIREKI = JapaneseEra.SEIREKI;
    /**
     * The singleton instance for the Meiji era (1868-09-08 - 1912-07-29)
     * which has the value -1.
     */
    public static final Era<JapaneseChronology> MEIJI = JapaneseEra.MEIJI;
    /**
     * The singleton instance for the Taisho era (1912-07-30 - 1926-12-24)
     * which has the value 0.
     */
    public static final Era<JapaneseChronology> TAISHO = JapaneseEra.TAISHO;
    /**
     * The singleton instance for the Showa era (1926-12-25 - 1989-01-07)
     * which has the value 1.
     */
    public static final Era<JapaneseChronology> SHOWA = JapaneseEra.SHOWA;
    /**
     * The singleton instance for the Heisei era (1989-01-08 - current)
     * which has the value 2.
     */
    public static final Era<JapaneseChronology> HEISEI = JapaneseEra.HEISEI;
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Narrow names for eras.
     */
    private static final Map<String, String[]> ERA_NARROW_NAMES = new HashMap<>();
    /**
     * Short names for eras.
     */
    private static final Map<String, String[]> ERA_SHORT_NAMES = new HashMap<>();
    /**
     * Full names for eras.
     */
    private static final Map<String, String[]> ERA_FULL_NAMES = new HashMap<>();
    /**
     * Fallback language for the era names.
     */
    private static final String FALLBACK_LANGUAGE = "en";
    /**
     * Language that has the era names.
     */
    private static final String TARGET_LANGUAGE = "ja";

    /**
     * Name data.
     */
    // TODO: replace all the hard-coded Maps with locale resources
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE, new String[]{"Unknown", "\u6176", "\u660e", "\u5927", "\u662d", "\u5e73"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "Keio", "Meiji", "Taisho", "Showa", "Heisei"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE,
                new String[]{"Unknown", "\u6176\u5fdc", "\u660e\u6cbb", "\u5927\u6b63", "\u662d\u548c", "\u5e73\u6210"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restricted constructor.
     */
    private JapaneseChronology() {
    }

    /**
     * Resolve singleton.
     *
     * @return the singleton instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology - 'Japanese'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     * 
     * @return the chronology ID - 'Japanese'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Japanese";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'japanese'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     * 
     * @return the calendar system type - 'japanese'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "japanese";
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<JapaneseChronology> date(Era<JapaneseChronology> era, int yearOfEra, int month, int dayOfMonth) {
        if (era instanceof JapaneseEra == false) {
            throw new DateTimeException("Era must be JapaneseEra");
        }
        return JapaneseDate.of((JapaneseEra) era, yearOfEra, month, dayOfMonth);
    }

    @Override
    public ChronoLocalDate<JapaneseChronology> date(int prolepticYear, int month, int dayOfMonth) {
        return JapaneseDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    public ChronoLocalDate<JapaneseChronology> dateFromYearDay(int prolepticYear, int dayOfYear) {
        LocalDate date = LocalDate.ofYearDay(prolepticYear, dayOfYear);
        return date(prolepticYear, date.getMonthValue(), date.getDayOfMonth());
    }

    @Override
    public ChronoLocalDate<JapaneseChronology> date(DateTimeAccessor dateTime) {
        if (dateTime instanceof LocalDate) {
            return new JapaneseDate((LocalDate) dateTime);
        }
        if (dateTime instanceof JapaneseDate) {
            return (JapaneseDate) dateTime;
        }
        return (JapaneseDate)super.date(dateTime);
    }

    @Override
    public ChronoLocalDate<JapaneseChronology> dateFromEpochDay(long epochDay) {
        return new JapaneseDate(LocalDate.ofEpochDay(epochDay));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * Japanese calendar leap years occur exactly in line with ISO leap years.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return DateTimes.isLeapYear(prolepticYear);
    }

    @Override
    public int prolepticYear(Era<JapaneseChronology> era, int yearOfEra) {
        if (era instanceof JapaneseEra == false) {
            throw new DateTimeException("Era must be JapaneseEra");
        }
        JapaneseEra jera = (JapaneseEra) era;
        int gregorianYear = jera.getPrivateEra().getSinceDate().getYear() + yearOfEra - 1;
        if (yearOfEra == 1) {
            return gregorianYear;
        }
        LocalGregorianCalendar.Date jdate = JCAL.newCalendarDate(null);
        jdate.setEra(jera.getPrivateEra()).setDate(yearOfEra, 1, 1);
        JCAL.normalize(jdate);
        if (jdate.getNormalizedYear() == gregorianYear) {
            return gregorianYear;
        }
        throw new DateTimeException("invalid yearOfEra value");
    }

    /**
     * Returns the calendar system era object from the given numeric value.
     * This method is equivalent to a call to {@link JapaneseEra#of(int) JapaneseEra.of(eraValue)}.
     *
     * @param eraValue  the era value
     * @return the {@code JapaneseEra} for the given numeric era value
     * @throws DateTimeException if {@code eraValue} is invalid
     */
    @Override
    public Era<JapaneseChronology> eraOf(int eraValue) {
        return JapaneseEra.of(eraValue);
    }

    @Override
    public List<Era<JapaneseChronology>> eras() {
        return Arrays.<Era<JapaneseChronology>>asList(JapaneseEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(LocalDateTimeField field) {
        switch (field) {
            case DAY_OF_MONTH:
            case DAY_OF_WEEK:
            case MICRO_OF_DAY:
            case MICRO_OF_SECOND:
            case HOUR_OF_DAY:
            case HOUR_OF_AMPM:
            case MINUTE_OF_DAY:
            case MINUTE_OF_HOUR:
            case SECOND_OF_DAY:
            case SECOND_OF_MINUTE:
            case MILLI_OF_DAY:
            case MILLI_OF_SECOND:
            case NANO_OF_DAY:
            case NANO_OF_SECOND:
            case CLOCK_HOUR_OF_DAY:
            case CLOCK_HOUR_OF_AMPM:
            case EPOCH_DAY:
            case EPOCH_MONTH:
                return field.range();
        }
        Calendar jcal = Calendar.getInstance(LOCALE);
        int fieldIndex;
        switch (field) {
            case ERA:
                return DateTimeValueRange.of(jcal.getMinimum(Calendar.ERA) - JapaneseEra.ERA_OFFSET,
                                             jcal.getMaximum(Calendar.ERA) - JapaneseEra.ERA_OFFSET);
            case YEAR:
            case YEAR_OF_ERA:
                return DateTimeValueRange.of(DateTimes.MIN_YEAR, jcal.getGreatestMinimum(Calendar.YEAR),
                                             jcal.getLeastMaximum(Calendar.YEAR), DateTimes.MAX_YEAR);
            case MONTH_OF_YEAR:
                return DateTimeValueRange.of(jcal.getMinimum(Calendar.MONTH) + 1, jcal.getGreatestMinimum(Calendar.MONTH) + 1,
                                             jcal.getLeastMaximum(Calendar.MONTH) + 1, jcal.getMaximum(Calendar.MONTH) + 1);
            case WEEK_OF_YEAR:
                // TODO: revisit this when the week definition gets clear
                fieldIndex = Calendar.WEEK_OF_YEAR;
                break;
            case DAY_OF_YEAR:
                fieldIndex = Calendar.DAY_OF_YEAR;
                break;
            default:
                 // TODO: review the remaining fields
                throw new UnsupportedOperationException("Unimplementable field: " + field);
        }
        return DateTimeValueRange.of(jcal.getMinimum(fieldIndex), jcal.getGreatestMinimum(fieldIndex),
                                     jcal.getLeastMaximum(fieldIndex), jcal.getMaximum(fieldIndex));
    }

}
