/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;

/**
 * Prints and parses a numeric date-time field with optional padding.
 * <p>
 * FractionPrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class FractionPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The rule to output, not null.
     */
    private final DateTimeRule rule;
    /**
     * The minimum width, from 0 to 9.
     */
    private final int minWidth;
    /**
     * The maximum width, from 0 to 9.
     */
    private final int maxWidth;

    /**
     * Constructor.
     *
     * @param rule  the rule to output, not null
     * @param minWidth  the minimum width to output, from 0 to 9
     * @param maxWidth  the maximum width to output, from 0 to 9
     */
    FractionPrinterParser(DateTimeRule rule, int minWidth, int maxWidth) {
        DateTimeFormatterBuilder.checkNotNull(rule, "DateTimeRule must not be null");
        if (rule.getValueRange().isFixed() == false) {
            throw new IllegalArgumentException("The rule must have a fixed set of values");
        }
        if (minWidth < 0 || minWidth > 9) {
            throw new IllegalArgumentException("The minimum width must be from 0 to 9 inclusive but was " + minWidth);
        }
        if (maxWidth < 1 || maxWidth > 9) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 9 inclusive but was " + maxWidth);
        }
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " +
                    maxWidth + " < " + minWidth);
        }
        this.rule = rule;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        DateTimeField field = context.getValue(rule);
        if (field == null) {
            return false;
        }
        long value = field.getValue();
        DateTimeFormatSymbols symbols = context.getSymbols();
        BigDecimal fraction = rule.convertToFraction(value);
        if (fraction.scale() == 0) {  // scale is zero if value is zero
            if (minWidth > 0) {
                buf.append(symbols.getDecimalSeparator());
                for (int i = 0; i < minWidth; i++) {
                    buf.append(symbols.getZeroDigit());
                }
            }
        } else {
            int outputScale = Math.min(Math.max(fraction.scale(), minWidth), maxWidth);
            fraction = fraction.setScale(outputScale, RoundingMode.FLOOR);
            String str = fraction.toPlainString().substring(2);
            str = symbols.convertNumberToI18N(str);
            buf.append(symbols.getDecimalSeparator());
            buf.append(str);
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, CharSequence text, int position) {
        int length = text.length();
        if (position == length ||
                text.charAt(position) != context.getSymbols().getDecimalSeparator()) {
            // valid if whole field is optional, invalid if minimum width
            return (minWidth > 0 ? ~position : position);
        }
        position++;
        int minEndPos = position + minWidth;
        if (minEndPos > length) {
            return ~position;  // need at least min width digits
        }
        int maxEndPos = Math.min(position + maxWidth, length);
        int total = 0;  // can use int because we are only parsing up to 9 digits
        int pos = position;
        while (pos < maxEndPos) {
            char ch = text.charAt(pos++);
            int digit = context.getSymbols().convertToDigit(ch);
            if (digit < 0) {
                if (pos < minEndPos) {
                    return ~position;  // need at least min width digits
                }
                pos--;
                break;
            }
            total = total * 10 + digit;
        }
        BigDecimal fraction = new BigDecimal(total).movePointLeft(pos - position);
        long value = rule.convertFromFraction(fraction);
        context.setParsedField(rule, value);
        return pos;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Fraction(" + rule.getName() + "," + minWidth + "," + maxWidth + ")";
    }

}
