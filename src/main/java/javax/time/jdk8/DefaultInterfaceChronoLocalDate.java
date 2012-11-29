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

import static javax.time.calendrical.ChronoField.DAY_OF_MONTH;
import static javax.time.calendrical.ChronoField.EPOCH_DAY;
import static javax.time.calendrical.ChronoField.ERA;
import static javax.time.calendrical.ChronoField.MONTH_OF_YEAR;
import static javax.time.calendrical.ChronoField.YEAR;
import static javax.time.calendrical.ChronoField.YEAR_OF_ERA;

import java.util.Objects;

import javax.time.LocalTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.Chrono;
import javax.time.chrono.ChronoLocalDate;
import javax.time.chrono.ChronoLocalDateTime;
import javax.time.chrono.Era;
import javax.time.format.DateTimeFormatter;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 *
 * @param <C> the chronology of this date-time
 */
public abstract class DefaultInterfaceChronoLocalDate<C extends Chrono<C>>
        extends DefaultInterfaceDateTime
        implements ChronoLocalDate<C> {

    @Override
    public Era<C> getEra() {
        return getChrono().eraOf(get(ERA));
    }

    @Override
    public boolean isLeapYear() {
        return getChrono().isLeapYear(getLong(YEAR));
    }

    @Override
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    //-------------------------------------------------------------------------
    @Override
    public ChronoLocalDate<C> with(WithAdjuster adjuster) {
        return getChrono().ensureChronoLocalDate(super.with(adjuster));
    }

    @Override
    public ChronoLocalDate<C> plus(PlusAdjuster adjuster) {
        return getChrono().ensureChronoLocalDate(super.plus(adjuster));
    }

    @Override
    public ChronoLocalDate<C> minus(MinusAdjuster adjuster) {
        return getChrono().ensureChronoLocalDate(super.minus(adjuster));
    }

    @Override
    public ChronoLocalDate<C> minus(long amountToSubtract, PeriodUnit unit) {
        return getChrono().ensureChronoLocalDate(super.minus(amountToSubtract, unit));
    }

    //-------------------------------------------------------------------------
    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime.with(EPOCH_DAY, toEpochDay());
    }

    @Override
    public ChronoLocalDateTime<C> atTime(LocalTime localTime) {
        return Chrono.dateTime(this, localTime);
    }

    @Override
    public <R> R query(Query<R> query) {
        if (query == Query.CHRONO) {
            return (R) getChrono();
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
            cmp = getChrono().compareTo(other.getChrono());
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
        return getChrono().hashCode() ^ ((int) (epDay ^ (epDay >>> 32)));
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        // getLong() reduces chances of exceptions in toString()
        long yoe = getLong(YEAR_OF_ERA);
        long moy = getLong(MONTH_OF_YEAR);
        long dom = getLong(DAY_OF_MONTH);
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChrono().toString())
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
