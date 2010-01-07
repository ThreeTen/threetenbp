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
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.DateTimeFieldRule.TextStore;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * Prints or parses field text.
 * <p>
 * TextPrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class TextPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The rule to output, not null.
     */
    private final DateTimeFieldRule<?> rule;
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
     * @param rule  the rule to output, not null
     * @param textStyle  the text style, not null
     */
    TextPrinterParser(DateTimeFieldRule<?> rule, TextStyle textStyle) {
        // validated by caller
        this.rule = rule;
        this.textStyle = textStyle;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        int value = rule.getInt(calendrical);
        TextStore textStore = rule.getTextStore(symbols.getLocale(), textStyle);
        String text = (textStore != null ? textStore.getValueText(value) : null);
        if (text != null) {
            appendable.append(text);
        } else {
            numberPrinterParser().print(calendrical, appendable, symbols);
        }
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        return calendrical.get(rule) != null;  // TODO: Better, or remove method
    }

    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        int length = parseText.length();
        if (position > length) {
            throw new IndexOutOfBoundsException();
        }
        if (context.isStrict()) {
            TextStore textStore = rule.getTextStore(context.getLocale(), textStyle);
            if (textStore != null) {
                long match = textStore.matchText(!context.isCaseSensitive(), parseText.substring(position));
                if (match == 0) {
                    return ~position;
                } else if (match > 0) {
                    position += (match >>> 32);
                    context.setParsed(rule, (int) match);
                    return position;
                }
            }
            return numberPrinterParser().parse(context, parseText, position);
        } else {
            for (TextStyle textStyle : TextStyle.values()) {
                TextStore textStore = rule.getTextStore(context.getLocale(), textStyle);
                if (textStore != null) {
                    long match = textStore.matchText(!context.isCaseSensitive(), parseText.substring(position));
                    if (match > 0) {
                        position += (match >>> 32);
                        context.setParsed(rule, (int) match);
                        return position;
                    }
                }
            }
            return numberPrinterParser().parse(context, parseText, position);
        }
    }

    /**
     * Create and cache a number printer parser.
     * @return the number printer parser for this field, never null
     */
    private NumberPrinterParser numberPrinterParser() {
        if (numberPrinterParser == null) {
            numberPrinterParser = new NumberPrinterParser(rule, 1, 10, SignStyle.NORMAL);
        }
        return numberPrinterParser;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (textStyle == TextStyle.FULL) {
            return "Text(" + rule.getID() + ")";
        }
        return "Text(" + rule.getID() + "," + textStyle + ")";
    }

}
