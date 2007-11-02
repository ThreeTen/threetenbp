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
     * Resolves the invalid combination of year, month and day.
     * The individual values may be out or their normal range, or the
     * day of month may be invalid for the specified month.
     * <p>
     * This method forwards to an internal package scoped method that
     * calls {@link #handleResolveDate(int, int, int)}. The package scoped
     * method will validate the result of <code>handleResolve</code> to
     * ensure that the date returned is valid.
     *
     * @param year  the year that was input, may be invalid
     * @param monthOfYear  the month of year, should be from 1 to 12 but may be invalid
     * @param dayOfMonth  the day of month, should be from 1 to 31 but may be invalid
     * @return the resolved values, returned as a (year,month,day) tuple, never null
     * @throws IllegalCalendarFieldValueException if a field cannot be resolved
     */
    public final int[] resolveDate(int year, int monthOfYear, int dayOfMonth) {
        return doResolveDate(year, monthOfYear, dayOfMonth);
    }

    /**
     * Provides validation that the result of {@link #handleResolveDate(int, int, int)}
     * is a valid date.
     *
     * @param year  the year that was input, may be invalid
     * @param monthOfYear  the month of year, should be from 1 to 12 but may be invalid
     * @param dayOfMonth  the day of month, should be from 1 to 31 but may be invalid
     * @return the resolved values, returned as a (year,month,day) tuple, never null
     * @throws IllegalCalendarFieldValueException if a field cannot be resolved
     */
    int[] doResolveDate(int year, int monthOfYear, int dayOfMonth) {
        int[] result = handleResolveDate(year, monthOfYear, dayOfMonth);
        if (result == null) {
            throw new IllegalCalendarFieldValueException(
                    "CalendricalResolver implementation must not return null: " + getClass().getName());
        }
        if (result.length != 3) {
            throw new IllegalCalendarFieldValueException(
                    "CalendricalResolver implementation must return a tuple: " + getClass().getName());
        }
        ISOChronology.INSTANCE.checkValidDate(result[0], result[1], result[2]);
        return result;
    }

    /**
     * Overridable method to allow the implementation of a strategy for
     * converting an invalid year-month-day to a valid one.
     * <p>
     * The individual values may be out or their normal range, or the
     * day of month may be invalid for the specified month.
     * After the completion of this method, the result will be validated.
     * If your implementation cannot resolve certain dates, then it can simply
     * return a (year,month,day) tuple and rely on the exception being thrown.
     *
     * @param year  the year that was input, may be invalid
     * @param monthOfYear  the month of year, should be from 1 to 12 but may be invalid
     * @param dayOfMonth  the day of month, should be from 1 to 31 but may be invalid
     * @return the resolved values, returned as a (year,month,day) tuple, never null
     * @throws IllegalCalendarFieldValueException if a field cannot be resolved
     */
    protected abstract int[] handleResolveDate(int year, int monthOfYear, int dayOfMonth);

}
