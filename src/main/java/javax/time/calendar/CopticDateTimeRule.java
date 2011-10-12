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
package javax.time.calendar;

import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISOPeriodUnit.DAYS;
import static javax.time.calendar.ISOPeriodUnit.MONTHS;
import static javax.time.calendar.ISOPeriodUnit.YEARS;

import java.io.Serializable;

import javax.time.i18n.CopticChronology;

/**
 * The rules of date and time used by the Coptic calendar system.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class CopticDateTimeRule extends DateTimeRule implements Serializable {
    // TODO: package scoped, expose via chrono
    // TODO: packed y-doy if date resolver needed

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
    private CopticDateTimeRule(int ordinal, 
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
    public DateTimeRuleRange getValueRange(Calendrical calendrical) {
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: {
                DateTimeField moy = calendrical.get(CopticChronology.MONTH_OF_YEAR);
                if (moy != null) {
                    if (moy.getValue() == 13) {
                        DateTimeField year = calendrical.get(CopticChronology.YEAR);
                        if (year != null) {
                            return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year.getValue()) ? 6 : 5);
                        }
                        return DateTimeRuleRange.of(1, 5, 6);
                    } else {
                        return DateTimeRuleRange.of(1, 30);
                    }
                }
                break;
            }
            case DAY_OF_YEAR_ORDINAL: {
                DateTimeField year = calendrical.get(CopticChronology.YEAR);
                if (year != null) {
                    return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year.getValidIntValue()) ? 366 : 365);
                }
                break;
            }
        }
        return super.getValueRange();
    }

    //-----------------------------------------------------------------------
    @Override
    protected DateTimeRuleRange valueRangeFrom(DateTimeRule valueRule, long value) {
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: {
                long moy = valueRule.extract(value, CopticChronology.MONTH_OF_YEAR);
                if (moy != Long.MIN_VALUE) {
                    if (moy == 13) {
                        long year = valueRule.extract(value, CopticChronology.YEAR);
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
                long year = valueRule.extract(value, CopticChronology.YEAR);
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
    protected long extract(long value, DateTimeRule requiredRule) {
        if (this == requiredRule) {
            return value;
        }
        if (requiredRule instanceof CopticDateTimeRule) {
            return extractCoptic(value, (CopticDateTimeRule) requiredRule);
        }
        return requiredRule.extractFrom(this, value);
    }

    @Override
    protected long extractFrom(DateTimeRule valueRule, long value) {
        long ed = valueRule.extract(value, EPOCH_DAY);
        if (ed != Long.MIN_VALUE) {
            return extractFromEd(ed, this);
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    private long extractCoptic(long value, CopticDateTimeRule requiredRule) {
        switch (ordinal) {
            case DAY_OF_YEAR_ORDINAL: return extractFromCdoy(value, requiredRule);
            case YEAR_ORDINAL: return extractFromCy(value, requiredRule);
        }
        return Long.MIN_VALUE;
    }

    private static long extractFromCy(long cy, CopticDateTimeRule requiredRule) {
        switch (requiredRule.ordinal) {
            case YEAR_OF_ERA_ORDINAL: return cyoeFromCy(cy);
            case ERA_ORDINAL: return ceFromCy(cy);
        }
        return Long.MIN_VALUE;
    }

    private static long cyoeFromCy(long cy) {
        return cy < 1 ? (1 - cy) : cy;
    }

    private static int ceFromCy(long cy) {
        return cy < 1 ? 0 : 1;
    }

    private static long extractFromCdoy(long cdoy, CopticDateTimeRule requiredRule) {
        switch (requiredRule.ordinal) {
            case DAY_OF_MONTH_ORDINAL: return ((cdoy - 1) % 30) + 1;
            case MONTH_OF_YEAR_ORDINAL: return ((cdoy - 1) / 30) + 1;
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    private static long extractFromEd(long ed, CopticDateTimeRule requiredRule) {
        ed = ed + ISOChronology.DAYS_0000_TO_1970 - ISOChronology.DAYS_0000_TO_MJD_EPOCH + 574971;
        long cy = ((ed * 4) + 1463) / 1461;
        switch (requiredRule.ordinal) {
            case YEAR_OF_ERA_ORDINAL: return cyoeFromCy(cy);
            case YEAR_ORDINAL: return cy;
            case ERA_ORDINAL: return ceFromCy(cy);
        }
        long startYearEpochDay = (cy - 1) * 365 + (cy / 4);
        long doy0 = ed - startYearEpochDay;
        switch (requiredRule.ordinal) {
            case DAY_OF_MONTH_ORDINAL: return ((doy0 % 30) + 1);
            case DAY_OF_YEAR_ORDINAL: return (doy0 + 1);
            case MONTH_OF_YEAR_ORDINAL: return ((doy0 / 30) + 1);
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(DateTimeRule other) {
        if (other instanceof CopticDateTimeRule) {
            return ordinal - ((CopticDateTimeRule) other).ordinal;
        }
        return super.compareTo(other);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CopticDateTimeRule) {
            return ordinal == ((CopticDateTimeRule) obj).ordinal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CopticDateTimeRule.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    private static final int DAY_OF_MONTH_ORDINAL =         1 * 16;
    private static final int DAY_OF_YEAR_ORDINAL =          2 * 16;
    private static final int MONTH_OF_YEAR_ORDINAL =        3 * 16;
    private static final int YEAR_OF_ERA_ORDINAL =          4 * 16;
    private static final int YEAR_ORDINAL =                 5 * 16;
    private static final int ERA_ORDINAL =                  6 * 16;

    //-----------------------------------------------------------------------
    /**
     * The rule for the day-of-month field in the Coptic chronology.
     * <p>
     * This field counts days sequentially from the start of the month.
     * The first day of the month is 1 and the last is 30 except in month 13 when it is 5 or 6.
     */
    public static final DateTimeRule DAY_OF_MONTH = new CopticDateTimeRule(DAY_OF_MONTH_ORDINAL, "CopticDayOfMonth", DAYS, MONTHS, 1, 30, 5, null);
    /**
     * The rule for the day-of-year field in the Coptic chronology.
     * <p>
     * This field counts days sequentially from the start of the year.
     * The first day of the year is 1 and the last is 365, or 366 in a leap year.
     */
    public static final DateTimeRule DAY_OF_YEAR = new CopticDateTimeRule(DAY_OF_YEAR_ORDINAL, "CopticDayOfYear", DAYS, YEARS, 1, 366, 365, null);
    /**
     * The rule for the month-of-year field in the Coptic chronology.
     * <p>
     * This field counts months sequentially from the start of the year from 1 to 13.
     */
    public static final DateTimeRule MONTH_OF_YEAR = new CopticDateTimeRule(MONTH_OF_YEAR_ORDINAL, "CopticMonthOfYear", MONTHS, YEARS, 1, 13, 13, null);
    /**
     * The rule for the year field in the Coptic chronology.
     * <p>
     * This field counts years as a single number, including year zero.
     */
    public static final DateTimeRule YEAR_OF_ERA = new CopticDateTimeRule(YEAR_OF_ERA_ORDINAL, "CopticYearOfEra", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, null);
    /**
     * The rule for the year field in the Coptic chronology.
     * <p>
     * This field counts years as a single number, including year zero.
     */
    public static final DateTimeRule YEAR = new CopticDateTimeRule(YEAR_ORDINAL, "CopticYear", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, null);
    /**
     * The rule for the year field in the Coptic chronology.
     * <p>
     * This field counts years as a single number, including year zero.
     */
    public static final DateTimeRule ERA = new CopticDateTimeRule(ERA_ORDINAL, "CopticEra", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, null);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final DateTimeRule[] RULE_CACHE = new DateTimeRule[] {
        DAY_OF_MONTH, DAY_OF_YEAR, MONTH_OF_YEAR,
        YEAR_OF_ERA, YEAR, ERA,
    };

}
