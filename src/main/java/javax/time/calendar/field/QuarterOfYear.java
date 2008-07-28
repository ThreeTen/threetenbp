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
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;

/**
 * A representation of a quarter of year in the ISO-8601 calendar system.
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
public enum QuarterOfYear implements CalendricalProvider, DateMatcher {

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

    /**
     * The quarter of year being represented.
     */
    private final int quarterOfYear;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the quarter of year field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the quarter of year rule, never null
     */
    public static DateTimeFieldRule rule() {
        return ISOChronology.quarterOfYearRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>QuarterOfYear</code> from a value.
     * <p>
     * A quarter of year object represents one of the 4 quarters of the year.
     * These are numbered from 1 (Q1) to 4 (Q4).
     * <p>
     * QuarterOfYear is an enum, thus each instance is a singleton.
     * As a result, QuarterOfYear instances can be compared using ==.
     *
     * @param quarterOfYear  the quarter of year to represent, from 1 to 4
     * @return the QuarterOfYear singleton, never null
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
                throw new IllegalCalendarFieldValueException(rule(), quarterOfYear, 1, 4);
        }
    }

    /**
     * Obtains an instance of <code>QuarterOfYear</code> from a date provider.
     * <p>
     * This can be used extract a quarter of year object directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the QuarterOfYear singleton, never null
     */
    public static QuarterOfYear quarterOfYear(DateProvider dateProvider) {
        return LocalDate.date(dateProvider).getMonthOfYear().getQuarterOfYear();
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
     * @return the quarter of year, from 1 to 4
     */
    public int getValue() {
        return quarterOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(rule(), getValue());
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
     * Checks if the value of this quarter of year matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return date.getMonthOfYear().getQuarterOfYear() == this;
    }

    //-----------------------------------------------------------------------
    /**
     * A string describing the quarter of year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "QuarterOfYear=" + name();
    }

}
