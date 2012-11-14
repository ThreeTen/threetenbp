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
package javax.time.extra.chrono;

import static javax.time.calendrical.ChronoField.ERA;

import java.util.Locale;

import javax.time.DateTimeException;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.chrono.ChronoLocalDate;
import javax.time.chrono.Era;
import javax.time.format.DateTimeFormatterBuilder;
import javax.time.format.TextStyle;

/**
 * An era in the Coptic calendar system.
 * <p>
 * The Coptic calendar system uses the 'Era of the Martyrs'.
 * The start of the Coptic epoch {@code 0001-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code CopticEra}.
 * Use {@code getValue()} instead.</b>
 *
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
enum CopticEra implements Era<CopticChrono> {

    /**
     * The singleton instance for the era BEFORE_AM, 'Before Era of the Martyrs'.
     * This has the numeric value of {@code 0}.
     */
    BEFORE_AM,
    /**
     * The singleton instance for the era AM, 'Era of the Martyrs'.
     * This has the numeric value of {@code 1}.
     */
    AM;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code CopticEra} from an {@code int} value.
     * <p>
     * {@code CopticEra} is an enum representing the Coptic eras of BEFORE_AM/AM.
     * This factory allows the enum to be obtained from the {@code int} value.
     *
     * @param era  the BEFORE_AM/AM value to represent, from 0 (BEFORE_AM) to 1 (AM)
     * @return the era singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static CopticEra of(int era) {
        switch (era) {
            case 0:
                return BEFORE_AM;
            case 1:
                return AM;
            default:
                throw new DateTimeException("Invalid era: " + era);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era BEFORE_AM has the value 0, while the era AM has the value 1.
     *
     * @return the era value, from 0 (BEFORE_AM) to 1 (AM)
     */
    public int getValue() {
        return ordinal();
    }

    @Override
    public CopticChrono getChrono() {
        return CopticChrono.INSTANCE;
    }

    // JDK8 default methods:
    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<CopticChrono> date(int year, int month, int day) {
        return getChrono().date(this, year, month, day);
    }

    @Override
    public ChronoLocalDate<CopticChrono> dateFromYearDay(int year, int dayOfYear) {
        return getChrono().dateFromYearDay(this, year, dayOfYear);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        if (field instanceof ChronoField) {
            return field == ERA;
        }
        return field != null && field.doIsSupported(this);
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field == ERA) {
            return field.range();
        } else if (field instanceof ChronoField) {
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        if (field == ERA) {
            return getValue();
        }
        return range(field).checkValidIntValue(getLong(field), field);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field == ERA) {
            return getValue();
        } else if (field instanceof ChronoField) {
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doGet(this);
    }

    @Override
    public Era<CopticChrono> with(DateTimeField field, long newValue) {
        if (field == ERA) {
            int eravalue = ((ChronoField) field).checkValidIntValue(newValue);
            return getChrono().eraOf(eravalue);
        } else if (field instanceof ChronoField) {
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doSet(this, newValue);
    }

    //-------------------------------------------------------------------------
    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime.with(ERA, getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(Query<R> query) {
        if (query == Query.ZONE_ID) {
            return null;
        } else if (query == Query.CHRONO) {
            return (R) getChrono();
        }
        return query.doQuery(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public String getText(TextStyle style, Locale locale) {
        return new DateTimeFormatterBuilder().appendText(ERA, style).toFormatter(locale).print(this);
    }

}
