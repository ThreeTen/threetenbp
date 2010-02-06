/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.format;

import javax.time.calendar.CalendricalMerger;

/**
 * Strategy for parsing text to calendrical information.
 * <p>
 * The parser may parse any piece of text from the input, storing the result
 * in the context. Typically, each individual parser will just parse one
 * field, such as the day-of-month, storing the value in the context.
 * Once the parse is complete, the caller will then convert the context
 * to a {@link CalendricalMerger} to merge the parsed values to create the
 * desired object, such as a {@code LocalDate}.
 * <p>
 * The parse position will be updated during the parse. Parsing will start at
 * the specified index and the return value specifies the new parse position
 * for the next parser. If an error occurs, the returned index will be negative
 * and will have the error position encoded using the complement operator.
 * <p>
 * DateTimeParser is an interface and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * <p>
 * The context is not a thread-safe object and a new instance will be created
 * for each parse that occurs. The context must not be stored in an instance
 * variable or shared with any other threads.
 *
 * @author Stephen Colebourne
 */
public interface DateTimeParser {

    /**
     * Parses from the supplied text and position into the calendrical.
     *
     * @param context  the context to use and parse into, not null
     * @param parseText  the input text to parse, not null
     * @param position  the position to start parsing at, from 0 to the text length
     * @return the new parse position, where negative means an error with the
     *  error position encoded using the complement ~ operator
     * @throws NullPointerException if the context or text is null
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    int parse(DateTimeParseContext context, String parseText, int position);

}
