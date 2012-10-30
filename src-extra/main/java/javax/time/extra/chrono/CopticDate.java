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
package javax.time.extra.chrono;

import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_YEAR;

import java.io.Serializable;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.chrono.ChronoDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.calendrical.LocalPeriodUnit;

import javax.time.format.CalendricalFormatter;
import javax.time.chrono.ChronoDate;
import javax.time.chrono.Chronology;
import javax.time.chrono.Era;

/**
 * A date in the Coptic calendar system.
 * <p>
 * This implements {@code ChronoDate} for the {@link CopticChronology Coptic calendar}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class CopticDate implements ChronoDate<CopticChronology>,
        Comparable<ChronoDate<CopticChronology>>, Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The difference between the Coptic and Coptic epoch day count.
     */
    private static final int EPOCH_DAY_DIFFERENCE = 574971 + 40587;

    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month.
     */
    private final short month;
    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Creates a date in Coptic calendar system from the Era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the CopticEra, not null
     * @param year  the calendar system year-of-era
     * @param month  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public static CopticDate of(CopticEra era, int year, int month, int dayOfMonth) {
        return (CopticDate)CopticChronology.INSTANCE.date(era, year, month, dayOfMonth);
    }

    /**
     * Creates an instance.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the Coptic date, not null
     * @throws DateTimeException if the date is invalid
     */
    public static CopticDate ofEpochDay(long epochDay) {
        // TODO: validate
//        if (epochDay < MIN_EPOCH_DAY || epochDay > MAX_EPOCH_DAY) {
//            throw new CalendricalRuleException("Date exceeds supported range for CopticDate", Coptic.YEAR);
//        }
        epochDay += EPOCH_DAY_DIFFERENCE;
        int prolepticYear = (int) (((epochDay * 4) + 1463) / 1461);
        int startYearEpochDay = (prolepticYear - 1) * 365 + (prolepticYear / 4);
        int doy0 = (int) (epochDay - startYearEpochDay);
        int month = doy0 / 30 + 1;
        int dom = doy0 % 30 + 1;
        return new CopticDate(prolepticYear, month, dom);
    }

    private static CopticDate resolvePreviousValid(int prolepticYear, int month, int day) {
        if (month == 13 && day > 5) {
            day = CopticChronology.INSTANCE.isLeapYear(prolepticYear) ? 6 : 5;
        }
        return new CopticDate(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     * 
     * @param prolepticYear  the Coptic proleptic-year
     * @param month  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     * @throws DateTimeException if the date is invalid
     */
    CopticDate(int prolepticYear, int month, int dayOfMonth) {
        CopticChronology.MOY_RANGE.checkValidValue(month, MONTH_OF_YEAR);
        DateTimeValueRange range;
        if (month == 13) {
            range = CopticChronology.INSTANCE.isLeapYear(prolepticYear) ? CopticChronology.DOM_RANGE_LEAP : CopticChronology.DOM_RANGE_NONLEAP;
        } else {
            range = CopticChronology.DOM_RANGE;
        }
        range.checkValidValue(dayOfMonth, DAY_OF_MONTH);
        
        this.prolepticYear = prolepticYear;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        // TODO: validate
        return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public CopticChronology getChronology() {
        return CopticChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public int lengthOfMonth() {
        switch (month) {
            case 13:
                return (isLeapYear() ? 6 : 5);
            default:
                return 30;
        }
    }
    
    @Override
    public boolean isSupported(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return ((LocalDateTimeField) field).isDateField() && field != WEEK_OF_MONTH &&
                    field != WEEK_OF_YEAR && field != WEEK_OF_WEEK_BASED_YEAR && field != WEEK_BASED_YEAR;
        }
        return field != null && field.doIsSupported(this);
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            switch (f) {
                case DAY_OF_MONTH: return DateTimeValueRange.of(1, lengthOfMonth());
                case DAY_OF_YEAR: return DateTimeValueRange.of(1, lengthOfYear());
                case ALIGNED_WEEK_OF_MONTH: return DateTimeValueRange.of(1, getMonthValue() == 13 ? 1 : 5);
                case YEAR:
                case YEAR_OF_ERA: return (prolepticYear <= 0 ?
                        DateTimeValueRange.of(1, DateTimes.MAX_YEAR + 1) : DateTimeValueRange.of(1, DateTimes.MAX_YEAR));  // TODO
            }
            return getChronology().range(f);
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        return range(field).checkValidIntValue(getLong(field), field);  // use chrono-specific range
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case DAY_OF_WEEK: return DateTimes.floorMod(toEpochDay() + 3, 7) + 1;
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: return ((day - 1) % 7) + 1;
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: return ((getDayOfYear() - 1) % 7) + 1;
                case DAY_OF_MONTH: return day;
                case DAY_OF_YEAR: return (month - 1) * 30 + day;
                case EPOCH_DAY: return toEpochDay();
                case ALIGNED_WEEK_OF_MONTH: return ((day - 1) / 7) + 1;
                case ALIGNED_WEEK_OF_YEAR: return ((getDayOfYear() - 1) / 7) + 1;
                case MONTH_OF_YEAR: return month;
                case YEAR_OF_ERA: return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
                case YEAR: return prolepticYear;
                case ERA: return (prolepticYear >= 1 ? 1 : 0);
            }
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doGet(this);
    }

    @Override
    public CopticDate with(WithAdjuster adjuster) {
        return (CopticDate) adjuster.doWithAdjustment(this);
    }
    
    @Override
    public CopticDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);        // TODO: validate value
            int nvalue = (int) newValue;
            switch (f) {
                case DAY_OF_WEEK: return plusDays(newValue - getDayOfWeek().getValue());
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: return plusDays(newValue - getLong(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: return plusDays(newValue - getLong(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case DAY_OF_MONTH: return resolvePreviousValid(prolepticYear, month, nvalue);
                case DAY_OF_YEAR: return resolvePreviousValid(prolepticYear, ((nvalue - 1) / 30) + 1, ((nvalue - 1) % 30) + 1);
                case EPOCH_DAY: return ofEpochDay(nvalue);
                case ALIGNED_WEEK_OF_MONTH: return plusDays((newValue - getLong(ALIGNED_WEEK_OF_MONTH)) * 7);
                case ALIGNED_WEEK_OF_YEAR: return plusDays((newValue - getLong(ALIGNED_WEEK_OF_YEAR)) * 7);
                case MONTH_OF_YEAR: return resolvePreviousValid(prolepticYear, nvalue, day);
                case YEAR_OF_ERA: return resolvePreviousValid(prolepticYear >= 1 ? nvalue : 1 - nvalue, month, day);
                case YEAR: return resolvePreviousValid(nvalue, month, day);
                case ERA: return resolvePreviousValid(1 - prolepticYear, month, day);
            }
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isLeapYear() {
        return getChronology().isLeapYear(get(LocalDateTimeField.YEAR));
    }

    @Override
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    @Override
    public CopticDate plus(PlusAdjuster adjuster) {
        return (CopticDate) adjuster.doPlusAdjustment(this);
    }

    @Override
    public CopticDate plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            switch (f) {
                case DAYS: return plusDays(amountToAdd);
                case WEEKS: return plusDays(DateTimes.safeMultiply(amountToAdd, 7));
                case MONTHS: return plusMonths(amountToAdd);
                case QUARTER_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 6);  // no overflow (256 is multiple of 2)
                case YEARS: return plusYears(amountToAdd);
                case DECADES: return plusYears(DateTimes.safeMultiply(amountToAdd, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(amountToAdd, 100));
                case MILLENNIA: return plusYears(DateTimes.safeMultiply(amountToAdd, 1000));
//                case ERAS: throw new DateTimeException("Unable to add era, standard calendar system only has one era");
//                case FOREVER: return (period == 0 ? this : (period > 0 ? LocalDate.MAX_DATE : LocalDate.MIN_DATE));
            }
            throw new DateTimeException(unit.getName() + " not valid for CopticDate");
        }
        return unit.doAdd(this, amountToAdd);
    }

    //-----------------------------------------------------------------------
    public CopticDate plusYears(long years) {
        return plusMonths(DateTimes.safeMultiply(years, 13));
    }

    public CopticDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long curEm = prolepticYear * 13L + (month - 1);
        long calcEm = DateTimes.safeAdd(curEm, months);
        int newYear = DateTimes.safeToInt(DateTimes.floorDiv(calcEm, 13));
        int newMonth = DateTimes.floorMod(calcEm, 13) + 1;
        return resolvePreviousValid(newYear, newMonth, day);
    }

    public CopticDate plusWeeks(long weeksToAdd) {
        return plusDays(DateTimes.safeMultiply(weeksToAdd, 7));
    }

    public CopticDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return CopticDate.ofEpochDay(DateTimes.safeAdd(toEpochDay(), days));
    }

    @Override
    public CopticDate minus(MinusAdjuster adjuster) {
        return (CopticDate) adjuster.doMinusAdjustment(this);
    }

    @Override
    public CopticDate minus(long amountToSubtract, PeriodUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    public Era<CopticChronology> getEra() {
        return getChronology().eraOf(DateTimes.safeToInt(get(LocalDateTimeField.ERA)));
    }

    public int getYear() {
        return DateTimes.safeToInt(get(LocalDateTimeField.YEAR_OF_ERA));
    }

    public int getMonthValue() {
        return DateTimes.safeToInt(get(LocalDateTimeField.MONTH_OF_YEAR));
    }

    public int getDayOfMonth() {
        return DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_MONTH));
    }

    public int getDayOfYear() {
        return DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_YEAR));
    }

    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.of(DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_WEEK)));
    }

    public CopticDate withEra(Era<CopticChronology> era) {
        return with(LocalDateTimeField.ERA, era.getValue());
    }

    public CopticDate withYear(int year) {
        return with(LocalDateTimeField.YEAR_OF_ERA, year);
    }

    public CopticDate withMonth(int month) {
        return with(LocalDateTimeField.MONTH_OF_YEAR, month);
    }

    public CopticDate withDayOfMonth(int dayOfMonth) {
        return with(LocalDateTimeField.DAY_OF_MONTH, month);
    }

    public CopticDate withDayOfYear(int dayOfYear) {
        return with(LocalDateTimeField.DAY_OF_YEAR, month);
    }

    public CopticDate minusYears(long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
    }

    public CopticDate minusMonths(long monthsToSubtract) {
        return (monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract));
    }

    public CopticDate minusWeeks(long weeksToSubtract) {
        return  (weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeksToSubtract));
    }

    public CopticDate minusDays(long daysToSubtract) {
        return (daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract));
    }

    @Override
    public final ChronoDateTime<CopticChronology> atTime(LocalTime localTime) {
        return Chronology.dateTime(this, localTime);
    }

    public <R> R extract(Class<R> type) {
        if (type == ChronoDate.class) {
            return (R) this;
        } else if (type == Chronology.class) {
            return (R) getChronology();
        }
        return null;
    }

    @Override
    public DateTime doWithAdjustment(DateTime calendrical) {
        return calendrical.with(EPOCH_DAY, this.getLong(LocalDateTimeField.EPOCH_DAY));
    }

    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoDate == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        ChronoDate<?> end = (ChronoDate) endDateTime;
        if (getChronology().equals(end.getChronology()) == false) {
            throw new DateTimeException("Unable to calculate period between two different chronologies");
        }
        if (unit instanceof LocalPeriodUnit) {
            return LocalDate.from(this).periodUntil(end, unit);  // TODO: this is wrong
        }
        return unit.between(this, endDateTime).getAmount();
    }
    
    @Override
    public int compareTo(ChronoDate<CopticChronology> other) {
        CopticDate cd = (CopticDate)other;
        if (getChronology().equals(other.getChronology()) == false) {
            throw new ClassCastException("Cannot compare ChronoDate in two different calendar systems, " +
            		"use the EPOCH_DAY field as a Comparator instead");
        }
        int cmp = Integer.compare(getEra().getValue(), cd.getEra().getValue());
        if (cmp == 0) {
            cmp = Integer.compare(getYear(), cd.getYear());
            if (cmp == 0) {
                cmp = Integer.compare(getMonthValue(), cd.getMonthValue());
                if (cmp == 0) {
                    cmp = Integer.compare(getDayOfMonth(), cd.getDayOfMonth());
                }
            }
        }
        return cmp;
    }

    @Override
    public boolean isAfter(ChronoDate<CopticChronology> other) {
        return this.getLong(LocalDateTimeField.EPOCH_DAY) > other.getLong(LocalDateTimeField.EPOCH_DAY);
    }

    @Override
    public boolean isBefore(ChronoDate<CopticChronology> other) {
        return this.getLong(LocalDateTimeField.EPOCH_DAY) < other.getLong(LocalDateTimeField.EPOCH_DAY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CopticDate) {
            CopticDate other = (CopticDate) obj;
            return getChronology().equals(other.getChronology()) &&
                    getEra() == other.getEra() &&
                    getYear() == other.getYear() &&
                    getMonthValue() == other.getMonthValue() &&
                    getDayOfMonth() == other.getDayOfMonth();
        }
        return false;
    }
    
    //-----------------------------------------------------------------------
    private long toEpochDay() {
        long year = (long) prolepticYear;
        long copticEpochDay = ((year - 1) * 365) + DateTimes.floorDiv(year, 4) + (getDayOfYear() - 1);
        return copticEpochDay - EPOCH_DAY_DIFFERENCE;
    }

    @Override
    public String toString() {
        int yearValue = getYear();
        int monthValue = getMonthValue();
        int dayValue = getDayOfMonth();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(12);
        if (absYear < 1000) {
            buf.append(yearValue + 10000).deleteCharAt(0);
        } else {
            buf.append(yearValue);
        }
        return buf.append(getEra())
            .append(monthValue < 10 ? "-0" : "-").append(monthValue)
            .append(dayValue < 10 ? "-0" : "-").append(dayValue)
            .append(" (").append(getChronology().getId()).append(')')
            .toString();
    }

    @Override
    public String toString(CalendricalFormatter formatter) {
        Objects.requireNonNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
