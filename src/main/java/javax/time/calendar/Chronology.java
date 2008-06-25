/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * A calendar system, consisting of rules controlling the passage of human-scale
 * time.
 * <p>
 * Calendar systems describe a set of fields that can be used to describe time
 * in a human-scale. Typical fields include year, month and day of month.
 * <p>
 * This abstract base class provides a common mechanism to access the standard
 * fields which are supported in the vast majority of calendar systems.
 * Subclasses will provide the full set of fields for that calendar system.
 * <p>
 * The default chronology is {@link ISOChronology ISO8601} which is the
 * <i>de facto</i> world calendar today.
 * <p>
 * Chronology is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 * Wherever possible subclasses should be singletons with no public constructor.
 * It is recommended that subclasses implement <code>Serializable</code>
 *
 * @author Stephen Colebourne
 */
public abstract class Chronology {

    /**
     * Restrictive constructor.
     */
    protected Chronology() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     * <p>
     * The name should not have the suffix 'Chronology'. For example, the
     * name of {@link ISOChronology} is 'ISO'.
     *
     * @return the name of the chronology, never null
     */
    public abstract String getName();

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the chronology.
     * <p>
     * The meaning of a 'year' will vary by chronology and will not necessarily
     * align with the years of the ISO chronology.
     *
     * @return the rule for the year field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule year();

    /**
     * Gets the rule for the month of year field.
     * <p>
     * The meaning of a 'month of year' will vary by chronology and will not
     * necessarily align with the months of the ISO chronology.
     *
     * @return the rule for the month of year field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule monthOfYear();

    /**
     * Gets the rule for the day of month field.
     * <p>
     * The meaning of a 'month of year' will vary by chronology and will not
     * necessarily align with the days of the ISO chronology.
     *
     * @return the rule for the day of month field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule dayOfMonth();

    /**
     * Gets the rule for the day of year field.
     * <p>
     * The meaning of a 'day of year' will vary by chronology and will not
     * necessarily align with the days of the ISO chronology.
     *
     * @return the rule for the day of year field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule dayOfYear();

    /**
     * Gets the rule for the day of week field.
     * <p>
     * The meaning of a 'day of week' will vary by chronology and will not
     * necessarily align with the days of the ISO chronology.
     *
     * @return the rule for the day of week field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule dayOfWeek();

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the hour of day field.
     * <p>
     * The meaning of a 'hour of day' will vary by chronology and will not
     * necessarily align with the hours of the ISO chronology.
     *
     * @return the rule for the hour of day field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule hourOfDay();

    /**
     * Gets the rule for the minute of hour field.
     * <p>
     * The meaning of a 'minute of hour' will vary by chronology and will not
     * necessarily align with the minutes of the ISO chronology.
     *
     * @return the rule for the minute of hour field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule minuteOfHour();

    /**
     * Gets the rule for the second of minute field.
     * <p>
     * The meaning of a 'second of minute' will vary by chronology and will not
     * necessarily align with the seconds of the ISO chronology.
     *
     * @return the rule for the second of minute field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule secondOfMinute();

    /**
     * Gets the rule for the nano of second field.
     * <p>
     * The meaning of a 'nano of second' will vary by chronology and will not
     * necessarily align with the nanos of the ISO chronology.
     *
     * @return the rule for the nano of second field, never null
     * @throws UnsupportedOperationException if the chronology does not support this field
     */
    public abstract DateTimeFieldRule nanoOfSecond();

    //-----------------------------------------------------------------------
    /**
     * Returns a textual description of the chronology.
     *
     * @return a string form for debugging, never null
     */
    @Override
    public String toString() {
        return getName();
    }

}
