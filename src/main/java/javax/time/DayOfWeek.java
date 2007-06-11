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
package javax.time;

/**
 * A time field representing a day of week.
 * <p>
 * DayOfWeek is an immutable time field that can only store a day of week.
 * It is a type-safe way of representing a day of week in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a DayOfWeek 
 * instance; use dayOfWeek() instead.</b>
 * The day of week may be queried using getDayOfWeek().
 * <p>
 * DayOfWeek is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 */
public enum DayOfWeek implements Moment {

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
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

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

}
