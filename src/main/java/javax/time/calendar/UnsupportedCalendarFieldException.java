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
 * An exception used when querying a calendar with a field that is not supported.
 *
 * @author Stephen Colebourne
 */
public class UnsupportedCalendarFieldException extends RuntimeException {

    /**
     * Constructs a new unsupported field exception with no message.
     */
    public UnsupportedCalendarFieldException() {
        super();
    }

    /**
     * Constructs a new unsupported field exception with the specified message.
     *
     * @param message  the message to use for this exception, may be null
     */
    public UnsupportedCalendarFieldException(String message) {
        super(message);
    }

    /**
     * Constructs a new unsupported field exception creating a standard error message.
     *
     * @param field  the field that is not supported, may be null
     * @param objectDescription  the description of the calendrical that does not support the field, not null
     */
    public UnsupportedCalendarFieldException(TimeFieldRule field, String objectDescription) {
        super("Field " + (field == null ? "null" : field.getName()) + " is not supported on a " + objectDescription);
    }

//    /**
//     * Constructs a new unsupported field exception with the specified message and cause.
//     *
//     * @param message  the message to use for this exception, may be null
//     * @param cause  the underlying cause of this exception, may be null
//     */
//    public UnsupportedCalendarFieldException(String message, Throwable cause) {
//        super(message, cause);
//    }
//
//    /**
//     * Constructs a new unsupported field exception with the specified cause,
//     * extracting the message from the cause if possible.
//     *
//     * @param cause  the underlying cause of this exception, may be null
//     */
//    public UnsupportedCalendarFieldException(Throwable cause) {
//        super(cause);
//    }

}
