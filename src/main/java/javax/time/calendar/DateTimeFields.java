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
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * A set of date-time fields which may or may not be valid.
 * <p>
 * This class holds a set of field-value pairs which represent a full or partial
 * view of a date, time or date-time. Each value might be invalid, thus
 * for example a month is not limited to the normal range of 1 to 12.
 * Instances must therefore be treated with care.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeFields
        implements CalendricalProvider, DateProvider, TimeProvider,
            DateMatcher, TimeMatcher, DateTimeProvider, Iterable<DateTimeFieldRule>, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** A singleton empty field set, placing no restrictions on the date-time. */
    private static final DateTimeFields EMPTY = new DateTimeFields(createMap());

    /**
     * The date time map, never null, may be empty.
     */
    private final Map<DateTimeFieldRule, Integer> fieldValueMap;

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
     * @throws NullPointerException if the field is null
     */
    public static DateTimeFields fields(DateTimeFieldRule fieldRule, int value) {
        checkNotNull(fieldRule, "The field rule must not be null");
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
     * @throws NullPointerException if either field is null
     */
    public static DateTimeFields fields(DateTimeFieldRule fieldRule1, int value1, DateTimeFieldRule fieldRule2, int value2) {
        checkNotNull(fieldRule1, "The first field rule must not be null");
        checkNotNull(fieldRule2, "The second field rule must not be null");
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
     */
    public static DateTimeFields fields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
        checkNotNull(fieldValueMap, "The field-value map must not be null");
        if (fieldValueMap.isEmpty()) {
            return EMPTY;
        }
        // don't use contains() as tree map and others can throw NPE
        TreeMap<DateTimeFieldRule, Integer> map = createMap();
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            DateTimeFieldRule key = entry.getKey();
            Integer value = entry.getValue();
            checkNotNull(key, "Null keys are not permitted in field-value map");
            checkNotNull(value, "Null values are not permitted in field-value map");
            map.put(key, value);
        }
        return new DateTimeFields(map);
    }

    /**
     * Creates a new empty map.
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
     * Checks if a value can be obtained for the specified field.
     * <p>
     * This method does not check if the value returned would be valid.
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
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value will be checked for basic validity.
     * The value returned will be within the valid range for the field.
     * <p>
     * Instances of DateTimeFields can hold invalid field values, such as
     * a day of month of -3 or an hour of 1000. This method ensures that
     * the result is within the valid range for the field.
     * No cross-validation between fields is performed.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public int getValue(DateTimeFieldRule fieldRule) {
        return getValue(fieldRule, true);
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value is optionally checked for basic validity.
     * <p>
     * Instances of DateTimeFields can hold invalid field values, such as
     * a day of month of -3 or an hour of 1000. This method optionally ensures
     * that the result is within the valid range for the field.
     * No cross-validation between fields is performed.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @param validate  true to validate the value, false to return the raw value
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     * @throws IllegalCalendarFieldValueException if validation is performed and the value is invalid
     */
    public int getValue(DateTimeFieldRule fieldRule, boolean validate) {
        checkNotNull(fieldRule, "The field rule must not be null");
        Integer value = fieldValueMap.get(fieldRule);
        if (value != null) {
            if (validate) {
                fieldRule.checkValue(value);
            }
            return value;
        }
        throw new UnsupportedCalendarFieldException(fieldRule, "DateTimeFields");
    }

    /**
     * Gets the value for the specified field returning null if the field is
     * not in the field-value map.
     * <p>
     * The value is not validated and might be out of range for the rule.
     * <p>
     * Instances of DateTimeFields can hold invalid field values, such as
     * a day of month of -3 or an hour of 1000. This method performs no
     * validation on the returned value.
     *
     * @param fieldRule  the rule to query from the map, null returns null
     * @return the value mapped to the specified field, null if not present
     */
    public Integer getValueQuiet(DateTimeFieldRule fieldRule) {
        if (fieldRule == null) {
            return null;
        }
        return fieldValueMap.get(fieldRule);
    }

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
     * around the fields using the for-each loop. The values can be obtained
     * using {@link #getValue(DateTimeFieldRule)}.
     *
     * @return an iterator over the fields in this object, never null
     */
    public Iterator<DateTimeFieldRule> iterator() {
        return fieldValueMap.keySet().iterator();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateTimeFields with the specified field value.
     * <p>
     * If this instance already has a value for the field then the value is
     * replaced. Otherwise the value is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fieldRule  the field to set in the returned set of fields, not null
     * @param value  the value to set in the returned set of fields
     * @return a new, updated DateTimeFields, never null
     */
    public DateTimeFields withFieldValue(DateTimeFieldRule fieldRule, int value) {
        checkNotNull(fieldRule, "The field rule must not be null");
        TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
        clonedMap.put(fieldRule, value);
        return new DateTimeFields(clonedMap);
    }

    /**
     * Returns a copy of this DateTimeFields with the fields from the specified set added.
     * <p>
     * If this instance already has a value for the field then the value is
     * replaced. Otherwise the value is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fieldValueMap  the new map of fields, not null
     * @return a new, updated DateTimeFields, never null
     * @throws IllegalArgumentException if the map contains null keys or values
     */
    public DateTimeFields withFields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
        checkNotNull(fieldValueMap, "The field-value map must not be null");
        if (fieldValueMap.isEmpty()) {
            return this;
        }
        // don't use contains() as tree map and others can throw NPE
        TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            DateTimeFieldRule key = entry.getKey();
            Integer value = entry.getValue();
            checkNotNull(key, "Null keys are not permitted in field-value map");
            checkNotNull(value, "Null values are not permitted in field-value map");
            clonedMap.put(key, value);
        }
        return new DateTimeFields(clonedMap);
    }

    /**
     * Returns a copy of this DateTimeFields with the fields from the specified set added.
     * <p>
     * If this instance already has a value for the field then the value is
     * replaced. Otherwise the value is added.
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

    //-----------------------------------------------------------------------
    /**
     * Merges the fields combining groups of less significant fields into
     * more significant ones.
     * <p>
     * The merge process is designed to take combinations of less significant
     * fields and merge them. For example, the AM/PM field and the hour of AM/PM
     * field could be merged to the hour of day field. The exact hierarchy as to
     * which fields are more significant than others is chronology dependent.
     *
     * @return the new instance, with merged fields, never null
     * @throws CalendarFieldException if a value cannot be merged as the value is invalid
     */
    public DateTimeFields mergeFields() {
        if (fieldValueMap.size() > 1) {
            TreeMap<DateTimeFieldRule, Integer> clonedMap = clonedMap();
            for (DateTimeFieldRule fieldRule : fieldValueMap.keySet()) {
                fieldRule.mergeFields(clonedMap);
            }
            if (clonedMap.equals(fieldValueMap) == false) {
                return new DateTimeFields(clonedMap).mergeFields();
            }
        }
        return this;
    }

    /**
     * Validates that the value of each field is within its valid range.
     * <p>
     * The validation simply checks that each value is within the normal range
     * for the field as defined by {@link DateTimeFieldRule#checkValue(int)}.
     * No cross-validation between fields is performed, thus the field set could
     * contain an invalid date such as February 31st.
     *
     * @return this, for chaining, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public DateTimeFields validateFields() {
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            entry.getKey().checkValue(entry.getValue());
        }
        return this;
    }

    /**
     * Checks if the value of each field is within its valid range.
     * <p>
     * The validation simply checks that each value is within the normal range
     * for the field as defined by {@link DateTimeFieldRule#isValidValue(int)}.
     * No cross-validation between fields is performed, thus the field set could
     * contain an invalid date such as February 31st.
     *
     * @return true if all the fields are with in their valid range
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public boolean isValidFieldValues() {
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            if (entry.getKey().isValidValue(entry.getValue()) == false) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the fields to create a date.
     * <p>
     * This method will merge any complete set of primary fields into a date.
     * It is normal practice to call {@link #mergeFields()} before calling this
     * method to ensure that less significant fields are already merged into
     * the primary fields.
     * <p>
     * For example, calling <code>mergeFields</code> would merge the
     * quarterOfYear and monthOfQuarter fields to form the primary field
     * of monthOfYear. This method would then take year, monthOfYear and
     * dayOfMonth fields to form a date.
     * <p>
     * It is possible that this field set contains two sets of primary fields.
     * For example, if this instance contains year, monthOfYear, dayOfMonth and
     * dayOfYear, there are two sets of primary fields (year/month/dayOfMonth
     * and year/dayOfYear). Both sets of primary fields must merge to the
     * same date or an exception is thrown.
     * <p>
     * This merge process is lenient. For example, if the month is 13 then this
     * will resolve to month 1 of the following year (assuming ISO chronology rules).
     * <p>
     * Combining this method with others will make it more strict.
     * <ul>
     * <li>Lenient - call <code>fields.mergeFields().mergeToDate()</code></li>
     * <li>Semi-lenient - call <code>fields.mergeFields().validateFields().mergeToDate()</code>
     *  which ensures that each field is only lenient within its range of valid values</li>
     * <li>Strict - call as per semi-lenient and then check the resulting non-null
     *  date with <code>fields.matchesDate(date)</code></li>
     * </ul>
     *
     * @return the LocalDate, null if there is insufficient information to create a date
     * @throws CalendarFieldException if a field cannot be merged
     * @throws CalendarConversionException if the merge creates more than one date and the dates differ
     */
    public LocalDate mergeToDate() {
        LocalDate date = null;
        for (DateTimeFieldRule fieldRule : fieldValueMap.keySet()) {
            LocalDate loopDate = fieldRule.mergeToDate(this);
            if (loopDate != null) {
                if (date != null && date.equals(loopDate) == false) {
                    throw new CalendarConversionException(
                        "Cannot convert DateTimeFields to LocalDate, merge process resulted in two different dates: " +
                            date + " and " + loopDate);
                }
                date = loopDate;
            }
        }
        return date;
    }

    /**
     * Merges the fields to create a time.
     * <p>
     * This method will merge any complete set of primary fields into a time.
     * It is normal practice to call {@link #mergeFields()} before calling this
     * method to ensure that less significant fields are already merged into
     * the primary fields.
     *
     * @return the LocalTime, never null
     * @throws CalendarFieldException if a field cannot be merged
     * @throws CalendarConversionException if the time cannot be converted
     */
    public LocalTime mergeToTime() {
        LocalTime time = null;
        for (DateTimeFieldRule fieldRule : fieldValueMap.keySet()) {
            LocalTime.Overflow loopTime = fieldRule.mergeToTime(this);
            if (loopTime != null) {
                if (time != null && time.equals(loopTime.getResultTime()) == false) {
                    throw new CalendarConversionException(
                        "Cannot convert DateTimeFields to LocalTime, merge process resulted in two different times: " +
                            time + " and " + loopTime.getResultTime());
                }
                time = loopTime.getResultTime();
            }
        }
        return time;
    }

    /**
     * Merges the fields to create a date-time.
     * <p>
     * This method will merge any complete set of primary fields into a date-time.
     * It is normal practice to call {@link #mergeFields()} before calling this
     * method to ensure that less significant fields are already merged into
     * the primary fields.
     *
     * @return the LocalDateTime, never null
     * @throws CalendarFieldException if a field cannot be merged
     * @throws CalendarConversionException if the date-time cannot be converted
     */
    public LocalDateTime mergeToDateTime() {
        LocalDate date = mergeToDate();
        if (date == null) {
            return null;
        }
        LocalTime.Overflow time = null;
        for (DateTimeFieldRule fieldRule : fieldValueMap.keySet()) {
            LocalTime.Overflow loopTime = fieldRule.mergeToTime(this);
            if (loopTime != null) {
                if (time != null && time.equals(loopTime) == false) {
                    throw new CalendarConversionException(
                        "Cannot convert DateTimeFields to LocalTime, merge process resulted in two different times: " +
                            time + " and " + loopTime.getResultTime());
                }
                time = loopTime;
            }
        }
        return time == null ? null : time.toLocalDateTime(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the date fields in this set of fields match the specified date.
     * <p>
     * This implementation checks that all date fields in this field set.
     * Time fields are ignored based on {@link LocalDate#isSupported(DateTimeFieldRule)}.
     *
     * @param date  the date to match, not null
     * @return this, for chaining, never null
     * @throws InvalidCalendarFieldException if any date field does not match the specified date
     */
    public DateTimeFields validateMatchesDate(LocalDate date) {
        checkNotNull(date, "The date to match against must not be null");
        for (DateTimeFieldRule field : new TreeSet<DateTimeFieldRule>(fieldValueMap.keySet())) {
            if (date.isSupported(field) && date.get(field) != fieldValueMap.get(field)) {
                throw new InvalidCalendarFieldException(
                    "LocalDate " + date + " does not match the field " +
                    field.getName() + "=" + fieldValueMap.get(field), field);
            }
        }
        return this;
    }

    /**
     * Checks if the date fields in this set of fields match the specified date.
     * <p>
     * This implementation checks that all date fields in this field set.
     * Time fields are ignored based on {@link LocalDate#isSupported(DateTimeFieldRule)}.
     *
     * @param date  the date to match, not null
     * @return true if the date fields match, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        checkNotNull(date, "The date to match against must not be null");
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            if (date.isSupported(entry.getKey()) && date.get(entry.getKey()) != entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates that the time fields in this set of fields match the specified time.
     * <p>
     * This implementation checks that all time fields in this field set.
     * Date fields are ignored based on {@link LocalTime#isSupported(DateTimeFieldRule)}.
     *
     * @param time  the time to match, not null
     * @return this, for chaining, never null
     * @throws InvalidCalendarFieldException if any time field does not match the specified time
     */
    public DateTimeFields validateMatchesTime(LocalTime time) {
        checkNotNull(time, "The time to match against must not be null");
        for (DateTimeFieldRule field : fieldValueMap.keySet()) {
            if (time.isSupported(field) && time.get(field) != fieldValueMap.get(field)) {
                throw new InvalidCalendarFieldException(
                    "LocalTime " + time + " does not match the field " +
                    field.getName() + "=" + fieldValueMap.get(field), field);
            }
        }
        return this;
    }

    /**
     * Checks if the time fields in this set of fields match the specified time.
     * <p>
     * This implementation checks that all time fields in this field set.
     * Date fields are ignored based on {@link LocalTime#isSupported(DateTimeFieldRule)}.
     *
     * @param time  the time to match, not null
     * @return true if the time fields match, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        checkNotNull(time, "The time to match against must not be null");
        for (Entry<DateTimeFieldRule, Integer> entry : fieldValueMap.entrySet()) {
            if (time.isSupported(entry.getKey()) && time.get(entry.getKey()) != entry.getValue()) {
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
        return new TreeMap<DateTimeFieldRule, Integer>(fieldValueMap);
    }

    /**
     * Converts this object to a LocalDate.
     * <p>
     * This method will validate and merge the fields to create a date.
     * This method is strict, meaning that the resulting date can be queried
     * for the same field values as are held in this object.
     *
     * @return the merged LocalDate, never null
     * @throws IllegalCalendarFieldValueException if any field is out of the valid range
     * @throws CalendarConversionException if the merge creates more than one date and the dates differ
     * @throws CalendarConversionException if there is insufficient information to create a date
     * @throws InvalidCalendarFieldException if one of the fields does not match the merged date
     */
    public LocalDate toLocalDate() {
        LocalDate date = validateFields().mergeFields().mergeToDate();
        if (date == null) {
            throw new CalendarConversionException(
                "Cannot convert DateTimeFields to LocalDate, insufficient infomation to create a date");
        }
        validateMatchesDate(date);
        return date;
    }

    /**
     * Converts this object to a LocalTime.
     * <p>
     * This method will validate and merge the fields to create a time.
     * This method is strict, meaning that the resulting time can be queried
     * for the same field values as are held in this object.
     *
     * @return the merged LocalTime, never null
     * @throws IllegalCalendarFieldValueException if any field is out of the valid range
     * @throws CalendarConversionException if the merge creates more than one date and the dates differ
     * @throws CalendarConversionException if there is insufficient information to create a date
     * @throws InvalidCalendarFieldException if one of the fields does not match the merged date
     */
    public LocalTime toLocalTime() {
        LocalTime time = validateFields().mergeFields().mergeToTime();
        if (time == null) {
            throw new CalendarConversionException(
                "Cannot convert DateTimeFields to LocalTime, insufficient infomation to create a date");
        }
        validateMatchesTime(time);
        return time;
    }

    /**
     * Converts this object to a LocalDateTime.
     * <p>
     * This method will validate and merge the fields to create a time.
     * This method is strict, meaning that the resulting time can be queried
     * for the same field values as are held in this object.
     *
     * @return the LocalDateTime, never null
     * @throws InvalidCalendarFieldException if any field is invalid
     * @throws CalendarConversionException if the date or time cannot be converted
     */
    public LocalDateTime toLocalDateTime() {
        LocalDateTime dateTime = validateFields().mergeFields().mergeToDateTime();
        if (dateTime == null) {
            throw new CalendarConversionException(
                "Cannot convert DateTimeFields to LocalTime, insufficient infomation to create a date");
        }
        for (DateTimeFieldRule field : fieldValueMap.keySet()) {
            if (dateTime.isSupported(field) && dateTime.get(field) != fieldValueMap.get(field)) {
                throw new InvalidCalendarFieldException(
                    "Converted LocalTime " + dateTime + " does not match the field " +
                    field.getName() + "=" + fieldValueMap.get(field), field);
            }
        }
        return dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a Calendrical.
     *
     * @return the calendrical with the same set of fields, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(this, null, null);
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
