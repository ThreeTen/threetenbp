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
package javax.time.extended;

import static javax.time.calendrical.LocalDateUnit.DAYS;
import static javax.time.calendrical.LocalDateUnit.FOREVER;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateField;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.PeriodUnit;

/**
 * A set of date fields that provide access to Julian Days.
 * <p>
 * The Julian Day is a standard way of expressing date and time commonly used in the scientific community.
 * It is expressed as a decimal number of whole days where days start at midday.
 * This class represents variations on Julian Days that count whole days from midnight.
 * <p>
 * This is an immutable and thread-safe enum.
 */
public enum JulianDayField implements DateField {

    /**
     * The Julian Day Number.
     * This is the integer form of the full Julian Day decimal value, {@code JDN = floor(JD)}.
     * The Julian Day is counted from zero at January 1st 4713BCE (ISO) at midday UTC.
     * <p>
     * This field will accurately reflect the midday date change if {@code LocalDateTime} is used.
     * If {@code LocalDate} is used then the value will cover midnight to midnight around the midday
     * where the Julian Day officially starts.
     * <p>
     * Technically, Julian Day represents a date relative to Greenwich UTC, however this
     * implementation uses the definition for a local date independent of offset/zone.
     */
    JULIAN_DAY("JulianDay", DAYS, FOREVER, DateTimeValueRange.of(-10000000000L, 10000000000L)),  // TODO: correct range
    /**
     * The Modified Julian Day.
     * The Modified Julian Day (MJD) is the Julian Day minus 2400000.5, with the 0.5
     * meaning that days start at midnight. This version of MJD has no decimal part.
     * <p>
     * Technically, Modified Julian Day represents a date relative to Greenwich UTC, however this
     * implementation uses the definition for a local date independent of offset/zone.
     */
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", DAYS, FOREVER, DateTimeValueRange.of(-10000000000L, 10000000000L)),  // TODO: correct range
    /**
     * The Rate Die day count.
     * Rata Die counts whole days starting day 1 at midnight at the beginning of 0001-01-01 (ISO).
     * Technically, Rata Die represents a local date independent of offset/zone.
     */
    RATA_DIE("RataDie", DAYS, FOREVER, DateTimeValueRange.of(-10000000000L, 10000000000L)),  // TODO: correct range
    // lots of others Truncated,Lilian, ANSI COBOL (also dotnet related), Excel?
    ;

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final Rules<LocalDate> dRules;
    private final Rules<LocalDateTime> dtRules;
    private final DateTimeValueRange range;

    private JulianDayField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.dRules = new DRules(this);
        this.dtRules = DateTimes.rulesForDate(this.dRules); // TODO: handle midday change in JDN
        this.range = range;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public Rules<LocalDate> getDateRules() {
        return dRules;
    }

    @Override
    public Rules<LocalDateTime> getDateTimeRules() {
        return dtRules;
    }

    @Override
    public DateTimeValueRange getValueRange() {
        return range;
    }

    @Override
    public long getValueFrom(CalendricalObject calendrical) {
        return getDateRules().get(LocalDate.from(calendrical));
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Date rules for the field.
     */
    private static final class DRules implements Rules<LocalDate> {
        private static final long ED_JDN = 2440588L;  // 719163L + 1721425L
        private static final long ED_MJD = 40587L; // 719163L - 678576L;
        private static final long ED_RD = 719163L;

        private final JulianDayField field;
        private DRules(JulianDayField field) {
            this.field = field;
        }
        @Override
        public DateTimeValueRange range(LocalDate date) {
            return field.getValueRange();
        }
        @Override
        public long get(LocalDate date) {
            switch (field) {
                case JULIAN_DAY: return date.toEpochDay() + ED_JDN;
                case MODIFIED_JULIAN_DAY: return date.toEpochDay() + ED_MJD;
                case RATA_DIE: return date.toEpochDay() + ED_RD;
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalDate set(LocalDate date, long newValue) {
            if (range(date).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch (field) {
                case JULIAN_DAY: return LocalDate.ofEpochDay(DateTimes.safeSubtract(newValue, ED_JDN));
                case MODIFIED_JULIAN_DAY: return LocalDate.ofEpochDay(DateTimes.safeSubtract(newValue, ED_MJD));
                case RATA_DIE: return LocalDate.ofEpochDay(DateTimes.safeSubtract(newValue, ED_RD));
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalDate roll(LocalDate date, long roll) {
            return date.plusDays(roll);
        }
        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            return false;  // TODO
        }
    }

}
