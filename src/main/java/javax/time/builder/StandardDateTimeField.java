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
package javax.time.builder;

import static javax.time.builder.StandardPeriodUnit.DAYS;
import static javax.time.builder.StandardPeriodUnit.ERAS;
import static javax.time.builder.StandardPeriodUnit.FOREVER;
import static javax.time.builder.StandardPeriodUnit.HOURS;
import static javax.time.builder.StandardPeriodUnit.MERIDIEMS;
import static javax.time.builder.StandardPeriodUnit.MICROS;
import static javax.time.builder.StandardPeriodUnit.MILLIS;
import static javax.time.builder.StandardPeriodUnit.MINUTES;
import static javax.time.builder.StandardPeriodUnit.MONTHS;
import static javax.time.builder.StandardPeriodUnit.NANOS;
import static javax.time.builder.StandardPeriodUnit.SECONDS;
import static javax.time.builder.StandardPeriodUnit.WEEKS;
import static javax.time.builder.StandardPeriodUnit.YEARS;

/**
 * A field of date/time.
 * 
 * @author Stephen Colebourne
 */
public enum StandardDateTimeField implements DateTimeField {

    NANO_OF_SECOND("NanoOfSecond", NANOS, SECONDS),
    NANO_OF_DAY("NanoOfDay", NANOS, DAYS),
    MICRO_OF_SECOND("MicroOfSecond", MICROS, SECONDS),
    MICRO_OF_DAY("MicroOfDay", MICROS, DAYS),
    MILLI_OF_SECOND("MilliOfSecond", MILLIS, SECONDS),
    MILLI_OF_DAY("MilliOfDay", MILLIS, DAYS),
    SECOND_OF_MINUTE("SecondOfMinute", SECONDS, MINUTES),
    SECOND_OF_HOUR("SecondOfHour", SECONDS, HOURS),
    SECOND_OF_DAY("SecondOfDay", SECONDS, DAYS),
    MINUTE_OF_HOUR("MinuteOfHour", MINUTES, HOURS),
    MINUTE_OF_DAY("MinuteOfDay", MINUTES, DAYS),
    HOUR_OF_AMPM("HourOfAmPm", HOURS, MERIDIEMS),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", HOURS, MERIDIEMS),
    HOUR_OF_DAY("HourOfDay", HOURS, DAYS),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", HOURS, DAYS),
    AMPM_OF_DAY("AmPmOfDay", MERIDIEMS, DAYS),
    DAY_OF_WEEK("DayOfWeek", DAYS, WEEKS),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", DAYS, WEEKS),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", DAYS, WEEKS),
    DAY_OF_MONTH("DayOfMonth", DAYS, MONTHS),
    DAY_OF_YEAR("DayOfYear", DAYS, MINUTES),
    EPOCH_DAY("EpochDay", DAYS, FOREVER),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", WEEKS, MONTHS),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", WEEKS, YEARS),
    MONTH_OF_YEAR("MonthOfYear", MONTHS, YEARS),
    EPOCH_MONTH("EpochMonth", MONTHS, FOREVER),
    YEAR_OF_ERA("YearOfEra", YEARS, ERAS),
    YEAR("Year", YEARS, FOREVER),
    ERA("Era", ERAS, FOREVER);

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;

    private StandardDateTimeField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public Chrono getRules(Chrono baseChronology) {
        return baseChronology;
    }

    public boolean isDateField() {
        return compareTo(DAY_OF_WEEK) >= 0;
    }

    public boolean isTimeField() {
        return compareTo(DAY_OF_WEEK) < 0;
    }

}
