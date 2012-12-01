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

import static javax.time.calendrical.ChronoUnit.SECONDS;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.Instant;
import javax.time.LocalDateTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.jdk8.DefaultInterfaceChronoZonedDateTime;
import javax.time.zone.ZoneOffsetTransition;
import javax.time.zone.ZoneRules;

/**
 * A date-time with a time-zone in the calendar neutral API.
 * <p>
 * {@code ZoneChronoDateTime} is an immutable representation of a date-time with a time-zone.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as a time-zone and zone offset.
 * <p>
 * The purpose of storing the time-zone is to distinguish the ambiguous case where
 * the local time-line overlaps, typically as a result of the end of daylight time.
 * Information about the local-time can be obtained using methods on the time-zone.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 *
 * @param <C> the chronology of this date
 */
final class ChronoZonedDateTimeImpl<C extends Chrono<C>>
        extends DefaultInterfaceChronoZonedDateTime<C>
        implements ChronoZonedDateTime<C>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -5261813987200935591L;

    /**
     * The local date-time.
     */
    private final ChronoDateTimeImpl<C> dateTime;
    /**
     * The zone offset.
     */
    private final ZoneOffset offset;
    /**
     * The zone ID.
     */
    private final ZoneId zoneId;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date-time
     * using the preferred offset if possible.
     *
     * @param localDateTime  the local date-time, not null
     * @param zoneId  the zone identifier, not null
     * @param preferredOffset  the zone offset, null if no preference
     * @return the zoned date-time, not null
     */
    static <R extends Chrono<R>> ChronoZonedDateTime<R> ofBest(
            ChronoDateTimeImpl<R> localDateTime, ZoneId zoneId, ZoneOffset preferredOffset) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        if (zoneId instanceof ZoneOffset) {
            return new ChronoZonedDateTimeImpl<R>(localDateTime, (ZoneOffset) zoneId, zoneId);
        }
        ZoneRules rules = zoneId.getRules();
        LocalDateTime isoLDT = LocalDateTime.from(localDateTime);
        List<ZoneOffset> validOffsets = rules.getValidOffsets(isoLDT);
        ZoneOffset offset;
        if (validOffsets.size() == 1) {
            offset = validOffsets.get(0);
        } else if (validOffsets.size() == 0) {
            ZoneOffsetTransition trans = rules.getTransition(isoLDT);
            localDateTime = localDateTime.plusSeconds(trans.getDuration().getSeconds());
            offset = trans.getOffsetAfter();
        } else {
            if (preferredOffset != null && validOffsets.contains(preferredOffset)) {
                offset = preferredOffset;
            } else {
                offset = validOffsets.get(0);
            }
        }
        Objects.requireNonNull(offset, "offset");  // protect against bad ZoneRules
        return new ChronoZonedDateTimeImpl<R>(localDateTime, offset, zoneId);
    }

    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from an {@code Instant}.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zoneId  the time-zone to use, validated not null
     * @return the zoned date-time, validated not null
     */
    private ChronoZonedDateTimeImpl<C> create(Instant instant, ZoneId zoneId) {
        ChronoDateTimeImpl<C> cldt = getDate().getChrono().localInstant(instant, zoneId);
        return new ChronoZonedDateTimeImpl<C>(cldt, offset, zoneId);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, not null
     * @param offset  the zone offset, not null
     * @param zone  the zone ID, not null
     */
    private ChronoZonedDateTimeImpl(ChronoDateTimeImpl<C> dateTime, ZoneOffset offset, ZoneId zoneId) {
        this.dateTime = Objects.requireNonNull(dateTime, "dateTime");
        this.offset = Objects.requireNonNull(offset, "offset");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
    }

    //-----------------------------------------------------------------------
    public ZoneOffset getOffset() {
        return offset;
    }

    @Override
    public ChronoZonedDateTime<C> withEarlierOffsetAtOverlap() {
        ZoneOffsetTransition trans = getZone().getRules().getTransition(LocalDateTime.from(this));
        if (trans != null && trans.isOverlap()) {
            ZoneOffset earlierOffset = trans.getOffsetBefore();
            if (earlierOffset.equals(offset) == false) {
                return new ChronoZonedDateTimeImpl<C>(dateTime, earlierOffset, zoneId);
            }
        }
        return this;
    }

    @Override
    public ChronoZonedDateTime<C> withLaterOffsetAtOverlap() {
        ZoneOffsetTransition trans = getZone().getRules().getTransition(LocalDateTime.from(this));
        if (trans != null) {
            ZoneOffset offset = trans.getOffsetAfter();
            if (offset.equals(getOffset()) == false) {
                return new ChronoZonedDateTimeImpl<C>(dateTime, offset, zoneId);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDateTime<C> getDateTime() {
        return dateTime;
    }

    public ZoneId getZone() {
        return zoneId;
    }

    public ChronoZonedDateTime<C> withZoneSameLocal(ZoneId zoneId) {
        return ofBest(dateTime, zoneId, offset);
    }

    @Override
    public ChronoZonedDateTime<C> withZoneSameInstant(ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "zoneId");
        return this.zoneId.equals(zoneId) ? this : create(dateTime.toInstant(offset), zoneId);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        return field instanceof ChronoField || (field != null && field.doIsSupported(this));
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> with(DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            switch (f) {
                case INSTANT_SECONDS: return plus(newValue - toEpochSecond(), SECONDS);
                case OFFSET_SECONDS: {
                    ZoneOffset offset = ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue));
                    return create(dateTime.toInstant(offset), zoneId);
                }
            }
            return ofBest(dateTime.with(field, newValue), zoneId, offset);
        }
        return getDate().getChrono().ensureChronoZonedDateTime(field.doWith(this, newValue));
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof ChronoUnit) {
            return with(dateTime.plus(amountToAdd, unit));
        }
        return getDate().getChrono().ensureChronoZonedDateTime(unit.doPlus(this, amountToAdd));   /// TODO: Generics replacement Risk!
    }

    //-----------------------------------------------------------------------
    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoZonedDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        @SuppressWarnings("unchecked")
        ChronoZonedDateTime<C> end = (ChronoZonedDateTime<C>) endDateTime;
        if (getDate().getChrono().equals(end.getDate().getChrono()) == false) {
            throw new DateTimeException("Unable to calculate period between two different chronologies");
        }
        if (unit instanceof ChronoUnit) {
            end = end.withZoneSameInstant(offset);
            return dateTime.periodUntil(end.getDateTime(), unit);
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    private Object writeReplace() {
        return new Ser(Ser.CHRONO_ZONEDDATETIME_TYPE, this);
    }

    void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(dateTime);
        out.writeObject(offset);
        out.writeObject(zoneId);
    }

    static ChronoZonedDateTime<?> readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ChronoLocalDateTime<?> dateTime = (ChronoLocalDateTime<?>) in.readObject();
        ZoneOffset offset = (ZoneOffset) in.readObject();
        ZoneId zone = (ZoneId) in.readObject();
        return dateTime.atZone(offset).withZoneSameLocal(zone);
        // TODO: ZDT uses ofLenient()
    }

}
