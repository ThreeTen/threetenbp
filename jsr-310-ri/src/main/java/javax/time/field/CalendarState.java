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
package javax.time.field;

import java.util.Set;

import javax.time.part.TimePart;

/**
 * A view over a set of related time fields, such as a Date or Time.
 * <p>
 * A time view consists of one or more fields that together desribe one
 * or more points in time. There is a restriction in that the fields must
 * connect with one another, thus a Year, MonthOfYear and DayOfMonth are
 * valid, but a Year and DayOfMonth are not as there is a gap in the definition.
 * <p>
 * TimeView is an interface and must be implemented with care to ensure
 * other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public class CalendarState
        implements Comparable<CalendarState> {

    /**
     * Does the moment occur once on the time-line.
     *
     * @return true if the moment occurs once on the time-line
     */
    public boolean isSingleOccurrance() {
        return false;
    }

    /**
     * Is the state representing a single contiguous view of time.
     *
     * @return the number of time fields forming the view, one or more
     */
    public boolean isContiguous() {
        return false;
    }

    /**
     * Gets the smallest unit part that this state supports.
     *
     * @return the number of time fields forming the view, one or more
     */
    public TimePart getSmallestUnitPart() {
        return null;
    }

    /**
     * Gets the lergest range part that this state supports.
     *
     * @return the number of time fields forming the view, one or more
     */
    public TimePart getLargestRangePart() {
        return null;
    }

    /**
     * Gets the number of time fields that define the view.
     *
     * @return the number of time fields forming the view, one or more
     */
    public int size() {
        return 0;
    }

    /**
     * Gets the time field types that define the view.
     *
     * @param <U> the unit time part
     * @param <R> the range time part
     * @return the time field types forming the view, one or more
     */
    public <U extends TimePart, R extends TimePart> Set<TimeFieldType<U, R>> getFieldTypes() {
        return null;
    }

    /**
     * Does this calendar support a time field using a unit part and range.
     *
     * @param <U> the unit time part
     * @param <R> the range time part
     * @param type  the field type to check for, null returns false
     * @return true if the field type is supported, false if not
     * @throws IllegalArgumentException if the new value is invalid
     */
    public <U extends TimePart, R extends TimePart> boolean isSupported(TimeFieldType<U, R> type) {
        return getFieldTypes().contains(type);
    }

    /**
     * Gets a time field using a field type.
     *
     * @param <U> the unit time part
     * @param <R> the range time part
     * @param type  the field type to check for, not null
     * @return the time field for the unit and range requested
     * @throws IllegalArgumentException if the new value is invalid
     */
    public <U extends TimePart, R extends TimePart> TimeField<U, R> getField(TimeFieldType<U, R> type) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Does this calendar support a time field using a unit part and range.
     *
     * @param <U> the unit time part
     * @param <R> the range time part
     * @param unitPart  the unit time part
     * @param rangePart  the range time part
     * @return the time field for the unit and range requested
     * @throws IllegalArgumentException if the new value is invalid
     */
    public <U extends TimePart, R extends TimePart> boolean isSupported(U unitPart, R rangePart) {
        return true;
    }

    /**
     * Gets a time field using a unit part and range.
     *
     * @param <U> the unit time part
     * @param <R> the range time part
     * @param unitPart  the unit time part
     * @param rangePart  the range time part
     * @return the time field for the unit and range requested
     * @throws IllegalArgumentException if the new value is invalid
     */
    public <U extends TimePart, R extends TimePart> TimeField<U, R> getField(U unitPart, R rangePart) {
        return null;
    }

    @Override
    public int compareTo(CalendarState other) {
        // TODO: Check comparable
        return 0;
    }

}
