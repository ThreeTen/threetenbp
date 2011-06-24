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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.ISODateTimeRule;
import javax.time.calendar.ZoneId;
import javax.time.calendar.ZoneOffset;

/**
 * Builder to create formatters for calendricals.
 * <p>
 * All date-time formatters are created ultimately using this builder.
 * Each consists of two halves a {@link DateTimePrinter printer} and a {@link DateTimeParser parser}.
 * Most of the methods will create both a printer and a parser automatically, however
 * it is possible to create a formatter that only prints or only parses.
 * <p>
 * The basic elements of calendricals can all be added:
 * <ul>
 * <li>Value - a numeric value</li>
 * <li>Fraction - a fractional value including the decimal place. Always use this when
 * outputting fractions to ensure that the fraction is parsed correctly</li>
 * <li>Text - the textual equivalent for the value</li>
 * <li>OffsetId/Offset - the {@link ZoneOffset zone offset}</li>
 * <li>ZoneId - the {@link ZoneId time-zone} id</li>
 * <li>ZoneText - the name of the time-zone</li>
 * <li>Literal - a text literal</li>
 * <li>Nested and Optional - formats can be nested or made optional</li>
 * <li>Other - the printer and parser interfaces can be used to add user supplied formatting</li>
 * </ul>
 * In addition, any of the elements may be decorated by padding, either with spaces or any other character.
 * <p>
 * Finally, a shorthand pattern, mostly compatible with {@code SimpleDateFormat}
 * can be used, see {@link #appendPattern(String)}.
 * In practice, this simply parses the pattern and calls other methods on the builder.
 *
 * @author Stephen Colebourne
 */
public final class DateTimeFormatterBuilder {

    /**
     * The currently active builder, used by the outermost builder.
     */
    private DateTimeFormatterBuilder active = this;
    /**
     * The parent builder, null for the outermost builder.
     */
    private final DateTimeFormatterBuilder parent;
    /**
     * The list of printers that will be used.
     */
    private final List<DateTimePrinter> printers = new ArrayList<DateTimePrinter>();
    /**
     * The list of parsers that will be used.
     */
    private final List<DateTimeParser> parsers = new ArrayList<DateTimeParser>();
    /**
     * Whether this builder produces an optional formatter.
     */
    private final boolean optional;
    /**
     * The width to pad the next field to.
     */
    private int padNextWidth;
    /**
     * The character to pad the next field with.
     */
    private char padNextChar;
    /**
     * The index of the last variable width value parser.
     */
    private int valueParserIndex = -1;

    /**
     * Constructs a new instance of the builder.
     */
    public DateTimeFormatterBuilder() {
        super();
        parent = null;
        optional = false;
    }

