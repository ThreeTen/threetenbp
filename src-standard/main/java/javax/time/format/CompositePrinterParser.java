/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.List;

/**
 * Composite printer and parser.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class CompositePrinterParser implements DateTimePrinterParser {

    /**
     * The list of printers that will be used, treated as immutable.
     */
    private final DateTimePrinterParser[] printerParsers;
    /**
     * Whether the print and parse are optional.
     */
    private final boolean optional;

    /**
     * Constructor.
     *
     * @param printerParsers  the printer-parsers, no nulls, not null
     * @param optional  whether the print/parse is optional
     */
    CompositePrinterParser(List<DateTimePrinterParser> printerParsers, boolean optional) {
        this(printerParsers.toArray(new DateTimePrinterParser[printerParsers.size()]), optional);
    }

    /**
     * Constructor.
     *
     * @param printerParsers  the printer-parsers, assigned, not null
     * @param optional  whether the print/parse is optional
     */
    CompositePrinterParser(DateTimePrinterParser[] printerParsers, boolean optional) {
        this.printerParsers = printerParsers;
        this.optional = optional;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this printer-parser with the optional flag changed.
     *
     * @param optional  the optional flag to set in the copy
     * @return the new printer-parser, not null
     */
    public CompositePrinterParser withOptional(boolean optional) {
        if (optional == this.optional) {
            return this;
        }
        return new CompositePrinterParser(printerParsers, optional);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        int length = buf.length();
        if (optional) {
            context.startOptional();
        }
        try {
            for (DateTimePrinterParser pp : printerParsers) {
                if (pp.print(context, buf) == false) {
                    buf.setLength(length);  // reset buffer
                    return true;
                }
            }
        } finally {
            if (optional) {
                context.endOptional();
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    @Override
    public int parse(DateTimeParseContext context, CharSequence text, int position) {
        if (optional) {
            context.startOptional();
            int pos = position;
            for (DateTimePrinterParser pp : printerParsers) {
                pos = pp.parse(context, text, pos);
                if (pos < 0) {
                    context.endOptional(false);
                    return position;  // return original position
                }
            }
            context.endOptional(true);
            return pos;
        } else {
            for (DateTimePrinterParser pp : printerParsers) {
                position = pp.parse(context, text, position);
                if (position < 0) {
                    break;
                }
            }
            return position;
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (printerParsers != null) {
            buf.append(optional ? "[" : "(");
            for (DateTimePrinterParser pp : printerParsers) {
                buf.append(pp);
            }
            buf.append(optional ? "]" : ")");
        }
        return buf.toString();
    }

}
