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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.QuarterYearField;
import javax.time.chrono.Chrono;
import javax.time.chrono.ISOChronology;
import javax.time.format.SimpleDateTimeTextProvider.LocaleStore;
import javax.time.zone.ZoneRulesGroup;

/**
 * Builder to create formatters for calendricals.
 * <p>
 * This allows a {@code DateTimeFormatter} to be created.
 * All date-time formatters are created ultimately using this builder.
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
 * <h4>Implementation notes</h4>
 * This class is a mutable builder intended for use from a single thread.
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
    private final List<DateTimePrinterParser> printerParsers = new ArrayList<DateTimePrinterParser>();
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
        appendInternal(CaseSensitivePrinterParser.SENSITIVE);
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
        appendInternal(CaseSensitivePrinterParser.INSENSITIVE);
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
        appendInternal(StrictLenientPrinterParser.STRICT);
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
        appendInternal(StrictLenientPrinterParser.LENIENT);
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
     * See {@link #appendValue(DateTimeField, int)} for full details.
     *
     * @param field  the field to append, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        active.valueParserIndex = appendInternal(new NumberPrinterParser(field, 1, 19, SignStyle.NORMAL));
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
     * For example, consider {@code builder.appendValue(YEAR).appendValue(MONTH_OF_YEAR, 2);}
     * The year is a variable width parse of between 1 and 19 digits.
     * The month is a fixed width parse of 2 digits.
     * Because these were appended to the same builder immediately after one another,
     * the year parser will reserve two digits for the month to parse.
     * Thus, the text '201106' will correctly parse to a year of 2011 and a month of 6.
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
     * @param field  the field to append, not null
     * @param width  the width of the printed field, from 1 to 19
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the width is invalid
     */
    public DateTimeFormatterBuilder appendValue(DateTimeField field, int width) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        if (width < 1 || width > 19) {
            throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + width);
        }
        NumberPrinterParser pp = new NumberPrinterParser(field, width, width, SignStyle.NOT_NEGATIVE);
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
     * See {@link #appendValue(DateTimeField, int)} for full details.
     *
     * @param field  the field to append, not null
     * @param minWidth  the minimum field width of the printed field, from 1 to 19
     * @param maxWidth  the maximum field width of the printed field, from 1 to 19
     * @param signStyle  the positive/negative output style, not null
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the widths are invalid
     */
    public DateTimeFormatterBuilder appendValue(
            DateTimeField field, int minWidth, int maxWidth, SignStyle signStyle) {
        if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
            return appendValue(field, maxWidth);
        }
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        DateTimes.checkNotNull(signStyle, "SignStyle must not be null");
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
        NumberPrinterParser pp = new NumberPrinterParser(field, minWidth, maxWidth, signStyle);
        if (minWidth == maxWidth) {
            appendInternal(pp);
        } else {
            active.valueParserIndex = appendInternal(pp);
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
     * See {@link #appendValue(DateTimeField, int)} for full details.
     *
     * @param field  the field to append, not null
     * @param width  the width of the printed and parsed field, from 1 to 18
     * @param baseValue  the base value of the range of valid values
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the width or base value is invalid
     */
    public DateTimeFormatterBuilder appendValueReduced(
            DateTimeField field, int width, int baseValue) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        ReducedPrinterParser pp = new ReducedPrinterParser(field, width, baseValue);
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
            NumberPrinterParser basePP = (NumberPrinterParser) active.printerParsers.get(active.valueParserIndex);
            basePP = basePP.withSubsequentWidth(width);
            int activeValueParser = active.valueParserIndex;
            active.printerParsers.set(active.valueParserIndex, basePP);
            appendInternal(pp);
            active.valueParserIndex = activeValueParser;
        } else {
            appendInternal(pp);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the fractional value of a date-time field to the formatter.
     * <p>
     * The fractional value of the field will be output including the
     * preceeding decimal point. The preceeding value is not output.
     * For example, the second-of-minute value of 15 would be output as {@code .25}.
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
     * @param field  the field to append, not null
     * @param minWidth  the minimum width of the field excluding the decimal point, from 0 to 9
     * @param maxWidth  the maximum width of the field excluding the decimal point, from 1 to 9
     * @return this, for chaining, not null
     * @throws IllegalArgumentException if the field has a variable set of valid values
     * @throws IllegalArgumentException if either width is invalid
     */
    public DateTimeFormatterBuilder appendFraction(
            DateTimeField field, int minWidth, int maxWidth) {
        appendInternal(new FractionPrinterParser(field, minWidth, maxWidth));
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the text of a date-time field to the formatter using the full
     * text style.
     * <p>
     * The text of the field will be output during a print.
     * The value must be within the valid range of the field.
     * If the value cannot be obtained then an exception will be thrown.
     * If the field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param field  the field to append, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendText(DateTimeField field) {
        return appendText(field, TextStyle.FULL);
    }

    /**
     * Appends the text of a date-time field to the formatter.
     * <p>
     * The text of the field will be output during a print.
     * The value must be within the valid range of the field.
     * If the value cannot be obtained then an exception will be thrown.
     * If the field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param field  the field to append, not null
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendText(DateTimeField field, TextStyle textStyle) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        DateTimes.checkNotNull(textStyle, "TextStyle must not be null");
        appendInternal(new TextPrinterParser(field, textStyle, DateTimeFormatters.getTextProvider()));
        return this;
    }

    /**
     * Appends the text of a date-time field to the formatter using the specified
     * map to supply the text.
     * <p>
     * The standard text outputting methods use the localized text in the JDK.
     * This method allows that text to be specified directly.
     * The supplied map is not validated by the builder to ensure that printing or
     * parsing is possible, thus an invalid map may throw an error during later use.
     * <p>
     * Supplying the map of text provides considerable flexibility in printing and parsing.
     * For example, a legacy application might require or supply the months of the
     * year as "JNY", "FBY", "MCH" etc. These do not match the standard set of text
     * for localized month names. Using this method, a map can be created which
     * defines the connection between each value and the text:
     * <pre>
     * Map&lt;Long, String&gt; map = new HashMap&lt;&gt;();
     * map.put(1, "JNY");
     * map.put(2, "FBY");
     * map.put(3, "MCH");
     * ...
     * builder.appendText(MONTH_OF_YEAR, map);
     * </pre>
     * <p>
     * Other uses might be to output the value with a suffix, such as "1st", "2nd", "3rd",
     * or as Roman numerals "I", "II", "III", "IV".
     * <p>
     * During printing, the value is obtained and checked that it is in the valid range.
     * If text is not available for the value then it is output as a number.
     * During parsing, the parser will match against the map of text and numeric values.
     *
     * @param field  the field to append, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendText(DateTimeField field, Map<Long, String> textLookup) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        DateTimes.checkNotNull(textLookup, "Map must not be null");
        Map<Long, String> copy = new LinkedHashMap<Long, String>(textLookup);
        Map<TextStyle, Map<Long, String>> map = Collections.singletonMap(TextStyle.FULL, copy);
        final LocaleStore store = new LocaleStore(map);
        DateTimeTextProvider provider = new DateTimeTextProvider() {
            @Override
            public String getText(DateTimeField field, long value, TextStyle style, Locale locale) {
                return store.getText(value, style);
            }
            @Override
            public Iterator<Entry<String, Long>> getTextIterator(DateTimeField field, TextStyle style, Locale locale) {
                return store.getTextIterator(style);
            }
            @Override
            public Locale[] getAvailableLocales() {
                throw new UnsupportedOperationException();
            }
        };
        appendInternal(new TextPrinterParser(field, TextStyle.FULL, provider));
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
        appendInternal(new ZoneOffsetPrinterParser(noOffsetText, pattern));
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the time-zone id, such as 'Europe/Paris', to the formatter.
     * <p>
     * The time-zone id will be output during a print.
     * If the zone cannot be obtained then an exception will be thrown.
     *
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendZoneId() {
        appendInternal(new ZoneIdPrinterParser(null));
        return this;
    }

    /**
     * Appends the time-zone name, such as 'British Summer Time', to the formatter.
     * <p>
     * The time-zone name will be output during a print.
     * If the zone cannot be obtained then an exception will be thrown.
     * <p>
     * The zone name is obtained from the formatting symbols.
     * Different names may be output depending on whether daylight saving time applies.
     * <p>
     * If the date, time or offset cannot be obtained it may not be possible to
     * determine which text to output. In this case, the text representing time
     * without daylight savings (winter time) will be used.
     *
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, not null
     */
    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle) {
        DateTimes.checkNotNull(textStyle, "TextStyle must not be null");
        appendInternal(new ZoneIdPrinterParser(textStyle));
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
    public DateTimeFormatterBuilder appendLocalized(FormatStyle dateStyle, FormatStyle timeStyle, Chrono chronology) {
        DateTimes.checkNotNull(chronology, "Chronology must not be null");
        if (dateStyle != null || timeStyle != null) {
            appendInternal(new LocalizedPrinterParser(dateStyle, timeStyle, chronology));
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
        appendInternal(new CharLiteralPrinterParser(literal));
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
        DateTimes.checkNotNull(literal, "Literal text must not be null");
        if (literal.length() > 0) {
            if (literal.length() == 1) {
                appendInternal(new CharLiteralPrinterParser(literal.charAt(0)));
            } else {
                appendInternal(new StringLiteralPrinterParser(literal));
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
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
        DateTimes.checkNotNull(formatter, "DateTimeFormatter must not be null");
        appendInternal(formatter.toPrinterParser(false));
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
        DateTimes.checkNotNull(formatter, "DateTimeFormatter must not be null");
        appendInternal(formatter.toPrinterParser(true));
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
     * of digits and without padding as per {@link #appendValue(DateTimeField)}. Otherwise, the
     * count of digits is used as the width of the output field as per {@link #appendValue(DateTimeField, int)}.
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
        DateTimes.checkNotNull(pattern, "Pattern must not be null");
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
                DateTimeField field = FIELD_MAP.get(cur);
                if (field != null) {
                    parseField(cur, count, field, fraction);
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

    private void parseField(char cur, int count, DateTimeField field, int fraction) {
        switch (cur) {
            case 'y':
            case 'Y':
                if (count == 2) {
                    appendValueReduced(field, 2, 2000);
                } else if (count < 4) {
                    appendValue(field, count, 19, SignStyle.NORMAL);
                } else {
                    appendValue(field, count, 19, SignStyle.EXCEEDS_PAD);
                }
                break;
            case 'M':
            case 'Q':
            case 'E':
                switch (count) {
                    case 1:
                        appendValue(field);
                        break;
                    case 2:
                        appendValue(field, 2);
                        break;
                    case 3:
                        appendText(field, TextStyle.SHORT);
                        break;
                    case 4:
                        appendText(field, TextStyle.FULL);
                        break;
                    case 5:
                        appendText(field, TextStyle.NARROW);
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
                        appendText(field, TextStyle.SHORT);
                        break;
                    case 4:
                        appendText(field, TextStyle.FULL);
                        break;
                    case 5:
                        appendText(field, TextStyle.NARROW);
                        break;
                    default:
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
                break;
            default:
                if (fraction > 0) {
                    appendFraction(field, count, fraction == 1 ? count : 9);
                } else {
                    if (count == 1) {
                        appendValue(field);
                    } else {
                        appendValue(field, count);
                    }
                }
                break;
        }
    }

    /** Map of letters to fields. */
    private static final Map<Character, DateTimeField> FIELD_MAP = new HashMap<Character, DateTimeField>();
    static {
        // TODO: G -> era
        // TODO: y -> year-of-era
        // TODO: u -> year
        // TODO: g -> mjDay
        // TODO: e -> day-of-week localized number (config somewhere)
        // TODO: standalone (L months, q quarters, c dayofweek, but use L as prefix instead -> LM,LQ,LE
        FIELD_MAP.put('y', LocalDateTimeField.YEAR);                      // 310, CLDR
//        FIELD_MAP.put('Y', ISODateTimeField.WEEK_BASED_YEAR);         // Java7, CLDR
        FIELD_MAP.put('Q', QuarterYearField.QUARTER_OF_YEAR);         // 310, CLDR
        FIELD_MAP.put('M', LocalDateTimeField.MONTH_OF_YEAR);             // Java, CLDR
        FIELD_MAP.put('q', QuarterYearField.MONTH_OF_QUARTER);        // 310, other meaning in CLDR
//        FIELD_MAP.put('w', ISODateTimeField.WEEK_OF_WEEK_BASED_YEAR); // Java, CLDR
        FIELD_MAP.put('D', LocalDateTimeField.DAY_OF_YEAR);               // Java, CLDR
        FIELD_MAP.put('d', LocalDateTimeField.DAY_OF_MONTH);              // Java, CLDR
        FIELD_MAP.put('F', LocalDateTimeField.ALIGNED_WEEK_OF_MONTH);     // Java, CLDR
        FIELD_MAP.put('E', LocalDateTimeField.DAY_OF_WEEK);               // Java, CLDR (different to both for 1/2 chars)
        FIELD_MAP.put('a', LocalDateTimeField.AMPM_OF_DAY);               // Java, CLDR
        FIELD_MAP.put('H', LocalDateTimeField.HOUR_OF_DAY);               // Java, CLDR
        FIELD_MAP.put('k', LocalDateTimeField.CLOCK_HOUR_OF_DAY);         // Java, CLDR
        FIELD_MAP.put('K', LocalDateTimeField.HOUR_OF_AMPM);              // Java, CLDR
        FIELD_MAP.put('h', LocalDateTimeField.CLOCK_HOUR_OF_AMPM);        // Java, CLDR
        FIELD_MAP.put('m', LocalDateTimeField.MINUTE_OF_HOUR);            // Java, CLDR
        FIELD_MAP.put('s', LocalDateTimeField.SECOND_OF_MINUTE);          // Java, CLDR
        FIELD_MAP.put('S', LocalDateTimeField.MILLI_OF_SECOND);           // Java, CLDR (CLDR fraction-of-second)
        FIELD_MAP.put('A', LocalDateTimeField.MILLI_OF_DAY);              // 310, CLDR
        FIELD_MAP.put('n', LocalDateTimeField.NANO_OF_SECOND);            // 310
        FIELD_MAP.put('N', LocalDateTimeField.NANO_OF_DAY);               // 310
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
     * {@code builder.appendValue(HOUR_OF_DAY,2).optionalStart().appendValue(MINUTE_OF_HOUR,2)}.
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
     * {@code builder.appendValue(HOUR_OF_DAY,2).optionalStart().appendValue(MINUTE_OF_HOUR,2).optionalEnd()}.
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
        if (active.printerParsers.size() > 0) {
            CompositePrinterParser cpp = new CompositePrinterParser(active.printerParsers, active.optional);
            active = active.parent;
            appendInternal(cpp);
        } else {
            active = active.parent;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends a printer and/or parser to the internal list handling padding.
     *
     * @param pp  the printer-parser to add, not null
     * @return this, for chaining, not null
     */
    private int appendInternal(DateTimePrinterParser pp) {
        DateTimes.checkNotNull(pp, "DateTimePrinterParser must not be null");
        if (active.padNextWidth > 0) {
            if (pp != null) {
                pp = new PadPrinterParserDecorator(pp, active.padNextWidth, active.padNextChar);
            }
            active.padNextWidth = 0;
            active.padNextChar = 0;
        }
        active.printerParsers.add(pp);
        active.valueParserIndex = -1;
        return active.printerParsers.size() - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Completes this builder by creating the DateTimeFormatter using the default locale.
     * <p>
     * This will create a formatter with the default locale.
     * Numbers will be printed and parsed using the standard non-localized set of symbols.
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
     * This will create a formatter with the specified locale.
     * Numbers will be printed and parsed using the standard non-localized set of symbols.
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
        DateTimes.checkNotNull(locale, "Locale must not be null");
        while (active.parent != null) {
            optionalEnd();
        }
        return new DateTimeFormatter(new CompositePrinterParser(printerParsers, false), locale, DateTimeFormatSymbols.STANDARD);
    }

    //-----------------------------------------------------------------------
    /**
     * Strategy for printing/parsing calendrical information.
     * <p>
     * The printer may print any part, or the whole, of the input Calendrical.
     * Typically, a complete print is constructed from a number of smaller
     * units, each outputting a single field.
     * <p>
     * The parser may parse any piece of text from the input, storing the result
     * in the context. Typically, each individual parser will just parse one
     * field, such as the day-of-month, storing the value in the context.
     * Once the parse is complete, the caller will then convert the context
     * to a {@link DateTimeBuilder} to merge the parsed values to create the
     * desired object, such as a {@code LocalDate}.
     * <p>
     * The parse position will be updated during the parse. Parsing will start at
     * the specified index and the return value specifies the new parse position
     * for the next parser. If an error occurs, the returned index will be negative
     * and will have the error position encoded using the complement operator.
     * 
     * <h4>Implementation notes</h4>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     * <p>
     * The context is not a thread-safe object and a new instance will be created
     * for each print that occurs. The context must not be stored in an instance
     * variable or shared with any other threads.
     */
    interface DateTimePrinterParser {

        /**
         * Prints the calendrical object to the buffer.
         * <p>
         * The context holds information to use during the print.
         * It also contains the calendrical information to be printed.
         * <p>
         * The buffer must not be mutated beyond the content controlled by the implementation.
         *
         * @param context  the context to print using, not null
         * @param buf  the buffer to append to, not null
         * @return false if unable to query the value from the calendrical, true otherwise
         * @throws CalendricalException if the calendrical cannot be printed successfully
         */
        boolean print(DateTimePrintContext context, StringBuilder buf);

        /**
         * Parses text into calendrical information.
         * <p>
         * The context holds information to use during the parse.
         * It is also used to store the parsed calendrical information.
         *
         * @param context  the context to use and parse into, not null
         * @param text  the input text to parse, not null
         * @param position  the position to start parsing at, from 0 to the text length
         * @return the new parse position, where negative means an error with the
         *  error position encoded using the complement ~ operator
         * @throws NullPointerException if the context or text is null
         * @throws IndexOutOfBoundsException if the position is invalid
         */
        int parse(DateTimeParseContext context, CharSequence text, int position);
    }

    //-----------------------------------------------------------------------
    /**
     * Composite printer and parser.
     */
    static final class CompositePrinterParser implements DateTimePrinterParser {
        private final DateTimePrinterParser[] printerParsers;
        private final boolean optional;

        CompositePrinterParser(List<DateTimePrinterParser> printerParsers, boolean optional) {
            this(printerParsers.toArray(new DateTimePrinterParser[printerParsers.size()]), optional);
        }

        CompositePrinterParser(DateTimePrinterParser[] printerParsers, boolean optional) {
            this.printerParsers = printerParsers;
            this.optional = optional;
        }

        /**
         * Returns a copy of this printer-parser with the optional flag changed.
         *
         * @param optional  the optional flag to set in the copy
         * @return the new printer-parser, not null
         */
        public CompositePrinterParser withOptional(boolean optional) {
            if (optional == this.optional) {
                return this;
            }
            return new CompositePrinterParser(printerParsers, optional);
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            int length = buf.length();
            if (optional) {
                context.startOptional();
            }
            try {
                for (DateTimePrinterParser pp : printerParsers) {
                    if (pp.print(context, buf) == false) {
                        buf.setLength(length);  // reset buffer
                        return true;
                    }
                }
            } finally {
                if (optional) {
                    context.endOptional();
                }
            }
            return true;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (optional) {
                context.startOptional();
                int pos = position;
                for (DateTimePrinterParser pp : printerParsers) {
                    pos = pp.parse(context, text, pos);
                    if (pos < 0) {
                        context.endOptional(false);
                        return position;  // return original position
                    }
                }
                context.endOptional(true);
                return pos;
            } else {
                for (DateTimePrinterParser pp : printerParsers) {
                    position = pp.parse(context, text, position);
                    if (position < 0) {
                        break;
                    }
                }
                return position;
            }
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            if (printerParsers != null) {
                buf.append(optional ? "[" : "(");
                for (DateTimePrinterParser pp : printerParsers) {
                    buf.append(pp);
                }
                buf.append(optional ? "]" : ")");
            }
            return buf.toString();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Pads the output to a fixed width.
     */
    static final class PadPrinterParserDecorator implements DateTimePrinterParser {
        private final DateTimePrinterParser printerParser;
        private final int padWidth;
        private final char padChar;

        /**
         * Constructor.
         *
         * @param printerParser  the printer, not null
         * @param padWidth  the width to pad to, 1 or greater
         * @param padChar  the pad character
         */
        PadPrinterParserDecorator(DateTimePrinterParser printerParser, int padWidth, char padChar) {
            // input checked by DateTimeFormatterBuilder
            this.printerParser = printerParser;
            this.padWidth = padWidth;
            this.padChar = padChar;
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            int preLen = buf.length();
            if (printerParser.print(context, buf) == false) {
                return false;
            }
            int len = buf.length() - preLen;
            if (len > padWidth) {
                throw new CalendricalPrintException(
                    "Cannot print as output of " + len + " characters exceeds pad width of " + padWidth);
            }
            for (int i = 0; i < padWidth - len; i++) {
                buf.insert(preLen, padChar);
            }
            return true;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            if (position > text.length()) {
                throw new IndexOutOfBoundsException();
            }
            int endPos = position + padWidth;
            if (endPos > text.length()) {
                return ~position;  // not enough characters in the string to meet the parse width
            }
            int pos = position;
            while (pos < endPos && text.charAt(pos) == padChar) {
                pos++;
            }
            text = text.subSequence(0, endPos);
            int firstError = 0;
            while (pos >= position) {
                int resultPos = printerParser.parse(context, text, pos);
                if (resultPos < 0) {
                    // parse of decorated field had an error
                    if (firstError == 0) {
                        firstError = resultPos;
                    }
                    // loop around in case the decorated parser can handle the padChar at the start
                    pos--;
                    continue;
                }
                if (resultPos != endPos) {
                    return ~position;  // parse of decorated field didn't parse to the end
                }
                return resultPos;
            }
            // loop runs at least once, so firstError must be set by the time we get here
            return firstError;  // return error from first parse of decorated field
        }

        @Override
        public String toString() {
            return "Pad(" + printerParser + "," + padWidth + (padChar == ' ' ? ")" : ",'" + padChar + "')");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration to set the case sensitivity parse style.
     */
    static enum CaseSensitivePrinterParser implements DateTimePrinterParser {
        SENSITIVE,
        INSENSITIVE;

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            return true;  // nothing to do here
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            context.setCaseSensitive(this == SENSITIVE);
            return position;
        }

        @Override
        public String toString() {
            return "ParseCaseSensitive(" + (this == SENSITIVE) + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration printer/parser to set the strict/lenient parse style.
     */
    static enum StrictLenientPrinterParser implements DateTimePrinterParser {
        STRICT,
        LENIENT;

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            return true;  // nothing to do here
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            context.setStrict(this == STRICT);
            return position;
        }

        @Override
        public String toString() {
            return "ParseStrict(" + (this == STRICT) + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints or parses a character literal.
     */
    static final class CharLiteralPrinterParser implements DateTimePrinterParser {
        private final char literal;

        CharLiteralPrinterParser(char literal) {
            this.literal = literal;
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            buf.append(literal);
            return true;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position == length) {
                return ~position;
            }
            char ch = text.charAt(position);
            if (ch != literal) {
                if (context.isCaseSensitive() ||
                        (Character.toUpperCase(ch) != Character.toUpperCase(literal) &&
                         Character.toLowerCase(ch) != Character.toLowerCase(literal))) {
                    return ~position;
                }
            }
            return position + 1;
        }

        @Override
        public String toString() {
            if (literal == '\'') {
                return "''";
            }
            return "'" + literal + "'";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints or parses a string literal.
     */
    static final class StringLiteralPrinterParser implements DateTimePrinterParser {
        private final String literal;

        StringLiteralPrinterParser(String literal) {
            this.literal = literal;  // validated by caller
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            buf.append(literal);
            return true;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position > length || position < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (context.subSequenceEquals(text, position, literal, 0, literal.length()) == false) {
                return ~position;
            }
            return position + literal.length();
        }

        @Override
        public String toString() {
            String converted = literal.replace("'", "''");
            return "'" + converted + "'";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints and parses a numeric date-time field with optional padding.
     */
    static class NumberPrinterParser implements DateTimePrinterParser {

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

        final DateTimeField field;
        final int minWidth;
        private final int maxWidth;
        private final SignStyle signStyle;
        private final int subsequentWidth;

        /**
         * Constructor.
         *
         * @param field  the field to print, not null
         * @param minWidth  the minimum field width, from 1 to 19
         * @param maxWidth  the maximum field width, from minWidth to 19
         * @param signStyle  the positive/negative sign style, not null
         */
        NumberPrinterParser(DateTimeField field, int minWidth, int maxWidth, SignStyle signStyle) {
            // validated by caller
            this.field = field;
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
            this.signStyle = signStyle;
            this.subsequentWidth = 0;
        }

        /**
         * Constructor.
         *
         * @param field  the field to print, not null
         * @param minWidth  the minimum field width, from 1 to 19
         * @param maxWidth  the maximum field width, from minWidth to 19
         * @param signStyle  the positive/negative sign style, not null
         * @param subsequentWidth  the width of subsequent non-negative numbers, 0 or greater
         */
        private NumberPrinterParser(DateTimeField field, int minWidth, int maxWidth, SignStyle signStyle, int subsequentWidth) {
            // validated by caller
            this.field = field;
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
            return new NumberPrinterParser(field, minWidth, maxWidth, signStyle, this.subsequentWidth + subsequentWidth);
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            Long valueLong = context.getValue(field);
            if (valueLong == null) {
                return false;
            }
            long value = getValue(valueLong);
            DateTimeFormatSymbols symbols = context.getSymbols();
            String str = (value == Long.MIN_VALUE ? "9223372036854775808" : Long.toString(Math.abs(value)));
            if (str.length() > maxWidth) {
                throw new CalendricalPrintException("Field " + field.getName() +
                    " cannot be printed as the value " + value +
                    " exceeds the maximum print width of " + maxWidth);
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
                        throw new CalendricalPrintException("Field " + field.getName() +
                            " cannot be printed as the value " + value +
                            " cannot be negative according to the SignStyle");
                }
            }
            for (int i = 0; i < minWidth - str.length(); i++) {
                buf.append(symbols.getZeroDigit());
            }
            buf.append(str);
            return true;
        }

        /**
         * Gets the value to output.
         * 
         * @param value  the base value of the field, not null
         * @return the value
         */
        long getValue(long value) {
            return value;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position == length) {
                return ~position;
            }
            char sign = text.charAt(position);  // IOOBE if invalid position
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
                    char ch = text.charAt(pos++);
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
            context.setParsedField(field, value);
        }

        @Override
        public String toString() {
            if (minWidth == 1 && maxWidth == 19 && signStyle == SignStyle.NORMAL) {
                return "Value(" + field.getName() + ")";
            }
            if (minWidth == maxWidth && signStyle == SignStyle.NOT_NEGATIVE) {
                return "Value(" + field.getName() + "," + minWidth + ")";
            }
            return "Value(" + field.getName() + "," + minWidth + "," + maxWidth + "," + signStyle + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints and parses a reduced numeric date-time field.
     */
    static final class ReducedPrinterParser extends NumberPrinterParser {
        private final int baseValue;
        private final int range;

        /**
         * Constructor.
         *
         * @param field  the field to print, validated not null
         * @param width  the field width, from 1 to 18
         * @param baseValue  the base value
         */
        ReducedPrinterParser(DateTimeField field, int width, int baseValue) {
            super(field, width, width, SignStyle.NOT_NEGATIVE);
            if (width < 1 || width > 18) {
                throw new IllegalArgumentException("The width must be from 1 to 18 inclusive but was " + width);
            }
            if (field.range().isValidValue(baseValue) == false) {
                throw new IllegalArgumentException("The base value must be within the range of the field");
            }
            this.baseValue = baseValue;
            this.range = EXCEED_POINTS[width];
            if ((((long) baseValue) + range) > Integer.MAX_VALUE) {
                throw new CalendricalException("Unable to add printer-parser as the range exceeds the capacity of an int");
            }
        }

        @Override
        long getValue(long value) {
            return Math.abs(value % range);
        }

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
            context.setParsedField(field, value);
        }

        @Override
        public String toString() {
            return "ReducedValue(" + field.getName() + "," + minWidth + "," + baseValue + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints and parses a numeric date-time field with optional padding.
     */
    static final class FractionPrinterParser implements DateTimePrinterParser {
        private final DateTimeField field;
        private final int minWidth;
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
            if (field.range().isFixed() == false) {
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

        @Override
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

        @Override
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

        /**
         * Converts a value for this field to a fraction between 0 and 1.
         * <p>
         * The fractional value is between 0 (inclusive) and 1 (exclusive).
         * It can only be returned if the {@link #range() value range} is fixed.
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
            DateTimeValueRange range = field.range();
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
         * It can only be returned if the {@link #range() value range} is fixed.
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
            DateTimeValueRange range = field.range();
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

        @Override
        public String toString() {
            return "Fraction(" + field.getName() + "," + minWidth + "," + maxWidth + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints or parses field text.
     */
    static final class TextPrinterParser implements DateTimePrinterParser {
        private final DateTimeField field;
        private final TextStyle textStyle;
        private final DateTimeTextProvider provider;
        /**
         * The cached number printer parser.
         * Immutable and volatile, so no synchronization needed.
         */
        private volatile NumberPrinterParser numberPrinterParser;

        /**
         * Constructor.
         *
         * @param field  the field to output, not null
         * @param textStyle  the text style, not null
         * @param provider  the text provider, not null
         */
        TextPrinterParser(DateTimeField field, TextStyle textStyle, DateTimeTextProvider provider) {
            // validated by caller
            this.field = field;
            this.textStyle = textStyle;
            this.provider = provider;
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            Long value = context.getValue(field);
            if (value == null) {
                return false;
            }
            String text = provider.getText(field, value, textStyle, context.getLocale());
            if (text == null) {
                return numberPrinterParser().print(context, buf);
            }
            buf.append(text);
            return true;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence parseText, int position) {
            int length = parseText.length();
            if (position < 0 || position > length) {
                throw new IndexOutOfBoundsException();
            }
            TextStyle style = (context.isStrict() ? textStyle : null);
            Iterator<Entry<String, Long>> it = provider.getTextIterator(field, style, context.getLocale());
            if (it != null) {
                while (it.hasNext()) {
                    Entry<String, Long> entry = it.next();
                    String text = entry.getKey();
                    if (context.subSequenceEquals(text, 0, parseText, position, text.length())) {
                        context.setParsedField(field, entry.getValue());
                        return position + text.length();
                    }
                }
                if (context.isStrict()) {
                    return ~position;
                }
            }
            return numberPrinterParser().parse(context, parseText, position);
        }

        /**
         * Create and cache a number printer parser.
         * @return the number printer parser for this field, not null
         */
        private NumberPrinterParser numberPrinterParser() {
            if (numberPrinterParser == null) {
                numberPrinterParser = new NumberPrinterParser(field, 1, 19, SignStyle.NORMAL);
            }
            return numberPrinterParser;
        }

        @Override
        public String toString() {
            if (textStyle == TextStyle.FULL) {
                return "Text(" + field.getName() + ")";
            }
            return "Text(" + field.getName() + "," + textStyle + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints or parses a zone offset.
     */
    static final class ZoneOffsetPrinterParser implements DateTimePrinterParser {
        static final String[] PATTERNS = new String[] {
            "+HH", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS",
        };  // order used in pattern builder

        private final String noOffsetText;
        private final int type;

        /**
         * Constructor.
         *
         * @param noOffsetText  the text to use for UTC, not null
         * @param pattern  the pattern
         */
        ZoneOffsetPrinterParser(String noOffsetText, String pattern) {
            DateTimes.checkNotNull(noOffsetText, "No offset text must not be null");
            DateTimes.checkNotNull(pattern, "Pattern must not be null");
            this.noOffsetText = noOffsetText;
            this.type = checkPattern(pattern);
        }

        private int checkPattern(String pattern) {
            for (int i = 0; i < PATTERNS.length; i++) {
                if (PATTERNS[i].equals(pattern)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Invalid zone offset pattern");
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            ZoneOffset offset = context.getValue(ZoneOffset.class);
            if (offset == null) {
                return false;
            }
            int totalSecs = offset.getTotalSeconds();
            if (totalSecs == 0) {
                buf.append(noOffsetText);
            } else if (type == 4 || (type == 2 && offset.getSecondsField() == 0)) {
                buf.append(offset.getID());
            } else {
                int absHours = Math.abs(offset.getHoursField());
                int absMinutes = Math.abs(offset.getMinutesField());
                int absSeconds = Math.abs(offset.getSecondsField());
                buf.append(totalSecs < 0 ? "-" : "+")
                    .append((char) (absHours / 10 + '0')).append((char) (absHours % 10 + '0'));
                if (type >= 1) {
                    buf.append((type % 2) == 0 ? ":" : "")
                        .append((char) (absMinutes / 10 + '0')).append((char) (absMinutes % 10 + '0'));
                    if (type >= 5 || (type >= 3 && absSeconds > 0)) {
                        buf.append((type % 2) == 0 ? ":" : "")
                            .append((char) (absSeconds / 10 + '0')).append((char) (absSeconds % 10 + '0'));
                    }
                }
            }
            return true;
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            ZoneOffset offset = null;
            int length = text.length();
            int utcLen = noOffsetText.length();
            if (utcLen == 0) {
                if (position == length) {
                    context.setParsed(ZoneOffset.UTC);
                    return position;
                }
            } else {
                if (position == length) {
                    return ~position;
                }
                if (context.subSequenceEquals(text, position, noOffsetText, 0, utcLen)) {
                    context.setParsed(ZoneOffset.UTC);
                    return position + utcLen;
                }
            }
            
            char sign = text.charAt(position);  // IOOBE if invalid position
            if (sign == '+' || sign == '-') {
                int negative = (sign == '-' ? -1 : 1);
                int[] array = new int[4];
                array[0] = position + 1;
                if (parseNumber(array, 1, text, true) ||
                        parseNumber(array, 2, text, true) ||
                        parseNumber(array, 3, text, false)) {
                    return ~position;
                }
                int total = (array[1] * 60 * 60) + (array[2] * 60) + array[3];
                if (total > 18 * 60 * 60) {  // max +18:00:00
                    return ~position;
                }
                offset = ZoneOffset.ofHoursMinutesSeconds(negative * array[1], negative * array[2], negative * array[3]);
                context.setParsed(offset);
                return array[0];
            } else {
                if (utcLen == 0) {
                    context.setParsed(ZoneOffset.UTC);
                    return position + utcLen;
                }
                return ~position;
            }
        }

        /**
         * Parse a two digit zero-prefixed number.
         *
         * @param array  the array of parsed data, 0=pos,1=hours,2=mins,3=secs, not null
         * @param arrayIndex  the index to parse the value into
         * @param parseText  the offset id, not null
         * @param required  whether this number is required
         * @return true if an error occurred
         */
        private boolean parseNumber(int[] array, int arrayIndex, CharSequence parseText, boolean required) {
            if ((type + 3) / 2 < arrayIndex) {
                return false;  // ignore seconds/minutes
            }
            int pos = array[0];
            if ((type % 2) == 0 && arrayIndex > 1) {
                if (pos + 1 > parseText.length() || parseText.charAt(pos) != ':') {
                    return required;
                }
                pos++;
            }
            if (pos + 2 > parseText.length()) {
                return required;
            }
            char ch1 = parseText.charAt(pos++);
            char ch2 = parseText.charAt(pos++);
            if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9') {
                return required;
            }
            int value = (ch1 - 48) * 10 + (ch2 - 48);
            if (value < 0 || value > 59) {
                return required;
            }
            array[arrayIndex] = value;
            array[0] = pos;
            return false;
        }

        @Override
        public String toString() {
            String converted = noOffsetText.replace("'", "''");
            return "Offset('" + converted + "'," + PATTERNS[type] + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints or parses a zone id.
     */
    static final class ZoneIdPrinterParser implements DateTimePrinterParser {
        /** The text style to output, null means the id. */
        private final TextStyle textStyle;

        ZoneIdPrinterParser(TextStyle textStyle) {
            // validated by caller
            this.textStyle = textStyle;
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            ZoneId zone = context.getValue(ZoneId.class);
            if (zone == null) {
                return false;
            }
            if (textStyle == null) {
                buf.append(zone.getID());
            } else {
                // TODO: fix getText(textStyle, context.getLocale())
                buf.append(zone.getRegionID());  // TODO: Use symbols
            }
            return true;
        }

        //-----------------------------------------------------------------------
        /**
         * The cached tree to speed up parsing.
         */
        private static SubstringTree preparedTree;
        /**
         * The cached IDs.
         */
        private static Set<String> preparedIDs;  // TODO: used inside and outside sync block

        /**
         * This implementation looks for the longest matching string.
         * For example, parsing Etc/GMT-2 will return Etc/GMC-2 rather than just
         * Etc/GMC although both are valid.
         * <p>
         * This implementation uses a tree to search for valid time-zone names in
         * the parseText. The top level node of the tree has a length equal to the
         * length of the shortest time-zone as well as the beginning characters of
         * all other time-zones.
         */
        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            int length = text.length();
            if (position > length) {
                throw new IndexOutOfBoundsException();
            }
            
            // setup parse tree
            Set<String> ids = ZoneRulesGroup.getParsableIDs();
            if (ids.size() == 0) {
                return ~position;
            }
            SubstringTree tree;
            synchronized (ZoneIdPrinterParser.class) {
                if (preparedTree == null || preparedIDs.size() < ids.size()) {
                    ids = new HashSet<String>(ids);
                    preparedTree = prepareParser(ids);
                    preparedIDs = ids;
                }
                tree = preparedTree;
            }
            
            // handle fixed time-zone ids
            if (text.subSequence(position, text.length()).toString().startsWith("UTC")) {
                DateTimeParseContext newContext = new DateTimeParseContext(context.getLocale(), DateTimeFormatSymbols.STANDARD);
                int startPos = position + 3;
                int endPos = new ZoneOffsetPrinterParser("", "+HH:MM:ss").parse(newContext, text, startPos);
                if (endPos < 0) {
                    context.setParsed(ZoneId.UTC);
                    return startPos;
                }
                ZoneId zone = ZoneId.of(newContext.getParsed(ZoneOffset.class));
                context.setParsed(zone);
                return endPos;
            }
            
            // parse
            String parsedZoneId = null;
            while (tree != null) {
                int nodeLength = tree.length;
                if (position + nodeLength > length) {
                    break;
                }
                parsedZoneId = text.subSequence(position, position + nodeLength).toString();
                tree = tree.get(parsedZoneId);
            }
            
            if (parsedZoneId == null || preparedIDs.contains(parsedZoneId) == false) {
                return ~position;
            }
            context.setParsed(ZoneId.of(parsedZoneId));
            return position + parsedZoneId.length();
        }

        //-----------------------------------------------------------------------
        /**
         * Model a tree of substrings to make the parsing easier. Due to the nature
         * of time-zone names, it can be faster to parse based in unique substrings
         * rather than just a character by character match.
         * <p>
         * For example, to parse America/Denver we can look at the first two
         * character "Am". We then notice that the shortest time-zone that starts
         * with Am is America/Nome which is 12 characters long. Checking the first
         * 12 characters of America/Denver gives America/Denv which is a substring
         * of only 1 time-zone: America/Denver. Thus, with just 3 comparisons that
         * match can be found.
         * <p>
         * This structure maps substrings to substrings of a longer length. Each
         * node of the tree contains a length and a map of valid substrings to
         * sub-nodes. The parser gets the length from the root node. It then
         * extracts a substring of that length from the parseText. If the map
         * contains the substring, it is set as the possible time-zone and the
         * sub-node for that substring is retrieved. The process continues until the
         * substring is no longer found, at which point the matched text is checked
         * against the real time-zones.
         */
        private static final class SubstringTree {
            /**
             * The length of the substring this node of the tree contains.
             * Subtrees will have a longer length.
             */
            final int length;
            /**
             * Map of a substring to a set of substrings that contain the key.
             */
            private final Map<CharSequence, SubstringTree> substringMap = new HashMap<CharSequence, SubstringTree>();

            /**
             * Constructor.
             *
             * @param length  the length of this tree
             */
            private SubstringTree(int length) {
                this.length = length;
            }

            private SubstringTree get(CharSequence substring2) {
                return substringMap.get(substring2);

            }

            /**
             * Values must be added from shortest to longest.
             *
             * @param newSubstring  the substring to add, not null
             */
            private void add(String newSubstring) {
                int idLen = newSubstring.length();
                if (idLen == length) {
                    substringMap.put(newSubstring, null);
                } else if (idLen > length) {
                    String substring = newSubstring.substring(0, length);
                    SubstringTree parserTree = substringMap.get(substring);
                    if (parserTree == null) {
                        parserTree = new SubstringTree(idLen);
                        substringMap.put(substring, parserTree);
                    }
                    parserTree.add(newSubstring);
                }
            }
        }

        /**
         * Builds an optimized parsing tree.
         *
         * @param availableIDs  the available IDs, not null, not empty
         * @return the tree, not null
         */
        private static SubstringTree prepareParser(Set<String> availableIDs) {
            // sort by length
            List<String> ids = new ArrayList<String>(availableIDs);
            Collections.sort(ids, new Comparator<String>() {
                public int compare(String str1, String str2) {
                    return str1.length() == str2.length() ? str1.compareTo(str2) : str1.length() - str2.length();
                }
            });
            
            // build the tree
            SubstringTree tree = new SubstringTree(ids.get(0).length());
            for (String id : ids) {
                tree.add(id);
            }
            return tree;
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            if (textStyle == null) {
                return "ZoneId()";
            }
            return "ZoneText(" + textStyle + ")";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prints or parses a localized pattern.
     */
    static final class LocalizedPrinterParser implements DateTimePrinterParser {
        private final FormatStyle dateStyle;
        private final FormatStyle timeStyle;
        private final Chrono chronology;

        /**
         * Constructor.
         *
         * @param dateStyle  the date style to use, may be null
         * @param timeStyle  the time style to use, may be null
         * @param chronology  the chronology to use, not null
         */
        LocalizedPrinterParser(FormatStyle dateStyle, FormatStyle timeStyle, Chrono chronology) {
            // validated by caller
            this.dateStyle = dateStyle;
            this.timeStyle = timeStyle;
            this.chronology = chronology;
        }

        @Override
        public boolean print(DateTimePrintContext context, StringBuilder buf) {
            return formatter(context.getLocale()).toPrinterParser(false).print(context, buf);
        }

        @Override
        public int parse(DateTimeParseContext context, CharSequence text, int position) {
            return formatter(context.getLocale()).toPrinterParser(false).parse(context, text, position);
        }

        /**
         * Gets the formatter to use.
         *
         * @param locale  the locale to use, not null
         * @return the formatter, not null
         * @throws IllegalArgumentException if the formatter cannot be found
         */
        private DateTimeFormatter formatter(Locale locale) {
            return DateTimeFormatters.getFormatStyleProvider().getFormatter(dateStyle, timeStyle, chronology, locale);
        }

        @Override
        public String toString() {
            return "Localized(" + (dateStyle != null ? dateStyle : "") + "," +
                (timeStyle != null ? timeStyle : "") + "," + chronology.getName() + ")";
        }
    }

}
