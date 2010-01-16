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

import java.util.AbstractList;
import javax.time.TimeScaleInstant;

/**
 *
 * @author Mark Thornton
 */
public class UTC_TAI<P extends UTCPeriod> extends AbstractList<P>{
    private P[] entries;
    
    UTC_TAI(P[] entries)
    {
        this.entries = entries;
        P p = null;
        for (int i=0; i<entries.length; i++) {
            P e = entries[i];
            e.initialise(p);
            p = e;
        }
    }

    public P entryFromUTC(long utcEpochSeconds) {
        // binary search for required entry
        int left = 0;
        int right = entries.length;
        // entries[-1].startEpochSeconds equiv to -infinite
        // entries[entries.length] equivalent to +infinite
        // invariant:
        // entries[left-1].startEpochSeconds <= utcEpochSeconds
        // entries[right] > utcEpochSeconds
        while (left < right) {
            int m = left +((right-left)>>1);
            if (utcEpochSeconds < entries[m].getStartEpochSeconds())
                right = m;
            else
                left = m+1;
        }
        if (right == 0)
            throw new IllegalArgumentException("Value below lower bound");
        return entries[right-1];
    }

    public P entryFromTAI(TimeScaleInstant t) {
        if (t.getTimeScale() != TAI.INSTANCE) {
            throw new IllegalArgumentException("Require TAI instant");
        }
        int left = 0;
        int right = entries.length;
        // entries[-1].startEpochSeconds equiv to -infinite
        // entries[entries.length] equivalent to +infinite
        // invariant:
        // entries[left-1].startEpochSeconds <= utcEpochSeconds
        // entries[right] > utcEpochSeconds
        while (left < right) {
            int m = left +((right-left)>>1);
            if (t.compareTo(entries[m].getStartTAI()) < 0)
                right = m;
            else
                left = m+1;
        }
        if (right == 0)
            throw new IllegalArgumentException("Value below lower bound");
        return entries[right-1];
    }

    public P entryFromTAI(long epochSeconds, int nanoOfSecond) {
        int left = 0;
        int right = entries.length;
        // entries[-1].startEpochSeconds equiv to -infinite
        // entries[entries.length] equivalent to +infinite
        // invariant:
        // entries[left-1].startEpochSeconds <= utcEpochSeconds
        // entries[right] > utcEpochSeconds
        while (left < right) {
            int m = left +((right-left)>>1);
            TimeScaleInstant ts = entries[m].getStartTAI();
            int z;
            if (epochSeconds < ts.getEpochSeconds())
                z = -1;
            else if (epochSeconds > ts.getEpochSeconds())
                z = 1;
            else
                z = nanoOfSecond - ts.getNanoOfSecond();
            if (z < 0)
                right = m;
            else
                left = m+1;
        }
        if (right == 0)
            throw new IllegalArgumentException("Value below lower bound");
        return entries[right-1];
    }

    @Override
    public P get(int index) {
        return entries[index];
    }

    @Override
    public int size() {
        return entries.length;
    }
}
