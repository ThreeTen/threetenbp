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

import static javax.time.builder.LocalDateUnit.DAYS;
import static javax.time.builder.LocalDateUnit.FOREVER;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A field of date/time.
 */
public enum JulianDayField implements DateField {

    JULIAN_DAY("JulianDay", DAYS, FOREVER, DateTimeRuleRange.of(-1000000, 1000000)),  // TODO: correct range
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", DAYS, FOREVER, DateTimeRuleRange.of(-1000000, 1000000)),  // TODO: correct range
    TRUCTATED_JULIAN_DAY("TrucatedJulianDay", DAYS, FOREVER, DateTimeRuleRange.of(-1000000, 1000000)),  // TODO: correct range
    // lots of others Lilian, ANSI COBOL (also dotnet related), RataDie, Excel?
    ;

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final DRules dRules;
    private final DateTimeRules<LocalDateTime> dtRules;
    private final DateTimeRuleRange range;

    private JulianDayField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeRuleRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.dRules = new DRules(this);
        this.dtRules = new DateBasedDateTimeRules(this);
        this.range = range;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public DateTimeRules<LocalDate> getDateRules() {
        return dRules;
    }

    @Override
    public DateTimeRules<LocalDateTime> getDateTimeRules() {
        return dtRules;
    }

    @Override
    public DateTimeRuleRange getValueRange() {
        return range;
    }

    @Override
    public String toString() {
        return getName();
    }

    //-------------------------------------------------------------------------
    /**
     * Date rules for the field.
     */
    private static final class DRules implements DateTimeRules<LocalDate> {
        private final JulianDayField field;
        private DRules(JulianDayField field) {
            this.field = field;
        }
        @Override
        public DateTimeRuleRange range(LocalDate date) {
            return field.getValueRange();
        }
        @Override
        public long get(LocalDate date) {
            switch (field) {  // TODO: insert values
                case JULIAN_DAY: return date.toEpochDay() - 0;
                case MODIFIED_JULIAN_DAY: return date.toEpochDay() - 0;
                case TRUCTATED_JULIAN_DAY: return date.toEpochDay() - 0;
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalDate set(LocalDate date, long newValue) {
            if (range(date).isValidValue(newValue) == false) {
                throw new CalendricalException("Invalid value: " + field + " " + newValue);
            }
            switch (field) {
                case JULIAN_DAY: return LocalDate.ofEpochDay(newValue - 0);
                case MODIFIED_JULIAN_DAY: return LocalDate.ofEpochDay(newValue - 0);
                case TRUCTATED_JULIAN_DAY: return LocalDate.ofEpochDay(newValue - 0);
            }
            throw new CalendricalException("Unsupported field");
        }
        @Override
        public LocalDate setLenient(LocalDate date, long newValue) {
            return set(date, newValue);
        }
        @Override
        public LocalDate roll(LocalDate date, long roll) {
            return null;
        }
    }

}
