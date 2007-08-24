/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

/**
 * The ISO calculation engine.
 * 
 * @author Stephen Colebourne
 */
public class ISOChronology {

    private static final ISOChronology INSTANCE = new ISOChronology();

    private static final long MAX_EPOCH_DAYS = Integer.MAX_VALUE * 365;
    private static final long MIN_EPOCH_DAYS = Integer.MIN_VALUE * 365;
    private static final int MAX_SECOND_OF_DAY = 60 * 60 * 24;

    /**
     * Restricted constructor.
     */
    private ISOChronology() {
        super();
    }

    /**
     * Singleton instance.
     */
    public static ISOChronology instance() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    public int toSecondOfDay(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        validateMinBound(hourOfDay, 0, "hourOfDay");
        validateMaxBound(hourOfDay, 23, "hourOfDay");
        validateMinBound(minuteOfHour, 0, "minuteOfHour");
        validateMaxBound(minuteOfHour, 59, "minuteOfHour");
        validateMinBound(secondOfMinute, 0, "secondOfMinute");
        validateMaxBound(secondOfMinute, 59, "secondOfMinute");
        return (hourOfDay * 60 + minuteOfHour) * 60 + secondOfMinute;
    }

    public int toSecondOfDayPlusWrapped(int hourOfDay, int hours, int minuteOfHour, int minutes, int secondOfMinute, int seconds) {
        validateMinBound(hourOfDay, 0, "hourOfDay");
        validateMaxBound(hourOfDay, 23, "hourOfDay");
        validateMinBound(minuteOfHour, 0, "minuteOfHour");
        validateMaxBound(minuteOfHour, 59, "minuteOfHour");
        validateMinBound(secondOfMinute, 0, "secondOfMinute");
        validateMaxBound(secondOfMinute, 59, "secondOfMinute");
        // TODO this algoritm needs more work
        hourOfDay = MathUtils.safeAdd(hourOfDay, hours % 24);
        minuteOfHour = MathUtils.safeAdd(minuteOfHour, minutes % 60);
        secondOfMinute = MathUtils.safeAdd(secondOfMinute, seconds % 60);
        return (hourOfDay * 60 + minuteOfHour) * 60 + secondOfMinute;
    }

    public int secondOfDayToHourOfDay(int secondOfDay) {
        return secondOfDay / (60 * 60);
    }

    public int secondOfDayToMinuteOfHour(int secondOfDay) {
        return (secondOfDay / 60) % 24;
    }

    public int secondOfDayToSecondOfMinute(int secondOfDay) {
        return secondOfDay % 60;
    }

    //-----------------------------------------------------------------------
    private void validateMinBound(int value, int minValid, String fieldName) {
        if (value < minValid) {
            throw new IllegalArgumentException("Field " + fieldName + " must not be less than " + minValid + " but was " + value);
        }
    }

    private void validateMaxBound(int value, int maxValid, String fieldName) {
        if (value > maxValid) {
            throw new IllegalArgumentException("Field " + fieldName + " must not be more than " + maxValid + " but was " + value);
        }
    }

//    //-----------------------------------------------------------------------
//    public void checkBounds(ISOState state) {
//        if (state.epochDays > MAX_EPOCH_DAYS) {
//            throw new IllegalArgumentException("Date too far in future: " + state.epochDays + " epoch days");
//        }
//        if (state.epochDays < MIN_EPOCH_DAYS) {
//            throw new IllegalArgumentException("Date too far in past: " + state.epochDays + " epoch days");
//        }
//        if (state.secondOfDay > MAX_SECOND_OF_DAY) {
//            throw new IllegalArgumentException("Time too large: " + state.secondOfDay + " seconds");
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    public boolean isLeapYear(ISOState state) {
//        int year = getYear(state);
//        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
//    }
//
//    //-----------------------------------------------------------------------
//    public int getYear(ISOState state) {
//        int year = state.year;
//        if (year == 0) {
//            long epochDays = state.epochDays;
//            long yearEst = epochDays * 4 / (365 + 365 + 365 + 366);
//            year = (int) yearEst;
//            state.year = year;
//        }
//        return year;
//    }
//
//    public int getMonthOfYear(ISOState state) {
//        int month = state.monthOfYear;
//        if (month == 0) {
//            
//        }
//        return month;
//    }
//
//    public int getDayOfMonth(ISOState state) {
//        int day = state.dayOfMonth;
//        if (day == 0) {
//            
//        }
//        return day;
//    }
//
////    //-----------------------------------------------------------------------
////    public ISOState setYear(ISOState state, int year) {
////        if (state.year == year) {
////            return state;
////        }
////        return state;
////    }
//
//    //-----------------------------------------------------------------------
//    public ISOState addYears(ISOState state, int years) {
//        return state;
//    }
//
//    public ISOState addMonths(ISOState state, int months) {
//        return state;
//    }
//
//    public ISOState addDays(ISOState state, int days) {
//        
//        return state;
//    }

}
