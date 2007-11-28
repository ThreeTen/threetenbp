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
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.TimeFieldRule;

/**
 * A calendrical representation of a quarter of year.
 * <p>
 * QuarterOfYear is an immutable time field that can only store a quarter of year.
 * It is a type-safe way of representing a quarter of year in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a QuarterOfYear
 * instance. Use getValue() instead.</b>
 * <p>
 * QuarterOfYear is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum QuarterOfYear implements Calendrical {

    /**
     * The singleton instance for the first quarter of year, from January to March.
     */
    Q1(1),
    /**
     * The singleton instance for the second quarter of year, from April to June.
     */
    Q2(2),
    /**
     * The singleton instance for the third quarter of year, from July to September.
     */
    Q3(3),
    /**
     * The singleton instance for the fourth quarter of year, from October to December.
     */
    Q4(4),
    ;
    /**
     * The rule implementation that defines how the quarter of year field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The quarter of year being represented.
     */
    private final int quarterOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>QuarterOfYear</code>.
     *
     * @param quarterOfYear  the quarter of year to represent
     * @return the existing QuarterOfYear
     * @throws IllegalCalendarFieldValueException if the quarter of year is invalid
     */
    public static QuarterOfYear quarterOfYear(int quarterOfYear) {
        switch (quarterOfYear) {
            case 1:
                return Q1;
            case 2:
                return Q2;
            case 3:
                return Q3;
            case 4:
                return Q4;
            default:
                throw new IllegalCalendarFieldValueException("QuarterOfYear cannot have the value " + quarterOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified quarter of year.
     *
     * @param quarterOfYear  the quarter of year to represent
     */
    private QuarterOfYear(int quarterOfYear) {
        this.quarterOfYear = quarterOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the quarter of year value.
     *
     * @return the quarter of year
     */
    public int getValue() {
        return quarterOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * QuarterOfYear instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next quarter of year wrapping so that the next quarter of year
     * is always returned.
     *
     * @return the next quarter of year, never null
     */
    public QuarterOfYear next() {
        return values()[(ordinal() + 1) % 4];
    }

    /**
     * Gets the previous quarter of year wrapping so that the previous quarter of year
     * is always returned.
     *
     * @return the previous quarter of year, never null
     */
    public QuarterOfYear previous() {
        return values()[(ordinal() + 4 - 1) % 4];
    }

    //-----------------------------------------------------------------------
    /**
     * Is this quarter of year instance greater than the specified quarter of year.
     *
     * @param otherQuarterOfYear  the other quarter of year instance, not null
     * @return true if this quarter of year is greater
     * @throws NullPointerException if otherQuarterOfYear is null
     */
    public boolean isGreaterThan(QuarterOfYear otherQuarterOfYear) {
        return compareTo(otherQuarterOfYear) > 0;
    }

    /**
     * Is this quarter of year instance less than the specified quarter of year.
     *
     * @param otherQuarterOfYear  the other quarter of year instance, not null
     * @return true if this quarter of year is less
     * @throws NullPointerException if otherQuarterOfYear is null
     */
    public boolean isLessThan(QuarterOfYear otherQuarterOfYear) {
        return compareTo(otherQuarterOfYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the quarter of year field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("QuarterOfYear", null, null, 1, 4);
        }
    }

}
