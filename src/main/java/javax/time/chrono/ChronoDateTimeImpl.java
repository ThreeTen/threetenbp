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

import static javax.time.DateTimeConstants.HOURS_PER_DAY;
import static javax.time.DateTimeConstants.MICROS_PER_DAY;
import static javax.time.DateTimeConstants.MILLIS_PER_DAY;
import static javax.time.DateTimeConstants.MINUTES_PER_DAY;
import static javax.time.DateTimeConstants.NANOS_PER_DAY;
import static javax.time.DateTimeConstants.NANOS_PER_HOUR;
import static javax.time.DateTimeConstants.NANOS_PER_MINUTE;
import static javax.time.DateTimeConstants.NANOS_PER_SECOND;
import static javax.time.DateTimeConstants.SECONDS_PER_DAY;
import static javax.time.calendrical.ChronoField.EPOCH_DAY;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.LocalTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.PeriodUnit;
import javax.time.jdk8.DefaultInterfaceChronoLocalDateTime;
import javax.time.jdk8.Jdk8Methods;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

/**
 * A date-time without a time-zone for the calendar neutral API.
 * <p>
 * {@code ChronoLocalDateTime} is an immutable date-time object that represents a date-time, often
 * viewed as year-month-day-hour-minute-second. This object can also access other
 * fields such as day-of-year, day-of-week and week-of-year.
 * <p>
 * This class stores all date and time fields, to a precision of nanoseconds.
 * It does not store or represent a time-zone. For example, the value
 * "2nd October 2007 at 13:45.30.123456789" can be stored in an {@code ChronoLocalDateTime}.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 *
 * @param <C> the chronology of this date
 */
