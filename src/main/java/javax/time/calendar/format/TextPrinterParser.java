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

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * Prints or parses field text.
 *
 * @author Stephen Colebourne
 */
class TextPrinterParser implements DateTimePrinter {

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
        if (fieldRule == null) {
            throw new NullPointerException("The field rule must not be null");
        }
        if (textStyle == null) {
            throw new NullPointerException("The text style must not be null");
        }
        this.fieldRule = fieldRule;
        this.textStyle = textStyle;
    }

    /** {@inheritDoc} */
    public void print(Appendable appendable, FlexiDateTime dateTime, Locale locale) throws IOException {
        appendable.append(fieldRule.getText(dateTime, locale, textStyle));
    }

//    /** {@inheritDoc} */
//    public int parse(DateTimeParseContext context, String parseText, int position) {
//        int length = parseText.length();
//        if (position > length) {
//            throw new IndexOutOfBoundsException();
//        }
//        int endPos = position + literal.length();
//        if (endPos > length) {
//            return ~position;
//        }
//        if (literal.equals(parseText.substring(position, endPos)) == false) {
//            return ~position;
//        }
//        return endPos;
//    }

}
