/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.format;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.ZoneId;
import org.threeten.bp.chrono.Chronology;
import org.threeten.bp.format.DateTimeFormatterBuilder.CompositePrinterParser;
import org.threeten.bp.format.DateTimeParseContext.Parsed;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalAccessor;

/**
 * Formatter for printing and parsing date-time objects.
 * <p>
 * This class provides the main application entry point for printing and parsing.
 * Common instances of {@code DateTimeFormatter} are provided by {@link DateTimeFormatters}.
 * For more complex formatters, a {@link DateTimeFormatterBuilder builder} is provided.
 * <p>
 * In most cases, it is not necessary to use this class directly when formatting.
 * The main date-time classes provide two methods - one for printing,
 * {@code toString(DateTimeFormatter formatter)}, and one for parsing,
 * {@code parse(CharSequence text, DateTimeFormatter formatter)}.
 * For example:
 * <pre>
 *  String text = date.toString(formatter);
 *  LocalDate date = LocalDate.parse(text, formatter);
 * </pre>
 * Some aspects of printing and parsing are dependent on the locale.
 * The locale can be changed using the {@link #withLocale(Locale)} method
 * which returns a new formatter in the requested locale.
 * <p>
 * Some applications may need to use the older {@link Format} class for formatting.
 * The {@link #toFormat()} method returns an implementation of the old API.
 *
 * <h3>Specification for implementors</h3>
 * This class is immutable and thread-safe.
 */
public final class DateTimeFormatter {

    /**
     * The printer and/or parser to use, not null.
     */
    private final CompositePrinterParser printerParser;
    /**
     * The locale to use for formatting, not null.
     */
    private final Locale locale;
    /**
     * The symbols to use for formatting, not null.
     */
    private final DateTimeFormatSymbols symbols;
    /**
     * The chronology to use for formatting, null for no override.
     */
    private final Chronology chrono;
    /**
     * The zone to use for formatting, null for no override.
     */
    private final ZoneId zone;

