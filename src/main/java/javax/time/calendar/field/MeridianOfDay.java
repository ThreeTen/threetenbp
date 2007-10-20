/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.CalendricalState;
import javax.time.calendar.TimeFieldRule;

/**
 * A time field representing a meridian of day.
 * <p>
 * MeridianOfDay is an immutable time field that can only store a meridian of day.
 * It is a type-safe way of representing a meridian of day in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a MeridianOfDay
 * instance. Use getMeridianOfDay() instead.</b>
 * <p>
 * MeridianOfDay is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum MeridianOfDay implements Calendrical {

    /**
     * The singleton instance for the morning (ante meridian).
     */
    AM(0),
    /**
     * The singleton instance for the afternoon (post meridian).
     */
    PM(1),
    ;
    /**
     * The rule implementation that defines how the meridian of day field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The meridian of day being represented.
     */
    private final int meridianOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MeridianOfDay</code>.
     *
     * @param meridianOfDay  the meridian of day to represent
     * @return the existing MeridianOfDay
     */
    public static MeridianOfDay meridianOfDay(int meridianOfDay) {
        switch (meridianOfDay) {
            case 0:
                return AM;
            case 1:
                return PM;
            default:
                throw new IllegalArgumentException("MeridianOfDay cannot have the value " + meridianOfDay);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified meridian of day.
     *
     * @param meridianOfDay  the meridian of day to represent
     */
    private MeridianOfDay(int meridianOfDay) {
        this.meridianOfDay = meridianOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the meridian of day value.
     *
     * @return the meridian of day
     */
    public int getMeridianOfDay() {
        return meridianOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * MeridianOfDay instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next meridian of day wrapping so that the next meridian of day
     * is always returned.
     *
     * @return the next meridian of day, never null
     */
    public MeridianOfDay next() {
        return values()[(ordinal() + 1) % 2];
    }

    /**
     * Gets the previous meridian of day wrapping so that the previous meridian of day
     * is always returned.
     *
     * @return the previous meridian of day, never null
     */
    public MeridianOfDay previous() {
        return values()[(ordinal() + 2 - 1) % 2];
    }

    //-----------------------------------------------------------------------
    /**
     * Is this meridian of day instance greater than the specified meridian of day.
     *
     * @param otherMeridianOfDay  the other meridian of day instance, not null
     * @return true if this meridian of day is greater
     * @throws NullPointerException if otherMeridianOfDay is null
     */
    public boolean isGreaterThan(MeridianOfDay otherMeridianOfDay) {
        return compareTo(otherMeridianOfDay) > 0;
    }

    /**
     * Is this meridian of day instance less than the specified meridian of day.
     *
     * @param otherMeridianOfDay  the other meridian of day instance, not null
     * @return true if this meridian of day is less
     * @throws NullPointerException if otherMeridianOfDay is null
     */
    public boolean isLessThan(MeridianOfDay otherMeridianOfDay) {
        return compareTo(otherMeridianOfDay) < 0;
    }

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
     * Implementation of the rules for the meridian of day field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("MeridianOfDay", null, null, 0, 1);
        }
    }

}
