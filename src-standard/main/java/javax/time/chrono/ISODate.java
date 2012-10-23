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
import java.util.Objects;
import javax.time.DateTimes;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;

/**
 * A date in the ISO calendar system.
 * <p>
 * This implements {@code ChronoDate} for the {@link ISOChronology ISO calendar}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class ISODate extends ChronoDate<ISOChronology> implements Comparable<ChronoDate<ISOChronology>>, Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ISO date.
     */
    private final LocalDate isoDate;

    /**
     * Creates an instance.
     * 
     * @param date  the time-line date, not null
     */
    ISODate(LocalDate date) {
        Objects.requireNonNull(date, "LocalDate");
        this.isoDate = date;
    }

    //-----------------------------------------------------------------------
    @Override
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public int lengthOfMonth() {
        return isoDate.lengthOfMonth();
    }

    @Override
    public boolean isSupported(DateTimeField field) {
        return isoDate.isSupported(field);
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        return isoDate.range(field);
    }

    @Override
    public long getLong(DateTimeField field) {
        return isoDate.getLong(field);
    }

    @Override
    public ISODate with(DateTimeField field, long newValue) {
        return with(isoDate.with(field, newValue));
    }

    //-----------------------------------------------------------------------
    @Override
    public ISODate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public ISODate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public ISODate plusWeeks(long weeksToAdd) {
        return plusDays(DateTimes.safeMultiply(weeksToAdd, 7));
    }

    @Override
    public ISODate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private ISODate with(LocalDate newDate) {
        return (newDate == isoDate ? this : new ISODate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate toLocalDate() {
        return isoDate;
    }

    @Override
    public Era<ISOChronology> getEra() {
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
    public ISODate withEra(Era era) {
        return (ISODate)super.withEra(era);
    }

    @Override
    public ISODate withYear(int year) {
        return (ISODate)super.withYear(year);
    }

    @Override
    public ISODate withMonth(int month) {
        return (ISODate)super.withMonth(month);
    }

    @Override
    public ISODate withDayOfMonth(int dayOfMonth) {
        return (ISODate)super.withDayOfMonth(dayOfMonth);
    }

    @Override
    public ISODate withDayOfYear(int dayOfYear) {
        return (ISODate)super.withDayOfYear(dayOfYear);
    }

    @Override
    public ISODate minusYears(long yearsToSubtract) {
        return (ISODate)super.minusYears(yearsToSubtract);
    }

    @Override
    public ISODate minusMonths(long monthsToSubtract) {
        return (ISODate)super.minusMonths(monthsToSubtract);
    }

    @Override
    public ISODate minusWeeks(long weeksToSubtract) {
        return (ISODate)super.minusWeeks(weeksToSubtract);
    }

    @Override
    public ISODate minusDays(long daysToSubtract) {
        return (ISODate)super.minusDays(daysToSubtract);
    }
    
    @Override
    public boolean isAfter(ChronoDate other) {
        return super.isAfter(other);
    }

    @Override
    public boolean isBefore(ChronoDate other) {
        return super.isBefore(other);
    }

    @Override
    public boolean equalDate(ChronoDate other) {
        return super.equalDate(other);
    }

}
