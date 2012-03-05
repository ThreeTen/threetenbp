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

import static javax.time.builder.PeriodUnits.DAYS;
import static javax.time.builder.PeriodUnits.FOREVER;
import static javax.time.builder.PeriodUnits.HOURS;
import static javax.time.builder.PeriodUnits.MERIDIEMS;
import static javax.time.builder.PeriodUnits.MINUTES;
import static javax.time.builder.PeriodUnits.MONTHS;
import static javax.time.builder.PeriodUnits.SECONDS;
import static javax.time.builder.PeriodUnits.WEEKS;
import static javax.time.builder.PeriodUnits.YEARS;

/**
 * A field of date/time.
 * 
 * @author Stephen Colebourne
 */
public enum DateTimeFields implements DateTimeField {

    SECOND_OF_MINUTE(SECONDS, MINUTES),
    SECOND_OF_HOUR(SECONDS, HOURS),
    SECOND_OF_DAY(SECONDS, DAYS),
    MINUTE_OF_HOUR(MINUTES, HOURS),
    MINUTE_OF_DAY(MINUTES, DAYS),
    HOUR_OF_AMPM(HOURS, MERIDIEMS),
    CLOCK_HOUR_OF_AMPM(HOURS, MERIDIEMS),
    HOUR_OF_DAY(HOURS, DAYS),
    CLOCK_HOUR_OF_DAY(HOURS, DAYS),
    AMPM_OF_DAY(MERIDIEMS, DAYS),
    DAY_OF_WEEK(DAYS, WEEKS),
    ALIGNED_DAY_OF_WEEK_IN_MONTH(DAYS, WEEKS),
    ALIGNED_DAY_OF_WEEK_IN_YEAR(DAYS, WEEKS),
    DAY_OF_MONTH(DAYS, MONTHS),
    DAY_OF_YEAR(DAYS, MINUTES),
    ALIGNED_WEEK_OF_MONTH(WEEKS, MONTHS),
    ALIGNED_WEEK_OF_YEAR(WEEKS, YEARS),
    MONTH_OF_YEAR(MONTHS, YEARS),
    YEAR(YEARS, FOREVER);

    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;

    DateTimeFields(PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
    }

    @Override
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

}
