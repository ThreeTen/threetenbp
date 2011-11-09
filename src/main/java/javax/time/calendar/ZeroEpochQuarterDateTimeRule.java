/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static javax.time.calendar.ISODateTimeRule.EPOCH_MONTH;
import static javax.time.calendar.ISOPeriodUnit.QUARTERS;
import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.i18n.CopticChronology;
import javax.time.i18n.CopticDateTimeRule;

/**
 * The rules of date and time for zero-epoch-quarter.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ZeroEpochQuarterDateTimeRule extends DateTimeRule implements Serializable {
    // This is a test to ensure that the longrules mechanism works
    public static void main(String[] args) {
//        assertEquals(DAY_OF_MONTH.doValueRangeFromThis(12, DAY_OF_MONTH), DateTimeRuleRange.of(1, 28, 31));
//        assertEquals(EPOCH_DAY.doValueRangeFromThis(12, DAY_OF_MONTH), DateTimeRuleRange.of(1, 31));
//        assertEquals(EPOCH_DAY.doValueRangeFromThis(34, DAY_OF_MONTH), DateTimeRuleRange.of(1, 28));
//        assertEquals(EPOCH_DAY.doValueRangeFromThis(64, DAY_OF_MONTH), DateTimeRuleRange.of(1, 31));
//        assertEquals(EPOCH_DAY.doValueRangeFromThis(94, DAY_OF_MONTH), DateTimeRuleRange.of(1, 30));
//        assertEquals(INSTANCE.doValueRangeFromThis(0, DAY_OF_MONTH), DateTimeRuleRange.of(1, 28, 31));
//        assertEquals(INSTANCE.doValueRangeFromThis(1, DAY_OF_MONTH), DateTimeRuleRange.of(1, 30, 31));
//        assertEquals(INSTANCE.doValueRangeFromThis(2, DAY_OF_MONTH), DateTimeRuleRange.of(1, 30, 31));
//        assertEquals(INSTANCE.doValueRangeFromThis(3, DAY_OF_MONTH), DateTimeRuleRange.of(1, 30, 31));
//        assertEquals(INSTANCE.doValueRangeFromThis(4, DAY_OF_MONTH), DateTimeRuleRange.of(1, 28, 31));
        //assertEquals(INSTANCE.valueRange(12, DAY_OF_MONTH), DateTimeRuleRange.of(1, 29, 31));
        assertEquals(DAY_OF_MONTH.extractFrom(INSTANCE, 4 * 86400), Long.MIN_VALUE);
        assertEquals(MONTH_OF_QUARTER.extractFrom(INSTANCE, 4 * 86400), Long.MIN_VALUE);
        assertEquals(MONTH_OF_YEAR.extractFrom(INSTANCE, 4 * 86400), Long.MIN_VALUE);
        assertEquals(QUARTER_OF_YEAR.extractFrom(INSTANCE, 4 * 86400), 1);
        assertEquals(EPOCH_MONTH.extractFrom(INSTANCE, 1 * 86400), Long.MIN_VALUE);
        assertEquals(INSTANCE.extractFrom(EPOCH_MONTH, 4 * 86400), 0);
        assertEquals(EPOCH_DAY.extractFrom(CopticDateTimeRule.PACKED_YEAR_DAY, 4 * 86400), 4);
        assertEquals(DAY_OF_MONTH.extractFrom(CopticDateTimeRule.PACKED_YEAR_DAY, 4 * 86400), 5);
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extractFrom(EthiopicDateTimeRule.PACKED_YEAR_DAY, 4 * 86400), 4);
//        assertEquals(EthiopicDateTimeRule.PACKED_YEAR_DAY.extractFrom(CopticDateTimeRule.PACKED_YEAR_DAY, 4 * 86400), 4);
        System.out.println("OK");
    }

    /**
     * Singleton.
     */
    public static final DateTimeRule INSTANCE = new ZeroEpochQuarterDateTimeRule();;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Restricted constructor.
     */
    private ZeroEpochQuarterDateTimeRule() {
        super("ZeroEpochQuarter", QUARTERS, null,
                DateTimeRuleRange.of(Year.MIN_YEAR * 4L, Year.MAX_YEAR * 4L), MONTH_OF_YEAR);
    }

    /**
     * Deserialize singletons.
     * 
     * @return the resolved value, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    protected DateTimeRuleRange doValueRangeFromThis(long zeq, DateTimeRule requiredRule) {
        if (requiredRule.equals(DAY_OF_MONTH)) {
            long qoy = qoyFromZeq(zeq);
            if (qoy == 1) {
                long year = yFromZeq(zeq);
                return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year) ? 29 : 28, 31);
            } else {
                return DateTimeRuleRange.of(1, 30, 31);
            }
        }
        return super.doValueRangeFromThis(zeq, requiredRule);
    }

    //-----------------------------------------------------------------------
    @Override
    protected long doExtractFrom(DateTimeRule valueRule, long pdt) {
        long em = EPOCH_MONTH.extractFrom(valueRule, pdt);
        if (em != Long.MIN_VALUE) {
            return extractFromZem(em);
        }
        return Long.MIN_VALUE;
    }

    @Override
    protected boolean doIsExtractableFrom(DateTimeRule parentRule) {
        // if EpMonth can be extracted from target rule then this can be extracted
        return EPOCH_MONTH.isExtractableFrom(parentRule);
    }

    @Override
    protected boolean doIsExtractableTo(DateTimeRule childRule) {
        // if target rule can be extracted from QoY or Year then it can be extracted from this
        return childRule.isExtractableFrom(QUARTER_OF_YEAR) || childRule.isExtractableFrom(YEAR);
    }

    //-----------------------------------------------------------------------
    private static long yFromZeq(long zeq) {
        return MathUtils.floorDiv(zeq, 4);
    }

    private static int qoyFromZeq(long zeq) {
        return MathUtils.floorMod(zeq, 4) + 1;
    }

    private long extractFromZem(long zem) {
        return MathUtils.floorDiv(zem, 3);
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(DateTimeRule other) {
        if (other == INSTANCE) {
            return 0;
        }
        return super.compareTo(other);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == INSTANCE);
    }

    @Override
    public int hashCode() {
        return ZeroEpochQuarterDateTimeRule.class.hashCode();
    }

}
