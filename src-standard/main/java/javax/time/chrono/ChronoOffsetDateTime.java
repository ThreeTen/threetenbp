/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import java.io.Serializable;

import javax.time.DateTimeException;
import javax.time.Instant;
import javax.time.LocalTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

/**
 * A date-time with a zone offset from UTC/Greenwich for the calendar neutral API.
 * <p>
 * {@code ChronoOffsetDateTime} is an immutable representation of a date-time with an offset.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as the offset from UTC/Greenwich. For example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00" can be stored in an {@code OffsetDateTime}.
 * <p>
 * {@code ChronoOffsetDateTime} and {@link Instant} both store an instant on the time-line
 * to nanosecond precision. The main difference is that this class also stores the
 * offset from UTC/Greenwich. {@code Instant} should be used when you only need to compare the
 * object to other instants. {@code ChronoOffsetDateTime} should be used when you want to actively
 * query and manipulate the date and time fields, although you should also consider using
 * {@link ChronoZonedDateTime}.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 *
 * @param <C> the chronology of this date
 */

public interface ChronoOffsetDateTime<C extends Chrono<C>>
                extends DateTime, WithAdjuster, Comparable<ChronoOffsetDateTime<C>>, Serializable {
    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset.
     *
     * @return the zone offset, not null
     */
    ZoneOffset getOffset();

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified offset ensuring
     * that the result has the same local date-time.
     * <p>
     * This method returns an object with the same {@code ChronoLocalDateTime} and the specified {@code ZoneOffset}.
     * No calculation is needed or performed.
     * For example, if this time represents {@code 2007-12-03T10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 2007-12-03T10:30+03:00}.
     * <p>
     * To take into account the difference between the offsets, and adjust the time fields,
     * use {@link #withOffsetSameInstant}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetDateTime} based on this date-time with the requested offset, not null
     */
    ChronoOffsetDateTime<C> withOffsetSameLocal(ZoneOffset offset);

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified offset ensuring
     * that the result is at the same instant.
     * <p>
     * This method returns an object with the specified {@code ZoneOffset} and a {@code ChronoLocalDateTime}
     * adjusted by the difference between the two offsets.
     * This will result in the old and new objects representing the same instant.
     * This is useful for finding the local time in a different offset.
     * For example, if this time represents {@code 2007-12-03T10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 2007-12-03T11:30+03:00}.
     * <p>
     * To change the offset without adjusting the local time use {@link #withOffsetSameLocal}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetDateTime} based on this date-time with the requested offset, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoOffsetDateTime<C> withOffsetSameInstant(ZoneOffset offset);

    //-----------------------------------------------------------------------
    /**
     * Returns a zoned date-time formed from the instant represented by this
     * date-time and the specified time-zone.
     * <p>
     * This conversion will ignore the visible local date-time and use the underlying instant instead.
     * This avoids any problems with local time-line gaps or overlaps.
     * The result might have different values for fields such as hour, minute an even day.
     * <p>
     * To attempt to retain the values of the fields, use {@link #atZoneSimilarLocal(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date-time, not null
     */
    ChronoZonedDateTime<C> atZoneSameInstant(ZoneId zone) ;

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date-time is in a gap or overlap according to
     * the rules then a resolver is used to determine the resultant local time and offset.
     * This method uses the {@link ZoneResolvers#retainOffset() retain-offset} resolver.
     * This selects the date-time immediately after a gap and retains the offset in
     * overlaps where possible, selecting the earlier offset if not possible.
     * <p>
     * Finer control over gaps and overlaps is available in two ways.
     * If you simply want to use the later offset at overlaps then call
     * {@link ChronoZonedDateTime#withLaterOffsetAtOverlap()} immediately after this method.
     * Alternately, pass a specific resolver to {@link #atZoneSimilarLocal(ZoneId, ZoneResolver)}.
     * <p>
     * To create a zoned date-time at the same instant irrespective of the local time-line,
     * use {@link #atZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, not null
     */
    ChronoZonedDateTime<C> atZoneSimilarLocal(ZoneId zone);

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone
     * taking control of what occurs in time-line gaps and overlaps.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date-time is in a gap or overlap according to
     * the rules then the resolver is used to determine the resultant local time and offset.
     * <p>
     * To create a zoned date-time at the same instant irrespective of the local time-line,
     * use {@link #atZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @param resolver  the zone resolver to use for gaps and overlaps, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, not null
     * @throws DateTimeException if the date-time cannot be resolved
     */
    ChronoZonedDateTime<C> atZoneSimilarLocal(ZoneId zone, ZoneResolver resolver);

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to an {@code Instant}.
     *
     * @return an Instant representing the same instant, not null
     */
    Instant toInstant();

    /**
     * Gets the {@code ChronoLocalDate} in this date-time.
     *
     * @return a ChronoLocalDate representing the date fields of this date-time, not null
     */
    ChronoLocalDate<C> getDate() ;

    /**
     * Gets the {@code LocalTime} in this date-time.
     *
     * @return a LocalTime representing the time fields of this date-time, not null
     */
    LocalTime getTime();

    /**
     * Gets the {@code ChronoOffsetDateTime} in this date-time.
     *
     * @return a ChronoLocalDateTime representing the fields of this date-time, not null
     */
    ChronoLocalDateTime<C> getDateTime();

    //-------------------------------------------------------------------------
    // override for covariant return type
    @Override
    ChronoOffsetDateTime<C> with(WithAdjuster adjuster);

    @Override
    ChronoOffsetDateTime<C> with(DateTimeField field, long newValue);

    @Override
    ChronoOffsetDateTime<C> plus(PlusAdjuster adjuster);

    @Override
    ChronoOffsetDateTime<C> plus(long amountToAdd, PeriodUnit unit);

    @Override
    ChronoOffsetDateTime<C> minus(MinusAdjuster adjuster);

    @Override
    ChronoOffsetDateTime<C> minus(long amountToSubtract, PeriodUnit unit);

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to the number of seconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * <p>
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z
     */
    long toEpochSecond();

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code OffsetDateTime} to another date-time.
     * <p>
     * The comparison is based on the instant then local date-time.
     * This ordering is consistent with {@code equals()}.
     * For example, the following is the comparator order:
     * <ol>
     * <li>{@code 2008-12-03T10:30+01:00}</li>
     * <li>{@code 2008-12-03T11:00+01:00}</li>
     * <li>{@code 2008-12-03T12:00+02:00}</li>
     * <li>{@code 2008-12-03T11:30+01:00}</li>
     * <li>{@code 2008-12-03T12:00+01:00}</li>
     * <li>{@code 2008-12-03T12:30+01:00}</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local date-time is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with {@code equals()}.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    int compareTo(ChronoOffsetDateTime<C> other);
    //-----------------------------------------------------------------------
    /**
     * Checks if the instant of this {@code OffsetDateTime} is after that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals} in that it
     * only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().isAfter(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the instant of the specified date-time
     */
    boolean isAfter(ChronoOffsetDateTime<C> other);

    /**
     * Checks if the instant of this {@code OffsetDateTime} is before that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().isBefore(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is before the instant of the specified date-time
     */
    boolean isBefore(ChronoOffsetDateTime<C> other);

    /**
     * Checks if the instant of this {@code OffsetDateTime} is equal to that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals}
     * in that it only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().equals(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if the instant equals the instant of the specified date-time
     */
    boolean equalInstant(ChronoOffsetDateTime<C> other);

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the local date-time and the offset.
     * To compare for the same instant on the time-line, use {@link #equalInstant}.
     * Only objects of type {@code OffsetDateTime} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date-time
     */
    @Override
    boolean equals(Object obj);

    /**
     * A hash code for this date-time.
     *
     * @return a suitable hash code
     */
    @Override
    int hashCode();

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    String toString();

    /**
     * Outputs this date-time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date-time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws DateTimeException if an error occurs during printing
     */
    String toString(CalendricalFormatter formatter);

}