    /**
     * Constructs a new instance of the builder.
     *
     * @param parent  the parent builder, not null
     * @param optional  whether the formatter is optional, not null
     */
    private DateTimeFormatterBuilder(DateTimeFormatterBuilder parent, boolean optional) {
        super();
        this.parent = parent;
        this.optional = optional;
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Changes the parse style to be case sensitive for the remainder of the formatter.
     * <p>
     * Parsing can be case sensitive or insensitive - by default it is case sensitive.
     * This controls how text is compared.
     * <p>
     * When used, this method changes the parsing to be case sensitive from this point onwards.
     * As case sensitive is the default, this is normally only needed after calling {@link #parseCaseInsensitive()}.
     * The change will remain in force until the end of the formatter that is eventually
     * constructed or until {@code parseCaseInsensitive} is called.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder parseCaseSensitive() {
        appendInternal(CaseSensitivePrinterParser.SENSITIVE, CaseSensitivePrinterParser.SENSITIVE);
        return this;
    }

    /**
     * Changes the parse style to be case insensitive for the remainder of the formatter.
     * <p>
     * Parsing can be case sensitive or insensitive - by default it is case sensitive.
     * This controls how text is compared.
     * <p>
     * When used, this method changes the parsing to be case insensitive from this point onwards.
     * The change will remain in force until the end of the formatter that is eventually
     * constructed or until {@code parseCaseSensitive} is called.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder parseCaseInsensitive() {
        appendInternal(CaseSensitivePrinterParser.INSENSITIVE, CaseSensitivePrinterParser.INSENSITIVE);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Changes the parse style to be strict for the remainder of the formatter.
     * <p>
     * Parsing can be strict or lenient - by default its strict.
     * This controls the degree of flexibility in matching the text and sign styles.
     * <p>
     * When used, this method changes the parsing to be strict from this point onwards.
     * As strict is the default, this is normally only needed after calling {@link #parseLenient()}.
     * The change will remain in force until the end of the formatter that is eventually
     * constructed or until {@code parseLenient} is called.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder parseStrict() {
        appendInternal(StrictLenientPrinterParser.STRICT, StrictLenientPrinterParser.STRICT);
        return this;
    }

    /**
     * Changes the parse style to be lenient for the remainder of the formatter.
     * Note that case sensitivity is set separately to this method.
     * <p>
     * Parsing can be strict or lenient - by default its strict.
     * This controls the degree of flexibility in matching the text and sign styles.
     * Applications calling this method should typically also call {@link #parseCaseInsensitive()}.
     * <p>
     * When used, this method changes the parsing to be strict from this point onwards.
     * The change will remain in force until the end of the formatter that is eventually
     * constructed or until {@code parseStrict} is called.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder parseLenient() {
        appendInternal(StrictLenientPrinterParser.LENIENT, StrictLenientPrinterParser.LENIENT);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the value of a date-time field to the formatter using a normal
     * output style.
     * <p>
     * The value of the field will be output during a print.
     * If the value cannot be obtained then an exception will be thrown.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     * <p>
     * The parser for a variable width value such as this normally behaves greedily, accepting as many
     * digits as possible. This behavior can be affected by 'adjacent value parsing'.
     * See {@link #appendValue(DateTimeRule, int)} for full details.
     *
     * @param rule  the rule of the field to append, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendValue(DateTimeRule rule) {
        checkNotNull(rule, "DateTimeRule must not be null");
        NumberPrinterParser pp = new NumberPrinterParser(rule, 1, 19, SignStyle.NORMAL);
        active.valueParserIndex = appendInternal(pp, pp);
        return this;
    }

    /**
     * Appends the value of a date-time field to the formatter using a fixed
     * width, zero-padded approach.
     * <p>
     * The value of the field will be output during a print.
     * If the value cannot be obtained then an exception will be thrown.
     * <p>
     * The value will be zero-padded on the left. If the size of the value
     * means that it cannot be printed within the width then an exception is thrown.
     * If the value of the field is negative then an exception is thrown during printing.
     * <p>
     * This method supports a special technique of parsing known as 'adjacent value parsing'.
     * This technique solves the problem where a variable length value is followed by one or more
     * fixed length values. The standard parser is greedy, and thus it would normally
     * steal the digits that are needed by the fixed width value parsers that follow the
     * variable width one.
     * <p>
     * No action is required to initiate 'adjacent value parsing'.
     * When a call to {@code appendValue} with a variable width is made, the builder
     * enters adjacent value parsing setup mode. If the immediately subsequent method
     * call or calls on the same builder are to this method, then the parser will reserve
     * space so that the fixed width values can be parsed.
     * <p>
     * For example, consider {@code builder.appendValue(yearRule).appendValue(monthRule, 2);}
     * The year is a variable width parse of between 1 and 19 digits.
     * The month is a fixed width parse of 2 digits.
     * Because these were appended to the same builder immediately after one another,
     * the year parser will reserve two digits for the month to parse.
     * Thus, the text '200906' will correctly parse to a year of 2009 and a month of 6.
     * Without adjacent value parsing, the year would greedily parse all six digits and leave
     * nothing for the month.
     * <p>
     * Adjacent value parsing applies to each set of fixed width not-negative values in the parser
     * that immediately follow any kind of variable width value.
     * Calling any other append method will end the setup of adjacent value parsing.
     * Thus, in the unlikely event that you need to avoid adjacent value parsing behavior,
     * simply add the {@code appendValue} to another {@code DateTimeFormatterBuilder}
     * and add that to this builder.
     * <p>
     * If the four-parameter version of {@code appendValue} is called with equal minimum
     * and maximum widths and a sign style of not-negative then it delegates to this method.
     *
     * @param rule  the rule of the field to append, not null
     * @param width  the width of the printed field, from 1 to 19
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the width is invalid
     */
    public DateTimeFormatterBuilder appendValue(DateTimeRule rule, int width) {
        checkNotNull(rule, "DateTimeRule must not be null");
        if (width < 1 || width > 19) {
            throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + width);
        }
        NumberPrinterParser pp = new NumberPrinterParser(rule, width, width, SignStyle.NOT_NEGATIVE);
        return appendFixedWidth(width, pp);
    }

    /**
     * Appends the value of a date-time field to the formatter providing full
     * control over printing.
     * <p>
     * The value of the field will be output during a print.
     * If the value cannot be obtained then an exception will be thrown.
     * <p>
     * This method provides full control of the numeric formatting, including
     * zero-padding and the positive/negative sign.
     * <p>
     * The parser for a variable width value normally behaves greedily, accepting as many
     * digits as possible. This behavior can be affected by 'adjacent value parsing'.
     * See {@link #appendValue(DateTimeRule, int)} for full details.
     *
     * @param rule  the rule of the field to append, not null
     * @param minWidth  the minimum field width of the printed field, from 1 to 19
     * @param maxWidth  the maximum field width of the printed field, from 1 to 19
     * @param signStyle  the positive/negative output style, not null
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the widths are invalid
     */
    public DateTimeFormatterBuilder appendValue(
            DateTimeRule rule, int minWidth, int maxWidth, SignStyle signStyle) {
        if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
            return appendValue(rule, maxWidth);
        }
        checkNotNull(rule, "DateTimeRule must not be null");
        checkNotNull(signStyle, "SignStyle must not be null");
        if (minWidth < 1 || minWidth > 19) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + minWidth);
        }
        if (maxWidth < 1 || maxWidth > 19) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + maxWidth);
        }
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " +
                    maxWidth + " < " + minWidth);
        }
        NumberPrinterParser pp = new NumberPrinterParser(rule, minWidth, maxWidth, signStyle);
        if (minWidth == maxWidth) {
            appendInternal(pp, pp);
        } else {
            active.valueParserIndex = appendInternal(pp, pp);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the reduced value of a date-time field to the formatter.
     * <p>
     * This is typically used for printing and parsing a two digit year.
     * The {@code width} is the printed and parsed width.
     * The {@code baseValue} is used during parsing to determine the valid range.
     * <p>
     * For printing, the width is used to determine the number of characters to print.
     * The rightmost characters are output to match the width, left padding with zero.
     * <p>
     * For parsing, exactly the number of characters specified by the width are parsed.
     * This is incomplete information however, so the base value is used to complete the parse.
     * The base value is the first valid value in a range of ten to the power of width.
     * <p>
     * For example, a base value of {@code 1980} and a width of {@code 2} will have
     * valid values from {@code 1980} to {@code 2079}.
     * During parsing, the text {@code "12"} will result in the value {@code 2012} as that
     * is the value within the range where the last two digits are "12".
     * <p>
     * This is a fixed width parser operating using 'adjacent value parsing'.
     * See {@link #appendValue(DateTimeRule, int)} for full details.
     *
     * @param rule  the rule of the field to append, not null
     * @param width  the width of the printed and parsed field, from 1 to 18
     * @param baseValue  the base value of the range of valid values
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the width or base value is invalid
     */
    public DateTimeFormatterBuilder appendValueReduced(
            DateTimeRule rule, int width, int baseValue) {
        checkNotNull(rule, "DateTimeRule must not be null");
        ReducedPrinterParser pp = new ReducedPrinterParser(rule, width, baseValue);
        appendFixedWidth(width, pp);
        return this;
    }

    /**
     * Appends a fixed width printer-parser.
     * 
     * @param width  the width
     * @param pp  the printer-parser, not null
     * @return this, for chaining, not null
     */
    private DateTimeFormatterBuilder appendFixedWidth(int width, NumberPrinterParser pp) {
        if (active.valueParserIndex >= 0) {
            NumberPrinterParser basePP = (NumberPrinterParser) active.printers.get(active.valueParserIndex);
            basePP = basePP.withSubsequentWidth(width);
            int activeValueParser = active.valueParserIndex;
            active.printers.set(active.valueParserIndex, basePP);
            active.parsers.set(active.valueParserIndex, basePP);
            appendInternal(pp, pp);
            active.valueParserIndex = activeValueParser;
        } else {
            appendInternal(pp, pp);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the fractional value of a date-time field to the formatter.
     * <p>
     * The fractional value of the field will be output including the
     * preceeding decimal point. The preceeding value is not output.
     * The fraction is obtained using {@link DateTimeRule#convertToFraction}.
     * <p>
     * The width of the output fraction can be controlled. Setting the
     * minimum width to zero will cause no output to be generated.
     * The output fraction will have the minimum width necessary between
     * the minimum and maximum widths - trailing zeroes are omitted.
     * No rounding occurs due to the maximum width - digits are simply dropped.
     * <p>
     * If the value cannot be obtained then an exception will be thrown.
     * If the value is negative an exception will be thrown.
     * If the field does not have a fixed set of valid values then an
     * exception will be thrown.
     * If the field value in the calendrical to be printed is invalid it
     * cannot be printed and an exception will be thrown.
     *
     * @param rule  the rule of the field to append, not null
     * @param minWidth  the minimum width of the field excluding the decimal point, from 0 to 9
     * @param maxWidth  the maximum width of the field excluding the decimal point, from 1 to 9
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the field has a variable set of valid values
     * @throws IllegalArgumentException if either width is invalid
     */
    public DateTimeFormatterBuilder appendFraction(
            DateTimeRule rule, int minWidth, int maxWidth) {
        FractionPrinterParser pp = new FractionPrinterParser(rule, minWidth, maxWidth);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the text of a date-time field to the formatter using the full
     * text style.
     * <p>
     * The text of the field will be output during a print.
     * The value must be within the valid range of the rule.
     * If the value cannot be obtained then an exception will be thrown.
     * If the field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param rule  the rule of the field to append, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendText(DateTimeRule rule) {
        return appendText(rule, TextStyle.FULL);
    }

    /**
     * Appends the text of a date-time field to the formatter.
     * <p>
     * The text of the field will be output during a print.
     * The value must be within the valid range of the rule.
     * If the value cannot be obtained then an exception will be thrown.
     * If the field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param rule  the rule of the field to append, not null
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendText(DateTimeRule rule, TextStyle textStyle) {
        checkNotNull(rule, "DateTimeRule must not be null");
        checkNotNull(textStyle, "TextStyle must not be null");
        TextPrinterParser pp = new TextPrinterParser(rule, textStyle);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the zone offset, such as '+01:00', to the formatter.
     * <p>
     * The zone offset id will be output during a print.
     * If the offset cannot be obtained then an exception will be thrown.
     * The format is defined by {@link ZoneOffset#getID()}.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendOffsetId() {
        return appendOffset("Z", "+HH:MM:ss");
    }

    /**
     * Appends the zone offset, such as '+01:00', to the formatter.
     * <p>
     * The zone offset will be output during a print.
     * If the offset cannot be obtained then an exception will be thrown.
     * <p>
     * The output format is controlled by a pattern which must be one of the following:
     * <ul>
     * <li>{@code +HH} - hour only, truncating any minute
     * <li>{@code +HHMM} - hour and minute, no colon
     * <li>{@code +HH:MM} - hour and minute, with colon
     * <li>{@code +HHMMss} - hour and minute, with second if non-zero and no colon
     * <li>{@code +HH:MM:ss} - hour and minute, with second if non-zero and colon
     * <li>{@code +HHMMSS} - hour, minute and second, no colon
     * <li>{@code +HH:MM:SS} - hour, minute and second, with colon
     * </ul>
     * <p>
     * The "no offset" text controls what text is printed when the offset is zero.
     * Example values would be 'Z', '+00:00', 'UTC' or 'GMT'.
     * <p>
     * The include colon parameter controls whether a colon should separate the
     * numeric fields or not.
     * <p>
     * The allow seconds parameter controls whether seconds may be output.
     * If false then seconds are never output.
     * If true then seconds are only output if non-zero.
     *
     * @param noOffsetText  the text to use when the offset is zero, not null
     * @param pattern  the pattern to use
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendOffset(String noOffsetText, String pattern) {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser(noOffsetText, pattern);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the time-zone rule id, such as 'Europe/Paris', to the formatter.
     * <p>
     * The time-zone id will be output during a print.
     * If the zone cannot be obtained then an exception will be thrown.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendZoneId() {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser();
        appendInternal(pp, pp);
        return this;
    }

    /**
     * Appends the time-zone rule name, such as 'British Summer Time', to the formatter.
     * <p>
     * The time-zone name will be output during a print.
     * If the zone cannot be obtained then an exception will be thrown.
     * <p>
     * The zone name is obtained from the formatting symbols.
     * Different names may be output depending on whether daylight savings time applies.
     * <p>
     * If the date, time or offset cannot be obtained it may not be possible to
     * determine which text to output. In this case, the text representing time
     * without daylight savings (winter time) will be used.
     *
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle) {
        checkNotNull(textStyle, "TextStyle must not be null");
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(textStyle);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends a localized date-time pattern to the formatter.
     * <p>
     * The pattern is resolved lazily using the locale being used during the print/parse
     * (stored in {@link DateTimeFormatter}.
     * <p>
     * The pattern can vary by chronology, although typically it doesn't.
     * This method uses the standard ISO chronology patterns.
     *
     * @param dateStyle  the date style to use, null means no date required
     * @param timeStyle  the time style to use, null means no time required
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendLocalized(FormatStyle dateStyle, FormatStyle timeStyle) {
        return appendLocalized(dateStyle, timeStyle, ISOChronology.INSTANCE);
    }

    /**
     * Appends a localized date-time pattern to the formatter.
     * <p>
     * The pattern is resolved lazily using the locale being used during the print/parse
     * (stored in {@link DateTimeFormatter}.
     * <p>
     * The pattern can vary by chronology, although typically it doesn't.
     * This method allows the chronology to be specified.
     *
     * @param dateStyle  the date style to use, null means no date required
     * @param timeStyle  the time style to use, null means no time required
     * @param chronology  the chronology to use, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendLocalized(FormatStyle dateStyle, FormatStyle timeStyle, Chronology chronology) {
        checkNotNull(chronology, "Chronology must not be null");
        if (dateStyle != null || timeStyle != null) {
            LocalizedPrinterParser pp = new LocalizedPrinterParser(dateStyle, timeStyle, chronology);
            appendInternal(pp, pp);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends a character literal to the formatter.
     * <p>
     * This character will be output during a print.
     *
     * @param literal  the literal to append, not null
     * @return this, for chaining, not null
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
     * <p>
     * If the literal is empty, nothing is added to the formatter.
     *
     * @param literal  the literal to append, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendLiteral(String literal) {
        checkNotNull(literal, "Literal text must not be null");
        if (literal.length() > 0) {
            if (literal.length() == 1) {
                CharLiteralPrinterParser pp = new CharLiteralPrinterParser(literal.charAt(0));
                appendInternal(pp, pp);
            } else {
                StringLiteralPrinterParser pp = new StringLiteralPrinterParser(literal);
                appendInternal(pp, pp);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends a printer and/or parser to the formatter.
     * <p>
     * If one of the two parameters is null then the formatter will only be able
     * to print or parse. If both are null, an exception is thrown.
     *
     * @param printer  the printer to add, null prevents the formatter from printing
     * @param parser  the parser to add, null prevents the formatter from parsing
     * @return this, for chaining, not null
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
     * Appends all the elements of a formatter to the builder.
     * <p>
     * This method has the same effect as appending each of the constituent
     * parts of the formatter directly to this builder.
     *
     * @param formatter  the formatter to add, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder append(DateTimeFormatter formatter) {
        checkNotNull(formatter, "DateTimeFormatter must not be null");
        CompositePrinterParser cpp = formatter.toPrinterParser(false);
        appendInternal(cpp, cpp);
        return this;
    }

    /**
     * Appends a formatter to the builder which will optionally print/parse.
     * <p>
     * This method has the same effect as appending each of the constituent
     * parts directly to this builder surrounded by an {@link #optionalStart()} and
     * {@link #optionalEnd()}.
     * <p>
     * The formatter will print if data is available for all the fields contained within it.
     * The formatter will parse if the string matches, otherwise no error is returned.
     *
     * @param formatter  the formatter to add, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendOptional(DateTimeFormatter formatter) {
        checkNotNull(formatter, "DateTimeFormatter must not be null");
        CompositePrinterParser cpp = formatter.toPrinterParser(true);
        appendInternal(cpp, cpp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the elements defined by the specified pattern to the builder.
     * <p>
     * All letters 'A' to 'Z' and 'a' to 'z' are reserved as pattern letters.
     * The following pattern letters are defined:
     * <pre>
     *  Symbol  Meaning                     Presentation      Examples
     *  ------  -------                     ------------      -------
     *   y       year                        year              2004; 04
     *   D       day-of-year                 number            189
     *   M       month-of-year               number/text       7; 07; Jul; July; J
     *   d       day-of-month                number            10
     *
     *   Q       quarter-of-year             number/text       3; 03; Q3
     *   q       month-of-quarter            number            2
     *
     *   Y       week-based-year             year              1996; 96
     *   w       week-of-week-based-year     number            27
     *   E       day-of-week                 number/text       2; Tue; Tuesday; T
     *   F       week-of-month               number            3
     *
     *   a       am-pm-of-day                text              PM
     *   h       clock-hour-of-am-pm (1-12)  number            12
     *   K       hour-of-am-pm (0-11)        number/fraction   0
     *   k       clock-hour-of-am-pm (1-24)  number            0
     *
     *   H       hour-of-day (0-23)          number/fraction   0
     *   m       minute-of-hour              number/fraction   30
     *   s       second-of-minute            number/fraction   55
     *   S       milli-of-second             number/fraction   978
     *   A       milli-of-day                number/fraction   1234
     *   n       nano-of-second              number/fraction   987654321
     *   N       nano-of-day                 number/fraction   1234000000
     *
     *   I       time-zone ID                zoneID            America/Los_Angeles
     *   z       time-zone name              text              Pacific Standard Time; PST
     *   Z       zone-offset                 offset-Z          +0000; -0800; -08:00;
     *   X       zone-offset 'Z' for zero    offset-X          Z; -0800; -08:00;
     *
     *   f       make next a fraction        fraction modifier .123
     *   p       pad next                    pad modifier      1
     *
     *   '       escape for text             delimiter
     *   ''      single quote                literal           '
     *   [       optional section start
     *   ]       optional section end
     * </pre>
     * <p>
     * The count of pattern letters determine the format.
     * <p>
     * <b>Text</b>: The text style is determined based on the number of pattern letters used.
     * Less than 4 pattern letters will use the {@link TextStyle#SHORT short form}.
     * Exactly 4 pattern letters will use the {@link TextStyle#FULL full form}.
     * Exactly 5 pattern letters will use the {@link TextStyle#NARROW narrow form}.
     * <p>
     * <b>Number</b>: If the count of letters is one, then the value is printed using the minimum number
     * of digits and without padding as per {@link #appendValue(DateTimeRule)}. Otherwise, the
     * count of digits is used as the width of the output field as per {@link #appendValue(DateTimeRule, int)}.
     * <p>
     * <b>Number/Text</b>: If the count of pattern letters is 3 or greater, use the Text rules above.
     * Otherwise use the Number rules above.
     * <p>
     * <b>Fraction modifier</b>: Modifies the pattern that immediately follows to be a fraction.
     * All fractional values must use the 'f' prefix to ensure correct parsing.
     * The fraction also outputs the decimal point.
     * If the count of 'f' is one, then the fractional value has the exact number of digits defined by
     * the count of the value being output.
     * If the count of 'f' is two or more, then the fractional value has the a minimum number of digits
     * defined by the count of the value being output and a maximum output of nine digits.
     * <p>
     * For example, 'ssffnnn' outputs the second followed by 3-9 digits of the nanosecond, while
     * 'mmfss' outputs the minute followed by exactly 2 digits representing the second.
     * <p>
     * <b>Year</b>: The count of letters determines the minimum field width below which padding is used.
     * If the count of letters is two, then a {@link #appendValueReduced reduced} two digit form is used.
     * For printing, this outputs the rightmost two digits. For parsing, this will parse using the
     * base value of 2000, resulting in a year within the range 2000 to 2099 inclusive.
     * If the count of letters is less than four (but not two), then the sign is only output for negative
     * years as per {@link SignStyle#NORMAL}.
     * Otherwise, the sign is output if the pad width is exceeded, as per {@link SignStyle#EXCEEDS_PAD}
     * <p>
     * <b>ZoneID</b>: 'I' outputs the zone id, such as 'Europe/Paris'.
     * <p>
     * <b>Offset X</b>: This formats the offset using 'Z' when the offset is zero.
     * One letter outputs just the hour', such as '+01'
     * Two letters outputs the hour and minute, without a colon, such as '+0130'.
     * Three letters outputs the hour and minute, with a colon, such as '+01:30'.
     * Four letters outputs the hour and minute and optional second, without a colon, such as '+013015'.
     * Five letters outputs the hour and minute and optional second, with a colon, such as '+01:30:15'.
     * <p>
     * <b>Offset Z</b>: This formats the offset using '+0000' or '+00:00' when the offset is zero.
     * One or two letters outputs the hour and minute, without a colon, such as '+0130'.
     * Three letters outputs the hour and minute, with a colon, such as '+01:30'.
     * <p>
     * <b>Zone names</b>: Time zone names ('z') cannot be parsed.
     * <p>
     * <b>Optional section</b>: The optional section markers work exactly like calling {@link #optionalStart()}
     * and {@link #optionalEnd()}.
     * <p>
     * <b>Pad modifier</b>: Modifies the pattern that immediately follows to be padded with spaces.
     * The pad width is determined by the number of pattern letters.
     * This is the same as calling {@link #padNext(int)}.
     * <p>
     * For example, 'ppH' outputs the hour-of-day padded on the left with spaces to a width of 2.
     * <p>
     * Any unrecognized letter is an error.
     * Any non-letter character, other than '[', ']' and the single quote will be output directly.
     * Despite this, it is recommended to use single quotes around all characters that you want to
     * output directly to ensure that future changes do not break your application.
     * <p>
     * The pattern string is similar, but not identical, to {@link SimpleDateFormat}.
     * Pattern letters 'E' and 'u' are merged.
     * Pattern letters 'G' and 'W' are not available.
     * Pattern letters 'Z' and 'X' are extended.
     * Pattern letter 'y' and 'Y' parse years of two digits and more than 4 digits differently.
     * Pattern letters 'Q', 'q', 'n', 'A', 'N', 'I', 'f' and 'p' are added.
     * Number types will reject large numbers.
     * The pattern is also similar, but not identical, to that defined by the
     * Unicode Common Locale Data Repository.
     *
     * @param pattern  the pattern to add, not null
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the pattern is invalid
     */
    public DateTimeFormatterBuilder appendPattern(String pattern) {
        checkNotNull(pattern, "Pattern must not be null");
        parsePattern(pattern);
        return this;
    }

    private void parsePattern(String pattern) {
        for (int pos = 0; pos < pattern.length(); pos++) {
            char cur = pattern.charAt(pos);
            if ((cur >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z')) {
                int start = pos++;
                for ( ; pos < pattern.length() && pattern.charAt(pos) == cur; pos++);  // short loop
                int count = pos - start;
                // padding
                if (cur == 'p') {
                    int pad = 0;
                    if (pos < pattern.length()) {
                        cur = pattern.charAt(pos);
                        if ((cur >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z')) {
                            pad = count;
                            start = pos++;
                            for ( ; pos < pattern.length() && pattern.charAt(pos) == cur; pos++);  // short loop
                            count = pos - start;
                        }
                    }
                    if (pad == 0) {
                        throw new IllegalArgumentException(
                                "Pad letter 'p' must be followed by valid pad pattern: " + pattern);
                    }
                    padNext(pad); // pad and continue parsing
                }
                // fractions
                int fraction = 0;
                if (cur == 'f') {
                    if (pos < pattern.length()) {
                        cur = pattern.charAt(pos);
                        if (cur == 'H' || cur == 'K' || cur == 'm' || cur == 's' || cur == 'S' || cur == 'A' || cur == 'n' || cur == 'N') {
                            fraction = count;
                            start = pos++;
                            for ( ; pos < pattern.length() && pattern.charAt(pos) == cur; pos++);  // short loop
                            count = pos - start;
                        }
                    }
                    if (fraction == 0) {
                        throw new IllegalArgumentException(
                                "Fraction letter 'f' must be followed by valid fraction pattern: " + pattern);
                    }
                }
                // main rules
                DateTimeRule rule = RULE_MAP.get(cur);
                if (rule != null) {
                    parseRule(cur, count, rule, fraction);
                } else if (cur == 'z') {
                    if (count < 4) {
                        appendZoneText(TextStyle.SHORT);
                    } else {
                        appendZoneText(TextStyle.FULL);
                    }
                } else if (cur == 'I') {
                    appendZoneId();
                } else if (cur == 'Z') {
                    if (count > 3) {
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                    }
                    if (count < 3) {
                        appendOffset("+0000", "+HHMM");
                    } else {
                        appendOffset("+00:00", "+HH:MM");
                    }
                } else if (cur == 'X') {
                    if (count > 5) {
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                    }
                    appendOffset("Z", ZoneOffsetPrinterParser.PATTERNS[count - 1]);
                } else {
                    throw new IllegalArgumentException("Unknown pattern letter: " + cur);
                }
                fraction = 0;
                pos--;
                
            } else if (cur == '\'') {
                // parse literals
                int start = pos++;
                for ( ; pos < pattern.length(); pos++) {
                    if (pattern.charAt(pos) == '\'') {
                        if (pos + 1 < pattern.length() && pattern.charAt(pos + 1) == '\'') {
                            pos++;
                        } else {
                            break;  // end of literal
                        }
                    }
                }
                if (pos >= pattern.length()) {
                    throw new IllegalArgumentException("Pattern ends with an incomplete string literal: " + pattern);
                }
                String str = pattern.substring(start + 1, pos);
                if (str.length() == 0) {
                    appendLiteral('\'');
                } else {
                    appendLiteral(str.replace("''", "'"));
                }
                
            } else if (cur == '[') {
                optionalStart();
                
            } else if (cur == ']') {
                if (active.parent == null) {
                    throw new IllegalArgumentException("Pattern invalid as it contains ] without previous [");
                }
                optionalEnd();
                
            } else {
                appendLiteral(cur);
            }
        }
    }

    private void parseRule(char cur, int count, DateTimeRule rule, int fraction) {
        switch (cur) {
            case 'y':
            case 'Y':
                if (count == 2) {
                    appendValueReduced(rule, 2, 2000);
                } else if (count < 4) {
                    appendValue(rule, count, 19, SignStyle.NORMAL);
                } else {
                    appendValue(rule, count, 19, SignStyle.EXCEEDS_PAD);
                }
                break;
            case 'M':
            case 'Q':
            case 'E':
                switch (count) {
                    case 1:
                        appendValue(rule);
                        break;
                    case 2:
                        appendValue(rule, 2);
                        break;
                    case 3:
                        appendText(rule, TextStyle.SHORT);
                        break;
                    case 4:
                        appendText(rule, TextStyle.FULL);
                        break;
                    case 5:
                        appendText(rule, TextStyle.NARROW);
                        break;
                    default:
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
                break;
            case 'a':
                switch (count) {
                    case 1:
                    case 2:
                    case 3:
                        appendText(rule, TextStyle.SHORT);
                        break;
                    case 4:
                        appendText(rule, TextStyle.FULL);
                        break;
                    case 5:
                        appendText(rule, TextStyle.NARROW);
                        break;
                    default:
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
                break;
            default:
                if (fraction > 0) {
                    appendFraction(rule, count, fraction == 1 ? count : 9);
                } else {
                    if (count == 1) {
                        appendValue(rule);
                    } else {
                        appendValue(rule, count);
                    }
                }
                break;
        }
    }

    /** Map of letters to rules. */
    private static final Map<Character, DateTimeRule> RULE_MAP = new HashMap<Character, DateTimeRule>();
    static {
        // TODO: G -> era
        // TODO: y -> year-of-era
        // TODO: u -> year
        // TODO: g -> mjDay
        // TODO: e -> day-of-week localized number (config somewhere)
        // TODO: standalone (L months, q quarters, c dayofweek, but use L as prefix instead -> LM,LQ,LE
        RULE_MAP.put('y', ISODateTimeRule.YEAR);                    // 310, CLDR
        RULE_MAP.put('Y', ISODateTimeRule.WEEK_BASED_YEAR);         // Java7, CLDR
        RULE_MAP.put('Q', ISODateTimeRule.QUARTER_OF_YEAR);         // 310, CLDR
        RULE_MAP.put('M', ISODateTimeRule.MONTH_OF_YEAR);           // Java, CLDR
        RULE_MAP.put('q', ISODateTimeRule.MONTH_OF_QUARTER);        // 310, other meaning in CLDR
        RULE_MAP.put('w', ISODateTimeRule.WEEK_OF_WEEK_BASED_YEAR); // Java, CLDR
        RULE_MAP.put('D', ISODateTimeRule.DAY_OF_YEAR);             // Java, CLDR
        RULE_MAP.put('d', ISODateTimeRule.DAY_OF_MONTH);            // Java, CLDR
        RULE_MAP.put('F', ISODateTimeRule.ALIGNED_WEEK_OF_MONTH);   // Java, CLDR
        RULE_MAP.put('E', ISODateTimeRule.DAY_OF_WEEK);             // Java, CLDR (different to both for 1/2 chars)
        RULE_MAP.put('a', ISODateTimeRule.AMPM_OF_DAY);             // Java, CLDR
        RULE_MAP.put('H', ISODateTimeRule.HOUR_OF_DAY);             // Java, CLDR
        RULE_MAP.put('k', ISODateTimeRule.CLOCK_HOUR_OF_DAY);       // Java, CLDR
        RULE_MAP.put('K', ISODateTimeRule.HOUR_OF_AMPM);            // Java, CLDR
        RULE_MAP.put('h', ISODateTimeRule.CLOCK_HOUR_OF_AMPM);      // Java, CLDR
        RULE_MAP.put('m', ISODateTimeRule.MINUTE_OF_HOUR);          // Java, CLDR
        RULE_MAP.put('s', ISODateTimeRule.SECOND_OF_MINUTE);        // Java, CLDR
        RULE_MAP.put('S', ISODateTimeRule.MILLI_OF_SECOND);         // Java, CLDR (CLDR fraction-of-second)
        RULE_MAP.put('A', ISODateTimeRule.MILLI_OF_DAY);            // 310, CLDR
        RULE_MAP.put('n', ISODateTimeRule.NANO_OF_SECOND);          // 310
        RULE_MAP.put('N', ISODateTimeRule.NANO_OF_DAY);             // 310
        // reserved - z,Z,X,I,f,p
        // reserved - v,V - future extended CLDR compatible zone names
        // reserved - l - future extended CLDR compatible leap month symbol
        // reserved - W - future extended CLDR compatible week of week-based-month
        // CLDR - L, q, c are altered to be L prefix
        // CLDR - j - not relevant
        // CLDR - Z - different approach here
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
     * @return this, for chaining, not null
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
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if pad width is too small
     */
    public DateTimeFormatterBuilder padNext(int padWidth, char padChar) {
        if (padWidth < 1) {
            throw new IllegalArgumentException("The pad width must be at least one but was " + padWidth);
        }
        active.padNextWidth = padWidth;
        active.padNextChar = padChar;
        active.valueParserIndex = -1;
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Mark the start of an optional section.
     * <p>
     * The output of printing can include optional sections, which may be nested.
     * An optional section is started by calling this method and ended by calling
     * {@link #optionalEnd()} or by ending the build process.
     * <p>
     * All elements in the optional section are treated as optional.
     * During printing, the section is only output if data is available in the
     * {@code Calendrical} for all the elements in the section.
     * During parsing, the whole section may be missing from the parsed string.
     * <p>
     * For example, consider a builder setup as
     * {@code builder.appendValue(hourRule,2).optionalStart().appendValue(minuteRule,2)}.
     * The optional section ends automatically at the end of the builder.
     * During printing, the minute will only be output if its value can be obtained from the calendrical.
     * During parsing, the input will be successfully parsed whether the minute is present or not.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder optionalStart() {
        active.valueParserIndex = -1;
        active = new DateTimeFormatterBuilder(active, true);
        return this;
    }

    /**
     * Ends an optional section.
     * <p>
     * The output of printing can include optional sections, which may be nested.
     * An optional section is started by calling {@link #optionalStart()} and ended
     * using this method (or at the end of the builder).
     * <p>
     * Calling this method without having previously called {@code optionalStart}
     * will throw an exception.
     * Calling this method immediately after calling {@code optionalStart} has no effect
     * on the formatter other than ending the (empty) optional section.
     * <p>
     * All elements in the optional section are treated as optional.
     * During printing, the section is only output if data is available in the
     * {@code Calendrical} for all the elements in the section.
     * During parsing, the whole section may be missing from the parsed string.
     * <p>
     * For example, consider a builder setup as
     * {@code builder.appendValue(hourRule,2).optionalStart().appendValue(minuteRule,2).optionalEnd()}.
     * During printing, the minute will only be output if its value can be obtained from the calendrical.
     * During parsing, the input will be successfully parsed whether the minute is present or not.
     *
     * @return this, for chaining, not null
     * @throws IllegalStateException if there was no previous call to {@code optionalStart}
     */
    public DateTimeFormatterBuilder optionalEnd() {
        if (active.parent == null) {
            throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
        }
        if (active.printers.size() > 0) {
            CompositePrinterParser cpp = new CompositePrinterParser(active.printers, active.parsers, active.optional);
            active = active.parent;
            appendInternal(cpp, cpp);
        } else {
            active = active.parent;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends a printer and/or parser to the internal list handling padding.
     *
     * @param printer  the printer to add, null prevents the formatter from printing
     * @param parser  the parser to add, null prevents the formatter from parsing
     * @return this, for chaining, not null
     */
    private int appendInternal(DateTimePrinter printer, DateTimeParser parser) {
        if (active.padNextWidth > 0) {
            if (printer != null || parser != null) {
                printer = new PadPrinterParserDecorator(printer, parser, active.padNextWidth, active.padNextChar);
            }
            active.padNextWidth = 0;
            active.padNextChar = 0;
        }
        active.printers.add(printer);
        active.parsers.add(parser);
        active.valueParserIndex = -1;
        return active.printers.size() - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Completes this builder by creating the DateTimeFormatter using the default locale.
     * <p>
     * Calling this method will end any open optional sections by repeatedly
     * calling {@link #optionalEnd()} before creating the formatter.
     * <p>
     * This builder can still be used after creating the formatter if desired,
     * although the state may have been changed by calls to {@code optionalEnd}.
     *
     * @return the created formatter, not null
     */
    public DateTimeFormatter toFormatter() {
        return toFormatter(Locale.getDefault());
    }

    /**
     * Completes this builder by creating the DateTimeFormatter using the specified locale.
     * <p>
     * Calling this method will end any open optional sections by repeatedly
     * calling {@link #optionalEnd()} before creating the formatter.
     * <p>
     * This builder can still be used after creating the formatter if desired,
     * although the state may have been changed by calls to {@code optionalEnd}.
     *
     * @param locale  the locale to use for formatting, not null
     * @return the created formatter, not null
     */
    public DateTimeFormatter toFormatter(Locale locale) {
        DateTimeFormatterBuilder.checkNotNull(locale, "Locale must not be null");
        
        while (active.parent != null) {
            optionalEnd();
        }
        return new DateTimeFormatter(locale, new CompositePrinterParser(printers, parsers, false));
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of ways to handle the positive/negative sign.
     *
     * @author Stephen Colebourne
     */
    public static enum SignStyle {
        /**
         * Style to output the sign only if the value is negative.
         * In strict parsing, the negative sign will be accepted and the positive sign rejected.
         * In lenient parsing, any sign will be accepted.
         */
        NORMAL,
        /**
         * Style to always output the sign, where zero will output '+'.
         * In strict parsing, the absence of a sign will be rejected.
         * In lenient parsing, the absence of a sign will be treated as a positive number.
         */
        ALWAYS,
        /**
         * Style to never output sign, only outputting the absolute value.
         * In strict parsing, any sign will be rejected.
         * In lenient parsing, any sign will be accepted unless the width is fixed.
         */
        NEVER,
        /**
         * Style to block negative values, throwing an exception on printing.
         * In strict parsing, any sign will be rejected.
         * In lenient parsing, any sign will be accepted unless the width is fixed.
         */
        NOT_NEGATIVE,
        /**
         * Style to always output the sign if the value exceeds the pad width.
         * A negative value will always output the '-' sign.
         * In strict parsing, the sign will be rejected unless the pad width is exceeded.
         * In lenient parsing, any sign will be accepted.
         */
        EXCEEDS_PAD;
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of the style of text output to use.
     *
     * @author Stephen Colebourne
     */
    public static enum TextStyle {
        // ordered from large to small
        /**
         * Full text, typically the full description.
         */
        FULL,
        /**
         * Short text, typically an abbreviation.
         */
        SHORT,
        /**
         * Narrow text, typically a single letter.
         */
        NARROW;
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of the style of a localized date, time or date-time formatter.
     *
     * @author Stephen Colebourne
     */
    public static enum FormatStyle {
        // ordered from large to small
        /**
         * Full text style, with the most detail.
         * An example might be 'Tuesday, April 12, 1952 AD' or '3:30:42pm PST'.
         */
        FULL,
        /**
         * Long text style, with lots of detail.
         * An example might be 'January 12, 1952'.
         */
        LONG,
        /**
         * Medium text style, with some detail.
         * An example might be 'Jan 12, 1952'.
         */
        MEDIUM,
        /**
         * Short text style, typically numeric.
         * An example might be '12.13.52' or '3:30pm'.
         */
        SHORT;
    }

}
