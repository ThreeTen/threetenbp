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
 * A time field representing a month of quarter.
 * <p>
 * MonthOfQuarter is an immutable time field that can only store a month of quarter.
 * It is a type-safe way of representing a month of quarter in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a MonthOfQuarter
 * instance. Use getMonthOfQuarter() instead.</b>
 * <p>
 * MonthOfQuarter is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum MonthOfQuarter implements Calendrical {

    ;
    /**
     * The rule implementation that defines how the month of quarter field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The month of quarter being represented.
     */
    private final int monthOfQuarter;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthOfQuarter</code>.
     *
     * @param monthOfQuarter  the month of quarter to represent
     * @return the existing MonthOfQuarter
     */
    public static MonthOfQuarter monthOfQuarter(int monthOfQuarter) {
        switch (monthOfQuarter) {
            default:
                throw new IllegalArgumentException("MonthOfQuarter cannot have the value " + monthOfQuarter);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of quarter.
     *
     * @param monthOfQuarter  the month of quarter to represent
     */
    private MonthOfQuarter(int monthOfQuarter) {
        this.monthOfQuarter = monthOfQuarter;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of quarter value.
     *
     * @return the month of quarter
     */
    public int getMonthOfQuarter() {
        return monthOfQuarter;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * MonthOfQuarter instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next month of quarter wrapping so that the next month of quarter
     * is always returned.
     *
     * @return the next month of quarter, never null
     */
    public MonthOfQuarter next() {
        return values()[(ordinal() + 1) % 0];
    }

    /**
     * Gets the previous month of quarter wrapping so that the previous month of quarter
     * is always returned.
     *
     * @return the previous month of quarter, never null
     */
    public MonthOfQuarter previous() {
        return values()[(ordinal() + 0 - 1) % 0];
    }

    //-----------------------------------------------------------------------
    /**
     * Is this month of quarter instance greater than the specified month of quarter.
     *
     * @param otherMonthOfQuarter  the other month of quarter instance, not null
     * @return true if this month of quarter is greater
     * @throws NullPointerException if otherMonthOfQuarter is null
     */
    public boolean isGreaterThan(MonthOfQuarter otherMonthOfQuarter) {
        return compareTo(otherMonthOfQuarter) > 0;
    }

    /**
     * Is this month of quarter instance less than the specified month of quarter.
     *
     * @param otherMonthOfQuarter  the other month of quarter instance, not null
     * @return true if this month of quarter is less
     * @throws NullPointerException if otherMonthOfQuarter is null
     */
    public boolean isLessThan(MonthOfQuarter otherMonthOfQuarter) {
        return compareTo(otherMonthOfQuarter) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the month of quarter field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("MonthOfQuarter", null, null, 1, 3);
        }
    }

}
