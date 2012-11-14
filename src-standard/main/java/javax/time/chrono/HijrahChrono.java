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

import static javax.time.calendrical.ChronoField.EPOCH_DAY;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.time.DateTimeException;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeValueRange;

/**
 * The Hijrah calendar system.
 * <p>
 * This chronology defines the rules of the Hijrah calendar system.
 * <p>
 * The implementation follows the Freeman-Grenville algorithm (*1) and has following features.
 * <ul>
 * <li>A year has 12 months.</li>
 * <li>Over a cycle of 30 years there are 11 leap years.</li>
 * <li>There are 30 days in month number 1, 3, 5, 7, 9, and 11,
 * and 29 days in month number 2, 4, 6, 8, 10, and 12.</li>
 * <li>In a leap year month 12 has 30 days.</li>
 * <li>In a 30 year cycle, year 2, 5, 7, 10, 13, 16, 18, 21, 24,
 * 26, and 29 are leap years.</li>
 * <li>Total of 10631 days in a 30 years cycle.</li>
 * </ul>
 * <P>
 * The table shows the features described above.
 * <blockquote>
 * <table border="1">
 *   <tbody>
 *     <tr>
 *       <th># of month</th>
 *       <th>Name of month</th>
 *       <th>Number of days</th>
 *     </tr>
 *     <tr>
 *       <td>1</td>
 *       <td>Muharram</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>2</td>
 *       <td>Safar</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>3</td>
 *       <td>Rabi'al-Awwal</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>4</td>
 *       <td>Rabi'ath-Thani</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>5</td>
 *       <td>Jumada l-Ula</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>6</td>
 *       <td>Jumada t-Tania</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>7</td>
 *       <td>Rajab</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>8</td>
 *       <td>Sha`ban</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>9</td>
 *       <td>Ramadan</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>10</td>
 *       <td>Shawwal</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>11</td>
 *       <td>Dhu 'l-Qa`da</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>12</td>
 *       <td>Dhu 'l-Hijja</td>
 *       <td>29, but 30 days in years 2, 5, 7, 10,<br>
 * 13, 16, 18, 21, 24, 26, and 29</td>
 *     </tr>
 *   </tbody>
 * </table>
 * </blockquote>
 * <p>
 * (*1) The algorithm is taken from the book,
 * The Muslim and Christian Calendars by G.S.P. Freeman-Grenville.
 * <p>
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class HijrahChrono extends Chrono<HijrahChrono> implements Serializable {

    /**
     * Singleton instance of the Hijrah chronology.
     */
    public static final HijrahChrono INSTANCE = new HijrahChrono();

    /**
     * The singleton instance for the era before the current one - Before Hijrah -
     * which has the value 0.
     */
    public static final Era<HijrahChrono> ERA_BEFORE_AH = HijrahEra.BEFORE_AH;
    /**
     * The singleton instance for the current era - Hijrah - which has the value 1.
     */
    public static final Era<HijrahChrono> ERA_AH = HijrahEra.AH;
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3127340209035924785L;
    /**
     * Narrow names for eras.
     */
    private static final HashMap<String, String[]> ERA_NARROW_NAMES = new HashMap<>();
    /**
     * Short names for eras.
     */
    private static final HashMap<String, String[]> ERA_SHORT_NAMES = new HashMap<>();
    /**
     * Full names for eras.
     */
    private static final HashMap<String, String[]> ERA_FULL_NAMES = new HashMap<>();
    /**
     * Fallback language for the era names.
     */
    private static final String FALLBACK_LANGUAGE = "en";

    /**
     * Language that has the era names.
     */
    //private static final String TARGET_LANGUAGE = "ar";
    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BH", "HE"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.H.", "H.E."});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Hijrah", "Hijrah Era"});
    }

    /**
     * Restrictive constructor.
     */
    private HijrahChrono() {
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
     * Gets the ID of the chronology - 'Hijrah'.
     * <p>
     * The ID uniquely identifies the {@code Chrono}.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Hijrah'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Hijrah";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'islamicc'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     *
     * @return the calendar system type - 'islamicc'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "islamicc";
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<HijrahChrono> date(int prolepticYear, int month, int dayOfMonth) {
        return HijrahDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    public ChronoLocalDate<HijrahChrono> dateFromYearDay(int prolepticYear, int dayOfYear) {
        return HijrahDate.of(prolepticYear, 1, 1).plusDays(dayOfYear - 1);  // TODO better
    }

    @Override
    public ChronoLocalDate<HijrahChrono> date(DateTimeAccessor dateTime) {
        if (dateTime instanceof HijrahDate) {
            return (HijrahDate) dateTime;
        }
        return HijrahDate.ofEpochDay(dateTime.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return HijrahDate.isLeapYear(prolepticYear);
    }

    @Override
    public int prolepticYear(Era<HijrahChrono> era, int yearOfEra) {
        if (era instanceof HijrahEra == false) {
            throw new DateTimeException("Era must be HijrahEra");
        }
        return (era == HijrahEra.AH ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public Era<HijrahChrono> eraOf(int eraValue) {
        switch (eraValue) {
            case 0:
                return HijrahEra.BEFORE_AH;
            case 1:
                return HijrahEra.AH;
            default:
                throw new DateTimeException("invalid Hijrah era");
        }
    }

    @Override
    public List<Era<HijrahChrono>> eras() {
        return Arrays.<Era<HijrahChrono>>asList(HijrahEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(ChronoField field) {
        return field.range();
    }

}
