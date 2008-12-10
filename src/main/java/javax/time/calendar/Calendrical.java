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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.time.CalendricalException;

/**
 * A flexible representation of calendrical information which may or may not be valid.
 * <p>
 * Dates and times often need to be processed even when they are not fully valid.
 * Instances of calendrical can hold any combination of date and time information.
 * This includes date, time, offset, zone and a field-value map, all are optional.
 * <p>
 * A calendrical may contain partially complete or conflicting information.
 * For example, the date might be '2007-12-03' while the field-value map holds the
 * month as February.
 * Another example, the offset might be '+02:00' while the time zone is 'Asia/Tokyo'
 * (the offset '+02:00' is never valid for this time zone).
 * <p>
 * In addition, the field-value map may contain values that are outside the
 * normal range for the field. For example, a day of month of -3 or an hour of 1000.
 * <p>
 * Given the complicated possible state of this class, care must be used.
 * <p>
 * Calendrical is mutable and cannot be shared safely between threads without
 * external synchronization.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Calendrical
        implements CalendricalProvider, DateProvider, TimeProvider, DateTimeProvider, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * The field map, never null.
     */
    private FieldMap fieldMap;
    /**
     * The date, may be null.
     */
    private LocalDate date;
    /**
     * The time, may be null.
     */
    private LocalTime time;
    /**
     * The offset, may be null.
     */
    private ZoneOffset offset;
    /**
     * The zone, may be null.
     */
    private TimeZone zone;

    /**
     * Constructor creating an empty instance which places no restrictions
     * on the date-time.
     */
    public Calendrical() {
        fieldMap = new FieldMap();
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
     * @throws NullPointerException if the field is null
     */
    public Calendrical(DateTimeFieldRule fieldRule, int value) {
        this();
        fieldMap.put(fieldRule, value);
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
     * @throws NullPointerException if either field is null
     */
    public Calendrical(DateTimeFieldRule fieldRule1, int value1, DateTimeFieldRule fieldRule2, int value2) {
        this();
        fieldMap.put(fieldRule1, value1);
        fieldMap.put(fieldRule2, value2);
    }

    /**
     * Constructor creating a calendrical from the four main date-time objects.
     * <p>
     * A calendrical can hold state that is not a valid date-time.
     * Thus, this constructor does not cross reference the date or time with the offset or zone.
     * For example, the zone could be set to 'America/New_York' and the offset could be
     * set to '+01:00', even though that is never a valid offset for the New York zone.
     *
     * @param date  the optional local date, such as '2007-12-03', may be null
     * @param time  the optional local time, such as '10:15:30', may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     */
    public Calendrical(LocalDate date, LocalTime time, ZoneOffset offset, TimeZone zone) {
        this();
        this.date = date;
        this.time = time;
        this.offset = offset;
        this.zone = zone;
    }

    /**
     * Constructor creating a calendrical from a set of fields.
     *
     * @param fields  the fields to copy, not null
     */
    public Calendrical(DateTimeFields fields) {
        fieldMap = new FieldMap(fields);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field-value map.
     * <p>
     * The map is part of the state of this instance.
     * Changing the returned map changes the state of this calendrical.
     *
     * @return the connected field value map, never null
     */
    public FieldMap getFieldMap() {
        return fieldMap;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date, such as '2007-12-03'.
     * The date and the field-value map may have conflicting values.
     *
     * @return the date, may be null
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date, such as '2007-12-03'.
     * The date and the field-value map may have conflicting values.
     *
     * @param date  the date, may be null
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time, such as '10:15:30'.
     * The time and the field-value map may have conflicting values.
     *
     * @return the time, may be null
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Sets the time, such as '10:15:30'.
     * The time and the field-value map may have conflicting values.
     *
     * @param time  the time, may be null
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone offset, such as '+02:00'.
     * The zone offset and time zone may have conflicting values.
     *
     * @return the offset, may be null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Sets the time zone offset, such as '+02:00'.
     * The zone offset and time zone may have conflicting values.
     *
     * @param offset  the offset, may be null
     */
    public void setOffset(ZoneOffset offset) {
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone rules, such as 'Europe/Paris'.
     * The time zone and zone offset may have conflicting values.
     *
     * @return the zone, may be null
     */
    public TimeZone getZone() {
        return zone;
    }

    /**
     * Sets the time zone rules, such as 'Europe/Paris'.
     * The time zone and zone offset may have conflicting values.
     *
     * @param zone  the zone, may be null
     */
    public void setZone(TimeZone zone) {
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This method checks if the field can be derived from the date, time or
     * field-value map. No check is performed on the validity of the value.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @return the value for the specified field, null if value not present
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public boolean isSupported(DateTimeFieldRule fieldRule) {
        if (fieldRule == null) {
            return false;
        }
        return (getValue(fieldRule) != null);
    }

    /**
     * Gets the value for the specified field from the date, time or field-value
     * map returning null if the field is not present.
     * <p>
     * The value will be derived first from the date and/or time.
     * If that does not suceed, then the field-value map will be queried.
     * Thus, if the date/time and field-value map are inconsistent, the value
     * from the date/time takes precedence.
     * <p>
     * A calendrical can hold invalid values, such as a day of month of -3 or an hour of 1000.
     * This method performs no validation on the returned value.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @return the value for the specified field, null if value not present
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public Integer getValue(DateTimeFieldRule fieldRule) {
        ISOChronology.checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        Integer value = fieldRule.getValueQuiet(date, time);
        return (value == null ? fieldMap.get(fieldRule) : value);
    }

    /**
     * Gets the value for the specified field from the date, time or field-value
     * map throwing an exception if the field is not present or is invalid.
     * <p>
     * The value will be derived first from the date and/or time.
     * If that does not suceed, then the field-value map will be queried.
     * Thus, if the date/time and field-value map are inconsistent, the value
     * from the date/time takes precedence.
     * <p>
     * A calendrical can hold invalid values, such as a day of month of -3 or an hour of 1000.
     * This method performs no validation on the returned value.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public int getValueInt(DateTimeFieldRule fieldRule) {
        ISOChronology.checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
        Integer value = fieldRule.getValueQuiet(date, time);
        return (value == null ? fieldMap.getInt(fieldRule) : value);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks this calendrical for consistency.
     * <p>
     * This method ensures that the values in the field-value map edo not conflict
     * those in the date, time or field-value map.
     *
     * @throws InvalidCalendarFieldException if any field is inconsistent
     */
    public void checkConsistent() {
        if (date != null || time != null) {
            Object errorValue = null;
            String errorText = null;
            if (date != null & time != null) {
                errorValue = LocalDateTime.dateTime(date, time);
                errorText = "date-time ";
            } else if (date != null) {
                errorValue = date;
                errorText = "date ";
            } else {
                errorValue = time;
                errorText = "time ";
            }
            Iterator<Entry<DateTimeFieldRule, Integer>> it = fieldMap.fieldValueMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<DateTimeFieldRule, Integer> entry = it.next();
                DateTimeFieldRule fieldRule = entry.getKey();
                Integer derivedValue = fieldRule.getValueQuiet(date, time);
                if (derivedValue != null) {
                    Integer mapValue = entry.getValue();
                    if (derivedValue.equals(mapValue) == false) {
                        throw new InvalidCalendarFieldException("Calendrical contains field map value " +
                                fieldRule.getID() + "=" + mapValue + " that is inconsistent with " +
                                errorText + errorValue, fieldRule);
//                        
//                        throw new InvalidCalendarFieldException("Calendrical contains a " + errorText +
//                                errorValue + " that is inconsistent with the value " + mapValue +
//                                " for " + fieldRule.getID(), fieldRule);
                    }
                }
            }
        }
        fieldMap.checkConsistent();
    }

    /**
     * Removes any field from the map that can be derived from the date, time
     * or from another field without checking if the removed value matches.
     * <p>
     * For example, if the field map contains 'MonthOfYear' and the date is
     * non-null, then the month will be removed from the map.
     */
    public void removeDerivable() {
        if (date != null || time != null) {
            Iterator<DateTimeFieldRule> it = fieldMap.fieldValueMap.keySet().iterator();
            while (it.hasNext()) {
                Integer derivedValue = it.next().getValueQuiet(date, time);
                if (derivedValue != null) {
                    it.remove();
                }
            }
        }
        fieldMap.removeDerivable();
    }

//    /**
//     * Checks this calendrical for consistency, removing any field that can be
//     * derived from date, time or field-value map.
//     */
//    public void checkAndRemoveDerivable() {
//        // date-time
//        Object errorValue = null;
//        String errorText = null;
//        if (date != null & time != null) {
//            errorValue = LocalDateTime.dateTime(date, time);
//            errorText = "date-time ";
//        } else if (date != null) {
//            errorValue = date;
//            errorText = "date ";
//        } else if (time != null) {
//            errorValue = time;
//            errorText = "time ";
//        }
//        if (errorValue != null) {
//            Iterator<Entry<DateTimeFieldRule, Integer>> it = fieldMap.fieldValueMap.entrySet().iterator();
//            while (it.hasNext()) {
//                Entry<DateTimeFieldRule, Integer> entry = it.next();
//                DateTimeFieldRule fieldRule = entry.getKey();
//                Integer mapValue = fieldRule.getValueQuiet(date, time);
//                if (mapValue != null) {
//                    Integer originalValue = entry.getValue();
//                    if (mapValue.equals(originalValue)) {
//                        it.remove();
//                    } else {
//                        throw new InvalidCalendarFieldException("Calendrical contained a " + errorText +
//                                errorValue + " that is inconsistent with the value " + originalValue +
//                                " for " + fieldRule.getID(), fieldRule);
//                    }
//                }
//            }
//        }
//        // fields
//        Iterator<Entry<DateTimeFieldRule, Integer>> it = fieldMap.fieldValueMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Entry<DateTimeFieldRule, Integer> entry = it.next();
//            DateTimeFieldRule fieldRule = entry.getKey();
//            Integer mergedValue = fieldRule.deriveValue(fieldMap);
//            if (mergedValue != null) {
//                Integer originalValue = entry.getValue();
//                if (mergedValue.equals(originalValue)) {
//                    it.remove();
//                } else {
//                    throw new InvalidCalendarFieldException("Calendrical contained a value " +
//                            mergedValue + " that is inconsistent with the input value " + originalValue +
//                            " for " + fieldRule.getID(), fieldRule);
//                }
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Merges the fields in this map to form a calendrical.
     * <p>
     * The merge process aims to extract the maximum amount of information
     * possible from this set of fields. Ideally the outcome will be a date, time
     * or both, however there may be insufficient information to achieve this.
     * <p>
     * The process repeatedly calls the field rule {@link DateTimeFieldRule#merge merge}
     * method to perform the merge on each individual field. Sometimes two or
     * more fields will combine to form a more significant field. Sometimes they
     * will combine to form a date or time. The process stops when there no more
     * merges can occur.
     * <p>
     * The process is based around hierarchies that can be combined.
     * For example, QuarterOfYear and MonthOfQuarter can be combined to form MonthOfYear.
     * Then, MonthOfYear can be combined with DayOfMonth and Year to form a date.
     * Any fields which take part in a merge will be removed from the result as their
     * values can be derived from the merged field.
     * <p>
     * The exact definition of which fields combine with which is chronology dependent.
     * For example, see {@link ISOChronology}.
     * <p>
     * The details of the process are controlled by the merge context.
     * This includes strict/lenient behaviour.
     * <p>
     * The merge must result in consistent values for each field, date and time.
     * If two different values are produced an exception is thrown.
     * For example, both Year/MonthOfYear/DayOfMonth and Year/DayOfYear will merge to form a date.
     * If both sets of fields do not produce the same date then an exception will be thrown.
     *
     * @return the new instance, with merged fields, never null
     * @throws CalendricalException if the fields cannot be merged
     */
    public Calendrical mergeStrict() {
        return merge(new CalendricalContext(true, true));
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the fields in this map to form a calendrical.
     * <p>
     * The merge process aims to extract the maximum amount of information
     * possible from this set of fields. Ideally the outcome will be a date, time
     * or both, however there may be insufficient information to achieve this.
     * <p>
     * The process repeatedly calls the field rule {@link DateTimeFieldRule#merge merge}
     * method to perform the merge on each individual field. Sometimes two or
     * more fields will combine to form a more significant field. Sometimes they
     * will combine to form a date or time. The process stops when there no more
     * merges can occur.
     * <p>
     * The process is based around hierarchies that can be combined.
     * For example, QuarterOfYear and MonthOfQuarter can be combined to form MonthOfYear.
     * Then, MonthOfYear can be combined with DayOfMonth and Year to form a date.
     * Any fields which take part in a merge will be removed from the result as their
     * values can be derived from the merged field.
     * <p>
     * The exact definition of which fields combine with which is chronology dependent.
     * For example, see {@link ISOChronology}.
     * <p>
     * The details of the process are controlled by the merge context.
     * This includes strict/lenient behaviour.
     * <p>
     * The merge must result in consistent values for each field, date and time.
     * If two different values are produced an exception is thrown.
     * For example, both Year/MonthOfYear/DayOfMonth and Year/DayOfYear will merge to form a date.
     * If both sets of fields do not produce the same date then an exception will be thrown.
     *
     * @return the new instance, with merged fields, never null
     * @throws CalendricalException if the fields cannot be merged
     */
    public Calendrical mergeLenient() {
        return merge(new CalendricalContext(false, true));
    }

    /**
     * Merges the fields in this map to form a calendrical using the specified context.
     * <p>
     * The merge process aims to extract the maximum amount of information
     * possible from this set of fields. Ideally the outcome will be a date, time
     * or both, however there may be insufficient information to achieve this.
     * <p>
     * The process repeatedly calls the field rule {@link DateTimeFieldRule#merge merge}
     * method to perform the merge on each individual field. Sometimes two or
     * more fields will combine to form a more significant field. Sometimes they
     * will combine to form a date or time. The process stops when there no more
     * merges can occur.
     * <p>
     * The process is based around hierarchies that can be combined.
     * For example, QuarterOfYear and MonthOfQuarter can be combined to form MonthOfYear.
     * Then, MonthOfYear can be combined with DayOfMonth and Year to form a date.
     * Any fields which take part in a merge will be removed from the result as their
     * values can be derived from the merged field.
     * <p>
     * The exact definition of which fields combine with which is chronology dependent.
     * For example, see {@link ISOChronology}.
     * <p>
     * The details of the process are controlled by the merge context.
     * This includes strict/lenient behaviour.
     * <p>
     * The merge must result in consistent values for each field, date and time.
     * If two different values are produced an exception is thrown.
     * For example, both Year/MonthOfYear/DayOfMonth and Year/DayOfYear will merge to form a date.
     * If both sets of fields do not produce the same date then an exception will be thrown.
     *
     * @return the new instance, with merged fields, never null
     * @throws CalendricalException if the fields cannot be merged
     */
    public Calendrical merge(CalendricalContext context) {
        if (fieldMap.size() > 0) {
            Merger merger = new Merger(this, context);
            merger.merge();
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this calendrical to a DateTimeFields, returning only those
     * fields in the field-value map.
     * <p>
     * This calendrical is converted to a fields instance by returning only
     * those fields in the field-value map. There will typically be other
     * date-time content in the date or time fields of this calendrical.
     * They will be ignored.
     * <p>
     * To obtain the best results, call one of the merge methods before calling
     * this method.
     * <pre>
     * DateTimeFields fields = calendrical.mergeStrict().toDateTimeFields();
     * </pre>
     *
     * @return the DateTimeFields, never null
     */
    public DateTimeFields toDateTimeFields() {
        return DateTimeFields.fields(fieldMap.toFieldValueMap());  // optimize
    }

    /**
     * Converts this calendrical to a LocalDate.
     * <p>
     * This calendrical is converted to a date by returning the date field.
     * If the date is null then an exception is thrown.
     * <p>
     * There will typically be date-time content in the field-value map of this calendrical.
     * In order to use that, call one of the merge methods before calling this method.
     * <pre>
     * LocalDate date = calendrical.mergeStrict().toLocalDate();
     * </pre>
     *
     * @return the LocalDate, never null
     * @throws CalendarConversionException if the date cannot be converted
     */
    public LocalDate toLocalDate() {
        if (date == null) {
            throw new CalendarConversionException(
                "Cannot convert Calendrical to LocalDate, insufficient infomation to create a date");
        }
        return date;
    }

    /**
     * Converts this calendrical to a LocalTime.
     * <p>
     * This calendrical is converted to a time by returning the time field.
     * If the time is null then an exception is thrown.
     * <p>
     * There will typically be date-time content in the field-value map of this calendrical.
     * In order to use that, call one of the merge methods before calling this method.
     * <pre>
     * LocalTime time = calendrical.mergeStrict().toLocalTime();
     * </pre>
     *
     * @return the LocalTime, never null
     * @throws CalendarConversionException if the time cannot be converted
     */
    public LocalTime toLocalTime() {
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
     * Converts this object to a Calendrical, returning a clone.
     *
     * @return a clone of this instance, never null
     */
    public Calendrical toCalendrical() {
        Calendrical cloned = new Calendrical(date, time, offset, zone);
        cloned.getFieldMap().putAll(fieldMap.fieldValueMap);
        return cloned;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this calendrical equal to the specified calendrical.
     * <p>
     * The comparison is based on the fields, date, time, offset and zone.
     *
     * @param obj  the other Calendrical to compare to, null returns false
     * @return true if this instance is equal to the specified Calendrical
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Calendrical == false) {
            return false;
        }
        final Calendrical other = (Calendrical) obj;
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
        return this.fieldMap.equals(other.fieldMap);
    }

    /**
     * A hash code for this calendrical.
     * <p>
     * The hash code is based on the fields, date, time, offset and zone.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (date != null ? date.hashCode() : 0);
        hash = 59 * hash + (time != null ? time.hashCode() : 0);
        hash = 59 * hash + (offset != null ? offset.hashCode() : 0);
        hash = 59 * hash + (zone != null ? zone.hashCode() : 0);
        hash = 59 * hash + fieldMap.hashCode();
        return hash;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the calendrical as a string.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (fieldMap.size() > 0) {
            buf.append(fieldMap).append(' ');
        }
        if (date != null) {
            buf.append(date).append(' ');
        }
        if (time != null) {
            buf.append(time).append(' ');
        }
        if (offset != null) {
            buf.append(offset).append(' ');
        }
        if (zone != null) {
            buf.append(zone).append(' ');
        }
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Mutable map of date-time fields that may be invalid.
     * <p>
     * The field map is part of the state of the underlying calendrical.
     * Changes to this instance directly affect the underlying calendrical.
     * <p>
     * Calendrical.FieldMap is mutable and not thread-safe.
     * It must only be used from a single thread and must not be passed between threads.
     */
    public static final class FieldMap implements Iterable<DateTimeFieldRule>, Serializable {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /**
         * The date time field map, may be null.
         */
        private final Map<DateTimeFieldRule, Integer> fieldValueMap =
            new TreeMap<DateTimeFieldRule, Integer>(Collections.reverseOrder());

        /**
         * Constructor.
         */
        private FieldMap() {
        }

        /**
         * Constructor.
         *
         * @param fields  the fields to copy, not null
         */
        private FieldMap(DateTimeFields fields) {
            fields.copyInto(fieldValueMap);
        }

        //-----------------------------------------------------------------------
        /**
         * The size of the map of fields to values.
         *
         * @return number of field-value pairs
         */
        public int size() {
            return fieldValueMap.size();
        }

        /**
         * Iterates through all the fields.
         * <p>
         * This method fulfills the {@link Iterable} interface and allows looping
         * around the fields using the for-each loop. The values can be obtained using
         * {@link #get(DateTimeFieldRule)} or {@link #getValueValidated(DateTimeFieldRule)}.
         *
         * @return an iterator over the fields in this object, never null
         */
        public Iterator<DateTimeFieldRule> iterator() {
            return fieldValueMap.keySet().iterator();
        }

        //-----------------------------------------------------------------------
        /**
         * Checks if the field-value map contains the specified field.
         * <p>
         * This method does not check if the value returned would be valid.
         * <p>
         * Calling this method checks whether {@link #getIntValidated} will
         * throw an exception and whether {@link #get} will return null.
         *
         * @param fieldRule  the field to query, null returns false
         * @return true if the field is supported, false otherwise
         */
        public boolean contains(DateTimeFieldRule fieldRule) {
            if (fieldRule == null) {
                return false;
            }
            return fieldValueMap.containsKey(fieldRule);
        }

        /**
         * Checks if the field-value map contains the specified field and that its
         * value is valid.
         * <p>
         * Calling this method checks whether {@link #getIntValidated} will
         * throw an exception or return a valid value.
         *
         * @param fieldRule  the field to query, null returns false
         * @return true if the field is supported, false otherwise
         */
        public boolean containsValid(DateTimeFieldRule fieldRule) {
            if (fieldRule == null) {
                return false;
            }
            Integer value = fieldValueMap.get(fieldRule);
            return value != null && fieldRule.isValidValue(value);
        }

        //-----------------------------------------------------------------------
        /**
         * Gets the value from the field-value map returning null if the field
         * is not present.
         * <p>
         * The value is not validated and might be out of range for the rule.
         * <p>
         * A calendrical can hold invalid values, such as a day of month of -3 or an hour of 1000.
         * This method performs no validation on the returned value.
         *
         * @param fieldRule  the rule to query from the map, not null
         * @return the value mapped to the specified field, null if not present
         */
        public Integer get(DateTimeFieldRule fieldRule) {
            ISOChronology.checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
            return fieldValueMap.get(fieldRule);
        }

        /**
         * Gets the value from the field-value map throwing an exception if the field
         * is not present.
         * <p>
         * A calendrical can hold invalid values, such as a day of month of -3 or an hour of 1000.
         * This method performs no validation on the returned value.
         *
         * @param fieldRule  the rule to query from the map, not null
         * @return the value mapped to the specified field
         * @throws UnsupportedCalendarFieldException if the field is not in the map
         * @throws IllegalCalendarFieldValueException if the value is invalid
         */
        public int getInt(DateTimeFieldRule fieldRule) {
            Integer value = get(fieldRule);
            if (value != null) {
                return value;
            }
            throw new UnsupportedCalendarFieldException(fieldRule, "Calendrical");
        }

        /**
         * Gets the value from the field-value map throwing an exception if the field
         * is not present or is invalid.
         * <p>
         * A calendrical can hold invalid values, such as a day of month of -3 or an hour of 1000.
         * This method ensures that the result is within the valid range for the field.
         * No cross-validation between fields is performed.
         *
         * @param fieldRule  the rule to query from the map, not null
         * @return the value mapped to the specified field
         * @throws UnsupportedCalendarFieldException if the field is not in the map
         * @throws IllegalCalendarFieldValueException if the value is invalid
         */
        public int getIntValidated(DateTimeFieldRule fieldRule) {
            Integer value = get(fieldRule);
            if (value != null) {
                fieldRule.checkValue(value);
                return value;
            }
            throw new UnsupportedCalendarFieldException(fieldRule, "Calendrical");
        }

        //-----------------------------------------------------------------------
        /**
         * Puts a field-value pair into this map replacing any previous value.
         * <p>
         * This method adds the specified field-value pair to the map.
         * If this instance already has a value for a field then the value is replaced.
         * Otherwise the value is added.
         *
         * @param fieldRule  the field to store, not null
         * @param value  the value to store
         * @return this, for method chaining, never null
         */
        public FieldMap put(DateTimeFieldRule fieldRule, int value) {
            ISOChronology.checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
            fieldValueMap.put(fieldRule, value);
            return this;
        }

        /**
         * Puts a map of field-value pairs into this map.
         * <p>
         * This method adds the specified field-value pairs to the map.
         * If this instance already has a value for a field then the value is replaced.
         * Otherwise the value is added.
         *
         * @param fieldValueMap  the map of field-value pairs to store, not null
         * @return this, for method chaining, never null
         * @throws IllegalArgumentException if the map contains null keys or values
         */
        public FieldMap putAll(Map<DateTimeFieldRule, Integer> fieldValueMap) {
            ISOChronology.checkNotNull(fieldValueMap, "Field-value map must not be null");
            if (fieldValueMap.size() > 0) {
                // don't use contains() as tree map and others can throw NPE
                for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                    DateTimeFieldRule key = entry.getKey();
                    Integer value = entry.getValue();
                    ISOChronology.checkNotNull(key, "Null keys are not permitted in field-value map");
                    ISOChronology.checkNotNull(value, "Null values are not permitted in field-value map");
                }
                this.fieldValueMap.putAll(fieldValueMap);
            }
            return this;
        }

//        /**
//         * Puts a map of field-value pairs into the field-value map.
//         * <p>
//         * This method adds the specified field-value pairs to the map.
//         * If this instance already has a value for a field then the value is replaced.
//         * Otherwise the value is added.
//         *
//         * @param fields  the map of field-value pairs to store, not null
//         * @return this, for method chaining, never null
//         */
//        public Fields putFields(DateTimeFields fields) {
//            ISOChronology.checkNotNull(fields, "DateTimeFields must not be null");
//            return fields.copyInto(fieldValueMap);
//        }

        /**
         * Removes the specified field rule from the field-value map.
         *
         * @param fieldRule  the field to remove, not null
         * @return this, for method chaining, never null
         */
        public FieldMap remove(DateTimeFieldRule fieldRule) {
            ISOChronology.checkNotNull(fieldRule, "DateTimeFieldRule must not be null");
            fieldValueMap.remove(fieldRule);
            return this;
        }

        /**
         * Removes the specified field rule from the field-value map.
         *
         * @param fieldRules  the fields to remove, not null
         * @return this, for method chaining, never null
         */
        public FieldMap removeAll(Iterable<DateTimeFieldRule> fieldRules) {
            ISOChronology.checkNotNull(fieldRules, "DateTimeFieldRule iterable must not be null");
            for (DateTimeFieldRule fieldRule : fieldRules) {
                remove(fieldRule);
            }
            return this;
        }

        /**
         * Clears the field-value map.
         *
         * @return this, for method chaining, never null
         */
        public FieldMap clear() {
            fieldValueMap.clear();
            return this;
        }

        //-----------------------------------------------------------------------
        /**
         * Checks if the value of each field is within its valid range.
         * <p>
         * The validation simply checks that each value in the field map is within the
         * normal range for the field as defined by {@link DateTimeFieldRule#checkValue(int)}.
         * No cross-validation between fields is performed, thus the field map could
         * contain an invalid date such as February 31st.
         *
         * @return true if all the fields are with in their valid range
         * @throws IllegalCalendarFieldValueException if any field is invalid
         */
        public boolean isValid() {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                if (entry.getKey().isValidValue(entry.getValue()) == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Validates that the value of each field in the map is within its valid range
         * throwing an exception if not.
         * <p>
         * The validation simply checks that each value in the field map is within the
         * normal range for the field as defined by {@link DateTimeFieldRule#checkValue(int)}.
         * No cross-validation between fields is performed, thus the field map could
         * contain an invalid date such as February 31st.
         *
         * @return this, for method chaining, never null
         * @throws IllegalCalendarFieldValueException if any field is invalid
         */
        public FieldMap validate() {
            for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
                entry.getKey().checkValue(entry.getValue());
            }
            return this;
        }

        //-----------------------------------------------------------------------
        /**
         * Checks the calendrical fields for consistency.
         * <p>
         * This method ensures that each field in the map is consistent with each other field.
         * For example, if the map contains 'HourOfDay' and 'AmPm' then the AM/PM value must
         * be correct for the hour of day.
         *
         * @throws InvalidCalendarFieldException if any field is inconsistent
         */
        public void checkConsistent() {
            Iterator<Entry<DateTimeFieldRule, Integer>> it = fieldValueMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<DateTimeFieldRule, Integer> entry = it.next();
                DateTimeFieldRule fieldRule = entry.getKey();
                Integer derivedValue = fieldRule.deriveValue(this);
                if (derivedValue != null) {
                    Integer mapValue = entry.getValue();
                    if (derivedValue.equals(mapValue) == false) {
                        throw new InvalidCalendarFieldException("Calendrical contains field map value " +
                                fieldRule.getID() + "=" + mapValue + " that is inconsistent with value " +
                                derivedValue + " derived from another field", fieldRule);
                    }
                }
            }
        }

        /**
         * Removes any field from the map that can be derived from another field.
         * No check is performed that the removed field was consistent with the
         * remaining value.
         * <p>
         * For example, if the field map contains 'MonthOfYear' and the date is
         * non-null, then the month will be removed from the map.
         */
        public void removeDerivable() {
            Iterator<DateTimeFieldRule> it = fieldValueMap.keySet().iterator();
            while (it.hasNext()) {
                Integer derivedValue = it.next().deriveValue(this);
                if (derivedValue != null) {
                    it.remove();
                }
            }
        }

        //-----------------------------------------------------------------------
        /**
         * Converts this instance to a map.
         *
         * @return a modifiable copy of the field-value map, never null
         */
        public Map<DateTimeFieldRule, Integer> toFieldValueMap() {
            Map<DateTimeFieldRule, Integer> cloned =
                new TreeMap<DateTimeFieldRule, Integer>(Collections.reverseOrder());
            cloned.putAll(fieldValueMap);
            return cloned;
        }

        //-----------------------------------------------------------------------
        /**
         * Is this field map equal to the specified map.
         *
         * @param obj  the other field set to compare to, null returns false
         * @return true if this instance is equal to the specified field set
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof FieldMap) {
                FieldMap other = (FieldMap) obj;
                return fieldValueMap.equals(other.fieldValueMap);
            }
            return false;
        }

        /**
         * A hashcode for this set of fields.
         *
         * @return a suitable hashcode
         */
        @Override
        public int hashCode() {
            return fieldValueMap.hashCode();
        }

        //-----------------------------------------------------------------------
        /**
         * Outputs the set of fields as a <code>String</code>.
         * <p>
         * The output will consist of the field-value map in standard map format.
         *
         * @return the formatted date-time string, never null
         */
        @Override
        public String toString() {
            return fieldValueMap.toString();
        }
    }

    /**
     * Stateful helper used during the calendrical merge process.
     * <p>
     * Instances of this class are used internally when merging a field-value map
     * into a calendrical. The class is public as it is possible for additional
     * date/time fields to be created and used by subclassing {@link DateTimeFieldRule}.
     * <p>
     * The merger is a view of the underlying calendrical.
     * Changes to this instance affect the calendrical which created it.
     * <p>
     * Calendrical.Merger is mutable and not thread-safe.
     * It must only be used from a single thread and must not be passed between threads.
     *
     * @author Michael Nascimento Santos
     * @author Stephen Colebourne
     */
    public static final class Merger {

        /**
         * The calendrical being merged, never null.
         */
        private final Calendrical calendrical;
        /**
         * The merge context to use.
         */
        private final CalendricalContext context;
        /**
         * The time inclusing number of days overflow.
         */
        private LocalTime.Overflow mergedTime;
        /**
         * The fields that have been processed so far.
         */
        private final Set<DateTimeFieldRule> processedFieldSet = new HashSet<DateTimeFieldRule>();
        /**
         * Current iterator, updated when the state of the map is changed.
         */
        private Iterator<DateTimeFieldRule> iterator;

        /**
         * Constructs an instance using a specific context.
         *
         * @param calendrical  the calendrical to merge, not null
         * @param context  the context to use, not null
         */
        private Merger(Calendrical calendrical, CalendricalContext context) {
            ISOChronology.checkNotNull(calendrical, "The calendrical must not be null");
            ISOChronology.checkNotNull(context, "The calendrical context must not be null");
            this.calendrical = calendrical;
            this.context = context;
        }

        //-----------------------------------------------------------------------
//        /**
//         * Gets the underlying calendrical that is being merged.
//         * <p>
//         * This should not normally be changed during the merge process, as this
//         * will have unexpected side effects.
//         *
//         * @return the original field-value map being merged, never null
//         */
//        public Calendrical getCalendrical() {
//            return calendrical;
//        }

        /**
         * Gets the calendrical context in use for the merge.
         *
         * @return the calendrical context, never null
         */
        public CalendricalContext getContext() {
            return context;
        }

        /**
         * Checks if the merge is strict.
         *
         * @return true if the merge is strict
         */
        public boolean isStrict() {
            return context.isStrict();
        }

//        /**
//         * Gets the set of fields that have been processed so far.
//         * <p>
//         * These are the fields that have been merged into a date, time or other fields.
//         * The set may contain fields that are not in the original field-value map.
//         *
//         * @return the processed field set, modifiable copy, not null
//         */
//        public Set<DateTimeFieldRule> getProcessedFieldSet() {
//            return new HashSet<DateTimeFieldRule>(processedFieldSet);
//        }

        //-----------------------------------------------------------------------
        /**
         * Gets the value for the specified field, returning null if the field is not present.
         * <p>
         * This obtains the value for the field as defined in the underlying field-value map.
         * If the map does not define the field, null is returned.
         * <p>
         * The underlying field-value map is unvalidated and can contain out of range
         * values, such as a day of month of -3 or an hour of 1000.
         * If the context is strict, then the result of this method will be validated
         * before it is returned. This ensures that the value will be between the
         * minimum and maximum values for that field.
         *
         * @param fieldRule  the rule to query from the map, not null
         * @return the value mapped to the specified field, null if not present
         * @throws IllegalCalendarFieldValueException if the merge is strict and the value is invalid
         */
        public Integer getValue(DateTimeFieldRule fieldRule) {
            return calendrical.getFieldMap().get(fieldRule);
        }

        /**
         * Gets the value for the specified field, throwing an exception if the field is not present.
         * <p>
         * This obtains the value for the field as defined in the underlying field-value map.
         * If this object does not define the field an exception is thrown.
         * <p>
         * The underlying field-value map is unvalidated and can contain out of range
         * values, such as a day of month of -3 or an hour of 1000.
         * If the context is strict, then the result of this method will be validated
         * before it is returned. This ensures that the value will be between the
         * minimum and maximum values for that field.
         *
         * @param fieldRule  the rule to query from the map, not null
         * @return the value mapped to the specified field
         * @throws UnsupportedCalendarFieldException if the field is not supported
         */
        public int getValueInt(DateTimeFieldRule fieldRule) {
            return calendrical.getFieldMap().getInt(fieldRule);
        }

        //-----------------------------------------------------------------------
        /**
         * Marks a field that has been processed to the list.
         * <p>
         * The merge process needs to keep track of those fields that are merged
         * at each stage. This is done when the field rule calls this method.
         * For example, if fields A and B are merged to produce C, then this
         * method must be called twice, passing in the values A and B.
         *
         * @param fieldRule  the field to mark as processed, not null
         */
        public void markFieldAsProcessed(DateTimeFieldRule fieldRule) {
            ISOChronology.checkNotNull(fieldRule, "Field rule must not be null");
            processedFieldSet.add(fieldRule);
        }

        /**
         * Stores the merged date checking that it matches any previously stored date.
         * <p>
         * The ultimate aim of the merge process for date fields is to produce a date.
         * When the merge results in a date then this method must be called.
         * For example, when Year and DayOfYear are merged, the result is a date
         * and that is stored by calling this method.
         * <p>
         * It is possible that the field-value map contains multiple hierarchies that
         * can produce a date. In this case, all the hierarchies must produce the same
         * date, somthing which is validated by this method.
         *
         * @param date  the date to set, not null
         * @throws CalendricalException if the input date does not match a previously stored date
         */
        public void storeMergedDate(LocalDate date) {
            ISOChronology.checkNotNull(date, "Date must not be null");
            LocalDate storedDate = calendrical.getDate();
            if (storedDate != null && storedDate.equals(date) == false) {
                throw new CalendricalException("Merge resulted in two different dates, " + storedDate + " and " + date);
            }
            calendrical.setDate(date);
            // no need to reset iterator, as store of date should not enable any more calculations
        }

        /**
         * Stores the merged time checking that it matches any previously stored
         * time ignoring the number of days overflow.
         *
         * @param time  the time to set, may be null
         * @throws CalendricalException if the input time does not match a previously stored time
         */
        public void storeMergedTime(LocalTime time) {
            ISOChronology.checkNotNull(time, "Time must not be null");
            LocalTime storedTime = calendrical.getTime();
            if (storedTime != null && storedTime.equals(time) == false) {
                throw new CalendricalException("Merge resulted in two different times, " + storedTime + " and " + time);
            }
            calendrical.setTime(time);
            // no need to reset iterator, as store of date should not enable any more calculations
        }

        /**
         * Stores the merged time checking that it matches any previously stored
         * time including the number of days overflow.
         *
         * @param time  the time to set, may be null
         * @throws CalendricalException if the input time does not match a previously stored time
         */
        public void storeMergedTime(LocalTime.Overflow time) {
            ISOChronology.checkNotNull(time, "Time must not be null");
            if (mergedTime != null && mergedTime.equals(time) == false) {
                throw new CalendricalException("Merge resulted in two different times, " + mergedTime + " and " + time);
            }
            storeMergedTime(time.getResultTime());
            mergedTime = time;
            // no need to reset iterator, as store of date should not enable any more calculations
        }

        /**
         * Stores a field-value pair into this map ensuring that it does not clash
         * with any previous value defined for that field.
         * <p>
         * This method adds the specified field-value pair to the map.
         * If this instance already has a value for the field then the value is checked
         * to see if it is the same with an exception being thrown if it is not.
         * If this instance does not hold the field already, then the value is simply added.
         * <p>
         * DateTimeFieldMap is an unvalidated map of field to value.
         * The value specified may be outside the normal valid range for the field.
         * For example, you could setup a map with a day of month of -3.
         *
         * @param fieldRule  the field to store, not null
         * @param value  the value to store
         * @throws CalendricalException if the input field does not match a previously stored field
         */
        public void storeMergedField(DateTimeFieldRule fieldRule, int value) {
            ISOChronology.checkNotNull(fieldRule, "Field rule must not be null");
            Integer oldValue = calendrical.getFieldMap().get(fieldRule);
            if (oldValue != null) {
                if (oldValue.intValue() != value) {
                    throw new InvalidCalendarFieldException("Merge resulted in two different values, " + value +
                            " and " + oldValue + ", for " + fieldRule.getID() + " within fields " +
                            calendrical.getFieldMap(), fieldRule);
                } else {
                    return;  // no change
                }
            }
            calendrical.getFieldMap().put(fieldRule, value);
            iterator = calendrical.getFieldMap().iterator();  // restart the iterator
        }

        //-----------------------------------------------------------------------
        /**
         * Merges the fields to extract the maximum possible date, time and offset information.
         * <p>
         * The merge process aims to extract the maximum amount of information
         * possible from this set of fields. Ideally the outcome will be a date, time
         * or both, however there may be insufficient information to achieve this.
         * <p>
         * The process repeatedly calls the field rule {@link DateTimeFieldRule#merge merge}
         * method to perform the merge on each individual field. Sometimes two or
         * more fields will combine to form a more significant field. Sometimes they
         * will combine to form a date or time. The process stops when there no more
         * merges can occur.
         * <p>
         * The process is based around hierarchies that can be combined.
         * For example, QuarterOfYear and MonthOfQuarter can be combined to form MonthOfYear.
         * Then, MonthOfYear can be combined with DayOfMonth and Year to form a date.
         * Any fields which take part in a merge will be removed from the result as their
         * values can be derived from the merged field.
         * <p>
         * The exact definition of which fields combine with which is chronology dependent.
         * For example, see {@link ISOChronology}.
         * <p>
         * The details of the process are controlled by the merge context.
         * This includes strict/lenient behaviour.
         * <p>
         * The merge must result in consistent values for each field, date and time.
         * If two different values are produced an exception is thrown.
         * For example, both Year/MonthOfYear/DayOfMonth and Year/DayOfYear will merge to form a date.
         * If both sets of fields do not produce the same date then an exception will be thrown.
         *
         * @throws CalendricalException if the merge cannot be completed successfully
         */
        void merge() {
            // it is essential to validate for consistency, as there is no way to
            // reliably determine which is the more significant or original value
            
            // exit quick in simple case
            if (calendrical.getFieldMap().size() > 0) {
                // strict must pre-validate fields for out of range problems
                if (isStrict()) {
                    calendrical.getFieldMap().validate();
                }
                
                // we keep all the fields in the map during the merge and only remove at the end
                // once merged, the initial fields can be derived from the merged fields
                mergeFieldsLoop();
                mergeDateTime();
                
                // remove the fields that have been merged into more significant fields
                calendrical.getFieldMap().removeAll(processedFieldSet);
                
                // check and remove any remaining less significant that can be derived from
                // the new set of more significant fields
                if (getContext().isCheckUnusedFields()) {
                    calendrical.checkConsistent();
                }
                
                // add days overflow
                if (calendrical.getDate() != null && mergedTime != null && mergedTime.getOverflowDays() > 0) {
                    calendrical.setDate(calendrical.getDate().plusDays(mergedTime.getOverflowDays()));
                }
                
                // remove derivable fields
                calendrical.removeDerivable();
            }
        }

        /**
         * Performs the loop to merge the fields.
         *
         * @throws CalendricalException if the merge cannot be completed successfully
         */
        private void mergeFieldsLoop() {
            iterator = calendrical.getFieldMap().iterator();
            int protect = 0;  // avoid infinite looping
            while (iterator.hasNext() && protect < 100) {
                iterator.next().mergeFields(this);
                protect++;
            }
            if (iterator.hasNext()) {
                throw new CalendricalException("Merge fields failed, infinite loop blocked, " +
                        "probably caused by an incorrectly implemented field rule");
            }
        }

        /**
         * Performs the loop to merge the fields to date/time.
         *
         * @throws CalendricalException if the merge cannot be completed successfully
         */
        private void mergeDateTime() {
            iterator = calendrical.getFieldMap().iterator();
            while (iterator.hasNext()) {
                iterator.next().mergeDateTime(this);
            }
        }
    }
}
