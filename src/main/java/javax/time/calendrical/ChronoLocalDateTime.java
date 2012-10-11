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

import javax.time.LocalTime;
import javax.time.chrono.Chronology;
import javax.time.chrono.ISOChronology;

/**
 * A date-time without time-zone.
 * <p>
 * Implementations of this interface represent the combination of date and time without
 * offset or time-zone. The most common implementation is {@code LocalDateTime}
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
 * @param <T> the implementing subclass
 * @param <D> the date class
 */
public final class ChronoLocalDateTime<D extends ChronoDate<?>>
        implements DateTime<ChronoLocalDateTime<D>>, DateTimeAdjuster, Comparable<ChronoLocalDateTime<?>> {

    private final D date;
    private final LocalTime time;
    private final Chronology chrono;

    public <R extends ChronoDate<?>> ChronoLocalDateTime<R> of(R date, LocalTime time, Chronology chrono) {
        return new ChronoLocalDateTime<R>(date, time, chrono);
    }

    public ChronoLocalDateTime(D date, LocalTime time, Chronology chrono) {
        this.date = date;
        this.time = time;
        this.chrono = chrono;
    }

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
        return chrono;
    }

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

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        return date.range(field);
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            if (((LocalDateTimeField) field).isDateField()) {
                return date.get(field);
            } else {
                return time.get(field);
            }
        }
        return field.doGet(this);
    }

    @Override
    public ChronoLocalDateTime<D> with(DateTimeField field, long newValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDateTime<D> plus(long periodAmount, PeriodUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDateTime<D> minus(long periodAmount, PeriodUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChronoLocalDateTime<D> with(DateTimeAdjuster adjuster) {
        return adjuster.doAdjustment(this);
    }

    @Override
    public ChronoLocalDateTime<D> plus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doAdd(this);
    }

    @Override
    public ChronoLocalDateTime<D> minus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doSubtract(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == Chronology.class) {
            return (R) chrono;
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
     * Compares this {@code LocalDate} to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int compareTo(ChronoLocalDateTime<?> other) {
        int cmp = ((ChronoDate) date).compareTo((ChronoDate) other.date);
        if (cmp == 0) {
            cmp = time.compareTo(other.time);
            if (cmp == 0) {
                cmp = chrono.getName().compareTo(other.chrono.getName());
            }
        }
        return cmp;
    }

}
