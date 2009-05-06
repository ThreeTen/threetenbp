/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;

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
     * The list of indices into the printer/parser list for the start of an optional section.
     */
    private final List<Integer> optionalIndexStack = new ArrayList<Integer>();
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
     * Appends the value of a date-time field to the formatter using a normal
     * output style.
     * <p>
     * The value of the field will be output during a print.
     * If the value cannot be obtained then an exception will be thrown.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param fieldRule  the rule of the field to append, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule is null
     */
    public DateTimeFormatterBuilder appendValue(DateTimeFieldRule fieldRule) {
        checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        NumberPrinterParser pp = new NumberPrinterParser(fieldRule, 1, 10, SignStyle.NORMAL);
        appendInternal(pp, pp);
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
     *
     * @param fieldRule  the rule of the field to append, not null
     * @param width  the width of the printed field, from 1 to 10
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule is null
     * @throws IllegalArgumentException if the width is invalid
     */
    public DateTimeFormatterBuilder appendValue(DateTimeFieldRule fieldRule, int width) {
        checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        if (width < 1 || width > 10) {
            throw new IllegalArgumentException("The width must be from 1 to 10 inclusive but was " + width);
        }
        NumberPrinterParser pp = new NumberPrinterParser(fieldRule, width, width, SignStyle.NOT_NEGATIVE);
        appendInternal(pp, pp);
        return this;
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
     *
     * @param fieldRule  the rule of the field to append, not null
     * @param minWidth  the minimum field width of the printed field, from 1 to 10
     * @param maxWidth  the maximum field width of the printed field, from 1 to 10
     * @param signStyle  the positive/negative output style, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule or sign style is null
     * @throws IllegalArgumentException if the widths are invalid
     */
    public DateTimeFormatterBuilder appendValue(
            DateTimeFieldRule fieldRule, int minWidth, int maxWidth, SignStyle signStyle) {
        checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        checkNotNull(signStyle, "SignStyle must not be null");
        if (minWidth < 1 || minWidth > 10) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 10 inclusive but was " + minWidth);
        }
        if (maxWidth < 1 || maxWidth > 10) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 10 inclusive but was " + maxWidth);
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
     * Appends the fractional value of a date-time field to the formatter.
     * <p>
     * The fractional value of the field will be output including the
     * preceeding decimal point. The preceeding value is not output.
     * The raw value for this field is obtained from
     * {@link DateTimeFieldRule#getFractionalValue getFractionalValue}.
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
     * @param fieldRule  the rule of the field to append, not null
     * @param minWidth  the minimum width of the field excluding the decimal point, from 0 to 9
     * @param maxWidth  the maximum width of the field excluding the decimal point, from 1 to 9
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule or sign style is null
     * @throws IllegalArgumentException if the field has a variable set of valid values
     * @throws IllegalArgumentException if the field has a non-zero minimum
     * @throws IllegalArgumentException if the widths are invalid
     */
    public DateTimeFormatterBuilder appendFraction(
            DateTimeFieldRule fieldRule, int minWidth, int maxWidth) {
        checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        if (fieldRule.isFixedValueSet() == false) {
            throw new IllegalArgumentException("The field does not have a fixed set of values");
        }
        if (fieldRule.getMinimumValue() != 0) {
            throw new IllegalArgumentException("The field does not have a minimum value of zero");
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
        FractionPrinterParser pp = new FractionPrinterParser(fieldRule, minWidth, maxWidth);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the text of a date-time field to the formatter using the full
     * text style.
     * <p>
     * The text of the field will be output during a print.
     * If the value cannot be obtained then an exception will be thrown.
     * If the field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param fieldRule  the rule of the field to append, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule is null
     */
    public DateTimeFormatterBuilder appendText(DateTimeFieldRule fieldRule) {
        return appendText(fieldRule, TextStyle.FULL);
    }

    /**
     * Appends the text of a date-time field to the formatter.
     * <p>
     * The text of the field will be output during a print.
     * If the value cannot be obtained then an exception will be thrown.
     * If the field has no textual representation, then the numeric value will be used.
     * <p>
     * The value will be printed as per the normal print of an integer value.
     * Only negative numbers will be signed. No padding will be added.
     *
     * @param fieldRule  the rule of the field to append, not null
     * @param textStyle  the text style to use, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the field rule or text style is null
     */
    public DateTimeFormatterBuilder appendText(DateTimeFieldRule fieldRule, TextStyle textStyle) {
        checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        checkNotNull(textStyle, "TextStyle must not be null");
        TextPrinterParser pp = new TextPrinterParser(fieldRule, textStyle);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the zone offset, such as '+01:00', to the formatter.
     * <p>
     * The zone offset id will be output during a print.
     * If the offset cannot be obtained then an exception will be thrown.
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
    public DateTimeFormatterBuilder appendOffsetId() {
        return appendOffset("Z", true, true);
    }

    /**
     * Appends the zone offset, such as '+01:00', to the formatter.
     * <p>
     * The zone offset will be output during a print.
     * If the offset cannot be obtained then an exception will be thrown.
     * The output format is controlled by the specified parameters.
     * <p>
     * The UTC text controls what text is printed when the offset is zero.
     * Example values would be 'Z', '+00:00', 'UTC' or 'GMT'.
     * <p>
     * The include colon parameter controls whether a colon should separate the
     * numeric fields or not.
     * <p>
     * The allow seconds parameter controls whether seconds may be output.
     * If false then seconds are never output.
     * If true then seconds are only output if non-zero.
     *
     * @param utcText  the text to use for UTC, not null
     * @param includeColon  whether to include a colon
     * @param allowSeconds  whether to allow seconds
     * @return this, for chaining, never null
     * @throws NullPointerException if the UTC text is null
     */
    public DateTimeFormatterBuilder appendOffset(String utcText, boolean includeColon, boolean allowSeconds) {
        checkNotNull(utcText, "UTC text must not be null");
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser(utcText, includeColon, allowSeconds);
        appendInternal(pp, pp);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the time zone rule id, such as 'Europe/Paris', to the formatter.
     * <p>
     * The time zone id will be output during a print.
     * If the zone cannot be obtained then an exception will be thrown.
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
     * The time zone name will be output during a print.
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
     * @return this, for chaining, never null
     * @throws NullPointerException if the text style is null
     */
    public DateTimeFormatterBuilder appendZoneText(TextStyle textStyle) {
        checkNotNull(textStyle, "TextStyle must not be null");
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
     * <p>
     * If the literal is empty, nothing is added to the formatter.
     *
     * @param literal  the literal to append, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the literal is null
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
     * Appends a formatter to the builder which will optionally print/parse.
     * <p>
     * The formatter will print if data is available for all the fields contained within it.
     * The formatter will parse if the string matches, otherwise no error is returned.
     *
     * @param formatter  the formatter to add, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the formatter is null
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
     * The formatter will print if data is available for all the fields contained within it.
     * The formatter will parse if the string matches, otherwise no error is returned.
     *
     * @param formatter  the formatter to add, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the formatter is null
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
     *  Symbol  Meaning                      Presentation  Examples
     *  ------  -------                      ------------  -------
     *   y       year                         year          1996
     *   D       day of year                  number        189
     *   M       month of year                month         July; Jul; 07
     *   d       day of month                 number        10
     *
     *   Q       quarter of year              number        3
     *   q       month of quarter             number        2
     *
     *   x       week-based-year              year          1996
     *   w       week of week-based-year      number        27
     *   e       day of week                  number        2
     *   E       day of week                  text          Tuesday; Tue
     *   F       week of month                number        3
     *
     *   a       am-pm of day                 text          PM
     *   h       clockhour of am-pm  (1~12)   number        12
     *   K       hour of am-pm (0~11)         number        0
     *
     *   H       hour of day (0~23)           number        0
     *   m       minute of hour               number        30
     *   s       second of minute             number        55
     *   S       milli of second              number        978
     *   n       nano of second               number        987654321
     *
     *   I       time zone ID                 zoneID        America/Los_Angeles
     *   z       time zone name               text          Pacific Standard Time; PST
     *   Z       zone offset                  offset        -0800; -08:00;
     *
     *   '       escape for text              delimiter
     *   ''      single quote                 literal       '
     *   [       optional section start
     *   ]       optional section end
     * </pre>
     * <p>
     * The count of pattern letters determine the format.
     * <p>
     * <b>Text</b>: If the number of pattern letters is 4 or more, the full textual form is used
     * as per {@link TextStyle#FULL}. Otherwise a short form is used, as per {@link TextStyle#SHORT}.
     * <p>
     * <b>Number</b>: If the count of letters is one, then the value is printed using the minimum number
     * of digits and without padding as per {@link #appendValue(DateTimeFieldRule)}. Otherwise, the
     * count of digits is used as the width of the output field as per {@link #appendValue(DateTimeFieldRule, int)}.
     * <p>
     * <b>Year</b>: The count of letters determines the minimum field width below which padding is used.
     * If the count of letters is less than four, then the sign is only output for negative years as per
     * {@link SignStyle#NORMAL}.
     * Otherwise, the sign is output if the pad width is exceeded, as per {@link SignStyle#EXCEEDS_PAD}
     * <p>
     * <b>Month</b>: If the count of letters is 3 or greater, use the Text rules above.
     * Otherwise use the Number rules above.
     * <p>
     * <b>ZoneID</b>: 'Z' outputs offset without a colon, 'ZZ' outputs the offset with a colon, 'ZZZ' or more outputs the zone id.
     * <p>
     * <b>Offset</b>: 'Z' outputs offset without a colon, without seconds and '+0000' as the text for UTC.
     * 'ZZ' outputs the offset with a colon, without seconds and '+00:00' as the text for UTC.
     * 'ZZZ' outputs offset without a colon, with seconds and 'Z' as the text for UTC (ISO-8601 style).
     * 'ZZZZ' outputs the offset with a colon, with seconds and 'Z' as the text for UTC (ISO-8601 style).
     * <p>
     * <b>Zone names</b>: Time zone names ('z') cannot be parsed.
     * <p>
     * <b>Optional section</b>: The optional section markers work exactly like calling {@link #optionalStart()}
     * and {@link #optionalEnd()}.
     * <p>
     * Any unrecognized letter will be output directly.
     * However, since these are reserved, that may change in future versions.
     * Any non-letter character, other than '[', ']' and the single quote will be output directly.
     * Despite this, it is recommended to use single quotes around all characters that you want to
     * output directly to ensure that future changes do not break your application.
     * <p>
     * The pattern string is similar, but not identical, to {@link SimpleDateFormat}.
     * SimpleDateFormat pattern letters 'G', 'W' and 'k' are not available.
     * Pattern letters 'x', 'Q', 'q', 'e', 'n' and 'I' are added.
     * Letters 'y', 'z' and 'Z' have some differences.
     *
     * @param pattern  the pattern to add, not null
     * @return this, for chaining, never null
     * @throws NullPointerException if the pattern is null
     * @throws IllegalArgumentException if the pattern is invalid
     */
    public DateTimeFormatterBuilder appendPattern(String pattern) {
        checkNotNull(pattern, "Pattern must not be null");
        parse(pattern);
        return this;
    }

    private void parse(String pattern) {
        for (int pos = 0; pos < pattern.length(); pos++) {
            char cur = pattern.charAt(pos);
            if (cur == '\'') {
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
                if (optionalIndexStack.isEmpty()) {
                    throw new IllegalArgumentException("Pattern invalid as it contains ] without previous [");
                }
                optionalEnd();
                
            } else if ((cur >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z')) {
                // parse patterns
                int start = pos++;
                for ( ; pos < pattern.length() && pattern.charAt(pos) == cur; pos++);  // short loop
                int count = pos - start;
                DateTimeFieldRule rule = RULE_MAP.get(cur);
                if (rule == null) {
                    if (cur == 'z') {
                        if (count < 4) {
                            appendZoneText(TextStyle.SHORT);
                        } else {
                            appendZoneText(TextStyle.FULL);
                        }
                    } else if (cur == 'I') {
                        appendZoneId();
                    } else if (cur == 'Z') {
                        appendOffset(count == 1 ? "+0000" : (count == 2 ? "+00:00" : "Z"), count == 2 || count >= 4, count >= 3);
                    } else {
                        appendLiteral(pattern.substring(start, pos));
                    }
                } else {
                    parseRule(cur, count, rule);
                }
                pos--;
            } else {
                appendLiteral(cur);
            }
        }
    }

    private void parseRule(char cur, int count, DateTimeFieldRule rule) {
        switch (cur) {
            case 'x':
            case 'y':
                if (count < 4) {
                    appendValue(rule, count, 10, SignStyle.NORMAL);
                } else {
                    appendValue(rule, count, 10, SignStyle.EXCEEDS_PAD);
                }
                break;
            case 'M':
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
                    default:
                        appendText(rule, TextStyle.FULL);
                        break;
                }
                break;
            case 'a':
            case 'E':
                if (count < 4) {
                    appendText(rule, TextStyle.SHORT);
                } else {
                    appendText(rule, TextStyle.FULL);
                }
                break;
            default:
                if (count == 1) {
                    appendValue(rule);
                } else {
                    appendValue(rule, count);
                }
                break;
        }
    }

    /** Map of letters to rules. */
    private static final Map<Character, DateTimeFieldRule> RULE_MAP = new HashMap<Character, DateTimeFieldRule>();
    static {
//        RULE_MAP.put('G', ISOChronology.eraRule());
        RULE_MAP.put('y', ISOChronology.yearRule());
        RULE_MAP.put('x', ISOChronology.weekBasedYearRule());  // new
        RULE_MAP.put('Q', ISOChronology.quarterOfYearRule());  // new
        RULE_MAP.put('M', ISOChronology.monthOfYearRule());
        RULE_MAP.put('q', ISOChronology.monthOfQuarterRule());  // new
        RULE_MAP.put('w', ISOChronology.weekOfWeekBasedYearRule());
//        RULE_MAP.put('W', ISOChronology.weekOfWeekBasedMonthRule());
        RULE_MAP.put('D', ISOChronology.dayOfYearRule());
        RULE_MAP.put('d', ISOChronology.dayOfMonthRule());
        RULE_MAP.put('F', ISOChronology.weekOfMonthRule());
        RULE_MAP.put('E', ISOChronology.dayOfWeekRule());
        RULE_MAP.put('e', ISOChronology.dayOfWeekRule());
        RULE_MAP.put('a', ISOChronology.amPmOfDayRule());
        RULE_MAP.put('H', ISOChronology.hourOfDayRule());
//        RULE_MAP.put('k', ISOChronology.clockHourOfDayRule());
        RULE_MAP.put('K', ISOChronology.hourOfAmPmRule());
//        RULE_MAP.put('h', ISOChronology.clockHourOfAmPmRule());  // TODO
        RULE_MAP.put('m', ISOChronology.minuteOfHourRule());
        RULE_MAP.put('s', ISOChronology.secondOfMinuteRule());
        RULE_MAP.put('S', ISOChronology.milliOfSecondRule());
        RULE_MAP.put('n', ISOChronology.nanoOfSecondRule());  // new
        // reserved - z,Z,I
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
     * Mark the start of an optional section.
     * <p>
     * The output of printing can include optional sections, which may be nested.
     * An optional section is started by calling this method and ended by calling
     * {@link #optionalEnd()} or by ending the build process.
     * <p>
     * All elements in the optional section are treated as optional.
     * During printing, the section is only output if data is available in the
     * <code>Calendrical</code> for all the elements in the section.
     * During parsing, the whole section may be missing from the parsed string.
     * <p>
     * For example, consider a builder setup as
     * <code>builder.appendValue(hourRule,2).optionalStart().appendValue(minuteRule,2)</code>.
     * The optional section ends automatically at the end of the builder.
     * During printing, the minute will only be output if its value can be obtained from the calendrical.
     * During parsing, the input will be successfully parsed whether the minute is present or not.
     *
     * @return this, for chaining, never null
     */
    public DateTimeFormatterBuilder optionalStart() {
        optionalIndexStack.add(printers.size());
        return this;
    }

    /**
     * Ends an optional section.
     * <p>
     * The output of printing can include optional sections, which may be nested.
     * An optional section is started by calling {@link #optionalStart()} and ended
     * using this method (or at the end of the builder).
     * <p>
     * Calling this method without having previously called <code>optionalStart</code>
     * will throw an exception.
     * Calling this method immediately after calling <code>optionalStart</code> has no effect
     * on the formatter other than ending the (empty) optional section.
     * <p>
     * All elements in the optional section are treated as optional.
     * During printing, the section is only output if data is available in the
     * <code>Calendrical</code> for all the elements in the section.
     * During parsing, the whole section may be missing from the parsed string.
     * <p>
     * For example, consider a builder setup as
     * <code>builder.appendValue(hourRule,2).optionalStart().appendValue(minuteRule,2).optionalEnd()</code>.
     * During printing, the minute will only be output if its value can be obtained from the calendrical.
     * During parsing, the input will be successfully parsed whether the minute is present or not.
     *
     * @return this, for chaining, never null
     * @throws IllegalStateException if there was no previous call to <code>optionalStart</code>
     */
    public DateTimeFormatterBuilder optionalEnd() {
        if (optionalIndexStack.isEmpty()) {
            throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
        }
        int startIndex = optionalIndexStack.remove(optionalIndexStack.size() - 1);
        if (startIndex < printers.size()) {
            optionalEnd(startIndex, printers, parsers);
        }
        return this;
    }

    /**
     * Ends the optional section.
     * @param startIndex  the index of the start of the optional section, valid
     * @param printers  the printers to alter, not null
     * @param parsers  the parsers to alter, not null
     */
    private void optionalEnd(int startIndex, List<DateTimePrinter> printers, List<DateTimeParser> parsers) {
        List<DateTimePrinter> optionalPrinters = printers.subList(startIndex, printers.size());
        List<DateTimeParser> optionalParsers = parsers.subList(startIndex, parsers.size());
        CompositePrinterParser cpp = new CompositePrinterParser(optionalPrinters, optionalParsers, true);
        optionalPrinters.clear();
        optionalParsers.clear();
        printers.add(cpp.isPrintSupported() ? cpp : null);
        parsers.add(cpp.isParseSupported() ? cpp : null);
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
            if (printer != null || parser != null) {
                printer = new PadPrinterParserDecorator(printer, parser, padNextWidth, padNextChar);
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
     * Completes this builder by creating the DateTimeFormatter.
     * <p>
     * This builder can still be used after creating the formatter, and the
     * created formatter will be unaffected.
     * <p>
     * Any optional sections that have been started but not ended will be considered
     * to have been ended in the returned formatter. However, this builder itself will
     * still have the optional sections as open.
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
     * <p>
     * Any optional sections that have been started but not ended will be considered
     * to have been ended in the returned formatter. However, this builder itself will
     * still have the optional sections as open.
     *
     * @param locale  the locale to use for formatting, not null
     * @return the created formatter, never null
     */
    public DateTimeFormatter toFormatter(Locale locale) {
        FormatUtil.checkNotNull(locale, "locale");
        
        if (optionalIndexStack.size() > 0) {
            List<DateTimePrinter> printers = new ArrayList<DateTimePrinter>(this.printers);
            List<DateTimeParser> parsers = new ArrayList<DateTimeParser>(this.parsers);
            for (int i = optionalIndexStack.size() - 1; i >= 0; i--) {
                int startIndex = optionalIndexStack.get(i);
                optionalEnd(startIndex, printers, parsers);
            }
            return new DateTimeFormatter(locale, new CompositePrinterParser(printers, parsers, false));
        } else {
            return new DateTimeFormatter(locale, new CompositePrinterParser(printers, parsers, false));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Enumeration of ways to handle the positive/negative sign.
     *
     * @author Stephen Colebourne
     */
    public enum SignStyle {
        /**
         * Style to output the sign only if the value is negative.
         * In strict parsing, the positive sign will be rejected.
         */
        NORMAL,
        /**
         * Style to always output the sign, where zero will output '+'.
         * In strict parsing, the absence of a sign will be rejected.
         */
        ALWAYS,
        /**
         * Style to never output sign, only outputting the absolute value.
         * In strict parsing, any sign will be rejected.
         */
        NEVER,
        /**
         * Style to block negative values, throwing an exception on printing.
         * In strict parsing, any sign will be rejected.
         */
        NOT_NEGATIVE,
        /**
         * Style to always output the sign if the value exceeds the pad width.
         * A negative value will always output the '-' sign.
         * In strict parsing, the sign will be rejected unless the pad width is exceeded.
         */
        EXCEEDS_PAD;
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
