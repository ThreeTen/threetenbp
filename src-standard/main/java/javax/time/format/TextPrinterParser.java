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

import java.util.Iterator;
import java.util.Map.Entry;

import javax.time.calendrical.DateTimeField;

/**
 * Prints or parses field text.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class TextPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The field to output, not null.
     */
    private final DateTimeField field;
    /**
     * The text style, not null.
     */
    private final TextStyle textStyle;
    /**
     * The cached number printer parser.
     * Immutable and volatile, so no synchronization needed.
     */
    private volatile NumberPrinterParser numberPrinterParser;

    /**
     * Constructor.
     *
     * @param field  the field to output, not null
     * @param textStyle  the text style, not null
     */
    TextPrinterParser(DateTimeField field, TextStyle textStyle) {
        // validated by caller
        this.field = field;
        this.textStyle = textStyle;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        Long value = context.getValue(field);
        if (value == null) {
            return false;
        }
        String text = DateTimeFormatters.getTextProvider().getText(field, value, textStyle, context.getLocale());
        if (text == null) {
            return numberPrinterParser().print(context, buf);
        }
        buf.append(text);
        return true;
    }

    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, CharSequence parseText, int position) {
        int length = parseText.length();
        if (position < 0 || position > length) {
            throw new IndexOutOfBoundsException();
        }
        TextStyle style = (context.isStrict() ? textStyle : null);
        Iterator<Entry<String, Long>> it = DateTimeFormatters.getTextProvider().getTextIterator(field, style, context.getLocale());
        if (it != null) {
            while (it.hasNext()) {
                Entry<String, Long> entry = it.next();
                String text = entry.getKey();
                if (context.subSequenceEquals(text, 0, parseText, position, text.length())) {
                    context.setParsedField(field, entry.getValue());
                    return position + text.length();
                }
            }
            if (context.isStrict()) {
                return ~position;
            }
        }
        return numberPrinterParser().parse(context, parseText, position);
    }

    /**
     * Create and cache a number printer parser.
     * @return the number printer parser for this field, not null
     */
    private NumberPrinterParser numberPrinterParser() {
        if (numberPrinterParser == null) {
            numberPrinterParser = new NumberPrinterParser(field, 1, 19, SignStyle.NORMAL);
        }
        return numberPrinterParser;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (textStyle == TextStyle.FULL) {
            return "Text(" + field.getName() + ")";
        }
        return "Text(" + field.getName() + "," + textStyle + ")";
    }

}
