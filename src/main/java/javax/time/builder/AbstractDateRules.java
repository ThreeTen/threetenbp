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

import javax.time.LocalDateTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * Abstract rules that assist with creating date-only rules.
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractDateRules implements DateRules {

    @Override
    public DateTimeRuleRange range(DateTimeField field, LocalDateTime dateTime) {
        return range(field, dateTime != null ? dateTime.toLocalDate() : null);
    }

    @Override
    public long get(LocalDateTime dateTime, DateTimeField field) {
        return get(dateTime.toLocalDate(), field);
    }

    @Override
    public LocalDateTime set(LocalDateTime dateTime, DateTimeField field, long newValue) {
        return dateTime.with(set(dateTime.toLocalDate(), field, newValue));
    }

    @Override
    public LocalDateTime setLenient(LocalDateTime dateTime, DateTimeField field, long newValue) {
        return dateTime.with(setLenient(dateTime.toLocalDate(), field, newValue));
    }

    @Override
    public LocalDateTime roll(LocalDateTime dateTime, DateTimeField field, long roll) {
        return dateTime.with(roll(dateTime.toLocalDate(), field, roll));
    }

}
