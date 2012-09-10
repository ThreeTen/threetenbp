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

import javax.time.DateTimeException;
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
     * JULIAN_DAY.
     * <p>
     * This is an integer-based version of the Julian Day Number.
     * Julian Day is a well-known system that represents the count of whole days since day 0,
     * which is defined to be January 1, 4713 BCE in the Julian calendar, and -4713-11-24 Gregorian.
     * The field  has "JulianDay" as 'name', and 'DAYS' as 'baseUnit'.
     * <p>
     * For classes whose values represent two dates or datetimes (such
     * as OffsetDateTime and ZonedDateTime), the JULIAN_DAY refers to
     * the local datetime.
     * <p>
     * For datetimes, 'JULIAN_DAY.doGet()' assumes the same value from
     * midnight until just before the next midnight.
     * When 'JULIAN_DAY.doSet()' is applied to a datetime, the time of day portion remains unaltered.
     * 'JULIAN_DAY.doSet()' and 'JULIAN_DAY.doGet()' only apply to DateTime objects that can be 
     * converted into {@link LocalDateTimeField#EPOCH_DAY}.  A {@link DateTimeException} is thrown
     * for any other type of object.
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
    JULIAN_DAY("JulianDay", DAYS, FOREVER, DateTimeValueRange.of(-365243219162L + 2440588L, 365241780471L + 2440588L)),
    /**
     * MODIFIED_JULIAN_DAY
     * <p>
     * This is an integer-based version of the Modified Julian Day Number.
     * Modified Julian Day (MJD) is a well-known system that counts days continuously.
     * It is defined relative to astronomical Julian Day as  {@code MJD = JD - 2400000.5}.
     * Each Modified Julian Day runs from midnight to midnight.
     * <p>
     * For classes whose values represent two dates or datetimes (such
     * as OffsetDateTime and ZonedDateTime), the MODIFIED_JULIAN_DAY refers to
     * the local datetime.
     * <p>
     * For datetimes, 'MODIFIED_JULIAN_DAY.doGet()' assumes the same value from
     * midnight until just before the next midnight.
     * When 'MODIFIED_JULIAN_DAY.doSet()' is applied to a datetime, the time of day portion remains unaltered.
     * 'MODIFIED_JULIAN_DAY.doSet()' and 'MODIFIED_JULIAN_DAY.doGet()' only apply to DateTime objects that can be 
     * converted into {@link LocalDateTimeField#EPOCH_DAY}.  A {@link DateTimeException} is thrown
     * for any other type of object.
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
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", DAYS, FOREVER, DateTimeValueRange.of(-365243219162L + 40587L, 365241780471L + 40587L)),
    /**
     * RATA_DIE.
     * <p>
     * Rata Die counts whole days continuously starting day 1 at midnight at the beginning of 0001-01-01 (ISO).
     * <p>
     * For classes whose values represent two dates or datetimes (such
     * as OffsetDateTime and ZonedDateTime), the RATA_DIE refers to
     * the local datetime.
     * <p>
     * For datetimes, 'RATA_DIE.doGet()' assumes the same value from
     * midnight until just before the next midnight.
     * When 'RATA_DIE.doSet()' is applied to a datetime, the time of day portion remains unaltered.
     * 'MODIFIED_JULIAN_DAY.doSet()' and 'RATA_DIE.doGet()' only apply to DateTime objects that can be 
     * converted into {@link LocalDateTimeField#EPOCH_DAY}.  A {@link DateTimeException} is thrown
     * for any other type of object.
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
     * @throws DateTimeException if the value exceeds the supported date range
     */
    public LocalDate createDate(long value) {
        return doSet(LocalDate.MIN_DATE, value);
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange doRange(DateTime dateTime) {
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
            throw new DateTimeException("Invalid value: " + name + " " + newValue);
        }
        switch (this) {
            case JULIAN_DAY: return (R) calendrical.with(EPOCH_DAY, DateTimes.safeSubtract(newValue, ED_JDN));
            case MODIFIED_JULIAN_DAY: return (R) calendrical.with(EPOCH_DAY, DateTimes.safeSubtract(newValue, ED_MJD));
            case RATA_DIE: return (R) calendrical.with(EPOCH_DAY, DateTimes.safeSubtract(newValue, ED_RD));
            default:
                throw new IllegalStateException("Unreachable");
        }
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
