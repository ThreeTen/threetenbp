/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Locale;

import javax.time.calendar.FlexiDateTime;

/**
 * Pads the output to a fixed width.
 *
 * @author Stephen Colebourne
 */
class PadPrinterDecorator implements DateTimePrinter {

    /**
     * The printer to decorate.
     */
    private DateTimePrinter printer;
    /**
     * The width to pad the next field to.
     */
    private int padWidth;
    /**
     * The character to pad the next field with.
     */
    private char padChar;

    /**
     * Constructor.
     *
     * @param printer  the printer, not null
     * @param padWidth  the width to pad to, 1 or greater
     * @param padChar  the pad character
     */
    PadPrinterDecorator(DateTimePrinter printer, int padWidth, char padChar) {
        // input checked by DateTimeFormatterBuilder
        this.printer = printer;
        this.padWidth = padWidth;
        this.padChar = padChar;
    }

    /** {@inheritDoc} */
    public void print(Appendable appendable, FlexiDateTime dateTime, Locale locale) throws IOException {
        StringBuilder buf = new StringBuilder(32);
        printer.print(buf, dateTime, locale);
        int len = buf.length();
        if (len > padWidth) {
            throw new CalendricalFormatException("Output of " + len + " characters exceeds pad width of " + padWidth);
        }
        for (int i = 0; i < padWidth - len; i++) {
            appendable.append(padChar);
        }
        appendable.append(buf);
    }

//    /** {@inheritDoc} */
//    public int parse(DateTimeParseContext context, String parseText, int position) {
//    }

}
