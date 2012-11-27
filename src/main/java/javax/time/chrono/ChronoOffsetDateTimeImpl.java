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

import static javax.time.DateTimeConstants.SECONDS_PER_DAY;
import static javax.time.calendrical.ChronoField.EPOCH_DAY;
import static javax.time.calendrical.ChronoField.NANO_OF_DAY;
import static javax.time.calendrical.ChronoField.OFFSET_SECONDS;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.Instant;
import javax.time.LocalTime;
import javax.time.OffsetDateTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.jdk8.DefaultInterfaceChronoOffsetDateTime;
import javax.time.jdk8.Jdk8Methods;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;
import javax.time.zone.ZoneRules;

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
class ChronoOffsetDateTimeImpl<C extends Chrono<C>>
        extends DefaultInterfaceChronoOffsetDateTime<C>
        implements  ChronoOffsetDateTime<C>, DateTime, WithAdjuster, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2187570992341262959L;

    /**
     * The local date-time.
     */
    private final ChronoDateTimeImpl<C> dateTime;
    /**
     * The zone offset.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ChronoOffsetDateTime} from a date-time and offset.
     *
     * @param dateTime  the local date-time, not null
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     */
    static <R extends Chrono<R>> ChronoOffsetDateTime<R> of(ChronoDateTimeImpl<R> dateTime, ZoneOffset offset) {
        return new ChronoOffsetDateTimeImpl<>(dateTime, offset);
    }

    /**
     * Constructor.
     *
     * @param dateTime  the date-time, not null
     * @param offset  the zone offset, not null
     */
    protected ChronoOffsetDateTimeImpl(ChronoDateTimeImpl<C> dateTime, ZoneOffset offset) {
        Objects.requireNonNull(dateTime, "dateTime");
        Objects.requireNonNull(offset, "offset");
        this.dateTime = dateTime;
        this.offset = offset;
    }

    /**
     * Returns a new date-time based on this one, returning {@code this} where possible.
     * <p>
     * This method must be overridden so the subclass can create its own type
     * of ChronoLocalDateTime.
     *
     * @param dateTime  the date-time to create with, not null
     * @param offset  the zone offset to create with, not null
     */
    private ChronoOffsetDateTimeImpl<C> with(ChronoDateTimeImpl<C> dateTime, ZoneOffset offset) {
        if (this.dateTime == dateTime && this.offset.equals(offset)) {
            return this;
        }
        ChronoDateTimeImpl<C> impl = getDate().getChrono().ensureChronoLocalDateTime(dateTime);
        return new ChronoOffsetDateTimeImpl<>(impl, offset);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDateTime<C> getDateTime() {
        return dateTime;
    }

    //-----------------------------------------------------------------------
    @Override
    public ZoneOffset getOffset() {
        return offset;
    }

    @Override
    public ChronoOffsetDateTimeImpl<C> withOffsetSameLocal(ZoneOffset offset) {
        return with(dateTime, offset);
    }

    @Override
    public ChronoOffsetDateTimeImpl<C> withOffsetSameInstant(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getTotalSeconds() - this.offset.getTotalSeconds();
        ChronoDateTimeImpl<C> adjusted = dateTime.plusSeconds(difference);
        return new ChronoOffsetDateTimeImpl<C>(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        return field instanceof ChronoField || (field != null && field.doIsSupported(this));  // TODO: not all ChronoField are supported
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public ChronoOffsetDateTime<C> with(WithAdjuster adjuster) {
        if (adjuster instanceof ChronoLocalDate || adjuster instanceof LocalTime || adjuster instanceof ChronoLocalDateTime) {
            return with(dateTime.with(adjuster), offset);
        } else if (adjuster instanceof ZoneOffset) {
            return with(dateTime, (ZoneOffset) adjuster);
        } else if (adjuster instanceof ChronoOffsetDateTime) {
            return (ChronoOffsetDateTime<C>) adjuster;
        }
        return getDate().getChrono().ensureChronoOffsetDateTime((ChronoOffsetDateTime<C>) adjuster.doWithAdjustment(this));
    }

    @Override
    public ChronoOffsetDateTimeImpl<C> with(DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            switch (f) {
                case INSTANT_SECONDS:
                    long epochDays = Jdk8Methods.floorDiv(newValue, SECONDS_PER_DAY);
                    ChronoOffsetDateTimeImpl<C> odt = with(ChronoField.EPOCH_DAY, epochDays);
                    int secsOfDay = Jdk8Methods.floorMod(newValue, SECONDS_PER_DAY);
                    odt  = odt.with(ChronoField.SECOND_OF_DAY, secsOfDay);
                    return odt;

                case OFFSET_SECONDS: {
                    return with(dateTime, ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue)));
                }
            }
            return with(dateTime.with(field, newValue), offset);
        }
        return getDate().getChrono().ensureChronoOffsetDateTime(field.doWith(this, newValue));
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoOffsetDateTime<C> plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof ChronoUnit) {
            return with(dateTime.plus(amountToAdd, unit), offset);
        }
        return getDate().getChrono().ensureChronoOffsetDateTime(unit.doPlus(this, amountToAdd));
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> atZoneSameInstant(ZoneId zone) {
        ZoneRules rules = zone.getRules();  // latest rules version
        // Add optimization to avoid toInstant
        ChronoOffsetDateTimeImpl<C> codt = this.withOffsetSameInstant(rules.getOffset(this.toInstant()));
        return ChronoZonedDateTimeImpl.of(codt, zone);
    }

    @Override
    public ChronoZonedDateTime<C> atZoneSimilarLocal(ZoneId zone) {
        return atZoneSimilarLocal(zone, ZoneResolvers.retainOffset());
    }

    @Override
    public ChronoZonedDateTime<C> atZoneSimilarLocal(ZoneId zone, ZoneResolver resolver) {
        ZonedDateTime resolved = OffsetDateTime.from(this).atZoneSimilarLocal(zone, resolver);
        ChronoOffsetDateTimeImpl<C> codt = this
                .with(EPOCH_DAY, resolved.getDate().toEpochDay())
                .with(NANO_OF_DAY, resolved.getTime().toNanoOfDay());
        return ChronoZonedDateTimeImpl.of(codt, zone);
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime
                .with(OFFSET_SECONDS, getOffset().getTotalSeconds())
                .with(EPOCH_DAY, getDate().toEpochDay())
                .with(NANO_OF_DAY, getTime().toNanoOfDay());
    }

    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoOffsetDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
//        ChronoOffsetDateTime<?> end = (ChronoOffsetDateTime<?>) endDateTime;
        if (unit instanceof ChronoUnit) {
//            ChronoUnit f = (ChronoUnit) unit;
//            long until = dateTime.periodUntil(end.getDateTime(), unit);
            // NYI Adjust for offsets
            throw new DateTimeException("nyi: ChronoOffsetDateTime.periodUntil");
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochSecond() {
        long epochDay = dateTime.getDate().toEpochDay();
        long secs = epochDay * SECONDS_PER_DAY + dateTime.getTime().toSecondOfDay();
        secs -= offset.getTotalSeconds();
        return secs;
    }


    //-----------------------------------------------------------------------
    private Object writeReplace() {
        return new Ser(Ser.CHRONO_OFFSETDATETIME_TYPE, this);
    }

    void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(dateTime);
        out.writeObject(offset);
    }

    static ChronoOffsetDateTime<?> readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ChronoLocalDateTime<?> dateTime = (ChronoLocalDateTime<?>) in.readObject();
        ZoneOffset offset = (ZoneOffset) in.readObject();
        return dateTime.atOffset(offset);
    }

}
