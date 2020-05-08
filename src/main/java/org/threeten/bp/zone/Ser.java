/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.zone;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

import javaemul.internal.annotations.GwtIncompatible;
import org.threeten.bp.ZoneOffset;

/**
 * The shared serialization delegate for this package.
 *
 * <h4>Implementation notes</h4>
 * This class is mutable and should be created once per serialization.
 *
 * @serial include
 */
@GwtIncompatible
final class Ser implements Externalizable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -8885321777449118786L;

//    /** Type for StandardZoneRules. */
//    static final byte SZR = 1;
    static final byte SZR = Ser2.SZR;

//    /** Type for ZoneOffsetTransition. */
//    static final byte ZOT = 2;
    static final byte ZOT = Ser2.ZOT;

//    /** Type for ZoneOffsetTransition. */
//    static final byte ZOTRULE = 3;
    static final byte ZOTRULE = Ser2.ZOTRULE;

    /** The type being serialized. */
    private byte type;
    /** The object being serialized. */
    private Object object;

    /**
     * Constructor for deserialization.
     */
    public Ser() {
    }

    /**
     * Creates an instance for serialization.
     *
     * @param type  the type
     * @param object  the object
     */
    Ser(byte type, Object object) {
        this.type = type;
        this.object = object;
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the {@code Externalizable} interface to write the object.
     *
     * @param out  the data stream to write to, not null
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        writeInternal(type, object, out);
    }

    static void write(Object object, DataOutput out) throws IOException {
        writeInternal(SZR, object, out);
    }

    private static void writeInternal(byte type, Object object, DataOutput out) throws IOException {
        out.writeByte(type);
        switch (type) {
            case SZR:
                ((StandardZoneRules) object).writeExternal(out);
                break;
            case ZOT:
                ((ZoneOffsetTransition) object).writeExternal(out);
                break;
            case ZOTRULE:
                ((ZoneOffsetTransitionRule) object).writeExternal(out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the {@code Externalizable} interface to read the object.
     *
     * @param in  the data to read, not null
     */
    public void readExternal(ObjectInput in) throws IOException {
        type = in.readByte();
        object = Ser2.readInternal(type, in);
    }

//    static Object read(DataInput in) throws IOException {
//        byte type = in.readByte();
//        return readInternal(type, in);
//    }
//
//    private static Object readInternal(byte type, DataInput in) throws IOException {
//        switch (type) {
//            case SZR:
//                return StandardZoneRules.readExternal(in);
//            case ZOT:
//                return ZoneOffsetTransition.readExternal(in);
//            case ZOTRULE:
//                return ZoneOffsetTransitionRule.readExternal(in);
//            default:
//                throw new StreamCorruptedException("Unknown serialized type");
//        }
//    }
//

    /**
     * Returns the object that will replace this one.
     *
     * @return the read object, should never be null
     */
    private Object readResolve() {
         return object;
    }

    //-----------------------------------------------------------------------
    /**
     * Writes the state to the stream.
     *
     * @param offset  the offset, not null
     * @param out  the output stream, not null
     * @throws IOException if an error occurs
     */
    static void writeOffset(ZoneOffset offset, DataOutput out) throws IOException {
        final int offsetSecs = offset.getTotalSeconds();
        int offsetByte = offsetSecs % 900 == 0 ? offsetSecs / 900 : 127;  // compress to -72 to +72
        out.writeByte(offsetByte);
        if (offsetByte == 127) {
            out.writeInt(offsetSecs);
        }
    }

// moved to Ser2
//
//    /**
//     * Reads the state from the stream.
//     *
//     * @param in  the input stream, not null
//     * @return the created object, not null
//     * @throws IOException if an error occurs
//     */
//    static ZoneOffset readOffset(DataInput in) throws IOException {
//        int offsetByte = in.readByte();
//        return (offsetByte == 127 ? ZoneOffset.ofTotalSeconds(in.readInt()) : ZoneOffset.ofTotalSeconds(offsetByte * 900));
//    }

    //-----------------------------------------------------------------------
    /**
     * Writes the state to the stream.
     *
     * @param epochSec  the epoch seconds, not null
     * @param out  the output stream, not null
     * @throws IOException if an error occurs
     */
    static void writeEpochSec(long epochSec, DataOutput out) throws IOException {
        if (epochSec >= -4575744000L && epochSec < 10413792000L && epochSec % 900 == 0) {  // quarter hours between 1825 and 2300
            int store = (int) ((epochSec + 4575744000L) / 900);
            out.writeByte((store >>> 16) & 255);
            out.writeByte((store >>> 8) & 255);
            out.writeByte(store & 255);
        } else {
            out.writeByte(255);
            out.writeLong(epochSec);
        }
    }

// moved to Ser2
//    /**
//     * Reads the state from the stream.
//     *
//     * @param in  the input stream, not null
//     * @return the epoch seconds, not null
//     * @throws IOException if an error occurs
//     */
//    static long readEpochSec(DataInput in) throws IOException {
//        int hiByte = in.readByte() & 255;
//        if (hiByte == 255) {
//            return in.readLong();
//        } else {
//            int midByte = in.readByte() & 255;
//            int loByte = in.readByte() & 255;
//            long tot = ((hiByte << 16) + (midByte << 8) + loByte);
//            return (tot * 900) - 4575744000L;
//        }
//    }
}
