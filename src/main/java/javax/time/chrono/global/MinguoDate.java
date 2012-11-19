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
package javax.time.chrono.global;

import static javax.time.calendrical.ChronoField.YEAR;
import static javax.time.chrono.global.MinguoChrono.YEARS_DIFFERENCE;

import java.io.Serializable;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.chrono.Era;
import javax.time.jdk8.Jdk8Methods;

/**
 * A date in the Minguo calendar system.
 * <p>
 * This implements {@code ChronoLocalDate} for the {@link MinguoChrono Minguo calendar}.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class MinguoDate
        extends ChronoDateImpl<MinguoChrono>
        implements Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1300372329181994526L;

    /**
     * The underlying date.
     */
    private final LocalDate isoDate;

    //-----------------------------------------------------------------------
    /**
     * Creates a date in Minguo calendar system from the Era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the Era, not null
     * @param year  the calendar system year-of-era
     * @param month  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public static MinguoDate of(Era<MinguoChrono> era, int year, int month, int dayOfMonth) {
        return (MinguoDate)MinguoChrono.INSTANCE.date(era, year, month, dayOfMonth);
    }

    /**
     * Creates an instance.
     *
     * @param date  the time-line date, not null
     */
    MinguoDate(LocalDate date) {
        Objects.requireNonNull(date, "date");
        this.isoDate = date;
    }

    //-----------------------------------------------------------------------
    @Override
    public MinguoChrono getChrono() {
        return MinguoChrono.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public int lengthOfMonth() {
        return isoDate.lengthOfMonth();
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;
                switch (f) {
                    case DAY_OF_MONTH:
                    case DAY_OF_YEAR:
                    case ALIGNED_WEEK_OF_MONTH:
                        return isoDate.range(field);
                    case YEAR_OF_ERA: {
                        DateTimeValueRange range = YEAR.range();
                        long max = (getProlepticYear() <= 0 ? -range.getMinimum() + 1 + YEARS_DIFFERENCE : range.getMaximum() - YEARS_DIFFERENCE);
                        return DateTimeValueRange.of(1, max);
                    }
                }
                return getChrono().range(f);
            }
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doRange(this);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case YEAR_OF_ERA: {
                    int prolepticYear = getProlepticYear();
                    return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
                }
                case YEAR: return getProlepticYear();
                case ERA: return (isoDate.getYear() - YEARS_DIFFERENCE >= 1 ? 1 : 0);
            }
            return isoDate.getLong(field);
        }
        return field.doGet(this);
    }

    @Override
    public MinguoDate with(DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            switch (f) {
                case YEAR_OF_ERA:
                case YEAR:
                case ERA: {
                    f.checkValidValue(newValue);
                    int nvalue = (int) newValue;
                    switch (f) {
                        case YEAR_OF_ERA: return with(isoDate.withYear(
                                getProlepticYear() >= 1 ? nvalue + YEARS_DIFFERENCE : (1 - nvalue)  + YEARS_DIFFERENCE));
                        case YEAR: return with(isoDate.withYear(nvalue + YEARS_DIFFERENCE));
                        case ERA: return with(isoDate.withYear((1 - getProlepticYear()) + YEARS_DIFFERENCE));
                    }
                }
            }
            return with(isoDate.with(field, newValue));
        }
        return field.doSet(this, newValue);
    }

    private int getProlepticYear() {
        return isoDate.getYear() - YEARS_DIFFERENCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public MinguoDate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public MinguoDate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public MinguoDate plusWeeks(long weeksToAdd) {
        return plusDays(Jdk8Methods.safeMultiply(weeksToAdd, 7));
    }

    @Override
    public MinguoDate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private MinguoDate with(LocalDate newDate) {
        return (newDate.equals(isoDate) ? this : new MinguoDate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public Era<MinguoChrono> getEra() {
        return super.getEra();
    }

    @Override
    public int getYear() {
        return super.getYear();
    }

    @Override
    public int getMonthValue() {
        return super.getMonthValue();
    }

    @Override
    public int getDayOfMonth() {
        return super.getDayOfMonth();
    }

    @Override
    public int getDayOfYear() {
        return super.getDayOfYear();
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return super.getDayOfWeek();
    }

    @Override
    public boolean isLeapYear() {
        return super.isLeapYear();
    }

    @Override
    public int lengthOfYear() {
        return super.lengthOfYear();
    }

    @Override
    public MinguoDate withEra(Era<MinguoChrono> era) {
        return (MinguoDate)super.withEra(era);
    }

    @Override
    public MinguoDate withYear(int year) {
        return (MinguoDate)super.withYear(year);
    }

    @Override
    public MinguoDate withMonth(int month) {
        return (MinguoDate)super.withMonth(month);
    }

    @Override
    public MinguoDate withDayOfMonth(int dayOfMonth) {
        return (MinguoDate)super.withDayOfMonth(dayOfMonth);
    }

    @Override
    public MinguoDate withDayOfYear(int dayOfYear) {
        return (MinguoDate)super.withDayOfYear(dayOfYear);
    }

    @Override
    public MinguoDate minusYears(long yearsToSubtract) {
        return (MinguoDate)super.minusYears(yearsToSubtract);
    }

    @Override
    public MinguoDate minusMonths(long monthsToSubtract) {
        return (MinguoDate)super.minusMonths(monthsToSubtract);
    }

    @Override
    public MinguoDate minusWeeks(long weeksToSubtract) {
        return (MinguoDate)super.minusWeeks(weeksToSubtract);
    }

    @Override
    public MinguoDate minusDays(long daysToSubtract) {
        return (MinguoDate)super.minusDays(daysToSubtract);
    }

    @Override
    public long toEpochDay() {
        return isoDate.toEpochDay();
    }

}
