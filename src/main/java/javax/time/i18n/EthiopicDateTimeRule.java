/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISOPeriodUnit.DAYS;
import static javax.time.calendar.ISOPeriodUnit.ERAS;
import static javax.time.calendar.ISOPeriodUnit.MONTHS;
import static javax.time.calendar.ISOPeriodUnit.YEARS;

import java.io.Serializable;

import javax.time.calendar.DateTimeResolver;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.DateTimeRuleRange;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.PeriodUnit;
import javax.time.calendar.Year;

/**
 * The rules of date and time used by the Ethiopic calendar system.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class EthiopicDateTimeRule extends DateTimeRule implements Serializable {
    // TODO: package scoped, expose via chrono

    private static final int DAYS_PER_CYCLE = 146097;
    private static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    private static final long DAYS_0000_TO_MJD_EPOCH = 678941;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ordinal for performance and serialization.
     */
    private final int ordinal;

    /**
     * Restricted constructor.
     */
    private EthiopicDateTimeRule(int ordinal, 
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            long minimumValue,
            long maximumValue,
            long smallestMaximum,
            DateTimeRule baseRule) {
        super(name, periodUnit, periodRange,
                DateTimeRuleRange.of(minimumValue, smallestMaximum, maximumValue), baseRule);
        this.ordinal = ordinal;
    }

    /**
     * Deserialize singletons.
     * 
     * @return the resolved value, not null
     */
    private Object readResolve() {
        return RULE_CACHE[ordinal];
    }

    //-----------------------------------------------------------------------
