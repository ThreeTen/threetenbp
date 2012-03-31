/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

/**
 * Provides access in a uniform way to objects that provide date and time information.
 * <p>
 * This interface acts as the base for  classes representing an aspect of date and time.
 * As such, it fulfills a similar role to {@link Number} for numeric values.
 * <p>
 * The interface provides the ability to obtain an instance of an object from another object.
 * For example, given a {@code CalendricalAccessor} it is possible to attempt to obtain
 * a {@link LocalDate} or a {@link ZoneOffset}:
 * <pre>
 *  CalendricalAccessor accessor = ...
 *  LocalDate date = accessor.extract(LocalDate.class);
 * </pre>
 * <p>
 * This interface makes no guarantees about the thread-safety or immutability of implementations.
 * 
 * @author Stephen Colebourne
 */
public interface CalendricalAccessor {

    /**
     * Extracts an instance of a type from an arbitrary date/time object.
     * <p>
     * Only a fixed set of classes may be specified.
     * <ul>
     * <li>LocalDate
     * <li>LocalTime
     * <li>LocalDateTime
     * <li>OffsetDate
     * <li>OffsetTime
     * <li>OffsetDateTime
     * <li>ZonedDateTime
     * <li>ZoneOffset
     * <li>ZoneId
     * <li>Instant
     * </ul>
     * <p>
     * Implementations should ensure that calls to this method are thread-safe.
     * An immutable implementation will naturally provide this guarantee.
     *
     * @param typeToExtract  the type of date/time to extract, not null
     * @return the extracted instance, null if unable to extract
     */
    <T> T extract(Class<T> typeToExtract);

}
