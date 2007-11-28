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
package javax.time.calendar;

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.MonthOfYear;

/**
 * Strategy for resolving an invalid year-month-day to a valid one
 * within the ISO calendar system.
 * <p>
 * CalendricalResolver is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public abstract class CalendricalResolver {

    /**
     * Restrictive constructor.
     */
    protected CalendricalResolver() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Resolves the combination of year, month and day into a date.
     * <p>
     * The purpose of resolution is to avoid invalid dates. Each of the three
     * fields are individually valid. However, the day of month may not be
     * valid for the associated month and year.
     * <p>
     * This method forwards to the protected method {@link #handleResolveDate}.
     *
     * @param year  the year that was input, not null
     * @param monthOfYear  the month of year, not null
     * @param dayOfMonth  the proposed day of month, not null
     * @return the resolved date, never null
     * @throws IllegalCalendarFieldValueException if the date cannot be resolved
     */
    public final LocalDate resolveDate(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        LocalDate date = handleResolveDate(year, monthOfYear, dayOfMonth);
        if (date == null) {
            throw new IllegalCalendarFieldValueException(
                    "CalendricalResolver implementation must not return null: " + getClass().getName());
        }
        return date;
    }

    /**
     * Overridable method to allow the implementation of a strategy for
     * converting an invalid year-month-day to a valid one.
     * <p>
     * The day of month may be invalid for the specified month and year.
     *
     * @param year  the year that was input, not null
     * @param monthOfYear  the month of year, not null
     * @param dayOfMonth  the day of month, not null
     * @return the resolved date, never null
     * @throws IllegalCalendarFieldValueException if the date cannot be resolved
     */
    protected abstract LocalDate handleResolveDate(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth);

}
