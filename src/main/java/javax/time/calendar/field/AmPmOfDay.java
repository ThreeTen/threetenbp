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
package javax.time.calendar.field;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjustor;
import javax.time.calendar.TimeMatcher;

/**
 * A representation of the half-day AM/PM value in the ISO-8601 calendar system.
 * <p>
 * AmPmOfDay is an enum that represents the half-day concepts of AM and PM.
 * AM is defined as from 00:00 to 11:59, while PM is defined from 12:00 to 23:59.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a AmPmOfDay
 * instance. Use getValue() instead.</b>
 * <p>
 * AmPmOfDay is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum AmPmOfDay
        implements Calendrical, TimeAdjustor, TimeMatcher {

    /**
     * The singleton instance for the morning, AM - ante meridiem.
     */
    AM(0),
    /**
     * The singleton instance for the afternoon, PM - post meridiem.
     */
    PM(1),
    ;

    /**
     * The AM/PM being represented.
     */
    private final int amPmOfDay;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the AM/PM field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the AM/PM rule, never null
     */
    public static DateTimeFieldRule rule() {
        return ISOChronology.INSTANCE.amPmOfDay();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>AmPmOfDay</code>.
     *
     * @param amPmOfDay  the AM/PM value to represent, from 0 (AM) to 1 (PM)
     * @return the singleton AmPmOfDay instance
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static AmPmOfDay amPmOfDay(int amPmOfDay) {
        switch (amPmOfDay) {
            case 0:
                return AM;
            case 1:
                return PM;
            default:
                throw new IllegalCalendarFieldValueException(rule(), amPmOfDay, 0, 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified AM/PM value.
     *
     * @param amPmOfDay  the AM/PM value to represent
     */
    private AmPmOfDay(int amPmOfDay) {
        this.amPmOfDay = amPmOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the AM/PM value.
     *
     * @return the AM/PM value, from 0 (AM) to 1 (PM)
     */
    public int getValue() {
        return amPmOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(rule(), getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance representing AM (ante-meridiem).
     *
     * @return true is this instance represents AM
     */
    public boolean isAm() {
        return (this == AM);
    }

    /**
     * Is this instance representing PM (post-meridiem).
     *
     * @return true is this instance represents PM
     */
    public boolean isPm() {
        return (this == PM);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a time to have the the am/pm value represented by this object,
     * returning a new time.
     * <p>
     * Only the AM/PM value is adjusted. The other date and time fields are
     * unaffected. Changing from AM to PM will effectively add 12 hours,
     * while changing from PM to AM will effectively subtract 12 hours.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        if (this == time.getHourOfDay().getAmPm()) {
            return time;
        }
        return LocalTime.time(
                HourOfDay.hourOfDay(this, time.getHourOfDay().getHourOfAmPm()),
                time.getMinuteOfHour(),
                time.getSecondOfMinute(),
                time.getNanoOfSecond());
    }

    /**
     * Checks if the input time has the same AM/PM value that is represented
     * by this object.
     *
     * @param time  the time to match, not null
     * @return true if the time matches, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        return this == time.getHourOfDay().getAmPm();
    }

}
