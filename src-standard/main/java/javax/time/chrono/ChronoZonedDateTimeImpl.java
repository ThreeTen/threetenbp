/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;

import java.io.Serializable;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.OffsetDateTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.jdk8.DefaultInterfaceChronoZonedDateTime;
import javax.time.zone.ZoneOffsetInfo;
import javax.time.zone.ZoneOffsetTransition;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;
import javax.time.zone.ZoneRules;

/**
 * A date-time with a time-zone in the calendar neutral API.
 * <p>
 * {@code ZoneChronoDateTime} is an immutable representation of a date-time with a time-zone.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as a time-zone and zone offset.
 * <p>
 * The purpose of storing the time-zone is to distinguish the ambiguous case where
 * the local time-line overlaps, typically as a result of the end of daylight time.
 * Information about the local-time can be obtained using methods on the time-zone.
 * <p>
 * This class provides control over what happens at these cutover points
 * (typically a gap in spring and an overlap in autumn). The {@link ZoneResolver}
 * interface and implementations in {@link ZoneResolvers} provide strategies for
 * handling these cases. The methods {@link #withEarlierOffsetAtOverlap()} and
 * {@link #withLaterOffsetAtOverlap()} provide further control for overlaps.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 *
 * @param <C> the chronology of this date
 */
 class ChronoZonedDateTimeImpl<C extends Chrono<C>>
        extends DefaultInterfaceChronoZonedDateTime<C>
        implements ChronoZonedDateTime<C>, WithAdjuster, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -5261813987200935591L;

    /**
     * The offset date-time.
     */
    private final ChronoOffsetDateTimeImpl<C> dateTime;
    /**
     * The time-zone.
     */
    private final ZoneId zone;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from a local date-time
     * providing a resolver to handle an invalid date-time.
     * <p>
     * This factory creates a {@code ZoneChronoDateTime} from a date-time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param dateTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the resolver cannot resolve an invalid local date-time
     */
    static <R extends Chrono<R>> ChronoZonedDateTime<R> of(ChronoDateTimeImpl<R> dateTime, ZoneId zone, ZoneResolver resolver) {
        return resolve(dateTime, zone, null, resolver);
    }

    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from an {@code ChronoOffsetDateTime}
     * ensuring that the offset provided is valid for the time-zone.
     * <p>
     * This factory creates a {@code ZoneChronoDateTime} from an offset date-time and time-zone.
     * If the date-time is invalid for the zone due to a time-line gap then an exception is thrown.
     * Otherwise, the offset is checked against the zone to ensure it is valid.
     * <p>
     * An alternative to this method is {@link #ofInstant}. This method will retain
     * the date and time and throw an exception if the offset is invalid.
     * The {@code ofInstant} method will change the date and time if necessary
     * to retain the same instant.
     *
     * @param dateTime  the offset date-time to use, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if no rules can be found for the zone
     * @throws DateTimeException if the date-time is invalid due to a gap in the local time-line
     * @throws DateTimeException if the offset is invalid for the time-zone at the date-time
     */
    static <R extends Chrono<R>> ChronoZonedDateTimeImpl<R> of(ChronoOffsetDateTimeImpl<R> dateTime, ZoneId zone) {
        Objects.requireNonNull(dateTime, "dateTime");
        Objects.requireNonNull(zone, "zone");
        ZoneOffset inputOffset = dateTime.getOffset();
        ZoneRules rules = zone.getRules();  // latest rules version
        LocalDateTime ldt = LocalDate.from(dateTime.getDate()).atTime(dateTime.getTime());
        ZoneOffsetInfo info = rules.getOffsetInfo(ldt);
        if (info.isValidOffset(inputOffset) == false) {
            if (info instanceof ZoneOffsetTransition && ((ZoneOffsetTransition) info).isGap()) {
                throw new DateTimeException("The local time " + LocalDateTime.from(dateTime) +
                        " does not exist in time-zone " + zone + " due to a daylight savings gap");
            }
            throw new DateTimeException("The offset in the date-time " + dateTime +
                    " is invalid for time-zone " + zone);
        }
        return new ChronoZonedDateTimeImpl<>(dateTime, zone);
    }


    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from an {@code ChronoOffsetDateTime}.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * As such, the resulting local date-time may be different from the input.
     * <p>
     * If the instant represents a point on the time-line outside the supported year
     * range then an exception will be thrown.
     *
     * @param instantDateTime  the instant to create the date-time from, not null
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    private static <R extends Chrono<R>> ChronoZonedDateTimeImpl<R>
            ofInstant(ChronoOffsetDateTimeImpl<R> instantDateTime, ZoneId zone) {
        Objects.requireNonNull(instantDateTime, "instantDateTime");
        Objects.requireNonNull(zone, "zone");
        ZoneRules rules = zone.getRules();  // latest rules version
        // Add optimization to avoid toInstant
        instantDateTime = instantDateTime.withOffsetSameInstant(rules.getOffset(instantDateTime.toInstant()));
        instantDateTime.atZoneSameInstant(zone); ///recurse
        return new ChronoZonedDateTimeImpl<R>(instantDateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneChronoDateTime}.
     *
     * @param desiredLocalDateTime  the date-time, not null
     * @param zone  the time-zone, not null
     * @param oldDateTime  the old date-time prior to the calculation, may be null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the date-time cannot be resolved
     */
    private static <R extends Chrono<R>> ChronoZonedDateTime<R>
            resolve(ChronoLocalDateTime<R> desiredLocalDateTime, ZoneId zone,
                    ChronoOffsetDateTime<?> oldDateTime, ZoneResolver resolver) {
        Objects.requireNonNull(desiredLocalDateTime, "desiredLocalDateTime");
        Objects.requireNonNull(zone, "zone");
        Objects.requireNonNull(resolver, "resolver");
        ZoneRules rules = zone.getRules();
        LocalDateTime desired = LocalDateTime.from(desiredLocalDateTime);
        OffsetDateTime old = (oldDateTime == null ? null : OffsetDateTime.from(oldDateTime));
        OffsetDateTime offsetDT = resolver.resolve(desired, rules.getOffsetInfo(desired), rules, zone, old);
        if (offsetDT == null || rules.isValidDateTime(offsetDT) == false) {
            throw new DateTimeException(
                    "ZoneResolver implementation must return a valid date-time and offset for the zone: " + resolver.getClass().getName());
        }
        // Convert the date back to the current chronology and set the time.
        ChronoLocalDateTime<R> cdt = desiredLocalDateTime.with(EPOCH_DAY, offsetDT.getLong(EPOCH_DAY)).with(offsetDT.getTime());
        ChronoOffsetDateTime<R> codt = cdt.atOffset(offsetDT.getOffset());
        return codt.atZoneSimilarLocal(zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, validated as not null
     * @param zone  the time-zone, validated as not null
     */
    protected ChronoZonedDateTimeImpl(ChronoOffsetDateTimeImpl<C> dateTime, ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        Objects.requireNonNull(dateTime, "dateTime");
        this.dateTime = dateTime;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        return field instanceof LocalDateTimeField || (field != null && field.doIsSupported(this));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset, such as '+01:00'.
     *
     * @return the zone offset, not null
     */
    public ZoneOffset getOffset() {
        return dateTime.getOffset();
    }

    /**
     * Returns a copy of this ZoneChronoDateTime changing the zone offset to the
     * earlier of the two valid offsets at a local time-line overlap.
     * <p>
     * This method only has any effect when the local time-line overlaps, such as
     * at an autumn daylight savings cutover. In this scenario, there are two
     * valid offsets for the local date-time. Calling this method will return
     * a zoned date-time with the earlier of the two selected.
     * <p>
     * If this method is called when it is not an overlap, {@code this}
     * is returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code ZoneChronoDateTime} based on this date-time with the earlier offset, not null
     * @throws DateTimeException if no rules can be found for the zone
     * @throws DateTimeException if no rules are valid for this date-time
     */
    @Override
    public ChronoZonedDateTime<C> withEarlierOffsetAtOverlap() {
        ZoneOffsetInfo info = getZone().getRules().getOffsetInfo(LocalDateTime.from(this));
        if (info instanceof ZoneOffsetTransition) {
            ZoneOffset offset = ((ZoneOffsetTransition) info).getOffsetBefore();
            if (offset.equals(getOffset()) == false) {
                ChronoOffsetDateTimeImpl<C> newDT = dateTime.withOffsetSameLocal(offset);
                return new ChronoZonedDateTimeImpl<C>(newDT, zone);
            }
        }
        return this;
    }

    /**
     * Returns a copy of this ZoneChronoDateTime changing the zone offset to the
     * later of the two valid offsets at a local time-line overlap.
     * <p>
     * This method only has any effect when the local time-line overlaps, such as
     * at an autumn daylight savings cutover. In this scenario, there are two
     * valid offsets for the local date-time. Calling this method will return
     * a zoned date-time with the later of the two selected.
     * <p>
     * If this method is called when it is not an overlap, {@code this}
     * is returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code ZoneChronoDateTime} based on this date-time with the later offset, not null
     * @throws DateTimeException if no rules can be found for the zone
     * @throws DateTimeException if no rules are valid for this date-time
     */
    public ChronoZonedDateTime<C> withLaterOffsetAtOverlap() {
        ZoneOffsetInfo info = getZone().getRules().getOffsetInfo(LocalDateTime.from(this));
        if (info instanceof ZoneOffsetTransition) {
            ZoneOffset offset = ((ZoneOffsetTransition) info).getOffsetAfter();
            if (offset.equals(getOffset()) == false) {
                ChronoOffsetDateTimeImpl<C> newDT = dateTime.withOffsetSameLocal(offset);
                return new ChronoZonedDateTimeImpl<C>(newDT, zone);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-zone, such as 'Europe/Paris'.
     * <p>
     * This returns the stored time-zone id used to determine the time-zone rules.
     *
     * @return the time-zone, not null
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Returns a copy of this ZoneChronoDateTime with a different time-zone,
     * retaining the local date-time if possible.
     * <p>
     * This method changes the time-zone and retains the local date-time.
     * The local date-time is only changed if it is invalid for the new zone.
     * In that case, the {@link ZoneResolvers#retainOffset() retain offset} resolver is used.
     * <p>
     * To change the zone and adjust the local date-time,
     * use {@link #withZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to change to, not null
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested zone, not null
     */
    public ChronoZonedDateTime<C> withZoneSameLocal(ZoneId zone) {
        return withZoneSameLocal(zone, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this ZoneChronoDateTime with a different time-zone,
     * retaining the local date-time if possible.
     * <p>
     * This method changes the time-zone and retains the local date-time.
     * The local date-time is only changed if it is invalid for the new zone.
     * In that case, the specified resolver is used.
     * <p>
     * To change the zone and adjust the local date-time,
     * use {@link #withZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to change to, not null
     * @param resolver  the resolver to use, not null
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested zone, not null
     */
    @Override
    public ChronoZonedDateTime<C> withZoneSameLocal(ZoneId zone, ZoneResolver resolver) {
        Objects.requireNonNull(zone, "zone");
        Objects.requireNonNull(resolver, "resolver");
        return zone == this.zone ? this :
            resolve(dateTime.getDateTime(), zone, dateTime, resolver);
    }

    /**
     * Returns a copy of this ZoneChronoDateTime with a different time-zone,
     * retaining the instant.
     * <p>
     * This method changes the time-zone and retains the instant.
     * This normally results in a change to the local date-time.
     * <p>
     * This method is based on retaining the same instant, thus gaps and overlaps
     * in the local time-line have no effect on the result.
     * <p>
     * To change the offset while keeping the local time,
     * use {@link #withZoneSameLocal(ZoneId)}.
     *
     * @param zone  the time-zone to change to, not null
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested zone, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    public ChronoZonedDateTime<C> withZoneSameInstant(ZoneId zone) {
        return zone == this.zone ? this : ofInstant(dateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    int getYear() {
        return dateTime.getYear();
    }

    /**
     * Gets the month-of-year field from 1 to 12 or 13 depending on the chronology.
     *
     * @return the month-of-year, from 1 to 12 or 13
     */
    int getMonthValue() {
        return dateTime.getMonthValue();
    }

    /**
     * Gets the day-of-month field.
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    int getDayOfMonth() {
        return dateTime.getDayOfMonth();
    }

    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    int getDayOfYear() {
        return dateTime.getDayOfYear();
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    DayOfWeek getDayOfWeek() {
        return dateTime.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    int getHour() {
        return dateTime.getHour();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    int getMinute() {
        return dateTime.getMinute();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    int getSecond() {
        return dateTime.getSecond();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    int getNano() {
        return dateTime.getNano();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the local date-time altered.
     * <p>
     * This method returns an object with the same {@code ZoneId} and the
     * specified {@code ChronoLocalDateTime}.
     * <p>
     * If the adjusted date results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     *
     * @param dateTime  the local date-time to change to, not null
     * @return a {@code ZoneChronoDateTime} based on this time with the requested date-time, not null
     */
    private <R extends Chrono<R>> ChronoZonedDateTime<R> withDateTime(ChronoLocalDateTime<R> dateTime) {
        return withDateTime(dateTime, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the local date-time altered,
     * providing a resolver for invalid date-times.
     * <p>
     * This method returns an object with the same {@code ZoneId} and the
     * specified {@code ChronoLocalDateTime}.
     * <p>
     * If the adjusted date results in a date-time that is invalid, then the
     * specified resolver is used.
     *
     * @param newDateTime  the local date-time to change to, not null
     * @param resolver  the resolver to use, not null
     * @return a {@code ZoneChronoDateTime} based on this time with the requested date-time, not null
     */
    @SuppressWarnings("unchecked")
    private <R extends Chrono<R>> ChronoZonedDateTime<R> withDateTime(ChronoLocalDateTime<R> newDateTime, ZoneResolver resolver) {
        Objects.requireNonNull(newDateTime, "newDateTime");
        Objects.requireNonNull(resolver, "resolver");
        if (dateTime.getDateTime().equals(newDateTime)) {
            return (ChronoZonedDateTime<R>) this;
        } else {
            return resolve(newDateTime, zone, this.dateTime, resolver);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> with(WithAdjuster adjuster) {
        return with(adjuster, ZoneResolvers.retainOffset());
    }

    public ChronoZonedDateTime<C> with(WithAdjuster adjuster, ZoneResolver resolver) {
        Objects.requireNonNull(adjuster, "adjuster");
        Objects.requireNonNull(resolver, "resolver");
        ChronoOffsetDateTime<C> newDT = dateTime.with(adjuster);  // TODO: should adjust ZDT, not ODT
        return (newDT == dateTime ? this : resolve(newDT.getDateTime(), zone, dateTime, resolver));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified field altered.
     * <p>
     * This method returns a new date-time based on this date-time with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * In some cases, changing the specified field can cause the resulting date-time to become invalid,
     * such as changing the month from January to February would make the day-of-month 31 invalid.
     * In cases like this, the field is responsible for resolving the date. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned date-time, not null
     * @param newValue  the new value of the field in the returned date-time, not null
     * @return a {@code ZoneChronoDateTime} based on this date-time with the specified field set, not null
     * @throws DateTimeException if the value is invalid
     */
    @Override
    public ChronoZonedDateTime<C> with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            switch (f) {
                case INSTANT_SECONDS:
                    //return ofEpochSecond(newValue, zone);
                    throw new RuntimeException("NYI:");
                case OFFSET_SECONDS: {
                    ZoneOffset offset = ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue));
                    ChronoOffsetDateTimeImpl<C> odt = dateTime.withOffsetSameLocal(offset);
                    return ofInstant(odt, zone);
                }
            }
            return with(dateTime.with(field, newValue));
        }
        return field.doSet(this, newValue);
    }

    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from a local date-time
     * providing a resolver to handle an invalid date-time.
     * <p>
     * This factory creates a {@code ZoneChronoDateTime} from a date-time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param desiredTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the resolver cannot resolve an invalid local date-time
     */
    @SuppressWarnings("unchecked")
    private <R extends Chrono<R>> ChronoZonedDateTime<R> with(ChronoOffsetDateTime<R> desiredTime, ZoneId zone, ZoneResolver resolver) {
        Objects.requireNonNull(desiredTime, "desiredTime");
        Objects.requireNonNull(zone, "zone");
        Objects.requireNonNull(resolver, "resolver");
        ZoneRules rules = zone.getRules();
        // Convert to ISO desired date and time to apply zone check/replacement
        LocalDateTime desired = LocalDateTime.from(desiredTime);
        OffsetDateTime old = OffsetDateTime.from(dateTime);
        OffsetDateTime offsetDT = resolver.resolve(desired, rules.getOffsetInfo(desired), rules, zone, old);
        if (offsetDT == null || rules.isValidDateTime(offsetDT) == false) {
            throw new DateTimeException(
                    "ZoneResolver implementation must return a valid date-time and offset for the zone: " + resolver.getClass().getName());
        }
        if (offsetDT.equals(old) && getZone() == zone) {
            return (ChronoZonedDateTime<R>) this;
        }
        // Convert offsetDT.date back to the right chronology ChronoLocalDate
        // Convert the date back to the current chronology and set the time.
        ChronoOffsetDateTimeImpl<R> cdt = (ChronoOffsetDateTimeImpl<R>) desiredTime.with(EPOCH_DAY, offsetDT.get(EPOCH_DAY)).with(offsetDT.getTime());
        ChronoOffsetDateTimeImpl<R> codt = cdt.withOffsetSameLocal(offsetDT.getOffset());
        return new ChronoZonedDateTimeImpl<R>(codt, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the year value altered.
     * <p>
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    ChronoZonedDateTime<C> withYear(int year) {
        ChronoOffsetDateTime<C> newDT = dateTime.withYear(year);
        if (newDT == dateTime) {
            return this;
        }
        return with(newDT, zone, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the month-of-year value altered.
     * <p>
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested month, not null
     * @throws DateTimeException if the month value is invalid
     */
    ChronoZonedDateTime<C> withMonth(int month) {
        ChronoOffsetDateTime<C> newDT = dateTime.withMonth(month);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the day-of-month value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested day, not null
     * @throws DateTimeException if the day-of-month value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    ChronoZonedDateTime<C> withDayOfMonth(int dayOfMonth) {
        ChronoOffsetDateTime<C> newDT = dateTime.withDayOfMonth(dayOfMonth);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the day-of-year altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code ZoneChronoDateTime} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-year value is invalid
     * @throws DateTimeException if the day-of-year is invalid for the year
     */
    ChronoZonedDateTime<C> withDayOfYear(int dayOfYear) {
        ChronoOffsetDateTime<C> newDT = dateTime.withDayOfYear(dayOfYear);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the hour-of-day value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested hour, not null
     * @throws DateTimeException if the hour value is invalid
     */
    ChronoZonedDateTime<C> withHour(int hour) {
        ChronoOffsetDateTime<C> newDT = dateTime.withHour(hour);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the minute-of-hour value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested minute, not null
     * @throws DateTimeException if the minute value is invalid
     */
    ChronoZonedDateTime<C> withMinute(int minute) {
        ChronoOffsetDateTime<C> newDT = dateTime.withMinute(minute);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the second-of-minute value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested second, not null
     * @throws DateTimeException if the second value is invalid
     */
    ChronoZonedDateTime<C> withSecond(int second) {
        ChronoOffsetDateTime<C> newDT = dateTime.withSecond(second);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the nano-of-second value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code ZoneChronoDateTime} based on this date-time with the requested nanosecond, not null
     * @throws DateTimeException if the nanos value is invalid
     */
    ChronoZonedDateTime<C> withNano(int nanoOfSecond) {
        ChronoOffsetDateTime<C> newDT = dateTime.withNano(nanoOfSecond);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }


    //-----------------------------------------------------------------------
    @Override
    public ChronoZonedDateTime<C> plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            return with(dateTime.plus(amountToAdd, unit));
        }
        return unit.doAdd(this, amountToAdd);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in four steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the years added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusYears(long years) {
        ChronoOffsetDateTime<C> newDT = dateTime.plusYears(years);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in four steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the months added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusMonths(long months) {
        ChronoOffsetDateTime<C> newDT = dateTime.plusMonths(months);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusWeeks(long weeks) {
        ChronoOffsetDateTimeImpl<C> newDT = dateTime.plusWeeks(weeks);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the days added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusDays(long days) {
        ChronoOffsetDateTimeImpl<C> newDT = dateTime.plusDays(days);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in hours added.
     * <p>
     * This method uses field based addition.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being added
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a time-zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a period of 2 hours to 00:30 will result in 02:30, but it is important
     * to note that the change in duration was only 1 hour.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the hours added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusHours(long hours) {
        ChronoOffsetDateTime<C> newDT = dateTime.plusHours(hours);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in minutes added.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the minutes added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusMinutes(long minutes) {
        ChronoOffsetDateTime<C> newDT = dateTime.plusMinutes(minutes);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in seconds added.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the seconds added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusSeconds(long seconds) {
        ChronoOffsetDateTime<C> newDT = dateTime.plusSeconds(seconds);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in nanoseconds added.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the nanoseconds added, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> plusNanos(long nanos) {
        ChronoOffsetDateTime<C> newDT = dateTime.plusNanos(nanos);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount to the years field in four steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusYears(long years) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusYears(years);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount to the months field in four steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusMonths(long months) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusMonths(months);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one week would result in the 2009-01-07.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusWeeks(long weeks) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusWeeks(weeks);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in days subtracted.
     * <p>
     * This method subtracts the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one day would result in the 2009-01-01.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusDays(long days) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusDays(days);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in hours subtracted.
     * <p>
     * This method uses field based subtraction.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being subtracted
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a time-zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, subtracting
     * a period of 2 hours from 02:30 will result in 00:30, but it is important
     * to note that the change in duration was only 1 hour.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the hours subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusHours(long hours) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusHours(hours);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in minutes subtracted.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the minutes subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusMinutes(long minutes) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusMinutes(minutes);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in seconds subtracted.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the seconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusSeconds(long seconds) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusSeconds(seconds);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZoneChronoDateTime} with the specified period in nanoseconds subtracted.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, positive or negative
     * @return a {@code ZoneChronoDateTime} based on this date-time with the nanoseconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    ChronoZonedDateTime<C> minusNanos(long nanos) {
        ChronoOffsetDateTime<C> newDT = dateTime.minusNanos(nanos);
        return (newDT == dateTime ? this :
            resolve(newDT.getDateTime(), zone, dateTime, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoOffsetDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        ChronoZonedDateTimeImpl<?> end = (ChronoZonedDateTimeImpl<?>) endDateTime;
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            long until = dateTime.periodUntil(end.dateTime, unit);
            // NYI Adjust for offsets
            throw new DateTimeException("nyi: ChronoZonedDateTime.periodUntil");
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this {@code ZonedDateTime} to a {@code OffsetDateTime}.
     *
     * @return a OffsetDateTime representing the fields of this date-time, not null
     */
    public ChronoOffsetDateTime<C> getOffsetDateTime() {
        return dateTime;
    }

}
