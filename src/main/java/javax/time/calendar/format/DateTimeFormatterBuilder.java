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

import javax.time.calendar.DateTimeFieldRule;

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
    /**
     * The list of parsers that will be used.
     */
    private final List<DateTimeParser> parsers = new ArrayList<DateTimeParser>();
    /**
     * The width to pad the next field to.
     */
    private int padNextWidth;
    /**
     * The character to pad the next field with.
     */
    private char padNextChar;

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

    //-----------------------------------------------------------------------
    /**
     * Appends the value of a date-time field to the formatter using a normal
     * output style.
     * <p>
     * The value of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendValue(DateTimeFieldRule fieldRule) {
        return appendValue(fieldRule, 1, 10, SignStyle.NORMAL);
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
     * @param width  the width of the printed field, from 1 to 10
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendValue(DateTimeFieldRule fieldRule, int width) {
        return appendValue(fieldRule, width, width, SignStyle.NEGATIVE_ERROR);
    }

    /**
     * Appends the value of a date-time field to the formatter providing full
     * control over printing.
     * <p>
     * The value of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop.
     * <p>
     * This method provides full control of the numeric formatting, including
     * padding and the positive/negative sign.
     *
     * @param fieldRule  the rule of the field to print, not null
     * @param minWidth  the minimum field width of the printed field, from 1 to 10
     * @param maxWidth  the maximum field width of the printed field, from 1 to 10
     * @param signStyle  the postive/negative output style, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule or sign style is null
     * @throws IllegalArgumentException if the widths are invalid
     */
    public DateTimeFormatterBuilder appendValue(
            DateTimeFieldRule fieldRule, int minWidth, int maxWidth, SignStyle signStyle) {
        NumberPrinterParser pp = new NumberPrinterParser(fieldRule, minWidth, maxWidth, signStyle);
        appendInternal(pp, pp);
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
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser(literal);
        appendInternal(pp, pp);
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
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser(literal);
        appendInternal(pp, pp);
        return this;
    }

    /**
     * Appends a printer and/or parser to the formatter.
     * <p>
     * If one of the two parameters is null then the formatter will only be able
     * to print or parse. If both are null, an exception is thrown.
     *
     * @param printer  the printer to add, null prevents the formatter from printing
     * @param parser  the parser to add, null prevents the formatter from parsing
     * @return this, for chaining, never null
     * @throws NullPointerException if both printer and parser are null
     */
    public DateTimeFormatterBuilder append(DateTimePrinter printer, DateTimeParser parser) {
        if (printer == null && parser == null) {
            throw new NullPointerException("One of DateTimePrinter or DateTimeParser must be non-null");
        }
        appendInternal(printer, parser);
        return this;
    }

    /**
     * Appends a printer and/or parser to the internal list handling padding.
     *
     * @param printer  the printer to add, null prevents the formatter from printing
     * @param parser  the parser to add, null prevents the formatter from parsing
     * @return this, for chaining, never null
     */
    private DateTimeFormatterBuilder appendInternal(DateTimePrinter printer, DateTimeParser parser) {
        if (padNextWidth > 0) {
            if (printer != null) {
                printer = new PadPrinterDecorator(printer, padNextWidth, padNextChar);
            }
            padNextWidth = 0;
            padNextChar = 0;
        }
        printers.add(printer);
        parsers.add(parser);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Causes the next added printer/parser to pad to a fixed width using a space.
     * <p>
     * This padding will pad to a fixed width using spaces.
     * <p>
     * An exception will be thrown during printing if the pad width
     * is exceeded.
     *
     * @param padWidth  the pad width, 1 or greater
     * @return this, for chaining, never null
     * @throws IllegalArgumentException if pad width is too small
     */
    public DateTimeFormatterBuilder padNext(int padWidth) {
        return padNext(padWidth, ' ');
    }

    /**
     * Causes the next added printer/parser to pad to a fixed width.
     * <p>
     * This padding is intended for padding other than zero-padding.
     * Zero-padding should be achieved using the appendValue methods.
     * <p>
     * An exception will be thrown during printing if the pad width
     * is exceeded.
     *
     * @param padWidth  the pad width, 1 or greater
     * @param padChar  the pad character
     * @return this, for chaining, never null
     * @throws IllegalArgumentException if pad width is too small
     */
    public DateTimeFormatterBuilder padNext(int padWidth, char padChar) {
        if (padWidth < 1) {
            throw new IllegalArgumentException("The pad width must be at least one but was " + padWidth);
        }
        this.padNextWidth = padWidth;
        this.padNextChar = padChar;
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

//    //-----------------------------------------------------------------------
//    /**
//     * Enumeration of ways to pad the output.
//     *
//     * @author Stephen Colebourne
//     */
//    public enum PadStyle {
//        /**
//         * Style to pad before any output.
//         */
//        BEFORE {
//            /** {@inheritDoc} */
//            @Override
//            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
//            }
//        },
//        /**
//         * Style to pad a number being output.
//         */
//        NUMBER {
//            /** {@inheritDoc} */
//            @Override
//            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
//            }
//        },
//        /**
//         * Style to pad after the output.
//         */
//        AFTER {
//            /** {@inheritDoc} */
//            @Override
//            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
//            }
//        };
//
//        /**
//         * Prints the sign to the output appendable.
//         *
//         * @param appendable  the appendable to output to, not null
//         * @param fieldRule  the rule of the field to output, not null
//         * @param value  the value being output
//         * @param padWidth  the pad width
//         * @throws IOException if an error occurs
//         */
//        abstract void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException;
//
//    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of ways to handle the positive/negative sign.
     *
     * @author Stephen Colebourne
     */
    public enum SignStyle {
        // TODO: I18N: The minus sign varies by locale

        /**
         * Style to throw an exception if trying to output a negative value.
         */
        NEGATIVE_ERROR {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
                if (value < 0) {
                    throw new CalendricalFormatFieldException(fieldRule, value);
                }
            }
        },
        /**
         * Style to never output a sign, only outputting the absolute value.
         */
        NEVER {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
            }
        },
        /**
         * Style to output the a sign only if the value is negative.
         */
        NORMAL {
            /** {@inheritDoc} */
            @Override
            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
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
            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
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
            void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException {
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
        abstract void print(Appendable appendable, DateTimeFieldRule fieldRule, int value, int padWidth) throws IOException;

//        /**
//         * Prints the sign to the output appendable.
//         *
//         * @param ch  is the character valid
//         * @throws IOException if an error occurs
//         */
//        abstract boolean isParseValid(char ch);

    }

}
