/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.format;

import javax.time.CalendricalException;
import javax.time.CalendricalParseException;
import javax.time.calendrical.DateTime;

/**
 * A formatter that can print and parse calendrical dates and times.
 * <p>
 * This interface is implemented by classes that provide the ability to format calendricals.
 * The main implementation is {@link javax.time.format.DateTimeFormatter DateTimeFormatter},
 * which is created using {@link javax.time.format.DateTimeFormatters DateTimeFormatters} or
 * {@link javax.time.format.DateTimeFormatterBuilder DateTimeFormatterBuilder}.
 * Localized formatting is the responsibility of the implementation.
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 */
public interface CalendricalFormatter {

    /**
     * Prints the calendrical using this formatter.
     * <p>
     * This prints the calendrical to a String using the rules of the formatter.
     * <p>
     * It is not required that all formatters are able to print - some are parse only.
     * As such, this method can throw {@code UnsupportedOperationException}.
     *
     * @param calendrical  the calendrical to print, not null
     * @return the printed string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    String print(DateTime calendrical);

    /**
     * Fully parses the text producing an object of the specified type.
     * <p>
     * This parses the text to an instance of the specified type.
     * The entire length of the text must be fully parsed.
     * <p>
     * It is not required that all formatters are able to parse - some are print only.
     * As such, this method can throw {@code UnsupportedOperationException}.
     *
     *
     * @param <T> the type to extract
     * @param text  the text to parse, not null
     * @param type  the type to extract, not null
     * @return the parsed calendrical, not null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalParseException if the parse fails
     */
    <T> T parse(CharSequence text, Class<T> type);

}
