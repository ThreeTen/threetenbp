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
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.UnsupportedCalendarFieldException;

/**
 * Formatter for dates and times.
 *
 * @author Stephen Colebourne
 */
public class DateTimeFormatter {

    /**
     * The symbols to use for formatting, not null.
     */
    private final DateTimeFormatSymbols symbols;
    /**
     * Whether to use standard ASCII numerics (true) or use the locale (false).
     */
    private final boolean asciiNumerics;
    /**
     * The list of printers that will be used, treated as immutable.
     */
    private final DateTimePrinter[] printers;
    /**
     * The list of parsers that will be used, treated as immutable.
     */
    private final DateTimeParser[] parsers;

//    //-----------------------------------------------------------------------
//    /**
//     * Creates a formatter using the default locale for text formatting.
//     *
//     * @return the created formatter, never null
//     */
//    public static DateTimeFormatter forDefaultLocale() {
//        return forLocale(Locale.getDefault(), true);
//    }
//
//    /**
//     * Creates a formatter using the specified locale.
//     *
//     * @param locale  the locale to use for text formatting, not null
//     * @return the created formatter, never null
//     */
//    public static DateTimeFormatter forLocale(Locale locale) {
//        return forLocale(locale, true);
//    }
//
//    /**
//     * Creates a formatter using the specified locale controlling whether
//     * numeric values should be formatted using ASCII or the locale driven
//     * number system.
//     *
//     * @param locale  the locale to use for text formatting, not null
//     * @param asciiNumerics  whether to use ASCII numerics (true) or locale numerics (false)
//     * @return the created formatter, never null
//     */
//    public static DateTimeFormatter forLocale(Locale locale, boolean asciiNumerics) {
//        return new DateTimeFormatter(locale, asciiNumerics, null);
//    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param locale  the locale to use for text formatting, not null
     * @param asciiNumerics  whether to use ASCII numerics (true) or locale numerics (false)
     * @param printers  the printers to use, cloned by this method, not null
     * @param parsers  the parsers to use, cloned by this method, not null
     */
    DateTimeFormatter(Locale locale, boolean asciiNumerics, List<DateTimePrinter> printers, List<DateTimeParser> parsers) {
        // validated by caller
        this.symbols = DateTimeFormatSymbols.getInstance(locale);
        this.asciiNumerics = asciiNumerics;
        this.printers = printers.contains(null) ? null : printers.toArray(new DateTimePrinter[printers.size()]);
        this.parsers = parsers.contains(null) ? null : parsers.toArray(new DateTimeParser[parsers.size()]);
    }

    /**
     * Constructor used by immutable copying.
     *
     * @param symbols  the symbols to use for text formatting, not null
     * @param asciiNumerics  whether to use ASCII numerics (true) or locale numerics (false)
     * @param printers  the printers to use, assigned by this method, not null
     * @param parsers  the parsers to use, assigned by this method, not null
     */
    private DateTimeFormatter(
            DateTimeFormatSymbols symbols,
            boolean asciiNumerics,
            DateTimePrinter[] printers,
            DateTimeParser[] parsers) {
        this.symbols = symbols;
        this.asciiNumerics = asciiNumerics;
        this.printers = printers;
        this.parsers = parsers;
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
        if (locale == null) {
            throw new NullPointerException("Locale must not be null");
        }
        if (locale.equals(this.getLocale())) {
            return this;
        }
        DateTimeFormatSymbols newSymbols = DateTimeFormatSymbols.getInstance(locale);
        return new DateTimeFormatter(newSymbols, asciiNumerics, printers, parsers);
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
        return printers != null;
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
     * @throws CalendricalFormatException if an error occurs during printing
     */
    public String print(Calendrical calendrical) {
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
     * @param calendrical  the provider of the calendrical to print, not null
     * @param appendable  the appendable to print to, not null
     * @throws UnsupportedOperationException if this formatter cannot print
     * @throws CalendricalFormatException if an error occurs during printing
     */
    public void print(Calendrical calendrical, Appendable appendable) {
        if (printers == null) {
            throw new UnsupportedOperationException("Formatter does not support printing");
        }
        FlexiDateTime dateTime = calendrical.toFlexiDateTime();
        try {
            for (DateTimePrinter printer : printers) {
                printer.print(dateTime, appendable, symbols);
            }
        } catch (UnsupportedCalendarFieldException ex) {
            throw new CalendricalFormatFieldException(ex);
        } catch (IOException ex) {
            throw new CalendricalFormatException(ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this formatter can parse.
     * <p>
     * Depending on how this formatter is initialised, it may not be possible
     * for it to parse at all. This method allows the caller to check whether
     * the parse methods will throw UnsupportedOperationException or not.
     *
     * @return true if the formatter supports parsing
     */
    public boolean isParseSupported() {
        return parsers != null;
    }

    //-----------------------------------------------------------------------
    /**
     * Parses the text into a FlexiDateTime.
     * <p>
     * The result may be invalid including out of range values such as
     * a month of 65. The methods on FlexiDateTime allow you to handle the
     * invalid input.
     *
     * @param text  the text to parse, not null
     * @return the parsed text, never null
     * @throws UnsupportedOperationException if this formatter cannot parse
     * @throws NullPointerException if the text is null
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public FlexiDateTime parse(String text) {
        ParsePosition pp = new ParsePosition(0);
        return parse(text, pp);
    }

    /**
     * Parses the text into a FlexiDateTime.
     * <p>
     * The result may be invalid including out of range values such as
     * a month of 65. The methods on FlexiDateTime allow you to handle the
     * invalid input.
     *
     * @param text  the text to parse, not null
     * @param position  the position to parse from, updated with length parsed
     *  and the index of any error, not null
     * @return the parsed text, null only if the parse results in an error
     * @throws UnsupportedOperationException if this formatter cannot parse
     * @throws NullPointerException if the text is null
     * @throws IndexOutOfBoundsException if the position is invalid
     */
    public FlexiDateTime parse(String text, ParsePosition position) {
        if (text == null) {
            throw new UnsupportedOperationException("Text to parse must not be null");
        }
        if (position == null) {
            throw new UnsupportedOperationException("Position to parse from must not be null");
        }
        if (parsers == null) {
            throw new UnsupportedOperationException("Formatter does not support printing");
        }
        DateTimeParseContext context = new DateTimeParseContext();
        int pos = position.getIndex();
        for (DateTimeParser parser : parsers) {
            pos = parser.parse(context, text, pos);
            if (pos < 0) {
                position.setErrorIndex(~pos);
                return null;
            }
        }
        return context.toFlexiDateTime();
    }

    /**
     * Returns this formatter as a <code>java.text.Format</code> instance.
     * <p>
     * The format instance will print any {@link Calendrical} and parses to
     * a {@link FlexiDateTime}.
     * <p>
     * The format will throw exceptions in line with those thrown by the
     * {@link #print(Calendrical, Appendable) print} and
     * {@link #parse(String, ParsePosition) parse} methods.
     * <p>
     * The format does not support attributing of the returned format string.
     *
     * @return this formatter as a classic format instance, never null
     */
    public Format asFormat() {
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
            FlexiDateTime fdt = null;
            if (obj instanceof Calendrical) {
                fdt = ((Calendrical) obj).toFlexiDateTime();
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
        public Object parseObject(String source, ParsePosition pos) {
            return parse(source, pos);
        }
    }

}
