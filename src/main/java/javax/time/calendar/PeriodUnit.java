/*
 * Copyright (c) 2010 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.time.Duration;
import javax.time.period.PeriodField;
import javax.time.period.PeriodFields;

/**
 * A unit of time for measuring a period, such as 'Days' or 'Minutes'.
 * <p>
 * <code>PeriodUnit</code> is an immutable definition of a unit of human-scale time.
 * For example, humans typically measure periods of time in units of years, months,
 * days, hours, minutes and seconds. These concepts are defined by instances of
 * this class defined in the chronology classes.
 * <p>
 * Units are either basic or derived. A derived unit can be converted accurately to
 * another smaller unit. A basic unit is fundamental, and has no smaller representation.
 * For example years are a derived unit consisting of 12 months, where a month is
 * a basic unit.
 * <p>
 * PeriodUnit is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodUnit
        implements Comparable<PeriodUnit>, Serializable {
    // TODO: serialization

    /** The serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the unit, not null.
     */
    private final String name;
    /**
     * The estimated duration of the unit, not null.
     */
    private final Duration estimatedDuration;
    /**
     * The periods equivalent to this unit.
     */
    private final List<PeriodField> equivalentPeriods;

    /**
     * Factory to create a base unit that cannot be derived.
     * <p>
     * A base unit cannot be derived from any smaller unit.
     * For example, an ISO month period cannot be derived from any other smaller period.
     * <p>
     * This method is typically only used when writing a {@link Chronology}.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     */
    public static PeriodUnit basic(String name, Duration estimatedDuration) {
        // avoid possible circular references by using inline NPE checks
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (estimatedDuration == null) {
            throw new NullPointerException("Estimated duration must not be null");
        }
        if (estimatedDuration.isNegative() || estimatedDuration.isZero()) {
            throw new IllegalArgumentException("Alternate period must not be negative or zero");
        }
        return new PeriodUnit(name, null, estimatedDuration);
    }

    /**
     * Factory to create a unit that is derived from another smaller unit.
     * <p>
     * A derived unit is created as a multiple of a smaller unit.
     * For example, an ISO year period can be derived as 12 ISO month periods.
     * <p>
     * The estimated duration is calculated using {@link PeriodFields#toEstimatedDuration()}.
     * <p>
     * This method is typically only used when writing a {@link Chronology}.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param equivalentPeriod  the period this is derived from, not null
     * @throws ArithmeticException if the equivalent period calculation overflows
     * @throws ArithmeticException if the estimated duration is too large
     */
    public static PeriodUnit derived(String name, PeriodField equivalentPeriod) {
        // avoid possible circular references by using inline NPE checks
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (equivalentPeriod == null) {
            throw new NullPointerException("Equivalent period must not be null");
        }
        if (equivalentPeriod.isNegative() || equivalentPeriod.isZero()) {
            throw new IllegalArgumentException("Equivalent period must not be negative or zero");
        }
        return new PeriodUnit(name, equivalentPeriod, equivalentPeriod.toEstimatedDuration());
    }

    /**
     * Package private constructor used for to enhance system startup performance.
     *
     * @param name  the name of the type, not null
     * @param equivalentPeriod  the period this is derived from, null if no equivalent
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     * @throws ArithmeticException if the equivalent period calculation overflows
     */
    PeriodUnit(
            String name,
            PeriodField equivalentPeriod,
            Duration estimatedDuration) {
        // input known to be valid, don't call this by reflection!
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        
        List<PeriodField> equivalents = new ArrayList<PeriodField>();
        if (equivalentPeriod != null) {
            equivalents.add(equivalentPeriod);
            long multiplier = equivalentPeriod.getAmount();
            List<PeriodField> baseEquivalents = equivalentPeriod.getUnit().getEquivalentPeriods();
            for (int i = 0; i < baseEquivalents.size(); i++) {
                equivalents.add(baseEquivalents.get(i).multipliedBy(multiplier));
            }
        }
        this.equivalentPeriods = Collections.unmodifiableList(equivalents);
    }

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//        out.writeObject(chronology);
//        out.writeUTF(name);
//        out.writeLong(estimatedDuration.getSeconds());
//        out.writeInt(estimatedDuration.getNanosAdjustment());
//        out.writeLong(equivalentPeriods.size() == 0 ? 0 : equivalentPeriods.get(0).getAmount());
//        out.writeObject(equivalentPeriods.size() == 0 ? null : equivalentPeriods.get(0).getUnit());
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        Chronology chrono = (Chronology) in.readObject();
//        String name = in.readUTF();
//        Duration dur = Duration.seconds(in.readLong(), in.readInt());
//        long amount = in.readLong();
//        PeriodUnit unit = (PeriodUnit) in.readObject();
//        PeriodField equivalent = (amount == 0 ? null : PeriodField.of(amount, unit));
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the unit, used as an identifier for the unit.
     * <p>
     * Implementations should use the name that best represents themselves.
     * Most units will have a plural name, such as 'Years' or 'Minutes'.
     * The name is not localized.
     *
     * @return the name of the unit, never null
     */
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the periods that are equivalent to this unit.
     * <p>
     * Most units are related to other units.
     * For example, an hour might be represented as 60 minutes or 3600 seconds.
     * Thus, if this is the 'Hour' unit, then this method would return a list
     * including both '60 Minutes', '3600 Seconds' and any other equivalent periods.
     * <p>
     * The list will be unmodifiable and sorted, from largest unit to smallest.
     *
     * @return the equivalent periods, may be empty, never null
     */
    public List<PeriodField> getEquivalentPeriods() {
        return equivalentPeriods;
    }

    /**
     * Gets the period in the specified unit that is equivalent to this unit.
     * <p>
     * Most units are related to other units.
     * For example, an hour might be represented as 60 minutes or 3600 seconds.
     * Thus, if this is the 'Hour' unit and the 'Seconds' unit is requested,
     * then this method would return '3600 Seconds'.
     * <p>
     * If the unit specified is this unit, then a period of 1 of this unit is returned.
     *
     * @param requiredUnit  the required unit, not null
     * @return the equivalent period, null if no equivalent in that unit
     */
    public PeriodField getEquivalentPeriod(PeriodUnit requiredUnit) {
        for (PeriodField equivalent : equivalentPeriods) {
            if (equivalent.getUnit().equals(requiredUnit)) {
                return equivalent;
            }
        }
        if (requiredUnit.equals(this)) {
            return PeriodField.of(1, this);  // cannot be cached in constructor, as would be unsafe publication
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an estimate of the duration of the unit in seconds.
     * <p>
     * Each unit has a duration which is a reasonable estimate.
     * For those units which can be derived ultimately from nanoseconds, the
     * estimated duration will be accurate. For other units, it will be an estimate.
     * <p>
     * One key use for the estimated duration is to implement {@link Comparable}.
     *
     * @return the estimate of the duration in seconds, never null
     */
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this unit to another.
     * <p>
     * The comparison is based primarily on the {@link #getEstimatedDuration() estimated duration}.
     * If that is equal, the name and then the first equivalent period are checked.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(PeriodUnit other) {
        int cmp = estimatedDuration.compareTo(other.estimatedDuration);
        if (cmp == 0) {
            cmp = name.compareTo(other.name);
            if (cmp == 0) {
                cmp = (equivalentPeriods.size() - other.equivalentPeriods.size());
                if (cmp == 0 && equivalentPeriods.size() > 0) {
                    cmp = (equivalentPeriods.get(0).compareTo(other.equivalentPeriods.get(0)));
                }
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two units based on the name, estimated duration and equivalent period.
     *
     * @return true if the units are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PeriodUnit) {
            PeriodUnit other = (PeriodUnit) obj;
            return name.equals(other.name) &&
                    estimatedDuration.equals(other.estimatedDuration) &&
                    equivalentPeriods.size() == other.equivalentPeriods.size() &&
                    (equivalentPeriods.size() == 0 || equivalentPeriods.get(0).equals(other.equivalentPeriods.get(0)));
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a hash code based on the name, estimated duration and equivalent period.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        // TODO: use transient field when serialization fixed
        return name.hashCode() ^ estimatedDuration.hashCode() ^ equivalentPeriods.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the unit.
     * <p>
     * The string representation is the same as the name.
     *
     * @return the unit name, never null
     */
    @Override
    public String toString() {
        return name;
    }

}
