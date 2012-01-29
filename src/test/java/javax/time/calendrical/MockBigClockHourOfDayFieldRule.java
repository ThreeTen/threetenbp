/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import static javax.time.calendrical.ISODateTimeRule.CLOCK_HOUR_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.HOUR_OF_DAY;

import java.io.Serializable;

/**
 * Mock rule, with parent of ClockHourOfDay.
 *
 * @author Stephen Colebourne
 */
public final class MockBigClockHourOfDayFieldRule extends DateTimeRule implements Serializable {

    /** Singleton instance. */
    public static final DateTimeRule INSTANCE = new MockBigClockHourOfDayFieldRule();
    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    private MockBigClockHourOfDayFieldRule() {
        super("BigClockHourOfDay", ISOPeriodUnit.HOURS, ISOPeriodUnit.DAYS,
                DateTimeRuleRange.of(0, 2300), CLOCK_HOUR_OF_DAY);
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    protected DateTimeField deriveFrom(CalendricalEngine engine) {
        DateTimeField hod = engine.derive(HOUR_OF_DAY);
        if (hod == null) {
            return null;
        }
        return field(convertFromPeriod(hod.getValue()));
    }

    @Override
    public long convertToPeriod(long value) {
        return value / 100;
    }

    @Override
    public long convertFromPeriod(long period) {
        return period * 100;
    }

}
