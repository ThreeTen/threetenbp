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

import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoField.NANO_OF_DAY;
import static org.threeten.bp.temporal.ChronoUnit.NANOS;

import java.util.Objects;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.Chrono;
import org.threeten.bp.temporal.ChronoLocalDateTime;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAdder;
import org.threeten.bp.temporal.TemporalAdjuster;
import org.threeten.bp.temporal.TemporalQueries;
import org.threeten.bp.temporal.TemporalQuery;
import org.threeten.bp.temporal.TemporalSubtractor;
import org.threeten.bp.temporal.TemporalUnit;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 *
 * @param <C> the chronology of this date-time
 */
public abstract class DefaultInterfaceChronoLocalDateTime<C extends Chrono<C>>
        extends DefaultInterfaceDateTime
        implements ChronoLocalDateTime<C> {

    @Override
    public ChronoLocalDateTime<C> with(TemporalAdjuster adjuster) {
        return getDate().getChrono().ensureChronoLocalDateTime(super.with(adjuster));
    }

    @Override
    public ChronoLocalDateTime<C> plus(TemporalAdder adjuster) {
        return getDate().getChrono().ensureChronoLocalDateTime(super.plus(adjuster));
    }

    @Override
    public ChronoLocalDateTime<C> minus(TemporalSubtractor adjuster) {
        return getDate().getChrono().ensureChronoLocalDateTime(super.minus(adjuster));
    }

    @Override
    public ChronoLocalDateTime<C> minus(long amountToSubtract, TemporalUnit unit) {
        return getDate().getChrono().ensureChronoLocalDateTime(super.minus(amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal
                .with(EPOCH_DAY, getDate().toEpochDay())
                .with(NANO_OF_DAY, getTime().toNanoOfDay());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.chrono()) {
            return (R) getDate().getChrono();
        } else if (query == TemporalQueries.precision()) {
            return (R) NANOS;
        }
        return super.query(query);
    }

    //-----------------------------------------------------------------------
    @Override
    public Instant toInstant(ZoneOffset offset) {
        return Instant.ofEpochSecond(toEpochSecond(offset), getTime().getNano());
    }

    @Override
    public long toEpochSecond(ZoneOffset offset) {
        Objects.requireNonNull(offset, "offset");
        long epochDay = getDate().toEpochDay();
        long secs = epochDay * 86400 + getTime().toSecondOfDay();
        secs -= offset.getTotalSeconds();
        return secs;
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(ChronoLocalDateTime<?> other) {
        int cmp = getDate().compareTo(other.getDate());
        if (cmp == 0) {
            cmp = getTime().compareTo(other.getTime());
            if (cmp == 0) {
                cmp = getDate().getChrono().compareTo(other.getDate().getChrono());
            }
        }
        return cmp;
    }

    @Override
    public boolean isAfter(ChronoLocalDateTime<?> other) {
        long thisEpDay = this.getDate().toEpochDay();
        long otherEpDay = other.getDate().toEpochDay();
        return thisEpDay > otherEpDay ||
            (thisEpDay == otherEpDay && this.getTime().toNanoOfDay() > other.getTime().toNanoOfDay());
    }

    @Override
    public boolean isBefore(ChronoLocalDateTime<?> other) {
        long thisEpDay = this.getDate().toEpochDay();
        long otherEpDay = other.getDate().toEpochDay();
        return thisEpDay < otherEpDay ||
            (thisEpDay == otherEpDay && this.getTime().toNanoOfDay() < other.getTime().toNanoOfDay());
    }

    @Override
    public boolean isEqual(ChronoLocalDateTime<?> other) {
        // Do the time check first, it is cheaper than computing EPOCH day.
        return this.getTime().toNanoOfDay() == other.getTime().toNanoOfDay() &&
               this.getDate().toEpochDay() == other.getDate().toEpochDay();
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoLocalDateTime) {
            return compareTo((ChronoLocalDateTime<?>) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getDate().hashCode() ^ getTime().hashCode();
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        return getDate().toString() + 'T' + getTime().toString();
    }

    @Override
    public String toString(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.print(this);
    }

}
