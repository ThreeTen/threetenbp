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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


/**
 * Flexible date time implementation that can hold a date, time, date-time or
 * parts of a date-time, with optional offset and time zone.
 * <p>
 * This class is useful when you don't know the kind of date-time object that
 * you will receive, just that it will be some form of date-time. The various
 * fields of a flexible date-time can be setup to be invalid, thus instances
 * must be treated with care.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class FlexiDateTime implements Calendrical {

    /**
     * The date time map, never null, may be empty.
     */
    private final Map<DateTimeFieldRule, Integer> fieldValueMap = new TreeMap<DateTimeFieldRule, Integer>();
    /**
     * The date, may be null.
     */
    private final LocalDate date;
    /**
     * The time, may be null.
     */
    private final LocalTime time;
    /**
     * The offset, may be null.
     */
    private final ZoneOffset offset;
    /**
     * The zone, may be null.
     */
    private final TimeZone zone;

    /**
     * Constructor creating from the four main objects.
     *
     * @param date  the optional local date, such as '2007-12-03', may be null
     * @param time  the optional local time, such as '10:15:30', may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     */
    public FlexiDateTime(LocalDate date, LocalTime time, ZoneOffset offset, TimeZone zone) {
        this(null, date, time, offset, zone);
    }

    /**
     * Constructor creating from a rule-value pair.
     *
     * @param rule  the rule, not null
     * @param value  the field value
     */
    public FlexiDateTime(DateTimeFieldRule rule, int value) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        fieldValueMap.put(rule, value);
        this.date = null;
        this.time = null;
        this.offset = null;
        this.zone = null;
    }

    /**
     * Constructor creating from two rule-value pairs.
     *
     * @param rule1  the first rule, not null
     * @param value1  the first field value
     * @param rule2  the second rule, not null
     * @param value2  the second field value
     */
    public FlexiDateTime(DateTimeFieldRule rule1, int value1, DateTimeFieldRule rule2, int value2) {
        if (rule1 == null || rule2 == null) {
            throw new NullPointerException("The rules must not be null");
        }
        fieldValueMap.put(rule1, value1);
        fieldValueMap.put(rule2, value2);
        this.date = null;
        this.time = null;
        this.offset = null;
        this.zone = null;
    }

    /**
     * Constructor creating from with any combination of date and time information.
     *
     * @param fieldValueMap  the map of field rules and their values, may be null
     * @param date  the optional local date, such as '2007-12-03', may be null
     * @param time  the optional local time, such as '10:15:30', may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     * @throws NullPointerException if the map contains null keys or values
     */
    public FlexiDateTime(
            Map<DateTimeFieldRule, Integer> fieldValueMap,
            LocalDate date,
            LocalTime time,
            ZoneOffset offset,
            TimeZone zone) {
        if (fieldValueMap != null) {
            if (fieldValueMap.containsKey(null)) {
                throw new NullPointerException("Null keys are not permitted in field-value map");
            }
            if (fieldValueMap.containsValue(null)) {
                throw new NullPointerException("Null values are not permitted in field-value map");
            }
            fieldValueMap.putAll(fieldValueMap);
        }
        this.date = date;
        this.time = time;
        this.offset = offset;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value for the specified field throwing an exception if the
     * field cannot be obtained.
     * <p>
     * The value will be checked for basic validity.
     * The value returned will be within the valid range for the field.
     * Also, if the value is present in both the date/time and the field-value
     * map then the two values must be the same.
     *
     * @param rule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     * @throws InvalidCalendarFieldException if the value for the field is invalid
     */
    public int getValue(DateTimeFieldRule rule) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        Integer value = rule.extractValue(this);
        Integer mapValue = fieldValueMap.get(rule);
        if (mapValue == null && value == null) {
            throw new UnsupportedCalendarFieldException(rule);
        }
        if (value != null) {
            if (mapValue != null && mapValue.equals(value) == false) {
                throw new InvalidCalendarFieldException("Field " + rule.getName() + " has two different values " +
                        value + " and " + mapValue, rule);
            }
            return value;
        }
        rule.checkValue(mapValue);
        return mapValue;
    }

    //-----------------------------------------------------------------------
    /**
     * The optional set of specific fields and values.
     * <p>
     * The map will never be null, however it may be empty.
     * The values contained in the map might contradict the date or time, or
     * be out of range for the rule.
     * <p>
     * For example, the day of month might be set to 50, or the hour to 1000.
     * The purpose of this class is simply to store the values, not to provide
     * any guarantees as to their validity.
     *
     * @return a modifiable copy of the field-value map, never null
     */
    public Map<DateTimeFieldRule, Integer> getFieldValueMap() {
        return new HashMap<DateTimeFieldRule, Integer>(fieldValueMap);
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value returned might contradict the date or time, or be out of
     * range for the rule.
     * <p>
     * For example, the day of month might be set to 50, or the hour to 1000.
     * The purpose of this class is simply to store the values, not to provide
     * any guarantees as to their validity.
     *
     * @param rule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     */
    public int getFieldValueMapValue(DateTimeFieldRule rule) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        Integer value = fieldValueMap.get(rule);
        if (value != null) {
            return value;
        }
        throw new UnsupportedCalendarFieldException(rule, "FlexiDateTime");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the optional local date, such as '2007-12-03'.
     * This method will return null if the date is null.
     *
     * @return the date, may be null
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the optional local time, such as '10:15:30'.
     * This method will return null if the time is null.
     *
     * @return the time, may be null
     */
    public LocalTime getTime() {
        return time;
    }

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
     * Returns a copy of this FlexiDateTime with the map of fields altered.
     *
     * @param fieldValueMap  the new map of fields, not null
     * @return a new, updated FlexiDateTime, never null
     * @throws IllegalArgumentException if the map contains null keys or values
     */
    public FlexiDateTime withFieldValueMap(Map<DateTimeFieldRule, Integer> fieldValueMap) {
        return new FlexiDateTime(fieldValueMap, date, time, offset, zone);
    }

    /**
     * Returns a copy of this FlexiDateTime with the map of fields altered.
     *
     * @param fieldRule  the field to set in the field-value map, not null
     * @param value  the value to set in the field-value map
     * @return a new, updated FlexiDateTime, never null
     */
    public FlexiDateTime withFieldValue(DateTimeFieldRule fieldRule, int value) {
        if (fieldRule == null) {
            throw new NullPointerException("The field rule must not be null");
        }
        Map<DateTimeFieldRule, Integer> map = getFieldValueMap();
        map.put(fieldRule, value);
        return new FlexiDateTime(fieldValueMap, date, time, offset, zone);
    }

    /**
     * Returns a copy of this FlexiDateTime with the date altered.
     *
     * @param date  the date to store, may be null
     * @return a new, updated FlexiDateTime, never null
     */
    public FlexiDateTime withDate(LocalDate date) {
        return new FlexiDateTime(fieldValueMap, date, time, offset, zone);
    }

    /**
     * Returns a copy of this FlexiDateTime with the time altered.
     *
     * @param time  the time to store, may be null
     * @return a new, updated FlexiDateTime, never null
     */
    public FlexiDateTime withTime(LocalTime time) {
        return new FlexiDateTime(fieldValueMap, date, time, offset, zone);
    }

    /**
     * Returns a copy of this FlexiDateTime with the zone offset altered.
     *
     * @param offset  the zone offset to store, may be null
     * @return a new, updated FlexiDateTime, never null
     */
    public FlexiDateTime withOffset(ZoneOffset offset) {
        return new FlexiDateTime(fieldValueMap, date, time, offset, zone);
    }

    /**
     * Returns a copy of this FlexiDateTime with the zone altered.
     *
     * @param zone  the zone to store, may be null
     * @return a new, updated FlexiDateTime, never null
     */
    public FlexiDateTime withZone(TimeZone zone) {
        return new FlexiDateTime(fieldValueMap, date, time, offset, zone);
    }

    //-----------------------------------------------------------------------
//    /**
//     * Merges the field-value map fully creating date and/or time objects.
//     * <p>
//     * This method calls both {@link #merge()} and {@link #validate()}.
//     * In addition, it checks that
//     *
//     * @throws IllegalCalendarFieldValueException if invalid
//     */
//    public void mergeFully() {
//        FlexiDateTime merged = merge();
//        if (merged.fieldValueMap.size() > 0) {
//            throw new RuntimeException("Merging process resulted in leftover fields");  // TODO
//        }
//        merged.validate();
//    }

    /**
     * Merges the field-value map creating date and/or time objects.
     * <p>
     * Merging occurs by repeatedly calling {@link DateTimeFieldRule#mergeFields}
     * until the map of fields is reduced to its minimum size or both the
     * date and time are non-null. This method is normally only called when
     * the date, time or both is null.
     * <p>
     * Once the field-value map is merged into date/time objects, it is
     * recommended to call {@link #validate()} to check that any fields that
     * were not merged are valid.
     * <p>
     * For example, the field-value map might contain a year, day of year and
     * day of week. This merge method would combine the year and day of year
     * into a LocalDate, but the day of week would be unaffected. Calling
     * <code>validate()</code> would check that the day of week is correct for
     * the LocalDate that was created.
     * <p>
     * This method will throw an exception if a set of fields is found that should
     * be capable of creating a date/time, but the specifc values are invalid.
     * <p>
     * If there are no suitable combinations of fields to merge into a date/time,
     * then the returned object will still have a null date/time. No exception
     * is thrown in this case.
     *
     * @return the new instance, with merged date/time, never null
     * @throws IllegalCalendarFieldValueException if a set of date/time creation fields is invalid
     */
    public FlexiDateTime mergeFields() {
        return mergeDateFields().mergeTimeFields();
//        if (date == null || time == null) {
//            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
//                FlexiDateTime fdt = entry.getKey().mergeFields(this);
//                if (fdt != this) {
//                    return fdt.merge();
//                }
//            }
//        }
//        return this;
    }

    /**
     * Merges the field-value map creating a LocalDate.
     * <p>
     * Merging occurs by repeatedly calling {@link DateTimeFieldRule#mergeFields}
     * until the map of fields is reduced to its minimum size or the date has
     * been created. This method is normally only called when date is null as
     * there will simply return immediately if the date is non-null on entry.
     * <p>
     * Once the field-value map is merged into date objects, it is recommended
     * to call {@link #validateDate()} to check that any fields that were not
     * merged are valid.
     * <p>
     * For example, the field-value map might contain a year, day of year and
     * day of week. This merge method would combine the year and day of year
     * into a LocalDate, but the day of week would be unaffected. Calling
     * <code>validateDate()</code> would check that the day of week is correct
     * for the LocalDate that was created.
     * <p>
     * This method will throw an exception if a set of fields is found that should
     * be capable of creating a date, but the specifc values are invalid.
     * <p>
     * If there are no suitable combinations of fields to merge into a date,
     * then the returned object will still have a null date. No exception is
     * thrown in this case.
     *
     * @return the new instance, with merged date, never null
     * @throws IllegalCalendarFieldValueException if a set of date creation fields is invalid
     */
    public FlexiDateTime mergeDateFields() {
        if (date == null) {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                DateTimeFieldRule rule = entry.getKey();
                if (rule.isDateField()) {
                    FlexiDateTime fdt = rule.mergeFields(this);
                    if (fdt != this) {
                        return fdt.mergeDateFields();
                    }
                }
            }
        }
        return this;
    }

    /**
     * Merges the field-value map creating a LocalTime.
     * <p>
     * Merging occurs by repeatedly calling {@link DateTimeFieldRule#mergeFields}
     * until the map of fields is reduced to its minimum size or the time has
     * been created. This method is normally only called when time is null as
     * there will simply return immediately if the time is non-null on entry.
     * <p>
     * Once the field-value map is merged into time objects, it is recommended
     * to call {@link #validateTime()} to check that any fields that were not
     * merged are valid.
     * <p>
     * For example, the field-value map might contain an hour, minute and second,
     * plus a second of day. This merge method would combine the hour, minute and
     * second into a LocalTime, but the second of day would be unaffected. Calling
     * <code>validateTime()</code> would check that the second of day is correct
     * for the LocalTime that was created.
     * <p>
     * This method will throw an exception if a set of fields is found that should
     * be capable of creating a time, but the specifc values are invalid.
     * <p>
     * If there are no suitable combinations of fields to merge into a time,
     * then the returned object will still have a null time. No exception is
     * thrown in this case.
     *
     * @return the new instance, with merged time, never null
     * @throws IllegalCalendarFieldValueException if a set of time creation fields is invalid
     */
    public FlexiDateTime mergeTimeFields() {
        if (time == null) {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                DateTimeFieldRule rule = entry.getKey();
                if (rule.isTimeField()) {
                    FlexiDateTime fdt = rule.mergeFields(this);
                    if (fdt != this) {
                        return fdt.mergeTimeFields();
                    }
                }
            }
        }
        return this;
    }

    /**
     * Validates the contents of this FlexiDateTime.
     * <p>
     * Validation occurs in three steps.
     * Firstly, each field in the field-value map is checked to determine if
     * it is in range for the rules of the field.
     * Secondly, each date field is checked against the stored date.
     * Thirdly, each time field is checked against the stored time.
     * <p>
     * If the date and time are null, then only the field-value map will be
     * validated. Since the individual fields are not cross-validated in this
     * scenario, the method could succeed with an invalid date, such as February
     * 31st. To prevent this, it is recommended to call merge before calling
     * validate.
     *
     * @return this, for chaining, never null
     * @throws InvalidCalendarFieldException if any field is invalid
     */
    public FlexiDateTime validate() {
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            entry.getKey().checkValue(entry.getValue());
        }
        if (date != null) {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                DateTimeFieldRule rule = entry.getKey();
                if (date.isSupported(rule) && date.get(rule) != entry.getValue()) {
                    throw new InvalidCalendarFieldException("Value " + entry.getValue() +
                            " for " + rule.getName() + " does not match value for date " + date, rule);
                }
            }
        }
        if (time != null) {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                DateTimeFieldRule rule = entry.getKey();
                if (time.isSupported(rule) && time.get(rule) != entry.getValue()) {
                    throw new InvalidCalendarFieldException("Value " + entry.getValue() +
                            " for " + rule.getName() + " does not match value for time " + time, rule);
                }
            }
        }
        return this;
    }

    /**
     * Validates that any date represented by this FlexiDateTime is valid.
     * <p>
     * If this FlexiDateTime has a LocalDate, then each date field in the
     * field-value map is compared against it.
     * <p>
     * If this FlexiDateTime does not have a LocalDate, then each date field
     * in the field-value map is validated independently against its own rules.
     * In this case, no cross-validation occurs, thus a field-value map
     * containing February 31st would be valid, as each field is valid when
     * considered separately. Normally, this method is called after
     * {@link #mergeDateFields()} to ensure that cross-validation against the
     * LocalDate occurs.
     *
     * @return this, for chaining, never null
     * @throws InvalidCalendarFieldException if any field is invalid
     */
    public FlexiDateTime validateDate() {
        if (date == null) {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                DateTimeFieldRule rule = entry.getKey();
                if (rule.isDateField()) {
                    rule.checkValue(entry.getValue());
                }
            }
        } else {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                DateTimeFieldRule rule = entry.getKey();
                if (rule.isDateField() && date.isSupported(rule) && date.get(rule) != entry.getValue()) {
                    throw new InvalidCalendarFieldException("Value " + entry.getValue() +
                            " for " + rule.getName() + " does not match value for date " + date, rule);
                }
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a LocalDate.
     * <p>
     * This method will merge and validate any date fields in the field-value
     * map. The resulting validated LocalDate will then be returned, or an
     * exception thrown if the date could not be created.
     * <p>
     * If the field-value map is empty and the date field is non-null on
     * calling this method, then that date field will be returned.
     *
     * @return the LocalDate, never null
     * @throws CalendarConversionException if the date cannot be converted
     */
    public LocalDate toLocalDate() {
        FlexiDateTime merged = mergeDateFields();
        if (merged.date == null) {
            throw new CalendarConversionException(
                    "Cannot convert FlexiDateTime to LocalDate, insufficient infomation to create a date");
        }
        return merged.validateDate().date;
    }

    /**
     * Converts this object to a LocalTime.
     * <p>
     * This method will merge and validate any time fields in the field-value
     * map. The resulting validated LocalTime will then be returned, or an
     * exception thrown if the time could not be created.
     * <p>
     * If the field-value map is empty and the time field is non-null on
     * calling this method, then that time field will be returned.
     *
     * @return the LocalTime, never null
     * @throws CalendarConversionException if the time cannot be converted
     */
    public LocalTime toLocalTime() {
        FlexiDateTime merged = mergeDateFields();
        if (merged.time == null) {
            throw new CalendarConversionException(
                "Cannot convert FlexiDateTime to LocalTime, insufficient infomation to create a time");
        }
        return merged.validate().time;
    }

    /**
     * Converts this object to a LocalDateTime.
     * <p>
     * This method will merge and validate any fields in the field-value map.
     * The resulting validated LocalTime will then be returned, or an
     * exception thrown if either the date or time could not be created.
     * <p>
     * If the field-value map is empty and the date and time fields are non-null
     * on calling this method, then those date and time field will be used.
     *
     * @return the LocalDateTime, never null
     * @throws CalendarConversionException if the date or time cannot be converted
     */
    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.dateTime(toLocalDate(), toLocalTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to an OffsetDate.
     * This method will fail if the date or offset is null.
     *
     * @return the OffsetDate, never null
     * @throws CalendarConversionException if the date cannot be converted, or the offset is null
     */
    public OffsetDate toOffsetDate() {
        LocalDate date = toLocalDate();
        if (offset == null) {
            throw new CalendarConversionException("Cannot convert FlexiDateTime to OffsetDate because the offset is null");
        }
        return OffsetDate.date(date, offset);
    }

    /**
     * Converts this object to an OffsetTime.
     * This method will fail if the time or offset is null.
     *
     * @return the OffsetTime, never null
     * @throws CalendarConversionException if the time cannot be converted, or the offset is null
     */
    public OffsetTime toOffsetTime() {
        LocalTime time = toLocalTime();
        if (offset == null) {
            throw new CalendarConversionException("Cannot convert FlexiDateTime to OffsetTime because the offset is null");
        }
        return OffsetTime.time(time, offset);
    }

    /**
     * Converts this object to an OffsetDateTime.
     * This method will fail if the time or offset is null.
     *
     * @return the OffsetDateTime, never null
     * @throws CalendarConversionException if the date or time cannot be converted, or the offset is null
     */
    public OffsetDateTime toOffsetDateTime() {
        LocalDateTime dateTime = toLocalDateTime();
        if (offset == null) {
            throw new CalendarConversionException("Cannot convert FlexiDateTime to OffsetDateTime because the offset is null");
        }
        return OffsetDateTime.dateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a ZonedDateTime.
     * This method will fail if the date or offset is null.
     *
     * @return the ZonedDateTime, never null
     * @throws CalendarConversionException if the date or time cannot be converted, or the offset or zone is null
     */
    public ZonedDateTime toZonedDateTime() {
        OffsetDateTime dateTime = toOffsetDateTime();
        if (zone == null) {
            throw new CalendarConversionException("Cannot convert FlexiDateTime to ZonedDateTime because the zone is null");
        }
        return ZonedDateTime.dateTime(dateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a FlexiDateTime, trivially returning <code>this</code>.
     *
     * @return this
     */
    public FlexiDateTime toFlexiDateTime() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this FlexiDateTime equal to the specified FlexiDateTime.
     *
     * @param o  the other FlexiDateTime to compare to, null returns false
     * @return true if this instance is equal to the specified FlexiDateTime
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FlexiDateTime)) {
            return false;
        }
        final FlexiDateTime other = (FlexiDateTime) o;
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        if (this.offset != other.offset && (this.offset == null || !this.offset.equals(other.offset))) {
            return false;
        }
        if (this.zone != other.zone && (this.zone == null || !this.zone.equals(other.zone))) {
            return false;
        }
        if (this.fieldValueMap.equals(other.fieldValueMap) == false) {
            return false;
        }
        return true;
    }

    /**
     * A hashcode for this FlexiDateTime.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.fieldValueMap != null ? this.fieldValueMap.hashCode() : 0);
        hash = 59 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 59 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 59 * hash + (this.offset != null ? this.offset.hashCode() : 0);
        hash = 59 * hash + (this.zone != null ? this.zone.hashCode() : 0);
        return hash;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the flexi date-time as a <code>String</code>.
     * <p>
     * The output will use the following format:
     * <ul>
     * <li>Field-Value map, followed by space if non-empty</li>
     * <li>Date</li>
     * <li>Time, prefixed by 'T' if non-null</li>
     * <li>Offset</li>
     * <li>Zone, prefixed by a space if non-null</li>
     * </ul>
     * If an instance of LocalDate, LocalTime, LocalDateTime, OffsetDate, OffsetTime,
     * OffsetDateTime or ZonedDateTime is converted to a FlexiDateTime then the
     * toString output will remain the same.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (getFieldValueMap().size() > 0) {
            buf.append(getFieldValueMap());
            if (date != null || time != null || offset != null) {
                buf.append(' ');
            }
        }
        if (date != null) {
            buf.append(date);
        }
        if (time != null) {
            buf.append('T').append(time);
        }
        if (offset != null) {
            buf.append(offset);
        }
        if (zone != null) {
            if (date != null || time != null || offset != null) {
                buf.append(' ');
            }
            buf.append(zone);
        }
        return buf.toString();
    }

}
