/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.MathUtils;

/**
 * Stateful class used to merge calendrical information.
 * <p>
 * This class is a tool for merging any set of calendrical information into the
 * most meaningful set of information. For example, separate year, month and day
 * fields will be merged into a date. And if both date and time are present, then
 * they will be merged into a date-time.
 * <p>
 * This class is mutable and not thread-safe.
 * It must only be used from a single thread and must not be passed between threads.
 *
 * @author Stephen Colebourne
 */
public final class CalendricalNormalizer {

    /**
     * The calendricals prior to merging.
     * Null if not merging.
     */
    private final List<Calendrical> calendricals;
    /**
     * The rule of the calendrical supplying the normalized fields.
     */
    private final CalendricalRule<?> rule;
    /**
     * The date.
     */
    private LocalDate date;
    /**
     * The time.
     */
    private LocalTime time;
    /**
     * The offset.
     */
    private ZoneOffset offset;
    /**
     * The zone.
     */
    private ZoneId zone;
    /**
     * The chronloogy.
     */
    private Chronology chronology;
    /**
     * The map of fields.
     */
    private Map<DateTimeRule, DateTimeField> fields;
    /**
     * The errors that occur during normalization.
     */
    private final Set<String> errors = new LinkedHashSet<String>();

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code CalendricalNormalizer}.
     *
     * @return the rule for the merger, not null
     */
    public static CalendricalRule<CalendricalNormalizer> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Derives the specified rule from a set of calendricals.
     * <p>
     * This method is used to combine any number of calendrical objects into
     * a more meaningful form. For example, a {@code Year}, {@code MonthDay}
     * and {@code LocalTime} could be combined into a {@code LocalDateTime}.
     * <p>
     * The returned merger will contain the combined information of the input
     * in a normalized form. Use {@link #derive(CalendricalRule)} or
     * {@link #deriveChecked(CalendricalRule)} to derive the desired values or values.
     * <p>
     * If the input cannot be combined into any sensible normalized output then
     * an exception is thrown. For example, passing in the month January and the
     * date 2011-06-30 will fail as the months are in conflict.
     * 
     * @param calendricals  the calendricals to merge, not null, no nulls
     * @return the normalized merger to query, not null
     * @throws CalendricalException if the calendricals cannot be successfully merged
     */
    public static CalendricalNormalizer merge(Calendrical... calendricals) {
        ISOChronology.checkNotNull(calendricals, "Calendricals must not be null");
        CalendricalNormalizer target;
        try {
            List<CalendricalNormalizer> mergers = new ArrayList<CalendricalNormalizer>(calendricals.length);
            for (Calendrical calendrical : calendricals) {
                CalendricalNormalizer merger = rule().getValue(calendrical);
                if (merger != null) {  // ignore anything with no normalized form
                    mergers.add(merger);
                }
            }
            mergers = Collections.unmodifiableList(mergers);  // make list safe for external use
            for (CalendricalNormalizer merger : mergers) {
                if (merger.getRule() != null) {
                    merger.getRule().merge(merger, mergers);
                }
            }
            target = new CalendricalNormalizer(calendricals, mergers);
            target.normalize();
        } catch (RuntimeException ex) { 
            // provide more useful error message
            throw new CalendricalException("Unable to merge " + Arrays.toString(calendricals) + ": " + ex.getMessage(), ex);
        }
        // normalization is quiet, so need to check it was successful
        if (target.getErrors().size() > 0) {
            throw new CalendricalException("Unable to merge " + Arrays.toString(calendricals) + ": " + target.getErrors());
        }
        return target;
    }

    //-----------------------------------------------------------------------
    /**
     * Derives the specified rule from a the normalized set of objects.
     * <p>
     * This method is designed to be called from {@link Calendrical#get(CalendricalRule)}.
     * The class implementing the interface must call this method passing in
     * parameters to fully describe the state of the object to be derived from.
     * Avoid duplicating information between the date, time and fields if possible.
     * 
     * @param <R>  the type of the desired rule
     * @param ruleToDerive  the rule to derive, not null
     * @param ruleOfData  the rule of the data to derive from, may be null
     * @param date  the date to derive from, may be null
     * @param time  the time to derive from, may be null
     * @param offset  the zone offset to derive from, may be null
     * @param zoneId  the zone ID to derive from, may be null
     * @param chrono  the chronology to derive from, may be null
     * @param fields  the fields to derive from, may be null
     * @return the derived value for the rule, null if unable to derive
     */
    @SuppressWarnings("unchecked")
    public static <R> R derive(CalendricalRule<R> ruleToDerive, CalendricalRule<?> ruleOfData,
            LocalDate date, LocalTime time, ZoneOffset offset, ZoneId zoneId, Chronology chrono, DateTimeFields fields) {
        ISOChronology.checkNotNull(ruleToDerive, "CalendricalRule must not be null");
        if (fields == null) {
            if (ruleToDerive instanceof ISOCalendricalRule<?>) {
                switch (((ISOCalendricalRule<?>) ruleToDerive).ordinal) {
                    case ISOCalendricalRule.LOCAL_DATE_ORDINAL: return (R) date;
                    case ISOCalendricalRule.LOCAL_TIME_ORDINAL: return (R) time;
                    case ISOCalendricalRule.LOCAL_DATE_TIME_ORDINAL: return (R) LocalDateTime.of(date, time);
                    case ISOCalendricalRule.OFFSET_DATE_ORDINAL: return (R) OffsetDate.of(date, offset);
                    case ISOCalendricalRule.OFFSET_TIME_ORDINAL: return (R) OffsetTime.of(time, offset);
                    case ISOCalendricalRule.OFFSET_DATE_TIME_ORDINAL: return (R) OffsetDateTime.of(date, time, offset);
                    case ISOCalendricalRule.ZONE_OFFSET_ORDINAL: return (R) offset;
                    case ISOCalendricalRule.ZONE_ID_ORDINAL: return (R) zoneId;
                    case ISOCalendricalRule.CHRONOLOGY_ORDINAL: return (R) chrono;
                }
                // TODO ZonedDateTime drops through?
            } else if (ruleToDerive instanceof ISODateTimeRule) {
                if (date != null && time != null) {
                    return (R) ((ISODateTimeRule) ruleToDerive).derive(date, time);
                } else if (date != null) {
                    return (R) ((ISODateTimeRule) ruleToDerive).derive(date);
                } else if (time != null) {
                    return (R) ((ISODateTimeRule) ruleToDerive).derive(time);
                } else {
                    return null;
                }
            }
        }
        CalendricalNormalizer merger = new CalendricalNormalizer(ruleOfData, date, time, offset, zoneId, chrono, fields);
        merger.normalize();
        return merger.derive(ruleToDerive);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from a normalized list of mergers.
     * 
     * @param calendricals  the original calendricals prior to merging, not null
     * @param mergers  the merged form of the calendricals, not null
     */
    private CalendricalNormalizer(Calendrical[] calendricals, List<CalendricalNormalizer> mergers) {
        this.calendricals = Collections.unmodifiableList(Arrays.asList(calendricals));
        this.rule = null;
        for (CalendricalNormalizer merger : mergers) {
            if (merger.date != null) {
                setDate(merger.date, true);
            }
            if (merger.time != null) {
                setTime(merger.time, true);
            }
            if (merger.offset != null) {
                setOffset(merger.offset, true);
            }
            if (merger.zone != null) {
                setZone(merger.zone, true);
            }
            if (merger.chronology != null) {
                setChronology(merger.chronology, true);
            }
            if (merger.fields != null) {
                for (DateTimeField field : merger.fields.values()) {
                    setField(field, true);
                }
            }
        }
    }

    /**
     * Creates an instance from a normalized set of objects.
     * 
     * @param ruleOfData  the rule of the calendrical that these objects represent, may be null
     * @param date  the date, may be null
     * @param time  the time, may be null
     * @param offset  the zone offset, may be null
     * @param zoneId  the zone ID, may be null
     * @param chrono  the chronology, may be null
     * @param fields  the fields, may be null
     */
    private CalendricalNormalizer(
            CalendricalRule<?> ruleOfData, LocalDate date, LocalTime time, ZoneOffset offset,
            ZoneId zone, Chronology chronology, DateTimeFields fields) {
        this.calendricals = null;
        this.rule = ruleOfData;
        this.date = date;
        this.time = time;
        this.offset = offset;
        this.zone = zone;
        this.chronology = chronology;
        if (fields != null) {
            for (DateTimeField field : fields) {
                setField(field, false);  // can use false here as DateTimeFields ensure no rule clashes
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * The original calendricals that have been merged.
     * <p>
     * This will only be present if this is the result of merging.
     * 
     * @return the original calendricals, null if not the result of a merge
     */
    public List<Calendrical> getMergedCalendricals() {
        return calendricals;
    }

    /**
     * The single rule that defines the data in this merger.
     * <p>
     * This will only be present if this merger is created from a single
     * conceptual object as opposed to being merged.
     * 
     * @return the rule of the data, may be null
     */
    public CalendricalRule<?> getRule() {
        return rule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public LocalDate getDate(boolean storeErrorIfNull) {
        if (storeErrorIfNull && date == null) {
            addError("Missing LocalDate");
        }
        return date;
    }

    /**
     * Gets the time.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public LocalTime getTime(boolean storeErrorIfNull) {
        if (storeErrorIfNull && time == null) {
            addError("Missing LocalTime");
        }
        return time;
    }

    /**
     * Gets the offset.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public ZoneOffset getOffset(boolean storeErrorIfNull) {
        if (storeErrorIfNull && offset == null) {
            addError("Missing ZoneOffset");
        }
        return offset;
    }

    /**
     * Gets the zone.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public ZoneId getZone(boolean storeErrorIfNull) {
        if (storeErrorIfNull && zone == null) {
            addError("Missing ZoneId");
        }
        return zone;
    }

    /**
     * Gets the chronology.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public Chronology getChronology(boolean storeErrorIfNull) {
        if (storeErrorIfNull && chronology == null) {
            addError("Missing Chronology");
        }
        return chronology;
    }

    /**
     * Gets a field by rule.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param rule  the rule to retrieve, null returns null
     * @return the field, may be null
     */
    public DateTimeField getField(DateTimeRule rule, boolean storeErrorIfNull) {
        DateTimeField field = (fields == null ? null : fields.get(rule));
        if (storeErrorIfNull && field == null) {
            addError("Missing field " + rule.getName());
        }
        return field;
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the date.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param date  the date to store, may be null
     * @param storeErrorIfClash  true to store an error if the date clashes with the stored value
     */
    public void setDate(LocalDate date, boolean storeErrorIfClash) {
        this.date = set(this.date, date, storeErrorIfClash);
    }

    /**
     * Sets the time.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param time  the time to store, may be null
     * @param storeErrorIfClash  true to store an error if the time clashes with the stored value
     */
    public void setTime(LocalTime time, boolean storeErrorIfClash) {
        this.time = set(this.time, time, storeErrorIfClash);
    }

    /**
     * Sets the offset.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param offset  the offset to store, may be null
     * @param storeErrorIfClash  true to store an error if the offset clashes with the stored value
     */
    public void setOffset(ZoneOffset offset, boolean storeErrorIfClash) {
        this.offset = set(this.offset, offset, storeErrorIfClash);
    }

    /**
     * Sets the zone.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param zone  the zone to store, may be null
     * @param storeErrorIfClash  true to store an error if the zone clashes with the stored value
     */
    public void setZone(ZoneId zone, boolean storeErrorIfClash) {
        this.zone = set(this.zone, zone, storeErrorIfClash);
    }

    /**
     * Sets the chronology.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param chronology  the chronology to store, may be null
     * @param storeErrorIfClash  true to store an error if the chronology clashes with the stored value
     */
    public void setChronology(Chronology chronology, boolean storeErrorIfClash) {
        this.chronology = set(this.chronology, chronology, storeErrorIfClash);
    }

    /**
     * Sets the specified field.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * The flag should be set to <code>true</code> to cause the method to check
     * if the value for the field clashes with an existing value.
     * A <code>false</code> value simply overwrites any previous value.
     * 
     * @param field  the field to store, null ignored
     * @param storeErrorIfClash  true to store an error if the field clashes with an existing field
     */
    public void setField(DateTimeField field, boolean storeErrorIfClash) {
        if (field != null) {
            if (fields == null) {
                fields = new HashMap<DateTimeRule, DateTimeField>();
            }
            DateTimeField curField = fields.get(field.getRule());
            DateTimeField newField = set(curField, field, storeErrorIfClash);
            fields.put(newField.getRule(), newField);
        }
    }

    private <T> T set(T curObj, T newObj, boolean storeErrorIfClash) {
        if (storeErrorIfClash) {
            if (curObj != null && curObj.equals(newObj) == false) {
                addError("Clash: " + curObj + " and " + newObj);
                return curObj;
            }
        }
        return newObj;
    }

    // phase 2
    //-----------------------------------------------------------------------
    /**
     * Normalize the fields, converting to LocalDate/LocalTime if possible.
     */
    private void normalize() {
        // do not call from the constructor
        if (fields != null && fields.size() > 0) {
            normalizeAuto();
            normalizeManual();
        }
    }

    private void normalizeAuto() {
        // normalize each individual field in isolation
        for (DateTimeField field : new ArrayList<DateTimeField>(fields.values())) {
            DateTimeField normalized = field.normalized();
            if (normalized != field) {
                setField(normalized, true);
                fields.remove(field.getRule());
            }
        }
        if (fields.size() < 2 || errors.size() > 0) {
            return;
        }
        
        // group according to base rule
        Map<DateTimeRule, List<DateTimeField>> grouped = new HashMap<DateTimeRule, List<DateTimeField>>();
        for (DateTimeField field : fields.values()) {
            DateTimeRule baseRule = field.getRule().getBaseRule();
            List<DateTimeField> groupedList = grouped.get(baseRule);
            if (groupedList == null) {
                groupedList = new ArrayList<DateTimeField>();
                grouped.put(baseRule, groupedList);
            }
            groupedList.add(field);
        }
        
        // normalize groups
        // TODO: loop again (group again) if register on group is public
        fields.clear();
        for (Entry<DateTimeRule, List<DateTimeField>> entry : grouped.entrySet()) {
            List<DateTimeField> group = entry.getValue();
            if (group.size() >= 2) {
                mergeGroup(entry.getKey(), group);
            }
            for (DateTimeField field : group) {
                fields.put(field.getRule(), field);  // should be no clashes here
            }
        }
    }

    private void mergeGroup(DateTimeRule baseRule, List<DateTimeField> group) {
        sort(group);
        DateTimeRuleGroup ruleGroup = DateTimeRuleGroup.of(baseRule);
        for (int i = 0; i < group.size() - 1; i++) {
            final DateTimeField field1 = group.get(i);
            for (int j = i + 1; j < group.size(); j++) {
                final DateTimeField field2 = group.get(j);
                final DateTimeRule rule1 = field1.getRule();
                final DateTimeRule rule2 = field2.getRule();
                // remove if derived
                // the fields share a base rule, so this must succeed if there field1 surrounds field2
                final DateTimeField derived2 = field1.derive(rule2);
                if (derived2 != null) {
                    if (derived2.equals(field2)) {
                        group.remove(j--);
                        continue;
                    } else {
                        addError("Clash: " + field2 + " and " + derived2);
                        return;
                    }
                }
                // merge overlap or adjacent
                if (DateTimeRule.comparePeriodUnits(rule2.getPeriodRange(), rule1.getPeriodUnit()) >= 0 &&
                        DateTimeRule.comparePeriodUnits(rule2.getPeriodUnit(), rule1.getPeriodUnit()) < 0) {
                    final long period1 = rule1.convertToPeriod(field1.getValue());
                    final long period2 = rule2.convertToPeriod(field2.getValue());
                    final PeriodField conversion1 = rule2.getPeriodRange().getEquivalentPeriod(rule1.getPeriodUnit());
                    // if was an overlap, then check it is valid
                    // this must be done before the combined rule check to ensure that the final derivation is OK
                    if (DateTimeRule.comparePeriodUnits(rule2.getPeriodRange(), rule1.getPeriodUnit()) > 0) {
                        long periodMid1 = MathUtils.floorMod(period1, conversion1.getAmount());
                        final PeriodField conversion2 = rule2.getPeriodRange().getEquivalentPeriod(rule2.getPeriodUnit());
                        final PeriodField conversion3 = rule1.getPeriodUnit().getEquivalentPeriod(rule2.getPeriodUnit());
                        long periodMid2 = MathUtils.floorMod(period2, conversion2.getAmount());
                        periodMid2 = MathUtils.floorDiv(periodMid2, conversion3.getAmount());
                        if (periodMid1 != periodMid2) {
                            addError("Clash: " + field1 + " and " + field2);
                            return;
                        }
                    }
                    // merge if possible
                    DateTimeRule ruleCombined = ruleGroup.getRelatedRule(rule2.getPeriodUnit(), rule1.getPeriodRange());
                    if (ruleCombined != null) {
                        long period = MathUtils.floorDiv(period1, conversion1.getAmount());
                        final PeriodField conversion2 = rule2.getPeriodRange().getEquivalentPeriod(rule2.getPeriodUnit());
                        period = MathUtils.safeMultiply(period, conversion2.getAmount());
                        period = MathUtils.safeAdd(period, period2);
                        DateTimeField fieldCombined = ruleCombined.field(ruleCombined.convertFromPeriod(period));
                        group.set(i--, fieldCombined);
                        group.remove(j);
                        break;
                    }
                }
            }
        }
    }

    private void sort(List<DateTimeField> group) {
        Collections.sort(group, new Comparator<DateTimeField>() {
            public int compare(DateTimeField dtf1, DateTimeField dtf2) {
                int cmp = -dtf1.getRule().comparePeriodRange(dtf2.getRule());
                if (cmp == 0) {
                    cmp = dtf1.getRule().comparePeriodUnit(dtf2.getRule());
                }
                return cmp;
            }
        });
    }

    private void normalizeManual() {
        if (errors.size() == 0) {
            for (DateTimeField field : fields.values()) {
                field.getRule().normalize(this);
            }
        }
    }

    // phase 3
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public <R> R derive(CalendricalRule<R> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (R) this;
        }
        if (errors.size() > 0) {
            return null;  // quiet
        }
        try {
            return ruleToDerive.deriveFrom(this);
//            if (result == null) {
//                addError("Unable to derive " + ruleToDerive + ": " + this);
//            }
//            return result;
        } catch (RuntimeException ex) {
            addError(ex.getMessage());
            return null;
        }
    }

    public <R> R deriveChecked(CalendricalRule<R> ruleToDerive) {
        R result = derive(ruleToDerive);
        if (errors.size() > 0) {
            throw new CalendricalException("Unable to derive " + ruleToDerive + " from " + this + ": " + errors);
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the modifiable list of errors that have occurred.
     * <p>
     * Use {@link #addError} to add to this list.
     * 
     * @return the list of errors, not null, no nulls
     */
    public Set<String> getErrors() {
        return errors;
    }

    /**
     * Adds an error to those tracked by the normalizer.
     * 
     * @param error  the error text, not null
     */
    public void addError(String error) {
        if (error != null) {
            errors.add(error);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        if (calendricals != null) {
            return calendricals.toString();
        }
        List<Object> list = new ArrayList<Object>();
        if (date != null) {
            list.add(date);
        }
        if (time != null) {
            list.add(time);
        }
        if (offset != null) {
            list.add(offset);
        }
        if (zone != null) {
            list.add(zone);
        }
        if (chronology != null) {
            list.add(chronology);
        }
        if (fields != null) {
            list.addAll(fields.values());
        }
        return list.toString();
    }

    //-----------------------------------------------------------------------
    // TODO
    static class Rule extends CalendricalRule<CalendricalNormalizer> {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Serialization version. */
        static final Rule INSTANCE = new Rule();

        private Rule() {
            super(CalendricalNormalizer.class, "CalendricalNormalizer");  // TODO
        }
    }

}
