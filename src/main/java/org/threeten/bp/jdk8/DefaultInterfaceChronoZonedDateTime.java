/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.jdk8;

import static org.threeten.bp.temporal.ChronoField.INSTANT_SECONDS;
import static org.threeten.bp.temporal.ChronoField.OFFSET_SECONDS;
import static org.threeten.bp.temporal.ChronoUnit.NANOS;

import java.util.Objects;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalTime;
import org.threeten.bp.chrono.ChronoLocalDate;
import org.threeten.bp.chrono.ChronoZonedDateTime;
import org.threeten.bp.chrono.Chronology;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalAdjuster;
import org.threeten.bp.temporal.TemporalAmount;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.TemporalQueries;
import org.threeten.bp.temporal.TemporalQuery;
import org.threeten.bp.temporal.TemporalUnit;
import org.threeten.bp.temporal.ValueRange;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 *
 * @param <C> the chronology of this date-time
 */
public abstract class DefaultInterfaceChronoZonedDateTime<C extends Chronology<C>>
        extends DefaultInterfaceTemporal
        implements ChronoZonedDateTime<C> {

    @Override
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == INSTANT_SECONDS || field == OFFSET_SECONDS) {
                return field.range();
            }
            return toLocalDateTime().range(field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override
    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case INSTANT_SECONDS: throw new DateTimeException("Field too large for an int: " + field);
                case OFFSET_SECONDS: return getOffset().getTotalSeconds();
            }
            return toLocalDateTime().get(field);
        }
        return super.get(field);
    }

    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case INSTANT_SECONDS: return toEpochSecond();
                case OFFSET_SECONDS: return getOffset().getTotalSeconds();
            }
            return toLocalDateTime().getLong(field);
        }
        return field.getFrom(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<C> toLocalDate() {
        return toLocalDateTime().toLocalDate();
    }

    @Override
    public LocalTime toLocalTime() {
        return toLocalDateTime().toLocalTime();
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> with(TemporalAdjuster adjuster) {
        return toLocalDate().getChronology().ensureChronoZonedDateTime(super.with(adjuster));
    }

    @Override
    public ChronoZonedDateTime<C> plus(TemporalAmount amount) {
        return toLocalDate().getChronology().ensureChronoZonedDateTime(super.plus(amount));
    }

    @Override
    public ChronoZonedDateTime<C> minus(TemporalAmount amount) {
        return toLocalDate().getChronology().ensureChronoZonedDateTime(super.minus(amount));
    }

    @Override
    public ChronoZonedDateTime<C> minus(long amountToSubtract, TemporalUnit unit) {
        return toLocalDate().getChronology().ensureChronoZonedDateTime(super.minus(amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId()) {
            return (R) getZone();
        } else if (query == TemporalQueries.chronology()) {
            return (R) toLocalDate().getChronology();
        } else if (query == TemporalQueries.precision()) {
            return (R) NANOS;
        } else if (query == TemporalQueries.offset()) {
            return (R) getOffset();
        } else if (query == TemporalQueries.localDate()) {
            return (R) toLocalDate();
        } else if (query == TemporalQueries.localTime()) {
            return (R) toLocalTime();
        }
        return super.query(query);
    }

    //-------------------------------------------------------------------------
    @Override
    public Instant toInstant() {
        return Instant.ofEpochSecond(toEpochSecond(), toLocalTime().getNano());
    }

    @Override
    public long toEpochSecond() {
        long epochDay = toLocalDate().toEpochDay();
        long secs = epochDay * 86400 + toLocalTime().toSecondOfDay();
        secs -= getOffset().getTotalSeconds();
        return secs;
    }

    //-------------------------------------------------------------------------
    @Override
    public int compareTo(ChronoZonedDateTime<?> other) {
        int cmp = Long.compare(toEpochSecond(), other.toEpochSecond());
        if (cmp == 0) {
            cmp = toLocalTime().getNano() - other.toLocalTime().getNano();
            if (cmp == 0) {
                cmp = toLocalDateTime().compareTo(other.toLocalDateTime());
                if (cmp == 0) {
                    cmp = getZone().getId().compareTo(other.getZone().getId());
                    if (cmp == 0) {
                        cmp = toLocalDate().getChronology().compareTo(other.toLocalDate().getChronology());
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
            (thisEpochSec == otherEpochSec && toLocalTime().getNano() > other.toLocalTime().getNano());
    }

    @Override
    public boolean isBefore(ChronoZonedDateTime<?> other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec < otherEpochSec ||
            (thisEpochSec == otherEpochSec && toLocalTime().getNano() < other.toLocalTime().getNano());
    }

    @Override
    public boolean isEqual(ChronoZonedDateTime<?> other) {
        return toEpochSecond() == other.toEpochSecond() &&
                toLocalTime().getNano() == other.toLocalTime().getNano();
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
        return toLocalDateTime().hashCode() ^ getOffset().hashCode() ^ Integer.rotateLeft(getZone().hashCode(), 3);
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        String str = toLocalDateTime().toString() + getOffset().toString();
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
