/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.temporal;

import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.FOREVER;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeBuilder;
import org.threeten.bp.jdk8.Jdk8Methods;

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
public enum JulianFields implements TemporalField {

    /**
     * Julian Day field.
     * <p>
     * This is an integer-based version of the Julian Day Number.
     * Julian Day is a well-known system that represents the count of whole days since day 0,
     * which is defined to be January 1, 4713 BCE in the Julian calendar, and -4713-11-24 Gregorian.
     * The field  has "JulianDay" as 'name', and 'DAYS' as 'baseUnit'.
     * The field always refers to the local date-time, ignoring the offset or zone.
     * <p>
     * For date-times, 'JULIAN_DAY.doGet()' assumes the same value from
     * midnight until just before the next midnight.
     * When 'JULIAN_DAY.doWith()' is applied to a date-time, the time of day portion remains unaltered.
     * 'JULIAN_DAY.doWith()' and 'JULIAN_DAY.doGet()' only apply to {@code DateTime} objects that
     * can be converted into {@link ChronoField#EPOCH_DAY}.
     * A {@link DateTimeException} is thrown for any other type of object.
     * <p>
     * <h4>Astronomical and Scientific Notes</h4>
     * The standard astronomical definition uses a fraction to indicate the time-of-day,
     * thus 3.25 would represent the time 18:00, since days start at midday.
     * This implementation uses an integer and days starting at midnight.
     * The integer value for the Julian Day Number is the astronomical Julian Day value at midday
     * of the date in question.
     * This amounts to the astronomical Julian Day, rounded to an integer {@code JDN = floor(JD + 0.5)}.
     * <p>
     * <pre>
     *  | ISO date          |  Julian Day Number | Astronomical Julian Day |
     *  | 1970-01-01T00:00  |         2,440,588  |         2,440,587.5     |
     *  | 1970-01-01T06:00  |         2,440,588  |         2,440,587.75    |
     *  | 1970-01-01T12:00  |         2,440,588  |         2,440,588.0     |
     *  | 1970-01-01T18:00  |         2,440,588  |         2,440,588.25    |
     *  | 1970-01-02T00:00  |         2,440,589  |         2,440,588.5     |
     *  | 1970-01-02T06:00  |         2,440,589  |         2,440,588.75    |
     *  | 1970-01-02T12:00  |         2,440,589  |         2,440,589.0     |
     * </pre>
     * <p>
     * Julian Days are sometimes taken to imply Universal Time or UTC, but this
     * implementation always uses the Julian Day number for the local date,
     * regardless of the offset or time-zone.
     */
    // 719163L + 1721425L = 2440588L
    JULIAN_DAY("JulianDay", DAYS, FOREVER, 2440588L),
    /**
     * Modified Julian Day field.
     * <p>
     * This is an integer-based version of the Modified Julian Day Number.
     * Modified Julian Day (MJD) is a well-known system that counts days continuously.
     * It is defined relative to astronomical Julian Day as  {@code MJD = JD - 2400000.5}.
     * Each Modified Julian Day runs from midnight to midnight.
     * The field always refers to the local date-time, ignoring the offset or zone.
     * <p>
     * For date-times, 'MODIFIED_JULIAN_DAY.doGet()' assumes the same value from
     * midnight until just before the next midnight.
     * When 'MODIFIED_JULIAN_DAY.doWith()' is applied to a date-time, the time of day portion remains unaltered.
     * 'MODIFIED_JULIAN_DAY.doWith()' and 'MODIFIED_JULIAN_DAY.doGet()' only apply to {@code DateTime} objects
     * that can be converted into {@link ChronoField#EPOCH_DAY}.
     * A {@link DateTimeException} is thrown for any other type of object.
     * <p>
     * This implementation is an integer version of MJD with the decimal part rounded to floor.
     * <p>
     * <h4>Astronomical and Scientific Notes</h4>
     * <pre>
     *  | ISO date          | Modified Julian Day |      Decimal MJD |
     *  | 1970-01-01T00:00  |             40,587  |       40,587.0   |
     *  | 1970-01-01T06:00  |             40,587  |       40,587.25  |
     *  | 1970-01-01T12:00  |             40,587  |       40,587.5   |
     *  | 1970-01-01T18:00  |             40,587  |       40,587.75  |
     *  | 1970-01-02T00:00  |             40,588  |       40,588.0   |
     *  | 1970-01-02T06:00  |             40,588  |       40,588.25  |
     *  | 1970-01-02T12:00  |             40,588  |       40,588.5   |
     * </pre>
     * <p>
     * Modified Julian Days are sometimes taken to imply Universal Time or UTC, but this
     * implementation always uses the Modified Julian Day for the local date,
     * regardless of the offset or time-zone.
     */
    // 719163L - 678576L = 40587L
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", DAYS, FOREVER, 40587L),
    /**
     * Rata Die field.
     * <p>
     * Rata Die counts whole days continuously starting day 1 at midnight at the beginning of 0001-01-01 (ISO).
     * The field always refers to the local date-time, ignoring the offset or zone.
     * <p>
     * For date-times, 'RATA_DIE.doGet()' assumes the same value from
     * midnight until just before the next midnight.
     * When 'RATA_DIE.doWith()' is applied to a date-time, the time of day portion remains unaltered.
     * 'MODIFIED_JULIAN_DAY.doWith()' and 'RATA_DIE.doGet()' only apply to {@code DateTime} objects
     * that can be converted into {@link ChronoField#EPOCH_DAY}.
     * A {@link DateTimeException} is thrown for any other type of object.
     */
    RATA_DIE("RataDie", DAYS, FOREVER, 719163L),
    // lots of others Truncated,Lilian, ANSI COBOL (also dotnet related), Excel?
    ;

