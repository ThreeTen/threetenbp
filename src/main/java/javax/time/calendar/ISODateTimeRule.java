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

import static javax.time.calendar.ISOChronology.NANOS_PER_HOUR;
import static javax.time.calendar.ISOChronology.NANOS_PER_MINUTE;
import static javax.time.calendar.ISOChronology.NANOS_PER_SECOND;
import static javax.time.calendar.ISOChronology.SECONDS_PER_DAY;
import static javax.time.calendar.ISOPeriodUnit.DAYS;
import static javax.time.calendar.ISOPeriodUnit.HOURS;
import static javax.time.calendar.ISOPeriodUnit.MILLIS;
import static javax.time.calendar.ISOPeriodUnit.MINUTES;
import static javax.time.calendar.ISOPeriodUnit.MONTHS;
import static javax.time.calendar.ISOPeriodUnit.NANOS;
import static javax.time.calendar.ISOPeriodUnit.QUARTERS;
import static javax.time.calendar.ISOPeriodUnit.SECONDS;
import static javax.time.calendar.ISOPeriodUnit.WEEKS;
import static javax.time.calendar.ISOPeriodUnit.WEEK_BASED_YEARS;
import static javax.time.calendar.ISOPeriodUnit.YEARS;
import static javax.time.calendar.ISOPeriodUnit._12_HOURS;

import java.io.Serializable;

import javax.time.MathUtils;

/**
 * The rules of date and time used by the ISO calendar system, such as 'HourOfDay' or 'MonthOfYear'.
 * <p>
 * {@code ISODateTimeRule} consists of immutable definitions of the rules of the ISO calendar system.
 * This is the <i>de facto</i> world calendar and the most important set of rules in the time framework.
 * <p>
 * The ISO calendar system follows the rules of the Gregorian calendar for all time.
 * Thus, dates in the past, particularly before 1583, may not correspond to historical documents.
 * <p>
 * Rules contain complex logic to allow them to be derived and combined to form other rules.
 * For example, the value for 'AmPmOfDay' can be derived from 'HourOfDay'.
 * <p>
 * Other calendar systems should use these rules wherever possible to define their own rules.
 * For example, the definition of 'DayOfWeek' is usually the same in other calendar systems.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ISODateTimeRule extends DateTimeRule implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constant for the minimum week-based-year.
     */
    private static final int MIN_WEEK_BASED_YEAR = Year.MIN_YEAR;  // TODO check value
    /**
     * Constant for the maximum week-based-year.
     */
    private static final int MAX_WEEK_BASED_YEAR = Year.MAX_YEAR;  // TODO check value
    /**
     * Constant for the day-of-month length of 28.
     */
    private static final DateTimeRuleRange RANGE_1_28 = DateTimeRuleRange.of(1, 28);
    /**
     * Constant for the day-of-month length of 29.
     */
    private static final DateTimeRuleRange RANGE_1_29 = DateTimeRuleRange.of(1, 29);
    /**
     * Constant for the day-of-month length of 28 or 29.
     */
    private static final DateTimeRuleRange RANGE_1_28_29 = DateTimeRuleRange.of(1, 28, 29);
    /**
     * Constant for the day-of-month length of 30.
     */
    private static final DateTimeRuleRange RANGE_1_30 = DateTimeRuleRange.of(1, 30);
    /**
     * Constant for the day-of-month length of 31.
     */
    private static final DateTimeRuleRange RANGE_1_31 = DateTimeRuleRange.of(1, 31);
    /**
     * Lookup for the range.
     */
    private static final DateTimeRuleRange[] RANGE = {
        RANGE_1_31, RANGE_1_28_29, RANGE_1_31, RANGE_1_30, RANGE_1_31, RANGE_1_30,
        RANGE_1_31, RANGE_1_31, RANGE_1_30, RANGE_1_31, RANGE_1_30, RANGE_1_31};

    /**
     * Ordinal for performance and serialization.
     */
    private final int ordinal;

    /**
     * Restricted constructor.
     */
    private ISODateTimeRule(int ordinal, 
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
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: {
                DateTimeField moyVal = calendrical.get(MONTH_OF_YEAR);
                if (moyVal != null) {
                    int moy = moyVal.getValidIntValue();
                    if (moy == 2) {
                        DateTimeField yearVal = calendrical.get(YEAR);
                        if (yearVal != null) {
                            return (ISOChronology.isLeapYear(yearVal.getValue()) ? RANGE_1_29 : RANGE_1_28);
                        }
                    }
                    return RANGE[moy - 1];
                }
                break;
            }
            case DAY_OF_YEAR_ORDINAL: {
                DateTimeField yearVal = calendrical.get(YEAR);
                if (yearVal != null) {
                    int len = ISOChronology.isLeapYear(yearVal.getValidIntValue()) ? 366 : 365;
                    return DateTimeRuleRange.of(1, len);
                }
                break;
            }
            case ALIGNED_WEEK_OF_MONTH_ORDINAL: {
                DateTimeField moyVal = calendrical.get(MONTH_OF_YEAR);
                if (moyVal != null) {
                    if (moyVal.getValue() == 2) {
                        DateTimeField yearVal = calendrical.get(YEAR);
                        if (yearVal != null) {
                            return DateTimeRuleRange.of(1, ISOChronology.isLeapYear(yearVal.getValidIntValue()) ? 5 : 4);
                        }
                    } else {
                        return DateTimeRuleRange.of(1, 5);
                    }
                }
                break;
            }
            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: {
                // TODO: derive from WeekBasedYear
                LocalDate date = calendrical.get(LocalDate.rule());
                if (date != null) {
                    date = date.withDayOfYear(1);
                    if (date.getDayOfWeek() == DayOfWeek.THURSDAY ||
                            (date.getDayOfWeek() == DayOfWeek.WEDNESDAY && ISOChronology.isLeapYear(date.getYear()))) {
                        return DateTimeRuleRange.of(1, 53);
                    }
                    return DateTimeRuleRange.of(1, 52);
                }
                break;
            }
        }
        return super.getValueRange();
    }

    //-----------------------------------------------------------------------
