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

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.UnsupportedCalendarFieldException;

/**
 * Formatter for dates and times.
 * <p>
 * This class provides the main application entry point for performing formatting.
 * Formatting consists of printing and parsing.
 * <p>
 * Instances of DateTimeFormatter are constructed using DateTimeFormatterBuilder
 * or by using one of the predefined constants on DateTimeFormatters.
 * <p>
 * DateTimeFormatter is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public class DateTimeFormatter {

    /**
     * The symbols to use for formatting, not null.
     */
    private final DateTimeFormatSymbols symbols;
    /**
     * The list of printers that will be used, treated as immutable.
     */
    private final CompositePrinterParser printerParser;

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param locale  the locale to use for text formatting, not null
     * @param printerParser  the printer/parser to use, not null
     */
    DateTimeFormatter(Locale locale, CompositePrinterParser printerParser) {
        // validated by caller
        this.symbols = DateTimeFormatSymbols.getInstance(locale);
        this.printerParser = printerParser;
    }

    /**
     * Constructor used by immutable copying.
     *
     * @param symbols  the symbols to use for text formatting, not null
     * @param asciiNumerics  whether to use ASCII numerics (true) or locale numerics (false)
     * @param printerParser  the printer/parser to use, not null
     */
    private DateTimeFormatter(
            DateTimeFormatSymbols symbols,
            CompositePrinterParser printerParser) {
        this.symbols = symbols;
        this.printerParser = printerParser;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to be used during formatting.
     *
     * @return the locale of this DateTimeFormatter, never null
     */
    public Locale getLocale() {
        return symbols.getLocale();
    }

    /**
     * Returns a copy of this DateTimeFormatter with a new locale.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param locale  the new locale, not null
     * @return a new DateTimeFormatter with the same format and the new locale, never null
     */
    public DateTimeFormatter withLocale(Locale locale) {
        FormatUtil.checkNotNull(locale, "locale");
        if (locale.equals(this.getLocale())) {
            return this;
        }
        DateTimeFormatSymbols newSymbols = DateTimeFormatSymbols.getInstance(locale);
        return new DateTimeFormatter(newSymbols, printerParser);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this formatter can print.
     * <p>
     * Depending on how this formatter is initialised, it may not be possible
     * for it to print at all. This method allows the caller to check whether
     * the print methods will throw UnsupportedOperationException or not.
     *
     * @return true if the formatter supports printing
     */
    public boolean isPrintSupported() {
        return printerParser.isPrintSupported();
    }

    //-----------------------------------------------------------------------
    /**
     * Prints the calendrical using this formatter.
     * <p>
     * This method prints the calendrical to a String.
     *
     * @param calendrical  the calendrical to print, not null
     * @return the printed string, never null
     * @throws UnsupportedOperationException if this formatter cannot print
     * @throws NullPointerException if the calendrical is null
     * @throws CalendricalFormatException if an error occurs during printing
     */
    public String print(CalendricalProvider calendrical) {
        StringBuilder buf = new StringBuilder(32);
        print(calendrical, buf);
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Prints the calendrical to an Appendable using this formatter.
     * <p>
     * This method prints the calendrical to the specified Appendable.
     * Appendable is a general purpose interface that is implemented by all
     * key character output classes including StringBuffer, StringBuilder,
     * PrintStream and Writer.
     * <p>
     * Although Appendable methods throw an IOException, this method does not.
     * Instead, any IOException is wrapped in a runtime exception.
     * See {@link CalendricalFormatException#rethrowIOException()} for a means
     * to extract the IOException.
     *
     * @param calendricalProvider  the provider of the calendrical to print, not null
     * @param appendable  the appendable to print to, not null
     * @throws UnsupportedOperationException if this formatter cannot print
     * @throws NullPointerException if the calendrical or appendable is null
     * @throws CalendricalFormatException if an error occurs during printing
     */
    public void print(CalendricalProvider calendricalProvider, Appendable appendable) {
        FormatUtil.checkNotNull(calendricalProvider, "calendrical provider");
        FormatUtil.checkNotNull(appendable, "appendable");
        Calendrical calendrical = calendricalProvider.toCalendrical();
        try {
            printerParser.print(calendrical, appendable, symbols);
        } catch (UnsupportedCalendarFieldException ex) {
            throw new CalendricalFormatFieldException(ex);
        } catch (IOException ex) {
            throw new CalendricalFormatException(ex.getMessage(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this formatter can parse.
     * <p>
     * Depending on how this formatter is initialized, it may not be possible
     * for it to parse at all. This method allows the caller to check whether
     * the parse methods will throw UnsupportedOperationException or not.
     *
     * @return true if the formatter supports parsing
     */
    public boolean isParseSupported() {
        return printerParser.isParseSupported();
    }

    //-----------------------------------------------------------------------
    /**
     * Fully parses the text into a Calendrical.
     * <p>
     * This parses the entire text into a calendrical. If the parse completes
     * without reading the entire length of the text, and exception is thrown.
     * If a problem occurs during parsing, an exception is thrown.
     * <p>
     * The result may be invalid including out of range values such as a month of 65.
     * The methods on the calendrical allow you to handle the invalid input.
     * For example:
     * <pre>
     * LocalDateTime dt = parser.parse(str).mergeStrict().toLocalDateTime();
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed text, never null
     * @throws UnsupportedOperationException if this formatter cannot parse
     * @throws NullPointerException if the text is null
     * @throws IndexOutOfBoundsException if the position is invalid
     * @throws CalendricalParseException if the parse fails
     */
    public Calendrical parse(String text) {
        ParsePosition pos = new ParsePosition(0);
        Calendrical result = parse(text, pos);
        boolean textNotConsumed = pos.getIndex() < text.length();
        if (textNotConsumed || pos.getErrorIndex() >= 0) {
            String str = text;
            if (str.length() > 64) {
                str = str.substring(0, 64) + "...";
            }
            throw new CalendricalParseException("Text could not be parsed: " + str, text,
                    pos.getErrorIndex() >= 0 ? pos.getErrorIndex() : pos.getIndex());
        }
        return result;
    }

    /**
     * Parses the text into a Calendrical.
     * <p>
     * The result may be invalid including out of range values such as a month of 65.
     * The methods on the calendrical allow you to handle the invalid input.
     * For example:
     * <pre>
     * LocalDateTime dt = parser.parse(str).mergeStrict().toLocalDateTime();
     * </pre>
     *
     * @param text  the text to parse, not null
     * @param position  the position to parse from, updated with length parsed
     *  and the index of any error, not null
     * @return the parsed text, null only if the parse results in an error
     * @throws UnsupportedOperationException if this formatter cannot parse
     * @throws NullPointerException if the text or position is null
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public Calendrical parse(String text, ParsePosition position) {
        FormatUtil.checkNotNull(text, "text to parse");
        FormatUtil.checkNotNull(position, "position to parse from");
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        int pos = position.getIndex();
        pos = printerParser.parse(context, text, pos);
        if (pos < 0) {
            position.setErrorIndex(~pos);
            return null;
        }
        position.setIndex(pos);
        return context.toCalendrical();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the formatter as a composite printer parser.
     *
     * @param optional  whether the printer/parser should be optional
     * @return the printer/parser, never null
     */
    CompositePrinterParser toPrinterParser(boolean optional) {
        return printerParser.withOptional(optional);
    }

    /**
     * Returns this formatter as a <code>java.text.Format</code> instance.
     * <p>
     * The format instance will print any {@link CalendricalProvider} and parses to
     * a {@link Calendrical}.
     * <p>
     * The format will throw <code>UnsupportedOperationException</code> and
     * <code>IndexOutOfBoundsException</code> in line with those thrown by the
     * {@link #print(CalendricalProvider, Appendable) print} and
     * {@link #parse(String, ParsePosition) parse} methods.
     * <p>
     * The format does not support attributing of the returned format string.
     *
     * @return this formatter as a classic format instance, never null
     */
    public Format toFormat() {
        return new ClassicFormat();
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the classic Java Format API.
     */
    private class ClassicFormat extends Format {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /** {@inheritDoc} */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            FormatUtil.checkNotNull(obj, "object to be printed");
            FormatUtil.checkNotNull(toAppendTo, "string buffer");
            FormatUtil.checkNotNull(pos, "field position");
            Calendrical fdt = null;
            if (obj instanceof CalendricalProvider) {
                fdt = ((CalendricalProvider) obj).toCalendrical();
                if (fdt == null) {
                    throw new NullPointerException("The CalendricalProvider implementation must not return null");
                }
            } else {
                throw new IllegalArgumentException("DateTimeFormatter can format Calendrical instances");
            }
            pos.setBeginIndex(0);
            pos.setEndIndex(0);
            print(fdt, toAppendTo);
            return toAppendTo;
        }

        /** {@inheritDoc} */
        @Override
        public Object parseObject(String source) throws ParseException {
            try {
                return parse(source);
            } catch (CalendricalParseException ex) {
                throw new ParseException(ex.getMessage(), ex.getErrorIndex());
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return parse(source, pos);
        }
    }

}
