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

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

/**
 * Prints an integer with padding.
 *
 * @author Stephen Colebourne
 */
class NumberParser implements DateTimeParser {
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
    public NumberParser(DateTimeFieldRule fieldRule) {
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
    NumberParser(DateTimeFieldRule fieldRule, int minWidth, int maxWidth, char padChar, boolean padOnLeft, SignStyle signStyle) {
        this.fieldRule = fieldRule;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.padChar = padChar;
        this.padOnLeft = padOnLeft;
        this.signStyle = signStyle;
    }

//    /** {@inheritDoc} */
//    public FlexiDateTime parse(CharSequence parseText, ParsePosition parsePosition, FlexiDateTime dateTime, Locale locale) {
//        int length = parseText.length();
//        int pos = parsePosition.getIndex();
//        int minEndPos = pos + minWidth;
//        if (minEndPos > length) {
//            parsePosition.setErrorIndex(pos);
//            return dateTime;
//        }
//        int total = 0;
//        while (pos < minEndPos) {
//            char ch = parseText.charAt(pos++);
//            if (ch >= '0' && ch <= '9') {  // TODO: I18N
//                total *= 10;
//                total += (ch - '0');
//            } else {
//                parsePosition.setErrorIndex(pos - 1);
//                return dateTime;
//            }
//        }
//        int maxEndPos = pos + maxWidth;
//        while (pos < maxEndPos) {
//            char ch = parseText.charAt(pos++);
//            if (ch >= '0' && ch <= '9') {
//                total *= 10;
//                total += (ch - '0');
//            } else {
//                break;
//            }
//        }
//        parsePosition.setIndex(pos);
//        return dateTime.withFieldValue(fieldRule, total);
//    }

    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        int length = parseText.length();
        int minEndPos = position + minWidth;
        if (minEndPos > length) {
            return ~position;
        }
        int total = 0;
        if (minWidth > 1) {
            while (position < minEndPos - 1) {
                char ch = parseText.charAt(position);
                if (ch != padChar) {
                    break;
                }
                position++;
            }
        }
        while (position < minEndPos) {
            char ch = parseText.charAt(position++);
            int digit = context.digit(ch);
            if (digit < 0) {
                return ~(position - 1);
            }
            total *= 10;
            total += digit;
        }
        int maxEndPos = position + maxWidth;
        while (position < maxEndPos) {
            char ch = parseText.charAt(position++);
            int digit = context.digit(ch);
            if (digit < 0) {
                position--;
                break;
            }
            total *= 10;
            total += digit;
        }
        context.setFieldValue(fieldRule, total);
        return position;
    }

}
