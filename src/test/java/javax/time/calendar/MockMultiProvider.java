/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import javax.time.Instant;
import javax.time.InstantProvider;

/**
 * Mock provider that aims to test APIs which are ambiguous.
 *
 * @author Stephen Colebourne
 */
public class MockMultiProvider
        implements InstantProvider, DateTimeProvider, DateProvider, TimeProvider, Calendrical {
    final OffsetDateTime dateTime;
    /** Constructor. */
    public MockMultiProvider(int y, int mon, int d, int h, int min) {
        this(y, mon, d, h, min, ZoneOffset.UTC);
    }
    public MockMultiProvider(int y, int mon, int d, int h, int min, int s) {
        this(y, mon, d, h, min, s, ZoneOffset.UTC);
    }
    public MockMultiProvider(int y, int mon, int d, int h, int min, int s, int n) {
        this(y, mon, d, h, min, s, n, ZoneOffset.UTC);
    }
    /** Constructor. */
    public MockMultiProvider(int y, int mon, int d, int h, int min, ZoneOffset offset) {
        super();
        dateTime = OffsetDateTime.of(y, mon, d, h, min, offset);
    }
    public MockMultiProvider(int y, int mon, int d, int h, int min, int s, ZoneOffset offset) {
        super();
        dateTime = OffsetDateTime.of(y, mon, d, h, min, s, offset);
    }
    public MockMultiProvider(int y, int mon, int d, int h, int min, int s, int n, ZoneOffset offset) {
        super();
        dateTime = OffsetDateTime.of(y, mon, d, h, min, s, n, offset);
    }
    public Instant toInstant() {
        return dateTime.toInstant();
    }
    public LocalDateTime toLocalDateTime() {
        return dateTime.toLocalDateTime();
    }
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }
    public LocalTime toLocalTime() {
        return dateTime.toLocalTime();
    }
    public OffsetDateTime toOffsetDateTime() {
        return dateTime;
    }
    public OffsetDate toOffsetDate() {
        return dateTime.toOffsetDate();
    }
    public OffsetTime toOffsetTime() {
        return dateTime.toOffsetTime();
    }
    public ZonedDateTime toZonedDateTime() {
        return ZonedDateTime.of(dateTime, ZoneId.of("Europe/Paris"));
    }
    public <T> T get(CalendricalRule<T> rule) {
        return null;
    }
}
