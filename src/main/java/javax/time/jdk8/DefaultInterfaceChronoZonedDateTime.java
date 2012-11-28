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
package javax.time.jdk8;

import static javax.time.DateTimeConstants.SECONDS_PER_DAY;
import static javax.time.calendrical.ChronoField.INSTANT_SECONDS;
import static javax.time.calendrical.ChronoField.OFFSET_SECONDS;

import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.Instant;
import javax.time.LocalTime;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.Chrono;
import javax.time.chrono.ChronoLocalDate;
import javax.time.chrono.ChronoZonedDateTime;
import javax.time.format.DateTimeFormatter;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 *
 * @param <C> the chronology of this date-time
 */
public abstract class DefaultInterfaceChronoZonedDateTime<C extends Chrono<C>>
        extends DefaultInterfaceDateTime
        implements ChronoZonedDateTime<C> {

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof ChronoField) {
            if (field == INSTANT_SECONDS || field == OFFSET_SECONDS) {
                return field.range();
            }
            return getDateTime().range(field);
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case INSTANT_SECONDS: throw new DateTimeException("Field too large for an int: " + field);
                case OFFSET_SECONDS: return getOffset().getTotalSeconds();
            }
            return getDateTime().get(field);
        }
        return super.get(field);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case INSTANT_SECONDS: return toEpochSecond();
                case OFFSET_SECONDS: return getOffset().getTotalSeconds();
            }
            return getDateTime().getLong(field);
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<C> getDate() {
        return getDateTime().getDate();
    }

    @Override
    public LocalTime getTime() {
        return getDateTime().getTime();
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> with(WithAdjuster adjuster) {
        return getDate().getChrono().ensureChronoZonedDateTime(super.with(adjuster));
    }

    @Override
    public ChronoZonedDateTime<C> plus(PlusAdjuster adjuster) {
        return getDate().getChrono().ensureChronoZonedDateTime(super.plus(adjuster));
    }

    @Override
    public ChronoZonedDateTime<C> minus(MinusAdjuster adjuster) {
        return getDate().getChrono().ensureChronoZonedDateTime(super.minus(adjuster));
    }

    @Override
    public ChronoZonedDateTime<C> minus(long amountToSubtract, PeriodUnit unit) {
        return getDate().getChrono().ensureChronoZonedDateTime(super.minus(amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override
    public <R> R query(Query<R> query) {
        if (query == Query.ZONE_ID) {
            return (R) getZone();
        } else if (query == Query.CHRONO) {
            return (R) getDate().getChrono();
        }
        return query.doQuery(this);
    }

    //-------------------------------------------------------------------------
    @Override
    public Instant toInstant() {
        return Instant.ofEpochSecond(toEpochSecond(), getTime().getNano());
    }

    @Override
    public long toEpochSecond() {
        long epochDay = getDate().toEpochDay();
        long secs = epochDay * SECONDS_PER_DAY + getTime().toSecondOfDay();
        secs -= getOffset().getTotalSeconds();
        return secs;
    }

    //-------------------------------------------------------------------------
    @Override
    public int compareTo(ChronoZonedDateTime<?> other) {
        int cmp = Long.compare(toEpochSecond(), other.toEpochSecond());
        if (cmp == 0) {
            cmp = getTime().getNano() - other.getTime().getNano();
            if (cmp == 0) {
                cmp = getDateTime().compareTo(other.getDateTime());
                if (cmp == 0) {
                    cmp = getZone().getId().compareTo(other.getZone().getId());
                    if (cmp == 0) {
                        cmp = getDate().getChrono().compareTo(other.getDate().getChrono());
                    }
                }
            }
        }
        return cmp;
    }

    @Override
    public boolean isAfter(ChronoZonedDateTime<?> other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec > otherEpochSec ||
            (thisEpochSec == otherEpochSec && getTime().getNano() > other.getTime().getNano());
    }

    @Override
    public boolean isBefore(ChronoZonedDateTime<?> other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec < otherEpochSec ||
            (thisEpochSec == otherEpochSec && getTime().getNano() < other.getTime().getNano());
    }

    @Override
    public boolean isEqual(ChronoZonedDateTime<?> other) {
        return toEpochSecond() == other.toEpochSecond() &&
                getTime().getNano() == other.getTime().getNano();
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoZonedDateTime) {
            return compareTo((ChronoZonedDateTime<?>) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getDateTime().hashCode() ^ getOffset().hashCode() ^ Integer.rotateLeft(getZone().hashCode(), 3);
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        String str = getDateTime().toString() + getOffset().toString();
        if (getOffset() != getZone()) {
            str += '[' + getZone().toString() + ']';
        }
        return str;
    }

    @Override
    public String toString(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.print(this);
    }

}
