/*
 * Copyright © 2020 Miroslav Pokorny
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;



@J2clTestInput(JunitTest.class)
public final class JunitTest {
    
    @Test
    public void testDateTimeFormatterFormatLocalDate() {
        this.formatAndCheck(DateTimeFormatter.ISO_LOCAL_DATE,
                LocalDate.of(2020, 5, 6),
                "2020-05-06");
    }

    @Test
    public void testDateTimeFormatterFormatLocalDateFormatStyleFULL() {
        this.formatAndCheck(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL),
                LocalDate.of(2020, 5, 6),
                "Wednesday, 6 May 2020");
    }

    @Test
    public void testDateTimeFormatterFormatLocalDateLocaleSpanish() {
        this.formatAndCheck(DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.forLanguageTag("ES")),
                LocalDate.of(2020, 5, 6),
                "2020-05-06");
    }

    @Test
    public void testDateTimeFormatterFormatLocalDateLocaleStyleFULLSpanish() {
        this.formatAndCheck(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.forLanguageTag("ES")),
                LocalDate.of(2020, 5, 6),
                "miércoles, 6 de mayo de 2020");
    }

    @Test
    public void testDateTimeFormatterFormatLocalDateTime() {
        this.formatAndCheck(DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                LocalDateTime.of(2020, 5, 6, 18, 28, 59),
                "2020-05-06T18:28:59");
    }

    @Test
    public void testDateTimeFormatterFormatLocalTime() {
        this.formatAndCheck(DateTimeFormatter.ISO_LOCAL_TIME,
                LocalTime.of(18, 28, 59),
                "18:28:59");
    }

    @Test
    public void testDateTimeFormatterFormatOffsetDateTime() {
        this.formatAndCheck(DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                LocalDateTime.of(2020, 5, 6, 18, 28, 59).atOffset(ZoneOffset.UTC),
                "2020-05-06T18:28:59");
    }


    private void formatAndCheck(final DateTimeFormatter formatter,
                                final TemporalAccessor value,
                                final String expected) {
        Assert.assertEquals(formatter + " format " + value,
            expected,
            formatter.format(value));
    }
}