//    @Override
//    protected DateTimeRuleRange doValueRangeFromOther(DateTimeRule valueRule, long value) {
//        switch (ordinal) {
//            case DAY_OF_MONTH_ORDINAL: {
//                long moy = valueRule.extract(value, MONTH_OF_YEAR);
//                if (moy != Long.MIN_VALUE) {
//                    if (moy == 13) {
//                        long year = valueRule.extract(value, YEAR);
//                        if (year != Long.MIN_VALUE) {
//                            return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year) ? 6 : 5);
//                        }
//                        return DateTimeRuleRange.of(1, 5, 6);
//                    } else {
//                        return DateTimeRuleRange.of(1, 30);
//                    }
//                }
//                break;
//            }
//            case DAY_OF_YEAR_ORDINAL: {
//                long year = valueRule.extract(value, YEAR);
//                if (year != Long.MIN_VALUE) {
//                    return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year) ? 366 : 365);
//                }
//                break;
//            }
//        }
//        return super.getValueRange();
//    }

    //-------------------------------------------------------------------------
    @Override
    protected long doExtractFromInstant(long localEpochDay, long nanoOfDay, long offsetSecs) {
        return doGetFromEpochDay(localEpochDay);
    }

    //-----------------------------------------------------------------------
    @Override
    protected long doExtractFromValue(DateTimeRule fieldRule, long fieldValue) {
        if (DAY_OF_YEAR.equals(fieldRule)) {
            switch (ordinal) {
                case DAY_OF_MONTH_ORDINAL: return domFromDoy(fieldValue);
                case MONTH_OF_YEAR_ORDINAL: return moyFromDoy(fieldValue);
            }
            return Long.MIN_VALUE;
        }
        if (YEAR.equals(fieldRule)) {
            switch (ordinal) {
                case YEAR_OF_ERA_ORDINAL: return yoeFromY(fieldValue);
                case ERA_ORDINAL: return eFromY(fieldValue);
            }
        }
        long ed = EPOCH_DAY.extractFromValue(fieldRule, fieldValue);
        if (ed != Long.MIN_VALUE) {
            return doGetFromEpochDay(ed);
        }
        return Long.MIN_VALUE;
    }

    private long doGetFromEpochDay(long ed) {
        long dayCount = ed + DAYS_0000_TO_1970 - DAYS_0000_TO_MJD_EPOCH + 574971;
        long y = ((dayCount * 4) + 1463) / 1461;
        switch (ordinal) {
            case YEAR_OF_ERA_ORDINAL: return yoeFromY(y);
            case YEAR_ORDINAL: return y;
            case ERA_ORDINAL: return eFromY(y);
        }
        long startYearEpochDay = (y - 1) * 365 + (y / 4);
        long doy0 = dayCount - startYearEpochDay;
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: return ((doy0 % 30) + 1);
            case DAY_OF_YEAR_ORDINAL: return (doy0 + 1);
            case MONTH_OF_YEAR_ORDINAL: return ((doy0 / 30) + 1);
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    @Override
    protected long[] doSetIntoInstant(long newValue, long localEpochDay, long nanoOfDay, long offsetSecs, DateTimeResolver resolver) {
        localEpochDay = doSetIntoEpochDay(newValue, localEpochDay, resolver);
        return new long[] {localEpochDay, nanoOfDay, offsetSecs};
    }

    @Override
    protected long doSetIntoValue(long newValue, DateTimeRule fieldRule, long fieldValue, DateTimeResolver resolver) {
        if (DAY_OF_YEAR.equals(fieldRule)) {
            // allow overflow to invalid day-of-year  TODO resolve?
            switch (ordinal) {
                case DAY_OF_MONTH_ORDINAL: return fieldValue + (newValue - domFromDoy(fieldValue));
                case MONTH_OF_YEAR_ORDINAL: return fieldValue + (newValue - moyFromDoy(fieldValue)) * 30;
            }
            return Long.MIN_VALUE;
        }
        if (YEAR.equals(fieldRule)) {
            switch (ordinal) {
                case YEAR_OF_ERA_ORDINAL: return fieldValue + (newValue - yoeFromY(fieldValue));
                case ERA_ORDINAL: return (1 - yoeFromY(fieldValue));
            }
        }
        long ed = EPOCH_DAY.extractFromValue(fieldRule, fieldValue);
        if (ed != Long.MIN_VALUE) {
            long newEd = doSetIntoEpochDay(newValue, ed, resolver);
            return EPOCH_DAY.setIntoValue(newEd, fieldRule, fieldValue, resolver);
        }
        return super.doSetIntoValue(newValue, fieldRule, fieldValue, resolver);
    }

    private long doSetIntoEpochDay(long newValue, long fieldEd, DateTimeResolver resolver) {
        long dayCount = fieldEd + DAYS_0000_TO_1970 - DAYS_0000_TO_MJD_EPOCH + 574971;
        long year = ((dayCount * 4) + 1463) / 1461;
        long startYearEpochDay = (year - 1) * 365 + (year / 4);
        long doy = dayCount - startYearEpochDay + 1;
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL:
            case DAY_OF_YEAR_ORDINAL:
            case MONTH_OF_YEAR_ORDINAL: {
                long newDoy = this.setIntoValue(newValue, DAY_OF_YEAR, doy, resolver);
                return packDate(year, newDoy);
            }
            case YEAR_OF_ERA_ORDINAL:
            case YEAR_ORDINAL:
            case ERA_ORDINAL: {
                long newYear = this.setIntoValue(newValue, YEAR, year, resolver);
                return packDate(newYear, doy);
            }
        }
        return Long.MIN_VALUE;
    }

    private static long packDate(long year, long doy) {
        YEAR.checkValidValue(year);
        DAY_OF_YEAR.checkValidValue(doy);
        if (doy == 366 && CopticChronology.isLeapYear(year) == false) {
            throw new InvalidCalendarFieldException("Invalid Coptic date", CopticChronology.DAY_OF_YEAR);
        }
        return (year - 1) * 365 + (year / 4) + doy - 1;
    }

    //-----------------------------------------------------------------------
    private static long yoeFromY(long y) {
        return y < 1 ? (1 - y) : y;
    }

    private static int eFromY(long y) {
        return y < 1 ? 0 : 1;
    }

    private static long moyFromDoy(long doy) {
        return ((doy - 1) / 30) + 1;
    }

    private static long domFromDoy(long doy) {
        return ((doy - 1) % 30) + 1;
    }

    //-----------------------------------------------------------------------
    @Override
    public long convertToPeriod(long value) {
        return (ordinal <= YEAR_OF_ERA_ORDINAL ? value - 1 : value);
    }

    @Override
    public long convertFromPeriod(long period) {
        return (ordinal <= YEAR_OF_ERA_ORDINAL ? period + 1 : period);
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(DateTimeRule other) {
        if (other instanceof EthiopicDateTimeRule) {
            return ordinal - ((EthiopicDateTimeRule) other).ordinal;
        }
        return super.compareTo(other);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EthiopicDateTimeRule) {
            return ordinal == ((EthiopicDateTimeRule) obj).ordinal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return EthiopicDateTimeRule.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    private static final int DAY_OF_MONTH_ORDINAL =         0;
    private static final int DAY_OF_YEAR_ORDINAL =          1;
    private static final int MONTH_OF_YEAR_ORDINAL =        2;
    private static final int YEAR_OF_ERA_ORDINAL =          3;
    private static final int YEAR_ORDINAL =                 4;
    private static final int ERA_ORDINAL =                  5;

    //-----------------------------------------------------------------------
    /**
     * The rule for the day-of-month field in the Ethiopic chronology.
     * <p>
     * This field counts days sequentially from the start of the month.
     * The first day of the month is 1 and the last is 30 except in month 13 when it is 5 or 6.
     */
    public static final DateTimeRule DAY_OF_MONTH = new EthiopicDateTimeRule(DAY_OF_MONTH_ORDINAL, "EthiopicDayOfMonth", DAYS, MONTHS, 1, 30, 5, null);
    /**
     * The rule for the day-of-year field in the Ethiopic chronology.
     * <p>
     * This field counts days sequentially from the start of the year.
     * The first day of the year is 1 and the last is 365, or 366 in a leap year.
     */
    public static final DateTimeRule DAY_OF_YEAR = new EthiopicDateTimeRule(DAY_OF_YEAR_ORDINAL, "EthiopicDayOfYear", DAYS, YEARS, 1, 366, 365, null);
    /**
     * The rule for the month-of-year field in the Ethiopic chronology.
     * <p>
     * This field counts months sequentially from the start of the year from 1 to 13.
     */
    public static final DateTimeRule MONTH_OF_YEAR = new EthiopicDateTimeRule(MONTH_OF_YEAR_ORDINAL, "EthiopicMonthOfYear", MONTHS, YEARS, 1, 13, 13, null);
    /**
     * The rule for the year field in the Ethiopic chronology.
     * <p>
     * This field counts years as a single number, including year zero.
     */
    public static final DateTimeRule YEAR_OF_ERA = new EthiopicDateTimeRule(YEAR_OF_ERA_ORDINAL, "EthiopicYearOfEra", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, null);
    /**
     * The rule for the year field in the Ethiopic chronology.
     * <p>
     * This field counts years as a single number, including year zero.
     */
    public static final DateTimeRule YEAR = new EthiopicDateTimeRule(YEAR_ORDINAL, "EthiopicYear", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, null);  // TODO
    /**
     * The rule for the year field in the Ethiopic chronology.
     * <p>
     * This field counts years as a single number, including year zero.
     */
    public static final DateTimeRule ERA = new EthiopicDateTimeRule(ERA_ORDINAL, "EthiopicEra", ERAS, null, 0, 1, 1, null);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final DateTimeRule[] RULE_CACHE = new DateTimeRule[] {
        DAY_OF_MONTH, DAY_OF_YEAR,
        MONTH_OF_YEAR, YEAR_OF_ERA, YEAR, ERA,
    };

}
