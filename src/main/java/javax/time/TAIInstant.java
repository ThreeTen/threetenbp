/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.time.calendar.format.CalendricalParseException;

/**
 * An instantaneous point on the time-line measured in the TAI time-scale.
 * <p>
 * Most of the Time Framework for Java works on the assumption that the time-line is
 * simple, there are no leap-seconds and there are always 24 * 60 * 60 seconds in a day.
 * Sadly, the real-life time-line is not this simple.
 * <p>
 * This class is an alternative representation based on the TAI time-scale.
 * This scale is defined using atomic clocks and has proceeded in a continuous uninterrupted
 * manner since its epoch of 1958-01-01T00:00:00(TAI).
 * <p>
 * As there are no leap seconds, or other discontinuities, in TAI, this time-scale
 * would make an excellent timestamp. While there are, at the time of writing, few
 * easy ways to obtain an accurate TAI instant, it is relatively easy to obtain a GPS instant.
 * GPS and TAI differ by the fixed amount of 19 seconds.
 * <p>
 * The duration between two points on the TAI time-scale is calculated solely using this class.
 * Do not use the {@code between} method on {@code Duration} as that will lose information.
 * Instead use {@link #durationUntil(TAIInstant)} on this class.
 * <p>
 * It is intended that most applications will use the {@code Instant} class
 * which uses the UTC-SLS mapping from UTC to guarantee 86400 seconds per day.
 * Specialist applications with access to an accurate time-source may find this class useful.
 * <p>
 * TAIInstant is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TAIInstant
        implements Comparable<TAIInstant>, Serializable {
    // does not implement InstantProvider as that would enable methods like
    // Duration.between which gives the wrong answer due to lossy conversion

    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * Parse regex.
     */
    private static final Pattern PARSER = Pattern.compile("([-]?[0-9]+)\\.([0-9]{9})s[(]TAI[)]");
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of seconds from the epoch of 1958-01-01T00:00:00(TAI).
     */
    private final long seconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code TAIInstant} from the number of seconds from
     * the TAI epoch of 1958-01-01T00:00:00(TAI) with a nanosecond fraction of second.
     * <p>
     * This method allows an arbitrary number of nanoseconds to be passed in.
     * The factory will alter the values of the second and nanosecond in order
     * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
     * For example, the following will result in the exactly the same instant:
     * <pre>
     *  TAIInstant.ofSeconds(3, 1);
     *  TAIInstant.ofSeconds(4, -999999999);
     *  TAIInstant.ofSeconds(2, 1000000001);
     * </pre>
     *
     * @param taiSeconds  the number of seconds from the epoch of 1958-01-01T00:00:00(TAI)
     * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
     * @return the TAI instant, never null
     * @throws IllegalArgumentException if nanoOfSecond is out of range
     */
    public static TAIInstant ofTAISeconds(long taiSeconds, long nanoAdjustment) {
        long secs = MathUtils.safeAdd(taiSeconds, MathUtils.floorDiv(nanoAdjustment, NANOS_PER_SECOND));
        int nos = MathUtils.floorMod(nanoAdjustment, NANOS_PER_SECOND);
        return new TAIInstant(secs, nos);
    }

    /**
     * Obtains an instance of {@code TAIInstant} from an {@code Instant}
     * using the system default leap second rules.
     * <p>
     * Converting a UTC-SLS instant to a TAI instant requires leap second rules.
     * This method uses the latest available system rules.
     * The conversion first maps from UTC-SLS to UTC, then converts to TAI.
     * <p>
     * Conversion from an {@code Instant} will not be completely accurate near
     * a leap second in accordance with UTC-SLS.
     *
     * @param instant  the instant to convert, not null
     * @return the TAI instant, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public static TAIInstant of(Instant instant) {
        return UTCInstant.of(instant).toTAIInstant();
    }

    /**
     * Obtains an instance of {@code TAIInstant} from a {@code UTCInstant}.
     * <p>
     * Converting a UTC instant to a TAI instant requires leap second rules.
     * This method uses the rules held in within the UTC instant.
     * <p>
     * Conversion from a {@code UTCInstant} will be entirely accurate.
     * The resulting TAI instant will not reference the leap second rules, so
     * converting back to a UTC instant may result in a different UTC instant.
     *
     * @param instant  the instant to convert, not null
     * @return the TAI instant, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public static TAIInstant of(UTCInstant instant) {
        return instant.toTAIInstant();
    }

    /**
     * Obtains an instance of {@code TAIInstant} from a text string.
     * <p>
     * The following format is accepted in ASCII:
     * <ul>
     * <li>{@code {seconds).(nanosOfSecond}s(TAI)
     * </ul>
     * The accepted format is strict.
     * The seconds part must contain only numbers and a possible leading negative sign.
     * The nanoseconds part must contain exactly nine digits.
     * The trailing literal must be exactly specified.
     * This format parses the {@code toString} format.
     *
     * @param text  the text to parse such as '12345.123456789s(TAI)', not null
     * @return the parsed instant, never null
     * @throws CalendricalException if the text cannot be parsed
     */
    public static TAIInstant parse(String text) {
        Instant.checkNotNull(text, "Text to parse must not be null");
        Matcher matcher = PARSER.matcher(text);
        if (matcher.matches()) {
            try {
                long seconds = Long.parseLong(matcher.group(1));
                long nanos = Long.parseLong(matcher.group(2));
                return TAIInstant.ofTAISeconds(seconds, nanos);
            } catch (NumberFormatException ex) {
                throw new CalendricalParseException("The text could not be parsed", text, 0, ex);
            }
        }
        throw new CalendricalParseException("The text could not be parsed", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance.
     *
     * @param epochSeconds  the number of seconds from the epoch
     * @param nanoOfSecond  the nanoseconds within the second, from 0 to 999,999,999
     */
    private TAIInstant(long epochSeconds, int nanoOfSecond) {
        super();
        this.seconds = epochSeconds;
        this.nanos = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds from the TAI epoch of 1958-01-01T00:00:00(TAI).
     * <p>
     * The TAI second count is a simple incrementing count of seconds where
     * second 0 is 1958-01-01T00:00:00(TAI).
     * The nanosecond part of the day is returned by {@code getNanosOfSecond}.
     *
     * @return the seconds from the epoch of 1958-01-01T00:00:00(TAI)
     */
    public long getTAISeconds() {
        return seconds;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second.
     * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds from
     * the second returned by {@code getTAISeconds}.
     *
     * @return the nanoseconds within the second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * The duration is added using simple addition of the seconds and nanoseconds
     * in the duration to the seconds and nanoseconds of this instant.
     * As a result, the duration is treated as being measured in TAI compatible seconds
     * for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a {@code TAIInstant} based on this instant with the duration added, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public TAIInstant plus(Duration duration) {
        long secsToAdd = duration.getSeconds();
        int nanosToAdd = duration.getNanoOfSecond();
        if ((secsToAdd | nanosToAdd) == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(seconds, secsToAdd);
        long nanoAdjustment = ((long) nanos) + nanosToAdd;  // safe int+int
        return ofTAISeconds(secs, nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration subtracted.
     * <p>
     * The duration is subtracted using simple subtraction of the seconds and nanoseconds
     * in the duration from the seconds and nanoseconds of this instant.
     * As a result, the duration is treated as being measured in TAI compatible seconds
     * for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a {@code TAIInstant} based on this instant with the duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public TAIInstant minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNanoOfSecond();
        if ((secsToSubtract | nanosToSubtract) == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secsToSubtract);
        long nanoAdjustment = ((long) nanos) - nanosToSubtract;  // safe int+int
        return ofTAISeconds(secs, nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the duration between this instant and the specified instant.
     * <p>
     * This calculates the duration between this instant and another based on
     * the TAI time-scale. Adding the duration to this instant using {@link #plus}
     * will always result in an instant equal to the specified instant.
     *
     * @param taiInstant  the instant to calculate the duration until, not null
     * @return the duration until the specified instant, may be negative, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Duration durationUntil(TAIInstant taiInstant) {
        long durSecs = MathUtils.safeSubtract(taiInstant.seconds, seconds);
        long durNanos = taiInstant.nanos - nanos;
        return Duration.ofSeconds(durSecs, durNanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to a {@code UTCInstant} using the system default
     * leap second rules.
     * <p>
     * This method converts this instant from the TAI to the UTC time-scale using the
     * system default leap-second rules. This conversion does not lose information
     * and the UTC instant may safely be converted back to a {@code TAIInstant}.
     *
     * @return a {@code UTCInstant} representing the same instant using the system leap second rules, never null
     */
    public UTCInstant toUTCInstant() {
        return UTCInstant.of(this, UTCRules.system());
    }

    /**
     * Converts this instant to an {@code Instant} using the system default
     * leap second rules.
     * <p>
     * This method converts this instant from the TAI to the UTC-SLS time-scale using the
     * system default leap-second rules to convert to UTC.
     * This conversion will lose information around a leap second in accordance with UTC-SLS.
     * Converting back to a {@code TAIInstant} may result in a slightly different instant.
     *
     * @return an {@code Instant} representing the best approximation of this instant, never null
     */
    public Instant toInstant() {
        return toUTCInstant().toInstant();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to another based on the time-line.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(TAIInstant otherInstant) {
        int cmp = MathUtils.safeCompare(seconds, otherInstant.seconds);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanos, otherInstant.nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instant is equal to the specified {@code TAIInstant}.
     *
     * @param otherInstant  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof TAIInstant) {
            TAIInstant other = (TAIInstant) otherInstant;
            return this.seconds == other.seconds &&
                   this.nanos == other.nanos;
        }
        return false;
    }

    /**
     * Returns a hash code for this instant.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        // TODO: Evaluate hash code
        return ((int) (seconds ^ (seconds >>> 32))) + 51 * nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this instant.
     * <p>
     * The string is formatted as {@code {seconds).(nanosOfSecond}s(TAI).
     * At least one second digit will be present.
     * The nanoseconds will always be nine digits.
     *
     * @return a representation of this instant, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(seconds);
        int pos = buf.length();
        buf.append(nanos + NANOS_PER_SECOND);
        buf.setCharAt(pos, '.');
        buf.append("s(TAI)");
        return buf.toString();
    }

}
