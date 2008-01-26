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
import javax.time.calendar.DateAdjustor;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.ReadableDate;
import javax.time.calendar.TimeFieldRule;

/**
 * A representation of a era in the ISO-8601 calendar system.
 * <p>
 * Era is an immutable time field that can only store a era.
 * It is a type-safe way of representing a era in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a Era
 * instance. Use getValue() instead.</b>
 * <p>
 * Era is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum Era
        implements Calendrical, DateAdjustor, DateMatcher {

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
     * Obtains an instance of <code>Era</code> from a value.
     * <p>
     * A day of week object represents one of the 2 eras. These are numbered
     * as 0 (BC/BCE) and 1 (AD/CE).
     * <p>
     * Era is an enum, thus each instance is a singleton.
     * As a result, Era instances can be compared using ==.
     *
     * @param era  the era to represent, from 0 to 1
     * @return the Era singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static Era era(int era) {
        switch (era) {
            case 0:
                return BC;
            case 1:
                return AD;
            default:
                throw new IllegalCalendarFieldValueException("Era", era, 0, 1);
        }
    }

    /**
     * Obtains an instance of <code>Era</code> from a date provider.
     * <p>
     * This can be used extract an era object directly from any implementation
     * of ReadableDate, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the Era singleton, never null
     */
    public static Era era(ReadableDate dateProvider) {
        return dateProvider.toLocalDate().getYear().getEra();
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
    public int getValue() {
        return era;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * Era instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the era represented by this object, returning a new date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        if (this == date.getYear().getEra()) {
            return date;
        }
        Year newYear = Year.year(this, date.getYear().getYearOfEra());
        return LocalDate.date(newYear, date.getMonthOfYear(), date.getDayOfMonth());
    }

    /**
     * Checks if this era matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return date.getYear().getEra() == this;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance representing BC/BCE.
     *
     * @return true is this instance represents BC/BCE
     */
    public boolean isBC() {
        return (this == BC);
    }

    /**
     * Is this instance representing AD/CE.
     *
     * @return true is this instance represents AD/CE
     */
    public boolean isAD() {
        return (this == AD);
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
