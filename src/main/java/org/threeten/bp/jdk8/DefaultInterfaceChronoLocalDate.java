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

import static org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoField.ERA;
import static org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.YEAR;
import static org.threeten.bp.temporal.ChronoField.YEAR_OF_ERA;

import java.util.Objects;

import org.threeten.bp.LocalTime;
import org.threeten.bp.chrono.ChronoLocalDate;
import org.threeten.bp.chrono.ChronoLocalDateTime;
import org.threeten.bp.chrono.Chronology;
import org.threeten.bp.chrono.Era;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAdjuster;
import org.threeten.bp.temporal.TemporalAmount;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.TemporalQueries;
import org.threeten.bp.temporal.TemporalQuery;
import org.threeten.bp.temporal.TemporalUnit;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 *
 * @param <D> the chronology of this date-time
 */
public abstract class DefaultInterfaceChronoLocalDate<D extends ChronoLocalDate<D>>
        extends DefaultInterfaceTemporal
        implements ChronoLocalDate<D> {

    @Override
    public Era getEra() {
        return getChronology().eraOf(get(ERA));
    }

    @Override
    public boolean isLeapYear() {
        return getChronology().isLeapYear(getLong(YEAR));
    }

    @Override
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return ((ChronoField) field).isDateField();
        }
        return field != null && field.isSupportedBy(this);
    }

    //-------------------------------------------------------------------------
    @Override
    public ChronoLocalDate<D> with(TemporalAdjuster adjuster) {
        return getChronology().ensureChronoLocalDate(super.with(adjuster));
    }

    @Override
    public ChronoLocalDate<D> plus(TemporalAmount amount) {
        return getChronology().ensureChronoLocalDate(super.plus(amount));
    }

    @Override
    public ChronoLocalDate<D> minus(TemporalAmount amount) {
        return getChronology().ensureChronoLocalDate(super.minus(amount));
    }

    @Override
    public ChronoLocalDate<D> minus(long amountToSubtract, TemporalUnit unit) {
        return getChronology().ensureChronoLocalDate(super.minus(amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(EPOCH_DAY, toEpochDay());
    }

    @Override
    public ChronoLocalDateTime<D> atTime(LocalTime localTime) {
        return Chronology.dateTime(this, localTime);
    }

    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.chronology()) {
            return (R) getChronology();
        }
        return super.query(query);
    }

    @Override
    public long toEpochDay() {
        return getLong(EPOCH_DAY);
    }

    //-------------------------------------------------------------------------
    @Override
    public int compareTo(ChronoLocalDate<?> other) {
        int cmp = Long.compare(toEpochDay(), other.toEpochDay());
        if (cmp == 0) {
            cmp = getChronology().compareTo(other.getChronology());
        }
        return cmp;
    }

    @Override
    public boolean isAfter(ChronoLocalDate<?> other) {
        return this.toEpochDay() > other.toEpochDay();
    }

    @Override
    public boolean isBefore(ChronoLocalDate<?> other) {
        return this.toEpochDay() < other.toEpochDay();
    }

    @Override
    public boolean isEqual(ChronoLocalDate<?> other) {
        return this.toEpochDay() == other.toEpochDay();
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoLocalDate) {
            return compareTo((ChronoLocalDate<?>) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        long epDay = toEpochDay();
        return getChronology().hashCode() ^ ((int) (epDay ^ (epDay >>> 32)));
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        // getLong() reduces chances of exceptions in toString()
        long yoe = getLong(YEAR_OF_ERA);
        long moy = getLong(MONTH_OF_YEAR);
        long dom = getLong(DAY_OF_MONTH);
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString())
                .append(" ")
                .append(getEra())
                .append(" ")
                .append(yoe)
                .append(moy < 10 ? "-0" : "-").append(moy)
                .append(dom < 10 ? "-0" : "-").append(dom);
        return buf.toString();
    }

    @Override
    public String toString(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.print(this);
    }

}
