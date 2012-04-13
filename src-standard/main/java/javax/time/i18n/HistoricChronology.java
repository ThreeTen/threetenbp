/*
 * Copyright (c) 2010-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.DateTimes;
import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.MonthOfYear;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.Chronology;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.DateTimeRuleRange;
import javax.time.calendrical.ISOPeriodUnit;
import javax.time.calendrical.InvalidCalendarFieldException;
import javax.time.calendrical.PeriodUnit;
import javax.time.extended.Year;

/**
 * The Historic calendar system.
 * <p>
 * HistoricChronology defines the rules of the Historic calendar system.
 * The Historic calendar has twelve months of 30 days followed by an additional
 * period of 5 or 6 days, modelled as the thirteenth month in this implementation.
 * <p>
 * Years are measured in the 'Era of the Martyrs'.
 * 0001-01-01 (Historic) equals 0284-08-29 (ISO).
 * The supported range is from Historic year 1 to year 9999 (inclusive).
 * <p>
 * HistoricChronology is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class HistoricChronology extends Chronology implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The start of months in a standard year.
     */
    private static final int[] STANDARD_MONTH_START = new int[] {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    /**
     * The start of months in a leap year.
     */
    private static final int[] LEAP_MONTH_START = new int[] {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    /**
     * The cutover from Julian to Gregorian.
     */
    private final LocalDate cutover;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HistoricChronology</code> specifying the
     * cutover date when the Gregorian/ISO calendar system was first used.
     *
     * @param cutover  the cutover date, not null
     * @return a {@code HistoricChronology}, not null
     */
    public static HistoricChronology cutoverAt(final LocalDate cutover) {
        DateTimes.checkNotNull(cutover, "Cutover date must not be null");
        return new HistoricChronology(cutover);
    }

    /**
     * Obtains an instance of <code>HistoricChronology</code> using the standard
     * cutover date of 1582-10-15.
     *
     * @return a {@code HistoricChronology}, not null
     */
    public static HistoricChronology standardCutover() {
        return new HistoricChronology(HistoricDate.STANDARD_CUTOVER);
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private HistoricChronology(final LocalDate cutover) {
        this.cutover = cutover;
    }

    //-------------------------------------------------------------------------
    /**
     * Calculates the day-of-year from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-year
     */
    int getDayOfYear(HistoricDate date) {
        int moy0 = date.getMonthOfYear().ordinal();
        int dom = date.getDayOfMonth();
        if (isLeapYear(date.getYear())) {
            return LEAP_MONTH_START[moy0] + dom;
        } else {
            return STANDARD_MONTH_START[moy0] + dom;
        }
    }

    /**
     * Calculates the date from a year and day-of-year.
     *
     * @param year  the year, valid
     * @param dayOfYear  the day-of-year, valid
     * @return the date, not null
     */
    HistoricDate getDateFromDayOfYear(int year, int dayOfYear) {
        boolean leap = isLeapYear(year);
        if (dayOfYear == 366 && leap == false) {
            throw new InvalidCalendarFieldException("DayOfYear 366 is invalid for year " + year, dayOfYearRule());
        }
        int doy0 = dayOfYear - 1;
        int[] array = (leap ? LEAP_MONTH_START : STANDARD_MONTH_START);
        int month = 1;
        for ( ; month < 12; month++) {
            if (doy0 < array[month]) {
                break;
            }
        }
        MonthOfYear moy = MonthOfYear.of(month);
        int dom = dayOfYear - array[month - 1];
        return new HistoricDate(this, year, moy, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A year is leap if the remainder after division by four equals three.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param year  the year to check, not validated for range
     * @return true if the year is a leap year
     */
    public boolean isLeapYear(int year) {
        if (year < cutover.getYear()) {
            return JulianChronology.isLeapYear(year);
        } else if (year > cutover.getYear()) {
            return Year.isLeap(year);
        } else {
            if (cutover.getMonthOfYear().compareTo(MonthOfYear.FEBRUARY) < 0) {
                return false;  // TODO
            }
            return false;  // TODO
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the cutover date of the chronology.
     *
     * @return the cutover date of the chronology, not null
     */
    public LocalDate getCutover() {
        return cutover;
    }

    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, not null
     */
    @Override
    public String getName() {
        return "Historic " + cutover;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the Historic chronology.
     *
     * @return the rule for the year field, not null
     */
    public DateTimeRule eraRule() {
        return new EraRule(this);
    }

    /**
     * Gets the rule for the year field in the Historic chronology.
     *
     * @return the rule for the year field, not null
     */
    public DateTimeRule yearOfEraRule() {
        return new YearRule(this);
    }

    /**
     * Gets the rule for the year field in the Historic chronology.
     *
     * @return the rule for the year field, not null
     */
    public DateTimeRule yearRule() {
        return new YearRule(this);
    }

    /**
     * Gets the rule for the month-of-year field in the Historic chronology.
     *
     * @return the rule for the month-of-year field, not null
     */
    public DateTimeRule monthOfYearRule() {
        return new MonthOfYearRule(this);
    }

    /**
     * Gets the rule for the day-of-month field in the Historic chronology.
     *
     * @return the rule for the day-of-month field, not null
     */
    public DateTimeRule dayOfMonthRule() {
        return new DayOfMonthRule(this);
    }

    /**
     * Gets the rule for the day-of-year field in the Historic chronology.
     *
     * @return the rule for the day-of-year field, not null
     */
    public DateTimeRule dayOfYearRule() {
        return new DayOfYearRule(this);
    }

    /**
     * Gets the rule for the day-of-week field in the Historic chronology.
     *
     * @return the rule for the day-of-week field, not null
     */
    public DateTimeRule dayOfWeekRule() {
        return new DayOfWeekRule(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit for eras.
     * <p>
     * The period unit defines the concept of a period of an era.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 2,000,000,000 years.
     * This is equivalent to the ISO era period unit.
     * <p>
     * See {@link #eraRule()} for the main date-time field.
     *
     * @return the period unit for years, not null
     */
    public static PeriodUnit periodEras() {
        return ISOPeriodUnit.ERAS;
    }

    /**
     * Gets the period unit for years.
     * <p>
     * The period unit defines the concept of a period of a year.
     * This has an estimated duration equal to 365.25 days.
     * <p>
     * See {@link #yearRule()} for the main date-time field.
     *
     * @return the period unit for years, not null
     */
    public static PeriodUnit periodYears() {
        return YEARS;
    }

    /**
     * Gets the period unit for months.
     * <p>
     * The period unit defines the concept of a period of a month.
     * Historic months are typically 30 days long, except for the 13th month which is
     * 5 or 6 days long. The rule uses an estimated duration of 29.5 days.
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period unit for months, not null
     */
    public static PeriodUnit periodMonths() {
        return MONTHS;
    }

    /**
     * Gets the period unit for weeks.
     * <p>
     * The period unit defines the concept of a period of a week.
     * This is equivalent to the ISO weeks period unit.
     *
     * @return the period unit for weeks, not null
     */
    public static PeriodUnit periodWeeks() {
        return ISOPeriodUnit.WEEKS;
    }

    /**
     * Gets the period unit for days.
     * <p>
     * The period unit defines the concept of a period of a day.
     * This is equivalent to the ISO days period unit.
     * <p>
     * See {@link #dayOfMonthRule()} for the main date-time field.
     *
     * @return the period unit for days, not null
     */
    public static PeriodUnit periodDays() {
        return ISOPeriodUnit.DAYS;
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the fields.
     * 
     * @param engine  the merge context
     */
    void merge(CalendricalEngine engine) {
        // TODO: era
        DateTimeField year = engine.getFieldDerived(yearRule(), false);
        if (year != null) {
            // year-month-day
            DateTimeField moy = engine.getFieldDerived(monthOfYearRule(), false);
            DateTimeField dom = engine.getFieldDerived(dayOfMonthRule(), false);
            if (moy != null && dom != null) {
                HistoricDate date = HistoricDate.of(year.getValidIntValue(), MonthOfYear.of(moy.getValidIntValue()), dom.getValidIntValue());
                engine.setDate(date.toLocalDate(), true);
//                engine.removeProcessed(yearRule());
//                engine.removeProcessed(monthOfYearRule());
//                engine.removeProcessed(dayOfMonthRule());
            }
            // year-day
            DateTimeField doy = engine.getFieldDerived(dayOfYearRule(), false);
            if (doy != null) {
                HistoricDate date = HistoricDate.of(year.getValidIntValue(), MonthOfYear.JANUARY, 1).withDayOfYear(doy.getValidIntValue());
                engine.setDate(date.toLocalDate(), true);
//                engine.removeProcessed(yearRule());
//                engine.removeProcessed(dayOfYearRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeRule implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule(HistoricChronology chrono) {
            super("HistoricEra", periodEras(), null, 0, 1, null);
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            HistoricDate date = engine.derive(HistoricDate.rule());
            return date != null ? field(date.getEra().getValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeRule implements Serializable {
        /** The chronology. */
        private final HistoricChronology chrono;
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule(HistoricChronology chrono) {
            super("HistoricYear", YEARS, null, -(HistoricDate.MAX_YEAR - 1), HistoricDate.MAX_YEAR, null);
            this.chrono = chrono;
        }
        @Override
        protected void normalize(CalendricalEngine engine) {
            chrono.merge(engine);
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            HistoricDate date = engine.derive(HistoricDate.rule());
            return date != null ? field(date.getYear()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeRule implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule(HistoricChronology chrono) {
            super("HistoricMonthOfYear", MONTHS, YEARS, 1, 12, null);
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            HistoricDate date = engine.derive(HistoricDate.rule());
            return date != null ? field(date.getMonthOfYear().getValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeRule implements Serializable {
        /** The chronology. */
        private final HistoricChronology chrono;
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule(HistoricChronology chrono) {
            super("HistoricDayOfMonth", periodDays(), MONTHS, DateTimeRuleRange.of(1, 28, 31), null);
            this.chrono = chrono;
        }
        @Override
        public DateTimeRuleRange getValueRange(Calendrical calendrical) {
            DateTimeField moy = calendrical.get(chrono.monthOfYearRule());
            if (moy != null) {
                MonthOfYear month = MonthOfYear.of(moy.getValidIntValue());
                if (month == MonthOfYear.FEBRUARY) {
                    DateTimeField year = calendrical.get(chrono.yearRule());
                    if (year != null) {
                        return DateTimeRuleRange.of(1, month.lengthInDays(Year.isLeap(year.getValue())));
                    }
                    return DateTimeRuleRange.of(1, 28, 29);
                } else {
                    return DateTimeRuleRange.of(1, month.maxLengthInDays());
                }
            }
            return getValueRange();
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            HistoricDate date = engine.derive(HistoricDate.rule());
            return date != null ? field(date.getDayOfMonth()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeRule implements Serializable {
        /** The chronology. */
        private final HistoricChronology chrono;
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule(HistoricChronology chrono) {
            super("HistoricDayOfYear", periodDays(), YEARS, DateTimeRuleRange.of(1, 365, 366), null);
            this.chrono = chrono;
        }
        @Override
        public DateTimeRuleRange getValueRange(Calendrical calendrical) {
            DateTimeField year = calendrical.get(chrono.yearRule());
            if (year != null) {
                return DateTimeRuleRange.of(1, chrono.isLeapYear(year.getValidIntValue()) ? 366 : 365);
            }
            return getValueRange();
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            HistoricDate date = engine.derive(HistoricDate.rule());
            return date != null ? field(date.getDayOfYear()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeRule implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule(HistoricChronology chrono) {
            super("HistoricDayOfWeek", periodDays(), periodWeeks(), 1, 7, null);
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            HistoricDate date = engine.derive(HistoricDate.rule());
            return date != null ? field(date.getDayOfWeek().getValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Period unit for years.
     */
    private static final PeriodUnit YEARS = new Years();
    /**
     * Unit class for years.
     */
    private static final class Years extends PeriodUnit {
        private static final long serialVersionUID = 1L;
        private Years() {
            super("JulianYears", Duration.ofSeconds(31557600L));  // 365.25 days
        }
        private Object readResolve() {
            return YEARS;
        }
    }
    /**
     * Period unit for months.
     */
    private static final PeriodUnit MONTHS = new Months();
    /**
     * Unit class for months.
     */
    private static final class Months extends PeriodUnit {
        private static final long serialVersionUID = 1L;
        private Months() {
            super("JulianMonths", Duration.ofHours(31557600L / 12L));  // 365.25 days / 12
        }
        private Object readResolve() {
            return MONTHS;
        }
    }

}
