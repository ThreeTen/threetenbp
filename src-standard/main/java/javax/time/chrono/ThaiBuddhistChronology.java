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

import static javax.time.calendrical.LocalDateTimeField.WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.io.Serializable;
import java.util.HashMap;

import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;

/**
 * The Thai Buddhist calendar system.
 * <p>
 * This chronology defines the rules of the Thai Buddhist calendar system.
 * This calendar system is primarily used in Thailand.
 * The Thai Buddhist calendar system is the same as the ISO calendar system apart from the year.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class ThaiBuddhistChronology extends Chronology implements Serializable {

    /**
     * Singleton instance.
     */
    public static final ThaiBuddhistChronology INSTANCE = new ThaiBuddhistChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Containing the offset to add to the ISO year.
     */
    static final int YEARS_DIFFERENCE = 543;
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
    private static final String TARGET_LANGUAGE = "th";
    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BB", "BE"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"BB", "BE"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.B.", "B.E."});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE,
                new String[]{"\u0e1e.\u0e28.",
                "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Buddhist", "Budhhist Era"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE,
                new String[]{"\u0e1e\u0e38\u0e17\u0e18\u0e28\u0e31\u0e01\u0e23\u0e32\u0e0a",
                "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48"});
    }

    /**
     * Restrictive constructor.
     */
    private ThaiBuddhistChronology() {
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
    @Override
    public String getName() {
        return "ThaiBuddhist";
    }

    @Override
    protected String getLocaleId() {
        return "buddhist";
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoDate date(int prolepticYear, int month, int dayOfMonth) {
        return ThaiBuddhistDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    public ChronoDate dateFromYearDay(int prolepticYear, int dayOfYear) {
        return ThaiBuddhistDate.ofYearDay(prolepticYear, dayOfYear);
    }

    @Override
    public ChronoDate date(DateTime calendrical) {
        if (calendrical instanceof LocalDate) {
            return ThaiBuddhistDate.from((LocalDate) calendrical);
        }
        if (calendrical instanceof ThaiBuddhistDate) {
            return (ThaiBuddhistDate) calendrical;
        }
        return super.date(calendrical);
    }

    @Override
    public ChronoDate dateFromEpochDay(long epochDay) {
        return ThaiBuddhistDate.ofEpochDay(epochDay);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return DateTimes.isLeapYear(prolepticYear);
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof ThaiBuddhistEra == false) {
            throw new DateTimeException("Era must be ThaiBuddhistEra");
        }
        return (era == ThaiBuddhistEra.BUDDHIST ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public ThaiBuddhistEra createEra(int eraValue) {
        return ThaiBuddhistEra.of(eraValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(LocalDateTimeField field) {
        switch (field) {
            case WEEK_BASED_YEAR: {
                DateTimeValueRange range = WEEK_BASED_YEAR.range();
                return DateTimeValueRange.of(range.getMinimum() + YEARS_DIFFERENCE, range.getMaximum() + YEARS_DIFFERENCE);
            }
            case YEAR_OF_ERA: {
                DateTimeValueRange range = YEAR.range();
                return DateTimeValueRange.of(1, -(range.getMinimum() + YEARS_DIFFERENCE) + 1, range.getMaximum() + YEARS_DIFFERENCE);
            }
            case YEAR: {
                DateTimeValueRange range = YEAR.range();
                return DateTimeValueRange.of(range.getMinimum() + YEARS_DIFFERENCE, range.getMaximum() + YEARS_DIFFERENCE);
            }
        }
        return field.range();
    }

}
