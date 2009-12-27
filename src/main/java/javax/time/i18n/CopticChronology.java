/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.period.PeriodUnits.DAYS;
import static javax.time.period.PeriodUnits.MONTHS;
import static javax.time.period.PeriodUnits.WEEKS;
import static javax.time.period.PeriodUnits.YEARS;

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.LocalDate;

/**
 * The Coptic calendar system.
 * <p>
 * CopticChronology defines the rules of the Coptic calendar system.
 * The Coptic calendar has twelve months of 30 days followed by an additional
 * period of 5 or 6 days, modelled as the thirteenth month in this implementation.
 * <p>
 * Years are measured in the 'Era of the Martyrs'.
 * 0001-01-01 (Coptic) equals 0284-08-29 (ISO).
 * The supported range is from Coptic year 1 to year 9999 (inclusive).
 * <p>
 * CopticChronology is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class CopticChronology extends Chronology implements Serializable {
    // TODO: PeriodUnit for years/months

    /**
     * The singleton instance of <code>CopticChronology</code>.
     */
    public static final CopticChronology INSTANCE = new CopticChronology();
//    /**
//     * The period unit for coptic years.
//     * One coptic year is 13 coptic months.
//     */
//    public static final PeriodUnit COPTIC_YEARS = new PeriodUnit();
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 24275872L;

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private CopticChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
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
    public static boolean isLeapYear(int year) {
        return ((year % 4) == 3);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    @Override
    public String getName() {
        return "Coptic";
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the equivalent rule for the specified field in the Coptic chronology.
//     * <p>
//     * This will take the input field and provide the closest matching field
//     * that is based......
//     *
//     * @param rule  the rule to convert, not null
//     * @return the rule in Coptic chronology, never null
//     */
////    @Override
//    public DateTimeFieldRule<?> convertRule(DateTimeFieldRule<?> rule) {
//        if (rule.getChronology().equals(this)) {
//            return rule;
//        }
//        rule = convertToISO(rule);
//        if (rule.equals(ISOChronology.yearRule())) {
//            return year();
//        }
//        if (rule.equals(ISOChronology.monthOfYearRule())) {
//            return monthOfYear();
//        }
//        if (rule.equals(ISOChronology.dayOfMonthRule())) {
//            return dayOfMonth();
//        }
//        if (rule.equals(ISOChronology.dayOfYearRule())) {
//            return dayOfYear();
//        }
//        if (rule.equals(ISOChronology.dayOfWeekRule())) {
//            return dayOfWeek();
//        }
//        return null;
//    }
//
//    /**
//     * Gets the equivalent rule for the specified field in the Coptic chronology.
//     * <p>
//     * This will take the input field and provide the closest matching field
//     * that is based......
//     *
//     * @param rule  the rule to convert, not null
//     * @return the rule in ISO chronology, never null
//     */
////    @Override
//    protected DateTimeFieldRule<?> convertToISO(DateTimeFieldRule<?> rule) {
//        if (rule.getChronology().equals(ISOChronology.INSTANCE)) {
//            return rule;
//        }
//        if (rule.equals(year())) {
//            return ISOChronology.yearRule();
//        }
//        if (rule.equals(monthOfYear())) {
//            return ISOChronology.monthOfYearRule();
//        }
//        if (rule.equals(dayOfMonth())) {
//            return ISOChronology.dayOfMonthRule();
//        }
//        if (rule.equals(dayOfYear())) {
//            return ISOChronology.dayOfYearRule();
//        }
//        if (rule.equals(dayOfWeek())) {
//            return ISOChronology.dayOfWeekRule();
//        }
//        return null;
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the Coptic chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule<Integer> yearRule() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Coptic chronology.
     *
     * @return the rule for the month of year field, never null
     */
    public static DateTimeFieldRule<Integer> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Coptic chronology.
     *
     * @return the rule for the day of month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Coptic chronology.
     *
     * @return the rule for the day of year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Coptic chronology.
     *
     * @return the rule for the day of week field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(Integer.class, CopticChronology.INSTANCE, "Year", YEARS, null, CopticDate.MIN_YEAR, CopticDate.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            CopticDate cd = calendrical.get(CopticDate.rule());
            return cd != null ? cd.getYear() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            Integer moyVal = merger.getValue(CopticChronology.monthOfYearRule());
            Integer domVal = merger.getValue(CopticChronology.dayOfMonthRule());
            if (moyVal != null && domVal != null) {
                int year = merger.getValue(this);
                CopticDate date;
                if (merger.getContext().isStrict()) {
                    date = CopticDate.copticDate(year, moyVal, domVal);
                } else {
                    date = CopticDate.copticDate(year, 1, 1)
                                .plusMonths(moyVal).plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMerged(LocalDate.rule(), date.toLocalDate());
                merger.removeProcessed(this);
                merger.removeProcessed(CopticChronology.monthOfYearRule());
                merger.removeProcessed(CopticChronology.dayOfMonthRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(Integer.class, CopticChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 13);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            CopticDate cd = calendrical.get(CopticDate.rule());
            return cd != null ? cd.getMonthOfYear() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(Integer.class, CopticChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 30);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 5;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer year = calendrical.get(CopticChronology.yearRule());
            Integer moy = calendrical.get(CopticChronology.monthOfYearRule());
            if (year != null && moy != null) {
                if (moy == 13) {
                    return isLeapYear(year) ? 6 : 5;
                } else {
                    return 30;
                }
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            CopticDate cd = calendrical.get(CopticDate.rule());
            return cd != null ? cd.getDayOfMonth() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(Integer.class, CopticChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 365;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer year = calendrical.get(CopticChronology.yearRule());
            if (year != null) {
                return isLeapYear(year) ? 366 : 365;
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            CopticDate cd = calendrical.get(CopticDate.rule());
            return cd != null ? cd.getDayOfYear() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            Integer yearVal = merger.getValue(CopticChronology.yearRule());
            if (yearVal != null) {
                int doy = merger.getValue(this);
                CopticDate date;
                if (merger.getContext().isStrict()) {
                    date = CopticDate.copticDate(yearVal, 1, 1).withDayOfYear(doy);
                } else {
                    date = CopticDate.copticDate(yearVal, 1, 1).plusDays(doy).plusDays(-1);
                }
                merger.storeMerged(LocalDate.rule(), date.toLocalDate());
                merger.removeProcessed(this);
                merger.removeProcessed(CopticChronology.yearRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(Integer.class, CopticChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            CopticDate cd = calendrical.get(CopticDate.rule());
            return cd != null ? cd.getDayOfWeek() : null;
        }
    }
}
