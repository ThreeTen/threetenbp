/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * Amended OpenJDK classes that integrate JSR-310 with the existing date and time APIs.
 * <p>
 * {@link java.util.Date} is extended with conversions to and from {@link javax.time.Instant}
 * with methods {@code java.util.Date.setInstant(Instant)},
 * {@code java.util.Date(Instant)} to create a Date from an Instant, and
 * {@code java.util.Date.toInstant()} to create an Instant from a Date.
 * <p>
 * {@link java.util.Calendar} is extended with conversions to and from {@link javax.time.Instant}
 * with methods {@code java.util.Date.setInstant(Instant)} and
 * {@code java.util.Calendar.toInstant()} to create an Instant from a Calendar instance.
 * <p>
 * {@link java.util.GregorianCalendar} is extended with conversions to and from the ISO Calendar
 * API classes including {@link javax.time.LocalDate}, {@link javax.time.LocalTime},
 * {@link javax.time.LocalDateTime}, {@link javax.time.OffsetDateTime},
 * {@link javax.time.OffsetDate}, {@link javax.time.OffsetTime}, and
 * {@link javax.time.ZonedDateTime}.
 * GregorianCalendar implements the {@link javax.time.calendrical.DateTime} Interface,
 * so its fields can be accessed with {@link javax.time.calendrical.DateTime#get},
 * {@link javax.time.calendrical.DateTime#with}, {@link javax.time.calendrical.DateTime#extract}.
 *
 */
package java.util;
