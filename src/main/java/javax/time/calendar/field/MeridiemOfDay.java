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

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjustor;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.TimeMatcher;

/**
 * A representation of a meridiem of day in the ISO-8601 calendar system.
 * <p>
 * MeridiemOfDay is an immutable time field that can only store a meridiem of day.
 * It is a type-safe way of representing a meridiem of day in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a MeridiemOfDay
 * instance. Use getValue() instead.</b>
 * <p>
 * MeridiemOfDay is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum MeridiemOfDay
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
     * The rule implementation that defines how the meridiem of day field operates.
     */
    public static final DateTimeFieldRule RULE = Rule.INSTANCE;

    /**
     * The meridiem of day being represented.
     */
    private final int meridiemOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MeridiemOfDay</code>.
     *
     * @param meridiemOfDay  the meridiem of day to represent, from 0 (AM) to 1 (PM)
     * @return the existing MeridiemOfDay
     * @throws IllegalCalendarFieldValueException if the meridiem of day is invalid
     */
    public static MeridiemOfDay meridiemOfDay(int meridiemOfDay) {
        switch (meridiemOfDay) {
            case 0:
                return AM;
            case 1:
                return PM;
            default:
                throw new IllegalCalendarFieldValueException(RULE, meridiemOfDay, 0, 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified meridiem of day.
     *
     * @param meridiemOfDay  the meridiem of day to represent
     */
    private MeridiemOfDay(int meridiemOfDay) {
        this.meridiemOfDay = meridiemOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the meridiem of day value.
     *
     * @return the meridiem of day, from 0 (AM) to 1 (PM)
     */
    public int getValue() {
        return meridiemOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(RULE, getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next meridiem of day wrapping so that the next meridiem of day
     * is always returned.
     *
     * @return the next meridiem of day, never null
     */
    public MeridiemOfDay next() {
        return values()[(ordinal() + 1) % 2];
    }

    /**
     * Gets the previous meridiem of day wrapping so that the previous meridiem of day
     * is always returned.
     *
     * @return the previous meridiem of day, never null
     */
    public MeridiemOfDay previous() {
        return values()[(ordinal() + 2 - 1) % 2];
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
     * Adjusts a time to have the the meridem of day represented by this object,
     * returning a new time.
     * <p>
     * Only the meridiem of day field is adjusted in the result. The other time
     * fields are unaffected.
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
     * Checks if the input time has the same meridiem of day that is represented
     * by this object.
     *
     * @param time  the time to match, not null
     * @return true if the time matches, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        return this == time.getHourOfDay().getAmPm();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the meridiem of day field.
     */
    private static class Rule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new Rule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private Rule() {
            super("MeridiemOfDay", null, null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getTime() != null ? dateTime.getTime().getHourOfDay().getAmPm().getValue() : null;
        }
    }
}
