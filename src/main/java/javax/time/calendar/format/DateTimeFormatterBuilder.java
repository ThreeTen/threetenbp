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

    /**
     * Constructs a new instance of the builder.
     */
    public DateTimeFormatterBuilder() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the object is not null throwing an exception if it is.
     *
     * @param object  the object to check for null
     * @param description  the description to use in the exception if the object is null
     */
    static void checkNotNull(Object object, String description) {
        if (object == null) {
            throw new NullPointerException("The " + description + " must not be null");
        }
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
     * @param fieldRule  the rule of the field to append, not null
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
     * @param fieldRule  the rule of the field to append, not null
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
     * @param fieldRule  the rule of the field to append, not null
     * @param minWidth  the minimum field width of the printed field, from 1 to 10
     * @param maxWidth  the maximum field width of the printed field, from 1 to 10
     * @param signStyle  the postive/negative output style, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule or sign style is null
     * @throws IllegalArgumentException if the widths are invalid
     */
    public DateTimeFormatterBuilder appendValue(
            DateTimeFieldRule fieldRule, int minWidth, int maxWidth, SignStyle signStyle) {
        checkNotNull(fieldRule, "field rule");
        checkNotNull(signStyle, "sign style");
        if (minWidth < 1 || minWidth > 10) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 10 inclusive but was " + minWidth);
        }
        if (maxWidth < 1 || maxWidth > 10) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 10 inclusive but was " + minWidth);
        }
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " +
                    maxWidth + " < " + minWidth);
        }
        NumberPrinterParser pp = new NumberPrinterParser(fieldRule, minWidth, maxWidth, signStyle);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the text of a date-time field to the formatter using the full
     * text style.
     * <p>
     * The text of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop. If the
     * field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param fieldRule  the rule of the field to append, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendText(DateTimeFieldRule fieldRule) {
        return appendText(fieldRule, TextStyle.FULL);
    }

    /**
     * Appends the text of a date-time field to the formatter.
     * <p>
     * The text of the field will be output during a print. If the value
     * cannot be obtained from the date-time then printing will stop. If the
     * field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param fieldRule  the rule of the field to append, not null
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendText(DateTimeFieldRule fieldRule, TextStyle textStyle) {
        checkNotNull(fieldRule, "field rule");
        checkNotNull(textStyle, "text style");
        TextPrinterParser pp = new TextPrinterParser(fieldRule, textStyle);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the zone offset, such as '+01:00', to the formatter.
     * <p>
     * The zone offset id will be output during a print. If the calendrical
     * has no offset then printing will stop.
     * <p>
     * The output id is minor variation to the standard ISO-8601 format.
     * There are three formats:
     * <ul>
     * <li>'Z' - for UTC (ISO-8601)
     * <li>'&plusmn;hh:mm' - if the seconds are zero (ISO-8601)
     * <li>'&plusmn;hh:mm:ss' - if the seconds are non-zero (not ISO-8601)
     * </ul>
     *
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendOffset() {
        return appendOffset("Z", true, false);
    }

    /**
     * Appends the zone offset, such as '+01:00', to the formatter.
     * <p>
     * The zone offset will be output during a print. If the calendrical
     * has no offset then printing will stop. The output format is controlled
     * by the specified parameters.
     * <p>
     * The utc text controls what text is printed when the offset is zero.
     * Example values would be 'Z', '+00:00', 'UTC' or 'GMT'.
     * <p>
     * The include colon parameter controls whether a colon should separate the
     * numeric fields or not.
     * <p>
     * The exclude seconds parameter controls whether seconds should be excluded
     * or not. If false, seconds are only output if non-zero.
     *
     * @param utcText  the text to use for UTC, not null
     * @param includeColon  whether to include a colon
     * @param excludeSeconds  whether to exclude seconds
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendOffset(String utcText, boolean includeColon, boolean excludeSeconds) {
        checkNotNull(utcText, "UTC text");
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser(utcText, includeColon, excludeSeconds);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the time zone rule id, such as 'Europe/Paris', to the formatter.
     * <p>
     * The time zone id will be output during a print. If the calendrical
     * has no zone then printing will stop.
     *
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendZoneId() {
        ZonePrinterParser pp = new ZonePrinterParser();
        appendInternal(pp, pp);
        return this;
    }

    /**
     * Appends the time zone rule name, such as 'British Summer Time', to the formatter.
     * <p>
     * The time zone name will be output during a print. If the calendrical
     * has no zone then printing will stop.
     *
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle) {
        checkNotNull(textStyle, "text style");
        ZonePrinterParser pp = new ZonePrinterParser(textStyle);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends a character literal to the formatter.
     * <p>
     * This character will be output during a print.
     *
     * @param literal  the literal to append, not null
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
     * @param literal  the literal to append, not null
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder appendLiteral(String literal) {
        checkNotNull(literal, "literal");
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

    //-----------------------------------------------------------------------
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
        checkNotNull(locale, "locale");
        return new DateTimeFormatter(locale, false, printers, parsers);
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of ways to handle the positive/negative sign.
     *
     * @author Stephen Colebourne
     */
    public enum SignStyle {
        /**
         * Style to throw an exception if trying to output a negative value.
         */
        NEGATIVE_ERROR,
        /**
         * Style to never output a sign, only outputting the absolute value.
         */
        NEVER,
        /**
         * Style to output the a sign only if the value is negative.
         */
        NORMAL,
        /**
         * Style to always output the sign if the value exceeds the pad width.
         * A negative value will always output the '-' sign.
         */
        EXCEEDS_PAD,
        /**
         * Style to always output the sign.
         * Zero will output '+'.
         */
        ALWAYS;
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of the style of text output to use.
     *
     * @author Stephen Colebourne
     */
    public enum TextStyle {
        /**
         * Narrow text, typically a single letter.
         */
        NARROW,
        /**
         * Short text, typically an abreviation.
         */
        SHORT,
        /**
         * Full text, typically the full description.
         */
        FULL;
    }

}
