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
package org.threeten.bp.chrono;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

import javaemul.internal.annotations.GwtIncompatible;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

/**
 * The shared serialization delegate for this package.
 *
 * <h4>Implementation notes</h4>
 * This class wraps the object being serialized, and takes a byte representing the type of the class to
 * be serialized.  This byte can also be used for versioning the serialization format.  In this case another
 * byte flag would be used in order to specify an alternative version of the type format.
 * For example {@code JAPANESE_DATE_TYPE_VERSION_2 = 21}.
 * <p>
 * In order to serialise the object it writes its byte and then calls back to the appropriate class where
 * the serialisation is performed.  In order to deserialise the object it read in the type byte, switching
 * in order to select which class to call back into.
 * <p>
 * The serialisation format is determined on a per class basis.  In the case of field based classes each
 * of the fields is written out with an appropriate size format in descending order of the field's size.  For
 * example in the case of {@link LocalDate} year is written before month.  Composite classes, such as
 * {@link LocalDateTime} are serialised as one object.
 * <p>
 * This class is mutable and should be created once per serialization.
 *
 * @serial include
 */
@GwtIncompatible
final class Ser implements Externalizable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7857518227608961174L;

    static final byte JAPANESE_DATE_TYPE = 1;
    static final byte JAPANESE_ERA_TYPE = 2;
    static final byte HIJRAH_DATE_TYPE = 3;
    static final byte HIJRAH_ERA_TYPE = 4;
    static final byte MINGUO_DATE_TYPE = 5;
    static final byte MINGUO_ERA_TYPE = 6;
    static final byte THAIBUDDHIST_DATE_TYPE = 7;
    static final byte THAIBUDDHIST_ERA_TYPE = 8;
    static final byte CHRONO_TYPE = 11;
    static final byte CHRONO_LOCALDATETIME_TYPE = 12;
    static final byte CHRONO_ZONEDDATETIME_TYPE = 13;

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
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        writeInternal(type, object, out);
    }

    private static void writeInternal(byte type, Object object, ObjectOutput out) throws IOException {
        out.writeByte(type);
        switch (type) {
            case JAPANESE_DATE_TYPE:
                ((JapaneseDate) object).writeExternal(out);
                break;
            case JAPANESE_ERA_TYPE:
                ((JapaneseEra) object).writeExternal(out);
                break;
            case HIJRAH_DATE_TYPE:
                ((HijrahDate) object).writeExternal(out);
                break;
            case HIJRAH_ERA_TYPE:
                ((HijrahEra) object).writeExternal(out);
                break;
            case MINGUO_DATE_TYPE:
                ((MinguoDate) object).writeExternal(out);
                break;
            case MINGUO_ERA_TYPE:
                ((MinguoEra) object).writeExternal(out);
                break;
            case THAIBUDDHIST_DATE_TYPE:
                ((ThaiBuddhistDate) object).writeExternal(out);
                break;
            case THAIBUDDHIST_ERA_TYPE:
                ((ThaiBuddhistEra) object).writeExternal(out);
                break;
            case CHRONO_TYPE:
                ((Chronology) object).writeExternal(out);
                break;
            case CHRONO_LOCALDATETIME_TYPE:
                ((ChronoLocalDateTimeImpl<?>) object).writeExternal(out);
                break;
            case CHRONO_ZONEDDATETIME_TYPE:
                ((ChronoZonedDateTimeImpl<?>) object).writeExternal(out);
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
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = in.readByte();
        object = readInternal(type, in);
    }

    static Object read(ObjectInput in) throws IOException, ClassNotFoundException {
        byte type = in.readByte();
        return readInternal(type, in);
    }

    private static Object readInternal(byte type, ObjectInput in) throws IOException, ClassNotFoundException {
        switch (type) {
            case JAPANESE_DATE_TYPE:  return JapaneseDate.readExternal(in);
            case JAPANESE_ERA_TYPE: return JapaneseEra.readExternal(in);
            case HIJRAH_DATE_TYPE: return HijrahDate.readExternal(in);
            case HIJRAH_ERA_TYPE: return HijrahEra.readExternal(in);
            case MINGUO_DATE_TYPE: return MinguoDate.readExternal(in);
            case MINGUO_ERA_TYPE: return MinguoEra.readExternal(in);
            case THAIBUDDHIST_DATE_TYPE: return ThaiBuddhistDate.readExternal(in);
            case THAIBUDDHIST_ERA_TYPE: return ThaiBuddhistEra.readExternal(in);
            case CHRONO_TYPE: return Chronology.readExternal(in);
            case CHRONO_LOCALDATETIME_TYPE: return ChronoLocalDateTimeImpl.readExternal(in);
            case CHRONO_ZONEDDATETIME_TYPE: return ChronoZonedDateTimeImpl.readExternal(in);
            default:
                throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    /**
     * Returns the object that will replace this one.
     *
     * @return the read object, should never be null
     */
    private Object readResolve() {
         return object;
    }

}
