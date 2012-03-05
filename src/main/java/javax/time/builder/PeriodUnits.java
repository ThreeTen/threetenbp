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
public enum PeriodUnits implements PeriodUnit {

    FOREVER(Duration.ofSeconds(Long.MAX_VALUE, 999999999), false),
    ERAS(Duration.ofSeconds(31556952L * 1000000000L), false),
    MILLENIA(Duration.ofSeconds(31556952L * 1000L), false),
    CENTURIES(Duration.ofSeconds(31556952L * 100L), false),
    DECADES(Duration.ofSeconds(31556952L * 10L), false),
    YEARS(Duration.ofSeconds(31556952L), false),
    MONTHS(Duration.ofSeconds(31556952L / 12), false),
    WEEKS(Duration.ofSeconds(7 * 86400L), true),
    DAYS(Duration.ofSeconds(86400), true),
    MERIDIEMS(Duration.ofSeconds(43200), true),
    HOURS(Duration.ofSeconds(3600), true),
    MINUTES(Duration.ofSeconds(60), true),
    SECONDS(Duration.ofSeconds(1), true);

    private final Duration estimatedDuration;
    private final boolean accurate;

    PeriodUnits(Duration estimatedDuration, boolean accurate) {
        this.estimatedDuration = estimatedDuration;
        this.accurate = accurate;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    public boolean isAccurate() {
        return accurate;
    }

}
