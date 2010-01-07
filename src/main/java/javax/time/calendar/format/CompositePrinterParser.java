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
import java.util.Arrays;
import java.util.List;

import javax.time.calendar.Calendrical;

/**
 * Composite printer and parser.
 * <p>
 * CompositePrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class CompositePrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The list of printers that will be used, treated as immutable.
     */
    private final DateTimePrinter[] printers;
    /**
     * The list of parsers that will be used, treated as immutable.
     */
    private final DateTimeParser[] parsers;
    /**
     * Whether the print and parse are optional.
     */
    private final boolean optional;

    /**
     * Constructor.
     *
     * @param printers  the printers, may be null in which case print() must not be called
     * @param parsers  the parsers, may be null in which case parse() must not be called
     * @param optional  whether the print/parse is optional
     */
    CompositePrinterParser(List<DateTimePrinter> printers, List<DateTimeParser> parsers, boolean optional) {
        this.printers = printers.contains(null) ? null : printers.toArray(new DateTimePrinter[printers.size()]);
        this.parsers = parsers.contains(null) ? null : parsers.toArray(new DateTimeParser[parsers.size()]);
        this.optional = optional;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this printer-parser with the optional flag changed.
     *
     * @param optional  the optional flag to set in the copy
     * @return the new printer-parser, never null
     */
    public CompositePrinterParser withOptional(boolean optional) {
        if (optional == this.optional) {
            return this;
        }
        return new CompositePrinterParser(Arrays.asList(printers), Arrays.asList(parsers), optional);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public boolean isPrintSupported() {
        return printers != null;
    }

    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        if (printers == null) {
            throw new UnsupportedOperationException("Formatter does not support printing");
        }
        if (optional) {
            for (DateTimePrinter printer : printers) {
                if (printer.isPrintDataAvailable(calendrical) == false) {
                    return;  // data not available to print
                }
            }
        }
        for (DateTimePrinter printer : printers) {
            printer.print(calendrical, appendable, symbols);
        }
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        if (optional) {
            return true;  // always printable, although sometimes it might print nothing
        }
        for (DateTimePrinter printer : printers) {
            if (printer.isPrintDataAvailable(calendrical) == false) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public boolean isParseSupported() {
        return parsers != null;
    }

    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        if (parsers == null) {
            throw new UnsupportedOperationException("Formatter does not support parsing");
        }
        if (optional) {
            context.startOptional();
            int pos = position;
            for (DateTimeParser parser : parsers) {
                pos = parser.parse(context, parseText, pos);
                if (pos < 0) {
                    context.endOptional(false);
                    return position;  // return original position
                }
            }
            context.endOptional(true);
            return pos;
        } else {
            for (DateTimeParser parser : parsers) {
                position = parser.parse(context, parseText, position);
                if (position < 0) {
                    break;
                }
            }
            return position;
        }
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (printers != null) {
            buf.append(optional ? "[" : "(");
            for (DateTimePrinter printer : printers) {
                buf.append(printer);
            }
            buf.append(optional ? "]" : ")");
        }
        return buf.toString();
    }

}
