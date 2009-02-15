/*
 * Copyright (c) 2007, 2008, Stephen Colebourne & Michael Nascimento Santos
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
 * An exception used when a value specified for a calendar field is invalid.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public class IllegalCalendarFieldValueException extends CalendarFieldException {

    /**
     * Constructs a new illegal field value exception with no message.
     *
     * @param fieldRule  the field rule, not null
     * @param actual  the actual invalid value
     * @param minValue  the minimum value allowed
     * @param maxValue  the maximum value allowed
     */
    public IllegalCalendarFieldValueException(DateTimeFieldRule fieldRule, long actual, int minValue, int maxValue) {
        super("Illegal value for " + fieldRule.getID() + " field, value " + actual +
                " is not in the range " + minValue + " to " + maxValue, fieldRule);
    }

    /**
     * Constructs a new illegal field value exception with the specified message.
     *
     * @param message  the message to use for this exception, may be null
     * @param fieldRule  the field rule, not null
     */
    public IllegalCalendarFieldValueException(String message, DateTimeFieldRule fieldRule) {
        super(message, fieldRule);
    }

//    /**
//     * Constructs a new illegal field value exception with the specified message and cause.
//     *
//     * @param message  the message to use for this exception, may be null
//     * @param cause  the underlying cause of this exception, may be null
//     */
//    public IllegalCalendarFieldValueException(String message, Throwable cause) {
//        super(message, cause);
//    }
//
//    /**
//     * Constructs a new illegal field value exception with the specified cause,
//     * extracting the message from the cause if possible.
//     *
//     * @param cause  the underlying cause of this exception, may be null
//     */
//    public IllegalCalendarFieldValueException(Throwable cause) {
//        super(cause);
//    }

}
