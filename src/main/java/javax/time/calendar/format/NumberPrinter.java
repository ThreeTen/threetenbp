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
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

/**
 * Prints an integer with padding.
 *
 * @author Stephen Colebourne
 */
class NumberPrinter implements DateTimePrinter {
    // TODO: I18N: The numeric output varies by locale

    /**
     * The field to output.
     */
    private final DateTimeFieldRule fieldRule;
    /**
     * The minimum width allowed, padding is used up to this width.
     */
    private final int minWidth;
    /**
     * The maximum width allowed.
     */
    private final int maxWidth;
    /**
     * The pad character.
     */
    private final char padChar;
    /**
     * Whether to left pad (true) or right pad (false).
     */
    private final boolean padOnLeft;
    /**
     * The positive/negative sign style.
     */
    private final SignStyle signStyle;

    /**
     * Constructor.
     *
     * @param fieldRule  the rule of the field to print, not null
     */
    public NumberPrinter(DateTimeFieldRule fieldRule) {
        this(fieldRule, 0, Integer.MAX_VALUE, '0', true, SignStyle.NORMAL);
    }

    /**
     * Constructor.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @param minWidth  the minimum field width
     * @param maxWidth  the maximum field width
     * @param padChar  the padding character
     * @param padOnLeft  whether to left pad (true) or right pad (false)
     * @param signStyle  the positive/negative sign style, not null
     */
    NumberPrinter(DateTimeFieldRule fieldRule, int minWidth, int maxWidth, char padChar, boolean padOnLeft, SignStyle signStyle) {
        this.fieldRule = fieldRule;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.padChar = padChar;
        this.padOnLeft = padOnLeft;
        this.signStyle = signStyle;
    }

    /** {@inheritDoc} */
    public void print(Appendable appendable, FlexiDateTime dateTime, Locale locale) throws IOException {
        int value = dateTime.getValue(fieldRule);
        String str = Integer.toString(Math.abs(value));
        if (str.length() > maxWidth) {
            throw new CalendricalFormatFieldException(fieldRule, value, maxWidth);
        }
        signStyle.print(appendable, fieldRule, value, minWidth);
        if (padOnLeft) {
            for (int i = 0; i < minWidth - str.length(); i++) {
                appendable.append(padChar);
            }
            appendable.append(str);
        } else {
            appendable.append(str);
            for (int i = 0; i < minWidth - str.length(); i++) {
                appendable.append(padChar);
            }
        }
    }

}
