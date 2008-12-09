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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * A set of date-time fields.
 * <p>
 * Instances of this class store a map of field-value pairs.
 * Together these specify constrints on the dates and times that match.
 * For example, if an instance stores 'DayOfMonth=13' and 'DayOfWeek=Friday'
 * then it represents and matches only dates of Friday the Thirteenth.
 * <p>
 * All the values will be within the valid range for the field.
 * However, there is no cross validation between fields.
 * Thus, it is possible for the date-time represented to never exist.
 * For exampl, if an instance stores 'DayOfMonth=31' and 'MonthOfYear=February'
 * then there will never be a matching date.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeFields
        implements CalendricalProvider,
            DateMatcher, TimeMatcher, Iterable<DateTimeFieldRule>, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** A singleton empty field set, placing no restrictions on the date-time. */
    private static final DateTimeFields EMPTY = new DateTimeFields(createMap());

    /**
     * The date time map, never null, may be empty.
     */
    private final TreeMap<DateTimeFieldRule, Integer> fieldValueMap;

    /**
     * Obtains an empty instance of <code>DateTimeFields</code>.
     *
     * @return a DateTimeFields object, never null
     */
    public static DateTimeFields fields() {
        return EMPTY;
    }

    /**
     * Obtains an instance of <code>DateTimeFields</code> from a field-value pair.
     * <p>
     * A field set can hold state that is not a valid date-time.
     * Thus, this constructor does not check to see if the value is valid for the field.
     * For example, you could setup a calendrical with a day of month of 75.
     *
     * @param fieldRule  the rule, not null
     * @param value  the field value, may be invalid
     * @return a DateTimeFields object, never null
     * @throws NullPointerException if the field rule is null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static DateTimeFields fields(DateTimeFieldRule fieldRule, int value) {
        checkNotNull(fieldRule, "The field rule must not be null");
        fieldRule.checkValue(value);
        TreeMap<DateTimeFieldRule, Integer> map = createMap();
        map.put(fieldRule, value);
        return new DateTimeFields(map);
    }

    /**
     * Obtains an instance of <code>DateTimeFields</code> from two field-value pairs.
     * <p>
     * A field set can hold state that is not a valid date-time.
     * Thus, this constructor does not check to see if the value is valid for the field.
     * For example, you could setup a calendrical with a day of month of 75.
     *
     * @param fieldRule1  the first rule, not null
     * @param value1  the first field value
     * @param fieldRule2  the second rule, not null
     * @param value2  the second field value
     * @return a DateTimeFields object, never null
     * @throws NullPointerException if either field rule is null
     * @throws IllegalCalendarFieldValueException if either value is invalid
     */
    public static DateTimeFields fields(DateTimeFieldRule fieldRule1, int value1, DateTimeFieldRule fieldRule2, int value2) {
        checkNotNull(fieldRule1, "The first field rule must not be null");
        checkNotNull(fieldRule2, "The second field rule must not be null");
        fieldRule1.checkValue(value1);
        fieldRule2.checkValue(value2);
        TreeMap<DateTimeFieldRule, Integer> map = createMap();
        map.put(fieldRule1, value1);
        map.put(fieldRule2, value2);
        return new DateTimeFields(map);
    }

    /**
     * Constructor creating a calendrical from date-time fields, offset and zone.
     * <p>
     * A field set can hold state that is not a valid date-time.
     * This constructor does not check to see if the value is valid for the field.
     * For example, you could setup a calendrical with a day of month of 75.
     *
     * @param fieldValueMap  a map of fields that will be used to create a field set,
     *  not updated by this factory, not null, contains no nulls
     * @return a DateTimeFields object, never null
     * @throws NullPointerException if the map contains null keys or values
     * @throws IllegalCalendarFieldValueException if any value is invalid
     */
    public static DateTimeFields fields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
        checkNotNull(fieldValueMap, "The field-value map must not be null");
        if (fieldValueMap.isEmpty()) {
            return EMPTY;
        }
        // don't use contains() as tree map and others can throw NPE
        TreeMap<DateTimeFieldRule, Integer> map = createMap();
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            DateTimeFieldRule fieldRule = entry.getKey();
            Integer value = entry.getValue();
            checkNotNull(fieldRule, "Null keys are not permitted in field-value map");
            checkNotNull(value, "Null values are not permitted in field-value map");
            fieldRule.checkValue(value);
            map.put(fieldRule, value);
        }
        return new DateTimeFields(map);
    }

    /**
     * Creates a new empty map.
     *
     * @return ordered representation of internal map
     */
    private static TreeMap<DateTimeFieldRule, Integer> createMap() {
        return new TreeMap<DateTimeFieldRule, Integer>(Collections.reverseOrder());
    }

    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    private static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     * @param assignedMap  the map of fields, which is assigned, not null
     */
    private DateTimeFields(TreeMap<DateTimeFieldRule, Integer> assignedMap) {
        fieldValueMap = assignedMap;
    }

    /**
     * Ensure EMPTY singleton.
     *
     * @return the resolved instance
     * @throws ObjectStreamException if an error occurs
     */
    private Object readResolve() throws ObjectStreamException {
        return fieldValueMap.isEmpty() ? EMPTY : this;
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
     * {@link #getValueInt(DateTimeFieldRule)}.
     *
     * @return an iterator over the fields in this object, never null
     */
    public Iterator<DateTimeFieldRule> iterator() {
        return fieldValueMap.keySet().iterator();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if a value can be obtained for the specified field.
     *
     * @param fieldRule  the field to query, null returns false
     * @return true if the field is supported, false otherwise
     */
    public boolean isSupported(DateTimeFieldRule fieldRule) {
        if (fieldRule == null) {
            return false;
        }
        return fieldValueMap.containsKey(fieldRule);
    }

    /**
     * Gets the value for the specified field returning null if the field is
     * not in the field-value map.
     * <p>
     * The value will be within the valid range for the field.
     * No cross-validation between fields is performed.
     *
     * @param fieldRule  the rule to query from the map, null returns null
     * @return the value mapped to the specified field, null if not present
     */
    public Integer getValue(DateTimeFieldRule fieldRule) {
        if (fieldRule == null) {
            return null;
        }
        return fieldValueMap.get(fieldRule);
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value will be within the valid range for the field.
     * No cross-validation between fields is performed.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     */
    public int getValueInt(DateTimeFieldRule fieldRule) {
        checkNotNull(fieldRule, "The field rule must not be null");
        Integer value = fieldValueMap.get(fieldRule);
        if (value == null) {
            throw new UnsupportedCalendarFieldException(fieldRule, "DateTimeFields");
        }
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateTimeFields with the specified field value.
     * <p>
     * If this instance already has a value for the field then the value is replaced.
     * Otherwise the value is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fieldRule  the field to set in the returned set of fields, not null
     * @param value  the value to set in the returned set of fields
     * @return a new, updated DateTimeFields, never null
     * @throws NullPointerException if the field rule is null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public DateTimeFields withFieldValue(DateTimeFieldRule fieldRule, int value) {
        checkNotNull(fieldRule, "The field rule must not be null");
        fieldRule.checkValue(value);
        TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
        clonedMap.put(fieldRule, value);
        return new DateTimeFields(clonedMap);
    }

    /**
     * Returns a copy of this DateTimeFields with the fields from the specified set added.
     * <p>
     * If this instance already has a value for the field then the value is replaced.
     * Otherwise the value is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fieldValueMap  the new map of fields, not null
     * @return a new, updated DateTimeFields, never null
     * @throws NullPointerException if the map contains null keys or values
     * @throws IllegalCalendarFieldValueException if any value is invalid
     */
    public DateTimeFields withFields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
        checkNotNull(fieldValueMap, "The field-value map must not be null");
        if (fieldValueMap.isEmpty()) {
            return this;
        }
        // don't use contains() as tree map and others can throw NPE
        TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            DateTimeFieldRule fieldRule = entry.getKey();
            Integer value = entry.getValue();
            checkNotNull(fieldRule, "Null keys are not permitted in field-value map");
            checkNotNull(value, "Null values are not permitted in field-value map");
            fieldRule.checkValue(value);
            clonedMap.put(fieldRule, value);
        }
        return new DateTimeFields(clonedMap);
    }

    /**
     * Returns a copy of this DateTimeFields with the fields from the specified set added.
     * <p>
     * If this instance already has a value for the field then the value is replaced.
     * Otherwise the value is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fields  the field set to add to the returned instance, not null
     * @return a new, updated DateTimeFields, never null
     */
    public DateTimeFields withFields(DateTimeFields fields) {
        checkNotNull(fields, "The field-set must not be null");
        if (fields.size() == 0 || fields == this) {
            return this;
        }
        TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
        clonedMap.putAll(fields.fieldValueMap);
        return new DateTimeFields(clonedMap);
    }

    /**
     * Returns a copy of this DateTimeFields with the specified field removed.
     * <p>
     * If this instance does not contain the field then the returned instance
     * is the same as this one.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fieldRule  the field to set in the returned set of fields, not null
     * @return a new, updated DateTimeFields, never null
     */
    public DateTimeFields withFieldRemoved(DateTimeFieldRule fieldRule) {
        checkNotNull(fieldRule, "The field rule must not be null");
        TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
        if (clonedMap.remove(fieldRule) == null) {
            return this;
        }
        return clonedMap.isEmpty() ? EMPTY : new DateTimeFields(clonedMap);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Merges the fields in this map to form a calendrical.
//     * <p>
//     * The merge process aims to extract the maximum amount of information
//     * possible from this set of fields. Ideally the outcome will be a date, time
//     * or both, however there may be insufficient information to achieve this.
//     * <p>
//     * The process repeatedly calls the field rule {@link DateTimeFieldRule#merge merge}
//     * method to perform the merge on each individual field. Sometimes two or
//     * more fields will combine to form a more significant field. Sometimes they
//     * will combine to form a date or time. The process stops when there no more
//     * merges can occur.
//     * <p>
//     * The process is based around hierarchies that can be combined.
//     * For example, QuarterOfYear and MonthOfQuarter can be combined to form MonthOfYear.
//     * Then, MonthOfYear can be combined with DayOfMonth and Year to form a date.
//     * Any fields which take part in a merge will be removed from the result as their
//     * values can be derived from the merged field.
//     * <p>
//     * The exact definition of which fields combine with which is chronology dependent.
//     * For example, see {@link ISOChronology}.
//     * <p>
//     * The details of the process are controlled by the merge context.
//     * This includes strict/lenient behaviour.
//     * <p>
//     * The merge must result in consistent values for each field, date and time.
//     * If two different values are produced an exception is thrown.
//     * For example, both Year/MonthOfYear/DayOfMonth and Year/DayOfYear will merge to form a date.
//     * If both sets of fields do not produce the same date then an exception will be thrown.
//     *
//     * @return the new instance, with merged fields, never null
//     * @throws CalendricalException if the fields cannot be merged
//     */
//    public Calendrical mergeStrict() {
//        if (fieldValueMap.size() == 0) {
//            return new Calendrical();
//        }
//        CalendricalMerger merger = new CalendricalMerger(this, new CalendricalContext(true, true));
//        merger.merge();
//        return merger.toCalendrical();
//    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the date fields in this set of fields match the specified date.
     * <p>
     * This implementation checks that all date fields in this field set match the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date fields match, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        checkNotNull(date, "The date to match against must not be null");
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            Integer dateValue = entry.getKey().getValueQuiet(date, null);
            if (dateValue != null && dateValue.equals(entry.getValue()) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the time fields in this set of fields match the specified time.
     * <p>
     * This implementation checks that all time fields in this field set match the input time.
     *
     * @param time  the time to match, not null
     * @return true if the time fields match, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        checkNotNull(time, "The time to match against must not be null");
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            Integer timeValue = entry.getKey().getValueQuiet(null, time);
            if (timeValue != null && timeValue.equals(entry.getValue()) == false) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field set to a map of fields to values.
     * <p>
     * The map will never be null, however it may be empty.
     * The values contained in the map might be out of range for the rule.
     * <p>
     * For example, the day of month might be set to 75, or the hour to 1000.
     * The purpose of this class is simply to store the values, not to provide
     * any guarantees as to their validity.
     *
     * @return a modifiable copy of the field-value map, never null
     */
    public Map<DateTimeFieldRule, Integer> toFieldValueMap() {
        return new HashMap<DateTimeFieldRule, Integer>(fieldValueMap);
    }

    /**
     * Clones the field-value map.
     *
     * @return a clone of the field-value map, never null
     */
    private TreeMap<DateTimeFieldRule, Integer> clonedMap() {
        TreeMap<DateTimeFieldRule, Integer> cloned = createMap();
        cloned.putAll(fieldValueMap);
        return cloned;
    }

    /**
     * Copies the field-value map into the specified map.
     *
     * @param map  the map to copy into, not null
     */
    void copyInto(Map<DateTimeFieldRule, Integer> map) {
        map.putAll(fieldValueMap);
    }

//    /**
//     * Converts this object to a LocalDate.
//     * <p>
//     * This method will validate and merge the fields to create a date.
//     * This merge process is strict as defined by {@link #mergeToDate}.
//     *
//     * @return the merged LocalDate, never null
//     * @throws IllegalCalendarFieldValueException if any field is out of the valid range
//     * @throws CalendarConversionException if the merge creates more than one date and the dates differ
//     * @throws CalendarConversionException if there is insufficient information to create a date
//     * @throws InvalidCalendarFieldException if one of the fields does not match the merged date
//     */
//    public LocalDate toLocalDate() {
//        LocalDate date = mergeToDate(true, true);
//        if (date == null) {
//            throw new CalendarConversionException(
//                "Cannot convert DateTimeFields to LocalDate, insufficient infomation to create a date");
//        }
//        return date;
//    }
//
//    /**
//     * Converts this object to a LocalTime.
//     * <p>
//     * This method will validate and merge the fields to create a time.
//     * This merge process is strict as defined by {@link #mergeToTime}.
//     *
//     * @return the merged LocalTime, never null
//     * @throws IllegalCalendarFieldValueException if any field is out of the valid range
//     * @throws CalendarConversionException if the merge creates more than one date and the dates differ
//     * @throws CalendarConversionException if there is insufficient information to create a date
//     * @throws InvalidCalendarFieldException if one of the fields does not match the merged date
//     */
//    public LocalTime toLocalTime() {
//        LocalTime time = mergeToTime(true, true);
//        if (time == null) {
//            throw new CalendarConversionException(
//                "Cannot convert DateTimeFields to LocalTime, insufficient infomation to create a time");
//        }
//        return time;
//    }
//
//    /**
//     * Converts this object to a LocalDateTime.
//     * <p>
//     * This method will validate and merge the fields to create a date-time.
//     * This merge process is strict as defined by {@link #mergeToDateTime}.
//     *
//     * @return the LocalDateTime, never null
//     * @throws InvalidCalendarFieldException if any field is invalid
//     * @throws CalendarConversionException if the date or time cannot be converted
//     */
//    public LocalDateTime toLocalDateTime() {
//        LocalDateTime dateTime = mergeToDateTime(true, true);
//        if (dateTime == null) {
//            throw new CalendarConversionException(
//                "Cannot convert DateTimeFields to LocalTime, insufficient infomation to create a date-time");
//        }
//        return dateTime;
//    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a calendrical with the same set of fields.
     *
     * @return the calendrical with the same set of fields, never null
     */
    public Calendrical toCalendrical() {
        return new Calendrical(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this set of fields equal to the specified set.
     *
     * @param obj  the other field set to compare to, null returns false
     * @return true if this instance is equal to the specified field set
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DateTimeFields) {
            DateTimeFields other = (DateTimeFields) obj;
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
