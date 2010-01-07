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
 * Pads the output to a fixed width.
 * <p>
 * PadPrinterParserDecorator is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class PadPrinterParserDecorator implements DateTimePrinter, DateTimeParser {

    /**
     * The printer to decorate.
     */
    private final DateTimePrinter printer;
    /**
     * The parser to decorate.
     */
    private final DateTimeParser parser;
    /**
     * The width to pad the next field to.
     */
    private final int padWidth;
    /**
     * The character to pad the next field with.
     */
    private final char padChar;

    /**
     * Constructor.
     *
     * @param printer  the printer, may be null in which case print() must not be called
     * @param parser  the parser, may be null in which case parse() must not be called
     * @param padWidth  the width to pad to, 1 or greater
     * @param padChar  the pad character
     */
    PadPrinterParserDecorator(DateTimePrinter printer, DateTimeParser parser, int padWidth, char padChar) {
        // input checked by DateTimeFormatterBuilder
        this.printer = printer;
        this.parser = parser;
        this.padWidth = padWidth;
        this.padChar = padChar;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        StringBuilder buf = new StringBuilder(32);
        printer.print(calendrical, buf, symbols);
        int len = buf.length();
        if (len > padWidth) {
            throw new CalendricalPrintException("Output of " + len + " characters exceeds pad width of " + padWidth);
        }
        for (int i = 0; i < padWidth - len; i++) {
            appendable.append(padChar);
        }
        appendable.append(buf);
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        return printer.isPrintDataAvailable(calendrical);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        if (position > parseText.length()) {
            throw new IndexOutOfBoundsException();
        }
        int endPos = position + padWidth;
        if (endPos > parseText.length()) {
            return ~position;  // not enough characters in the string to meet the parse width
        }
        int pos = position;
        while (pos < endPos && parseText.charAt(pos) == padChar) {
            pos++;
        }
        parseText = parseText.substring(0, endPos);
        int firstError = 0;
        while (pos >= position) {
            int resultPos = parser.parse(context, parseText, pos);
            if (resultPos < 0) {
                // parse of decorated field had an error
                if (firstError == 0) {
                    firstError = resultPos;
                }
                // loop around in case the decorated parser can handle the padChar at the start
                pos--;
                continue;
            }
            if (resultPos != endPos) {
                return ~position;  // parse of decorated field didn't parse to the end
            }
            return resultPos;
        }
        // loop runs at least once, so firstError must be set by the time we get here
        return firstError;  // return error from first parse of decorated field
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        String base = "Pad(";
        if (printer == parser) {
            base += printer;
        } else {
            base += (printer == null ? "" : printer) + "," + (parser == null ? "" : parser);
        }
        return base + "," + padWidth + (padChar == ' ' ? ")" : ",'" + padChar + "')");
    }

}
