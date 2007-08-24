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

import javax.time.part.TimePart;


/**
 * A time field.
 *
 * @param <U> the unit time part
 * @param <R> the range time part
 * @author Stephen Colebourne
 */
public class GenericTimeField<U extends TimePart, R extends TimePart>
        implements TimeField<U, R> {

    /**
     * The field of time being represented, valid for the field.
     */
    private final int value;
    /**
     * The type being represented, not null.
     */
    private final TimeFieldType<U, R> type;

    /**
     * Constructor.
     *
     * @param value  the value of this time field, must be valid for the field
     * @param type  the time field type, not null
     */
    public GenericTimeField(int value, TimeFieldType<U, R> type) {
        super();
        this.value = value;
        this.type = type;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the internal state of the object for interoperation.
     * Most applications will not need to use this method.
     *
     * @return the calendar state, never null
     */
    @Override
    public CalendarState getCalendarState() {
        return new CalendarState();
    }

    /** {@inheritDoc} */
    public int getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public TimeField<U, R> withValue(int newValue) {
        return new GenericTimeField<U, R>(newValue, getType());
    }

    /** {@inheritDoc} */
    public TimeFieldType<U, R> getType() {
        return type;
    }

    /**
     * Compares this time field to another.
     *
     * @param other  the other time field, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     */
    @Override
    public int compareTo(TimeView<U, R> other) {
        return getCalendarState().compareTo(other.getCalendarState());
    }

}
