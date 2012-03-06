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

import javax.time.Duration;
import javax.time.LocalTime;
import javax.time.calendrical.DateTimeRuleRange;
import javax.time.calendrical.PeriodField;

/**
 * A calendar system that provides a way to query and manipulate time-of-day.
 * 
 * @author Stephen Colebourne
 */
public interface TimeChrono {

    String getName();

    DateTimeRuleRange getRange(DateTimeField field);

    DateTimeRuleRange getRange(DateTimeField field, LocalTime time);

    int getValue(DateTimeField field, LocalTime time);

    LocalTime setValue(DateTimeField field, LocalTime time, int newValue);

    LocalTime setValueLenient(DateTimeField field, LocalTime time, int newValue);

    LocalTime addValue(DateTimeField field, LocalTime time, int amount);

    LocalTime rollValue(DateTimeField field, LocalTime time, int roll);

    PeriodField getPeriodBetween(PeriodUnit unit, LocalTime time1, LocalTime time2);

    Duration getEstimatedDuration(PeriodUnit unit);

    Duration getDurationBetween(LocalTime time1, LocalTime time2);

//    LocalTime toChronology(DateChrono chrono, LocalTime time);

}
