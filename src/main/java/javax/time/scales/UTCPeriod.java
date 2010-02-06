/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.scales;


/**
 * Period of UTC history.
 * Within a period the relationship between UTC and TAI is linear (and since 1972
 * a constant delta).
 *
 * @author Mark Thornton
 */
public abstract class UTCPeriod {
    private static final int NANOS_PER_SECOND = 1000000000;
    private UTCPeriod next;
    private UTCPeriod previous;
    private TimeScaleInstant startTAI;
    private final long startEpochSeconds;

    UTCPeriod(int year, int month, int day) {
        super();
        startEpochSeconds = ScaleUtil.epochSeconds(year, month, day);
    }

    /**
     * Next period.
     *
     * @return next period or null if none
     */
    public UTCPeriod getNext() {
        return next;
    }

    /**
     * Previous period.
     * 
     * @return previous period or null if none
     */
    public UTCPeriod getPrevious() {
        return previous;
    }

    /**
     * Start of period in simple UTC epoch seconds.
     * 
     * @return seconds since 1970-01-01 without leap seconds
     */
    public long getStartEpochSeconds() {
        return startEpochSeconds;
    }

    /**
     * Start instant on TAI time scale.
     *
     * @return the start TAI
     */
    public TimeScaleInstant getStartTAI() {
        return startTAI;
    }

    void initialise(UTCPeriod previous) {
        if (previous != null) {
            this.previous = previous;
            previous.next = this;
        }
        long delta = getUTCDeltaNanoseconds(startEpochSeconds, 0);
        startTAI = TimeScaleInstant.seconds(TAI.INSTANCE, startEpochSeconds+delta/NANOS_PER_SECOND, (int)(delta%NANOS_PER_SECOND));
    }

    /**
     * TAI-UTC at UTC instant.
     *
     * @param utcEpochSeconds
     * @param nanoOfSecond
     * @return
     */
    public abstract long getUTCDeltaNanoseconds(long utcEpochSeconds, int nanoOfSecond);

    /**
     * TAI-UTC at TAI instant.
     *
     * @param taiEpochSeconds
     * @param nanoOfSecond
     * @return
     */
    public abstract long getTAIDeltaNanoseconds(long taiEpochSeconds, int nanoOfSecond);

}
