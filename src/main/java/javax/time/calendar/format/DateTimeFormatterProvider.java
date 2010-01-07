/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

import javax.time.calendar.Chronology;
import javax.time.calendar.format.DateTimeFormatterBuilder.FormatStyle;

/**
 * The Service Provider Interface (SPI) to be implemented by classes providing
 * date-time formatting information.
 * <p>
 * DateTimeFormatterProvider is an abstract class and must be implemented with care.
 * Implementations must be thread-safe.
 * It is strongly recommended that implementations cache the returned formatters.
 *
 * @author Stephen Colebourne
 */
public abstract class DateTimeFormatterProvider extends LocaleServiceProvider {

    /**
     * Gets a localized date, time or date-time format.
     *
     * @param dateStyle  the date formatter style to obtain, null to obtain a time formatter
     * @param timeStyle  the time formatter style to obtain, null to obtain a date formatter
     * @param locale  the locale to use, not null
     * @param chronology  the chronology to use, not null
     * @return the date-time formatter, never null
     * @throws IllegalArgumentException if both format styles are null
     * @throws IllegalArgumentException if the locale is not a recognized locale
     */
    public abstract DateTimeFormatter getFormatter(
            FormatStyle dateStyle, FormatStyle timeStyle, Locale locale, Chronology chronology);

}
