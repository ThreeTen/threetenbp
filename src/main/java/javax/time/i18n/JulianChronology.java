/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.Duration;
import javax.time.calendar.Chronology;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.PeriodUnit;

/**
 * The Julian calendar system.
 * <p>
 * JulianChronology defines the rules of the Julian calendar system.
 * The Julian calendar was introduced by Julius Caesar in 46 BCE to replace the
 * previous Roman calendar system.
 * <p>
 * The calendar system is the same as the {@code ISOChronology ISO-8601} calendar
 * system with the exception of the rule for the leap year. The Julian definition
 * has a leap year every four years without fail.
 * <p>
 * JulianChronology is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class JulianChronology extends Chronology implements Serializable {

    /**
     * The singleton instance of {@code JulianChronology}.
     */
    public static final JulianChronology INSTANCE = new JulianChronology();
    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private JulianChronology() {
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
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * The Julian calendar system defines a leap year as being divisible by four
     * without remainder. The calculation is proleptic - applying the same rules
     * into the far future and far past.
     *
     * @param year  the year to check, not validated for range
     * @return true if the year is a leap year
     */
    public static boolean isLeapYear(int year) {
        return ((year & 3) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    @Override
    public String getName() {
        return "Julian";
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the rule for the year field in the Julian chronology.
//     *
//     * @return the rule for the year field, never null
//     */
//    public static DateTimeFieldRule<Integer> yearRule() {
//        return YearRule.INSTANCE;
//    }
//
//    /**
//     * Gets the rule for the month-of-year field in the Julian chronology.
//    * <p>
//    * This field counts months sequentially from the start of the year.
//    * The values follow the traditional numbering scheme defined from
//    * January as value 1 to December as value 12.
//    * <p>
//    * The enum {@link MonthOfYear} should be used wherever possible in
//    * applications when referring to the day of the week to avoid
//    * hard-coding the values.
//     *
//     * @return the rule for the month-of-year field, never null
//     */
//    public static DateTimeFieldRule<MonthOfYear> monthOfYearRule() {
//        return MonthOfYearRule.INSTANCE;
//    }
//
//    /**
//     * Gets the rule for the day-of-month field in the Julian chronology.
//     *
//     * @return the rule for the day-of-month field, never null
//     */
//    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
//        return DayOfMonthRule.INSTANCE;
//    }
//
//    /**
//     * Gets the rule for the day-of-year field in the Julian chronology.
//     *
//     * @return the rule for the day-of-year field, never null
//     */
//    public static DateTimeFieldRule<Integer> dayOfYearRule() {
//        return DayOfYearRule.INSTANCE;
//    }
//
//    /**
//     * Gets the rule for the day-of-week field in the Julian chronology.
//     *
//     * @return the rule for the day-of-week field, never null
//     */
//    public static DateTimeFieldRule<DayOfWeek> dayOfWeekRule() {
//        return DayOfWeekRule.INSTANCE;
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit for years.
     * <p>
     * The period unit defines the concept of a period of a year in the Julian calendar system.
     * This has an estimated duration equal to 365.25 days.
     * <p>
     * See {@link #yearRule()} for the main date-time field.
     *
     * @return the period unit for years, never null
     */
    public static PeriodUnit periodYears() {
        return YEARS;
    }

    /**
     * Gets the period unit for months.
     * <p>
     * The period unit defines the concept of a period of a month in the Julian calendar system.
     * This has an estimated duration equal to one-twelfth of 365.25 days.
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period unit for months, never null
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
     * @return the period unit for weeks, never null
     */
    public static PeriodUnit periodWeeks() {
        return ISOChronology.periodWeeks();
    }

    /**
     * Gets the period unit for days.
     * <p>
     * The period unit defines the concept of a period of a day.
     * This is equivalent to the ISO days period unit.
     * <p>
     * See {@link #dayOfMonthRule()} for the main date-time field.
     *
     * @return the period unit for days, never null
     */
    public static PeriodUnit periodDays() {
        return ISOChronology.periodDays();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class YearRule extends DateTimeFieldRule<Integer> implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<Integer> INSTANCE = new YearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private YearRule() {
//            super(Integer.class, JulianChronology.INSTANCE, "Year", YEARS, null, JulianDate.MIN_YEAR, JulianDate.MAX_YEAR);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        protected Integer derive(Calendrical calendrical) {
//            JulianDate cd = calendrical.get(JulianDate.rule());
//            return cd != null ? cd.getYear() : null;
//        }
//        @Override
//        protected void merge(CalendricalMerger merger) {
//            Integer moyVal = merger.getValue(JulianChronology.monthOfYearRule());
//            Integer domVal = merger.getValue(JulianChronology.dayOfMonthRule());
//            if (moyVal != null && domVal != null) {
//                int year = merger.getValue(this);
//                JulianDate date;
//                if (merger.getContext().isStrict()) {
//                    date = JulianDate.of(year, moyVal, domVal);
//                } else {
//                    date = JulianDate.of(year, 1, 1)
//                                .plusMonths(moyVal).plusMonths(-1).plusDays(domVal).plusDays(-1);
//                }
//                merger.storeMerged(LocalDate.rule(), date.toLocalDate());
//                merger.removeProcessed(this);
//                merger.removeProcessed(JulianChronology.monthOfYearRule());
//                merger.removeProcessed(JulianChronology.dayOfMonthRule());
//            }
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class MonthOfYearRule extends DateTimeFieldRule<MonthOfYear> implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<MonthOfYear> INSTANCE = new MonthOfYearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private MonthOfYearRule() {
//            super(MonthOfYear.class, JulianChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 13);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        protected MonthOfYear derive(Calendrical calendrical) {
//            JulianDate jd = calendrical.get(JulianDate.rule());
//            return jd != null ? jd.getMonthOfYear() : null;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class DayOfMonthRule extends DateTimeFieldRule<Integer> implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfMonthRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private DayOfMonthRule() {
//            super(Integer.class, JulianChronology.INSTANCE, "DayOfMonth", periodDays(), MONTHS, 1, 30);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        public int getSmallestMaximumValue() {
//            return 5;
//        }
//        @Override
//        public int getMaximumValue(Calendrical calendrical) {
//            Integer year = calendrical.get(JulianChronology.yearRule());
//            Integer moy = calendrical.get(JulianChronology.monthOfYearRule());
//            if (year != null && moy != null) {
//                if (moy == 13) {
//                    return isLeapYear(year) ? 6 : 5;
//                } else {
//                    return 30;
//                }
//            }
//            return getMaximumValue();
//        }
//        @Override
//        protected Integer derive(Calendrical calendrical) {
//            JulianDate cd = calendrical.get(JulianDate.rule());
//            return cd != null ? cd.getDayOfMonth() : null;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class DayOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfYearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private DayOfYearRule() {
//            super(Integer.class, JulianChronology.INSTANCE, "DayOfYear", periodDays(), YEARS, 1, 366);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        public int getSmallestMaximumValue() {
//            return 365;
//        }
//        @Override
//        public int getMaximumValue(Calendrical calendrical) {
//            Integer year = calendrical.get(JulianChronology.yearRule());
//            if (year != null) {
//                return isLeapYear(year) ? 366 : 365;
//            }
//            return getMaximumValue();
//        }
//        @Override
//        protected Integer derive(Calendrical calendrical) {
//            JulianDate cd = calendrical.get(JulianDate.rule());
//            return cd != null ? cd.getDayOfYear() : null;
//        }
//        @Override
//        protected void merge(CalendricalMerger merger) {
//            Integer yearVal = merger.getValue(JulianChronology.yearRule());
//            if (yearVal != null) {
//                int doy = merger.getValue(this);
//                JulianDate date;
//                if (merger.getContext().isStrict()) {
//                    date = JulianDate.of(yearVal, 1, 1).withDayOfYear(doy);
//                } else {
//                    date = JulianDate.of(yearVal, 1, 1).plusDays(doy).plusDays(-1);
//                }
//                merger.storeMerged(LocalDate.rule(), date.toLocalDate());
//                merger.removeProcessed(this);
//                merger.removeProcessed(JulianChronology.yearRule());
//            }
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class DayOfWeekRule extends DateTimeFieldRule<DayOfWeek> implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<DayOfWeek> INSTANCE = new DayOfWeekRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private DayOfWeekRule() {
//            super(DayOfWeek.class, JulianChronology.INSTANCE, "DayOfWeek", periodDays(), periodWeeks(), 1, 7);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        protected DayOfWeek derive(Calendrical calendrical) {
//            JulianDate cd = calendrical.get(JulianDate.rule());
//            return cd != null ? cd.getDayOfWeek() : null;
//        }
//    }

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
            super("JulianMonths", Duration.ofStandardHours(31557600L / 12L));  // 365.25 days / 12
        }
        private Object readResolve() {
            return MONTHS;
        }
    }

}
