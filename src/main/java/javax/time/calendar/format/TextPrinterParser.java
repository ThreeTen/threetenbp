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

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * Prints or parses field text.
 *
 * @author Stephen Colebourne
 */
class TextPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The field to output, not null.
     */
    private final DateTimeFieldRule fieldRule;
    /**
     * The text style, not null.
     */
    private final TextStyle textStyle;

    /**
     * Constructor.
     *
     * @param fieldRule  the rule of the field, not null
     * @param textStyle  the text style, not null
     */
    TextPrinterParser(DateTimeFieldRule fieldRule, TextStyle textStyle) {
        // validated by caller
        this.fieldRule = fieldRule;
        this.textStyle = textStyle;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        int value = calendrical.getValueInt(fieldRule);
        String text = symbols.getFieldValueText(fieldRule, textStyle, value);
        if (text == null) {
            text = FormatUtil.convertToI18N(Integer.toString(value), symbols);
        }
        appendable.append(text);
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        return calendrical.isSupported(fieldRule);
    }

    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        int length = parseText.length();
        if (position > length) {
            throw new IndexOutOfBoundsException();
        }
        int[] match = context.getSymbols().matchFieldText(fieldRule, textStyle, false, parseText.substring(position));
        if (match == null) {
            return new NumberPrinterParser(fieldRule, 1, 10, SignStyle.NORMAL).parse(context, parseText, position);
        } else if (match[0] == 0) {
            return ~position;
        } else {
            position += match[0];
            context.setFieldValue(fieldRule, match[1]);
        }
        return position;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Text(" + fieldRule.getID() + "," + textStyle + ")";
    }

}