    /**
     * Constructor.
     *
     * @param printerParser  the printer/parser to use, not null
     * @param locale  the locale to use, not null
     * @param symbols  the symbols to use, not null
     * @param chrono  the chronology to use, null for no override
     * @param zone  the zone to use, null for no override
     */
    DateTimeFormatter(CompositePrinterParser printerParser, Locale locale,
                      DateTimeFormatSymbols symbols, Chronology chrono, ZoneId zone) {
        this.printerParser = Objects.requireNonNull(printerParser, "printerParser");
        this.locale = Objects.requireNonNull(locale, "locale");
        this.symbols = Objects.requireNonNull(symbols, "symbols");
        this.chrono = chrono;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to be used during formatting.
     * <p>
     * This is used to lookup any part of the formatter needing specific
     * localization, such as the text or localized pattern.
     *
     * @return the locale of this formatter, not null
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns a copy of this formatter with a new locale.
     * <p>
     * This is used to lookup any part of the formatter needing specific
     * localization, such as the text or localized pattern.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param locale  the new locale, not null
     * @return a formatter based on this formatter with the requested locale, not null
     */
    public DateTimeFormatter withLocale(Locale locale) {
        if (this.locale.equals(locale)) {
            return this;
        }
        return new DateTimeFormatter(printerParser, locale, symbols, chrono, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the set of symbols to be used during formatting.
     *
     * @return the locale of this formatter, not null
     */
    public DateTimeFormatSymbols getSymbols() {
        return symbols;
    }

    /**
     * Returns a copy of this formatter with a new set of symbols.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param symbols  the new symbols, not null
     * @return a formatter based on this formatter with the requested symbols, not null
     */
    public DateTimeFormatter withSymbols(DateTimeFormatSymbols symbols) {
        if (this.symbols.equals(symbols)) {
            return this;
        }
        return new DateTimeFormatter(printerParser, locale, symbols, chrono, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the overriding chronology to be used during formatting.
     * <p>
     * This returns the override chronology, used to convert dates.
     * By default, a formatter has no override chronology, returning null.
     * See {@link #withChrono(Chronology)} for more details on overriding.
     *
     * @return the chronology of this formatter, null if no override
     */
    public Chronology getChrono() {
        return chrono;
    }

    /**
     * Returns a copy of this formatter with a new override chronology.
     * <p>
     * This returns a formatter with similar state to this formatter but
     * with the override chronology set.
     * By default, a formatter has no override chronology, returning null.
     * <p>
     * If an override is added, then any date that is printed or parsed will be affected.
     * <p>
     * When printing, if the {@code Temporal} object contains a date then it will
     * be converted to a date in the override chronology.
     * Any time or zone will be retained unless overridden.
     * The converted result will behave in a manner equivalent to an implementation
     * of {@code ChronoLocalDate},{@code ChronoLocalDateTime} or {@code ChronoZonedDateTime}.
     * <p>
     * When parsing, the override chronology will be used to interpret the
     * {@linkplain ChronoField fields} into a date unless the
     * formatter directly parses a valid chronology.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param chrono  the new chronology, not null
     * @return a formatter based on this formatter with the requested override chronology, not null
     */
    public DateTimeFormatter withChrono(Chronology chrono) {
        if (Objects.equals(this.chrono, chrono)) {
            return this;
        }
        return new DateTimeFormatter(printerParser, locale, symbols, chrono, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the overriding zone to be used during formatting.
     * <p>
     * This returns the override zone, used to convert instants.
     * By default, a formatter has no override zone, returning null.
     * See {@link #withZone(ZoneId)} for more details on overriding.
     *
     * @return the chronology of this formatter, null if no override
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Returns a copy of this formatter with a new override zone.
     * <p>
     * This returns a formatter with similar state to this formatter but
     * with the override zone set.
     * By default, a formatter has no override zone, returning null.
     * <p>
     * If an override is added, then any instant that is printed or parsed will be affected.
     * <p>
     * When printing, if the {@code Temporal} object contains an instant then it will
     * be converted to a zoned date-time using the override zone.
     * If the input has a chronology then it will be retained unless overridden.
     * If the input does not have a chronology, such as {@code Instant}, then
     * the ISO chronology will be used.
     * The converted result will behave in a manner equivalent to an implementation
     * of {@code ChronoZonedDateTime}.
     * <p>
     * When parsing, the override zone will be used to interpret the
     * {@linkplain ChronoField fields} into an instant unless the
     * formatter directly parses a valid zone.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the new override zone, not null
     * @return a formatter based on this formatter with the requested override zone, not null
     */
    public DateTimeFormatter withZone(ZoneId zone) {
        if (Objects.equals(this.zone, zone)) {
            return this;
        }
        return new DateTimeFormatter(printerParser, locale, symbols, chrono, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Prints a date-time object using this formatter.
     * <p>
     * This prints the date-time to a String using the rules of the formatter.
     *
     * @param temporal  the temporal object to print, not null
     * @return the printed string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String print(TemporalAccessor temporal) {
        StringBuilder buf = new StringBuilder(32);
        printTo(temporal, buf);
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Prints a date-time object to an {@code Appendable} using this formatter.
     * <p>
     * This prints the date-time to the specified destination.
     * {@link Appendable} is a general purpose interface that is implemented by all
     * key character output classes including {@code StringBuffer}, {@code StringBuilder},
     * {@code PrintStream} and {@code Writer}.
     * <p>
     * Although {@code Appendable} methods throw an {@code IOException}, this method does not.
     * Instead, any {@code IOException} is wrapped in a runtime exception.
     *
     * @param temporal  the temporal object to print, not null
     * @param appendable  the appendable to print to, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public void printTo(TemporalAccessor temporal, Appendable appendable) {
        Objects.requireNonNull(temporal, "temporal");
        Objects.requireNonNull(appendable, "appendable");
        try {
            DateTimePrintContext context = new DateTimePrintContext(temporal, this);
            if (appendable instanceof StringBuilder) {
                printerParser.print(context, (StringBuilder) appendable);
            } else {
                // buffer output to avoid writing to appendable in case of error
                StringBuilder buf = new StringBuilder(32);
                printerParser.print(context, buf);
                appendable.append(buf);
            }
        } catch (IOException ex) {
            throw new DateTimeException(ex.getMessage(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Fully parses the text producing a temporal object.
     * <p>
     * This parses the entire text producing a temporal object.
     * It is typically more useful to use {@link #parse(CharSequence, TemporalQuery)}.
     * The result of this method is {@code TemporalAccessor} which has been resolved,
     * applying basic validation checks to help ensure a valid date-time.
     * <p>
     * If the parse completes without reading the entire length of the text,
     * or a problem occurs during parsing or merging, then an exception is thrown.
     *
     * @param text  the text to parse, not null
     * @return the parsed temporal object, not null
     * @throws DateTimeParseException if unable to parse the requested result
     */
    public TemporalAccessor parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        try {
            return parseToBuilder(text, null).resolve();
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw createError(text, ex);
        }
    }

    /**
     * Parses the text using this formatter, providing control over the text position.
     * <p>
     * This parses the text without requiring the parse to start from the beginning
     * of the string or finish at the end.
     * The result of this method is {@code TemporalAccessor} which has been resolved,
     * applying basic validation checks to help ensure a valid date-time.
     * <p>
     * The text will be parsed from the specified start {@code ParsePosition}.
     * The entire length of the text does not have to be parsed, the {@code ParsePosition}
     * will be updated with the index at the end of parsing.
     * <p>
     * The operation of this method is slightly different to similar methods using
     * {@code ParsePosition} on {@code java.text.Format}. That class will return
     * errors using the error index on the {@code ParsePosition}. By contrast, this
     * method will throw a {@link DateTimeParseException} if an error occurs, with
     * the exception containing the error index.
     * This change in behavior is necessary due to the increased complexity of
     * parsing and resolving dates/times in this API.
     * <p>
     * If the formatter parses the same field more than once with different values,
     * the result will be an error.
     *
     * @param text  the text to parse, not null
     * @param position  the position to parse from, updated with length parsed
     *  and the index of any error, not null
     * @return the parsed temporal object, not null
     * @throws DateTimeParseException if unable to parse the requested result
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public TemporalAccessor parse(CharSequence text, ParsePosition position) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(position, "position");
        try {
            return parseToBuilder(text, position).resolve();
        } catch (DateTimeParseException | IndexOutOfBoundsException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw createError(text, ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Fully parses the text producing an object of the specified type.
     * <p>
     * Most applications should use this method for parsing.
     * It parses the entire text to produce the required date-time.
     * For example:
     * <pre>
     *  LocalDateTime dt = parser.parse(str, LocalDateTime.class);
     * </pre>
     * If the parse completes without reading the entire length of the text,
     * or a problem occurs during parsing or merging, then an exception is thrown.
     *
     * @param <T> the type to extract
     * @param text  the text to parse, not null
     * @param type  the type to extract, not null
     * @return the parsed date-time, not null
     * @throws DateTimeParseException if unable to parse the requested result
     */
    public <T> T parse(CharSequence text, Class<T> type) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(type, "type");
        try {
            DateTimeBuilder builder = parseToBuilder(text, null).resolve();
            return builder.build(type);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw createError(text, ex);
        }
    }

    /**
     * Fully parses the text producing an object of one of the specified types.
     * <p>
     * This parse method is convenient for use when the parser can handle optional elements.
     * For example, a pattern of 'yyyy[-MM[-dd]]' can be fully parsed to a {@code LocalDate},
     * or partially parsed to a {@code YearMonth} or a {@code Year}.
     * The types must be specified in order, starting from the best matching full-parse option
     * and ending with the worst matching minimal parse option.
     * <p>
     * The result is associated with the first type that successfully parses.
     * Normally, applications will use {@code instanceof} to check the result.
     * For example:
     * <pre>
     *  TemporalAccessor dt = parser.parseBest(str, LocalDate.class, YearMonth.class);
     *  if (dt instanceof LocalDate) {
     *   ...
     *  } else {
     *   ...
     *  }
     * </pre>
     * If the parse completes without reading the entire length of the text,
     * or a problem occurs during parsing or merging, then an exception is thrown.
     *
     * @param text  the text to parse, not null
     * @param types  the types to attempt to parse to, which must implement {@code TemporalAccessor}, not null
     * @return the parsed date-time, not null
     * @throws IllegalArgumentException if less than 2 types are specified
     * @throws DateTimeParseException if unable to parse the requested result
     */
    public TemporalAccessor parseBest(CharSequence text, Class<?>... types) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(types, "types");
        if (types.length < 2) {
            throw new IllegalArgumentException("At least two types must be specified");
        }
        try {
            DateTimeBuilder builder = parseToBuilder(text, null).resolve();
            for (Class<?> type : types) {
                try {
                    return (TemporalAccessor) builder.build(type);
                } catch (RuntimeException ex) {
                    // continue
                }
            }
            throw new DateTimeException("Unable to convert parsed text to any specified type: " + Arrays.toString(types));
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw createError(text, ex);
        }
    }

    private DateTimeParseException createError(CharSequence text, RuntimeException ex) {
        String abbr = "";
        if (text.length() > 64) {
            abbr = text.subSequence(0, 64).toString() + "...";
        } else {
            abbr = text.toString();
        }
        return new DateTimeParseException("Text '" + abbr + "' could not be parsed: " + ex.getMessage(), text, 0, ex);
    }

    //-----------------------------------------------------------------------
    /**
     * Parses the text to a builder.
     * <p>
     * This parses to a {@code DateTimeBuilder} ensuring that the text is fully parsed.
     * This method throws {@link DateTimeParseException} if unable to parse, or
     * some other {@code DateTimeException} if another date/time problem occurs.
     *
     * @param text  the text to parse, not null
     * @param position  the position to parse from, updated with length parsed
     *  and the index of any error, null if parsing whole string
     * @return the engine representing the result of the parse, not null
     * @throws DateTimeParseException if the parse fails
     */
    private DateTimeBuilder parseToBuilder(final CharSequence text, final ParsePosition position) {
        ParsePosition pos = (position != null ? position : new ParsePosition(0));
        Parsed result = parseUnresolved0(text, pos);
        if (result == null || pos.getErrorIndex() >= 0 || (position == null && pos.getIndex() < text.length())) {
            String abbr = "";
            if (text.length() > 64) {
                abbr = text.subSequence(0, 64).toString() + "...";
            } else {
                abbr = text.toString();
            }
            if (pos.getErrorIndex() >= 0) {
                throw new DateTimeParseException("Text '" + abbr + "' could not be parsed at index " +
                        pos.getErrorIndex(), text, pos.getErrorIndex());
            } else {
                throw new DateTimeParseException("Text '" + abbr + "' could not be parsed, unparsed text found at index " +
                        pos.getIndex(), text, pos.getIndex());
            }
        }
        return result.resolveFields().toBuilder();
    }

    /**
     * Parses the text using this formatter, without resolving the result, intended
     * for advanced use cases.
     * <p>
     * Parsing is implemented as a two-phase operation.
     * First, the text is parsed using the layout defined by the formatter, producing
     * a {@code Map} of field to value, a {@code ZoneId} and a {@code Chronology}.
     * Second, the parsed data is <em>resolved</em>, by validating, combining and
     * simplifying the various fields into more useful ones.
     * This method performs the parsing stage but not the resolving stage.
     * <p>
     * The result of this method is {@code TemporalAccessor} which represents the
     * data as seen in the input. Values are not validated, thus parsing a date string
     * of '2012-00-65' would result in a temporal with three fields - year of '2012',
     * month of '0' and day-of-month of '65'.
     * <p>
     * The text will be parsed from the specified start {@code ParsePosition}.
     * The entire length of the text does not have to be parsed, the {@code ParsePosition}
     * will be updated with the index at the end of parsing.
     * <p>
     * Errors are returned using the error index field of the {@code ParsePosition}
     * instead of {@code DateTimeParseException}.
     * The returned error index will be set to an index indicative of the error.
     * Callers must check for errors before using the context.
     * <p>
     * If the formatter parses the same field more than once with different values,
     * the result will be an error.
     * <p>
     * This method is intended for advanced use cases that need access to the
     * internal state during parsing. Typical application code should use
     * {@link #parse(CharSequence, TemporalQuery)} or the parse method on the target type.
     *
     * @param text  the text to parse, not null
     * @param position  the position to parse from, updated with length parsed
     *  and the index of any error, not null
     * @return the parsed text, null if the parse results in an error
     * @throws DateTimeException if some problem occurs during parsing
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public TemporalAccessor parseUnresolved(CharSequence text, ParsePosition position) {
        return parseUnresolved0(text, position);
    }

    private Parsed parseUnresolved0(CharSequence text, ParsePosition position) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(position, "position");
        DateTimeParseContext context = new DateTimeParseContext(this);
        int pos = position.getIndex();
        pos = printerParser.parse(context, text, pos);
        if (pos < 0) {
            position.setErrorIndex(~pos);  // index not updated from input
            return null;
        }
        position.setIndex(pos);  // errorIndex not updated from input
        return context.toParsed();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the formatter as a composite printer parser.
     *
     * @param optional  whether the printer/parser should be optional
     * @return the printer/parser, not null
     */
    CompositePrinterParser toPrinterParser(boolean optional) {
        return printerParser.withOptional(optional);
    }

    /**
     * Returns this formatter as a {@code java.text.Format} instance.
     * <p>
     * The returned {@link Format} instance will print any {@link TemporalAccessor}
     * and parses to a resolved {@link DateTimeBuilder}.
     * <p>
     * Exceptions will follow the definitions of {@code Format}, see those methods
     * for details about {@code IllegalArgumentException} during formatting and
     * {@code ParseException} or null during parsing.
     * The format does not support attributing of the returned format string.
     *
     * @return this formatter as a classic format instance, not null
     */
    public Format toFormat() {
        return new ClassicFormat(this, null);
    }

    /**
     * Returns this formatter as a {@code java.text.Format} instance that will
     * parse to the specified type.
     * <p>
     * The returned {@link Format} instance will print any {@link TemporalAccessor}
     * and parses to the type specified.
     * The type must be one that is supported by {@link #parse}.
     * <p>
     * Exceptions will follow the definitions of {@code Format}, see those methods
     * for details about {@code IllegalArgumentException} during formatting and
     * {@code ParseException} or null during parsing.
     * The format does not support attributing of the returned format string.
     *
     * @param parseType  the type to parse to, not null
     * @return this formatter as a classic format instance, not null
     */
    public Format toFormat(Class<?> parseType) {
        Objects.requireNonNull(parseType, "parseType");
        return new ClassicFormat(this, parseType);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a description of the underlying formatters.
     *
     * @return a description of this formatter, not null
     */
    @Override
    public String toString() {
        String pattern = printerParser.toString();
        return pattern.startsWith("[") ? pattern : pattern.substring(1, pattern.length() - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the classic Java Format API.
     * @serial exclude
     */
    @SuppressWarnings("serial")  // not actually serializable
    static class ClassicFormat extends Format {
        /** The formatter. */
        private final DateTimeFormatter formatter;
        /** The type to be parsed. */
        private final Class<?> parseType;
        /** Constructor. */
        public ClassicFormat(DateTimeFormatter formatter, Class<?> parseType) {
            this.formatter = formatter;
            this.parseType = parseType;
        }

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Objects.requireNonNull(obj, "obj");
            Objects.requireNonNull(toAppendTo, "toAppendTo");
            Objects.requireNonNull(pos, "pos");
            if (obj instanceof TemporalAccessor == false) {
                throw new IllegalArgumentException("Format target must implement TemporalAccessor");
            }
            pos.setBeginIndex(0);
            pos.setEndIndex(0);
            try {
                formatter.printTo((TemporalAccessor) obj, toAppendTo);
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
            return toAppendTo;
        }
        @Override
        public Object parseObject(String text) throws ParseException {
            Objects.requireNonNull(text, "text");
            try {
                if (parseType == null) {
                    return formatter.parseToBuilder(text, null).resolve();
                }
                return formatter.parse(text, parseType);
            } catch (DateTimeParseException ex) {
                throw new ParseException(ex.getMessage(), ex.getErrorIndex());
            } catch (RuntimeException ex) {
                throw (ParseException) new ParseException(ex.getMessage(), 0).initCause(ex);
            }
        }
        @Override
        public Object parseObject(String text, ParsePosition pos) {
            Objects.requireNonNull(text, "text");
            Parsed unresolved;
            try {
                unresolved = formatter.parseUnresolved0(text, pos);
            } catch (IndexOutOfBoundsException ex) {
                if (pos.getErrorIndex() < 0) {
                    pos.setErrorIndex(0);
                }
                return null;
            }
            if (unresolved == null) {
                if (pos.getErrorIndex() < 0) {
                    pos.setErrorIndex(0);
                }
                return null;
            }
            try {
                DateTimeBuilder builder = unresolved.resolveFields().toBuilder().resolve();
                if (parseType == null) {
                    return builder;
                }
                return builder.build(parseType);
            } catch (RuntimeException ex) {
                pos.setErrorIndex(0);
                return null;
            }
        }
    }

}
