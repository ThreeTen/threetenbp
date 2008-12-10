/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static javax.time.period.PeriodUnits.*;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.field.Year;

/**
 * Mock rule.
 *
 * @author Stephen Colebourne
 */
public final class MockCenturyFieldRule extends DateTimeFieldRule implements Serializable {
    /** Singleton instance. */
    public static final DateTimeFieldRule INSTANCE = new MockCenturyFieldRule();
    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;
    /** Constructor. */
    private MockCenturyFieldRule() {
        super(ISOChronology.INSTANCE, "Century", CENTURIES, null, Year.MIN_YEAR / 100, Year.MAX_YEAR / 100);
    }
    private Object readResolve() {
        return INSTANCE;
    }
    /** {@inheritDoc} */
    @Override
    public Integer getValueQuiet(LocalDate date, LocalTime time) {
        return date == null ? null : date.getYear().getValue() / 100;
    }
    /** {@inheritDoc} */
    @Override
    protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
        Integer yearVal = ISOChronology.yearRule().getValueQuiet(fieldMap);
        return (yearVal == null ? null : yearVal / 100);
    }
    /** {@inheritDoc} */
    @Override
    protected void mergeFields(Calendrical.Merger merger) {
        Integer yocVal = merger.getValue(MockYearOfCenturyFieldRule.INSTANCE);
        if (yocVal != null) {
            int cen = merger.getValueInt(this);
            int year = MathUtils.safeAdd(MathUtils.safeMultiply(cen, 100), yocVal);
            merger.storeMergedField(ISOChronology.yearRule(), year);
            merger.markFieldAsProcessed(this);
            merger.markFieldAsProcessed(MockYearOfCenturyFieldRule.INSTANCE);
        }
    }
}
