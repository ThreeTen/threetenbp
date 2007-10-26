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
 * Provides resolvers for handling invalid date-time values.
 *
 * @author Stephen Colebourne
 */
public abstract class CalendricalResolver {

    /**
     * Resolves the invalid combination of year, month and day.
     * The individual values may be out or their normal range, or the
     * day of month may be invalid for the specified month.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return the resolved values, returned as a (year,month,day) tuple, never null
     * @throws IllegalCalendarFieldValueException if a field cannot be resolved
     */
    public abstract int[] resolveYMD(int year, int monthOfYear, int dayOfMonth);

    /**
     * Returns a state object with the value set.
     *
     * @param field  the field to be updated, not null
     * @param state  the state to resolve, not null
     * @param value  the value being set, may be completely out of range
     * @return the resolved state, never null
     * @throws IllegalCalendarFieldValueException if a field cannot be resolved
     */
    public abstract CalendricalState set(TimeFieldRule field, CalendricalState state, int value);

    /**
     * Returns a state object with the value added.
     *
     * @param field  the field to be updated, not null
     * @param state  the state to resolve, not null
     * @param value  the value being added, positive or negative
     * @return the resolved state, never null
     * @throws IllegalCalendarFieldValueException if a field cannot be resolved
     */
    public abstract CalendricalState plus(TimeFieldRule field, CalendricalState state, int value);

}
