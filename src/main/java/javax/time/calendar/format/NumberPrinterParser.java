/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * Prints and parses a numeric date-time field with optional padding.
 * <p>
 * NumberPrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
class NumberPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * Array of 10 to the power of n
     */
    private static final int[] EXCEED_POINTS = new int[] {
        0,
        10,
        100,
        1000,
        10000,
        100000,
        1000000,
        10000000,
        100000000,
        1000000000,
    };

    /**
     * The field to output, not null.
     */
    private final DateTimeFieldRule fieldRule;
    /**
     * The minimum width allowed, zero padding is used up to this width, from 1 to 10.
     */
    private final int minWidth;
    /**
     * The maximum width allowed, from 1 to 10.
     */
    private final int maxWidth;
    /**
     * The positive/negative sign style, not null.
     */
    private final SignStyle signStyle;

    /**
     * Constructor.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @param minWidth  the minimum field width, from 1 to 10
     * @param maxWidth  the maximum field width, from minWidth to 10
     * @param signStyle  the positive/negative sign style, not null
     */
    NumberPrinterParser(DateTimeFieldRule fieldRule, int minWidth, int maxWidth, SignStyle signStyle) {
        // validated by caller
        this.fieldRule = fieldRule;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.signStyle = signStyle;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        int value = calendrical.deriveValue(fieldRule);
        String str = (value == Integer.MIN_VALUE ? "2147483648" : Integer.toString(Math.abs(value)));
        if (str.length() > maxWidth) {
            throw new CalendricalFormatFieldException(fieldRule, value, maxWidth);
        }
        str = FormatUtil.convertToI18N(str, symbols);
        
        if (value >= 0) {
            switch (signStyle) {
                case EXCEEDS_PAD:
                    if (minWidth < 10 && value >= EXCEED_POINTS[minWidth]) {
                        appendable.append(symbols.getPositiveSignChar());
                    }
                    break;
                case ALWAYS:
                    appendable.append(symbols.getPositiveSignChar());
                    break;
            }
        } else {
            switch (signStyle) {
                case NORMAL:
                case EXCEEDS_PAD:
                case ALWAYS:
                    appendable.append(symbols.getNegativeSignChar());
                    break;
                case NOT_NEGATIVE:
                    throw new CalendricalFormatFieldException(fieldRule, value);
            }
        }
        for (int i = 0; i < minWidth - str.length(); i++) {
            appendable.append(symbols.getZeroChar());
        }
        appendable.append(str);
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        return calendrical.isDerivable(fieldRule);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        int length = parseText.length();
        if (position == length) {
            return ~position;
        }
        char sign = parseText.charAt(position);  // IOOBE if invalid position
        boolean negative = false;
        boolean positive = false;
        if (sign == context.getSymbols().getPositiveSignChar()) {
            positive = true;
            switch (signStyle) {
                case ALWAYS:
                case EXCEEDS_PAD:
                    position++;
                    break;
                default:
                    return ~position;
            }
        } else if (sign == context.getSymbols().getNegativeSignChar()) {
            negative = true;
            switch (signStyle) {
                case ALWAYS:
                case EXCEEDS_PAD:
                case NORMAL:
                    position++;
                    break;
                default:
                    return ~position;
            }
        } else if (signStyle == SignStyle.ALWAYS) {
            return ~position;
        }
        int minEndPos = position + minWidth;
        if (minEndPos > length) {
            return ~position;
        }
        int maxEndPos = Math.min(position + maxWidth, length);
        long total = 0;  // long to handle large numbers
        int pos = position;
        while (pos < maxEndPos) {
            char ch = parseText.charAt(pos++);
            int digit = context.getSymbols().convertToDigit(ch);
            if (digit < 0) {
                pos--;
                if (pos < minEndPos) {
                    return ~position;  // need at least min width digits
                }
                break;
            }
            total = total * 10 + digit;
        }
        if (negative) {
            if (total == 0) {
                return ~(position - 1);  // minus zero not allowed
            }
            total = -total;
        } else if (signStyle == SignStyle.EXCEEDS_PAD) {
            int parseLen = pos - position;
            if (positive) {
                if (parseLen <= minWidth) {
                    return ~(position - 1);  // '+' only parsed if minWidth exceeded
                }
            } else {
                if (parseLen > minWidth) {
                    return ~position;  // '+' must be parsed if minWidth exceeded
                }
            }
        }
        if (total > Integer.MAX_VALUE || total < Integer.MIN_VALUE) {
            return ~position;  // overflow
        }
        context.setFieldValue(fieldRule, (int) total);
        return pos;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (minWidth == 1 && maxWidth == 10 && signStyle == SignStyle.NORMAL) {
            return "Number(" + fieldRule.getID() + ")";
        }
        if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
            return "Number(" + fieldRule.getID() + "," + minWidth + ")";
        }
        return "Number(" + fieldRule.getID() + "," + minWidth + "," + maxWidth + "," + signStyle + ")";
    }

}
