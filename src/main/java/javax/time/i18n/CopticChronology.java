/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.period.PeriodUnits.*;

import java.io.Serializable;

import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;

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
 * CopticChronology is thread-safe and immutable.
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

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the Coptic chronology.
     *
     * @return the rule for the year field, never null
     */
    @Override
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Coptic chronology.
     *
     * @return the rule for the month of year field, never null
     */
    @Override
    public DateTimeFieldRule monthOfYear() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Coptic chronology.
     *
     * @return the rule for the day of month field, never null
     */
    @Override
    public DateTimeFieldRule dayOfMonth() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Coptic chronology.
     *
     * @return the rule for the day of year field, never null
     */
    @Override
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Coptic chronology.
     *
     * @return the rule for the day of week field, never null
     */
    @Override
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * The hour of day field is not supported by the Coptic chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule hourOfDay() {
        throw new UnsupportedOperationException("CopticChronology does not support the hour of day field");
    }

    /**
     * The minute of hour field is not supported by the Coptic chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule minuteOfHour() {
        throw new UnsupportedOperationException("CopticChronology does not support the minute of hour field");
    }

    /**
     * The second of minute field is not supported by the Coptic chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule secondOfMinute() {
        throw new UnsupportedOperationException("CopticChronology does not support the second of minute field");
    }

    /**
     * The nano of second field is not supported by the Coptic chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule nanoOfSecond() {
        throw new UnsupportedOperationException("CopticChronology does not support the nano of second field");
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(CopticChronology.INSTANCE, "Year", YEARS, null, CopticDate.MIN_YEAR, CopticDate.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            if (date != null) {
                return CopticDate.copticDate(date).getYear();
            }
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(CopticChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 13);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            if (date != null) {
                return CopticDate.copticDate(date).getMonthOfYear();
            }
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(CopticChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 30);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 5;
        }
        /** {@inheritDoc} */
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            if (date != null) {
                return CopticDate.copticDate(date).getDayOfMonth();
            }
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(CopticChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 365;
        }
        /** {@inheritDoc} */
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            if (date != null) {
                return CopticDate.copticDate(date).getDayOfYear();
            }
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(CopticChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            if (date != null) {
                return CopticDate.copticDate(date).getDayOfWeek();
            }
            return null;
        }
    }

}
