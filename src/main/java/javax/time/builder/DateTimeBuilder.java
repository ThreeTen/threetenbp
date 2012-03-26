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
import static javax.time.builder.StandardDateTimeField.DAY_OF_MONTH;
import static javax.time.builder.StandardDateTimeField.DAY_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.EPOCH_DAY;
import static javax.time.builder.StandardDateTimeField.HOUR_OF_DAY;
import static javax.time.builder.StandardDateTimeField.MINUTE_OF_HOUR;
import static javax.time.builder.StandardDateTimeField.MONTH_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.NANO_OF_DAY;
import static javax.time.builder.StandardDateTimeField.NANO_OF_SECOND;
import static javax.time.builder.StandardDateTimeField.SECOND_OF_MINUTE;
import static javax.time.builder.StandardDateTimeField.YEAR;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;

/**
 * Builder that can combine date and time fields into date and time objects.
 * <p>
 * This is a mutable class.
 * 
 * @author Richard Warburton
 * @author Stephen Colebourne
 */
public final class DateTimeBuilder {

    private final EnumMap<StandardDateTimeField, Long> values;
    private Map<DateTimeField, Long> nonStandardValues;

    public static DateTimeBuilder of() {
        return new DateTimeBuilder();
    }

    private DateTimeBuilder() {
        values = new EnumMap<StandardDateTimeField, Long>(StandardDateTimeField.class);
        nonStandardValues = null;
    }

    public boolean containsValue(DateTimeField field) {
        checkNotNull(field, "Field cannot be null");
        return values.containsKey(field) || nonStandardValues != null && nonStandardValues.containsKey(field);
    }

    public long getValue(DateTimeField field) {
        checkNotNull(field, "Field cannot be null");
        Long val = values.get(field);
        if (val != null) {
            return val;
        }
        
        if (nonStandardValues != null) {
            return nonStandardValues.get(field);
        }
        
        throw new CalendricalException("Unable to find a value for that field"); // TODO
    }

    public DateTimeBuilder add(DateTimeField field, long value) {
        checkNotNull(field, "Field cannot be null");
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField standardField = (StandardDateTimeField) field;
            values.put(standardField, value);
        } else {
            if (nonStandardValues == null) {
                nonStandardValues = new HashMap<DateTimeField, Long>();
            }
            nonStandardValues.put(field, value);
        }
        return this;
    }

    public DateTimeBuilder remove(DateTimeField field) {
        checkNotNull(field, "Field cannot be null");
        if (values.remove(field) == null && nonStandardValues != null) {
            nonStandardValues.remove(field);
        }
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
    
    int getInt(StandardDateTimeField field) {
        return safeToInt(values.get(field));
    }

    boolean hasAllFields(StandardDateTimeField ... field) {
        for (StandardDateTimeField standardField : field) {
            if (!values.containsKey(standardField)) {
                return false;
            }
        }
        return true;
    }

    long resolve() {
        return 0;
    }

}

