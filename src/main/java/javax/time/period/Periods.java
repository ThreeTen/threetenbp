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


/**
 * Utility class providing constants and factories for working with periods.
 * <p>
 * Periods is non-instantiable.
 *
 * @author Stephen Colebourne
 */
public final class Periods {

    /**
     * Period range of forever.
     */
    public static final PeriodUnit FOREVER = PeriodUnit.createUnit("Forever");
    /**
     * Period unit for years.
     */
    public static final PeriodUnit YEARS = PeriodUnit.createUnit("Years");
    /**
     * Period unit for quarters.
     */
    public static final PeriodUnit QUARTERS = PeriodUnit.createUnit("Quarters");
    /**
     * Period unit for months.
     */
    public static final PeriodUnit MONTHS = PeriodUnit.createUnit("Months");
    /**
     * Period unit for weeks.
     */
    public static final PeriodUnit WEEKS = PeriodUnit.createUnit("Weeks");
    /**
     * Period unit for days.
     */
    public static final PeriodUnit DAYS = PeriodUnit.createUnit("Days");
    /**
     * Period unit for half days.
     */
    public static final PeriodUnit HALF_DAYS = PeriodUnit.createUnit("HalfDays");
    /**
     * Period unit for hours.
     */
    public static final PeriodUnit HOURS = PeriodUnit.createUnit("Hours");
    /**
     * Period unit for minutes.
     */
    public static final PeriodUnit MINUTES = PeriodUnit.createUnit("Minutes");
    /**
     * Period unit for seconds.
     */
    public static final PeriodUnit SECONDS = PeriodUnit.createUnit("Seconds");
    /**
     * Period unit for milliseconds.
     */
    public static final PeriodUnit MILLIS = PeriodUnit.createUnit("Millis");
    /**
     * Period unit for nanoseconds.
     */
    public static final PeriodUnit NANOS = PeriodUnit.createUnit("Nanos");

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private Periods() {
    }

}
