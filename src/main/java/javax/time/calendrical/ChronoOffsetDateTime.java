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
package javax.time.calendrical;

import static javax.time.calendrical.LocalDateTimeField.OFFSET_SECONDS;

import javax.time.DateTimes;
import javax.time.LocalTime;
import javax.time.ZoneOffset;
import javax.time.chrono.Chronology;
import javax.time.chrono.ISOChronology;

/**
 * A date-time storing an offset from UTC/Greenwich without time-zone.
 * <p>
 * Implementations of this interface represent the combination of date and time with
 * offset but without time-zone. The most common implementation is {@code LocalDateTime}
 * which should be used by the vast majority of applications.
 * Other implementations exist for non-ISO calendar systems.
 * <p>
 * When using this interface, bear in mind that two instances may represent dates
 * in two different calendar systems. Thus, application logic needs to handle
 * calendar systems, for example allowing for additional months, different years
 * and alternate leap systems.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 * 
 * @param <D> the date class
 */
public final class ChronoOffsetDateTime<D extends ChronoDate<?>>
        implements DateTime<ChronoOffsetDateTime<D>>, DateTimeAdjuster, Comparable<ChronoOffsetDateTime<?>> {

    /**
     * The date.
     */
    private final D date;
    /**
     * The time.
     */
    private final LocalTime time;
    /**
     * The offset.
     */
    private final ZoneOffset offset;

    public static <R extends ChronoDate<?>> ChronoOffsetDateTime<R> of(R date, LocalTime time, ZoneOffset offset) {
        DateTimes.checkNotNull(date, "Date must not be null");
        DateTimes.checkNotNull(time, "Time must not be null");
        return new ChronoOffsetDateTime<R>(date, time, offset);
    }

    private ChronoOffsetDateTime(D date, LocalTime time, ZoneOffset offset) {
        this.date = date;
        this.time = time;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date part of the date-time.
     *
     * @return the date, not null
     */
    public D getDate() {
        return date;
    }

    /**
     * Gets the time part of the date-time.
     *
     * @return the time, not null
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Gets the offset part of the date-time.
     *
     * @return the offset, not null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(DateTimeField field) {
        return date.range(field);
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f == OFFSET_SECONDS) {
                return offset.getTotalSeconds();
            } else if (f.isTimeField()) {
                return time.get(field);
            } else {
                return date.get(field);
            }
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoOffsetDateTime<D> with(DateTimeAdjuster adjuster) {
        return adjuster.doAdjustment(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoOffsetDateTime<D> with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f == OFFSET_SECONDS) {
                ZoneOffset updated = ZoneOffset.ofTotalSeconds(OFFSET_SECONDS.checkValidIntValue(newValue));
                return new ChronoOffsetDateTime<D>(date, time, updated);
            } else if (f.isTimeField()) {
                LocalTime updated = time.with(field, newValue);
                return new ChronoOffsetDateTime<D>(date, updated, offset);
            } else {
                D updated = (D) date.with(field, newValue);
                return new ChronoOffsetDateTime<D>(updated, time, offset);
            }
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoOffsetDateTime<D> plus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doAdd(this);
    }

    @Override
    public ChronoOffsetDateTime<D> plus(long periodAmount, PeriodUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoOffsetDateTime<D> minus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doSubtract(this);
    }

    @Override
    public ChronoOffsetDateTime<D> minus(long periodAmount, PeriodUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology in use for this date-time.
     * <p>
     * The {@code Chronology} represents the calendar system, where the world civil calendar
     * system is referred to as the {@link ISOChronology ISO calendar system}.
     * All fields are expressed in terms of the calendar system.
     *
     * @return the calendar system chronology used by the date-time, not null
     */
    public Chronology getChronology() {
        return date.getChronology();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == Chronology.class) {
            return (R) getChronology();
        }
        return null;
    }

    @Override
    public <R extends DateTime<R>> R doAdjustment(R dateTime) {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time.
     * <p>
     * The comparison is based on the date, time and offset.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int compareTo(ChronoOffsetDateTime<?> other) {
        int cmp = ((ChronoDate) date).compareTo((ChronoDate) other.date);
        if (cmp == 0) {
            cmp = time.compareTo(other.time);
            if (cmp == 0) {
                cmp = offset.compareTo(other.offset);
            }
        }
        return cmp;
    }

}
