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
package javax.time.calendar.format;

import java.math.BigInteger;

import javax.time.calendar.DateTimeRule;
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
     * Array of 10 to the power of n.
     */
    static final int[] EXCEED_POINTS = new int[] {
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
     * The rule to output, not null.
     */
    final DateTimeRule rule;
    /**
     * The minimum width allowed, zero padding is used up to this width, from 1 to 19.
     */
    final int minWidth;
    /**
     * The maximum width allowed, from 1 to 19.
     */
    private final int maxWidth;
    /**
     * The positive/negative sign style, not null.
     */
    private final SignStyle signStyle;
    /**
     * The subsequent width of fixed width non-negative number fields, 0 or greater.
     */
    private final int subsequentWidth;

    /**
     * Constructor.
     *
     * @param rule  the rule of the field to print, not null
     * @param minWidth  the minimum field width, from 1 to 19
     * @param maxWidth  the maximum field width, from minWidth to 19
     * @param signStyle  the positive/negative sign style, not null
     */
    NumberPrinterParser(DateTimeRule rule, int minWidth, int maxWidth, SignStyle signStyle) {
        // validated by caller
        this.rule = rule;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.signStyle = signStyle;
        this.subsequentWidth = 0;
    }

    /**
     * Constructor.
     *
     * @param rule  the rule of the field to print, not null
     * @param minWidth  the minimum field width, from 1 to 19
     * @param maxWidth  the maximum field width, from minWidth to 19
     * @param signStyle  the positive/negative sign style, not null
     * @param subsequentWidth  the width of subsequent non-negative numbers, 0 or greater
     */
    private NumberPrinterParser(DateTimeRule rule, int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
        // validated by caller
        this.rule = rule;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.signStyle = signStyle;
        this.subsequentWidth = subsequentWidth;
    }

    /**
     * Returns a new instance with an updated subsequent width.
     *
     * @param subsequentWidth  the width of subsequent non-negative numbers, 0 or greater
     * @return a new updated printer-parser, not null
     */
    NumberPrinterParser withSubsequentWidth(int subsequentWidth) {
        return new NumberPrinterParser(rule, minWidth, maxWidth, signStyle, this.subsequentWidth + subsequentWidth);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(DateTimePrintContext context, StringBuilder buf) {
        long value = getValue(context);
        DateTimeFormatSymbols symbols = context.getSymbols();
        String str = (value == Long.MIN_VALUE ? "9223372036854775808" : Long.toString(Math.abs(value)));
        if (str.length() > maxWidth) {
            throw new CalendricalPrintException("Rule " + rule.getName() +
                " cannot be printed as the value " + value +
                " exceeds the maximum print width of " + maxWidth, rule);
        }
        str = symbols.convertNumberToI18N(str);
        
        if (value >= 0) {
            switch (signStyle) {
                case EXCEEDS_PAD:
                    if (minWidth < 19 && value >= EXCEED_POINTS[minWidth]) {
                        buf.append(symbols.getPositiveSign());
                    }
                    break;
                case ALWAYS:
                    buf.append(symbols.getPositiveSign());
                    break;
            }
        } else {
            switch (signStyle) {
                case NORMAL:
                case EXCEEDS_PAD:
                case ALWAYS:
                    buf.append(symbols.getNegativeSign());
                    break;
                case NOT_NEGATIVE:
                    throw new CalendricalPrintException("Rule " + rule.getName() +
                        " cannot be printed as the value " + value +
                        " cannot be negative according to the SignStyle", rule);
            }
        }
        for (int i = 0; i < minWidth - str.length(); i++) {
            buf.append(symbols.getZeroDigit());
        }
        buf.append(str);
    }

    /**
     * Gets the value to output.
     * 
     * @param context  the context, not null
     * @return the value
     */
    long getValue(DateTimePrintContext context) {
        return context.getValueChecked(rule).getValue();
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
        if (sign == context.getSymbols().getPositiveSign()) {
            positive = true;
            switch (signStyle) {
                case ALWAYS:
                case EXCEEDS_PAD:
                    position++;
                    break;
                default:
                    if (context.isStrict() || (signStyle != SignStyle.NORMAL && minWidth == maxWidth)) {
                        return ~position;
                    }
                    position++;
                    break;
            }
        } else if (sign == context.getSymbols().getNegativeSign()) {
            negative = true;
            switch (signStyle) {
                case ALWAYS:
                case EXCEEDS_PAD:
                case NORMAL:
                    position++;
                    break;
                default:
                    if (context.isStrict() || minWidth == maxWidth) {
                        return ~position;
                    }
                    position++;
                    break;
            }
        } else {
            if (signStyle == SignStyle.ALWAYS && context.isStrict()) {
                return ~position;
            }
        }
        int minEndPos = position + minWidth;
        if (minEndPos > length) {
            return ~position;
        }
        int effMaxWidth = maxWidth + subsequentWidth;
        long total = 0;
        BigInteger totalBig = null;
        int pos = position;
        for (int pass = 0; pass < 2; pass++) {
            int maxEndPos = Math.min(pos + effMaxWidth, length);
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
                if ((pos - position) > 18) {
                    if (totalBig == null) {
                        totalBig = BigInteger.valueOf(total);
                    }
                    totalBig = totalBig.multiply(BigInteger.TEN).add(BigInteger.valueOf(digit));
                } else {
                    total = total * 10 + digit;
                }
            }
            if (subsequentWidth > 0 && pass == 0) {
                // re-parse now we know the correct width
                int parseLen = pos - position;
                effMaxWidth = Math.max(minWidth, parseLen - subsequentWidth);
                pos = position;
                total = 0;
                totalBig = null;
            } else {
                break;
            }
        }
        if (negative) {
            if (totalBig != null) {
                if (totalBig.equals(BigInteger.ZERO) && context.isStrict()) {
                    return ~(position - 1);  // minus zero not allowed
                }
                totalBig = totalBig.negate();
            } else {
                if (total == 0 && context.isStrict()) {
                    return ~(position - 1);  // minus zero not allowed
                }
                total = -total;
            }
        } else if (signStyle == SignStyle.EXCEEDS_PAD && context.isStrict()) {
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
        if (totalBig != null) {
            if (totalBig.bitLength() > 63) {
                // overflow, parse 1 less digit
                totalBig = totalBig.divide(BigInteger.TEN);
                pos--;
            }
            setValue(context, totalBig.longValue());
        } else {
            setValue(context, total);
        }
        return pos;
    }

    /**
     * Stores the value.
     * 
     * @param context  the context to store into, not null
     * @param value  the value
     */
    void setValue(DateTimeParseContext context, long value) {
        context.setParsedField(rule, value);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (minWidth == 1 && maxWidth == 19 && signStyle == SignStyle.NORMAL) {
            return "Value(" + rule.getID() + ")";
        }
        if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
            return "Value(" + rule.getID() + "," + minWidth + ")";
        }
        return "Value(" + rule.getID() + "," + minWidth + "," + maxWidth + "," + signStyle + ")";
    }

}
