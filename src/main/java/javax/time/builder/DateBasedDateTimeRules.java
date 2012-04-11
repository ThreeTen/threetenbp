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

import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.MathUtils;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * Implementation of {@code DateTimeRules} that delegates to a date-based set of rules.
 */
public final class DateBasedDateTimeRules implements DateTimeRules<LocalDateTime> {
    // consider converting to a defaulted interface method
    // DateTimeField.getDateTimeRules()
    // (would involve creating object every method call)
    // or consider a utils class with static method so this can be private

    /**
     * The date rules to delegate to.
     */
    private final DateTimeRules<LocalDate> rules;

    /**
     * Creates an instance of the rules that wraps an underlying date rules.
     * 
     * @param field  the date field, not null
     */
    public DateBasedDateTimeRules(DateTimeRules<LocalDate> rules) {
        MathUtils.checkNotNull(rules, "DateTimeRules must not be null");
        this.rules = rules;
    }

    @Override
    public DateTimeRuleRange range(LocalDateTime dateTime) {
        return rules.range(dateTime.toLocalDate());
    }
    @Override
    public long get(LocalDateTime dateTime) {
        return rules.get(dateTime.toLocalDate());
    }
    @Override
    public LocalDateTime set(LocalDateTime dateTime, long newValue) {
        return dateTime.with(rules.set(dateTime.toLocalDate(), newValue));
    }
    @Override
    public LocalDateTime setLenient(LocalDateTime dateTime, long newValue) {
        return dateTime.with(rules.setLenient(dateTime.toLocalDate(), newValue));
    }
    @Override
    public LocalDateTime roll(LocalDateTime dateTime, long roll) {
        return dateTime.with(rules.roll(dateTime.toLocalDate(), roll));
    }

}