//    @Override
//    protected DateTimeRuleRange doValueRangeFromOther(DateTimeRule valueRule, long value) {
//        switch (ordinal) {
//            case DAY_OF_MONTH_ORDINAL: {
//                long moy = valueRule.extract(value, MONTH_OF_YEAR);
//                if (moy != Long.MIN_VALUE) {
//                    if (moy == 2) {
//                        long year = valueRule.extract(value, YEAR);
//                        if (year != Long.MIN_VALUE) {
//                            return (ISOChronology.isLeapYear(year) ? RANGE_1_29 : RANGE_1_28);
//                        }
//                    }
//                    return RANGE[(int) (moy - 1)];
//                }
//                break;
//            }
//            case DAY_OF_YEAR_ORDINAL: {
//                long year = valueRule.extract(value, YEAR);
//                if (year != Long.MIN_VALUE) {
//                    int len = ISOChronology.isLeapYear(year) ? 366 : 365;
//                    return DateTimeRuleRange.of(1, len);
//                }
//                break;
//            }
//            case ALIGNED_WEEK_OF_MONTH_ORDINAL: {
//                long moy = valueRule.extract(value, MONTH_OF_YEAR);
//                if (moy != Long.MIN_VALUE) {
//                    if (moy == 2) {
//                        long year = valueRule.extract(value, YEAR);
//                        if (year != Long.MIN_VALUE) {
//                            return DateTimeRuleRange.of(1, ISOChronology.isLeapYear(year) ? 5 : 4);
//                        }
//                    } else {
//                        return DateTimeRuleRange.of(1, 5);
//                    }
//                }
//                break;
//            }
//        }
//        return super.getValueRange();
//    }

    //-----------------------------------------------------------------------
    // maybe for performance in ISO?
