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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.time.calendar.TimeFieldRule;

/**
 * Builder to create formats for dates and times.
 *
 * @author Stephen Colebourne
 */
public class DateTimeFormatterBuilder {

    /**
     * The list of printers that will be used.
     */
    private final List<DateTimePrinter> printers = new ArrayList<DateTimePrinter>();


    //-----------------------------------------------------------------------
    /**
     * Returns the strict zone resolver which rejects all gaps and overlaps
     * as invalid, resulting in an exception.
     *
     * @return the strict resolver, never null
     */
    public static  DateTimeFormatterBuilder builder() {
        return new DateTimeFormatterBuilder();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Appends the value of the ISO year date-time field to the formatter.
//     *
//     * @return this, for chaining, never null
//     */
//    public DateTimeFormatterBuilder appendYear() {
//        appendValue(Year.RULE, 4, Integer.MAX_VALUE, '0', true, SignStyle.EXCEEDS_PAD);
//        return this;
//    }

    //-----------------------------------------------------------------------
    /**
     * Appends the value of a date-time field to the formatter.
     * <p>
     * The value of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendValue(TimeFieldRule fieldRule) {
        if (fieldRule == null) {
            throw new NullPointerException("TimeFieldRule must not be null");
        }
        NumberPrinter printer = new NumberPrinter(fieldRule);
        printers.add(printer);
        return this;
    }

    /**
     * Appends the value of a date-time field to the formatter using a fixed
     * width, zero-padded approach.
     * <p>
     * The value of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop.
     * <p>
     * The value will be zero-padded on the left. If the size of the value
     * means that it cannot be printed within the width then an exception is thrown.
     * If the value of the field is negative then an exception is thrown.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @param width  the width of the printed field
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendValue(TimeFieldRule fieldRule, int width) {
        if (fieldRule == null) {
            throw new NullPointerException("TimeFieldRule must not be null");
        }
        NumberPrinter printer = new NumberPrinter(fieldRule, width, width, '0', true, SignStyle.NEGATIVE_ERROR);
        printers.add(printer);
        return this;
    }

    /**
     * Appends the value of a date-time field to the formatter using a fixed
     * width, zero-padded approach.
     * <p>
     * The value of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop.
     * <p>
     * This method provides full control of the numeric formatting, including
     * padding and the positive/negative sign.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @param minWidth  the minimum field width of the printed field
     * @param maxWidth  the maximum field width of the printed field
     * @param padChar  the padding character
     * @param padOnLeft  whether to left pad (true) or right pad (false)
     * @param signStyle  the postive/negative output style, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendValue(
            TimeFieldRule fieldRule, int minWidth, int maxWidth,
            char padChar, boolean padOnLeft, SignStyle signStyle) {
        if (fieldRule == null) {
            throw new NullPointerException("TimeFieldRule must not be null");
        }
        if (signStyle == null) {
            throw new NullPointerException("NegativeStyle must not be null");
        }
        NumberPrinter printer = new NumberPrinter(fieldRule, minWidth, maxWidth, padChar, padOnLeft, signStyle);
        printers.add(printer);
        return this;
    }

    /**
     * Appends a character literal to the formatter.
     * <p>
     * This character will be output during a print.
     *
     * @param literal  the literal to print, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendLiteral(char literal) {
        printers.add(new CharLiteralPrinter(literal));
        return this;
    }

    /**
     * Appends a string literal to the formatter.
     * <p>
     * This string will be output during a print.
     *
     * @param literal  the literal to print, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendLiteral(String literal) {
        if (literal == null) {
            throw new NullPointerException("String must not be null");
        }
        printers.add(new StringLiteralPrinter(literal));
        return this;
    }

    /**
     * Appends a printer to the formatter.
     * <p>
     * This printer will be called during a print.
     *
     * @param printer  the printer to add, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendPrinter(DateTimePrinter printer) {
        if (printer == null) {
            throw new NullPointerException("DateTimePrinter must not be null");
        }
        printers.add(printer);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Completes this builder by creating the DateTimeFormatter.
     * <p>
     * This builder can still be used after creating the formatter, and the
     * created formatter will be unaffected.
     *
     * @return the created formatter, never null
     */
    public DateTimeFormatter toFormatter() {
        return toFormatter(Locale.getDefault());
    }

    /**
     * Completes this builder by creating the DateTimeFormatter.
     * <p>
     * This builder can still be used after creating the formatter, and the
     * created formatter will be unaffected.
     *
     * @param locale  the locale to use for formatting, not null
     * @return the created formatter, never null
     */
    public DateTimeFormatter toFormatter(Locale locale) {
        return new DateTimeFormatter(locale, false, printers);
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of ways to handle the positive/negative sign.
     *
     * @author Stephen Colebourne
     */
    public enum SignStyle {
        // TODO: I18N: The minus sign varies by locale

        /**
         * Style to never output the negative sign, only outputting the absolute value.
         */
        NEGATIVE_ERROR {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, TimeFieldRule fieldRule, int value, int padWidth) throws IOException {
                if (value < 0) {
                    throw new CalendricalFormatException("The field " + fieldRule.getName() + " cannot be printed as it is negative");
                }
            }
        },
        /**
         * Style to never output the negative sign, only outputting the absolute value.
         */
        ABS {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, TimeFieldRule fieldRule, int value, int padWidth) throws IOException {
            }
        },
        /**
         * Style to output the negative sign only if the value is negative.
         */
        NORMAL {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, TimeFieldRule fieldRule, int value, int padWidth) throws IOException {
                if (value < 0) {
                    appendable.append('-');
                }
            }
        },
        /**
         * Style to always output the sign if the value exceeds the pad width.
         * A negative value will always output the '-' sign.
         */
        EXCEEDS_PAD {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, TimeFieldRule fieldRule, int value, int padWidth) throws IOException {
                if (value < 0) {
                    appendable.append('-');
                } else if (padWidth < 10 && value >= EXCEED_POINTS[padWidth]) {
                    appendable.append('+');
                }
            }
        },
        /**
         * Style to always output the sign.
         * Zero will output '+'.
         */
        ALWAYS {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, TimeFieldRule fieldRule, int value, int padWidth) throws IOException {
                if (value < 0) {
                    appendable.append('-');
                } else {
                    appendable.append('+');
                }
            }
        };

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
         * Prints the sign to the output appendable.
         *
         * @param appendable  the appendable to output to, not null
         * @param fieldRule  the rule of the field to output, not null
         * @param value  the value being output
         * @param padWidth  the pad width
         * @throws IOException if an error occurs
         */
        abstract void print(Appendable appendable, TimeFieldRule fieldRule, int value, int padWidth) throws IOException;

    }

}
