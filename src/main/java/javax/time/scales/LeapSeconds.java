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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.time.CalendricalException;

/** Table of known Leap Seconds
 * This is the history of TAI-UTC since 1972-01-01.
 * @see <a href="http://hpiers.obspm.fr/eoppc/bul/bulc/UTC-TAI.history">UTC-TAI History</a>
 * @author Mark Thornton
 */
public class LeapSeconds {
    /** Start of use of leap seconds. */
    public static final long START_EPOCH_SECONDS = ScaleUtil.epochSeconds(1972, 1, 1);
    private static UTC_TAI<Entry> list;
    private static long nextPossibleLeap;

    public static class Entry extends UTCPeriod {
        private int deltaSeconds;
        private long deltaNanoseconds;

        Entry(int year, int month, int day) {
            super(year, month, day);
        }

        @Override
        public Entry getNext() {
            return (Entry)super.getNext();
        }

        @Override
        public Entry getPrevious() {
            return (Entry)super.getPrevious();
        }

        public int getDeltaSeconds() {
            return deltaSeconds;
        }

        @Override
        void initialise(UTCPeriod previous) {
            deltaSeconds = previous == null ? 10 : ((Entry)previous).deltaSeconds+1;
            deltaNanoseconds = deltaSeconds * (long)ScaleUtil.NANOS_PER_SECOND;
            super.initialise(previous);
        }

        @Override
        public long getUTCDeltaNanoseconds(long utcEpochSeconds, int nanoOfSecond) {
            return deltaNanoseconds;
        }

        @Override
        public long getTAIDeltaNanoseconds(long taiEpochSeconds, int nanoOfSecond) {
            return deltaNanoseconds;
        }
    }

    /** List of LeapSecond.Entry's.
     * There is an entry for each different value of TAI-UTC. All
     * but the first entry are immediately preceded by a leap second.
     * @return list of entries
     */
    public static synchronized UTC_TAI<Entry> list() {
        if (list == null) {
            loadLeapSeconds();
        }
        return list;
    }

    /** epochSeconds of next possible leap second.
     * Decisions on leap seconds on or after this instant have yet to be made. Usually the presence
     * or absence of a leap second is announced about six months in advance. Current practice only inserts
     * leap seconds prior to 1 January or 1 July.
     * @return simple UTC epochSeconds
     * @see <a href="http://hpiers.obspm.fr/eoppc/bul/bulc/bulletinc.dat">IERS - Bulletin C</a>
     */
    public static synchronized long getNextPossibleLeap() {
        if (nextPossibleLeap == 0) {
            loadLeapSeconds();
        }
        return nextPossibleLeap;
    }

    // Check currency of leap year data and reload if necessary.
    // This may be useful for long running processes
    // public void refreshCache() {...}

    private static void loadLeapSeconds() {
        // It would be better for LeapSeconds.txt to be a file within the JRE lib folder to enable
        // simple updating.
        InputStream in = LeapSeconds.class.getResourceAsStream("LeapSeconds.txt");
        if (in == null) {
            throw new CalendricalException("LeapSeconds.txt resource missing");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            ArrayList<Entry> entries = new ArrayList<Entry>();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    int index = 0;
                    boolean possible = false;
                    if (line.charAt(0) == '?') {
                        possible = true;
                        index=1;
                    }
                    // parse a simple date from index
                    int x = line.indexOf('-', index);
                    int year = Integer.parseInt(line.substring(index, x));
                    index = x+1;
                    x = line.indexOf('-', index);
                    int month = Integer.parseInt(line.substring(index, x));
                    index = x+1;
                    x = line.indexOf(' ');
                    int day = Integer.parseInt(x < 0 ? line.substring(index) : line.substring(index, x));
                    if (possible)
                        nextPossibleLeap = ScaleUtil.epochSeconds(year, month, day);
                    else
                        entries.add(new Entry(year, month, day));
                }
            }
            Entry[] entryArray = new Entry[entries.size()];
            entries.toArray(entryArray);
            LeapSeconds.list = new UTC_TAI<Entry>(entryArray);
            in.close();
        }
        catch (IOException e) {
            throw new CalendricalException("Exception reading LeapSeconds.txt", e);
        }
    }
}
