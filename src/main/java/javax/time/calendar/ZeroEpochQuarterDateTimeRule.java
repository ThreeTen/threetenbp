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
        
        assertEquals(INSTANCE.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4), 0);
        assertEquals(INSTANCE.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 + 32), 0);
        assertEquals(INSTANCE.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 + 64), 0);
        assertEquals(INSTANCE.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 + 96), 1);
        assertEquals(INSTANCE.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 - 32), -1);
        
        assertEquals(DAY_OF_MONTH.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4), 5);
        assertEquals(DAY_OF_MONTH.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 - 32), 5);
        
        assertEquals(MONTH_OF_YEAR.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4), 1);
        assertEquals(MONTH_OF_YEAR.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 + 32), 2);
        assertEquals(MONTH_OF_YEAR.extractFromValue(PACKED_EPOCH_MONTH_DAY, 4 - 32), 12);
        
        assertEquals(INSTANCE.extractFromValue(DAY_OF_MONTH, 4), Long.MIN_VALUE);
        assertEquals(INSTANCE.extractFromValue(MONTH_OF_QUARTER, 4), Long.MIN_VALUE);
        assertEquals(INSTANCE.extractFromValue(MONTH_OF_YEAR, 4), Long.MIN_VALUE);
        assertEquals(INSTANCE.extractFromValue(QUARTER_OF_YEAR, 4), Long.MIN_VALUE);
        
        assertEquals(INSTANCE.extractFromValue(ZERO_EPOCH_MONTH, 0), 0);
        assertEquals(INSTANCE.extractFromValue(ZERO_EPOCH_MONTH, 1), 0);
        assertEquals(INSTANCE.extractFromValue(ZERO_EPOCH_MONTH, 2), 0);
        assertEquals(INSTANCE.extractFromValue(ZERO_EPOCH_MONTH, 3), 1);
        assertEquals(INSTANCE.extractFromValue(ZERO_EPOCH_MONTH, 4), 1);
        
        assertEquals(DAY_OF_MONTH.extractFromValue(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(MONTH_OF_QUARTER.extractFromValue(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(MONTH_OF_YEAR.extractFromValue(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(ZERO_EPOCH_MONTH.extractFromValue(INSTANCE, 4), Long.MIN_VALUE);
        assertEquals(QUARTER_OF_YEAR.extractFromValue(INSTANCE, 3), 4);
        assertEquals(QUARTER_OF_YEAR.extractFromValue(INSTANCE, 4), 1);
        
        assertEquals(QUARTER_OF_YEAR.extractFromValue(MONTH_OF_YEAR, 4), 2);
        assertEquals(MONTH_OF_QUARTER.extractFromValue(MONTH_OF_YEAR, 4), 1);
        assertEquals(ALIGNED_WEEK_OF_MONTH.extractFromValue(DAY_OF_MONTH, 4), 1);
        assertEquals(ALIGNED_WEEK_OF_MONTH.extractFromValue(DAY_OF_MONTH, 8), 2);
        assertEquals(ALIGNED_WEEK_OF_YEAR.extractFromValue(DAY_OF_YEAR, 4), 1);
        assertEquals(ALIGNED_WEEK_OF_YEAR.extractFromValue(DAY_OF_YEAR, 8), 2);
        assertEquals(HOUR_OF_DAY.extractFromValue(SECOND_OF_DAY, 1), 0);
        assertEquals(HOUR_OF_DAY.extractFromValue(SECOND_OF_DAY, 3601), 1);
        assertEquals(DAY_OF_MONTH.extractFromValue(EPOCH_DAY, 2), 3);
        
        assertEquals(EthiopicDateTimeRule.MONTH_OF_YEAR.extractFromValue(EthiopicDateTimeRule.DAY_OF_YEAR, 34), 2);
        assertEquals(EthiopicDateTimeRule.DAY_OF_MONTH.extractFromValue(EthiopicDateTimeRule.DAY_OF_YEAR, 34), 4);
        
        // sets
        assertEquals(HOUR_OF_DAY.setIntoValue(2, SECOND_OF_DAY, 3601), 7201);
        assertEquals(QUARTER_OF_YEAR.setIntoValue(3, MONTH_OF_YEAR, 2), 8);
        assertEquals(MONTH_OF_YEAR.setIntoValue(3, PACKED_EPOCH_MONTH_DAY, 4 + 32), 4 + 64);
//        assertEquals(MONTH_OF_YEAR.setIntoValue(3, EPOCH_DAY, 4 + 31 - 1), 4 + 31 + 28 - 1);
        assertEquals(INSTANCE.setIntoValue(3, ZERO_EPOCH_MONTH, 5), 11);
        
        assertEquals(CopticDateTimeRule.MONTH_OF_YEAR.setIntoValue(3, CopticDateTimeRule.DAY_OF_YEAR, 34), 64);
        assertEquals(CopticDateTimeRule.MONTH_OF_YEAR.setIntoValue(13, CopticDateTimeRule.DAY_OF_YEAR, 34), 12 * 30 + 4);
        assertEquals(CopticDateTimeRule.DAY_OF_MONTH.setIntoValue(3, CopticDateTimeRule.DAY_OF_YEAR, 34), 33);
        assertEquals(CopticDateTimeRule.DAY_OF_MONTH.setIntoValue(13, CopticDateTimeRule.DAY_OF_YEAR, 12 * 30 + 4), 12 * 30 + 13);
        
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extract(4, EPOCH_DAY), -362);
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extract(4, DAY_OF_MONTH), 4);
        
//        assertEquals(CopticDateTimeRule.PACKED_YEAR_DAY.extract(4, EthiopicDateTimeRule.PACKED_YEAR_DAY), 4);
        
        assertEquals(DAY_OF_MONTH.setIntoValue(1, EPOCH_DAY, 0), 0);
        assertEquals(DAY_OF_MONTH.setIntoValue(2, EPOCH_DAY, 0), 1);
        assertEquals(DAY_OF_MONTH.setIntoValue(3, EPOCH_DAY, 0), 2);
        
        assertEquals(HOUR_OF_DAY.setIntoInstant(2, 123, 3601 * 1000000000L, 321)[0], 123);
        assertEquals(HOUR_OF_DAY.setIntoInstant(2, 123, 3601 * 1000000000L, 321)[1], 7201 * 1000000000L);
        assertEquals(HOUR_OF_DAY.setIntoInstant(2, 123, 3601 * 1000000000L, 321)[2], 321);
        
        assertEquals(DAY_OF_MONTH.setIntoInstant(2, 33, 3601 * 1000000000L, 321)[0], 32);
        assertEquals(DAY_OF_MONTH.setIntoInstant(2, 33, 3601 * 1000000000L, 321)[1], 3601 * 1000000000L);
        assertEquals(DAY_OF_MONTH.setIntoInstant(2, 33, 3601 * 1000000000L, 321)[2], 321);
        
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
    protected long doExtractFromInstant(long localEpochDay, long nanoOfDay, long offsetSecs) {
        return doExtractFromValue(EPOCH_DAY, localEpochDay);
    }

    @Override
    protected long doExtractFromValue(DateTimeRule fieldRule, long fieldValue) {
        long em = ZERO_EPOCH_MONTH.extractFromValue(fieldRule, fieldValue);
        if (em != Long.MIN_VALUE) {
            return eqFromEm(em);
        }
        return super.doExtractFromValue(fieldRule, fieldValue);
    }

    //-------------------------------------------------------------------------
    @Override
    protected long[] doSetIntoInstant(long newValue, long localEpochDay, long nanoOfDay, long offsetSecs) {
        localEpochDay = doSetIntoValue(newValue, EPOCH_DAY, localEpochDay);
        return new long[] {localEpochDay, nanoOfDay, offsetSecs};
    }

    @Override
    protected long doSetIntoValue(long newValue, DateTimeRule fieldRule, long fieldValue) {
        long em = ZERO_EPOCH_MONTH.extractFromValue(fieldRule, fieldValue);
        if (em != Long.MIN_VALUE) {
            long newEm = em + (newValue - eqFromEm(em)) * 3;
            return ZERO_EPOCH_MONTH.setIntoValue(newEm, fieldRule, fieldValue);
        }
        return super.doSetIntoValue(newValue, fieldRule, fieldValue);
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
