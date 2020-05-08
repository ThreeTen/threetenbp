/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.threeten.bp.zone;

import org.threeten.bp.ZoneOffset;

import java.io.DataInput;
import java.io.IOException;

/**
 * Holds numerous methods pulled from {@link Ser} which will be completely ignored and unavailable in JS.
 */
public abstract class Ser2 {

    /** Type for StandardZoneRules. */
    static final byte SZR = 1;
    /** Type for ZoneOffsetTransition. */
    static final byte ZOT = 2;
    /** Type for ZoneOffsetTransition. */
    static final byte ZOTRULE = 3;

    Ser2() {
        super();
    }

    public static Object read(DataInput in) throws IOException {
        byte type = in.readByte();
        return readInternal(type, in);
    }

    // pulled from Ser which is @GwtIncompatible
    public static Object readInternal(byte type, DataInput in) throws IOException {
        switch (type) {
            case SZR:
                return StandardZoneRules.readExternal(in);
            case ZOT:
                return ZoneOffsetTransition.readExternal(in);
            case ZOTRULE:
                return ZoneOffsetTransitionRule.readExternal(in);
            default:
                throw new IOException("Unknown serialized type");
        }
    }

    /**
     * Reads the state from the stream.
     *
     * @param in  the input stream, not null
     * @return the epoch seconds, not null
     * @throws IOException if an error occurs
     */
    static long readEpochSec(DataInput in) throws IOException {
        int hiByte = in.readByte() & 255;
        if (hiByte == 255) {
            return in.readLong();
        } else {
            int midByte = in.readByte() & 255;
            int loByte = in.readByte() & 255;
            long tot = ((hiByte << 16) + (midByte << 8) + loByte);
            return (tot * 900) - 4575744000L;
        }
    }

    /**
     * Reads the state from the stream.
     *
     * @param in  the input stream, not null
     * @return the created object, not null
     * @throws IOException if an error occurs
     */
    static ZoneOffset readOffset(DataInput in) throws IOException {
        int offsetByte = in.readByte();
        return (offsetByte == 127 ? ZoneOffset.ofTotalSeconds(in.readInt()) : ZoneOffset.ofTotalSeconds(offsetByte * 900));
    }
}
