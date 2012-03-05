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

/**
 * A standard set of time units.
 * <p>
 * These units are not tied to any specific calendar system
 * 
 * @author Stephen Colebourne
 */
public enum StandardPeriodUnit implements PeriodUnit {

    FOREVER("forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999), false),
    ERAS("eras", Duration.ofSeconds(31556952L * 1000000000L), false),
    MILLENIA("millenia", Duration.ofSeconds(31556952L * 1000L), false),
    CENTURIES("centuries", Duration.ofSeconds(31556952L * 100L), false),
    DECADES("decades", Duration.ofSeconds(31556952L * 10L), false),
    YEARS("years", Duration.ofSeconds(31556952L), false),
    MONTHS("months", Duration.ofSeconds(31556952L / 12), false),
    WEEKS("weeks", Duration.ofSeconds(7 * 86400L), true),
    DAYS("days", Duration.ofSeconds(86400), true),
    MERIDIEMS("meridiems", Duration.ofSeconds(43200), true),
    HOURS("hours", Duration.ofSeconds(3600), true),
    MINUTES("minutes", Duration.ofSeconds(60), true),
    SECONDS("seconds", Duration.ofSeconds(1), true);

    private final String name;
    private final Duration estimatedDuration;
    private final boolean accurate;

    private StandardPeriodUnit(String name, Duration estimatedDuration, boolean accurate) {
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        this.accurate = accurate;
    }

    @Override
    public String getName() {
        return name;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    public boolean isAccurate() {
        return accurate;
    }

}
