/*
 * Copyright (c) 2010-2011, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.CalendricalException;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;

/**
 * Prints and parses a reduced numeric date-time field.
 * <p>
 * ReducedPrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class ReducedPrinterParser extends NumberPrinterParser {

    /**
     * The base value.
     */
    private final int baseValue;
    /**
     * The range.
     */
    private final int range;

    /**
     * Constructor.
     *
     * @param rule  the rule of the field to print, validated not null
     * @param width  the field width, from 1 to 18
     * @param baseValue  the base value
     */
    ReducedPrinterParser(DateTimeRule rule, int width, int baseValue) {
        super(rule, width, width, SignStyle.NOT_NEGATIVE);
        if (width < 1 || width > 18) {
            throw new IllegalArgumentException("The width must be from 1 to 18 inclusive but was " + width);
        }
        if (rule.getValueRange().isValidValue(baseValue) == false) {
            throw new IllegalArgumentException("The base value must be within the range of the field");
        }
        this.baseValue = baseValue;
        this.range = EXCEED_POINTS[width];
        if ((((long) baseValue) + range) > Integer.MAX_VALUE) {
            throw new CalendricalException("Unable to add printer-parser as the range exceeds the capacity of an int");
        }
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    long getValue(DateTimeField field) {
        long value = field.getValue();
        return Math.abs(value % range);
    }

    //-----------------------------------------------------------------------
    @Override
    void setValue(DateTimeParseContext context, long value) {
        int lastPart = baseValue % range;
        if (baseValue > 0) {
            value = baseValue - lastPart + value;
        } else {
            value = baseValue - lastPart - value;
        }
        if (value < baseValue) {
            value += range;
        }
        context.setParsedField(rule, value);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReducedValue(" + rule.getName() + "," + minWidth + "," + baseValue + ")";
    }

}
