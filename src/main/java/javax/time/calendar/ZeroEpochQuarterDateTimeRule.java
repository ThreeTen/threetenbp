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

import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.PACKED_EPOCH_MONTH_DAY;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.ZERO_EPOCH_MONTH;
import static javax.time.calendar.ISOPeriodUnit.QUARTERS;
import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.i18n.CopticDateTimeRule;
import javax.time.i18n.EthiopicDateTimeRule;

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
        
        assertEquals(INSTANCE.calculateGet(PACKED_EPOCH_MONTH_DAY, 4), 0);
        assertEquals(INSTANCE.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 + 32), 0);
        assertEquals(INSTANCE.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 + 64), 0);
        assertEquals(INSTANCE.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 + 96), 1);
        assertEquals(INSTANCE.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 - 32), -1);
        
        assertEquals(DAY_OF_MONTH.calculateGet(PACKED_EPOCH_MONTH_DAY, 4), 5);
        assertEquals(DAY_OF_MONTH.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 - 32), 5);
        
        assertEquals(MONTH_OF_YEAR.calculateGet(PACKED_EPOCH_MONTH_DAY, 4), 1);
        assertEquals(MONTH_OF_YEAR.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 + 32), 2);
        assertEquals(MONTH_OF_YEAR.calculateGet(PACKED_EPOCH_MONTH_DAY, 4 - 32), 12);
        
        assertEquals(INSTANCE.calculateGet(DAY_OF_MONTH, 4), Long.MIN_VALUE);
        assertEquals(INSTANCE.calculateGet(MONTH_OF_QUARTER, 4), Long.MIN_VALUE);
        assertEquals(INSTANCE.calculateGet(MONTH_OF_YEAR, 4), Long.MIN_VALUE);
        assertEquals(INSTANCE.calculateGet(QUARTER_OF_YEAR, 4), Long.MIN_VALUE);
        
        assertEquals(INSTANCE.calculateGet(ZERO_EPOCH_MONTH, 0), 0);
        assertEquals(INSTANCE.calculateGet(ZERO_EPOCH_MONTH, 1), 0);
        assertEquals(INSTANCE.calculateGet(ZERO_EPOCH_MONTH, 2), 0);
        assertEquals(INSTANCE.calculateGet(ZERO_EPOCH_MONTH, 3), 1);
        assertEquals(INSTANCE.calculateGet(ZERO_EPOCH_MONTH, 4), 1);
        
        assertEquals(DAY_OF_MONTH.calculateGet(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(MONTH_OF_QUARTER.calculateGet(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(MONTH_OF_YEAR.calculateGet(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(ZERO_EPOCH_MONTH.calculateGet(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(QUARTER_OF_YEAR.calculateGet(INSTANCE, 3), 4);
        assertEquals(QUARTER_OF_YEAR.calculateGet(INSTANCE, 4), 1);
        
        assertEquals(QUARTER_OF_YEAR.calculateGet(MONTH_OF_YEAR, 4), 2);
        assertEquals(MONTH_OF_QUARTER.calculateGet(MONTH_OF_YEAR, 4), 1);
        assertEquals(ALIGNED_WEEK_OF_MONTH.calculateGet(DAY_OF_MONTH, 4), 1);
        assertEquals(ALIGNED_WEEK_OF_MONTH.calculateGet(DAY_OF_MONTH, 8), 2);
        assertEquals(ALIGNED_WEEK_OF_YEAR.calculateGet(DAY_OF_YEAR, 4), 1);
        assertEquals(ALIGNED_WEEK_OF_YEAR.calculateGet(DAY_OF_YEAR, 8), 2);
        assertEquals(HOUR_OF_DAY.calculateGet(SECOND_OF_DAY, 1), 0);
        assertEquals(HOUR_OF_DAY.calculateGet(SECOND_OF_DAY, 3601), 1);
        assertEquals(DAY_OF_MONTH.calculateGet(EPOCH_DAY, 2), 3);
        
        assertEquals(EthiopicDateTimeRule.MONTH_OF_YEAR.calculateGet(EthiopicDateTimeRule.DAY_OF_YEAR, 34), 2);
        assertEquals(EthiopicDateTimeRule.DAY_OF_MONTH.calculateGet(EthiopicDateTimeRule.DAY_OF_YEAR, 34), 4);
        
        // sets
        DateTimeResolver resolver = null;
        assertEquals(HOUR_OF_DAY.calculateSet(SECOND_OF_DAY, 3601, 2, resolver), 7201);
        assertEquals(QUARTER_OF_YEAR.calculateSet(MONTH_OF_YEAR, 2, 3, resolver), 8);
        assertEquals(MONTH_OF_YEAR.calculateSet(PACKED_EPOCH_MONTH_DAY, 4 + 32, 3, resolver), 4 + 64);
//        assertEquals(MONTH_OF_YEAR.setIntoValue(3, EPOCH_DAY, 4 + 31 - 1), 4 + 31 + 28 - 1);
        assertEquals(INSTANCE.calculateSet(ZERO_EPOCH_MONTH, 5, 3, resolver), 11);
        
        assertEquals(CopticDateTimeRule.MONTH_OF_YEAR.calculateSet(CopticDateTimeRule.DAY_OF_YEAR, 34, 3, resolver), 64);
        assertEquals(CopticDateTimeRule.MONTH_OF_YEAR.calculateSet(CopticDateTimeRule.DAY_OF_YEAR, 34, 13, resolver), 12 * 30 + 4);
        assertEquals(CopticDateTimeRule.DAY_OF_MONTH.calculateSet(CopticDateTimeRule.DAY_OF_YEAR, 34, 3, resolver), 33);
        assertEquals(CopticDateTimeRule.DAY_OF_MONTH.calculateSet(CopticDateTimeRule.DAY_OF_YEAR, 12 * 30 + 4, 13, resolver), 12 * 30 + 13);
        
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extract(4, EPOCH_DAY), -362);
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extract(4, DAY_OF_MONTH), 4);
        
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extract(4, EthiopicDateTimeRule.PACKED_YEAR_DAY), 4);
        
        assertEquals(DAY_OF_MONTH.calculateSet(EPOCH_DAY, 0, 1, resolver), 0);
        assertEquals(DAY_OF_MONTH.calculateSet(EPOCH_DAY, 0, 2, resolver), 1);
        assertEquals(DAY_OF_MONTH.calculateSet(EPOCH_DAY, 0, 3, resolver), 2);
        
        assertEquals(HOUR_OF_DAY.calculateSetComplete(123, 3601 * 1000000000L, 321, 2, resolver)[0], 123);
        assertEquals(HOUR_OF_DAY.calculateSetComplete(123, 3601 * 1000000000L, 321, 2, resolver)[1], 7201 * 1000000000L);
        assertEquals(HOUR_OF_DAY.calculateSetComplete(123, 3601 * 1000000000L, 321, 2, resolver)[2], 321);
        
        assertEquals(DAY_OF_MONTH.calculateSetComplete(33, 3601 * 1000000000L, 321, 2, resolver)[0], 32);
        assertEquals(DAY_OF_MONTH.calculateSetComplete(33, 3601 * 1000000000L, 321, 2, resolver)[1], 3601 * 1000000000L);
        assertEquals(DAY_OF_MONTH.calculateSetComplete(33, 3601 * 1000000000L, 321, 2, resolver)[2], 321);
        
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
                DateTimeRuleRange.of(Year.MIN_YEAR * 4L, Year.MAX_YEAR * 4L), ZERO_EPOCH_MONTH);
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
//    @Override
//    protected DateTimeRuleRange doValueRangeFromThis(long zeq, DateTimeRule requiredRule) {
//        if (requiredRule.equals(DAY_OF_MONTH)) {
//            long qoy = qoyFromZeq(zeq);
//            if (qoy == 1) {
//                long year = yFromZeq(zeq);
//                return DateTimeRuleRange.of(1, CopticChronology.isLeapYear(year) ? 29 : 28, 31);
//            } else {
//                return DateTimeRuleRange.of(1, 30, 31);
//            }
//        }
//        return super.doValueRangeFromThis(zeq, requiredRule);
//    }
//
//    private static long yFromZeq(long zeq) {
//        return MathUtils.floorDiv(zeq, 4);
//    }
//
//    private static int qoyFromZeq(long zeq) {
//        return MathUtils.floorMod(zeq, 4) + 1;
//    }

    //-------------------------------------------------------------------------
    @Override
    protected long doCalculateGetComplete(long localEpochDay, long nanoOfDay, long offsetSecs) {
        return doCalculateGet(EPOCH_DAY, localEpochDay);
    }

    @Override
    protected long doCalculateGet(DateTimeRule fieldRule, long fieldValue) {
        long em = ZERO_EPOCH_MONTH.calculateGet(fieldRule, fieldValue);
        if (em != Long.MIN_VALUE) {
            return eqFromEm(em);
        }
        return super.doCalculateGet(fieldRule, fieldValue);
    }

    //-------------------------------------------------------------------------
    @Override
    protected long[] doCalculateSetComplete(long localEpochDay, long nanoOfDay, long offsetSecs, long newValue, DateTimeResolver resolver) {
        localEpochDay = doCalculateSet(EPOCH_DAY, localEpochDay, newValue, resolver);
        return new long[] {localEpochDay, nanoOfDay, offsetSecs};
    }

    @Override
    protected long doCalculateSet(DateTimeRule fieldRule, long fieldValue, long newValue, DateTimeResolver resolver) {
        long em = ZERO_EPOCH_MONTH.calculateGet(fieldRule, fieldValue);
        if (em != Long.MIN_VALUE) {
            long newEm = em + (newValue - eqFromEm(em)) * 3;
            return ZERO_EPOCH_MONTH.calculateSet(fieldRule, fieldValue, newEm, resolver);
        }
        return super.doCalculateSet(fieldRule, fieldValue, newValue, resolver);
    }

    private static long eqFromEm(long em) {
        return MathUtils.floorDiv(em, 3);
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
