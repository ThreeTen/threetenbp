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
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeFieldRule;

/**
 * Prints and parses a numeric date-time field with optional padding.
 *
 * @author Stephen Colebourne
 */
class FractionPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The field to output, not null.
     */
    private final DateTimeFieldRule fieldRule;
    /**
     * The array of widths of length 10.
     * The array index is the scale of the fraction.
     * The array value is the scale to print.
     */
    private final int[] widths;

    /**
     * Constructor.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @param scaleWidths  the valid widths, not null, array size 10, assigned not copied
     */
    FractionPrinterParser(DateTimeFieldRule fieldRule, int[] scaleWidths) {
        // validated by caller
        this.fieldRule = fieldRule;
        widths = scaleWidths;
    }

    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        BigDecimal fraction = fieldRule.getFractionalValue(calendrical);
        int outputScale = widths[fraction.scale()];
        if (fraction.scale() == 0) {
            if (outputScale > 0) {
                appendable.append(symbols.getDecimalPointChar());
                for (int i = 0; i < outputScale; i++) {
                    appendable.append(symbols.getZeroChar());
                }
            }
        } else {
            fraction = fraction.setScale(outputScale, RoundingMode.FLOOR);
            String str = fraction.toPlainString().substring(2);
            if (symbols.getZeroChar() != '0') {
                int diff = symbols.getZeroChar() - '0';
                char[] array = str.toCharArray();
                for (int i = 0; i < array.length; i++) {
                    array[i] = (char) (array[i] + diff);
                }
                str = new String(array);
            }
            appendable.append(symbols.getDecimalPointChar());
            appendable.append(str);
        }
    }

    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
//        int length = parseText.length();
//        if (position == length) {
//            return ~position;
//        }
//        char sign = parseText.charAt(position);  // IOOBE if invalid position
//        boolean negative = false;
//        if (sign == context.getSymbols().getPositiveSignChar()) {
//            switch (signStyle) {
//                case ALWAYS:
//                case EXCEEDS_PAD:
//                    position++;
//                    break;
//                default:
//                    return ~position;
//            }
//        } else if (sign == context.getSymbols().getNegativeSignChar()) {
//            negative = true;
//            switch (signStyle) {
//                case ALWAYS:
//                case EXCEEDS_PAD:
//                case NORMAL:
//                    position++;
//                    break;
//                default:
//                    return ~position;
//            }
//        }
//        int minEndPos = position + minWidth;
//        if (minEndPos > length) {
//            return ~position;
//        }
//        long total = 0;
//        while (position < minEndPos) {
//            char ch = parseText.charAt(position++);
//            int digit = context.getSymbols().convertToDigit(ch);
//            if (digit < 0) {
//                return ~(position - 1);
//            }
//            total *= 10;
//            total += digit;
//        }
//        int maxEndPos = Math.max(position + maxWidth, length);
//        while (position < maxEndPos) {
//            char ch = parseText.charAt(position++);
//            int digit = context.getSymbols().convertToDigit(ch);
//            if (digit < 0) {
//                position--;
//                break;
//            }
//            total *= 10;
//            total += digit;
//        }
//        total = (negative ? -total : total);
//        context.setFieldValue(fieldRule, MathUtils.safeToInt(total));
        return position;
    }

}