final class ChronoDateTimeImpl<C extends Chrono<C>>
        extends DefaultInterfaceChronoLocalDateTime<C>
        implements  ChronoLocalDateTime<C>, DateTime, WithAdjuster, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 4556003607393004514L;

    /**
     * The date part.
     */
    private final ChronoLocalDate<C> date;
    /**
     * The time part.
     */
    private final LocalTime time;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ChronoLocalDateTime} from a date and time.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @return the local date-time, not null
     */
    static <R extends Chrono<R>> ChronoDateTimeImpl<R> of(ChronoLocalDate<R> date, LocalTime time) {
        return new ChronoDateTimeImpl<>(date, time);
    }

    /**
     * Constructor.
     *
     * @param date  the date part of the date-time, not null
     * @param time  the time part of the date-time, not null
     */
    private ChronoDateTimeImpl(ChronoLocalDate<C> date, LocalTime time) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        this.date = date;
        this.time = time;
    }

    /**
     * Returns a copy of this date-time with the new date and time, checking
     * to see if a new object is in fact required.
     *
     * @param newDate  the date of the new date-time, not null
     * @param newTime  the time of the new date-time, not null
     * @return the date-time, not null
     */
    private ChronoDateTimeImpl<C> with(DateTime newDate, LocalTime newTime) {
        if (date == newDate && time == newTime) {
            return this;
        }
        // Validate that the new DateTime is a ChronoLocalDate (and not something else)
        ChronoLocalDate<C> cd = date.getChrono().ensureChronoLocalDate(newDate);
        return new ChronoDateTimeImpl<>(cd, newTime);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<C> getDate() {
        return date;
    }

    @Override
    public LocalTime getTime() {
        return time;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isDateField() || f.isTimeField();
        }
        return field != null && field.doIsSupported(this);
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return (f.isTimeField() ? time.range(field) : date.range(field));
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return (f.isTimeField() ? time.get(field) : date.get(field));
        }
        return range(field).checkValidIntValue(getLong(field), field);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return (f.isTimeField() ? time.getLong(field) : date.getLong(field));
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public ChronoDateTimeImpl<C> with(WithAdjuster adjuster) {
        if (adjuster instanceof ChronoLocalDate) {
            // The Chrono is checked in with(date,time)
            return with((ChronoLocalDate<C>) adjuster, time);
        } else if (adjuster instanceof LocalTime) {
            return with(date, (LocalTime) adjuster);
        } else if (adjuster instanceof ChronoDateTimeImpl) {
            return date.getChrono().ensureChronoLocalDateTime((ChronoDateTimeImpl<?>) adjuster);
        }
        return date.getChrono().ensureChronoLocalDateTime((ChronoDateTimeImpl<?>) adjuster.doWithAdjustment(this));
    }

    @Override
    public ChronoDateTimeImpl<C> with(DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (f.isTimeField()) {
                return with(date, time.with(field, newValue));
            } else {
                return with(date.with(field, newValue), time);
            }
        }
        return date.getChrono().ensureChronoLocalDateTime(field.doWith(this, newValue));
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoDateTimeImpl<C> plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (f) {
                case NANOS: return plusNanos(amountToAdd);
                case MICROS: return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
                case MILLIS: return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000000);
                case SECONDS: return plusSeconds(amountToAdd);
                case MINUTES: return plusMinutes(amountToAdd);
                case HOURS: return plusHours(amountToAdd);
                case HALF_DAYS: return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);  // no overflow (256 is multiple of 2)
            }
            return with(date.plus(amountToAdd, unit), time);
        }
        return date.getChrono().ensureChronoLocalDateTime(unit.doPlus(this, amountToAdd));
    }

    private ChronoDateTimeImpl<C> plusDays(long days) {
        return with(date.plus(days, ChronoUnit.DAYS), time);
    }

    private ChronoDateTimeImpl<C> plusHours(long hours) {
        return plusWithOverflow(date, hours, 0, 0, 0);
    }

    private ChronoDateTimeImpl<C> plusMinutes(long minutes) {
        return plusWithOverflow(date, 0, minutes, 0, 0);
    }

    ChronoDateTimeImpl<C> plusSeconds(long seconds) {
        return plusWithOverflow(date, 0, 0, seconds, 0);
    }

    private ChronoDateTimeImpl<C> plusNanos(long nanos) {
        return plusWithOverflow(date, 0, 0, 0, nanos);
    }

    //-----------------------------------------------------------------------
    private ChronoDateTimeImpl<C> plusWithOverflow(ChronoLocalDate<C> newDate, long hours, long minutes, long seconds, long nanos) {
        // 9223372036854775808 long, 2147483648 int
        if ((hours | minutes | seconds | nanos) == 0) {
            return with(newDate, time);
        }
        long totDays = nanos / NANOS_PER_DAY +             //   max/24*60*60*1B
                seconds / SECONDS_PER_DAY +                //   max/24*60*60
                minutes / MINUTES_PER_DAY +                //   max/24*60
                hours / HOURS_PER_DAY;                     //   max/24
        long totNanos = nanos % NANOS_PER_DAY +                    //   max  86400000000000
                (seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
                (minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
                (hours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
        long curNoD = time.toNanoOfDay();                          //   max  86400000000000
        totNanos = totNanos + curNoD;                              // total 432000000000000
        totDays += Jdk8Methods.floorDiv(totNanos, NANOS_PER_DAY);
        long newNoD = Jdk8Methods.floorMod(totNanos, NANOS_PER_DAY);
        LocalTime newTime = (newNoD == curNoD ? time : LocalTime.ofNanoOfDay(newNoD));
        return with(newDate.plus(totDays, ChronoUnit.DAYS), newTime);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoOffsetDateTime<C> atOffset(ZoneOffset offset) {
        return ChronoOffsetDateTimeImpl.of(this, offset);
    }

    @Override
    public ChronoZonedDateTime<C> atZone(ZoneId zone) {
        return ChronoZonedDateTimeImpl.of(this, zone, ZoneResolvers.postGapPreOverlap());
    }

    @Override
    public ChronoZonedDateTime<C> atZone(ZoneId zone, ZoneResolver resolver) {
        return ChronoZonedDateTimeImpl.of(this, zone, resolver);
    }

    //-----------------------------------------------------------------------
    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoLocalDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        @SuppressWarnings("unchecked")
        ChronoLocalDateTime<C> end = (ChronoLocalDateTime<C>) endDateTime;
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            if (f.isTimeUnit()) {
                long amount = end.getLong(EPOCH_DAY) - date.getLong(EPOCH_DAY);
                switch (f) {
                    case NANOS: amount = Jdk8Methods.safeMultiply(amount, NANOS_PER_DAY); break;
                    case MICROS: amount = Jdk8Methods.safeMultiply(amount, MICROS_PER_DAY); break;
                    case MILLIS: amount = Jdk8Methods.safeMultiply(amount, MILLIS_PER_DAY); break;
                    case SECONDS: amount = Jdk8Methods.safeMultiply(amount, SECONDS_PER_DAY); break;
                    case MINUTES: amount = Jdk8Methods.safeMultiply(amount, MINUTES_PER_DAY); break;
                    case HOURS: amount = Jdk8Methods.safeMultiply(amount, HOURS_PER_DAY); break;
                    case HALF_DAYS: amount = Jdk8Methods.safeMultiply(amount, 2); break;
                }
                return Jdk8Methods.safeAdd(amount, time.periodUntil(end.getTime(), unit));
            }
            ChronoLocalDate<C> endDate = end.getDate();
            if (end.getTime().isBefore(time)) {
                endDate = endDate.minus(1, ChronoUnit.DAYS);
            }
            return date.periodUntil(endDate, unit);
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    private Object writeReplace() {
        return new Ser(Ser.CHRONO_LOCALDATETIME_TYPE, this);
    }

    void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(date);
        out.writeObject(time);
    }

    static ChronoLocalDateTime<?> readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ChronoLocalDate<?> date = (ChronoLocalDate<?>) in.readObject();
        LocalTime time = (LocalTime) in.readObject();
        return date.atTime(time);
    }

}
