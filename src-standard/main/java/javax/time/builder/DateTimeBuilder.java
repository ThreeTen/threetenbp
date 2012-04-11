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
package javax.time.builder;

import static javax.time.MathUtils.checkNotNull;
import static javax.time.MathUtils.safeToInt;
import static javax.time.builder.LocalDateField.DAY_OF_MONTH;
import static javax.time.builder.LocalDateField.DAY_OF_YEAR;
import static javax.time.builder.LocalDateField.EPOCH_DAY;
import static javax.time.builder.LocalDateField.MONTH_OF_YEAR;
import static javax.time.builder.LocalDateField.YEAR;
import static javax.time.builder.LocalTimeField.HOUR_OF_DAY;
import static javax.time.builder.LocalTimeField.MINUTE_OF_HOUR;
import static javax.time.builder.LocalTimeField.NANO_OF_DAY;
import static javax.time.builder.LocalTimeField.NANO_OF_SECOND;
import static javax.time.builder.LocalTimeField.SECOND_OF_MINUTE;
import static javax.time.chrono.ChronoField.ERA;
import static javax.time.chrono.ChronoField.PROLEPTIC_YEAR;
import static javax.time.chrono.ChronoField.YEAR_OF_ERA;

import java.util.HashMap;
import java.util.Map;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.chrono.Chrono;
import javax.time.chrono.ChronoDate;
import javax.time.chrono.ChronoField;

/**
 * Builder that can combine date and time fields into date and time objects.
 * <p>
 * This is a mutable class.
 * 
 * @author Richard Warburton
 * @author Stephen Colebourne
 */
public final class DateTimeBuilder {

    private final Map<CalendricalField, Long> values;

    public static DateTimeBuilder of() {
        return new DateTimeBuilder();
    }

    private DateTimeBuilder() {
        values = new HashMap<CalendricalField, Long>();
    }

    public boolean containsValue(CalendricalField field) {
        checkNotNull(field, "Field cannot be null");
        return values.containsKey(field);
    }

    public long getLong(CalendricalField field) {
        checkNotNull(field, "Field cannot be null");
        Long val = (Long) values.get(field);
        if (val == null) {
            throw new CalendricalException("Unable to find a value for that field"); // TODO
        }
        
        return val;
    }

    public DateTimeBuilder add(CalendricalField field, long value) {
        checkNotNull(field, "Field cannot be null");
        values.put(field, value);
        return this;
    }

    public DateTimeBuilder remove(CalendricalField field) {
        checkNotNull(field, "Field cannot be null");
        values.remove(field);
        return this;
    }

    public long build() {
        return 0;
    }

    public LocalDate buildLocalDate() {
        if (hasAllFields(YEAR, MONTH_OF_YEAR, DAY_OF_MONTH)) {
            return LocalDate.of(getInt(YEAR), getInt(MONTH_OF_YEAR), getInt(DAY_OF_MONTH));
        } else if (hasAllFields(EPOCH_DAY)) {
            return LocalDate.ofEpochDay(values.get(EPOCH_DAY));
        } else if (hasAllFields(YEAR, DAY_OF_YEAR)) {
            return LocalDate.ofYearDay(getInt(YEAR), getInt(DAY_OF_YEAR));
        }
        throw new CalendricalException("Unable to build Date due to missing fields"); // TODO
    }
    
    public ChronoDate buildChronoDate(Chrono chrono) {
        if (hasAllFields(PROLEPTIC_YEAR, ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH)) {
            return chrono.createDate(getInt(PROLEPTIC_YEAR), getInt(ChronoField.MONTH_OF_YEAR), getInt(ChronoField.DAY_OF_MONTH));
        } else if (hasAllFields(ERA, YEAR_OF_ERA, ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH)) {
            // TODO: fix the Era situation
            return chrono.createDate(null, getInt(YEAR_OF_ERA), getInt(ChronoField.MONTH_OF_YEAR), getInt(ChronoField.DAY_OF_MONTH));
        } else {
            return chrono.createDate(buildLocalDate());
        }
    }

    public LocalTime buildLocalTime() {
        boolean normalFields = hasAllFields(HOUR_OF_DAY, MINUTE_OF_HOUR);
        boolean uptoSecond = normalFields && values.containsKey(SECOND_OF_MINUTE);
        boolean hasNano = values.containsKey(NANO_OF_SECOND);
        if (uptoSecond && hasNano) {
            return LocalTime.of(getInt(HOUR_OF_DAY), getInt(MINUTE_OF_HOUR), getInt(SECOND_OF_MINUTE), getInt(NANO_OF_SECOND));
        } else if (uptoSecond) {
            return LocalTime.of(getInt(HOUR_OF_DAY), getInt(MINUTE_OF_HOUR), getInt(SECOND_OF_MINUTE));
        } else if (normalFields) {
            return LocalTime.of(getInt(HOUR_OF_DAY), getInt(MINUTE_OF_HOUR));
        } else if (values.containsKey(NANO_OF_DAY)) {
            return LocalTime.ofNanoOfDay(values.get(NANO_OF_DAY));
        }
        throw new CalendricalException("Unable to build Time due to missing fields"); // TODO
    }
    
    public LocalDateTime buildLocalDateTime() {
        return LocalDateTime.of(buildLocalDate(), buildLocalTime());
    }
    
    int getInt(CalendricalField field) {
        return safeToInt(values.get(field));
    }

    boolean hasAllFields(CalendricalField ... fields) {
        for (CalendricalField field : fields) {
            if (!values.containsKey(field)) {
                return false;
            }
        }
        return true;
    }

    long resolve() {
        return 0;
    }

}
