/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
 * An exception used when a value specified for a calendrical field is out of range.
 * <p>
 * Most calendrical fields have a valid range of values. This exception is used
 * when a value outside that range is passed in.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public class IllegalCalendarFieldValueException extends CalendricalRuleException {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new illegal field value exception with a standard message.
     *
     * @param fieldRule  the field rule, not null
     * @param actual  the actual invalid value
     * @param minValue  the minimum value allowed
     * @param maxValue  the maximum value allowed
     */
    public IllegalCalendarFieldValueException(DateTimeFieldRule<?> fieldRule, long actual, int minValue, int maxValue) {
        super("Illegal value for " + fieldRule.getID() + " field, value " + actual +
                " is not in the range " + minValue + " to " + maxValue, fieldRule);
    }

    /**
     * Constructs a new illegal field value exception with the specified message.
     *
     * @param message  the message to use for this exception, may be null
     * @param fieldRule  the field rule, not null
     */
    public IllegalCalendarFieldValueException(String message, DateTimeFieldRule<?> fieldRule) {
        super(message, fieldRule);
    }

}
