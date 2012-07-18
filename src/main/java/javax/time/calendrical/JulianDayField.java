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
package javax.time.calendrical;

import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.FOREVER;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;

/**
 * A set of date fields that provide access to Julian Days.
 * <p>
 * The Julian Day is a standard way of expressing date and time commonly used in the scientific community.
 * It is expressed as a decimal number of whole days where days start at midday.
 * This class represents variations on Julian Days that count whole days from midnight.
 * 
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
public enum JulianDayField implements DateTimeField {

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
    JULIAN_DAY("JulianDay", DAYS, FOREVER, DateTimeValueRange.of(-365243219162L + 2440588L, 365241780471L + 2440588L)),
    /**
     * The Modified Julian Day.
     * The Modified Julian Day (MJD) is the Julian Day minus 2400000.5, with the 0.5
     * meaning that days start at midnight. This version of MJD has no decimal part.
     * <p>
     * Technically, Modified Julian Day represents a date relative to Greenwich UTC, however this
     * implementation uses the definition for a local date independent of offset/zone.
     */
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", DAYS, FOREVER, DateTimeValueRange.of(-365243219162L + 40587L, 365241780471L + 40587L)),
    /**
     * The Rate Die day count.
     * Rata Die counts whole days starting day 1 at midnight at the beginning of 0001-01-01 (ISO).
     * Technically, Rata Die represents a local date independent of offset/zone.
     */
    RATA_DIE("RataDie", DAYS, FOREVER, DateTimeValueRange.of(-365243219162L + 719163L, 365241780471L + 719163L)),
    // lots of others Truncated,Lilian, ANSI COBOL (also dotnet related), Excel?
    ;

    private static final long ED_JDN = 2440588L;  // 719163L + 1721425L
    private static final long ED_MJD = 40587L; // 719163L - 678576L;
    private static final long ED_RD = 719163L;

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final DateTimeValueRange range;

    private JulianDayField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
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
    public DateTimeValueRange range() {
        return range;
    }

    @Override
    public int compare(DateTime calendrical1, DateTime calendrical2) {
        return DateTimes.safeCompare(doGet(calendrical1), doGet(calendrical2));
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a date from a value of this field.
     * <p>
     * This allows a date to be created from a value representing the amount in terms of this field.
     * 
     * @param value  the value
     * @return the date, not null
     * @throws CalendricalException if the value exceeds the supported date range
     */
    public LocalDate createDate(long value) {
        return doSet(LocalDate.MIN_DATE, value);
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(DateTime date) {
        return range();
    }

    @Override
    public long doGet(DateTime calendrical) {
        long epDay = calendrical.get(EPOCH_DAY);
        switch (this) {
            case JULIAN_DAY: return epDay + ED_JDN;
            case MODIFIED_JULIAN_DAY: return epDay + ED_MJD;
            case RATA_DIE: return epDay + ED_RD;
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends DateTime> R doSet(R calendrical, long newValue) {
        if (range().isValidValue(newValue) == false) {
            throw new CalendricalException("Invalid value: " + name + " " + newValue);
        }
        switch (this) {
            case JULIAN_DAY: return (R) calendrical.with(EPOCH_DAY, DateTimes.safeSubtract(newValue, ED_JDN));
            case MODIFIED_JULIAN_DAY: return (R) calendrical.with(EPOCH_DAY, DateTimes.safeSubtract(newValue, ED_MJD));
            case RATA_DIE: return (R) calendrical.with(EPOCH_DAY, DateTimes.safeSubtract(newValue, ED_RD));
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends DateTime> R roll(R calendrical, long roll) {
        if (calendrical instanceof AdjustableDateTime) {
            return (R)  ((AdjustableDateTime) calendrical).plus(roll, DAYS);
        }
        throw new CalendricalException("Unable to roll");
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean resolve(DateTimeBuilder builder, long value) {
        boolean changed = false;
        if (builder.containsFieldValue(JULIAN_DAY)) {
            builder.addCalendrical(LocalDate.ofEpochDay(DateTimes.safeSubtract(builder.getFieldValue(JULIAN_DAY), ED_JDN)));
            builder.removeFieldValue(JULIAN_DAY);
            changed = true;
        }
        if (builder.containsFieldValue(MODIFIED_JULIAN_DAY)) {
            builder.addCalendrical(LocalDate.ofEpochDay(DateTimes.safeSubtract(builder.getFieldValue(MODIFIED_JULIAN_DAY), ED_MJD)));
            builder.removeFieldValue(MODIFIED_JULIAN_DAY);
            changed = true;
        }
        if (builder.containsFieldValue(RATA_DIE)) {
            builder.addCalendrical(LocalDate.ofEpochDay(DateTimes.safeSubtract(builder.getFieldValue(RATA_DIE), ED_RD)));
            builder.removeFieldValue(RATA_DIE);
            changed = true;
        }
        return changed;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

}
