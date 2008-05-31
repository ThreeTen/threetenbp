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
     * The locale to use for text formatting.
     */
    private final Locale locale;
    /**
     * Whether to use standard ASCII numerics (true) or use the locale (false).
     */
    private final boolean asciiNumerics;
    /**
     * The list of printers that will be used, treated as immutable.
     */
    private final DateTimePrinter[] printers;

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
     */
    public DateTimeFormatter(Locale locale, boolean asciiNumerics, List<DateTimePrinter> printers) {
        if (locale == null) {
            throw new NullPointerException("Locale must not be null");
        }
        if (printers == null) {
            throw new NullPointerException("Printers must not be null");
        }
        this.locale = locale;
        this.asciiNumerics = asciiNumerics;
        this.printers = printers.toArray(new DateTimePrinter[printers.size()]);
    }

    /**
     * Constructor used by immutable copying.
     *
     * @param locale  the locale to use for text formatting, not null
     * @param asciiNumerics  whether to use ASCII numerics (true) or locale numerics (false)
     * @param printers  the printers to use, assigned by this method, not null
     */
    private DateTimeFormatter(Locale locale, boolean asciiNumerics, DateTimePrinter[] printers) {
        this.locale = locale;
        this.asciiNumerics = asciiNumerics;
        this.printers = printers;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to be used during formatting.
     *
     * @return the locale of this DateTimeFormatter, never null
     */
    public Locale getLocale() {
        return locale;
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
        if (locale.equals(this.locale)) {
            return this;
        }
        return new DateTimeFormatter(locale, asciiNumerics, printers);
    }

    //-----------------------------------------------------------------------
    /**
     * Prints the calendrical using this formatter.
     * <p>
     * This method prints the calendrical to a String.
     *
     * @param calendrical  the calendrical to print, not null
     * @return the printed string, never null
     * @throws CalendricalFormatException if an error occurs during printing
     */
    public String print(Calendrical calendrical) {
        StringBuilder buf = new StringBuilder();
        print(calendrical, buf);
        return buf.toString();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Prints the calendrical to a StringBuilder using this formatter.
//     * <p>
//     * This method prints the calendrical to the specified StringBuffer
//     * avoiding the undesirable IOException from Appendable.
//     *
//     * @param calendrical  the calendrical to print, not null
//     * @param buffer  the buffer to print to, not null
//     * @throws CalendricalFormatException if an error occurs during printing
//     */
//    public void print(Calendrical calendrical, StringBuilder buffer) {
//        FlexiDateTime dateTime = calendrical.toFlexiDateTime();
//        try {
//            for (DateTimePrinter printer : printers) {
//                printer.print(buffer, dateTime, locale);
//            }
//        } catch (UnsupportedCalendarFieldException ex) {
//            throw new CalendricalFormatFieldException(ex);
//        } catch (IOException ex) {
//            throw new CalendricalFormatException("Unexpected IOException", ex);
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Prints the calendrical to a StringBuffer using this formatter.
//     * <p>
//     * This method prints the calendrical to the specified StringBuffer
//     * avoiding the undesirable IOException from Appendable.
//     *
//     * @param calendrical  the calendrical to print, not null
//     * @param buffer  the buffer to print to, not null
//     * @throws CalendricalFormatException if an error occurs during printing
//     */
//    public void print(Calendrical calendrical, StringBuffer buffer) {
//        FlexiDateTime dateTime = calendrical.toFlexiDateTime();
//        try {
//            for (DateTimePrinter printer : printers) {
//                printer.print(buffer, dateTime, locale);
//            }
//        } catch (UnsupportedCalendarFieldException ex) {
//            throw new CalendricalFormatFieldException(ex);
//        } catch (IOException ex) {
//            throw new CalendricalFormatException("Unexpected IOException", ex);
//        }
//    }

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
     * @param calendrical  the calendrical to print, not null
     * @param buffer  the buffer to print to, not null
     * @throws CalendricalFormatException if an error occurs during printing
     */
    public void print(Calendrical calendrical, Appendable buffer) {
        FlexiDateTime dateTime = calendrical.toFlexiDateTime();
        try {
            for (DateTimePrinter printer : printers) {
                printer.print(buffer, dateTime, locale);
            }
        } catch (UnsupportedCalendarFieldException ex) {
            throw new CalendricalFormatFieldException(ex);
        } catch (IOException ex) {
            throw new CalendricalFormatException(ex);
        }
    }

}
