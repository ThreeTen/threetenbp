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
 * A time field representing a era.
 * <p>
 * Era is an immutable time field that can only store a era.
 * It is a type-safe way of representing a era in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a Era
 * instance. Use getEra() instead.</b>
 * <p>
 * Era is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum Era implements Calendrical {

    /**
     * The singleton instance for the last Era, BC/BCE.
     */
    BC(0),
    /**
     * The singleton instance for the current Era, AD/CE.
     */
    AD(1),
    ;
    /**
     * The rule implementation that defines how the era field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The era being represented.
     */
    private final int era;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Era</code>.
     *
     * @param era  the era to represent
     * @return the existing Era
     */
    public static Era era(int era) {
        switch (era) {
            case 0:
                return BC;
            case 1:
                return AD;
            default:
                throw new IllegalArgumentException("Era cannot have the value " + era);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified era.
     *
     * @param era  the era to represent
     */
    private Era(int era) {
        this.era = era;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era value.
     *
     * @return the era
     */
    public int getEra() {
        return era;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * Era instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next era wrapping so that the next era
     * is always returned.
     *
     * @return the next era, never null
     */
    public Era next() {
        return values()[(ordinal() + 1) % 2];
    }

    /**
     * Gets the previous era wrapping so that the previous era
     * is always returned.
     *
     * @return the previous era, never null
     */
    public Era previous() {
        return values()[(ordinal() + 2 - 1) % 2];
    }

    //-----------------------------------------------------------------------
    /**
     * Is this era instance greater than the specified era.
     *
     * @param otherEra  the other era instance, not null
     * @return true if this era is greater
     * @throws NullPointerException if otherEra is null
     */
    public boolean isGreaterThan(Era otherEra) {
        return compareTo(otherEra) > 0;
    }

    /**
     * Is this era instance less than the specified era.
     *
     * @param otherEra  the other era instance, not null
     * @return true if this era is less
     * @throws NullPointerException if otherEra is null
     */
    public boolean isLessThan(Era otherEra) {
        return compareTo(otherEra) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the era field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("Era", null, null, 0, 1);
        }
    }

}
