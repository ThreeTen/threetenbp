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

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.CalendricalParseException;
import javax.time.DateTimes;
import javax.time.calendrical.CalendricalFormatter;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.format.DateTimeFormatterBuilder.CompositePrinterParser;

/**
 * Formatter for printing and parsing calendricals.
 * <p>
 * This class provides the main application entry point for printing and parsing.
 * Instances of DateTimeFormatter are constructed using DateTimeFormatterBuilder
 * or by using one of the predefined constants on DateTimeFormatters.
 * <p>
 * Some aspects of printing and parsing are dependent on the locale.
 * The locale can be changed using the {@link #withLocale(Locale)} method
 * which returns a new formatter in the requested locale.
 * <p>
 * Some applications may need to use the older {@link Format} class for formatting.
 * The {@link #toFormat()} method returns an implementation of the old API.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class DateTimeFormatter implements CalendricalFormatter {

    /**
     * The locale to use for formatting, not null.
     */
    private final Locale locale;
    /**
     * The symbols to use for formatting, not null.
     */
    private final DateTimeFormatSymbols symbols;
    /**
     * The printer and/or parser to use, not null.
     */
    private final CompositePrinterParser printerParser;

    /**
     * Constructor.
     *
     * @param printerParser  the printer/parser to use, not null
     * @param locale  the locale to use, not null
     * @param symbols  the symbols to use, not null
     */
    DateTimeFormatter(CompositePrinterParser printerParser, Locale locale, DateTimeFormatSymbols symbols) {
        this.locale = locale;
        this.symbols = symbols;
        this.printerParser = printerParser;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to be used during formatting.
     *
     * @return the locale of this formatter, not null
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns a copy of this formatter with a new locale.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param locale  the new locale, not null
     * @return a {@code DateTimeFormatter} based on this one with the requested locale, not null
     */
    public DateTimeFormatter withLocale(Locale locale) {
        DateTimes.checkNotNull(locale, "Locale must not be null");
        if (locale.equals(this.locale)) {
            return this;
        }
        return new DateTimeFormatter(printerParser, locale, symbols);
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
     * @return a {@code DateTimeFormatter} based on this one with the requested symbols, not null
     */
    public DateTimeFormatter withSymbols(DateTimeFormatSymbols symbols) {
        DateTimes.checkNotNull(symbols, "DateTimeFormatSymbols must not be null");
        if (symbols.equals(this.symbols)) {
            return this;
        }
        return new DateTimeFormatter(printerParser, locale, symbols);
    }

    //-----------------------------------------------------------------------
    /**
     * Prints the calendrical using this formatter.
     * <p>
     * This prints the calendrical to a String using the rules of the formatter.
     *
     * @param calendrical  the calendrical to print, not null
     * @return the printed string, not null
     * @throws CalendricalException if an error occurs during printing
     */
    public String print(DateTime calendrical) {
        StringBuilder buf = new StringBuilder(32);
        printTo(calendrical, buf);
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Prints the calendrical to an {@code Appendable} using this formatter.
     * <p>
     * This prints the calendrical to the specified destination.
     * {@link Appendable} is a general purpose interface that is implemented by all
     * key character output classes including {@code StringBuffer}, {@code StringBuilder},
     * {@code PrintStream} and {@code Writer}.
     * <p>
     * Although {@code Appendable} methods throw an {@code IOException}, this method does not.
     * Instead, any {@code IOException} is wrapped in a runtime exception.
     * See {@link CalendricalPrintException#rethrowIOException()} for a means
     * to extract the {@code IOException}.
     *
     * @param calendrical  the calendrical to print, not null
     * @param appendable  the appendable to print to, not null
     * @throws CalendricalException if an error occurs during printing
     */
    public void printTo(DateTime calendrical, Appendable appendable) {
        DateTimes.checkNotNull(calendrical, "Calendrical must not be null");
        DateTimes.checkNotNull(appendable, "Appendable must not be null");
        try {
            DateTimePrintContext context = new DateTimePrintContext(calendrical, locale, symbols);
            if (appendable instanceof StringBuilder) {
                printerParser.print(context, (StringBuilder) appendable);
            } else {
                // buffer output to avoid writing to appendable in case of error
                StringBuilder buf = new StringBuilder(32);
                printerParser.print(context, buf);
                appendable.append(buf);
            }
        } catch (IOException ex) {
            throw new CalendricalPrintException(ex.getMessage(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Fully parses the text producing an object of the specified type.
     * <p>
     * Most applications should use this method for parsing.
     * It parses the entire text to produce the required calendrical value.
     * For example:
     * <pre>
     * LocalDateTime dt = parser.parse(str, LocalDateTime.class);
     * </pre>
     * If the parse completes without reading the entire length of the text,
     * or a problem occurs during parsing or merging, then an exception is thrown.
     *
     * @param <T> the type to extract
     * @param text  the text to parse, not null
     * @param type  the type to extract, not null
     * @return the parsed calendrical, not null
     * @throws CalendricalParseException if the parse fails
     */
    @Override
    public <T> T parse(CharSequence text, Class<T> type) {
        DateTimes.checkNotNull(text, "Text must not be null");
        DateTimes.checkNotNull(type, "Class must not be null");
        String str = text.toString();  // parsing whole String, so this makes sense
        try {
            DateTimeBuilder builder = parseToBuilder(str);
            T result = builder.resolve().extract(type);
            if (result == null) {
                throw new CalendricalException("Unable to convert parsed text to " + type);
            }
            return result;
        } catch (UnsupportedOperationException ex) {
            throw ex;
        } catch (CalendricalParseException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw createError(str, ex);
        }
    }

    /**
     * Fully parses the text producing an object of one of the specified types.
     * <p>
     * This parse method is convenient for use when the parser can handle optional elements.
     * For example, a pattern of 'yyyy-MM[-dd[Z]]' can be fully parsed to an {@code OffsetDate},
     * or partially parsed to a {@code LocalDate} or a {@code YearMonth}.
     * The types must be specified in order, starting from the best matching full-parse option
     * and ending with the worst matching minimal parse option.
     * <p>
     * The result is associated with the first type that successfully parses.
     * Normally, applications will use {@code instanceof} to check the result.
     * For example:
     * <pre>
     * Calendrical dt = parser.parse(str, OffsetDate.class, LocalDate.class);
     * if (dt instanceof OffsetDate) {
     *  ...
     * } else {
     *  ...
     * }
     * </pre>
     * If the parse completes without reading the entire length of the text,
     * or a problem occurs during parsing or merging, then an exception is thrown.
     *
     * @param text  the text to parse, not null
     * @param types  the types to attempt to parse to
     * @return the parsed calendrical, not null
     * @throws IllegalArgumentException if less than 2 types are specified
     * @throws CalendricalParseException if the parse fails
     */
    public DateTime parseBest(CharSequence text, Class<?>... types) {
        DateTimes.checkNotNull(text, "Text must not be null");
        DateTimes.checkNotNull(types, "Class array must not be null");
        if (types.length < 2) {
            throw new IllegalArgumentException("At least two types must be specified");
        }
        String str = text.toString();  // parsing whole String, so this makes sense
        try {
            DateTimeBuilder builder = parseToBuilder(str).resolve();
            for (Class<?> type : types) {
                DateTime cal = (DateTime) builder.extract(type);
                if (cal != null) {
                    return cal;
                }
            }
            throw new CalendricalException("Unable to convert parsed text to any specified type: " + Arrays.toString(types));
        } catch (UnsupportedOperationException ex) {
            throw ex;
        } catch (CalendricalParseException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw createError(str, ex);
        }
    }

    private CalendricalParseException createError(String str, RuntimeException ex) {
        String abbr = str;
        if (abbr.length() > 64) {
            abbr = abbr.substring(0, 64) + "...";
        }
        return new CalendricalParseException("Text '" + abbr + "' could not be parsed: " + ex.getMessage(), str, 0, ex);
    }

    //-----------------------------------------------------------------------
    /**
     * Parses the text to a builder.
     * <p>
     * This parses to a {@code DateTimeBuilder} ensuring that the text is fully parsed.
     * This method throws {@link CalendricalParseException} if unable to parse, or
     * some other {@code CalendricalException} if another date/time problem occurs.
     *
     * @param text  the text to parse, not null
     * @return the engine representing the result of the parse, not null
     * @throws CalendricalParseException if the parse fails
     * @throws CalendricalException if there is a date/time problem
     */
    public DateTimeBuilder parseToBuilder(CharSequence text) {
        DateTimes.checkNotNull(text, "Text must not be null");
        String str = text.toString();  // parsing whole String, so this makes sense
        ParsePosition pos = new ParsePosition(0);
        DateTimeBuilder result = parseToBuilder(str, pos);
        if (result == null || pos.getErrorIndex() >= 0 || pos.getIndex() < str.length()) {
            String abbr = str.toString();
            if (abbr.length() > 64) {
                abbr = abbr.substring(0, 64) + "...";
            }
            if (pos.getErrorIndex() >= 0) {
                throw new CalendricalParseException("Text '" + abbr + "' could not be parsed at index " +
                        pos.getErrorIndex(), str, pos.getErrorIndex());
            } else {
                throw new CalendricalParseException("Text '" + abbr + "' could not be parsed, unparsed text found at index " +
                        pos.getIndex(), str, pos.getIndex());
            }
        }
        return result;
    }

    /**
     * Parses the text to a builder.
     * <p>
     * This parses to a {@code DateTimeBuilder} but does not require the input to be fully parsed.
     * <p>
     * This method does not throw {@link CalendricalParseException}.
     * Instead, errors are returned within the state of the specified parse position.
     * Callers must check for errors before using the context.
     * <p>
     * This method may throw some other {@code CalendricalException} if a date/time problem occurs.
     *
     * @param text  the text to parse, not null
     * @param position  the position to parse from, updated with length parsed
     *  and the index of any error, not null
     * @return the parsed text, null only if the parse results in an error
     * @throws IndexOutOfBoundsException if the position is invalid
     * @throws CalendricalException if there is a date/time problem
     */
    public DateTimeBuilder parseToBuilder(CharSequence text, ParsePosition position) {
        DateTimes.checkNotNull(text, "Text must not be null");
        DateTimes.checkNotNull(position, "ParsePosition must not be null");
        DateTimeParseContext context = new DateTimeParseContext(locale, symbols);
        int pos = position.getIndex();
        pos = printerParser.parse(context, text, pos);
        if (pos < 0) {
            position.setErrorIndex(~pos);
            return null;
        }
        position.setIndex(pos);
        return context.toBuilder();  // TODO: this can fail and throw CalendricalException, but should it?
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
     * The {@link Format} instance will print any {@link DateTime}
     * and parses to a merged {@link DateTimeBuilder}.
     * <p>
     * The format will throw {@code UnsupportedOperationException} and
     * {@code IndexOutOfBoundsException} in line with those thrown by the
     * {@link #printTo(DateTime, Appendable) print} and
     * {@link #parseToBuilder(CharSequence) parse} methods.
     * <p>
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
     * The {@link Format} instance will print any {@link DateTime}
     * and parses to a the type specified.
     * <p>
     * The format will throw {@code UnsupportedOperationException} and
     * {@code IndexOutOfBoundsException} in line with those thrown by the
     * {@link #printTo(DateTime, Appendable) print} and
     * {@link #parse(CharSequence, Class) parse} methods.
     * <p>
     * The format does not support attributing of the returned format string.
     *
     * @return this formatter as a classic format instance, not null
     */
    public Format toFormat(Class<?> parseType) {
        DateTimes.checkNotNull(parseType, "Class must not be null");
        return new ClassicFormat(this, parseType);
    }

    /**
     * Returns a description of the underlying formatters.
     *
     * @return the pattern that will be used, not null
     */
    @Override
    public String toString() {
        String pattern = printerParser.toString();
        return pattern.startsWith("[") ? pattern : pattern.substring(1, pattern.length() - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the classic Java Format API.
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
            DateTimes.checkNotNull(obj, "Object to be printed must not be null");
            DateTimes.checkNotNull(toAppendTo, "StringBuffer must not be null");
            DateTimes.checkNotNull(pos, "FieldPosition must not be null");
            if (obj instanceof DateTime == false) {
                throw new IllegalArgumentException("Format target must implement CalendricalObject");
            }
            pos.setBeginIndex(0);
            pos.setEndIndex(0);
            formatter.printTo((DateTime) obj, toAppendTo);
            return toAppendTo;
        }
        @Override
        public Object parseObject(String source) throws ParseException {
            try {
                if (parseType != null) {
                    return formatter.parse(source, parseType);
                }
                return formatter.parseToBuilder(source);
            } catch (CalendricalParseException ex) {
                throw new ParseException(ex.getMessage(), ex.getErrorIndex());
            }
        }
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return formatter.parseToBuilder(source, pos);
        }
    }

}
