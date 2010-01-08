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

import java.io.IOException;

import javax.time.calendar.Calendrical;

/**
 * Strategy for printing a calendrical to an appendable.
 * <p>
 * The printer may print any part, or the whole, of the input Calendrical.
 * Typically, a complete print is constructed from a number of smaller
 * units, each outputting a single field.
 * <p>
 * DateTimePrinter is an interface and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public interface DateTimePrinter {

    /**
     * Prints the calendrical object to the appendable.
     *
     * @param calendrical  the calendrical to print, not null
     * @param appendable  the appendable to add to, not null
     * @param symbols  the formatting symbols to use, not null
     * @throws CalendricalPrintException if the date time cannot be printed successfully
     * @throws IOException if the append throws an exception
     */
    void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException;

    /**
     * Checks if the calendrical contains the data necessary to be printed.
     * <p>
     * The implementation should not check the validity of the data, just
     * whether there is sufficient data to attempt a print.
     *
     * @param calendrical  the calendrical to check, not null
     * @return true if the calendrical can be printed, false if not
     */
    boolean isPrintDataAvailable(Calendrical calendrical);

}
