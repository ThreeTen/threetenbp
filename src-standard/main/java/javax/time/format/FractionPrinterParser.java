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

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;

/**
 * Prints and parses a numeric date-time field with optional padding.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class FractionPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The field to output, not null.
     */
    private final DateTimeField field;
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
     * @param field  the field to output, not null
     * @param minWidth  the minimum width to output, from 0 to 9
     * @param maxWidth  the maximum width to output, from 0 to 9
     */
    FractionPrinterParser(DateTimeField field, int minWidth, int maxWidth) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        if (field.getValueRange().isFixed() == false) {
            throw new IllegalArgumentException("Field must have a fixed set of values: " + field.getName());
        }
        if (minWidth < 0 || minWidth > 9) {
            throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + minWidth);
        }
        if (maxWidth < 1 || maxWidth > 9) {
            throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + maxWidth);
        }
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " +
                    maxWidth + " < " + minWidth);
        }
        this.field = field;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        Long value = context.getValue(field);
        if (value == null) {
            return false;
        }
        DateTimeFormatSymbols symbols = context.getSymbols();
        BigDecimal fraction = convertToFraction(value);
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
        long value = convertFromFraction(fraction);
        context.setParsedField(field, value);
        return pos;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts a value for this field to a fraction between 0 and 1.
     * <p>
     * The fractional value is between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if the {@link #getValueRange() value range} is fixed.
     * The fraction is obtained by calculation from the field range using 9 decimal
     * places and a rounding mode of {@link RoundingMode#FLOOR FLOOR}.
     * The calculation is inaccurate if the values do not run continuously from smallest to largest.
     * <p>
     * For example, the second-of-minute value of 15 would be returned as 0.25,
     * assuming the standard definition of 60 seconds in a minute.
     *
     * @param value  the value to convert, must be valid for this rule
     * @return the value as a fraction within the range, from 0 to 1, not null
     * @throws CalendricalRuleException if the value cannot be converted to a fraction
     */
    private BigDecimal convertToFraction(long value) {
        DateTimeValueRange range = field.getValueRange();
        if (range.isFixed() == false) {
            throw new CalendricalException("Unable to obtain fraction as field range is not fixed: " + field.getName());
        }
        range.checkValidValue(value, field);
        BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
        BigDecimal rangeBD = BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE);
        BigDecimal valueBD = BigDecimal.valueOf(value).subtract(minBD);
        BigDecimal fraction = valueBD.divide(rangeBD, 9, RoundingMode.FLOOR);
        // stripTrailingZeros bug
        return fraction.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : fraction.stripTrailingZeros();
    }

    /**
     * Converts a fraction from 0 to 1 for this field to a value.
     * <p>
     * The fractional value must be between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if the {@link #getValueRange() value range} is fixed.
     * The value is obtained by calculation from the field range and a rounding
     * mode of {@link RoundingMode#FLOOR FLOOR}.
     * The calculation is inaccurate if the values do not run continuously from smallest to largest.
     * <p>
     * For example, the fractional second-of-minute of 0.25 would be converted to 15,
     * assuming the standard definition of 60 seconds in a minute.
     *
     * @param fraction  the fraction to convert, not null
     * @return the value of the field, valid for this rule
     * @throws CalendricalException if the value cannot be converted
     */
    private long convertFromFraction(BigDecimal fraction) {
        DateTimeValueRange range = field.getValueRange();
        if (range.isFixed() == false) {
            throw new CalendricalException("Unable to obtain fraction as field range is not fixed: " + field.getName());
        }
        BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
        BigDecimal rangeBD = BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE);
        BigDecimal valueBD = fraction.multiply(rangeBD).setScale(0, RoundingMode.FLOOR).add(minBD);
        long value = valueBD.longValueExact();
        range.checkValidValue(value, field);
        return value;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Fraction(" + field.getName() + "," + minWidth + "," + maxWidth + ")";
    }

}
