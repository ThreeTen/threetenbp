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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.time.CalendricalException;

/**
 * Temporary helper that assists in the merging of fields into a calendrical.
 * <p>
 * This class holds a set of field-value pairs which represent a full or partial
 * view of a date, time or date-time. Each value might be invalid, thus
 * for example a month is not limited to the normal range of 1 to 12.
 * Instances must therefore be treated with care.
 * <p>
 * CalendricalMerger is mutable and not thread-safe.
 * It must only be used from a single thread and must not be passed between threads.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CalendricalMerger
        implements CalendricalProvider {

    /**
     * The original field-value map.
     */
    private final DateTimeFields originalFields;
    /**
     * The date time map being merged, never null, may be empty.
     */
    private final TreeMap<DateTimeFieldRule, Integer> fieldValueMap;
    /**
     * The merge context to use.
     */
    private final CalendricalContext context;
    /**
     * The fields that have been processed so far.
     */
    private final Set<DateTimeFieldRule> processedFieldSet;
    /**
     * Flag to record if the state has changed recently.
     */
    private boolean changed;
    /**
     * The merged date.
     */
    private LocalDate mergedDate;
    /**
     * The merged time.
     */
    private LocalTime.Overflow mergedTime;
    /**
     * The merged offset.
     */
    private ZoneOffset mergedOffset;

    /**
     * Constructs an instance using a specific context.
     *
     * @param fieldValueMap  the field-value map to merge, not null
     * @param context  the context to use, not null
     */
    public CalendricalMerger(DateTimeFields fieldValueMap, CalendricalContext context) {
        checkNotNull(fieldValueMap, "The field-value map must not be null");
        checkNotNull(context, "The calendrical context must not be null");
        this.context = context;
        this.originalFields = fieldValueMap;
        this.fieldValueMap = fieldValueMap.clonedMap();
        processedFieldSet = new HashSet<DateTimeFieldRule>();
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
     * Gets the original field-value map being merged.
     *
     * @return the original field-value map being merged, never null
     */
    public DateTimeFields getOriginalFields() {
        return originalFields;
    }

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

    //-----------------------------------------------------------------------
    /**
     * Gets the set of fields that have been processed so far.
     * <p>
     * These are the fields that have been merged into a date, time or other fields.
     * The set may contain fields that are not in the original field-value map.
     *
     * @return the processed field set, modifiable copy, not null
     */
    public Set<DateTimeFieldRule> getProcessedFieldSet() {
        return new HashSet<DateTimeFieldRule>(processedFieldSet);
    }

    /**
     * Gets the merged date.
     *
     * @return the merged date, may be null
     */
    public LocalDate getMergedDate() {
        return mergedDate;
    }

    /**
     * Gets the merged time.
     *
     * @return the merged time, may be null
     */
    public LocalTime.Overflow getMergedTime() {
        return mergedTime;
    }

    /**
     * Gets the merged field-value map.
     *
     * @return the merged field-value map, never null
     */
    public DateTimeFields getMergedFields() {
        return DateTimeFields.fields(fieldValueMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value for the specified field.
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
     */
    public Integer get(DateTimeFieldRule fieldRule) {
        checkNotNull(fieldRule, "The field rule must not be null");
        Integer value = fieldValueMap.get(fieldRule);
        if (value != null && isStrict()) {
            fieldRule.checkValue(value);
        }
        return value;
    }

    /**
     * Gets the value for the specified field.
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
    public int getValue(DateTimeFieldRule fieldRule) {
        Integer value = get(fieldRule);
        if (value == null) {
            throw new UnsupportedCalendarFieldException(fieldRule);
        }
        return value;
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
        checkNotNull(fieldRule, "The field rule must not be null");
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
        checkNotNull(date, "The date must not be null");
        if (mergedDate != null && mergedDate.equals(date) == false) {
            throw new CalendricalException("Merge resulted in two different dates, " + mergedDate +
                    " and " + date + ", for input fields " + originalFields);
        }
        mergedDate = date;
        // no need to set changed flag, as store of date should not enable any more calculations
    }

    /**
     * Stores the merged time checking that it matches any previously stored time.
     *
     * @param time  the time to set, may be null
     * @throws CalendricalException if the input time does not match a previously stored time
     */
    public void storeMergedTime(LocalTime.Overflow time) {
        checkNotNull(time, "The time must not be null");
        if (mergedTime != null && mergedTime.equals(time) == false) {
            throw new CalendricalException("Merge resulted in two different times, " + mergedTime +
                    " and " + time + ", for input fields " + originalFields);
        }
        mergedTime = time;
        // no need to set changed flag, as store of time should not enable any more calculations
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
        checkNotNull(fieldRule, "The field rule must not be null");
        Integer oldValue = fieldValueMap.get(fieldRule);
        if (oldValue != null) {
            if (oldValue.intValue() != value) {
                throw new InvalidCalendarFieldException("Merge resulted in two different values, " + value +
                        " and " + oldValue + ", for " + fieldRule.getID() + " within input fields " + originalFields, fieldRule);
            } else {
                return;  // no change
            }
        }
        fieldValueMap.put(fieldRule, value);
        changed = true;
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
     * @return this, for method chaining
     * @throws CalendricalException if the merge cannot be completed successfully
     */
    public CalendricalMerger merge() {
        // it is essential to validate for consistency, as there is no way to
        // reliably determine which is the more significant or original value
        
        // exit quick in simple case
        if (fieldValueMap.size() > 0) {
            // we keep all the fields in the map during the merge and only remove at the end
            // once merged, the initial fields can be derived from the merged fields
            mergeLoop();
            
            // remove the fields that have been merged into more significant fields
            fieldValueMap.keySet().removeAll(processedFieldSet);
            
            // check and remove any remaining less significant that can be derived from
            // the new set of more significant fields
            checkDerivableFields();
        }
        return this;
    }

    /**
     * Checks the current field-value map to see if any of the fields can be derived
     * from another field, the merged date or the merged time.
     *
     * @return this, for method chaining
     * @throws CalendricalException if the merge cannot be completed successfully
     */
    private void checkDerivableFields() {
        if (fieldValueMap.size() == 0) {
            return;
        }
        // date/time
        Object errorValue = null;
        String errorText = null;
        LocalDate date = null;
        LocalTime time = null;
        if (mergedDate != null & mergedTime != null) {
            LocalDateTime dateTime = mergedTime.toLocalDateTime(mergedDate);
            date = dateTime.toLocalDate();
            time = dateTime.toLocalTime();
            errorValue = dateTime;
            errorText = "date-time ";
        } else if (mergedDate != null) {
            date = mergedDate;
            errorValue = date;
            errorText = "date ";
        } else if (mergedTime != null) {
            time = mergedTime.getResultTime();
            errorValue = time;
            errorText = "time ";
        }
        if (errorValue != null) {
            for (Iterator<Entry<DateTimeFieldRule, Integer>> it = fieldValueMap.entrySet().iterator(); it.hasNext(); ) {
                Entry<DateTimeFieldRule, Integer> entry = it.next();
                DateTimeFieldRule fieldRule = entry.getKey();
                Integer mergedValue = fieldRule.getValueQuiet(date, time);
                if (mergedValue != null) {
                    Integer originalValue = entry.getValue();
                    if (getContext().isCheckUnusedFields() == false || mergedValue.equals(originalValue)) {
                        it.remove();
                    } else {
                        throw new InvalidCalendarFieldException("Merge resulted in a " + errorText +
                                errorValue + " that is inconsistent with the input value " + originalValue +
                                " for " + fieldRule.getID() + " within input fields " + originalFields, fieldRule);
                    }
                }
            }
        }
        // fields
        for (Iterator<Entry<DateTimeFieldRule, Integer>> it = fieldValueMap.entrySet().iterator(); it.hasNext(); ) {
            Entry<DateTimeFieldRule, Integer> entry = it.next();
            DateTimeFieldRule fieldRule = entry.getKey();
            DateTimeFields mergedFields = getMergedFields();
            Integer mergedValue = fieldRule.deriveValue(mergedFields);
            if (mergedValue != null) {
                Integer originalValue = entry.getValue();
                if (getContext().isCheckUnusedFields() == false || mergedValue.equals(originalValue)) {
                    it.remove();
                } else {
                    throw new InvalidCalendarFieldException("Merge resulted in a value " +
                            mergedValue + " that is inconsistent with the input value " + originalValue +
                            " for " + fieldRule.getID() + " within input fields " + originalFields, fieldRule);
                }
            }
        }
    }

    /**
     * Performs the main merge loop.
     *
     * @throws CalendricalException if the merge cannot be completed successfully
     */
    private void mergeLoop() {
        processedFieldSet.clear();
        
        List<DateTimeFieldRule> rules = new ArrayList<DateTimeFieldRule>();
        for (int i = 0; i < 100; i++) {  // avoid infinite looping
            rules.addAll(fieldValueMap.keySet());
            changed = false;
            for (DateTimeFieldRule fieldRule : rules) {
                fieldRule.merge(this);
            }
            if (changed == false) {
                return;  // fully merged
            }
            rules.clear();
        }
        throw new CalendricalException("Merge failed, infinite loop blocked, " +
                "probably caused by an incorrectly implemented field rule");
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a Calendrical.
     *
     * @return the calendrical with the same set of fields, never null
     */
    public Calendrical toCalendrical() {
        if (mergedTime != null) {
            if (mergedDate != null) {
                LocalDateTime dateTime = mergedTime.toLocalDateTime(mergedDate);
                return Calendrical.calendrical(dateTime.toLocalDate(), dateTime.toLocalTime(), mergedOffset, null);
            } else {
                return Calendrical.calendrical(null, mergedTime.getResultTime(), mergedOffset, null);
            }
        } else {
            return Calendrical.calendrical(mergedDate, null, mergedOffset, null);
        }
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Outputs a string representation of this instance for debugging.
//     *
//     * @return a string representation for debugging, never null
//     */
//    @Override
//    public String toString() {
//        return "CalendricalMerger[" + originalFields + ", " + context + "]";
//    }
}
