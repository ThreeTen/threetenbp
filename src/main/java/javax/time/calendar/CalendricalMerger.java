/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.time.CalendricalException;

/**
 * Stateful class used to merge calendrical information.
 * <p>
 * This class is a tool for merging any set of calendrical information into the
 * most meaningful set of information. For example, separate year, month and day
 * fields will be merged into a date. And if both date and time are present, then
 * they will be merged into a date-time.
 * <p>
 * CalendricalMerger is mutable and not thread-safe.
 * It must only be used from a single thread and must not be passed between threads.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CalendricalMerger implements Calendrical {

    /**
     * The map of potentially invalid data to being merged, never null.
     * This is a concurrent hash map mainly to gain the no-nulls implementation.
     */
    private final Map<CalendricalRule<?>, Object> inputMap = new ConcurrentHashMap<CalendricalRule<?>, Object>();
    /**
     * The map of in range data to be merged, never null.
     * This is a concurrent hash map mainly to gain the no-nulls implementation.
     */
    private final Map<CalendricalRule<?>, Object> processingMap = new ConcurrentHashMap<CalendricalRule<?>, Object>();
    /**
     * The merge context to use.
     */
    private CalendricalContext context;
    /**
     * Current iterator, updated when the state of the map is changed.
     */
    private Iterator<CalendricalRule<?>> iterator;
    /**
     * The overflow period to be added to the resultant date/time.
     */
    private Period overflow = Period.ZERO;

    /**
     * Constructor.
     *
     * @param context  the context to use, not null
     */
    public CalendricalMerger(CalendricalContext context) {
        ISOChronology.checkNotNull(context, null);
        this.context = context;
    }

    /**
     * Constructor.
     *
     * @param context  the context to use, not null
     * @param inputMap  the map of data to merge, not null
     */
    public CalendricalMerger(CalendricalContext context, Map<CalendricalRule<?>, Object> inputMap) {
        ISOChronology.checkNotNull(context, null);
        ISOChronology.checkNotNull(inputMap, null);
        this.inputMap.putAll(inputMap);
        this.context = context;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the underlying rule-value map that is being merged.
     * <p>
     * The map returned is the live data from this instance.
     * Updating the map will update the data held by the merger.
     * <p>
     * Values in this map may be invalid, for example the day-of-month may be
     * an invalid negative value, or the hour represented as a currency.
     * Some of these, like a negative day-of-month, may be capable of being
     * interpreted by a lenient merger. Others, like a currency, cannot.
     * <p>
     * The map must only be updated before merging starts.
     * If the map is updated after merging starts, the result is undefined.
     *
     * @return the rule-value map being merged, doesn't accept nulls, never null
     */
    public Map<CalendricalRule<?>, Object> getInputMap() {
        return inputMap;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical context in use for the merge.
     *
     * @return the calendrical context, never null
     */
    public CalendricalContext getContext() {
        return context;
    }

    /**
     * Sets the calendrical context to use for the merge.
     * <p>
     * The context must only be updated before merging starts.
     * If the context is updated after merging starts, the result is undefined.
     *
     * @param context  the calendrical context, not null
     */
    public void setContext(CalendricalContext context) {
        ISOChronology.checkNotNull(context, "CalendricalContext must not be null");
        this.context = context;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the overflow that results from the merge.
     * <p>
     * When some sets of fields are merged, the result may include an overflow.
     * This is a period that should be added to a date-time to make the result whole.
     *
     * @return the overflow resulting from the merge, never null
     */
    public Period getOverflow() {
        return overflow;
    }

    /**
     * Gets the overflow that results from the merge.
     * <p>
     * When some sets of fields are merged, the result may include an overflow.
     * This is a period that should be added to a date-time to make the result whole.
     *
     * @param additionalOverflow  the additional overflow to store, not null
     */
    public void addToOverflow(Period additionalOverflow) {
        if ((overflow.getYears() != 0 && additionalOverflow.getYears() != 0) ||
                (overflow.getMonths() != 0 && additionalOverflow.getMonths() != 0) || 
                (overflow.getDays() != 0 && additionalOverflow.getDays() != 0) || 
                (overflow.getHours() != 0 && additionalOverflow.getHours() != 0) || 
                (overflow.getMinutes() != 0 && additionalOverflow.getMinutes() != 0) || 
                (overflow.getSeconds() != 0 && additionalOverflow.getSeconds() != 0) || 
                (overflow.getNanos() != 0 && additionalOverflow.getNanos() != 0)) {
            throw new CalendricalException("Unable to complete merge as input contains two conflicting out of range values");
        }
        overflow = overflow.plus(additionalOverflow);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule from the merged result.
     * <p>
     * This method queries the value of the specified calendrical rule using
     * the merged rule-value map.
     * If the value cannot be returned for the rule from this date then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        ISOChronology.checkNotNull(rule, "CalendricalRule must not be null");
        T value = getValue(rule);
        return value != null ? value : rule.deriveValueFrom(this);
    }

    /**
     * Gets the value of the specified calendrical rule from the merged result.
     * <p>
     * This method queries the value of the specified calendrical rule using
     * the merged rule-value map.
     * If the value cannot be returned for the rule from this date then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T getValue(CalendricalRule<T> rule) {
        return rule.reify(processingMap.get(rule));
    }

    //-----------------------------------------------------------------------
    /**
     * Stores a rule-value pair into this map ensuring that it does not clash
     * with any previous value defined for that rule.
     * <p>
     * This method adds the specified rule-value pair to the map.
     * If this instance already has a value for the rule then the value is checked
     * to see if it is the same with an exception being thrown if it is not.
     * If this instance does not hold the rule already, then the value is simply added.
     * <p>
     * The merged value should be within the valid range for the rule.
     *
     * @param rule  the field to store, not null
     * @param value  the value to store, not null
     * @throws CalendricalException if the input field does not match a previously stored field
     */
    public <T> void storeMerged(CalendricalRule<T> rule, T value) {
        ISOChronology.checkNotNull(rule, "CalendricalRule must not be null");
        ISOChronology.checkNotNull(value, "Value must not be null");
        T oldValue = getValue(rule);
        if (oldValue != null) {
            if (oldValue.equals(value) == false) {
                throw new InvalidCalendarFieldException("Merge resulted in two different values, " + value +
                        " and " + oldValue + ", for " + rule.getID() + " given input " +
                        inputMap, rule);
            } else {
                return;  // no change
            }
        }
        processingMap.put(rule, value);
        iterator = processingMap.keySet().iterator();  // restart the iterator
    }

    /**
     * Removes a rule and its value from the map being processed.
     * <p>
     * This method is called to remove a rule-value pair that can now be derived
     * from another item in the map following a merge.
     *
     * @param rule  the rule to remove, not null
     */
    public void removeProcessed(CalendricalRule<?> rule) {
        ISOChronology.checkNotNull(rule, "CalendricalRule must not be null");
        processingMap.remove(rule);
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the fields to extract the maximum possible date, time and offset information.
     * <p>
     * The merge process aims to extract the maximum amount of information
     * possible from this set of fields. Ideally the outcome will be a date, time
     * or both, however there may be insufficient information to achieve this.
     * <p>
     * The process repeatedly calls the field rule {@link CalendricalRule#merge merge}
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
     * This includes strict/lenient behavior.
     * <p>
     * The merge must result in consistent values for each field, date and time.
     * If two different values are produced an exception is thrown.
     * For example, both Year/MonthOfYear/DayOfMonth and Year/DayOfYear will merge to form a date.
     * If both sets of fields do not produce the same date then an exception will be thrown.
     *
     * @throws CalendricalException if the merge cannot be completed successfully
     */
    public Calendrical merge() {
        // it is essential to validate for consistency, as there is no way to
        // reliably determine which is the more significant or original value
        
        processingMap.clear();
        
        // exit quick in simple case
        if (inputMap.size() > 0) {
            // convert to standard data types and ranges
            interpret();
            
//            List<Chronology> chronologies = null;
//            for (Chronology chronology : chronologies) {
//                chronology.merge(this);
//                removeDerivable();
//            }
            
//            // strict must pre-validate fields for out of range problems
//            if (context.isStrict()) {
//                validate();
//            }
            
            // we keep all the fields in the map during the merge and only remove at the end
            // once merged, the initial fields can be derived from the merged fields
            mergeLoop();
            ISOChronology.INSTANCE.merge(this);  // TODO better?
            
            // remove the fields that have been merged into more significant fields
//            inputMap.keySet().removeAll(processedFieldSet);
            
            // check and remove any remaining less significant that can be derived from
            // the new set of more significant fields
            // TODO
//            if (context.isCheckUnusedFields()) {
//                calendrical.checkConsistent();
//            }
            
            // add days overflow
            // TODO
//            if (calendrical.getDate() != null && mergedTime != null && mergedTime.getOverflowDays() > 0) {
//                calendrical.setDate(calendrical.getDate().plusDays(mergedTime.getOverflowDays()));
//            }
            
            // remove derivable fields
            if (processingMap.size() > 1) {
                removeDerivable();
            }
        }
        return this;
    }

//    /**
//     * Validates that the value of each field in the map is within its valid range
//     * throwing an exception if not.
//     * <p>
//     * The validation simply checks that each value in the field map is within the
//     * normal range for the field as defined by {@link CalendricalRule#checkValue(int)}.
//     * No cross-validation between fields is performed, thus the field map could
//     * contain an invalid date such as February 31st.
//     *
//     * @return this, for method chaining, never null
//     * @throws IllegalCalendarFieldValueException if any field is invalid
//     */
//    public void validate() {
//        for (Entry<CalendricalRule<?>, Object> entry : inputMap.entrySet()) {
//            entry.getKey().checkValue(entry.getValue());
//        }
//    }

    /**
     * Performs the merge based on the rules.
     *
     * @throws CalendricalException if the merge cannot be completed successfully
     */
    private void mergeLoop() {
        iterator = inputMap.keySet().iterator();
        int protect = 0;  // avoid infinite looping
        while (iterator.hasNext() && protect < 100) {
            iterator.next().merge(this);
            protect++;
        }
        if (iterator.hasNext()) {
            throw new CalendricalException("Merge fields failed, infinite loop blocked, " +
                    "probably caused by an incorrectly implemented field rule");
        }
    }

    /**
     * Interprets each value in the input map, converting to standard types.
     *
     * @throws CalendricalException if the value for any rule is invalid
     */
    private void interpret() {
        for (Entry<CalendricalRule<?>, Object> entry : inputMap.entrySet()) {
            CalendricalRule<?> rule = entry.getKey();
            Object value = rule.interpretValue(this, entry.getValue());
            processingMap.put(rule, value);
        }
    }

    /**
     * Removes any field from the processing map that can be derived from another field.
     */
    private void removeDerivable() {
        Iterator<CalendricalRule<?>> it = processingMap.keySet().iterator();
        while (it.hasNext()) {
            Object derivedValue = it.next().derive(this);
            if (derivedValue != null) {
                it.remove();
            }
        }
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc}  */
    @Override
    public String toString() {
        String str;
        if (processingMap.isEmpty() && inputMap.size() > 0) {
            str = inputMap.toString();
        } else {
            str = processingMap.toString();
        }
        if (overflow.isZero() == false) {
            str += "+" + overflow;
        }
        return str;
    }

}
