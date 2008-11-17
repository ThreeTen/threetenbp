/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * A set of calendrical information which may or may not be valid.
 * <p>
 * This class holds a representation of a date-time, an offset and a time zone.
 * All of these are optional and the date-time may consist of partially
 * complete information.
 * <p>
 * This class is useful when you don't know the kind of date-time object that
 * you will receive, just that it will be some form of date-time. The various
 * fields of a calendrical can be setup to be invalid, thus instances
 * must be treated with care.
 * For example a month is not limited to the normal range of 1 to 12.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Calendrical
        implements CalendricalProvider, DateProvider, TimeProvider, DateTimeProvider, Serializable {

    /**
     * The date time map, may be null.
     */
    private final DateTimeFields fields;
    /**
     * The date, may be null.
     */
    private transient volatile LocalDate date;
    /**
     * The time, may be null.
     */
    private transient volatile LocalTime time;
    /**
     * The offset, may be null.
     */
    private final ZoneOffset offset;
    /**
     * The zone, may be null.
     */
    private final TimeZone zone;

    /**
     * Constructor creating an empty instance which places no restrictions
     * on the date-time.
     *
     * @return the created Calendrical instance, never null
     */
    public static Calendrical calendrical() {
        return new Calendrical(null, null, null, null, null);
    }

    /**
     * Constructor creating a calendrical from a field-value pair.
     * <p>
     * A calendrical can hold state that is not a valid date-time.
     * Thus, this constructor does not check to see if the value is valid for the field.
     * For example, you could setup a calendrical with a day of month of 75.
     *
     * @param fieldRule  the rule, not null
     * @param value  the field value, may be invalid
     * @return the created Calendrical instance, never null
     * @throws NullPointerException if the field is null
     */
    public static Calendrical calendrical(DateTimeFieldRule fieldRule, int value) {
        return new Calendrical(DateTimeFields.fields(fieldRule, value), null, null, null, null);
    }

    /**
     * Constructor creating a calendrical from two field-value pairs.
     * <p>
     * A calendrical can hold state that is not a valid date-time.
     * Thus, this constructor does not check to see if the value is valid for the field.
     * For example, you could setup a calendrical with a day of month of 75.
     *
     * @param fieldRule1  the first rule, not null
     * @param value1  the first field value
     * @param fieldRule2  the second rule, not null
     * @param value2  the second field value
     * @return the created Calendrical instance, never null
     * @throws NullPointerException if either field is null
     */
    public static Calendrical calendrical(DateTimeFieldRule fieldRule1, int value1, DateTimeFieldRule fieldRule2, int value2) {
        return new Calendrical(DateTimeFields.fields(fieldRule1, value1, fieldRule2, value2), null, null, null, null);
    }

    /**
     * Constructor creating a calendrical from date-time fields, offset and zone.
     * <p>
     * A calendrical can hold state that is not a valid date-time.
     * This constructor does not check to see if the value is valid for the field.
     * For example, you could setup a calendrical with a day of month of 75.
     * <p>
     * This constructor also does not cross reference the date or time with the offset or zone.
     * For example, the zone could be set to America/New_York and the offset could be
     * set to +01:00, even though that is never a valid offset for the New York zone.
     *
     * @param fields  the date-time fields, may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     * @return the created Calendrical instance, never null
     */
    public static Calendrical calendrical(DateTimeFields fields, ZoneOffset offset, TimeZone zone) {
        return new Calendrical(fields, null, null, offset, zone);
    }

    /**
     * Constructor creating a calendrical from the four main date-time objects.
     * <p>
     * A calendrical can hold state that is not a valid date-time.
     * Thus, this constructor does not cross reference the date or time with the offset or zone.
     * For example, the zone could be set to America/New_York and the offset could be
     * set to +01:00, even though that is never a valid offset for the New York zone.
     *
     * @param date  the optional local date, such as '2007-12-03', may be null
     * @param time  the optional local time, such as '10:15:30', may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     * @return the created Calendrical instance, never null
     */
    public static Calendrical calendrical(LocalDate date, LocalTime time, ZoneOffset offset, TimeZone zone) {
        return new Calendrical(null, date, time, offset, zone);
    }

    /**
     * Copy constructor for immutability.
     *
     * @param fields  the date-time fields, may be null
     * @param date  the optional local date, such as '2007-12-03', may be null
     * @param time  the optional local time, such as '10:15:30', may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     */
    private Calendrical(
            DateTimeFields fields,
            LocalDate date,
            LocalTime time,
            ZoneOffset offset,
            TimeZone zone) {
//      // invariants
//      if (fields != null) {
//          if (date != null) {
//              fields.validateMatchesDate(date);
//          }
//          if (time != null) {
//              fields.validateMatchesTime(time);
//          }
//      }
        if (fields == null) {
            if (date == null && time == null) {
                fields = DateTimeFields.fields();
            } else {
                if (date != null) {
                    fields = date.toDateTimeFields();
                    if (time != null) {
                        fields = fields.withFields(time.toDateTimeFields());
                    }
                } else {
                    fields = time.toDateTimeFields();
                }
            }
        }
        this.fields = fields;
        this.date = date;
        this.time = time;
        this.offset = offset;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the field-value map fully creating date and/or time objects.
     * <p>
     * This method calls both {@link #merge()} and {@link #validate()}.
     * In addition, it checks that
     *
     * @throws CalendricalException if invalid
     */
    private void normalize() {
        Calendrical cal = fields.mergeStrict();
        date = cal.date;
        time = cal.time;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if a value can be obtained for the specified field.
     * <p>
     * This method does not check if the value returned would be valid.
     *
     * @param fieldRule  the field to query, null returns false
     * @return true if the field is supported, false otherwise
     */
    public boolean isSupported(DateTimeFieldRule fieldRule) {
        return getValueQuiet(fieldRule) != null;
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field cannot be obtained.
     * <p>
     * The value will be checked for basic validity.
     * The value returned will be within the valid range for the field.
     * <p>
     * Instances of Calendrical can hold invalid field values, such as
     * a day of month of -3 or an hour of 1000. This method ensures that
     * the result is within the valid range for the field.
     * No cross-validation between fields is performed.
     *
     * @param fieldRule  the field rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     * @throws InvalidCalendarFieldException if the value for the field is invalid
     */
    public int getValue(DateTimeFieldRule fieldRule) {
        return getValue(fieldRule, true);
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field cannot be obtained.
     * <p>
     * The value is optionally checked for basic validity.
     * <p>
     * Instances of Calendrical can hold invalid field values, such as
     * a day of month of -3 or an hour of 1000. This method optionally ensures
     * that the result is within the valid range for the field.
     * No cross-validation between fields is performed.
     *
     * @param fieldRule  the field rule to query from the map, not null
     * @param validate  true to validate the value, false to return the raw value
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     * @throws InvalidCalendarFieldException if the value for the field is invalid
     */
    public int getValue(DateTimeFieldRule fieldRule, boolean validate) {
        if (fieldRule == null) {
            throw new NullPointerException("The field rule must not be null");
        }
        Integer value = getValueQuiet(fieldRule);
        if (value != null) {
            if (validate) {
                fieldRule.checkValue(value);
            }
            return value;
        }
        throw new UnsupportedCalendarFieldException(fieldRule);
    }

    /**
     * Gets the value for the specified field returning null if the value
     * cannot be obtained.
     * <p>
     * The returned value is not validated and might be out of range for the rule.
     * <p>
     * Instances of DateTimeFields can hold invalid field values, such as
     * a day of month of -3 or an hour of 1000. This method performs no
     * validation on the returned value.
     *
     * @param fieldRule  the rule to query from the map, null returns null
     * @return the value mapped to the specified field, null if not present
     */
    public Integer getValueQuiet(DateTimeFieldRule fieldRule) {
        Integer value = null;
        if (fieldRule != null) {
            if (fields != null) {
                value = fields.getValueQuiet(fieldRule);
            }
            if (value == null) {
                normalize();
                value = fieldRule.getValueQuiet(date, time);
            }
        }
        return value;
    }

    //-----------------------------------------------------------------------
//    /**
//     * The optional set of specific fields and values.
//     * <p>
//     * This low-level method provides access to the stored set of fields.
//     * Field values should normally be accessed using {@link #getValue(DateTimeFieldRule)}.
//     * <p>
//     * A calendrical can contain three different representations of
//     * date-time information - DateTimeFields, LocalDate and LocalTime.
//     * All three are optional - this method returns the fields.
//     * <p>
//     * If both the date and fields are present then
//     * {@link DateTimeFields#matchesDate(LocalDate)} must return true.
//     * If both the time and fields are present then
//     * {@link DateTimeFields#matchesTime(LocalTime)} must return true.
//     * <p>
//     * The returned set of fields may contain invalid values, such as
//     * a day of month of -3 or an hour of 1000.
//     *
//     * @return the field set, may be null
//     */
//    public DateTimeFields getFields() {
//        return fields;
//    }
//
//    /**
//     * Gets the optional local date, such as '2007-12-03'.
//     * <p>
//     * This low-level method provides access to the stored date.
//     * Field values should normally be accessed using {@link #getValue(DateTimeFieldRule)}.
//     * <p>
//     * A calendrical can contain three different representations of
//     * date-time information - DateTimeFields, LocalDate and LocalTime.
//     * All three are optional - this method returns the date.
//     * <p>
//     * If both the date and fields are present then
//     * {@link DateTimeFields#matchesDate(LocalDate)} must return true.
//     * If both the time and fields are present then
//     * {@link DateTimeFields#matchesTime(LocalTime)} must return true.
//     *
//     * @return the date, may be null
//     */
//    public LocalDate getDate() {
//        return date;
//    }
//
//    /**
//     * Gets the optional local time, such as '10:15:30'.
//     * <p>
//     * This low-level method provides access to the stored time.
//     * Field values should normally be accessed using {@link #getValue(DateTimeFieldRule)}.
//     * <p>
//     * A calendrical can contain three different representations of
//     * date-time information - DateTimeFields, LocalDate and LocalTime.
//     * All three are optional - this method returns the time.
//     * <p>
//     * If both the date and fields are present then
//     * {@link DateTimeFields#matchesDate(LocalDate)} must return true.
//     * If both the time and fields are present then
//     * {@link DateTimeFields#matchesTime(LocalTime)} must return true.
//     *
//     * @return the time, may be null
//     */
//    public LocalTime getTime() {
//        return time;
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the optional time zone offset, such as '+02:00'.
     * This method will return null if the offset is null.
     *
     * @return the offset, may be null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Gets the optional time zone rules, such as 'Europe/Paris'.
     * This method will return null if the zone is null.
     *
     * @return the zone, may be null
     */
    public TimeZone getZone() {
        return zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Calendrical with the specified set of fields.
     * <p>
     * The fields replace any date, time or fields from when the calendrical
     * was first constructed. The existing set of fields are not merged with
     * the specified set.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fields  the fields to store, may be null
     * @return a new, updated Calendrical, never null
     */
    public Calendrical withFields(DateTimeFields fields) {
        fields = (fields == null ? DateTimeFields.fields() : fields);
        if (fields.equals(this.fields)) {
            return this;
        }
        return new Calendrical(fields, null, null, offset, zone);
    }

//    /**
//     * Returns a copy of this Calendrical with the date altered.
//     *
//     * @param date  the date to store, may be null
//     * @return a new, updated Calendrical, never null
//     */
//    public Calendrical withDate(LocalDate date) {
//        return new Calendrical(fields, date, time, offset, zone);
//    }
//
//    /**
//     * Returns a copy of this Calendrical with the time altered.
//     *
//     * @param time  the time to store, may be null
//     * @return a new, updated Calendrical, never null
//     */
//    public Calendrical withTime(LocalTime time) {
//        return new Calendrical(fields, date, time, offset, zone);
//    }

    /**
     * Returns a copy of this Calendrical with the zone offset altered.
     *
     * @param offset  the zone offset to store, may be null
     * @return a new, updated Calendrical, never null
     */
    public Calendrical withOffset(ZoneOffset offset) {
        if (offset == this.offset || (offset != null && offset.equals(this.offset))) {
            return this;
        }
        return new Calendrical(fields, date, time, offset, zone);
    }

    /**
     * Returns a copy of this Calendrical with the zone altered.
     *
     * @param zone  the zone to store, may be null
     * @return a new, updated Calendrical, never null
     */
    public Calendrical withZone(TimeZone zone) {
        if (zone == this.zone || (zone != null && zone.equals(this.zone))) {
            return this;
        }
        return new Calendrical(fields, date, time, offset, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this calendrical to a DateTimeFields.
     * <p>
     * The result will either be the fields specified in the factory method,
     * or a set of fields converted from the LocalDate and/or LocalTime.
     * The converted date fields are year, month and day of month.
     * The converted time fields are hour, minute, second and nano.
     *
     * @return the DateTimeFields, never null
     */
    public DateTimeFields toDateTimeFields() {
        return fields;
    }

    /**
     * Converts this calendrical to a LocalDate.
     * <p>
     * This method will convert the date-time information stored to a LocalDate.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the date, see {@link DateTimeFields}.
     *
     * @return the LocalDate, never null
     * @throws CalendarConversionException if the date cannot be converted
     */
    public LocalDate toLocalDate() {
        normalize();
        if (date == null) {
            throw new CalendarConversionException(
                "Cannot convert Calendrical to LocalDate, insufficient infomation to create a date");
        }
        return date;
    }

    /**
     * Converts this calendrical to a LocalTime.
     * <p>
     * This method will convert the date-time information stored to a LocalTime.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the time, see {@link DateTimeFields}.
     *
     * @return the LocalTime, never null
     * @throws CalendarConversionException if the time cannot be converted
     */
    public LocalTime toLocalTime() {
        normalize();
        if (time == null) {
            throw new CalendarConversionException(
                "Cannot convert Calendrical to LocalTime, insufficient infomation to create a time");
        }
        return time;
    }

    /**
     * Converts this calendrical to a LocalDateTime.
     * <p>
     * This method will convert the date-time information stored to a LocalDateTime.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the date-time, see {@link DateTimeFields}.
     *
     * @return the LocalDateTime, never null
     * @throws CalendarConversionException if the date or time cannot be converted
     */
    public LocalDateTime toLocalDateTime() {
        normalize();
        if (date == null || time == null) {
            throw new CalendarConversionException(
                "Cannot convert Calendrical to LocalTime, insufficient infomation available");
        }
        return LocalDateTime.dateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to an OffsetDate.
     * <p>
     * This method will convert the date-time information stored to an OffsetDate.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the date, see {@link DateTimeFields}.
     *
     * @return the OffsetDate, never null
     * @throws CalendarConversionException if the date cannot be converted, or the offset is null
     */
    public OffsetDate toOffsetDate() {
        if (offset == null) {
            throw new CalendarConversionException("Cannot convert Calendrical to OffsetDate because the offset is null");
        }
        return OffsetDate.date(toLocalDate(), offset);
    }

    /**
     * Converts this object to an OffsetTime.
     * <p>
     * This method will convert the date-time information stored to an OffsetTime.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the time, see {@link DateTimeFields}.
     *
     * @return the OffsetTime, never null
     * @throws CalendarConversionException if the time cannot be converted, or the offset is null
     */
    public OffsetTime toOffsetTime() {
        if (offset == null) {
            throw new CalendarConversionException("Cannot convert Calendrical to OffsetTime because the offset is null");
        }
        return OffsetTime.time(toLocalTime(), offset);
    }

    /**
     * Converts this object to an OffsetDateTime.
     * <p>
     * This method will convert the date-time information stored to an OffsetDateTime.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the date-time, see {@link DateTimeFields}.
     *
     * @return the OffsetDateTime, never null
     * @throws CalendarConversionException if the date or time cannot be converted, or the offset is null
     */
    public OffsetDateTime toOffsetDateTime() {
        if (offset == null) {
            throw new CalendarConversionException("Cannot convert Calendrical to OffsetDateTime because the offset is null");
        }
        return OffsetDateTime.dateTime(toLocalDateTime(), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a ZonedDateTime.
     * <p>
     * This method will convert the date-time information stored to an ZonedDateTime.
     * If this calendrical was created using a DateTimeFields, then the fields
     * will be merged to obtain the date-time, see {@link DateTimeFields}.
     *
     * @return the ZonedDateTime, never null
     * @throws CalendarConversionException if the date or time cannot be converted, or the offset or zone is null
     */
    public ZonedDateTime toZonedDateTime() {
        OffsetDateTime dateTime = toOffsetDateTime();
        if (zone == null) {
            throw new CalendarConversionException("Cannot convert Calendrical to ZonedDateTime because the zone is null");
        }
        return ZonedDateTime.dateTime(dateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a Calendrical, trivially returning <code>this</code>.
     *
     * @return this
     */
    public Calendrical toCalendrical() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this Calendrical equal to the specified Calendrical.
     * <p>
     * The comparison is based on the fields, offset and zone. Any date or
     * time that was specified in a factory method will be converted to a
     * set of fields as described in {@link #toDateTimeFields()}.
     *
     * @param obj  the other Calendrical to compare to, null returns false
     * @return true if this instance is equal to the specified Calendrical
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Calendrical)) {
            return false;
        }
        final Calendrical other = (Calendrical) obj;
        if (this.offset != other.offset && (this.offset == null || !this.offset.equals(other.offset))) {
            return false;
        }
        if (this.zone != other.zone && (this.zone == null || !this.zone.equals(other.zone))) {
            return false;
        }
        if (this.fields != other.fields && !this.fields.equals(other.fields)) {
            return false;
        }
        return true;
    }

    /**
     * A hash code for this calendrical.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.offset != null ? this.offset.hashCode() : 0);
        hash = 59 * hash + (this.zone != null ? this.zone.hashCode() : 0);
        hash = 59 * hash + this.fields.hashCode();
        return hash;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the Calendrical as a <code>String</code>.
     * <p>
     * The output will use the following format:
     * <ul>
     * <li>Fields, as formatted by DateTimeFields</li>
     * <li>Offset, prefixed by a space if non-null</li>
     * <li>Zone, prefixed by a space if non-null</li>
     * </ul>
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(fields);
        if (offset != null) {
            buf.append(' ').append(offset);
        }
        if (zone != null) {
            buf.append(' ').append(zone);
        }
        return buf.toString();
    }

}
