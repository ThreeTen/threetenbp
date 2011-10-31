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

import javax.time.calendar.DateTimeRule;
import javax.time.calendar.DateTimeRuleRange;
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
        this.ordinal = ordinal;  // 16 multiplier allow space for new rules
    }

    /**
     * Deserialize singletons.
     * 
     * @return the resolved value, not null
     */
    private Object readResolve() {
        return RULE_CACHE[ordinal / 16];
    }

    //-----------------------------------------------------------------------
    @Override
    protected DateTimeRuleRange doValueRangeFromOther(DateTimeRule valueRule, long value) {
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: {
                long moy = valueRule.extract(value, MONTH_OF_YEAR);
                if (moy != Long.MIN_VALUE) {
                    if (moy == 13) {
                        long year = valueRule.extract(value, YEAR);
                        if (year != Long.MIN_VALUE) {
                            return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year) ? 6 : 5);
                        }
                        return DateTimeRuleRange.of(1, 5, 6);
                    } else {
                        return DateTimeRuleRange.of(1, 30);
                    }
                }
                break;
            }
            case DAY_OF_YEAR_ORDINAL: {
                long year = valueRule.extract(value, YEAR);
                if (year != Long.MIN_VALUE) {
                    return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year) ? 366 : 365);
                }
                break;
            }
        }
        return super.getValueRange();
    }

    //-----------------------------------------------------------------------
    @Override
    protected long doExtractFromThis(long value, DateTimeRule requiredRule) {
        if (requiredRule instanceof EthiopicDateTimeRule) {
            return extractEthiopic(value, (EthiopicDateTimeRule) requiredRule);
        } else if (ordinal == PACKED_YEAR_DAY_ORDINAL) {
            return extractValue(EPOCH_DAY, edFromPyd(value), requiredRule);
        }
        return Long.MIN_VALUE;
    }

    @Override
    protected long doExtractFromOther(DateTimeRule valueRule, long value) {
        long ed = extractValue(valueRule, value, EPOCH_DAY);
        if (ed != Long.MIN_VALUE) {
            return extractFromEd(ed, this);
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    private long extractEthiopic(long value, EthiopicDateTimeRule requiredRule) {
        switch (ordinal) {
            case DAY_OF_YEAR_ORDINAL: return extractFromDoy(value, requiredRule);
            case PACKED_YEAR_DAY_ORDINAL: return extractFromPyd(value, requiredRule);
            case YEAR_ORDINAL: return extractFromY(value, requiredRule);
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    private static long extractFromPyd(long pyd, EthiopicDateTimeRule requiredRule) {
        switch (requiredRule.ordinal) {
            case DAY_OF_MONTH_ORDINAL: return domFromDoy(doyFromPyd(pyd));
            case DAY_OF_YEAR_ORDINAL: return doyFromPyd(pyd);
            case MONTH_OF_YEAR_ORDINAL: return moyFromDoy(doyFromPyd(pyd));
            case YEAR_OF_ERA_ORDINAL: return yoeFromY(yFromPyd(pyd));
            case YEAR_ORDINAL: return yFromPyd(pyd);
            case ERA_ORDINAL: return eFromY(yFromPyd(pyd));
        }
        return Long.MIN_VALUE;
    }

    private static long doyFromPyd(long pyd) {
        return (pyd & 511);
    }

    private static long yFromPyd(long pyd) {
        return (pyd >>> 9);
    }

    private static long edFromPyd(long pyd) {
        long doy = doyFromPyd(pyd);
        long y = yFromPyd(pyd);
        return (y - 1) * 365 + (y / 4) + doy - 1;
    }

    //-----------------------------------------------------------------------
    private static long extractFromY(long y, EthiopicDateTimeRule requiredRule) {
        switch (requiredRule.ordinal) {
            case YEAR_OF_ERA_ORDINAL: return yoeFromY(y);
            case ERA_ORDINAL: return eFromY(y);
        }
        return Long.MIN_VALUE;
    }

    private static long yoeFromY(long y) {
        return y < 1 ? (1 - y) : y;
    }

    private static int eFromY(long y) {
        return y < 1 ? 0 : 1;
    }

    //-----------------------------------------------------------------------
    private static long extractFromDoy(long doy, EthiopicDateTimeRule requiredRule) {
        switch (requiredRule.ordinal) {
            case DAY_OF_MONTH_ORDINAL: return domFromDoy(doy);
            case MONTH_OF_YEAR_ORDINAL: return moyFromDoy(doy);
        }
        return Long.MIN_VALUE;
    }

    private static long moyFromDoy(long doy) {
        return ((doy - 1) / 30) + 1;
    }

    private static long domFromDoy(long doy) {
        return ((doy - 1) % 30) + 1;
    }

    //-----------------------------------------------------------------------
    private static final int DAYS_PER_CYCLE = 146097;
    private static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    private static final long DAYS_0000_TO_MJD_EPOCH = 678941;

    private static long extractFromEd(long ed, EthiopicDateTimeRule requiredRule) {
        ed = ed + DAYS_0000_TO_1970 - DAYS_0000_TO_MJD_EPOCH + 574971;
        long y = ((ed * 4) + 1463) / 1461;
        switch (requiredRule.ordinal) {
            case YEAR_OF_ERA_ORDINAL: return yoeFromY(y);
            case YEAR_ORDINAL: return y;
            case ERA_ORDINAL: return eFromY(y);
        }
        long startYearEpochDay = (y - 1) * 365 + (y / 4);
        long doy0 = ed - startYearEpochDay;
        switch (requiredRule.ordinal) {
            case DAY_OF_MONTH_ORDINAL: return ((doy0 % 30) + 1);
            case DAY_OF_YEAR_ORDINAL: return (doy0 + 1);
            case PACKED_YEAR_DAY_ORDINAL: return packPyd(y, doy0 + 1);
            case MONTH_OF_YEAR_ORDINAL: return ((doy0 / 30) + 1);
        }
        return Long.MIN_VALUE;
    }

    private static long packPyd(long y, long doy) {
        return (y << 9) + doy;
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
    private static final int DAY_OF_MONTH_ORDINAL =         1 * 16;
    private static final int DAY_OF_YEAR_ORDINAL =          2 * 16;
    private static final int PACKED_YEAR_DAY_ORDINAL =      3 * 16;
    private static final int MONTH_OF_YEAR_ORDINAL =        4 * 16;
    private static final int YEAR_OF_ERA_ORDINAL =          5 * 16;
    private static final int YEAR_ORDINAL =                 6 * 16;
    private static final int ERA_ORDINAL =                  7 * 16;

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
     * The rule for the the combined year-day in the Ethiopic chronology.
     * <p>
     * This field combines the year and day-of-year values into a single {@code long}.
     * The format uses the least significant 9 bits for the unsigned day-of-year  (from 1 to 366)
     * and the most significant 55 bits for the signed Ethiopic year.
     * <p>
     * This field is intended primarily for internal use.
     */
    public static final DateTimeRule PACKED_YEAR_DAY = new EthiopicDateTimeRule(PACKED_YEAR_DAY_ORDINAL, "PackedEthiopicYearDay", DAYS, null, -1000000, 1000000, 1000000, null);  // TODO
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
        DAY_OF_MONTH, DAY_OF_YEAR, PACKED_YEAR_DAY,
        MONTH_OF_YEAR, YEAR_OF_ERA, YEAR, ERA,
    };

}
