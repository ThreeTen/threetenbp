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
import javax.time.duration.Durational;

/**
 * A time field representing a day of week.
 * <p>
 * DayOfWeek is an immutable time field that can only store a day of week.
 * It is a type-safe way of representing a day of week in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a DayOfWeek
 * instance. Use getDayOfWeek() instead.</b>
 * <p>
 * DayOfWeek is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum DayOfWeek implements Calendrical {

    /**
     * The singleton instance for the day of week of Monday.
     */
    MONDAY(1),
    /**
     * The singleton instance for the day of week of Tuesday.
     */
    TUESDAY(2),
    /**
     * The singleton instance for the day of week of Wednesday.
     */
    WEDNESDAY(3),
    /**
     * The singleton instance for the day of week of Thursday.
     */
    THURSDAY(4),
    /**
     * The singleton instance for the day of week of Friday.
     */
    FRIDAY(5),
    /**
     * The singleton instance for the day of week of Saturday.
     */
    SATURDAY(6),
    /**
     * The singleton instance for the day of week of Sunday.
     */
    SUNDAY(7),
    ;
    /**
     * The rule implementation that defines how the day of week field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The day of week being represented.
     */
    private final int dayOfWeek;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DayOfWeek</code>.
     *
     * @param dayOfWeek  the day of week to represent
     * @return the existing DayOfWeek
     */
    public static DayOfWeek dayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            case 7:
                return SUNDAY;
            default:
                throw new IllegalArgumentException("DayOfWeek cannot have the value " + dayOfWeek);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of week.
     *
     * @param dayOfWeek  the day of week to represent
     */
    private DayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of week value.
     *
     * @return the day of week
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * DayOfWeek instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next day of week wrapping so that the next day of week
     * is always returned.
     *
     * @return the next day of week, never null
     */
    public DayOfWeek next() {
        return values()[(ordinal() + 1) % 7];
    }

    /**
     * Gets the previous day of week wrapping so that the previous day of week
     * is always returned.
     *
     * @return the previous day of week, never null
     */
    public DayOfWeek previous() {
        return values()[(ordinal() + 7 - 1) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Is this day of week instance greater than the specified day of week.
     *
     * @param otherDayOfWeek  the other day of week instance, not null
     * @return true if this day of week is greater
     * @throws NullPointerException if otherDayOfWeek is null
     */
    public boolean isGreaterThan(DayOfWeek otherDayOfWeek) {
        return compareTo(otherDayOfWeek) > 0;
    }

    /**
     * Is this day of week instance less than the specified day of week.
     *
     * @param otherDayOfWeek  the other day of week instance, not null
     * @return true if this day of week is less
     * @throws NullPointerException if otherDayOfWeek is null
     */
    public boolean isLessThan(DayOfWeek otherDayOfWeek) {
        return compareTo(otherDayOfWeek) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the DayOfWeek which is the specified number of days after
     * this DayOfWeek.
     * <p>
     * The calculation wraps around the end of the week from Sunday to Monday.
     * The days to add may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return the resulting DayOfWeek, never null
     */
    public DayOfWeek plusDays(int days) {
        return values()[(ordinal() + (days % 7)) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the DayOfWeek which is the specified number of days before
     * this DayOfWeek.
     * <p>
     * The calculation wraps around the start of the week from Monday to Sunday.
     * The days to subtract may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return the resulting DayOfWeek, never null
     */
    public DayOfWeek minusDays(int days) {
        return values()[(ordinal() + (days % 7)) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the day of week field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("DayOfWeek", null, null, 1, 7);
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(Durational epochDuration) {
            return ((super.getValue(epochDuration) + 3) % 7) + 1;
        }
    }

}
