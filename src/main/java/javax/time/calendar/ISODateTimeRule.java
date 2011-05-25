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
 * Other calendar systems should be derived from these rules wherever possible.
 * For example, the definition of 'DayOfWeek' is usually the same in other calendar systems.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ISODateTimeRule extends DateTimeRule implements Serializable {

    /**
     * The serialization version.
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
        super(ISOChronology.INSTANCE, name, periodUnit, periodRange,
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
    protected DateTimeField derive(Calendrical calendrical) {
        switch (ordinal) {
            case NANO_OF_MILLI_ORDINAL:
            case NANO_OF_SECOND_ORDINAL:
            case NANO_OF_MINUTE_ORDINAL:
            case NANO_OF_HOUR_ORDINAL:
            case NANO_OF_DAY_ORDINAL:
            case MILLI_OF_SECOND_ORDINAL:
            case MILLI_OF_MINUTE_ORDINAL:
            case MILLI_OF_HOUR_ORDINAL:
            case MILLI_OF_DAY_ORDINAL:
            case SECOND_OF_MINUTE_ORDINAL:
            case SECOND_OF_HOUR_ORDINAL:
            case SECOND_OF_DAY_ORDINAL:
            case MINUTE_OF_HOUR_ORDINAL:
            case MINUTE_OF_DAY_ORDINAL:
            case HOUR_OF_DAY_ORDINAL: {
                LocalTime time = calendrical.get(LocalTime.rule());
                if (time != null) {
                    switch (ordinal) {
                        // TODO: derive new fields
                        case NANO_OF_SECOND_ORDINAL: return field(time.getNanoOfSecond());
                        case NANO_OF_DAY_ORDINAL: return field(time.toNanoOfDay());
                        case MILLI_OF_SECOND_ORDINAL: return field(time.getNanoOfSecond() / 1000000);
                        case MILLI_OF_DAY_ORDINAL: return field((int) (time.toNanoOfDay() / 1000000L));
                        case SECOND_OF_MINUTE_ORDINAL: return field(time.getSecondOfMinute());
                        case SECOND_OF_DAY_ORDINAL: return field(time.toSecondOfDay());
                        case MINUTE_OF_HOUR_ORDINAL: return field(time.getMinuteOfHour());
                        case MINUTE_OF_DAY_ORDINAL: return field(time.toSecondOfDay() / 60);
                        case HOUR_OF_DAY_ORDINAL: return field(time.getHourOfDay());
                    }
                }
                break;
            }
            case DAY_OF_WEEK_ORDINAL:
            case DAY_OF_MONTH_ORDINAL:
            case DAY_OF_YEAR_ORDINAL:
            case EPOCH_DAY_ORDINAL:
            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL:
            case WEEK_BASED_YEAR_ORDINAL:
            case MONTH_OF_YEAR_ORDINAL:
            case EPOCH_MONTH_ORDINAL:
            case YEAR_ORDINAL: {
                LocalDate date = calendrical.get(LocalDate.rule());
                if (date != null) {
                    switch (ordinal) {
                        case DAY_OF_WEEK_ORDINAL: return field(ISOChronology.getDayOfWeekFromDate(date).getValue());
                        case DAY_OF_MONTH_ORDINAL: return field(date.getDayOfMonth());
                        case DAY_OF_YEAR_ORDINAL: return field(ISOChronology.getDayOfYearFromDate(date));
                        case EPOCH_DAY_ORDINAL: return field(date.toEpochDay());
                        case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: return field(ISOChronology.getWeekOfWeekBasedYearFromDate(date));
                        case WEEK_BASED_YEAR_ORDINAL: return field(ISOChronology.getWeekBasedYearFromDate(date));
                        case MONTH_OF_YEAR_ORDINAL: return field(date.getMonthOfYear().getValue());
                        case EPOCH_MONTH_ORDINAL: return field(date.getYear() - 1970 + date.getMonthOfYear().ordinal());
                        case YEAR_ORDINAL: return field(date.getYear());
                    }
                }
                break;
            }
            case EPOCH_SECOND_ORDINAL: {
                LocalDateTime dateTime = calendrical.get(LocalDateTime.rule());
                return dateTime != null ? field(dateTime.atOffset(ZoneOffset.UTC).toEpochSecond()) : null;
            }
            case CLOCK_HOUR_OF_AMPM_ORDINAL: {
                DateTimeField hourVal = calendrical.get(HOUR_OF_AMPM);  // TODO derive from just HOUR_OF_DAY?
                return hourVal != null ? field(((hourVal.getValidIntValue() + 11) % 12) + 1) : null;
            }
            case HOUR_OF_AMPM_ORDINAL: {
                DateTimeField hourVal = calendrical.get(HOUR_OF_DAY);
                return hourVal != null ? field(hourVal.getValidIntValue() % 12) : null;
            }
            case CLOCK_HOUR_OF_DAY_ORDINAL: {
                DateTimeField hourVal = calendrical.get(HOUR_OF_DAY);
                return hourVal != null ? field(((hourVal.getValidIntValue() + 23) % 24) + 1) : null;
            }
            case AMPM_OF_DAY_ORDINAL: {
                DateTimeField hourVal = calendrical.get(HOUR_OF_DAY);
                return hourVal != null ? field(hourVal.getValidIntValue() / 12) : null;
            }
            case MONTH_OF_QUARTER_ORDINAL: {
                DateTimeField moy = calendrical.get(MONTH_OF_YEAR);
                return moy != null ? field(((moy.getValidIntValue() - 1) % 3 + 1)) : null;
            }
            case QUARTER_OF_YEAR_ORDINAL: {
                DateTimeField moy = calendrical.get(MONTH_OF_YEAR);
                return moy != null ? field((moy.getValidIntValue() - 1) / 3 + 1) : null;
            }
            case WEEK_OF_MONTH_ORDINAL: {
                DateTimeField domVal = calendrical.get(DAY_OF_MONTH);
                return domVal != null ? field((domVal.getValidIntValue() + 6) / 7) : null;
            }
            case WEEK_OF_YEAR_ORDINAL: {
                DateTimeField doyVal = calendrical.get(DAY_OF_YEAR);
                return doyVal != null ? field((doyVal.getValidIntValue() + 6) / 7) : null;
            }
            case EPOCH_YEAR_ORDINAL: {
                DateTimeField yearVal = calendrical.get(YEAR);
                return yearVal != null ? field(yearVal.getValue()  - 1970) : null;
            }
        }
        return null;
    }
    @Override
    public DateTimeRuleRange getRange(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        switch (ordinal) {
            case DAY_OF_MONTH_ORDINAL: {
                DateTimeField moyVal = calendrical.get(MONTH_OF_YEAR);
                if (moyVal != null) {
                    MonthOfYear moy = MonthOfYear.of(moyVal.getValidIntValue());
                    if (moy == MonthOfYear.FEBRUARY) {
                        DateTimeField yearVal = calendrical.get(YEAR);
                        if (yearVal != null) {
                            return DateTimeRuleRange.of(1, moy.lengthInDays(ISOChronology.isLeapYear(yearVal.getValue())));
                        }
                        return DateTimeRuleRange.of(1, 28, 29);
                    } else {
                        return DateTimeRuleRange.of(1, moy.maxLengthInDays());
                    }
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
            case WEEK_OF_MONTH_ORDINAL: {
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
        return super.getRange();
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
            case WEEK_OF_MONTH_ORDINAL:
            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL:
            case WEEK_OF_YEAR_ORDINAL:
            case MONTH_OF_QUARTER_ORDINAL:
            case MONTH_OF_YEAR_ORDINAL:
            case QUARTER_OF_YEAR_ORDINAL:
                return MathUtils.safeDecrement(value);
            case WEEK_BASED_YEAR_ORDINAL:
            case YEAR_ORDINAL:
                return MathUtils.safeSubtract(value, 1970);
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
            case WEEK_OF_MONTH_ORDINAL:
            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL:
            case WEEK_OF_YEAR_ORDINAL:
            case MONTH_OF_QUARTER_ORDINAL:
            case MONTH_OF_YEAR_ORDINAL:
            case QUARTER_OF_YEAR_ORDINAL:
                return MathUtils.safeIncrement(amount);
            case WEEK_BASED_YEAR_ORDINAL:
            case YEAR_ORDINAL:
                return MathUtils.safeAdd(amount, 1970);
            default:
                return amount;
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(CalendricalRule<?> other) {
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
        return super.equals(obj);
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
    private static final int SECOND_OF_MINUTE_ORDINAL =     9 * 16;
    private static final int SECOND_OF_HOUR_ORDINAL =       10 * 16;
    private static final int SECOND_OF_DAY_ORDINAL =        11 * 16;
    private static final int EPOCH_SECOND_ORDINAL =         12 * 16;
    private static final int MINUTE_OF_HOUR_ORDINAL =       13 * 16;
    private static final int MINUTE_OF_DAY_ORDINAL =        14 * 16;
    private static final int CLOCK_HOUR_OF_AMPM_ORDINAL =   15 * 16;
    private static final int HOUR_OF_AMPM_ORDINAL =         16 * 16;
    private static final int CLOCK_HOUR_OF_DAY_ORDINAL =    17 * 16;
    private static final int HOUR_OF_DAY_ORDINAL =          18 * 16;
    private static final int AMPM_OF_DAY_ORDINAL =          19 * 16;
    private static final int DAY_OF_WEEK_ORDINAL =          20 * 16;
    private static final int DAY_OF_MONTH_ORDINAL =         21 * 16;
    private static final int DAY_OF_YEAR_ORDINAL =          22 * 16;
    private static final int EPOCH_DAY_ORDINAL =            23 * 16;
    private static final int WEEK_OF_MONTH_ORDINAL =        24 * 16;
    private static final int WEEK_OF_WEEK_BASED_YEAR_ORDINAL = 25 * 16;
    private static final int WEEK_OF_YEAR_ORDINAL =         26 * 16;
    private static final int MONTH_OF_QUARTER_ORDINAL =     27 * 16;
    private static final int MONTH_OF_YEAR_ORDINAL =        28 * 16;
    private static final int EPOCH_MONTH_ORDINAL =          29 * 16;
    private static final int QUARTER_OF_YEAR_ORDINAL =      30 * 16;
    private static final int WEEK_BASED_YEAR_ORDINAL =      31 * 16;
    private static final int YEAR_ORDINAL =                 32 * 16;
    private static final int EPOCH_YEAR_ORDINAL =           33 * 16;

    //-----------------------------------------------------------------------
    /**
     * The rule for the nano-of-day field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the day.
     * The values run from 0 to 86,399,999,999,999.
     */
    public static final DateTimeRule NANO_OF_DAY = new ISODateTimeRule(NANO_OF_DAY_ORDINAL, "NanoOfDay", NANOS, DAYS, 0, 86399999999999L, 86399999999999L, null);
    /**
     * The rule for the nano-of-milli field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the millisecond.
     * The values run from 0 to 999,999.
     */
    public static final DateTimeRule NANO_OF_MILLI = new ISODateTimeRule(NANO_OF_MILLI_ORDINAL, "NanoOfMilli", NANOS, MILLIS, 0, 999999, 999999, NANO_OF_DAY);
    /**
     * The rule for the nano-of-second field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 999,999,999.
     */
    public static final DateTimeRule NANO_OF_SECOND = new ISODateTimeRule(NANO_OF_SECOND_ORDINAL, "NanoOfSecond", NANOS, SECONDS, 0, 999999999, 999999999, NANO_OF_DAY);
    /**
     * The rule for the nano-of-minute field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the minute.
     * The values run from 0 to 59,999,999,999.
     */
    public static final DateTimeRule NANO_OF_MINUTE = new ISODateTimeRule(NANO_OF_MINUTE_ORDINAL, "NanoOfMinute", NANOS, MINUTES, 0, 59999999999L, 59999999999L, NANO_OF_DAY);
    /**
     * The rule for the nano-of-hour field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the hour.
     * The values run from 0 to 3,599,999,999,999.
     */
    public static final DateTimeRule NANO_OF_HOUR = new ISODateTimeRule(NANO_OF_HOUR_ORDINAL, "NanoOfHour", NANOS, HOURS, 0, 3599999999999L, 3599999999999L, NANO_OF_DAY);

    /**
     * The rule for the milli-of-second field.
     * <p>
     * This field counts milliseconds sequentially from the start of the second.
     * The values run from 0 to 999.
     */
    public static final DateTimeRule MILLI_OF_SECOND = new ISODateTimeRule(MILLI_OF_SECOND_ORDINAL, "MilliOfSecond", MILLIS, SECONDS, 0, 999, 999, NANO_OF_DAY);
    /**
     * The rule for the milli-of-minute field.
     * <p>
     * This field counts milliseconds sequentially from the start of the minute.
     * The values run from 0 to 59,999.
     */
    public static final DateTimeRule MILLI_OF_MINUTE = new ISODateTimeRule(MILLI_OF_MINUTE_ORDINAL, "MilliOfMinute", MILLIS, MINUTES, 0, 59999, 59999, NANO_OF_DAY);
    /**
     * The rule for the milli-of-hour field.
     * <p>
     * This field counts milliseconds sequentially from the start of the hour.
     * The values run from 0 to 3,599,999.
     */
    public static final DateTimeRule MILLI_OF_HOUR = new ISODateTimeRule(MILLI_OF_HOUR_ORDINAL, "MilliOfHour", MILLIS, HOURS, 0, 3599999, 3599999, NANO_OF_DAY);
    /**
     * The rule for the milli-of-day field.
     * <p>
     * This field counts milliseconds sequentially from the start of the day.
     * The values run from 0 to 86,399,999.
     */
    public static final DateTimeRule MILLI_OF_DAY = new ISODateTimeRule(MILLI_OF_DAY_ORDINAL, "MilliOfDay", MILLIS, DAYS, 0, 86399999, 86399999, NANO_OF_DAY);

    /**
     * The rule for the second-of-minute field.
     * <p>
     * This field counts seconds sequentially from the start of the minute.
     * The values run from 0 to 59.
     */
    public static final DateTimeRule SECOND_OF_MINUTE = new ISODateTimeRule(SECOND_OF_MINUTE_ORDINAL, "SecondOfMinute", SECONDS, MINUTES, 0, 59, 59, NANO_OF_DAY);
    /**
     * The rule for the second-of-hour field.
     * <p>
     * This field counts seconds sequentially from the start of the hour.
     * The values run from 0 to 3,599.
     */
    public static final DateTimeRule SECOND_OF_HOUR = new ISODateTimeRule(SECOND_OF_HOUR_ORDINAL, "SecondOfHour", SECONDS, HOURS, 0, 3599, 3599, NANO_OF_DAY);
    /**
     * The rule for the second-of-day field.
     * <p>
     * This field counts seconds sequentially from the start of the day.
     * The values run from 0 to 86399.
     */
    public static final DateTimeRule SECOND_OF_DAY = new ISODateTimeRule(SECOND_OF_DAY_ORDINAL, "SecondOfDay", SECONDS, DAYS, 0, 86399, 86399, NANO_OF_DAY);
    /**
     * The rule for the epoch-second field.
     * <p>
     * This field counts seconds sequentially from 1970-01-01.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final DateTimeRule EPOCH_SECOND = new ISODateTimeRule(EPOCH_SECOND_ORDINAL, "EpochSecond", SECONDS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);

    /**
     * The rule for the minute-of-hour field.
     * <p>
     * This field counts minutes sequentially from the start of the hour.
     * The values run from 0 to 59.
     */
    public static final DateTimeRule MINUTE_OF_HOUR = new ISODateTimeRule(MINUTE_OF_HOUR_ORDINAL, "MinuteOfHour", MINUTES, HOURS, 0, 59, 59, NANO_OF_DAY);
    /**
     * The rule for the minute-of-day field.
     * <p>
     * This field counts minutes sequentially from the start of the day.
     * The values run from 0 to 1439.
     */
    public static final DateTimeRule MINUTE_OF_DAY = new ISODateTimeRule(MINUTE_OF_DAY_ORDINAL, "MinuteOfDay", MINUTES, DAYS, 0, 1439, 1439, NANO_OF_DAY);

    /**
     * The rule for the hour of AM/PM field from 0 to 11.
     * <p>
     * This field counts hours sequentially from the start of the half-day AM/PM.
     * The values run from 0 to 11.
     */
    public static final DateTimeRule HOUR_OF_AMPM = new ISODateTimeRule(HOUR_OF_AMPM_ORDINAL, "HourOfAmPm", HOURS, _12_HOURS, 0, 11, 11, NANO_OF_DAY);
    /**
     * The rule for the clock hour of AM/PM field from 1 to 12.
     * <p>
     * This field counts hours sequentially within the half-day AM/PM as normally seen on a clock or watch.
     * The values run from 1 to 12.
     */
    public static final DateTimeRule CLOCK_HOUR_OF_AMPM = new ISODateTimeRule(CLOCK_HOUR_OF_AMPM_ORDINAL, "ClockHourOfAmPm", HOURS, _12_HOURS, 1, 12, 12, HOUR_OF_AMPM);

    /**
     * The rule for the hour-of-day field.
     * <p>
     * This field counts hours sequentially from the start of the day.
     * The values run from 0 to 23.
     */
    public static final DateTimeRule HOUR_OF_DAY = new ISODateTimeRule(HOUR_OF_DAY_ORDINAL, "HourOfDay", HOURS, DAYS, 0, 23, 23, NANO_OF_DAY);
    /**
     * The rule for the clock hour of AM/PM field from 1 to 24.
     * <p>
     * This field counts hours sequentially within the day starting from 1.
     * The values run from 1 to 24.
     */
    public static final DateTimeRule CLOCK_HOUR_OF_DAY = new ISODateTimeRule(CLOCK_HOUR_OF_DAY_ORDINAL, "ClockHourOfDay", HOURS, DAYS, 1, 24, 24, HOUR_OF_DAY);
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
    public static final DateTimeRule AMPM_OF_DAY = new ISODateTimeRule(AMPM_OF_DAY_ORDINAL, "AmPmOfDay", _12_HOURS, DAYS, 0, 1, 1, NANO_OF_DAY);

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
    public static final DateTimeRule DAY_OF_WEEK = new ISODateTimeRule(DAY_OF_WEEK_ORDINAL, "DayOfWeek", DAYS, WEEKS, 1, 7, 7, null);
    /**
     * The rule for the day-of-month field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the month.
     * The first day of the month is 1 and the last is 28, 29, 30 or 31
     * depending on the month and whether it is a leap year.
     */
    public static final DateTimeRule DAY_OF_MONTH = new ISODateTimeRule(DAY_OF_MONTH_ORDINAL, "DayOfMonth", DAYS, MONTHS, 1, 31, 28, null);
    /**
     * The rule for the day-of-year field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the year.
     * The first day of the year is 1 and the last is 365, or 366 in a leap year.
     */
    public static final DateTimeRule DAY_OF_YEAR = new ISODateTimeRule(DAY_OF_YEAR_ORDINAL, "DayOfYear", DAYS, YEARS, 1, 366, 365, null);
    /**
     * The rule for the epoch-day field.
     * <p>
     * This field counts days sequentially from 1970-01-01.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final DateTimeRule EPOCH_DAY = new ISODateTimeRule(EPOCH_DAY_ORDINAL, "EpochDay", DAYS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);
    /**
     * The rule for the week-of-month field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * day of the month. The 1st to the 7th of a month is always week 1 while the
     * 8th to the 14th is always week 2 and so on.
     * <p>
     * This field can be used to create concepts such as 'the second Saturday'
     * of a month. To achieve this, setup a {@link DateTimeFields} instance
     * using this rule and the {@link #DAY_OF_WEEK day-of-week} rule.
     */
    public static final DateTimeRule WEEK_OF_MONTH = new ISODateTimeRule(WEEK_OF_MONTH_ORDINAL, "WeekOfMonth", WEEKS, MONTHS, 1, 5, 4, DAY_OF_MONTH);
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
    public static final DateTimeRule WEEK_OF_WEEK_BASED_YEAR = new ISODateTimeRule(WEEK_OF_WEEK_BASED_YEAR_ORDINAL, "WeekOfWeekBasedYear", WEEKS, WEEK_BASED_YEARS, 1, 53, 52, null);
    /**
     * The rule for the week-of-year field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first of January.
     * The 1st to the 7th of January is always week 1 while the 8th to the 14th is always week 2.
     */
    public static final DateTimeRule WEEK_OF_YEAR = new ISODateTimeRule(WEEK_OF_YEAR_ORDINAL, "WeekOfYear", WEEKS, YEARS, 1, 53, 53, DAY_OF_YEAR);

    /**
     * The rule for the epoch-month field.
     * <p>
     * This field counts months sequentially from 1970-01-01.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final DateTimeRule EPOCH_MONTH = new ISODateTimeRule(EPOCH_MONTH_ORDINAL, "EpochMonth", MONTHS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, null);
    /**
     * The rule for the month-of-quarter field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the quarter.
     * The first month of the quarter is 1 and the last is 3.
     * Each quarter lasts exactly three months.
     */
    public static final DateTimeRule MONTH_OF_QUARTER = new ISODateTimeRule(MONTH_OF_QUARTER_ORDINAL, "MonthOfQuarter", MONTHS, QUARTERS, 1, 3, 3, EPOCH_MONTH);
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
    public static final DateTimeRule MONTH_OF_YEAR = new ISODateTimeRule(MONTH_OF_YEAR_ORDINAL, "MonthOfYear", MONTHS, YEARS, 1, 12, 12, EPOCH_MONTH);
    /**
     * The rule for the quarter-of-year field in the ISO chronology.
     * <p>
     * This field counts quarters sequentially from the start of the year.
     * The first quarter of the year is 1 and the last is 4.
     * Each quarter lasts exactly three months.
     */
    public static final DateTimeRule QUARTER_OF_YEAR = new ISODateTimeRule(QUARTER_OF_YEAR_ORDINAL, "QuarterOfYear", QUARTERS, YEARS, 1, 4, 4, EPOCH_MONTH);
    /**
     * The rule for the epoch-year field.
     * <p>
     * This field counts years sequentially from 1970-01-01.
     * The values run from Long.MIN_VALUE to Long.MAX_VALUE.
     */
    public static final DateTimeRule EPOCH_YEAR = new ISODateTimeRule(EPOCH_YEAR_ORDINAL, "EpochYear", YEARS, null, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, EPOCH_MONTH);
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
    public static final DateTimeRule YEAR = new ISODateTimeRule(YEAR_ORDINAL, "Year", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR, EPOCH_YEAR);

    /**
     * The rule for the week-based-year field in the ISO chronology.
     * <p>
     * This field is the year that results from calculating weeks with the ISO-8601 algorithm.
     * See {@link #weekOfWeekBasedYearRule() week of week-based-year} for details.
     * <p>
     * The week-based-year will either be 52 or 53 weeks long, depending on the
     * result of the algorithm for a particular date.
     */
    public static final DateTimeRule WEEK_BASED_YEAR = new ISODateTimeRule(
            WEEK_BASED_YEAR_ORDINAL, "WeekBasedYear", WEEK_BASED_YEARS, null, MIN_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR, null);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final DateTimeRule[] RULE_CACHE = new DateTimeRule[] {
        NANO_OF_MILLI, NANO_OF_SECOND, NANO_OF_SECOND, NANO_OF_MINUTE, NANO_OF_DAY,
        MILLI_OF_SECOND, MILLI_OF_MINUTE, MILLI_OF_HOUR, MILLI_OF_DAY,
        SECOND_OF_MINUTE, SECOND_OF_HOUR, SECOND_OF_DAY, EPOCH_SECOND,
        MINUTE_OF_HOUR, MINUTE_OF_DAY,
        CLOCK_HOUR_OF_AMPM, HOUR_OF_AMPM, CLOCK_HOUR_OF_DAY, HOUR_OF_DAY, AMPM_OF_DAY,
        DAY_OF_WEEK, DAY_OF_MONTH, DAY_OF_YEAR, EPOCH_DAY,
        WEEK_OF_MONTH, WEEK_OF_WEEK_BASED_YEAR, WEEK_OF_YEAR,
        MONTH_OF_QUARTER, MONTH_OF_YEAR, EPOCH_MONTH,
        QUARTER_OF_YEAR,
        WEEK_BASED_YEAR,
        YEAR, EPOCH_YEAR,
    };

}
