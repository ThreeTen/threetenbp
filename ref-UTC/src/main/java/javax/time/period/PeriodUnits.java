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
package javax.time.period;

import javax.time.period.PeriodUnit.Standard;

/**
 * Utility class providing the standard set of period units.
 * <p>
 * PeriodUnits is non-instantiable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class PeriodUnits {
    // should be declared in PeriodUnit, but that causes a circular issue

    /**
     * Standard period unit for centuries.
     * This is a derived unit equal to 100 years.
     */
    public static final PeriodUnit CENTURIES = Standard.CENTURIES;
    /**
     * Standard period unit for decades.
     * This is a derived unit equal to 10 years.
     */
    public static final PeriodUnit DECADES = Standard.DECADES;
    /**
     * Standard period unit for years.
     * This is a standard unit equal to 12 months.
     */
    public static final PeriodUnit YEARS = Standard.YEARS;
    /**
     * Standard period unit for weekyears.
     * This is a basic unit.
     */
    public static final PeriodUnit WEEKYEARS = Standard.WEEKYEARS;
    /**
     * Standard period unit for quarters.
     * This is a derived unit equal to 3 months.
     */
    public static final PeriodUnit QUARTERS = Standard.QUARTERS;
    /**
     * Standard period unit for months.
     * This is a basic unit.
     */
    public static final PeriodUnit MONTHS = Standard.MONTHS;
    /**
     * Standard period unit for weeks.
     * This is a derived unit equal to 7 days.
     */
    public static final PeriodUnit WEEKS = Standard.WEEKS;
    /**
     * Standard period unit for days.
     * This is a basic unit.
     */
    public static final PeriodUnit DAYS = Standard.DAYS;
    /**
     * Standard period unit for twelve hours, useful for AM/PM.
     * This is a derived unit equal to 12 hours.
     */
    public static final PeriodUnit TWELVE_HOURS = Standard.TWELVE_HOURS;
    /**
     * Standard period unit for hours.
     * This is a standard unit equal to 60 minutes.
     */
    public static final PeriodUnit HOURS = Standard.HOURS;
    /**
     * Standard period unit for minutes.
     * This is a standard unit equal to 60 seconds.
     */
    public static final PeriodUnit MINUTES = Standard.MINUTES;
    /**
     * Standard period unit for seconds.
     * This is a standard unit equal to 1000000000 nanos.
     */
    public static final PeriodUnit SECONDS = Standard.SECONDS;
    /**
     * Standard period unit for milliseconds.
     * This is a standard unit equal to 1000000 nanos.
     */
    public static final PeriodUnit MILLIS = Standard.MILLIS;
    /**
     * Standard period unit for microseconds.
     * This is a standard unit equal to 1000 nanos.
     */
    public static final PeriodUnit MICROS = Standard.MICROS;
    /**
     * Standard period unit for nanoseconds.
     * This is a basic unit.
     */
    public static final PeriodUnit NANOS = Standard.NANOS;

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     */
    private PeriodUnits() {
        super();
    }

}