    private final String name;
    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range;
    private final long offset;

    private JulianFields(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit, long offset) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.range = ValueRange.of(-365243219162L + offset, 365241780471L + offset);
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public TemporalUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public TemporalUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public ValueRange range() {
        return range;
    }

    @Override
    public int compare(TemporalAccessor dateTime1, TemporalAccessor dateTime2) {
        return Long.compare(dateTime1.getLong(this), dateTime2.getLong(this));
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a date from a value of this field.
     * <p>
     * This allows a date to be created from a value representing the amount in terms of this field.
     *
     * @param value  the value
     * @return the date, not null
     * @throws DateTimeException if the value exceeds the supported date range
     */
    public LocalDate createDate(long value) {
        return doWith(LocalDate.MIN_DATE, value);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean doIsSupported(TemporalAccessor dateTime) {
        return dateTime.isSupported(EPOCH_DAY);
    }

    @Override
    public ValueRange doRange(TemporalAccessor dateTime) {
        if (doIsSupported(dateTime) == false) {
            throw new DateTimeException("Unsupported field: " + this);
        }
        return range();
    }

    @Override
    public long doGet(TemporalAccessor dateTime) {
        return dateTime.getLong(EPOCH_DAY) + offset;
    }

    @Override
    public <R extends Temporal> R doWith(R dateTime, long newValue) {
        if (range().isValidValue(newValue) == false) {
            throw new DateTimeException("Invalid value: " + name + " " + newValue);
        }
        return (R) dateTime.with(EPOCH_DAY, Jdk8Methods.safeSubtract(newValue, offset));
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean resolve(DateTimeBuilder builder, long value) {
        boolean changed = false;
        changed = resolve0(JULIAN_DAY, builder, changed);
        changed = resolve0(MODIFIED_JULIAN_DAY, builder, changed);
        changed = resolve0(RATA_DIE, builder, changed);
        return changed;
    }

    private boolean resolve0(JulianFields field, DateTimeBuilder builder, boolean changed) {
        if (builder.containsFieldValue(field)) {
            builder.addCalendrical(LocalDate.ofEpochDay(Jdk8Methods.safeSubtract(builder.getFieldValue(JULIAN_DAY), JULIAN_DAY.offset)));
            builder.removeFieldValue(JULIAN_DAY);
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
