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
package javax.time.calendar;

/**
 * An exception used when querying a calendar with a field that is not supported.
 *
 * @author Stephen Colebourne
 */
public class UnsupportedCalendarFieldException extends RuntimeException {

    /**
     * The field that caused the exception
     */
    private final TimeFieldRule fieldRule;

    /**
     * Constructs a new unsupported field exception creating a standard error message.
     *
     * @param fieldRule  the rule of the field that is not supported, may be null
     * @param objectDescription  the description of the calendrical that does not support the field, may be null
     */
    public UnsupportedCalendarFieldException(TimeFieldRule fieldRule, String objectDescription) {
        super("Field " + (fieldRule == null ? "null" : fieldRule.getName()) + " is not supported on " + objectDescription);
        this.fieldRule = fieldRule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule of the field that caused the exception.
     *
     * @return the field rule, null if unknown
     */
    public TimeFieldRule getFieldRule() {
        return fieldRule;
    }

}