//    @Override
//    protected long doExtractFromTime(long nanoOfDay) {
//        switch (ordinal) {
//            case NANO_OF_SECOND_ORDINAL: return simpleGet(nanoOfDay, 1, NANOS_PER_SECOND);
//            case NANO_OF_DAY_ORDINAL: return simpleGet(nanoOfDay, 1, NANOS_PER_DAY);
//            case SECOND_OF_MINUTE_ORDINAL: return simpleGet(nanoOfDay, 1, NANOS_PER_MINUTE);
//            case SECOND_OF_DAY_ORDINAL: return simpleGet(nanoOfDay, 1, NANOS_PER_DAY);
//            case MINUTE_OF_HOUR_ORDINAL: return simpleGet(nanoOfDay, NANOS_PER_MINUTE, MINUTES_PER_HOUR);
//            case MINUTE_OF_DAY_ORDINAL: return simpleGet(nanoOfDay, NANOS_PER_MINUTE, MINUTES_PER_DAY);
//            case CLOCK_HOUR_OF_AMPM_ORDINAL: return clock(simpleGet(nanoOfDay, NANOS_PER_HOUR, 12), 12);
//            case HOUR_OF_AMPM_ORDINAL: return simpleGet(nanoOfDay, NANOS_PER_HOUR, 12);
//            case CLOCK_HOUR_OF_DAY_ORDINAL: return clock(simpleGet(nanoOfDay, NANOS_PER_HOUR, HOURS_PER_DAY), 24);
//            case HOUR_OF_DAY_ORDINAL: return simpleGet(nanoOfDay, NANOS_PER_HOUR, HOURS_PER_DAY);
//            case AMPM_OF_DAY_ORDINAL: return simpleGet(nanoOfDay, NANOS_PER_HOUR * 12, 2);
//        }
//        return Long.MIN_VALUE;
//    }
//
//    private static long simpleGet(long nanoOfDay, long div, long mod) {
//        return (nanoOfDay / div) % mod;
//    }
//
//    private static long clock(long value, int clock) {
//        return (value == 0 ? clock : value);
//    }

    static long packHmsn(int hod, int moh, int som, int nos) {
        long total = hod * NANOS_PER_HOUR;
        total += moh * NANOS_PER_MINUTE;
        total += som * NANOS_PER_SECOND;
        total += nos;
        return total;
    }

    //-----------------------------------------------------------------------
    static long epochDayFromPackedDate(long pemd) {
        long dom0 = pemd & 31;
        long em = (pemd >> 5);
        long year = MathUtils.floorDiv(em, 12) + 1970;
        long y = year;
        long m = MathUtils.floorMod(em, 12) + 1;
        long total = 0;
        total += 365 * y;
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
        } else {
            total -= y / -4 - y / -100 + y / -400;
        }
        total += ((367 * m - 362) / 12);
        total += dom0;
        if (m > 2) {
            total--;
            if (ISOChronology.isLeapYear(year) == false) {
                total--;
            }
        }
        return total - ISOChronology.DAYS_0000_TO_1970;
    }

    static long packedDateFromEpochDay(long ed) {
        // find the march-based year
        long zeroDay = ed + ISOChronology.DAYS_0000_TO_1970 - 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
        long adjust = 0;
        if (zeroDay < 0) {
            // adjust negative years to positive for calculation
            long adjustCycles = (zeroDay + 1) / ISOChronology.DAYS_PER_CYCLE - 1;
            adjust = adjustCycles * 400;
            zeroDay += -adjustCycles * ISOChronology.DAYS_PER_CYCLE;
        }
        long yearEst = (400 * zeroDay + 591) / ISOChronology.DAYS_PER_CYCLE;
        long doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        if (doyEst < 0) {
            // fix estimate
            yearEst--;
            doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        }
        yearEst += adjust;  // reset any negative year
        int marchDoy0 = (int) doyEst;
        
        // convert march-based values back to january-based
        int marchMonth0 = (marchDoy0 * 5 + 2) / 153;
        int month = (marchMonth0 + 2) % 12 + 1;
        int dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1;
        long year = yearEst + marchMonth0 / 10;
        
        // pack
        return packPemd(year, month, dom);
    }

    static long packPemd(long year, int month, int dom) {
        return packPemd((year - 1970) * 12 + (month - 1), dom);
    }

    static long packPemd(long em, int dom) {
        return (em << 5) + (dom - 1);
    }

    //-------------------------------------------------------------------------
    @Override
    protected boolean isDate() {
        return (ordinal == EPOCH_DAY_ORDINAL || ordinal == PACKED_EPOCH_MONTH_DAY_ORDINAL);
    }

    //-----------------------------------------------------------------------
    @Override
    protected long doExtractFromInstant(long localEpochDay, long nanoOfDay, long offsetSecs) {
        switch (ordinal) {
            case EPOCH_MILLI_ORDINAL: {
                if (offsetSecs != Long.MIN_VALUE) {
                    return (localEpochDay * SECONDS_PER_DAY + offsetSecs) * 1000 + (nanoOfDay / 1000000L);
                }
                break;
            }
            case EPOCH_SECOND_ORDINAL: {
                if (offsetSecs != Long.MIN_VALUE) {
                    return localEpochDay * SECONDS_PER_DAY + (nanoOfDay / NANOS_PER_SECOND) + offsetSecs;
                }
                break;
            }

        }
        if (ordinal >= DAY_OF_WEEK_ORDINAL) {
            return doExtractFromEpochDay(localEpochDay);
        }
        return super.doExtractFromValue(NANO_OF_DAY, nanoOfDay);
    }

    //-------------------------------------------------------------------------
    @Override
    protected long doExtractFromValue(DateTimeRule fieldRule, long fieldValue) {
        if (PACKED_EPOCH_MONTH_DAY.equals(fieldRule)) {
            return extractFromPackedDate(fieldValue);
        }
        if (EPOCH_DAY.equals(fieldRule)) {
            return doExtractFromEpochDay(fieldValue);
        }
        if (isDate()) {  // needed to avoid infinite loop
            long ed = fieldRule.convertToPeriod(fieldValue);
            return doExtractFromEpochDay(ed);
        }
        return super.doExtractFromValue(fieldRule, fieldValue);
    }

    long extractFromPackedDate(long pemd) {
        switch (ordinal) {
            case DAY_OF_WEEK_ORDINAL: return dowFromEd(epochDayFromPackedDate(pemd));
            case DAY_OF_MONTH_ORDINAL: return domFromPemd(pemd);
            case DAY_OF_YEAR_ORDINAL: return doyFromPemd(pemd);
            case EPOCH_DAY_ORDINAL: return epochDayFromPackedDate(pemd);
            case PACKED_EPOCH_MONTH_DAY_ORDINAL: return pemd;
            case ALIGNED_WEEK_OF_MONTH_ORDINAL: return awomFromDom(domFromPemd(pemd));
            case ALIGNED_WEEK_OF_YEAR_ORDINAL: return awoyFromDoy(doyFromPemd(pemd));
            case MONTH_OF_QUARTER_ORDINAL: return moqFromMoy(moyFromEm(emFromPemd(pemd)));
            case MONTH_OF_YEAR_ORDINAL: return moyFromEm(emFromPemd(pemd));
            case QUARTER_OF_YEAR_ORDINAL: return qoyFromMoy(moyFromEm(emFromPemd(pemd)));
            case ZERO_EPOCH_MONTH_ORDINAL: return emFromPemd(pemd);
            case YEAR_ORDINAL: return yFromEm(emFromPemd(pemd));
        }
        return Long.MIN_VALUE;
    }

    private long doExtractFromEpochDay(long ed) {
        switch (ordinal) {
            case DAY_OF_WEEK_ORDINAL: return dowFromEd(ed);
            case EPOCH_DAY_ORDINAL: return ed;
        }
        return extractFromPackedDate(packedDateFromEpochDay(ed));
    }

    //-------------------------------------------------------------------------
    @Override
    protected long[] doSetIntoInstant(long newValue, long localEpochDay, long nanoOfDay, long offsetSecs) {
        if (ordinal == EPOCH_SECOND_ORDINAL) {
            offsetSecs = ((offsetSecs << 1) >> 1);  // converts MIN_VALUE to 0
            long localSecs = newValue - offsetSecs;
            localEpochDay = MathUtils.floorDiv(localSecs, SECONDS_PER_DAY);
            nanoOfDay = MathUtils.floorMod(localSecs, SECONDS_PER_DAY) * NANOS_PER_SECOND + (nanoOfDay % NANOS_PER_SECOND);
        } else if (ordinal == EPOCH_MILLI_ORDINAL) {
            offsetSecs = ((offsetSecs << 1) >> 1);  // converts MIN_VALUE to 0
            long localSecs = MathUtils.floorDiv(newValue, 1000) - offsetSecs;
            localEpochDay = MathUtils.floorDiv(localSecs, SECONDS_PER_DAY);
            nanoOfDay = MathUtils.floorMod(localSecs, SECONDS_PER_DAY) * NANOS_PER_SECOND +
                            MathUtils.floorMod(newValue, 1000) * 1000000L + (nanoOfDay % 1000000L);
        } else if (ordinal >= DAY_OF_WEEK_ORDINAL) {
            localEpochDay = doSetIntoEpochDay(newValue, localEpochDay);
        } else {
            nanoOfDay = doSetIntoValue(newValue, NANO_OF_DAY, nanoOfDay);
        }
        return new long[] {localEpochDay, nanoOfDay, offsetSecs};
    }

    @Override
    protected long doSetIntoValue(long newValue, DateTimeRule fieldRule, long fieldValue) {
        if (PACKED_EPOCH_MONTH_DAY.equals(fieldRule)) {
            return doSetIntoPackedDate(newValue, fieldValue);
        }
        if (fieldRule.isDate()) {
            long ed = fieldRule.convertToPeriod(fieldValue);
            long newEd = doSetIntoEpochDay(newValue, ed);
            return fieldRule.convertFromPeriod(newEd);
        }
        return super.doSetIntoValue(newValue, fieldRule, fieldValue);
    }

    private long doSetIntoEpochDay(long newValue, long fieldEd) {
        switch (ordinal) {
            case DAY_OF_WEEK_ORDINAL: return fieldEd + (newValue - dowFromEd(fieldEd));
            case EPOCH_DAY_ORDINAL: return newValue;
            default: return epochDayFromPackedDate(doSetIntoPackedDate(newValue, packedDateFromEpochDay(fieldEd)));
        }
    }

    private long doSetIntoPackedDate(long newValue, long fieldPemd) {
        switch (ordinal) {
            case DAY_OF_WEEK_ORDINAL: return packedDateFromEpochDay(doSetIntoEpochDay(newValue, epochDayFromPackedDate(fieldPemd)));
            case DAY_OF_MONTH_ORDINAL: return fieldPemd + (newValue - domFromPemd(fieldPemd));
            case DAY_OF_YEAR_ORDINAL: return 0;  // TODO
            case EPOCH_DAY_ORDINAL: return packedDateFromEpochDay(newValue);
            case PACKED_EPOCH_MONTH_DAY_ORDINAL: return newValue;
            case ALIGNED_WEEK_OF_MONTH_ORDINAL: return fieldPemd + (newValue - awomFromDom(domFromPemd(fieldPemd))) * 7;
            case ALIGNED_WEEK_OF_YEAR_ORDINAL: return 0;  // TODO
            case MONTH_OF_QUARTER_ORDINAL: return fieldPemd + (newValue - moqFromMoy(moyFromEm(emFromPemd(fieldPemd)))) * 32;
            case MONTH_OF_YEAR_ORDINAL: return fieldPemd + (newValue - moyFromEm(emFromPemd(fieldPemd))) * 32;
            case ZERO_EPOCH_MONTH_ORDINAL: return fieldPemd + (newValue - emFromPemd(fieldPemd)) * 32;
            case QUARTER_OF_YEAR_ORDINAL: return fieldPemd + (newValue - qoyFromMoy(moyFromEm(emFromPemd(fieldPemd)))) * 32 * 3;
            case YEAR_ORDINAL: return fieldPemd + (newValue - yFromEm(emFromPemd(fieldPemd))) * 32 * 12;
        }
        return Long.MIN_VALUE;
    }

    //-----------------------------------------------------------------------
    private static long dowFromEd(long ed) {
        return MathUtils.floorMod(ed + 3, 7) + 1;
    }

    static long domFromPemd(long pemd) {
        return (pemd & 31) + 1;
    }

    static long emFromPemd(long pemd) {
        return (pemd >> 5);
    }

    static long doyFromPemd(long pemd) {
        long dom = domFromPemd(pemd);
        long em = emFromPemd(pemd);
        long moy = moyFromEm(em);
        long year = yFromEm(em);
        return MonthOfYear.of((int) moy).getMonthStartDayOfYear(ISOChronology.isLeapYear(year)) + dom - 1;
    }

    static long moyFromEm(long em) {
        return MathUtils.floorMod(em, 12) + 1;
    }

    static long yFromEm(long em) {
        return MathUtils.floorDiv(em, 12) + 1970;
    }

    private static long awomFromDom(long value) {
        return ((value - 1) / 7) + 1;
    }

    private static long awoyFromDoy(long value) {
        return ((value - 1) / 7) + 1;
    }

    private static long qoyFromMoy(long value) {
        return ((value - 1) / 3) + 1;
    }

    private static long moqFromMoy(long value) {
        return ((value - 1) % 3) + 1;
    }
 
    //-----------------------------------------------------------------------
    @Override
    protected void normalize(CalendricalEngine engine) {
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: {
                // year-month-day
                DateTimeField dom = engine.getField(DAY_OF_MONTH, false);
                DateTimeField epm = engine.getField(ZERO_EPOCH_MONTH, false);
                if (dom != null && epm != null) {
                    int year = MathUtils.safeToInt(MathUtils.floorDiv(epm.getValue(), 12));
                    int moy = MathUtils.floorMod(epm.getValue(), 12) + 1;
                    LocalDate date = LocalDate.of(year, moy, 1).plusDays(MathUtils.safeDecrement(dom.getValue()));
                    engine.setDate(date, true);
                }
                break;
            }
            case DAY_OF_YEAR_ORDINAL: {
                // year-day
                DateTimeField doy = engine.getField(DAY_OF_YEAR, false);
                DateTimeField year = engine.derive(YEAR);
                if (doy != null && year != null) {
                    LocalDate date = ISOChronology.getDateFromDayOfYear(year.getValidIntValue(), 1)
                            .plusDays(doy.getValue()).minusDays(1);
                    engine.setDate(date, true);
                }
                break;
            }
            case ALIGNED_WEEK_OF_MONTH_ORDINAL: {
                // year-month-alignedWeek-day
                DateTimeField dow = engine.getField(DAY_OF_WEEK, false);
                DateTimeField wom = engine.getField(ALIGNED_WEEK_OF_MONTH, false);
                DateTimeField epm = engine.getField(ZERO_EPOCH_MONTH, false);
                if (dow != null && wom != null && epm != null) {
                    int year = MathUtils.safeToInt(MathUtils.floorDiv(epm.getValue(), 12));
                    int moy = MathUtils.floorMod(epm.getValue(), 12) + 1;
                    LocalDate date = LocalDate.of(year, moy, 1).plusWeeks(MathUtils.safeDecrement(wom.getValidIntValue()));
                    date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow.getValidIntValue())));
                    engine.setDate(date, true);
                }
                break;
            }
            case ALIGNED_WEEK_OF_YEAR_ORDINAL: {
                // year-alignedWeek-day
                DateTimeField woy = engine.getField(ALIGNED_WEEK_OF_YEAR, false);
                DateTimeField dow = engine.getField(DAY_OF_WEEK, false);
                DateTimeField year = engine.derive(YEAR);
                if (woy != null && dow != null && year != null) {
                    LocalDate date = LocalDate.of(year.getValidIntValue(), 1, 1).plusWeeks(woy.getValidIntValue() - 1);
                    date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow.getValidIntValue())));
                    engine.setDate(date, true);
                }
                break;
            }
            case EPOCH_DAY_ORDINAL: {
                DateTimeField epd = engine.getField(EPOCH_DAY, false);
                if (epd != null) {
                    engine.setDate(LocalDate.ofEpochDay(epd.getValue()), true);
                }
                break;
            }
            case NANO_OF_DAY_ORDINAL: {
                DateTimeField nod = engine.getField(NANO_OF_DAY, false);
                if (nod != null) {
                    LocalTime time = LocalTime.ofNanoOfDay(nod.getValue());  // TODO: lenient overflow
                    engine.setTime(time, true);
                }
                break;
            }
            case EPOCH_SECOND_ORDINAL: {
                DateTimeField eps = engine.getField(EPOCH_SECOND, false);
                DateTimeField nos = engine.getField(NANO_OF_SECOND, false);  // TODO: handle other nano fields
                if (eps != null && nos != null) {
                    OffsetDateTime odt = OffsetDateTime.ofEpochSecond(eps.getValue(), ZoneOffset.UTC);
                    odt = odt.plusNanos(nos.getValue());
                    engine.setDate(odt.toLocalDate(), true);
                    engine.setTime(odt.toLocalTime(), true);
                    engine.setOffset(odt.getOffset(), true);
                }
                break;
            }
        }
    }

    //-----------------------------------------------------------------------
    @Override
    protected DateTimeField deriveFrom(CalendricalEngine engine) {
        return deriveFrom(engine.getDate(false), engine.getTime(false), engine.getOffset(false));
    }

    /**
     * Derive from the major classes.
     */
    DateTimeField deriveFrom(LocalDate date, LocalTime time, ZoneOffset offset) {
        if (ordinal >= DAY_OF_WEEK_ORDINAL) {
            if (date != null) {
                switch (ordinal) {
                    case DAY_OF_WEEK_ORDINAL: return field(ISOChronology.getDayOfWeekFromDate(date).getValue());
                    case DAY_OF_MONTH_ORDINAL: return field(date.getDayOfMonth());
                    case DAY_OF_YEAR_ORDINAL: return field(ISOChronology.getDayOfYearFromDate(date));
                    case EPOCH_DAY_ORDINAL: return field(date.toEpochDay());
                    case ALIGNED_WEEK_OF_MONTH_ORDINAL: return field((date.getDayOfMonth() - 1) / 7 + 1);
                    case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: return field(ISOChronology.getWeekOfWeekBasedYearFromDate(date));
                    case ALIGNED_WEEK_OF_YEAR_ORDINAL: return field((date.getDayOfYear() - 1) / 7 + 1);
                    case MONTH_OF_QUARTER_ORDINAL: return field(date.getMonthOfYear().getMonthOfQuarter());
                    case MONTH_OF_YEAR_ORDINAL: return field(date.getMonthOfYear().getValue());
                    case ZERO_EPOCH_MONTH_ORDINAL: return field(MathUtils.safeAdd(MathUtils.safeMultiply(date.getYear(), 12L), date.getMonthOfYear().ordinal()));
                    case QUARTER_OF_YEAR_ORDINAL: return field(date.getMonthOfYear().getQuarterOfYear().getValue());
                    case WEEK_BASED_YEAR_ORDINAL: return field(ISOChronology.getWeekBasedYearFromDate(date));
                    case YEAR_ORDINAL: return field(date.getYear());
                }
            }
        } else {
            if (time != null) {
                switch (ordinal) {
                    case NANO_OF_MILLI_ORDINAL: return field(time.getNanoOfSecond() % 1000000L);
                    case NANO_OF_SECOND_ORDINAL: return field(time.getNanoOfSecond());
                    case NANO_OF_MINUTE_ORDINAL: return field(time.toNanoOfDay() % 60L * 1000000000L);
                    case NANO_OF_HOUR_ORDINAL: return field(time.toNanoOfDay() % 3600L * 1000000000L);
                    case NANO_OF_DAY_ORDINAL: return field(time.toNanoOfDay());
                    case MILLI_OF_SECOND_ORDINAL: return field(time.getNanoOfSecond() / 1000000);
                    case MILLI_OF_MINUTE_ORDINAL: return field((time.toNanoOfDay() / 1000000L) % 60 * 1000L);
                    case MILLI_OF_HOUR_ORDINAL: return field((time.toNanoOfDay() / 1000000L) % 3600 * 1000L);
                    case MILLI_OF_DAY_ORDINAL: return field(time.toNanoOfDay() / 1000000L);
                    case SECOND_OF_MINUTE_ORDINAL: return field(time.getSecondOfMinute());
                    case SECOND_OF_HOUR_ORDINAL: return field(time.getMinuteOfHour() * 60 + time.getSecondOfMinute());
                    case SECOND_OF_DAY_ORDINAL: return field(time.toSecondOfDay());
                    case EPOCH_SECOND_ORDINAL: {
                        if (date != null && offset != null) {
                            return field(OffsetDateTime.of(date, time, offset).toEpochSecond());
                        }
                        break;
                    }
                    case MINUTE_OF_HOUR_ORDINAL: return field(time.getMinuteOfHour());
                    case MINUTE_OF_DAY_ORDINAL: return field(time.toSecondOfDay() / 60);
                    case CLOCK_HOUR_OF_AMPM_ORDINAL: return field(((time.getHourOfDay() + 11) % 12) + 1);
                    case HOUR_OF_AMPM_ORDINAL: return field(time.getHourOfDay() % 12);
                    case CLOCK_HOUR_OF_DAY_ORDINAL: return field(((time.getHourOfDay() + 23) % 24) + 1);
                    case HOUR_OF_DAY_ORDINAL: return field(time.getHourOfDay());
                    case AMPM_OF_DAY_ORDINAL: return field(time.getHourOfDay() / 12);
                }
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public long convertToPeriod(long value) {
        switch (ordinal) {
            case CLOCK_HOUR_OF_AMPM_ORDINAL:
                return (value == 12 ? 0 : value);  // this matches GregorianCalendar
            case CLOCK_HOUR_OF_DAY_ORDINAL:
                return (value == 24 ? 0 : value);  // this matches GregorianCalendar
            case DAY_OF_WEEK_ORDINAL:
            case DAY_OF_MONTH_ORDINAL:
            case DAY_OF_YEAR_ORDINAL:
            case ALIGNED_WEEK_OF_MONTH_ORDINAL:
            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL:
            case ALIGNED_WEEK_OF_YEAR_ORDINAL:
            case MONTH_OF_QUARTER_ORDINAL:
            case MONTH_OF_YEAR_ORDINAL:
            case QUARTER_OF_YEAR_ORDINAL:
                return MathUtils.safeDecrement(value);
            case PACKED_EPOCH_MONTH_DAY_ORDINAL:
                return epochDayFromPackedDate(value);
            default:
                return value;
        }
    }

    @Override
    public long convertFromPeriod(long amount) {
        switch (ordinal) {
            case CLOCK_HOUR_OF_AMPM_ORDINAL:
                return (amount == 0 ? 12 : amount);
            case CLOCK_HOUR_OF_DAY_ORDINAL:
                return (amount == 0 ? 24 : amount);
            case DAY_OF_WEEK_ORDINAL:
            case DAY_OF_MONTH_ORDINAL:
            case DAY_OF_YEAR_ORDINAL:
            case ALIGNED_WEEK_OF_MONTH_ORDINAL:
            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL:
            case ALIGNED_WEEK_OF_YEAR_ORDINAL:
            case MONTH_OF_QUARTER_ORDINAL:
            case MONTH_OF_YEAR_ORDINAL:
            case QUARTER_OF_YEAR_ORDINAL:
                return MathUtils.safeIncrement(amount);
            case PACKED_EPOCH_MONTH_DAY_ORDINAL:
                return packedDateFromEpochDay(amount);
            default:
                return amount;
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(DateTimeRule other) {
        if (other instanceof ISODateTimeRule) {
            return ordinal - ((ISODateTimeRule) other).ordinal;
        }
        return super.compareTo(other);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ISODateTimeRule) {
            return ordinal == ((ISODateTimeRule) obj).ordinal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ISODateTimeRule.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    private static final int NANO_OF_MILLI_ORDINAL =        0 * 16;
    private static final int NANO_OF_SECOND_ORDINAL =       1 * 16;
    private static final int NANO_OF_MINUTE_ORDINAL =       2 * 16;
    private static final int NANO_OF_HOUR_ORDINAL =         3 * 16;
    private static final int NANO_OF_DAY_ORDINAL =          4 * 16;
    private static final int MILLI_OF_SECOND_ORDINAL =      5 * 16;
    private static final int MILLI_OF_MINUTE_ORDINAL =      6 * 16;
    private static final int MILLI_OF_HOUR_ORDINAL =        7 * 16;
    private static final int MILLI_OF_DAY_ORDINAL =         8 * 16;
    private static final int EPOCH_MILLI_ORDINAL =          9 * 16;
    private static final int SECOND_OF_MINUTE_ORDINAL =     10 * 16;
    private static final int SECOND_OF_HOUR_ORDINAL =       11 * 16;
    private static final int SECOND_OF_DAY_ORDINAL =        12 * 16;
    private static final int EPOCH_SECOND_ORDINAL =         13 * 16;
    private static final int MINUTE_OF_HOUR_ORDINAL =       14 * 16;
    private static final int MINUTE_OF_DAY_ORDINAL =        15 * 16;
    private static final int CLOCK_HOUR_OF_AMPM_ORDINAL =   16 * 16;
    private static final int HOUR_OF_AMPM_ORDINAL =         17 * 16;
    private static final int CLOCK_HOUR_OF_DAY_ORDINAL =    18 * 16;
    private static final int HOUR_OF_DAY_ORDINAL =          19 * 16;
    private static final int AMPM_OF_DAY_ORDINAL =          20 * 16;
    private static final int DAY_OF_WEEK_ORDINAL =          21 * 16;
    private static final int DAY_OF_MONTH_ORDINAL =         22 * 16;
    private static final int DAY_OF_YEAR_ORDINAL =          23 * 16;
    private static final int EPOCH_DAY_ORDINAL =            24 * 16;
    private static final int PACKED_EPOCH_MONTH_DAY_ORDINAL = 25 * 16;
    private static final int ALIGNED_WEEK_OF_MONTH_ORDINAL = 26 * 16;
    private static final int WEEK_OF_WEEK_BASED_YEAR_ORDINAL = 27 * 16;
    private static final int ALIGNED_WEEK_OF_YEAR_ORDINAL = 28 * 16;
    private static final int MONTH_OF_QUARTER_ORDINAL =     29 * 16;
    private static final int MONTH_OF_YEAR_ORDINAL =        30 * 16;
    private static final int ZERO_EPOCH_MONTH_ORDINAL =     31 * 16;
    private static final int QUARTER_OF_YEAR_ORDINAL =      32 * 16;
    private static final int WEEK_BASED_YEAR_ORDINAL =      33 * 16;
    private static final int YEAR_ORDINAL =                 34 * 16;

    //-----------------------------------------------------------------------
    /**
     * The rule for the nano-of-day field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the day.
     * The values run from 0 to 86,399,999,999,999.
     */
    public static final ISODateTimeRule NANO_OF_DAY = new ISODateTimeRule(NANO_OF_DAY_ORDINAL, "NanoOfDay", NANOS, DAYS, 0, 86399999999999L, 86399999999999L, null);
    /**
     * The rule for the nano-of-milli field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the millisecond.
     * The values run from 0 to 999,999.
     */
    public static final ISODateTimeRule NANO_OF_MILLI = new ISODateTimeRule(NANO_OF_MILLI_ORDINAL, "NanoOfMilli", NANOS, MILLIS, 0, 999999, 999999, NANO_OF_DAY);
    /**
     * The rule for the nano-of-second field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 999,999,999.
     */
    public static final ISODateTimeRule NANO_OF_SECOND = new ISODateTimeRule(NANO_OF_SECOND_ORDINAL, "NanoOfSecond", NANOS, SECONDS, 0, 999999999, 999999999, NANO_OF_DAY);
    /**
     * The rule for the nano-of-minute field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the minute.
     * The values run from 0 to 59,999,999,999.
     */
    public static final ISODateTimeRule NANO_OF_MINUTE = new ISODateTimeRule(NANO_OF_MINUTE_ORDINAL, "NanoOfMinute", NANOS, MINUTES, 0, 59999999999L, 59999999999L, NANO_OF_DAY);
    /**
     * The rule for the nano-of-hour field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the hour.
     * The values run from 0 to 3,599,999,999,999.
     */
    public static final ISODateTimeRule NANO_OF_HOUR = new ISODateTimeRule(NANO_OF_HOUR_ORDINAL, "NanoOfHour", NANOS, HOURS, 0, 3599999999999L, 3599999999999L, NANO_OF_DAY);

    /**
     * The rule for the milli-of-second field.
     * <p>
     * This field counts milliseconds sequentially from the start of the second.
     * The values run from 0 to 999.
     */
    public static final ISODateTimeRule MILLI_OF_SECOND = new ISODateTimeRule(MILLI_OF_SECOND_ORDINAL, "MilliOfSecond", MILLIS, SECONDS, 0, 999, 999, NANO_OF_DAY);
    /**
     * The rule for the milli-of-minute field.
     * <p>
     * This field counts milliseconds sequentially from the start of the minute.
     * The values run from 0 to 59,999.
     */
    public static final ISODateTimeRule MILLI_OF_MINUTE = new ISODateTimeRule(MILLI_OF_MINUTE_ORDINAL, "MilliOfMinute", MILLIS, MINUTES, 0, 59999, 59999, NANO_OF_DAY);
    /**
     * The rule for the milli-of-hour field.
     * <p>
     * This field counts milliseconds sequentially from the start of the hour.
     * The values run from 0 to 3,599,999.
     */
    public static final ISODateTimeRule MILLI_OF_HOUR = new ISODateTimeRule(MILLI_OF_HOUR_ORDINAL, "MilliOfHour", MILLIS, HOURS, 0, 3599999, 3599999, NANO_OF_DAY);
    /**
     * The rule for the milli-of-day field.
     * <p>
     * This field counts milliseconds sequentially from the start of the day.
     * The values run from 0 to 86,399,999.
     */
    public static final ISODateTimeRule MILLI_OF_DAY = new ISODateTimeRule(MILLI_OF_DAY_ORDINAL, "MilliOfDay", MILLIS, DAYS, 0, 86399999, 86399999, NANO_OF_DAY);
    /**
     * The rule for the epoch-milli field.
     * <p>
     * This field counts milliseconds sequentially from 1970-01-01Z.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final ISODateTimeRule EPOCH_MILLI = new ISODateTimeRule(EPOCH_MILLI_ORDINAL, "EpochMilli", MILLIS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);

    /**
     * The rule for the second-of-minute field.
     * <p>
     * This field counts seconds sequentially from the start of the minute.
     * The values run from 0 to 59.
     */
    public static final ISODateTimeRule SECOND_OF_MINUTE = new ISODateTimeRule(SECOND_OF_MINUTE_ORDINAL, "SecondOfMinute", SECONDS, MINUTES, 0, 59, 59, NANO_OF_DAY);
    /**
     * The rule for the second-of-hour field.
     * <p>
     * This field counts seconds sequentially from the start of the hour.
     * The values run from 0 to 3,599.
     */
    public static final ISODateTimeRule SECOND_OF_HOUR = new ISODateTimeRule(SECOND_OF_HOUR_ORDINAL, "SecondOfHour", SECONDS, HOURS, 0, 3599, 3599, NANO_OF_DAY);
    /**
     * The rule for the second-of-day field.
     * <p>
     * This field counts seconds sequentially from the start of the day.
     * The values run from 0 to 86399.
     */
    public static final ISODateTimeRule SECOND_OF_DAY = new ISODateTimeRule(SECOND_OF_DAY_ORDINAL, "SecondOfDay", SECONDS, DAYS, 0, 86399, 86399, NANO_OF_DAY);
    /**
     * The rule for the epoch-second field.
     * <p>
     * This field counts seconds sequentially from 1970-01-01Z.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final ISODateTimeRule EPOCH_SECOND = new ISODateTimeRule(EPOCH_SECOND_ORDINAL, "EpochSecond", SECONDS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);

    /**
     * The rule for the minute-of-hour field.
     * <p>
     * This field counts minutes sequentially from the start of the hour.
     * The values run from 0 to 59.
     */
    public static final ISODateTimeRule MINUTE_OF_HOUR = new ISODateTimeRule(MINUTE_OF_HOUR_ORDINAL, "MinuteOfHour", MINUTES, HOURS, 0, 59, 59, NANO_OF_DAY);
    /**
     * The rule for the minute-of-day field.
     * <p>
     * This field counts minutes sequentially from the start of the day.
     * The values run from 0 to 1439.
     */
    public static final ISODateTimeRule MINUTE_OF_DAY = new ISODateTimeRule(MINUTE_OF_DAY_ORDINAL, "MinuteOfDay", MINUTES, DAYS, 0, 1439, 1439, NANO_OF_DAY);

    /**
     * The rule for the hour of AM/PM field from 0 to 11.
     * <p>
     * This field counts hours sequentially from the start of the half-day AM/PM.
     * The values run from 0 to 11.
     */
    public static final ISODateTimeRule HOUR_OF_AMPM = new ISODateTimeRule(HOUR_OF_AMPM_ORDINAL, "HourOfAmPm", HOURS, _12_HOURS, 0, 11, 11, NANO_OF_DAY);
    /**
     * The rule for the clock hour of AM/PM field from 1 to 12.
     * <p>
     * This field counts hours sequentially within the half-day AM/PM as normally seen on a clock or watch.
     * The values run from 1 to 12.
     */
    public static final ISODateTimeRule CLOCK_HOUR_OF_AMPM = new ISODateTimeRule(CLOCK_HOUR_OF_AMPM_ORDINAL, "ClockHourOfAmPm", HOURS, _12_HOURS, 1, 12, 12, HOUR_OF_AMPM);

    /**
     * The rule for the hour-of-day field.
     * <p>
     * This field counts hours sequentially from the start of the day.
     * The values run from 0 to 23.
     */
    public static final ISODateTimeRule HOUR_OF_DAY = new ISODateTimeRule(HOUR_OF_DAY_ORDINAL, "HourOfDay", HOURS, DAYS, 0, 23, 23, NANO_OF_DAY);
    /**
     * The rule for the clock hour of AM/PM field from 1 to 24.
     * <p>
     * This field counts hours sequentially within the day starting from 1.
     * The values run from 1 to 24.
     */
    public static final ISODateTimeRule CLOCK_HOUR_OF_DAY = new ISODateTimeRule(CLOCK_HOUR_OF_DAY_ORDINAL, "ClockHourOfDay", HOURS, DAYS, 1, 24, 24, HOUR_OF_DAY);
    /**
     * The rule for the AM/PM of day field.
     * <p>
     * This field defines the half-day AM/PM value. The hour-of-day from 0 to 11 is
     * defined as AM, while the hours from 12 to 23 are defined as PM.
     * AM is defined with the value 0, while PM is defined with the value 1.
     * <p>
     * The enum {@link AmPmOfDay} should be used wherever possible in
     * applications when referring to the day of the week to avoid
     * hard-coding the values.
     */
    public static final ISODateTimeRule AMPM_OF_DAY = new ISODateTimeRule(AMPM_OF_DAY_ORDINAL, "AmPmOfDay", _12_HOURS, DAYS, 0, 1, 1, NANO_OF_DAY);

    /**
     * The rule for the day-of-week field.
     * <p>
     * This field uses the ISO-8601 values for the day-of-week.
     * These define Monday as value 1 to Sunday as value 7.
     * <p>
     * The enum {@link DayOfWeek} should be used wherever possible in
     * applications when referring to the day of the week value to avoid
     * needing to remember the values from 1 to 7.
     */
    public static final ISODateTimeRule DAY_OF_WEEK = new ISODateTimeRule(DAY_OF_WEEK_ORDINAL, "DayOfWeek", DAYS, WEEKS, 1, 7, 7, null);
    /**
     * The rule for the day-of-month field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the month.
     * The first day of the month is 1 and the last is 28, 29, 30 or 31
     * depending on the month and whether it is a leap year.
     */
    public static final ISODateTimeRule DAY_OF_MONTH = new ISODateTimeRule(DAY_OF_MONTH_ORDINAL, "DayOfMonth", DAYS, MONTHS, 1, 31, 28, null);
    /**
     * The rule for the day-of-year field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the year.
     * The first day of the year is 1 and the last is 365, or 366 in a leap year.
     */
    public static final ISODateTimeRule DAY_OF_YEAR = new ISODateTimeRule(DAY_OF_YEAR_ORDINAL, "DayOfYear", DAYS, YEARS, 1, 366, 365, null);
    /**
     * The rule for the epoch-day field.
     * <p>
     * This field counts days sequentially from 1970-01-01.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final ISODateTimeRule EPOCH_DAY = new ISODateTimeRule(EPOCH_DAY_ORDINAL, "EpochDay", DAYS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);
    /**
     * The rule for the aligned-week-of-month field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days aligned with the first
     * day of the month. The 1st to the 7th of a month is always week 1 while the
     * 8th to the 14th is always week 2 and so on.
     * <p>
     * This field can be used to create concepts such as 'the second Saturday'
     * of a month. To achieve this, setup a {@link DateTimeFields} instance
     * using this rule and the {@link #DAY_OF_WEEK day-of-week} rule.
     * <p>
     * See {@link WeekRules} for other week fields, including that defined by ISO-8601.
     */
    public static final ISODateTimeRule ALIGNED_WEEK_OF_MONTH = new ISODateTimeRule(ALIGNED_WEEK_OF_MONTH_ORDINAL, "AlignedWeekOfMonth", WEEKS, MONTHS, 1, 5, 4, DAY_OF_MONTH);
    /**
     * The rule for the week-of-week-based-year field in the ISO chronology.
     * <p>
     * This field counts weeks using the ISO-8601 algorithm.
     * The first week of the year is the week which has at least 4 days in the year
     * using a Monday to Sunday week definition. Thus it is possible for the first
     * week to start on any day from the 29th December in the previous year to the
     * 4th January in the new year. The year which is aligned with this field is
     * known as the {@link #WEEK_BASED_YEAR week-based-year}.
     */
    public static final ISODateTimeRule WEEK_OF_WEEK_BASED_YEAR = new ISODateTimeRule(WEEK_OF_WEEK_BASED_YEAR_ORDINAL, "WeekOfWeekBasedYear", WEEKS, WEEK_BASED_YEARS, 1, 53, 52, null);
    /**
     * The rule for the aligned-week-of-year field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days aligned with the first of January.
     * The 1st to the 7th of January is always week 1 while the 8th to the 14th is always
     * week 2 and so on.
     * <p>
     * This field can be used to create concepts such as 'the second Saturday'
     * of a year. To achieve this, setup a {@link DateTimeFields} instance
     * using this rule and the {@link #DAY_OF_WEEK day-of-week} rule.
     * <p>
     * See {@link WeekRules} for other week fields, including that defined by ISO-8601.
     */
    public static final ISODateTimeRule ALIGNED_WEEK_OF_YEAR = new ISODateTimeRule(ALIGNED_WEEK_OF_YEAR_ORDINAL, "AlignedWeekOfYear", WEEKS, YEARS, 1, 53, 53, DAY_OF_YEAR);

    /**
     * The rule for the zero-epoch-month field.
     * <p>
     * This field counts months sequentially from 0000-01-01 in the ISO year
     * numbering scheme, see {@link #YEAR}.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final ISODateTimeRule ZERO_EPOCH_MONTH = new ISODateTimeRule(ZERO_EPOCH_MONTH_ORDINAL, "ZeroEpochMonth", MONTHS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);
    /**
     * The rule for the month-of-quarter field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the quarter.
     * The first month of the quarter is 1 and the last is 3.
     * Each quarter lasts exactly three months.
     */
    public static final ISODateTimeRule MONTH_OF_QUARTER = new ISODateTimeRule(MONTH_OF_QUARTER_ORDINAL, "MonthOfQuarter", MONTHS, QUARTERS, 1, 3, 3, ZERO_EPOCH_MONTH);
    /**
     * The rule for the month-of-year field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the year.
     * The values follow the ISO-8601 standard and normal human interactions.
     * These define January as value 1 to December as value 12.
     * <p>
     * The enum {@link MonthOfYear} should be used wherever possible in applications
     * when referring to the day of the week to avoid hard-coding the values.
     */
    public static final ISODateTimeRule MONTH_OF_YEAR = new ISODateTimeRule(MONTH_OF_YEAR_ORDINAL, "MonthOfYear", MONTHS, YEARS, 1, 12, 12, ZERO_EPOCH_MONTH);
    /**
     * The rule for the quarter-of-year field in the ISO chronology.
     * <p>
     * This field counts quarters sequentially from the start of the year.
     * The first quarter of the year is 1 and the last is 4.
     * Each quarter lasts exactly three months.
     */
    public static final ISODateTimeRule QUARTER_OF_YEAR = new ISODateTimeRule(QUARTER_OF_YEAR_ORDINAL, "QuarterOfYear", QUARTERS, YEARS, 1, 4, 4, ZERO_EPOCH_MONTH);
    /**
     * The rule for the week-based-year field in the ISO chronology.
     * <p>
     * This field is the year that results from calculating weeks with the ISO-8601 algorithm.
     * See {@link #WEEK_OF_WEEK_BASED_YEAR week-of-week-based-year} for details.
     * <p>
     * The week-based-year will either be 52 or 53 weeks long, depending on the
     * result of the algorithm for a particular date.
     */
    public static final ISODateTimeRule WEEK_BASED_YEAR = new ISODateTimeRule(
            WEEK_BASED_YEAR_ORDINAL, "WeekBasedYear", WEEK_BASED_YEARS, null, MIN_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR, null);
    /**
     * The rule for the year field in the ISO chronology.
     * <p>
     * This field counts years using the modern civil calendar system as defined
     * by ISO-8601. There is no historical cutover (as found in historical dates
     * such as from the Julian to Gregorian calendar).
     * <p>
     * The implication of this is that historical dates will not be accurate.
     * All work requiring accurate historical dates must use the appropriate
     * chronology that defines the Gregorian cutover.
     * <p>
     * A further implication of the ISO-8601 rules is that the year zero
     * exists. This roughly equates to 1 BC/BCE, however the alignment is
     * not exact as explained above.
     */
    public static final ISODateTimeRule YEAR = new ISODateTimeRule(YEAR_ORDINAL, "Year", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, ZERO_EPOCH_MONTH);

    /**
     * The rule for the the combined year-month-day in the ISO chronology.
     * <p>
     * This field combines the year, month-of-year and day-of-month values into a single {@code long}.
     * The format uses the least significant 5 bits for the unsigned day-of-month  (from 1 to 31)
     * and the most significant 59 bits for the signed count of epoch months, where 0 is January 1970.
     * <p>
     * This field is intended primarily for internal use.
     */
    public static final ISODateTimeRule PACKED_EPOCH_MONTH_DAY = new ISODateTimeRule(
            PACKED_EPOCH_MONTH_DAY_ORDINAL, "PackedEpochMonthDay", DAYS, null, ((Year.MIN_YEAR * 12L) << 5) + 1, ((Year.MAX_YEAR * 12L) << 5) + 31, ((Year.MAX_YEAR * 12L) << 5) + 31, EPOCH_DAY);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final DateTimeRule[] RULE_CACHE = new DateTimeRule[] {
        NANO_OF_MILLI, NANO_OF_SECOND, NANO_OF_SECOND, NANO_OF_MINUTE, NANO_OF_DAY,
        MILLI_OF_SECOND, MILLI_OF_MINUTE, MILLI_OF_HOUR, MILLI_OF_DAY, EPOCH_MILLI,
        SECOND_OF_MINUTE, SECOND_OF_HOUR, SECOND_OF_DAY, EPOCH_SECOND,
        MINUTE_OF_HOUR, MINUTE_OF_DAY,
        CLOCK_HOUR_OF_AMPM, HOUR_OF_AMPM, CLOCK_HOUR_OF_DAY, HOUR_OF_DAY, AMPM_OF_DAY,
        DAY_OF_WEEK, DAY_OF_MONTH, DAY_OF_YEAR, EPOCH_DAY, PACKED_EPOCH_MONTH_DAY,
        ALIGNED_WEEK_OF_MONTH, WEEK_OF_WEEK_BASED_YEAR, ALIGNED_WEEK_OF_YEAR,
        MONTH_OF_QUARTER, MONTH_OF_YEAR, ZERO_EPOCH_MONTH,
        QUARTER_OF_YEAR,
        WEEK_BASED_YEAR,
        YEAR,
    };

}
